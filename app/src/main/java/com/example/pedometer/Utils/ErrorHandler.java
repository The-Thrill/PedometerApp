package com.example.pedometer.Utils;


import android.content.Context;

import androidx.annotation.NonNull;

import com.example.pedometer.AppBase.MyApplication;

/**
 * 未捕获的异常
 */
public class ErrorHandler implements Thread.UncaughtExceptionHandler  {

   /**
    * 将未捕获的error保存到文件
    * @param thread
    * @param throwable
    */
   @Override
   public void uncaughtException(@NonNull Thread thread, @NonNull Throwable throwable) {
      LogUtil.LogToFile( "Error", "崩溃信息:" + throwable.getMessage());
      LogUtil.LogToFile( "Error", "崩溃线程名称:" + thread.getName() + ", 线程ID:" + thread.getId());
      final StackTraceElement[] traceElements = throwable.getStackTrace();
      for (final StackTraceElement element:traceElements) {
         LogUtil.LogToFile("Error","Lines:" + element.getLineNumber() + " : " + element.getMethodName());
      }
      throwable.printStackTrace();
      MyApplication.exitApp();
   }

   private static ErrorHandler instance;
   public static ErrorHandler getInstance() {
      if(ErrorHandler.instance == null) {
         ErrorHandler.instance = new ErrorHandler();
      }
      return ErrorHandler.instance;
   }

   private ErrorHandler() {

   }

   public void setErrorHandler(final Context context) {
      Thread.setDefaultUncaughtExceptionHandler(this);
   }
}
