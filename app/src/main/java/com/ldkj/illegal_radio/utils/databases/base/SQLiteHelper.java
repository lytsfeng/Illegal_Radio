package com.ldkj.illegal_radio.utils.databases.base;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.ldkj.illegal_radio.utils.Reflection;

import java.util.ArrayList;


public class SQLiteHelper extends SQLiteOpenHelper {

    private static SQLiteDateBaseConfig SQLITE_DATEBASE_CONFIG;
    private static SQLiteHelper INSTANCE;
    private Context mContext;
    private Reflection mReflection;

    private SQLiteHelper(Context pContext) {
        super(pContext, SQLITE_DATEBASE_CONFIG.GetDataBaseName(), null, SQLITE_DATEBASE_CONFIG.GetVersion());
        mContext = pContext;
    }

    public static SQLiteHelper GetInstance(Context pContext) {
        if (INSTANCE == null) {
            SQLITE_DATEBASE_CONFIG = SQLiteDateBaseConfig.GetInstance(pContext);
            INSTANCE = new SQLiteHelper(pContext);
        }

        return INSTANCE;
    }

    @Override
    public void onCreate(SQLiteDatabase pDataBase) {
        ArrayList<String> _ArrayList = SQLITE_DATEBASE_CONFIG.GetTables();
        mReflection = new Reflection();

        for (int i = 0; i < _ArrayList.size(); i++) {
            try {
                ((SQLiteDataTable) mReflection.newInstance(_ArrayList.get(i), new Object[]{mContext}, new Class[]{Context.class})).OnCreate(pDataBase);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
        // TODO Auto-generated method stub

    }

    public interface SQLiteDataTable {
        public void OnCreate(SQLiteDatabase p_DataBase);
        public void OnUpgrade(SQLiteDatabase p_DataBase);
    }

}
