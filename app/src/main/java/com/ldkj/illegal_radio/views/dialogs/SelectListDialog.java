package com.ldkj.illegal_radio.views.dialogs;

import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.ldkj.illegal_radio.R;
import com.ldkj.illegal_radio.views.adapters.ListAdapter;
import com.ldkj.illegal_radio.views.dialogs.base.DialogBase;

/**
 * Created by john on 15-3-10.
 */
public class SelectListDialog extends DialogBase implements AdapterView.OnItemClickListener{

    private Context context;
    private ListView listView;
    private ListAdapter adapter;
    private String[] values;
    private int resId;
    private DialogCallBack<String> callBack;
    public SelectListDialog(Context context, int resId, int arrayId,DialogCallBack<String> callBack) {
        super(context);
        this.context = context;
        this.resId = resId;
        this.callBack = callBack;
        setContentView(R.layout.dialog_select_list);
        listView = (ListView) findViewById(R.id.dialog_select_item);
        values = context.getResources().getStringArray(arrayId);
        adapter = new ListAdapter(context, values);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
    }

    public SelectListDialog(Context context, int resId, String[] array ,DialogCallBack<String> callBack) {
        super(context);
        this.context = context;
        this.resId = resId;
        this.callBack = callBack;
        setContentView(R.layout.dialog_select_list);
        listView = (ListView) findViewById(R.id.dialog_select_item);
        values = array;
        adapter = new ListAdapter(context, values);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if(callBack != null)
            callBack.DialogBack(values[position], resId,false);
        dismiss();
    }
}
