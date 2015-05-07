package com.ldkj.illegal_radio.views.chart;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.ldkj.illegal_radio.utils.Attribute;
import com.ldkj.illegal_radio.views.chart.childsviews.LineBG;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by john on 15-5-5.
 */
public class Lines extends SurfaceView implements SurfaceHolder.Callback{

    private Canvas canvas;
    private Container chartContainer;
    private Timer drawTimer = null;

    public Lines(Context context, AttributeSet attrs) {
        super(context, attrs);
        getHolder().addCallback(this);
        chartContainer = new Container();
        chartContainer.addChild(new LineBG());
    }

    private void draw(){
        canvas = this.getHolder().lockCanvas();
        if(chartContainer != null){
            chartContainer.onDraw(canvas);
        }
        this.getHolder().unlockCanvasAndPost(canvas);
    }


    public void bindData(){
//        chartContainer.bindDate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float _EventX = event.getX();
        float _EventY = event.getY();
        if(chartContainer.isSelectXMarker(_EventX,_EventY)){
            switch (event.getAction()){
                case MotionEvent.ACTION_MOVE:
                    chartContainer.updatexCursorPixel(_EventX);
                    isChange = true;
                    isdraw = false;
                    break;
            }
            return true;
        }else if(chartContainer.isSelectYMarker(_EventX,_EventY)){
            switch (event.getAction()){
                case MotionEvent.ACTION_DOWN:
                    break;
                case MotionEvent.ACTION_MOVE:
                    chartContainer.updateyCursorPixel(_EventY);
                    isChange = true;
                    isdraw = false;
                    break;
            }
            return true;
        }else {
            return super.onTouchEvent(event);
        }
    }
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        startDraw();
    }
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }
    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.i(Attribute.TAG,"surfaceDestroyed");
        stopDraw();
    }

    private boolean isChange = true;
    private boolean isdraw = false;
    private void startDraw(){
        if(drawTimer != null){
            return;
        }
        drawTimer = new Timer();
        drawTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (isChange && !isdraw) {
                    draw();
                    isChange = false;
                    isdraw = true;
                }
            }
        },10,10);
    }
    private void stopDraw(){
        if(drawTimer != null){
            drawTimer.cancel();
            drawTimer = null;
        }
    }

}
