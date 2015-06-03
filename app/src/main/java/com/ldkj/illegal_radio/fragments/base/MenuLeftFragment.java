package com.ldkj.illegal_radio.fragments.base;


import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.ldkj.illegal_radio.R;

public class MenuLeftFragment extends Fragment {

    private MenuOnItemClickListener listener;
    private ListView listView;
    private String[] values;

    public MenuLeftFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        listView = (ListView) inflater.inflate(R.layout.fragment_menu_left, container, false);
        bindData();
        addListener();
        return listView;
    }
    private void bindData() {
        values = getActivity().getResources().getStringArray(R.array.menu_left_item_name);
        listView.setAdapter(new ListAdapter());
    }
    private void addListener() {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (listener != null) {
                    listener.menuOnItemClickListener(position);
                }
            }
        });
    }
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        listener = (MenuOnItemClickListener) activity;
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        listener = null;
    }

    public interface MenuOnItemClickListener {
        void menuOnItemClickListener(int position);
    }
    class ListAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return values.length;
        }
        @Override
        public Object getItem(int position) {
            return values[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Holder hodler = null;
            if (convertView == null) {
                convertView = LayoutInflater.from(MenuLeftFragment.this.getActivity()).inflate(R.layout.menu_left_list_item, null);
                hodler = new Holder();
                hodler.textView = (TextView) convertView.findViewById(R.id.id_menu_left_list_item_tv);
                convertView.setTag(hodler);
            } else {
                hodler = (Holder) convertView.getTag();
            }
            hodler.textView.setText(values[position]);
            return convertView;
        }
        class Holder {
            public TextView textView;
        }
    }
}
