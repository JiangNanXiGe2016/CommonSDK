package com.example.commondsdk.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.example.commondsdk.camera2.DashBoxInfo;

public class DashedRectangleView extends View {

    private Paint dashedPaint;
    private Paint fillPaint;
    private Paint maskPaint;

    private Paint textPaint;

    private String text;

    public DashedRectangleView(Context context) {
        super(context);
        init();
    }

    public DashedRectangleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DashedRectangleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        dashedPaint = new Paint();
        dashedPaint.setColor(Color.WHITE); // 虚线颜色
        dashedPaint.setStyle(Paint.Style.STROKE);
        dashedPaint.setStrokeWidth(5); // 虚线宽度
        dashedPaint.setPathEffect(new DashPathEffect(new float[]{5, 5}, 5)); // 虚线效果，10像素实线，5像素空白

        fillPaint = new Paint();
        fillPaint.setColor(Color.TRANSPARENT); // 内部高亮颜色
        fillPaint.setStyle(Paint.Style.FILL);


        maskPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        maskPaint.setColor(Color.argb(128, 255, 255, 255));


        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(Color.GREEN);
        textPaint.setStyle(Paint.Style.FILL);
        textPaint.setTextSize(20);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // 获取View的宽度和高度,计算绘制尺寸
        float factor = 1.8f;
        float width = getWidth();
        float height = getHeight();
        float retW = 500 * factor;
        float retH = 800 * factor;

        blankStart = (width - retW) / 2;
        blankTop = (height - retH) / 2;
        blankEnd = (width + retW) / 2;
        blankBottom = (height + retH) / 2;
        info.startLeftX = blankStart;
        info.startLeftY = blankTop;
        info.endRightX = blankEnd;
        info.endRightY = blankBottom;
        Log.i("yangliang", "width=" + width + " height=" + height);
        // 绘制半透明区域
        canvas.drawRect(0, 0, width, blankTop, maskPaint);
        canvas.drawRect(0, blankTop, blankStart, blankBottom, maskPaint);
        canvas.drawRect(blankEnd, blankTop, width, blankBottom, maskPaint);
        canvas.drawRect(0, blankBottom, width, height, maskPaint);
        //绘制虚线框
        canvas.drawRect(blankStart, blankTop, blankEnd, blankBottom, dashedPaint);
        // 绘制内部高亮区域（缩小一些以避免与虚线重叠）
        int inset = 5; // 内部高亮缩小的像素值
        canvas.drawRect(blankStart + inset, blankTop + inset, blankEnd - inset, blankBottom - inset, fillPaint);
    }

    float blankStart;
    float blankTop;
    float blankEnd;

    float blankBottom;
    DashBoxInfo info = new DashBoxInfo();
    ;

    public DashBoxInfo getDashBoxPos() {
        return info;
    }


    public void drawText(String text) {
        this.text = text;
        postInvalidate();
    }
}
