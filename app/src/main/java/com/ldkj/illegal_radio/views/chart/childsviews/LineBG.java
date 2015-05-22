package com.ldkj.illegal_radio.views.chart.childsviews;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import com.ldkj.illegal_radio.views.chart.Container;

/**
 * Created by john on 15-5-5.
 */
public class LineBG extends Container {

    private Paint paint;

    public LineBG() {
        paint = new Paint();
        paint.setColor(Color.WHITE);
    }

    protected void onDrawCustomChild(Canvas pCanvas) {
        super.onDrawCustomChild(pCanvas);
        onDrawX(pCanvas);
        onDrawY(pCanvas);
    }
    private void onDrawX(Canvas pCanvas){
        float _Flag = (xMaxValue - xMinValue) / 4;
        for (int i = 0; i < 5; i++){
            float _Value = i * _Flag + xMinValue;
            float _Pixel = getXPixelFromValue(_Value);
            pCanvas.drawLine(_Pixel,y,_Pixel,height,paint);
            pCanvas.drawText(_Value+"",_Pixel-16,height+16,paint);
        }
    }
    private void onDrawY(Canvas pCanvas){
        float _YColunt = yMaxValue - yMinValue;
        int _f = (int) (_YColunt / 20 + 1);
        float _flag = _YColunt / 5;
        for (int i =0; i < 6; i++){
            float _Value = _YColunt - i * _flag + yMinValue;
            float _Pixel = getYPixelFromValue(_Value);
            pCanvas.drawLine(x,_Pixel,width,_Pixel,paint);
            pCanvas.drawText(_Value+"",x-30,_Pixel,paint);
        }
    }


}
