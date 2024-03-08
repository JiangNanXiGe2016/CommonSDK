package com.example.ocr;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class DashedRectangleView extends View {

    private Paint dashedPaint;
    private Paint fillPaint;

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
        dashedPaint.setStrokeWidth(10); // 虚线宽度
        dashedPaint.setPathEffect(new DashPathEffect(new float[]{10, 5}, 0)); // 虚线效果，10像素实线，5像素空白

//        fillPaint = new Paint();
//        fillPaint.setColor(Color.TRANSPARENT); // 内部高亮颜色
//        fillPaint.setStyle(Paint.Style.FILL);

        fillPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        PorterDuffXfermode porterDuffXfermode = new PorterDuffXfermode(PorterDuff.Mode.CLEAR);
        fillPaint.setColor(Color.TRANSPARENT); // 内部高亮颜色
        fillPaint.setXfermode(porterDuffXfermode);
        fillPaint.setAntiAlias(true);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // 设置背景透明
        setBackgroundColor(Color.argb(128, 255, 255, 255)); // 半透明白色背景

        // 获取View的宽度和高度
        float factor=1.8f;
        float width = getWidth();
        float height = getHeight();
        float retW = 500*factor;
        float retH = 800*factor;
        float start = (width - retW) / 2;
        float top =  (height - retH) / 2;
        float end = (width + retW) / 2;
        float bottom = (height + retH) / 2;
        Log.i("yangliang","width="+width+" height="+height);

        // 绘制虚线矩形框
        //canvas.drawRect(120, 320, width - 120, height - 320, dashedPaint);
        canvas.drawRect(start, top, end, bottom, dashedPaint);

        // 绘制内部高亮区域（缩小一些以避免与虚线重叠）
        int inset = 5; // 内部高亮缩小的像素值
        canvas.drawRect(start+inset, top+inset, end-inset, bottom-inset, fillPaint);
    }
}
