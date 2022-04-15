package com.example.pedometer.AppBase;

import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

/**
 * 封装Activity,规范一些初始化等操作
 */
public abstract class BaseActivity extends FragmentActivity {

   //是否显示程序标题
   protected boolean isHideAppTitle = true;
   //是否显示系统标题
   protected boolean isHideSystemTitle = false;

   @Override
   protected void onCreate(@Nullable Bundle savedInstanceState) {
      //初始化变量
      this.onInitVariable();
      //隐藏程序标题
      if(this.isHideAppTitle) {
         this.requestWindowFeature(Window.FEATURE_NO_TITLE);
      }
      super.onCreate(savedInstanceState);
      //隐藏系统标题
      if(this.isHideSystemTitle) {
         this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
      }
      //构造view,绑定事件
      this.onInitView(savedInstanceState);
      //请求数据
      this.onRequestData();
      //加入ActivityList
      MyApplication.addToActivityList(this);
   }

   @Override
   protected void onDestroy() {
      MyApplication.removeFromActivityList(this);
      super.onDestroy();
   }

   /**
    * 1、最先被调用，用于初始化一些变量，创建一些对象
    */
   protected abstract void onInitVariable();

   /**
    * 2、初始化UI 布局加载等操作
    * @param savedInstanceState
    */
   protected abstract void onInitView(@Nullable Bundle savedInstanceState);

   /**
    * 3、请求数据
    */
   protected abstract void onRequestData();
}
