package com.ldkj.illegal_radio.fragments.DBFragmes;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.ldkj.illegal_radio.R;
import com.ldkj.illegal_radio.fragments.base.FragmentBase;
import com.ldkj.illegal_radio.models.RadioModels;
import com.ldkj.illegal_radio.utils.Utils;
import com.ldkj.illegal_radio.utils.databases.sqlitedal.SQLiteDALRadio;
import com.ldkj.illegal_radio.views.adapters.base.AdapterBase;
import com.ldkj.illegal_radio.views.dialogs.RadioInfoDialog;

import java.util.ArrayList;

public class DBRadioFragment extends FragmentBase implements AdapterView.OnItemClickListener{


    private View view;
    private ListView listView;
    private SQLiteDALRadio sqLiteDALRadio;
    private ArrayList<RadioModels> radioModels = new ArrayList<>();
    private MyAdapter adapter;
    public DBRadioFragment() {
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_dbradio, container, false);
        initview();
        addListener();
        binddata();
        return view;
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
        sqLiteDALRadio = new SQLiteDALRadio(getActivity());
        adapter  =new MyAdapter(radioModels);
    }

    private void addListener(){
        listView.setOnItemClickListener(this);
    }

    private void binddata(){
        radioModels.clear();
        radioModels.addAll(sqLiteDALRadio.queryAll(""));
        listView.setAdapter(adapter);
    }
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        new RadioInfoDialog(getActivity(), radioModels.get(position)).show();
    }
    class  MyAdapter extends AdapterBase<RadioModels>{
        public MyAdapter(ArrayList<RadioModels> list) {
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
                hodler.tvName = (TextView) convertView.findViewById(R.id.id_item_illegal_time);
                hodler.tvlat = (TextView) convertView.findViewById(R.id.id_item_illegal_lat);
                hodler.tvlon = (TextView) convertView.findViewById(R.id.id_item_illegal_lon);
                convertView.setTag(hodler);
            } else {
                hodler = (Holder) convertView.getTag();
            }
            RadioModels _Model = (RadioModels) getItem(position);
            hodler.tvFreq.setText(Utils.removalUnit(_Model.getFreq()));
            hodler.tvName.setText(_Model.name);
            hodler.tvlat.setText(String.format("%.4f", _Model.lat));
            hodler.tvlon.setText(String.format("%.4f", _Model.lon));
            return convertView;
        }

        class Holder {
            private TextView tvFreq;
            private TextView tvName;
            private TextView tvlat;
            private TextView tvlon;
        }




    }


}
