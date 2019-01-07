package com.example.wangky.mymusicplayer;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.SurfaceHolder;

public class CustomSurfaceView extends SurfaceView implements SurfaceHolder.Callback ,Runnable{

    private SurfaceHolder mSurfaceHolder;

    private Canvas mCanvas;

    private boolean mIsDrawing;

    private Paint mPaint;

    private Path mPath;


    public CustomSurfaceView(Context context) {
        this(context,null);
    }

    public CustomSurfaceView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public CustomSurfaceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mSurfaceHolder = getHolder();
        mSurfaceHolder.addCallback(this);
        setFocusable(true);
        mPaint = new Paint();
        mPaint.setColor(Color.BLACK);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(5);
        mPaint.setAntiAlias(true);


        mPath = new Path();
        mPath.moveTo(0,0);

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

        mIsDrawing = true;
        //启动绘制线程
        new Thread(this).start();

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        mIsDrawing = false;
    }

    @Override
    public void run() {
        while (mIsDrawing){
            long start = System.currentTimeMillis();
            drawImg();
            long end = System.currentTimeMillis();
            if(end -start <100){
                try {
                    Thread.sleep(100 -(end -start));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }





    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int x = (int) event.getX();
        int y = (int) event.getY();

        switch (event.getAction()){

            case MotionEvent.ACTION_DOWN:
                mPath.moveTo(x,y);
                break;

            case MotionEvent.ACTION_MOVE:
                mPath.lineTo(x,y);
                break;

            case MotionEvent.ACTION_UP:

                break;
        }

        return true;
    }



    public void drawImg(){

        try{
            mCanvas = mSurfaceHolder.lockCanvas();

            mCanvas.drawColor(Color.WHITE);

            mCanvas.drawPath(mPath,mPaint);

        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if(null != mCanvas){
                mSurfaceHolder.unlockCanvasAndPost(mCanvas);
            }
        }


    }
}
