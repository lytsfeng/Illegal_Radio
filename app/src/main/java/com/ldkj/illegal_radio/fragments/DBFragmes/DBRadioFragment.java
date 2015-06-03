package com.ldkj.illegal_radio.fragments.DBFragmes;

import android.annotation.TargetApi;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.ldkj.illegal_radio.R;
import com.ldkj.illegal_radio.events.UpdateViewEvent;
import com.ldkj.illegal_radio.fragments.base.FragmentBase;
import com.ldkj.illegal_radio.models.IllegalRadioModel;
import com.ldkj.illegal_radio.utils.Attribute;
import com.ldkj.illegal_radio.utils.Utils;
import com.ldkj.illegal_radio.utils.databases.sqlitedal.SQLiteDALIllegalRadio;
import com.ldkj.illegal_radio.views.adapters.base.AdapterBase;
import com.ldkj.illegal_radio.views.dialogs.IllegalInfoDialog;
import com.ldkj.illegal_radio.views.dialogs.base.DialogBase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import de.greenrobot.event.EventBus;

public class DBRadioFragment extends FragmentBase implements AdapterView.OnItemClickListener,DialogBase.DialogCallBack<IllegalRadioModel>{


    private View view;
    private ListView listView;
    private SQLiteDALIllegalRadio sqLiteDALIllegalRadio;
    private ArrayList<IllegalRadioModel> illegalRadioModels = new ArrayList<>();
    private MyAdapter adapter;
    public DBRadioFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        EventBus.getDefault().register(this);
        view = inflater.inflate(R.layout.fragment_dbradio, container, false);
        initview();
        addListener();
        binddata();
        return view;
    }


    public void onEventMainThread(UpdateViewEvent event) {
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroyView() {
        EventBus.getDefault().unregister(this);
        super.onDestroyView();
    }

    @Override
    protected void stopSelfTask() {

    }

    @Override
    protected void startNewTask() {

    }

    @Override
    public void updateData(float[] data) {

    }

    private void  initview() {
        listView = (ListView) view.findViewById(R.id.id_illegal_db_radio_list);
        sqLiteDALIllegalRadio = new SQLiteDALIllegalRadio(getActivity());
        adapter  =new MyAdapter(illegalRadioModels);
    }

    private void addListener(){
        listView.setOnItemClickListener(this);
    }

    private void binddata(){
//        new loadDataTask().execute();
        illegalRadioModels.clear();
        illegalRadioModels.addAll(sqLiteDALIllegalRadio.queryAll(""));
        Collections.sort(illegalRadioModels, new Comparator<IllegalRadioModel>() {
            public int compare(IllegalRadioModel arg0, IllegalRadioModel arg1) {
                return arg0.compareTo(arg1);
            }
        });


        listView.setAdapter(adapter);
    }
    private int position = 0;
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        this.position = position;
        IllegalRadioModel _Model = new IllegalRadioModel(illegalRadioModels.get(position));
        new IllegalInfoDialog(getActivity(),_Model,this).show();
    }

    @Override
    public void DialogBack(IllegalRadioModel radioModel, int p_ResId, Boolean isInit) {
        if(position >= 0 && position < illegalRadioModels.size()){
            IllegalRadioModel _Model = illegalRadioModels.get(position);
            if(_Model.handle != radioModel.handle || _Model.address != radioModel.address || _Model.tag != radioModel.tag){
                illegalRadioModels.remove(position);
                illegalRadioModels.add(position,radioModel);
                if(listener != null) {
                    listener.updateIllegal(radioModel, Attribute.OPERATION_TYPE.UPDATE);
                }
                EventBus.getDefault().post(new UpdateViewEvent());
            }
        }
    }

    class loadDataTask extends AsyncTask<Void,Void,Void>{

        //结束
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            //隐藏等待框

        }

        //开始
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            ////显示等待框

        }

        @Override
        protected Void doInBackground(Void... params) {
            if(sqLiteDALIllegalRadio != null){
                illegalRadioModels.clear();
                illegalRadioModels.addAll(sqLiteDALIllegalRadio.queryAll(""));

                Collections.sort(illegalRadioModels, new Comparator<IllegalRadioModel>() {
                    public int compare(IllegalRadioModel arg0, IllegalRadioModel arg1) {
                        return arg0.compareTo(arg1);
                    }
                });
            }
            return null;
        }
    }


//    private void updateIllegal(IllegalRadioModel pModel,Attribute.OPERATION_TYPE pType){
//        IllegalRadioModel _Model = null;
//        if(listener != null){
//            _Model = listener.updateIllegal(pModel,pType);
//        }
//        if(_Model == null){
//            return;
//        }
//        switch (pType){
//            case DELETE:
//                updateIllegal(_Model,false);
//                break;
//            case INSTER:
//                updateIllegal(_Model,true);
//                break;
//            default:
//                break;
//        }
//
//    }


    class  MyAdapter extends AdapterBase<IllegalRadioModel>{

        public MyAdapter(ArrayList<IllegalRadioModel> list) {
            super(list);
        }

        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            Holder hodler = null;
            if (convertView == null) {
                convertView = LayoutInflater.from(getActivity()).inflate(R.layout.item_list_illegal, null);
                hodler = new Holder();
                hodler.tvFreq = (TextView) convertView.findViewById(R.id.id_item_illegal_freq);
                hodler.tvTime = (TextView) convertView.findViewById(R.id.id_item_illegal_time);
                hodler.tvlat = (TextView) convertView.findViewById(R.id.id_item_illegal_lat);
                hodler.tvlon = (TextView) convertView.findViewById(R.id.id_item_illegal_lon);
                convertView.setTag(hodler);
            } else {
                hodler = (Holder) convertView.getTag();
            }

            IllegalRadioModel _Model = (IllegalRadioModel) getItem(position);
            hodler.tvFreq.setText(Utils.removalUnit(_Model.freq));
            hodler.tvTime.setText(_Model.appeartime);
            hodler.tvlat.setText(String.format("%.4f", _Model.lat));
            hodler.tvlon.setText(String.format("%.4f", _Model.lon));
            if(_Model.handle <= 0){
                convertView.setBackground(getActivity().getDrawable(R.color.red));
            }else {
                convertView.setBackground(getActivity().getDrawable(R.color.white));
            }
            return convertView;
        }

        class Holder {
            private TextView tvFreq;
            private TextView tvTime;
            private TextView tvlat;
            private TextView tvlon;
        }




    }


}
