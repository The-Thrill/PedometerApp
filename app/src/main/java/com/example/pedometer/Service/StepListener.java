package com.example.pedometer.Service;


import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;

import com.example.pedometer.Beans.StepBean;

/**
 * 加速度传感器的监听
 */
public class StepListener implements SensorEventListener {

   //当前步数
   private int currentStep = 0;
   //灵敏度
   private float sensitivity = 30;
   //采样时间
   private long limit = 300;
   //最后保存的数据
   private float lastValues;
   //缩放值
   private float scale = -4f;
   //偏移值
   private float offset = 240f;
   //采样时间
   private long start = 0;
   private long end = 0;

   //最后加速度方向
   private float lastDirections;
   //记录数值
   private float lastExtremes[][] = new float[2][1];
   //最后一次变化量
   private float lastDiff;
   //是否匹配
   private int lastMatch = -1;

   private StepBean stepBean;
   public StepListener(StepBean stepBean) {
      this.stepBean = stepBean;
   }

   public void setCurrentStep(int step) {
      currentStep = step;
   }

   /**
    * 当传感器检测到的数值发生变化时就会调用这个方法
    * @param sensorEvent
    */
   @Override
   public void onSensorChanged(SensorEvent sensorEvent) {
      Sensor sensor = sensorEvent.sensor;
      synchronized (this) {
         //加速传感器
         if(sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float sum = 0;
            for (int i = 0; i < 3; i++)
            {
               //数值进行放大
               float vector = offset + sensorEvent.values[i] * scale;
               sum += vector;
            }
            //记录三个轴向,传感器的平均值
            float average = sum/3;
            float dir;
            //判断方向
            if(average > lastValues) {
               dir = 1;
            }else if (average < lastValues) {
               dir = -1;
            }else {
               dir = 0;
            }

            //和上一次方向相反
            if(dir == -lastDirections) {
               //方向值
               int extType = (dir>0 ? 0:1);
               //保存数值变化
               lastExtremes[extType][0] = lastValues;
               //加速度变化的绝对值
//               float diff = Math.abs(lastExtremes[extType][0] - lastExtremes[extType][1]);
               float diff = Math.abs(lastExtremes[extType][0]);
               //是否大于灵敏度
               if(diff > sensitivity) {
                  //数值是否与上次的比，足够大
                  boolean isLargeAsPrevious = diff > (lastDiff * 2 / 3);
                  //数值是否小于上次数值的1/3
                  boolean isPreviousLargeEnough = lastDiff > (diff / 3);
                  //方向判断
                  boolean isNotContra = (lastMatch != 1 - extType);

                  //有效记录
                  if (isLargeAsPrevious && isPreviousLargeEnough && isNotContra) {
                     //当前毫秒数
                     end = System.currentTimeMillis();
                     //在limit限制时间内
                     if(end -start > limit) {
                        //此时判断为走了一步
                        currentStep++;
                        lastMatch = extType;
                        start = end;
                        lastDiff = diff;

                        if(stepBean != null) {
                           stepBean.setStepCount(currentStep);
                           stepBean.setLastStepTime(System.currentTimeMillis());
                        }

                     }else {
                        lastDiff = sensitivity;
                     }
                  }else {
                     //无效记录,不匹配
                     lastMatch = -1;
                     lastDiff = sensitivity;
                  }
               }
            }
            lastDirections = dir;
            lastValues = average;
         }
      }
   }

   @Override
   public void onAccuracyChanged(Sensor sensor, int i) {

   }
}
