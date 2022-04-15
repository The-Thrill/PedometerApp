package com.example.pedometer.AppBase;

import android.app.Activity;
import android.app.Application;

import com.example.pedometer.Utils.ErrorHandler;
import com.example.pedometer.Utils.SPManager;

import java.util.LinkedList;

/**
 * 自定义MyApplication，初始化一些设置
 */
public class MyApplication extends Application {

   private static MyApplication instance;
   private SPManager spManager;
   private ErrorHandler errorHandler;
   private static LinkedList<Activity> activityList = new LinkedList<Activity>();

   @Override
   public void onCreate() {
      super.onCreate();
      instance = this;
      spManager = new SPManager(this);
      errorHandler = ErrorHandler.getInstance();
   }

   /**
    * 返回MyApplication
    * @return
    */
   public static MyApplication getInstance() {
      return instance;
   }

   /**
    * 返回SP
    * @return
    */
   public SPManager getSpManager() {
      return spManager;
   }

   /**
    * 获取当前activity列表
    * @return activityList
    */
   public LinkedList<Activity> getActivityLinkedList() {
      return activityList;
   }

   /**
    * 添加到activity列表
    * @param activity
    */
   public static void addToActivityList(final Activity activity) {
      if(activity != null) {
         activityList.add(activity);
      }
   }

   /**
    * 移除activity
    * @param activity
    */
   public static void removeFromActivityList(final Activity activity) {
      if(activityList != null && activityList.size() > 0 && activityList.indexOf(activity) != -1 ) {
         activityList.remove(activity);
      }
   }

   /**
    * 清楚所有activity
    */
   public static void clearActivityList() {
      //倒序清除
      for (int i = activityList.size() - 1; i >= 0 ; i--) {
         final Activity activity = activityList.get(i);
         if(activity != null) {
            activity.finish();
         }
      }
   }

   /**
    * 退出App
    */
   public static void exitApp() {
      try {
         clearActivityList();
      }catch (Exception e){
         e.printStackTrace();
      }finally {
         System.exit(0);
         android.os.Process.killProcess(android.os.Process.myPid());
      }
   }


}
