package com.ldkj.illegal_radio.views.dialogs;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.ldkj.illegal_radio.R;
import com.ldkj.illegal_radio.models.IllegalRadioModel;
import com.ldkj.illegal_radio.views.dialogs.base.DialogBase;

/**
 * Created by john on 15-5-20.
 */
public class IllegalInfoDialog extends DialogBase implements DialogBase.DialogCallBack<String>,View.OnClickListener {


    private static final String ILLEGAL = "未处理";
    private static final String NO_ILLEGAL = "以处理";
    private TextView tvFreq;
    private TextView tvStatus;
    private TextView tvTime;
    private TextView tvlat;
    private TextView tvLon;
    private EditText et_address;
    private EditText et_tag;
    private Button btn_ok;
    private Button btn_cancel;
    private IllegalRadioModel model = null;
    private DialogCallBack<IllegalRadioModel> callBack;

    public IllegalInfoDialog(Context context, IllegalRadioModel pModel, DialogCallBack<IllegalRadioModel> callBack) {
        super(context);
        this.model = pModel;
        this.callBack = callBack;
        setContentView(R.layout.dialog_illegalinfo);
        initView();
        addListener();
        bindDate();
    }


    private void initView() {
        tvFreq = (TextView) findViewById(R.id.id_illegal_info_freq);
        tvStatus = (TextView) findViewById(R.id.id_illegal_info_status);
        tvTime = (TextView) findViewById(R.id.id_illegal_info_time);
        tvlat = (TextView) findViewById(R.id.id_illegal_info_lat);
        tvLon = (TextView) findViewById(R.id.id_illegal_info_lon);
        et_address = (EditText) findViewById(R.id.id_illegal_info_address);
        et_tag = (EditText) findViewById(R.id.id_illegal_info_tag);
        btn_ok = (Button) findViewById(R.id.id_illegal_info_ok);
        btn_ok.setFocusable(true);
        btn_cancel = (Button) findViewById(R.id.id_illegal_info_cancel);
    }

    private void addListener() {
        tvFreq.setOnClickListener(this);
        tvStatus.setOnClickListener(this);
        btn_ok.setOnClickListener(this);
        btn_cancel.setOnClickListener(this);
    }

    private void bindDate() {
        if (model != null) {
            tvFreq.setText(model.freq);
            tvStatus.setText(model.handle >= 1 ? NO_ILLEGAL : ILLEGAL);
            tvTime.setText(model.appeartime);
            tvlat.setText(String.format("%.4f", model.lat));
            tvLon.setText(String.format("%.4f", model.lon));
            et_address.setText(model.address);
            et_tag.setText(model.tag);
        }
    }

    private IllegalRadioModel getModel() {
        model.freq = tvFreq.getText().toString().trim();
        model.handle = tvStatus.getText().toString().trim().equalsIgnoreCase(NO_ILLEGAL) ? 1 : 0;
        model.appeartime = tvTime.getText().toString();
        model.lon = Double.parseDouble(tvLon.getText().toString().trim());
        model.lat = Double.parseDouble(tvlat.getText().toString().trim());
        model.address = et_address.getText().toString().trim();
        model.tag = et_tag.getText().toString().trim();
        return model;
    }


    @Override
    public void DialogBack(String s, int p_ResId, Boolean isInit) {
        switch (p_ResId){
            case R.id.id_illegal_info_freq:
                tvFreq.setText(s);
                break;
            case R.id.id_illegal_info_status:
                tvStatus.setText(s);
                break;
        }
    }
    @Override
    public void onClick(View v) {
        int vid = v.getId();
        switch (vid) {
            case R.id.id_illegal_info_cancel:
                dismiss();
                break;
            case R.id.id_illegal_info_ok:
                if (callBack != null)
                    callBack.DialogBack(getModel(), 0, false);
                dismiss();
                break;
            case R.id.id_illegal_info_freq:
                NumberDialog _di = new NumberDialog(getContext(),R.id.id_illegal_info_freq,this);
                _di.show();
                break;
            case R.id.id_illegal_info_status:
                SelectListDialog _Dialog = new SelectListDialog(getContext(),R.id.id_illegal_info_status,R.array.illegal_status_name,this);
                _Dialog.show();
                break;
        }


    }

}
