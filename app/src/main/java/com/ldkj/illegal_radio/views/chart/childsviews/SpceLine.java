package com.ldkj.illegal_radio.views.chart.childsviews;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import com.ldkj.illegal_radio.views.chart.Container;

/**
 * Created by john on 15-5-5.
 */
public class SpceLine extends Container {
    private Paint paint;


    public SpceLine(){
        paint = new Paint();
        paint.setColor(Color.GREEN);
    }

    @Override
    protected void onDrawCustomChild(Canvas pCanvas) {
        super.onDrawCustomChild(pCanvas);
        onDrawLine(pCanvas);
    }

    private void onDrawLine(Canvas canvas){
        synchronized (pointList){
            int _pointSize = pointList.size();
            float _pFlag = (width -x) / (_pointSize - 1);
            for(int i = 0; i < _pointSize -1; i++){
                canvas.drawLine(_pFlag * i + x,getYPixelFromValue(pointList.get(i)), _pFlag * (i + 1) + x, getYPixelFromValue(pointList.get(i+1)), paint);
            }
        }
    }

}
