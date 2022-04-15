package com.example.pedometer.Service;
import com.example.pedometer.Beans.StepChartBean;

interface IMyAidlInterface {

    //获取计步器步数
    int getStepCount();

    //重置计步器步数
    void resetCount();

    //开始记步
    void startStepCount();

    //停止记步
    void stopStepCount();

    //获取消耗的卡路里
    double getCalorie();

    //获取走路的距离
    double getDistance();

    //保存数据
    void saveData();

    //设置传感器敏感度
    void setSensitivity(float sensitivity);

    //获取传感器敏感度
    double getSensitivity();

    //获取采样时间
    int getInterval();

    //设置采样时间
    void setInterval(int interval);

    //获取时间戳
    long getStartTimestamp();

    //获取运行状态
    int getServiceRunningStatus();

    //获取运动图标数据
    StepChartBean getCharData();

}