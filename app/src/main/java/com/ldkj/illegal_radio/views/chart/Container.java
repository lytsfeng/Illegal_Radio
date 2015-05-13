package com.ldkj.illegal_radio.views.chart;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import java.util.ArrayList;

/**
 * Created by john on 15-5-5.
 */
public class Container {

    protected float xMaxValue = (float) 108;
    protected float xMinValue = (float) 88;
    protected float yMinValue = -40;
    protected float yMaxValue = 100;
    protected float xCursorPixel = -1;
    protected float yCursorPixel = -1;
    protected float x = 32, y = 32, width = 0, height = 0;
    private ArrayList<Container> childView = null;
    private static final float MARKERHEIGHT = 80;

    protected float xMarkerX1 = -1,xMarkerY1 = -1,xMarkerX2 = -1,xMarkerY2= -1;
    protected float yMarkerX1 = -1,yMarkerY1 = -1,yMarkerX2 = -1,yMarkerY2= -1;

    protected ArrayList<Float> pointList = new ArrayList<>();

    public Container() {
        childView = new ArrayList<>();
    }

    public void setxMaxValue(float xMaxValue) {
        this.xMaxValue = xMaxValue;
    }

    public void setxMinValue(float xMinValue) {
        this.xMinValue = xMinValue;
    }

    public void addChild(Container chartContainer) {
        if (childView != null) {
            childView.add(chartContainer);
        }
    }

    public void onDraw(Canvas pCanvas) {
        pCanvas.save();
        pCanvas.drawColor(Color.BLACK);
        onDrawCustomChild(pCanvas);
        if (childView != null) {
            for (Container child : childView) {
                child.onDrawCustomChild(pCanvas);
            }
        }
        onDrawLine(pCanvas);
        onDrawXMarker(pCanvas);
        onDrawYMarker(pCanvas);

        pCanvas.restore();
    }


    protected float getXValueFromPixel(float pixel) {
        float _flag = (xMaxValue - xMinValue) / (width - x);
        return (pixel - x) * _flag + xMinValue;
    }

    protected float getXPixelFromValue(float value) {
        float _flag = (width - x) / (xMaxValue - xMinValue);
        return (value - xMinValue) * _flag + x;
    }

    protected float getYValueFromPixel(float pixel) {
        float _yCount = yMaxValue - yMinValue;
        float _flag = _yCount / (height - y);
        return _yCount - (pixel - y) * _flag + yMinValue;
    }

    protected float getYPixelFromValue(float value) {
        float _valueFlag = (height - y) / (yMaxValue - yMinValue);
        return height - (value - yMinValue) * _valueFlag;
    }


    protected void onDrawCustomChild(Canvas pCanvas) {
        width = pCanvas.getWidth() - x;
        height = pCanvas.getHeight() - y;
        if (xCursorPixel < 0) {
            xCursorPixel = getXPixelFromValue((xMaxValue - xMinValue) / 2 + xMinValue);
        }
        if(yCursorPixel < 0){
            yCursorPixel = getYPixelFromValue((yMaxValue - yMinValue) / 2 + yMinValue);
        }
        updateXMarker();
        updateYMarker();

    }

    private void onDrawXMarker(Canvas pCanvas) {
        Paint paint = new Paint();
        paint.setColor(Color.BLUE);
        pCanvas.drawLine(xCursorPixel, y, xCursorPixel, height, paint);
        pCanvas.drawRect(xMarkerX1, xMarkerY1, xMarkerX2, xMarkerY2, paint);
    }
    private void onDrawYMarker(Canvas pCanvas) {
        Paint paint = new Paint();
        paint.setColor(Color.BLUE);
        pCanvas.drawLine(x, yCursorPixel, width, yCursorPixel, paint);
        paint.setColor(Color.YELLOW);
        paint.setAlpha(100);
        pCanvas.drawRect(yMarkerX1, yMarkerY1, yMarkerX2, yMarkerY2, paint);
        paint.setColor(Color.BLUE);
        paint.setTextSize(30);
        pCanvas.drawText(getYValueFromPixel(yCursorPixel) + "", xMarkerX1, yCursorPixel, paint);
    }


    public boolean isSelectXMarker(float x,float y){
        return x > xMarkerX1 && x < xMarkerX2 && y>xMarkerY1 && y < xMarkerY2;
    }
    public boolean isSelectYMarker(float x,float y){
        return x > yMarkerX1 && x < yMarkerX2 && y>yMarkerY1 && y < yMarkerY2;
    }

    public void updatexCursorPixel(float xCursorPixelMove){

        if(xCursorPixelMove > width){
            xCursorPixel = width;
            return;
        }
        if(xCursorPixelMove < x){
            xCursorPixel = x;
            return;
        }
        this.xCursorPixel = xCursorPixelMove;
    }
    public void updateyCursorPixel(float yCursorPixelMove){
        if(yCursorPixel > height){
            yCursorPixel = height;
            return;
        }
        if(yCursorPixel < y){
            yCursorPixel = y;
            return;
        }
        this.yCursorPixel = yCursorPixelMove;
    }

    public void updateXMarker(){
        xMarkerX1 = xCursorPixel - MARKERHEIGHT;
        xMarkerY1 = height - MARKERHEIGHT;
        xMarkerX2 = xCursorPixel + MARKERHEIGHT;
        xMarkerY2 = height;
    }
    protected void updateYMarker(){
        yMarkerX1 = width - MARKERHEIGHT / 2;
        yMarkerY1 = yCursorPixel - MARKERHEIGHT;
        yMarkerX2 = width  + MARKERHEIGHT / 2;
        yMarkerY2 = yCursorPixel + MARKERHEIGHT;
    }

    public void bindDate(float[] pDate){
        synchronized (pointList){
            pointList.clear();
//            pointList.addAll(Arrays.<Float>asList(pDate));
            int _dateLen = pDate.length;
            for(int i = 0; i < _dateLen; i++){
                pointList.add(pDate[i]);
            }
        }
    }

    private void onDrawLine(Canvas canvas){
        Paint paint = new Paint();
        paint.setColor(Color.GREEN);
        paint.setStrokeWidth(2);
        synchronized (pointList){
            int _pointSize = pointList.size();
            float _pFlag = (width -x) / (_pointSize - 1);
            for(int i = 0; i < _pointSize -1; i++){
                canvas.drawLine(_pFlag * i + x,getYPixelFromValue(pointList.get(i)), _pFlag * (i + 1) + x, getYPixelFromValue(pointList.get(i+1)), paint);
            }
        }
    }

}
