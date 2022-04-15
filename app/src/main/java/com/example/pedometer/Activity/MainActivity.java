package com.example.pedometer.Activity;

import androidx.annotation.Nullable;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.pedometer.AppBase.BaseActivity;
import com.example.pedometer.Beans.StepChartBean;
import com.example.pedometer.R;
import com.example.pedometer.Service.IMyAidlInterface;
import com.example.pedometer.Service.StepService;
import com.example.pedometer.Utils.LogUtil;
import com.example.pedometer.Utils.Utils;
import com.example.pedometer.Widgets.CircleProgressBar;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;

import java.util.ArrayList;

public class MainActivity extends BaseActivity {

    private CircleProgressBar circleProgressBar;
    private TextView textCalorie;
    private TextView time;
    private TextView distance;
    private TextView stepCount;
    private Button reset;
    private Button btnStart;
    private BarChart barChart;

    private IMyAidlInterface iMyAidlInterface;
    private int status = -1;
    private static final int STATUS_NOT_RUNNING = 0;
    private static final int STATUS_RUNNING = 1;

    private volatile boolean isRunning = false;
    private volatile boolean isChartUpdate = false;

    private static final int MESSAGE_UPDATE_STEP_COUNT = 1000; //步数
    private static final int MESSAGE_UPDATE_CHART = 2000;  //图标

    private StepChartBean chartBean;
    private static final int GET_DATA_TIME = 200;
    private static final long GET_CHART_DATA_TIME = 60000L;
    private boolean bindService = false;

    @Override
    protected void onInitVariable() {

    }

