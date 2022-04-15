package com.example.pedometer.Beans;

import android.os.Parcel;
import android.os.Parcelable;

public class StepChartBean implements Parcelable {

   //记录全天的运动数据,用来生成曲线
   private int [] arrayData;
   //当前记录的索引值
   private int index;

   public StepChartBean() {
      index = 0;
      arrayData = new int[1440];
   }

   protected StepChartBean(Parcel in) {
      arrayData = in.createIntArray();
      index = in.readInt();
   }

   public static final Creator<StepChartBean> CREATOR = new Creator<StepChartBean>() {
      @Override
      public StepChartBean createFromParcel(Parcel in) {
         return new StepChartBean(in);
      }

      @Override
      public StepChartBean[] newArray(int size) {
         return new StepChartBean[size];
      }
   };

   public int[] getArrayData() {
      return arrayData;
   }

   public void setArrayData(int[] arrayData) {
      this.arrayData = arrayData;
   }

   public int getIndex() {
      return index;
   }

   public void setIndex(int index) {
      this.index = index;
   }

   @Override
   public int describeContents() {
      return 0;
   }

   @Override
   public void writeToParcel(Parcel parcel, int i) {
      parcel.writeIntArray(arrayData);
      parcel.writeInt(index);
   }
}
