package com.ldkj.illegal_radio.fragments;


import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.ldkj.illegal_radio.R;
import com.ldkj.illegal_radio.activitys.MainActivity;
import com.ldkj.illegal_radio.events.UpdateViewEvent;
import com.ldkj.illegal_radio.fragments.base.FragmentBase;
import com.ldkj.illegal_radio.models.IllegalRadioModel;
import com.ldkj.illegal_radio.utils.Attribute;
import com.ldkj.illegal_radio.utils.Utils;
import com.ldkj.illegal_radio.views.chart.Lines;

import java.util.HashSet;
import java.util.Set;

import de.greenrobot.event.EventBus;

/**
 * A simple {@link Fragment} subclass.
 */
public class ScanFragment extends FragmentBase {


    private Lines lines = null;
    private ListView infoValue = null;
    private Set<IllegalRadioModel> scanIllegalRadioModelSet = new HashSet<>();
    private IllegalListAdapter listAdapter = null;

    public ScanFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        EventBus.getDefault().register(this);
        View _view = inflater.inflate(R.layout.fragment_scan, container, false);
        initView(_view);
        binddata();
        addListener();
        return _view;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
    }

    private void initView(View view) {
        lines = (Lines) view.findViewById(R.id.id_scan_chart);
        infoValue = (ListView) view.findViewById(R.id.id_scan_list);
        listAdapter = new IllegalListAdapter();
        infoValue.setAdapter(listAdapter);
    }

    private void addListener() {
        infoValue.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ((MainActivity) getActivity()).startSingle(position, true);
            }
        });
    }

    private void binddata() {
        scanIllegalRadioModelSet.addAll(((MainActivity) getActivity()).getMainIllegalRadioModelSet());
        EventBus.getDefault().post(new UpdateViewEvent());
        for (IllegalRadioModel model : scanIllegalRadioModelSet) {
            if (lines != null) {
                lines.updateIllegalMarker(Utils.valueToMHz(model.freq) + "", true);
            }
        }
    }

    @Override
    protected void stopSelfTask() {
        if (listener != null) {
            listener.stopTask(Attribute.TASKTYPE.SINGLE);
        }
    }

    @Override
    protected void startNewTask() {
        if (listener != null) {
            listener.startTask(Attribute.TASKTYPE.SCAN);
        }
    }

    @Override
    public void updateIllegal(IllegalRadioModel pModel, boolean isAdd) {
        lines.updateIllegalMarker(pModel.freq, isAdd);
        boolean isSuccess = false;
        if (isAdd) {
            if (scanIllegalRadioModelSet.add(pModel)) {
                //更新列表
                isSuccess = true;
            }
        } else {
            if (scanIllegalRadioModelSet.remove(pModel)) {
                //更新列表
                isSuccess = true;
            }
        }
//        if (isSuccess)
            EventBus.getDefault().post(new UpdateViewEvent());
    }

    public void onEventMainThread(UpdateViewEvent updateViewEvent) {
        listAdapter.notifyDataSetChanged();
    }

    @Override
    public void updateData(float[] data) {
        if(datatype != Attribute.DATATYPE.SCANDATA){
            return;
        }
        lines.bindData(data);
    }

    class IllegalListAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return scanIllegalRadioModelSet.size();
        }

        @Override
        public Object getItem(int position) {
            return scanIllegalRadioModelSet.toArray()[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            Holder hodler = null;
            if (convertView == null) {
                convertView = LayoutInflater.from(ScanFragment.this.getActivity()).inflate(R.layout.item_list_illegal, null);
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
