package com.example.pedometer.Service;

import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;

import androidx.annotation.Nullable;

import com.example.pedometer.AppBase.MyApplication;
import com.example.pedometer.Beans.StepBean;
import com.example.pedometer.Beans.StepChartBean;
import com.example.pedometer.DB.StepDBHelper;
import com.example.pedometer.Utils.ACache;
import com.example.pedometer.Utils.Settings;
import com.example.pedometer.Utils.Utils;

public class StepService extends Service {

   private SensorManager sensorManager;
   private StepBean stepBean;
   private StepChartBean stepChartBean;//记录显示数据
   private StepListener stepListener;

   private static final int STATUS_NOT_RUN = 0;
   private static final int STATUS_RUNNING = 1;
   //当前运行状态
   private int runStatus = STATUS_NOT_RUN;

   private Settings settings;

   private static final long SAVE_CHART_TIME = 6000L;
   private static Handler handler = new Handler();

   private Runnable timeRunnable = new Runnable() {
      @Override
      public void run() {
         if(runStatus == STATUS_RUNNING) {
            if(handler != null && stepChartBean != null) {
               handler.removeCallbacks(timeRunnable);
               updateChartData();//更新数据
               //定期刷新数据
               handler.postDelayed(timeRunnable,SAVE_CHART_TIME);
            }
         }
      }
   };


   @Override
   public void onCreate() {
      super.onCreate();
      sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
      stepBean = new StepBean();
      stepChartBean = new StepChartBean();
      stepListener = new StepListener(stepBean);
      settings = new Settings(this);
   }

   /**
    * 更新了计步器的图标数据
    */
   private void updateChartData() {
      if(stepChartBean.getIndex() < 1440 -1) {
         stepChartBean.setIndex(stepChartBean.getIndex() + 1);
         stepChartBean.getArrayData()[stepChartBean.getIndex()] = stepBean.getStepCount();
      }
   }


   /**
    * 保存计步器的图标数据
    */
   private void saveChartData() {
      String jsonStr = Utils.objToJson(stepChartBean);
      ACache.get(MyApplication.getInstance()).put("JsonCharData", jsonStr);
   }


   private IMyAidlInterface.Stub iMyAidlInterface = new IMyAidlInterface.Stub() {

      @Override
      public void startStepCount() throws RemoteException {
         if(sensorManager != null && stepListener != null) {
            //获取加速传感器
            Sensor sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            //参数类型——>监听、传感器、延迟类型
            sensorManager.registerListener(stepListener,sensor,SensorManager.SENSOR_DELAY_NORMAL);

            stepBean.setStartTime(System.currentTimeMillis());
            stepBean.setDay(Utils.getTimeStepByDay());
            runStatus = STATUS_RUNNING;

            //开始触发数据刷新
            handler.postDelayed(timeRunnable,SAVE_CHART_TIME);
         }
      }

      @Override
      public void stopStepCount() throws RemoteException {
         if(sensorManager != null && stepListener != null) {
            //获取加速传感器
            Sensor sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            sensorManager.unregisterListener(stepListener, sensor);
            runStatus = STATUS_NOT_RUN;

            //停止数据刷新
            handler.removeCallbacks(timeRunnable);
         }
      }

      @Override
      public int getServiceRunningStatus() throws RemoteException {
         return runStatus;
      }

      @Override
      public long getStartTimestamp() throws RemoteException {
         if(stepBean != null) {
            return stepBean.getStartTime();
         }
         return 0L;
      }

      @Override
      public int getStepCount() throws RemoteException {
         if(stepBean != null) {
            return stepBean.getStepCount();
         }
         return 0;
      }

      @Override
      public double getCalorie() throws RemoteException {
         if(stepBean != null) {
            return Utils.getCalorieBySteps(stepBean.getStepCount());
         }
         return 0L;
      }

      @Override
      public double getDistance() throws RemoteException {
         if(stepBean != null) {
            return Utils.getDistanceVal(stepBean.getStepCount());
         }
         return 0;
      }

      @Override
      public void resetCount() throws RemoteException {
         if(stepBean != null) {
            stepBean.reset();
            saveData();
         }
         if(stepListener != null) {
            stepListener.setCurrentStep(0);
         }
      }

      @Override
      public void saveData() throws RemoteException {
         if(stepBean != null) {
            new Thread(new Runnable() {
               @Override
               public void run() {
                  try {
                     StepDBHelper stepDBHelper = StepDBHelper.getInstance(StepService.this);
                     //设置距离
                     stepBean.setDistance(getDistance());
                     //设置热量消耗
                     stepBean.setCalorie(getCalorie());
                     long time = (stepBean.getLastStepTime() - stepBean.getStartTime()) / 1000;
                     if(time == 0) {
                        //设置多少步/分钟
                        stepBean.setPace(0);
                        //速度
                        stepBean.setSpeed(0);
                     }else {
                        //设置多少步/分钟
                        int pace = Math.round(60 * stepBean.getStepCount() / time);
                        stepBean.setPace(pace);
                        //速度
                        long speed = Math.round((stepBean.getDistance()/1000) / (time/60*60));
                        stepBean.setSpeed(speed);
                     }
                     //写入数据库
                     stepDBHelper.writeTODB(stepBean);
                  } catch (RemoteException e) {
                     e.printStackTrace();
                  }
               }
            }).start();
         }
      }

      @Override
      public void setSensitivity(float sensitivity) throws RemoteException {
         if(settings != null) {
            settings.setSensitivity(sensitivity);
         }
      }

      @Override
      public double getSensitivity() throws RemoteException {
         if(settings != null) {
            return settings.getSensitivity();
         }
         return 0L;
      }

      @Override
      public void setInterval(int interval) throws RemoteException {
         if(settings != null) {
            settings.setInterval(interval);
         }
      }

      @Override
      public int getInterval() throws RemoteException {
         if(settings != null) {
            return settings.getInterval();
         }
         return 0;
      }

      @Override
      public StepChartBean getCharData() throws RemoteException {
         return null;
      }

   };

   @Nullable
   @Override
   public IBinder onBind(Intent intent) {
      return iMyAidlInterface;
   }
}
