package com.ldkj.illegal_radio.views.dialogs;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.ldkj.illegal_radio.R;
import com.ldkj.illegal_radio.models.RadioModels;
import com.ldkj.illegal_radio.views.dialogs.base.DialogBase;

/**
 * Created by john on 15-6-4.
 */
public class RadioInfoDialog extends DialogBase {

    private RadioModels radioModels =  null;


    public RadioInfoDialog(Context context,RadioModels models) {
        super(context);
        this.radioModels = models;
        setContentView(R.layout.dialog_radioinfo);
        initView();
    }
    private void initView(){
        if (radioModels != null){
            setText(R.id.id_radio_info_name,radioModels.name);
            setText(R.id.id_radio_info_freq,radioModels.getFreq());
            setText(R.id.id_radio_info_span,radioModels.getSpan());
            setText(R.id.id_radio_info_power,radioModels.power+"");
            setText(R.id.id_radio_info_lat,radioModels.lat+"");
            setText(R.id.id_radio_info_lon,radioModels.lon+"");
            setText(R.id.id_radio_info_address,radioModels.address);
            setText(R.id.id_radio_info_begin_time,radioModels.beginTime.trim());
            setText(R.id.id_radio_info_end_time,radioModels.engTime);
            setText(R.id.id_radio_info_tag,radioModels.tag);
        }
    }
    private void setText(int viewId,String pText){
        View _view = findViewById(viewId);
        if(_view instanceof TextView){
            ((TextView)_view).setText(pText);
        }
    }




}
