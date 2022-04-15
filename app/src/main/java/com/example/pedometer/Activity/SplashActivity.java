package com.example.pedometer.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;

import androidx.annotation.Nullable;

import com.example.pedometer.AppBase.BaseActivity;
import com.example.pedometer.R;

public class SplashActivity extends BaseActivity {

   private Handler handler;
   private Runnable runnable;

   @Override
   protected void onInitVariable() {
      handler = new Handler();
      runnable = new Runnable() {
         @Override
         public void run() {
            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
            startActivity(intent);
            SplashActivity.this.finish();
         }
      };
      getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
   }

   @Override
   protected void onInitView(@Nullable Bundle savedInstanceState) {
      setContentView(R.layout.activity_splash);
   }

   @Override
   protected void onRequestData() {
      handler.postDelayed(runnable, 3000);

   }
}
