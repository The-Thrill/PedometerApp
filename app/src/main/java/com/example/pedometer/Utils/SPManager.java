package com.example.pedometer.Utils;


import android.content.Context;

/**
 * 保存配置文件
 */
public class SPManager {

   private static final String SP_NAME = "step_app";
   private final Context context;

   public SPManager(final Context context) {
      this.context = context;
   }

   /**
    * 清理
    */
   public void clear() {
      context.getSharedPreferences(SPManager.SP_NAME, Context.MODE_PRIVATE)
              .edit()
              .clear()
              .apply();
   }

   /**
    * 判断文件是否存在
    * @return boolean
    */
   public boolean contains() {
      return context.getSharedPreferences(SPManager.SP_NAME, Context.MODE_PRIVATE)
              .contains(SPManager.SP_NAME);
   }


   public boolean getBoolean(final String key) {
      return context.getSharedPreferences(SPManager.SP_NAME, Context.MODE_PRIVATE)
              .getBoolean(key, false);
   }


   public boolean getBooleanDefaultTrue(final String key) {
      return context.getSharedPreferences(SPManager.SP_NAME, Context.MODE_PRIVATE)
              .getBoolean(key, true);
   }


   public boolean putBoolean(final String key, final boolean value) {
      return context.getSharedPreferences(SPManager.SP_NAME, Context.MODE_PRIVATE)
              .edit()
              .putBoolean(key,value)
              .commit();
   }

   public int getInt(final String key)
   {
      return context.getSharedPreferences(SPManager.SP_NAME, Context.MODE_PRIVATE)
              .getInt(key, 0);
   }

   public boolean putInt(final String key, final int value)
   {
      return context.getSharedPreferences(SPManager.SP_NAME, Context.MODE_PRIVATE)
              .edit()
              .putInt(key, value)
              .commit();
   }

   public float getFloat(final String key)
   {
      return context.getSharedPreferences(SPManager.SP_NAME, Context.MODE_PRIVATE)
              .getFloat(key, 0.0f);
   }

   public boolean putFloat(final String key, final Float value)
   {
      return context.getSharedPreferences(SPManager.SP_NAME, Context.MODE_PRIVATE)
              .edit()
              .putFloat(key, value)
              .commit();
   }

   public long getLong(final String key)
   {
      return context.getSharedPreferences(SPManager.SP_NAME, Context.MODE_PRIVATE)
              .getLong(key, 0L);
   }

   public boolean putLong(final String key, final Long value)
   {
      return context.getSharedPreferences(SPManager.SP_NAME, Context.MODE_PRIVATE)
              .edit()
              .putLong(key, value)
              .commit();
   }

   public String getString(final String key)
   {
      return context.getSharedPreferences(SPManager.SP_NAME, Context.MODE_PRIVATE)
              .getString(key, "");
   }

   public boolean putString(final String key, final String value)
   {
      return context.getSharedPreferences(SPManager.SP_NAME, Context.MODE_PRIVATE)
              .edit()
              .putString(key, value)
              .commit();
   }


}