    @Override
    protected void onInitView(@Nullable Bundle savedInstanceState) {
        setContentView(R.layout.activity_main);
        circleProgressBar = (CircleProgressBar)findViewById(R.id.circleProgressBar);
        circleProgressBar.setProgress(5000);
        circleProgressBar.setMaxProgress(10000);
        textCalorie = (TextView)findViewById(R.id.textCalorie);
        time = (TextView)findViewById(R.id.time);
        distance = (TextView)findViewById(R.id.distance);
        stepCount = (TextView)findViewById(R.id.stepCount);
        reset = (Button) findViewById(R.id.reset);
        btnStart = (Button) findViewById(R.id.btnStart);
        barChart = (BarChart) findViewById(R.id.barChart);

        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder mBuilder = new AlertDialog.Builder(MainActivity.this);
                mBuilder.setTitle("确认重置");
                mBuilder.setMessage("您的记录将会被清零,确定吗?");
                mBuilder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if (iMyAidlInterface != null) {
                            try {
                                iMyAidlInterface.stopStepCount();
                                iMyAidlInterface.resetCount();
                                chartBean = iMyAidlInterface.getCharData();
                                setData(chartBean);
                                status = iMyAidlInterface.getServiceRunningStatus();
                                if (status == STATUS_RUNNING) {
                                    btnStart.setText("停止");
                                } else if (status == STATUS_NOT_RUNNING) {
                                    btnStart.setText("启动");
                                }
                            } catch (RemoteException e) {
                                LogUtil.d(e.toString());
                            }
                        }
                        dialog.dismiss();
                    }
                });
                mBuilder.setNegativeButton("取消", null);
                AlertDialog dlg = mBuilder.create();
                dlg.show();
            }
        });
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    status = iMyAidlInterface.getServiceRunningStatus();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                if(status == STATUS_RUNNING && iMyAidlInterface != null) {
                    try {
                        iMyAidlInterface.stopStepCount();
                        btnStart.setText("启动");
                        isRunning = false;
                        isChartUpdate = false;
                    } catch (RemoteException e) {
                        LogUtil.d(e.toString());
                    }
                }else if (status == STATUS_NOT_RUNNING) {
                    if (iMyAidlInterface != null) {
                        try {
                            iMyAidlInterface.startStepCount();
                            btnStart.setText("停止");
                            isRunning = true;
                            isChartUpdate = true;
                            chartBean = iMyAidlInterface.getCharData();
                            setData(chartBean);
                            new Thread(new StepRunnable()).start();
                            new Thread(new ChartRunnable()).start();
                        } catch (RemoteException e) {
                            LogUtil.d(e.toString());
                        }
                    }
                }
            }
        });
    }

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            iMyAidlInterface = IMyAidlInterface.Stub.asInterface(iBinder);
            try {
                status = iMyAidlInterface.getServiceRunningStatus();
                if (status == STATUS_RUNNING) {
                    btnStart.setText("停止");
                    isRunning = true;
                    isChartUpdate = true;
                    chartBean = iMyAidlInterface.getCharData();
                    setData(chartBean);

                    //启动两个线程，定时刷新
                    new Thread(new StepRunnable()).start();
                    new Thread(new ChartRunnable()).start();
                } else {
                    btnStart.setText("启动");
                }
            } catch (RemoteException e) {
                LogUtil.d(e.toString());
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    };

    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_UPDATE_STEP_COUNT:
                    handler.removeMessages(MESSAGE_UPDATE_STEP_COUNT);
                    updateStepCount();
                    break;
                case MESSAGE_UPDATE_CHART:
                    handler.removeMessages(MESSAGE_UPDATE_CHART);
                    if (chartBean != null) {
                        setData(chartBean);
                    }
                    break;
                default:
                    LogUtil.d("Default = " + msg.what);
            }
            super.handleMessage(msg);
        }
    };

    /**
     * 记步线程
     */
    private class StepRunnable implements Runnable {
        public void run() {
            while (isRunning) {
                try {
                    //刷新一下服务的运行状态
                    status = iMyAidlInterface.getServiceRunningStatus();
                    if (status == STATUS_RUNNING) {
                        // 清理一下以前的消息
                        handler.removeMessages(MESSAGE_UPDATE_STEP_COUNT);
                        //更新一下UI显示
                        handler.sendEmptyMessage(MESSAGE_UPDATE_STEP_COUNT);
                        Thread.sleep(GET_DATA_TIME);
                    }
                } catch (RemoteException e) {
                    LogUtil.d(e.toString());
                } catch (InterruptedException e) {
                    LogUtil.d(e.toString());
                }
            }
        }
    }

    /**
     * 更新UI
     */
    public void updateStepCount() {
        if (iMyAidlInterface != null) {
            //服务正在运行
            int stepCountVal = 0;
            double calorieVal = 0;
            double distanceVal = 0;
            try {
                stepCountVal = iMyAidlInterface.getStepCount();
                calorieVal = iMyAidlInterface.getCalorie();
                distanceVal = iMyAidlInterface.getDistance();
                LogUtil.d("distance =" + distanceVal);
            } catch (RemoteException e) {
                LogUtil.d(e.toString());
            }
            stepCount.setText(String.valueOf(stepCountVal) + "步");
            textCalorie.setText(Utils.getFormatVal(calorieVal) + "卡");
            distance.setText(Utils.getFormatVal(distanceVal));
            circleProgressBar.setProgress(stepCountVal);
        }
    }

    private class ChartRunnable implements Runnable {
        public void run() {
            while (isChartUpdate) {
                try {
                    chartBean = iMyAidlInterface.getCharData();
                    handler.sendEmptyMessage(MESSAGE_UPDATE_CHART);
                    Thread.sleep(GET_CHART_DATA_TIME);
                } catch (InterruptedException e) {
                    LogUtil.d(e.toString());
                } catch (RemoteException e) {
                    LogUtil.d(e.toString());
                }
            }
        }
    }

    public void setData(StepChartBean bean) {
        //x坐标轴方向数据
        ArrayList<String> xVals = new ArrayList<String>();
        //y坐标轴
        ArrayList<BarEntry> yVals = new ArrayList<BarEntry>();

        if (bean != null) {
            for (int i = 0; i <= bean.getIndex(); i++) {
                xVals.add(String.valueOf(i) + "分");
                int valY = bean.getArrayData()[i];
                yVals.add(new BarEntry(valY, i));
            }
            time.setText(String.valueOf(bean.getIndex()) + "分");
            BarDataSet set1 = new BarDataSet(yVals, "所走步数");
            set1.setBarBorderWidth(2f);

            ArrayList<BarDataSet> dataSets = new ArrayList<BarDataSet>();
            dataSets.add(set1);
            BarData data = new BarData((IBarDataSet) xVals, (IBarDataSet) dataSets);
            data.setValueTextSize(10f);

            barChart.setData(data);
            barChart.invalidate();
        }
    }

    @Override
    protected void onRequestData() {
        Intent myServiceIntent = new Intent(this, StepService.class);
        //检查服务是否运行
        if (!Utils.isServiceRunning(MainActivity.this, StepService.class.getName())) {
            //服务没有运行，启动服务
            startService(myServiceIntent);
        }else {
            // 设置新TASK的方式
            myServiceIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }

        // 以bindService方法连接绑定服务
        bindService = bindService(myServiceIntent, serviceConnection, BIND_AUTO_CREATE);

        //初始化一些状态
        if (bindService && iMyAidlInterface != null) {
            try {
                status = iMyAidlInterface.getServiceRunningStatus();
                if (status == STATUS_NOT_RUNNING) {
                    btnStart.setText("启动");
                } else if(status == STATUS_RUNNING) {
                    btnStart.setText("停止");
                    iMyAidlInterface.startStepCount();
                    isRunning = true;
                    isChartUpdate = true;
                    //启动两个线程，定时刷新
                    new Thread(new StepRunnable()).start();
                    new Thread(new ChartRunnable()).start();
                }
            } catch (RemoteException e) {
                LogUtil.d(e.toString());
            }
        } else {
            btnStart.setText("启动");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (bindService) {
            bindService = false;
            isRunning = false;
            isChartUpdate = false;
            unbindService(serviceConnection);
        }
    }
}