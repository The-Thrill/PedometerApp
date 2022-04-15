package com.example.pedometer.Widgets;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.SweepGradient;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

/**
 * 自定义进度条
 */
public class CircleProgressBar extends View {

    private int progress = 0;
    private int maxProgress = 100;

    //绘制轨迹
    private Paint pathPaint;
    //绘制填充
    private Paint fillPaint;

    private RectF oval;
    //梯度渐变的填充颜色
    private int[] arcColors = new int[]{0xFF02C016, 0xFF3DF346, 0xFF40F1D5, 0xFF02C016};
    //背景颜色
    private final int pathColor = 0xFFF0EEDF;
    //边框颜色
    private int borderColor = 0xFFD2D1C4;
    //环的路径宽度
    private int pathWidth = 35; //圆的宽度
    private int width;
    private int height;
    //默认圆的半径
    private int radius = 120;
    //梯度渲染渐变色
    private SweepGradient sweepGradient;
    //重置
    private boolean reset = false;

    public CircleProgressBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        pathPaint = new Paint();
        //反锯齿
        pathPaint.setAntiAlias(true);
        pathPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        // 设置中空的样式
        pathPaint.setStyle(Paint.Style.STROKE);
        pathPaint.setDither(true);
        //线条连接
        pathPaint.setStrokeJoin(Paint.Join.ROUND);

        fillPaint = new Paint();
        //反锯齿
        fillPaint.setAntiAlias(true);
        fillPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        // 设置中空的样式
        fillPaint.setStyle(Paint.Style.STROKE);
        fillPaint.setDither(true);
        //线条连接
        fillPaint.setStrokeJoin(Paint.Join.ROUND);

        oval = new RectF();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        setMeasuredDimension(width,height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(reset) {
            if (this.reset) {
                canvas.drawColor(0xFFFFFFFF);
                reset = false;
            }
            width = getMeasuredWidth();
            height = getMeasuredHeight();
            radius = getMeasuredWidth() / 2 - pathWidth;

            //设置背景颜色
            pathPaint.setColor(pathColor);
            //设置画笔宽度
            pathPaint.setStrokeWidth(pathWidth);
            //在中心的地方画个半径为r的圆
            canvas.drawCircle(width/2, height/2, radius, pathPaint);
            pathPaint.setStrokeWidth(0.5F);
            pathPaint.setColor(borderColor);
            canvas.drawCircle(width / 2, height / 2, (float) radius + pathWidth / 2 + 0.5F, pathPaint);
            canvas.drawCircle(width / 2, height / 2, (float)radius - pathWidth / 2 - 0.5F, pathPaint);

            sweepGradient = new SweepGradient((float)(width / 2), (float)(height / 2), arcColors, null);
            fillPaint.setShader(sweepGradient);
            //线帽为圆角
            fillPaint.setStrokeCap(Paint.Cap.ROUND);
            fillPaint.setStrokeWidth((float) pathWidth);
            oval.set(width/2 -radius, height/2 -radius,width/2 +radius, height/2 +radius);
            canvas.drawArc(oval, -90.0F,  progress / maxProgress * 360.0F, false, fillPaint);
        }
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
        invalidate();
    }

    public int getMaxProgress() {
        return maxProgress;
    }

    public void setMaxProgress(int maxProgress) {
        this.maxProgress = maxProgress;
        invalidate();
    }

    public int getPathColor() {
        return pathColor;
    }

    public int getBorderColor() {
        return borderColor;
    }

    public void setBorderColor(int borderColor) {
        this.borderColor = borderColor;
    }

    public int getPathWidth() {
        return pathWidth;
    }

    public void setPathWidth(int pathWidth) {
        this.pathWidth = pathWidth;
    }

    public void setReset(boolean reset) {
        this.reset = reset;
        if(reset) {
            progress =0;
            invalidate();
        }
    }
}
