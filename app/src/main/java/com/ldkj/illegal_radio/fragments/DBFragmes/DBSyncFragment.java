package com.ldkj.illegal_radio.fragments.DBFragmes;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.ldkj.illegal_radio.R;
import com.ldkj.illegal_radio.models.DBConfig;

public class DBSyncFragment extends Fragment {



    private DBConfig dbConfig;
    private EditText etAddress;
    private EditText etPort;
    private Button btnConn;
    private ImageButton iBtnPull;
    private ImageButton iBtnPush;
    private boolean isConn = false;

    public DBSyncFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View _view = inflater.inflate(R.layout.fragment_dbsync, container, false);
        dbConfig = DBConfig.getDBConfig();

        initView(_view);
        drawView();
        addListener();
        return _view;
    }


    private void initView(View view) {
        etAddress = (EditText) view.findViewById(R.id.et_db_address);
        etPort = (EditText) view.findViewById(R.id.et_db_port);
        btnConn = (Button) view.findViewById(R.id.btn_db_conn);
        iBtnPull = (ImageButton) view.findViewById(R.id.ibtn_db_pull);
        iBtnPush = (ImageButton) view.findViewById(R.id.ibtn_db_push);
    }

    private void bindData() {
        etAddress.setText(dbConfig.address);
        etPort.setText(dbConfig.port + "");
    }

    private void addListener() {
        btnConn.setOnClickListener(btnOnClickListener);
        iBtnPush.setOnClickListener(btnOnClickListener);
        iBtnPull.setOnClickListener(btnOnClickListener);
    }

    private void drawView() {
        btnConn.setText(isConn ? "断开" : "连接");
        iBtnPull.setEnabled(isConn);
        iBtnPush.setEnabled(isConn && dbConfig.isUpdate);
        etAddress.setEnabled(!isConn);
        etPort.setEnabled(!isConn);
    }
    private void saveDBConfig(){
        dbConfig.address = etAddress.getText().toString();
        dbConfig.port = Integer.parseInt(etPort.getText().toString().trim());
        DBConfig.saveDBConfig(dbConfig);
    }
    private View.OnClickListener btnOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int _ViewID = v.getId();
            switch (_ViewID) {
                case R.id.btn_db_conn:
                    isConn = !isConn;
                    drawView();
                    break;
                case R.id.ibtn_db_pull:  // 拉
                    break;
                case R.id.ibtn_db_push: // 推
                    dbConfig.isUpdate = false;
                    break;
                default:
                    break;
            }
            saveDBConfig();
        }
    };


    final class DBAsyncTask extends AsyncTask<Void, Integer, Integer> {

        @Override
        protected Integer doInBackground(Void... params) {
            return null;
        }
    }


}
