package com.example.pedometer.Utils;

import android.app.ActivityManager;
import android.content.Context;

import com.google.gson.Gson;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

public class Utils {

    /**
     * 获取day
     * @return
     */
    public static long getTimeStepByDay() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date d = new Date();
        String dateStr = sdf.format(d);
        try
        {
            Date date = sdf.parse(dateStr);
            return date.getTime();
        } catch (ParseException e)
        {
            e.printStackTrace();
        }
        return 0L;
    }

    /***
     * 热量计算
     * @param stepCount
     * @return
     */
    public static double getCalorieBySteps(int stepCount) {
        //步长
        int stepLen = 50;
        //体重
        int bodyWeight = 70;
        //跑步
        double METRIC_RUNNING_FACTOR = 1.02784823;
        //走路
        double METRIC_WALKING_FACTOR = 0.708;
        double mCalories = 0;
        // 跑步热量（kcal）＝体重（kg）×距离（公里）×1.02784823
        // 走路热量（kcal）＝体重（kg）×距离（公里）×0.708
        mCalories = (bodyWeight * METRIC_WALKING_FACTOR) * stepLen * stepCount / 100000.0;
        return mCalories;
    }

    /**
     * 距离,单位千米
     *
     * @return
     */
    public static double getDistanceVal(int stepCount)
    {
        //步长
        int stepLen = 50;
        double distance = (stepCount * (long)stepLen) / 100000.0f;
        return distance;
    }

    /**
     * 将对象转换为字符串
     * @param object
     * @return
     */
    public static String objToJson(Object object) {
        Gson gson = new Gson();
        return gson.toJson(object);
    }

    public static String getFormatVal(double val)
    {
        DecimalFormat df = new DecimalFormat("0.00");
        return	df.format(val);
    }

    /**
     * 检查服务是否运行
     * @param context
     * @param className
     * @return
     */
    public static boolean isServiceRunning(Context context, String className)
    {
        if(context == null || className == null) {
            return false;
        }
        ActivityManager activityManager = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
        List servicesList = activityManager.getRunningServices(Integer.MAX_VALUE);
        Iterator iterator = servicesList.iterator();
        while(iterator.hasNext()) {
            ActivityManager.RunningServiceInfo si = (ActivityManager.RunningServiceInfo)iterator.next();
            if(className.equals(si.service.getClassName().trim())) {
                return true;
            }
        }
        return false;
    }



}
