package com.example.pedometer.Utils;

import android.content.Context;

public class Settings {
    //灵敏度等级
    public static final float[] SENSITIVE_ARRAY = {1.97f, 2.96f, 4.44f, 6.66f, 10.0f, 15.0f, 22.50f, 33.75f, 50.62f};
    //采样时间
    public static final int[] INTERVAL_ARRAY = {100, 200, 300, 400, 500, 600, 700, 800};
    public static final String SENSITIVITY = "sensitivity";
    public static final String INTERVAL = "interval";
    public static final String STEP_LEN = "steplen";
    public static final String BODY_WEIGHT = "bodyweight";
    SPManager spManager = null;

    public Settings(Context context)
    {
        spManager = new SPManager(context);
    }

    /**
     * 获取传感器灵敏度
     * @return
     */
    public double getSensitivity() {
        //灵敏度
        float sensitivity = spManager.getFloat(SENSITIVITY);
        if (sensitivity == 0.0f) {
            return 10.0f;
        }
        return sensitivity;
    }

    /**
     * 设置传感器灵敏度
     * @param sensitivity
     */
    public void setSensitivity(float sensitivity) {
        spManager.putFloat(SENSITIVITY, sensitivity);
    }

    /**
     * 获取时间间隔，默认200
     * @return
     */
    public int getInterval() {
        int interval = spManager.getInt(INTERVAL);
        if (interval == 0) {
            return 200;
        }
        return interval;
    }

    /**
     * 设置时间间隔
     * @param interval
     */
    public void setInterval(int interval) {
        spManager.putInt(INTERVAL, interval);
    }

    /**
     * 获取步距，默认50kg
     * @return
     */
    public float getStepLength() {
        float stepLength = spManager.getFloat(STEP_LEN);
        if (stepLength == 0.0f) {
            return 50.0f;
        }
        return stepLength;
    }

    /**
     * 设置步距
     * @param stepLength
     */
    public void setStepLength(float stepLength) {
        spManager.putFloat(STEP_LEN, stepLength);
    }

    /**
     * 获取体重，默认60
     * @return
     */
    public float getBodyWeight() {
        float bodyWeight = spManager.getFloat(BODY_WEIGHT);
        if (bodyWeight == 0.0f)
        {
            return 60.0f;
        }
        return bodyWeight;
    }

    /**
     * 设置体重
     * @param bodyWeight
     */
    public void setBodyWeight(float bodyWeight) {
        spManager.putFloat(BODY_WEIGHT, bodyWeight);
    }
}
