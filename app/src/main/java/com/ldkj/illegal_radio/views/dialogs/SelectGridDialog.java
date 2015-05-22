package com.ldkj.illegal_radio.views.dialogs;

import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.ldkj.illegal_radio.R;
import com.ldkj.illegal_radio.views.adapters.GridAdapter;
import com.ldkj.illegal_radio.views.dialogs.base.DialogBase;


/**
 * Created by john on 15-3-12.
 */
public class SelectGridDialog extends DialogBase implements AdapterView.OnItemClickListener {
    private String[] values;
    private GridView gridView;
    private GridAdapter adapter;
    private int arrayId;
    private Context context;
    private int resId;
    private DialogCallBack<String> callBack;

    public SelectGridDialog(Context context, int resId, int arrayId, DialogCallBack<String> callBack) {
        super(context);
        this.context = context;
        this.arrayId = arrayId;
        this.resId = resId;
        this.callBack = callBack;
        setContentView(R.layout.dialog_select_grid);
        gridView = (GridView) findViewById(R.id.gridview);
        values = context.getResources().getStringArray(arrayId);
        adapter = new GridAdapter(context, values);
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (callBack != null)
            callBack.DialogBack(values[position], resId, false);
        dismiss();
    }
}
