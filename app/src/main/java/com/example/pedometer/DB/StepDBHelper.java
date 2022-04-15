package com.example.pedometer.DB;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.example.pedometer.Beans.StepBean;

import java.util.ArrayList;

public class StepDBHelper extends SQLiteOpenHelper {

    private static StepDBHelper stepDBHelper;
    //数据库版本号
    private static final int VERSION = 1;
    //数据库名称
    private static final String DB_NAME = "STEP";
    //数据表名称
    private static final String TABLE_NAME = "step";
    //数据库列
    public static final String[] COLUMNS =
            {
                    "id",
                    "stepCount",
                    "calorie",
                    "distance",
                    "pace",
                    "speed",
                    "startTime",
                    "lastStepTime",
                    "day"
            };

    private StepDBHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public static StepDBHelper getInstance(Context context) {
       synchronized (StepDBHelper.class) {
          if(stepDBHelper == null) {
             stepDBHelper = new StepDBHelper(context,StepDBHelper.DB_NAME,null,StepDBHelper.VERSION);
          }
          return stepDBHelper;
       }

    }

   //该函数是在第一次创建的时候执行，实际上是第一次得到SQLiteDatabase对象的时候才会调用这个方法
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
       //execSQL用于执行SQL语句
       sqLiteDatabase.execSQL("CREATE TABLE " + TABLE_NAME + " (id integer  PRIMARY KEY AUTOINCREMENT DEFAULT NULL," +
               "stepCount integer," +
               "calorie Double," +
               "distance Double DEFAULT NULL," +
               "pace INTEGER," +
               "speed Double," +
               "startTime Timestamp DEFAULT NULL," +
               "lastStepTime Timestamp  DEFAULT NULL," +
               "day Timestamp   DEFAULT NULL)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    /***
     * 将数据写入数据库
     * @param stepBean
     */
    public void writeTODB(StepBean stepBean) {
        SQLiteDatabase database = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("stepCount", stepBean.getStepCount());
        values.put("calorie", stepBean.getCalorie());
        values.put("distance", stepBean.getDistance());
        values.put("pace", stepBean.getPace());
        values.put("speed", stepBean.getSpeed());
        values.put("startTime", stepBean.getStartTime());
        values.put("lastStepTime", stepBean.getLastStepTime());
        values.put("day", stepBean.getDay());
        database.insert(StepDBHelper.TABLE_NAME, null, values);
        database.close();
    }

    /**
     * 根据天为单位获取计步的基本数据
     * @param dayTime
     * @return
     */
    @SuppressLint("Range")
    public StepBean getDayTime(long dayTime) {
        StepBean stepBean = new StepBean();
        Cursor cursor = null;
        SQLiteDatabase database = getWritableDatabase();
        cursor = database.rawQuery("select * from " + StepDBHelper.TABLE_NAME + " where day=" + String.valueOf(dayTime), null);
        if (cursor != null && cursor.getCount() > 0) {
            if (cursor.moveToNext()) {
                int id = cursor.getInt(cursor.getColumnIndex(StepDBHelper.COLUMNS[0]));
                int stepCount = cursor.getInt(cursor.getColumnIndex(StepDBHelper.COLUMNS[1]));
                double calorie = cursor.getDouble(cursor.getColumnIndex(StepDBHelper.COLUMNS[2]));
                double distance = cursor.getDouble(cursor.getColumnIndex(StepDBHelper.COLUMNS[3]));
                int pace = cursor.getInt(cursor.getColumnIndex(StepDBHelper.COLUMNS[4]));
                double speed = cursor.getDouble(cursor.getColumnIndex(StepDBHelper.COLUMNS[5]));
                long startTime = cursor.getLong(cursor.getColumnIndex(StepDBHelper.COLUMNS[6]));
                long lastStepTime = cursor.getLong(cursor.getColumnIndex(StepDBHelper.COLUMNS[7]));
                long day = cursor.getLong(cursor.getColumnIndex(StepDBHelper.COLUMNS[8]));
                stepBean.setId(id);
                stepBean.setStepCount(stepCount);
                stepBean.setCalorie(calorie);
                stepBean.setDistance(distance);
                stepBean.setPace(pace);
                stepBean.setSpeed(speed);
                stepBean.setStartTime(startTime);
                stepBean.setLastStepTime(lastStepTime);
                stepBean.setDay(day);
            }
            cursor.close();
            database.close();
        }
        return stepBean;
    }

    /**
     * 获取全部数据，进行分页
     * @return
     */
    @SuppressLint("Range")
    public ArrayList<StepBean> getFromDatabase() {
        //分页
        int pageSize = 20;
        int offVal = 0;
        ArrayList<StepBean> data = new ArrayList<StepBean>();
        StepBean stepBean = new StepBean();
        Cursor cursor = null;
        SQLiteDatabase db = getWritableDatabase();
        cursor = db.query(StepDBHelper.TABLE_NAME, null, null, null, null, null,
                "day desc limit " + String.valueOf(pageSize) + " offset " + String.valueOf(offVal),
                null);
        if (cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                int id = cursor.getInt(cursor.getColumnIndex(StepDBHelper.COLUMNS[0]));
                int stepCount = cursor.getInt(cursor.getColumnIndex(StepDBHelper.COLUMNS[1]));
                double calorie = cursor.getDouble(cursor.getColumnIndex(StepDBHelper.COLUMNS[2]));
                double distance = cursor.getDouble(cursor.getColumnIndex(StepDBHelper.COLUMNS[3]));
                int pace = cursor.getInt(cursor.getColumnIndex(StepDBHelper.COLUMNS[4]));
                double speed = cursor.getDouble(cursor.getColumnIndex(StepDBHelper.COLUMNS[5]));
                long startTime = cursor.getLong(cursor.getColumnIndex(StepDBHelper.COLUMNS[6]));
                long lastStepTime = cursor.getLong(cursor.getColumnIndex(StepDBHelper.COLUMNS[7]));
                long day = cursor.getLong(cursor.getColumnIndex(StepDBHelper.COLUMNS[8]));
                stepBean.setId(id);
                stepBean.setStepCount(stepCount);
                stepBean.setCalorie(calorie);
                stepBean.setDistance(distance);
                stepBean.setPace(pace);
                stepBean.setSpeed(speed);
                stepBean.setStartTime(startTime);
                stepBean.setLastStepTime(lastStepTime);
                stepBean.setDay(day);
                data.add(stepBean);
            }
            cursor.close();
            db.close();
        }
        return data;
    }

    /**
     * 更新数据
     * @param values
     * @param dayTime
     */
    public void updateToDatabase(ContentValues values, long dayTime) {
        SQLiteDatabase database = getWritableDatabase();
        database.update(StepDBHelper.TABLE_NAME, values, "day=?", new String[]{String.valueOf(dayTime)});
        database.close();
    }
}
