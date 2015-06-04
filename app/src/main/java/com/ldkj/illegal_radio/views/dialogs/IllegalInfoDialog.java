package com.ldkj.illegal_radio.views.dialogs;

import android.content.Context;
import android.media.MediaPlayer;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.ldkj.illegal_radio.OwnApplication;
import com.ldkj.illegal_radio.R;
import com.ldkj.illegal_radio.models.IllegalRadioModel;
import com.ldkj.illegal_radio.utils.Utils;
import com.ldkj.illegal_radio.views.dialogs.base.DialogBase;

/**
 * Created by john on 15-5-20.
 */
public class IllegalInfoDialog extends DialogBase implements DialogBase.DialogCallBack<String>,View.OnClickListener,CompoundButton.OnCheckedChangeListener {


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
    private CheckBox box_sound;
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
        tvFreq = (TextView) findViewById(R.id.id_radio_info_freq);
        tvStatus = (TextView) findViewById(R.id.id_radio_info_name);
        tvTime = (TextView) findViewById(R.id.id_illegal_info_time);
        tvlat = (TextView) findViewById(R.id.id_radio_info_lat);
        tvLon = (TextView) findViewById(R.id.id_radio_info_lon);
        et_address = (EditText) findViewById(R.id.id_radio_info_address);
        et_tag = (EditText) findViewById(R.id.id_radio_info_tag);
        btn_ok = (Button) findViewById(R.id.id_illegal_info_ok);
        box_sound = (CheckBox) findViewById(R.id.id_dialog_sound);
        btn_ok.setFocusable(true);
        btn_cancel = (Button) findViewById(R.id.id_illegal_info_cancel);
    }

    private void addListener() {
        tvFreq.setOnClickListener(this);
        tvStatus.setOnClickListener(this);
        btn_ok.setOnClickListener(this);
        btn_cancel.setOnClickListener(this);
        box_sound.setOnCheckedChangeListener(this);
    }

    private void bindDate() {
        if (model != null) {
            if(model.uid != -1 ) {
                tvFreq.setClickable(false);
            }
            if(!Utils.isExit(Utils.getSoundFileName(model,true))){
                box_sound.setVisibility(View.GONE);
            }
            tvFreq.setText(model.freq);
            tvStatus.setText(model.handle >= 1 ? NO_ILLEGAL : ILLEGAL);
            tvTime.setText(model.appeartime);
            tvlat.setText(String.format("%.4f", model.lat));
            tvLon.setText(String.format("%.4f", model.lon));
            et_address.setText(model.address == null ? "" : model.address);
            et_tag.setText(model.tag == null ? "" : model.tag);
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
            case R.id.id_radio_info_freq:
                tvFreq.setText(s);
                break;
            case R.id.id_radio_info_name:
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
                String _freq = tvFreq.getText().toString().trim();
                if(!TextUtils.isEmpty(_freq)){
                    if (callBack != null)
                        callBack.DialogBack(getModel(), 0, false);
                    dismiss();
                }else{
                    Toast.makeText(OwnApplication.getContext(), "中心频率不能为空", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.id_radio_info_freq:
                NumberDialog _di = new NumberDialog(getContext(),R.id.id_radio_info_freq,this);
                _di.show();
                break;
            case R.id.id_radio_info_name:
                SelectListDialog _Dialog = new SelectListDialog(getContext(),R.id.id_radio_info_name,R.array.illegal_status_name,this);
                _Dialog.show();
                break;
        }


    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
       sound(isChecked);
    }

    private void sound(Boolean isSound){
        if(isSound){
            //播放声音
            playMusic(Utils.getSoundFileName(model, true));
        }else {
            //停止播放声音
            stopPlayer();
        }
    }

    private void stopPlayer() {
        if(player != null){
            player.stop();
            player.release();
            player = null;
        }
    }
    private MediaPlayer player = null;
    void playMusic(String path){
        if(player == null){
            try {
                player =new MediaPlayer();
                player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        if(!mp.isPlaying()){
                            box_sound.setChecked(false);
                            stopPlayer();
                        }
                    }
                });
                player.reset();
                player.setDataSource(path);
                player.prepare();
                player.start();
            } catch (Exception e) {
                // TODO: handle exception
                e.printStackTrace();
            }
        }

    }
    @Override
    public void dismiss() {
        stopPlayer();
        super.dismiss();
    }
}
