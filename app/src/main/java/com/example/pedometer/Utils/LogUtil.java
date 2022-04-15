package com.example.pedometer.Utils;

import android.os.Environment;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

/**
 * 提供日志工具
 */
public class LogUtil {

   private static final String DEBUG_TAG = "step";
   private static boolean isDebug = true;
   //是否写入到文件,默认为false
   private static boolean isWriteToLog = false;

   /**
    * 写入到文件
    * @param tag
    * @param logText
    */
   public static void LogToFile(final String tag, final  String logText) {
      if(!LogUtil.isWriteToLog) {
         return;
      }

      final String needWriteToMessage = tag + " : " + logText;
      final String fileName = Environment.getDataDirectory().getPath() + "/LogFile.txt";
      final File file = new File(fileName);
      try {
         //追加字段
         final FileWriter fileWriter = new FileWriter(file,true);
         //BufferedWriter缓存
         final BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
         bufferedWriter.write(needWriteToMessage);
         bufferedWriter.newLine();
         bufferedWriter.close();
         fileWriter.close();
      }catch (Exception e){
         e.printStackTrace();
      }
   }

   public static void i(final String message) {
      //输出控制台
      if(LogUtil.isDebug) {
         Log.i(LogUtil.DEBUG_TAG, message);
      }

      //写入文件
      if(LogUtil.isWriteToLog) {
         LogUtil.LogToFile(LogUtil.DEBUG_TAG, message);
      }
   }

   public static void d(final String message) {
      //输出控制台
      if(LogUtil.isDebug) {
         Log.d(LogUtil.DEBUG_TAG, message);
      }

      //写入文件
      if(LogUtil.isWriteToLog) {
         LogUtil.LogToFile(LogUtil.DEBUG_TAG, message);
      }
   }

   public static void w(final String message) {
      //输出控制台
      if(LogUtil.isDebug) {
         Log.w(LogUtil.DEBUG_TAG, message);
      }

      //写入文件
      if(LogUtil.isWriteToLog) {
         LogUtil.LogToFile(LogUtil.DEBUG_TAG, message);
      }
   }

   public static void e(final String message) {
      //输出控制台
      if(LogUtil.isDebug) {
         Log.e(LogUtil.DEBUG_TAG, message);
      }

      //写入文件
      if(LogUtil.isWriteToLog) {
         LogUtil.LogToFile(LogUtil.DEBUG_TAG, message);
      }
   }

   public static void v(final String message) {
      //输出控制台
      if(LogUtil.isDebug) {
         Log.v(LogUtil.DEBUG_TAG, message);
      }

      //写入文件
      if(LogUtil.isWriteToLog) {
         LogUtil.LogToFile(LogUtil.DEBUG_TAG, message);
      }
   }
}
