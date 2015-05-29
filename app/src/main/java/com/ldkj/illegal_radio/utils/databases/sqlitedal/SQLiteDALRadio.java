package com.ldkj.illegal_radio.utils.databases.sqlitedal;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.ldkj.illegal_radio.models.RadioModels;
import com.ldkj.illegal_radio.utils.databases.base.SQLiteDALBase;

/**
 * Created by john on 15-5-28.
 */
public class SQLiteDALRadio extends SQLiteDALBase<RadioModels> {
    public SQLiteDALRadio(Context p_Context) {
        super(p_Context);
    }
    @Override
    public RadioModels install(RadioModels radioModels) {
        return null;
    }

    @Override
    protected RadioModels FindModel(Cursor pCursor) {
        return null;
    }

    @Override
    protected String[] GetTableNameAndPK() {
        return new String[0];
    }

    @Override
    public boolean Update(String pCondition, RadioModels pT) {
        return false;
    }


    @Override
    public void OnCreate(SQLiteDatabase p_DataBase) {
        String _SqlText = "create table radio(" +
                "id";
    }

    @Override
    public void OnUpgrade(SQLiteDatabase p_DataBase) {

    }
}
