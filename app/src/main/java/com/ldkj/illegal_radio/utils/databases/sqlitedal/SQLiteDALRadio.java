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
        RadioModels _Models = new RadioModels();
        _Models.id = pCursor.getInt(pCursor.getColumnIndex("id"));
        _Models.name = pCursor.getString(pCursor.getColumnIndex("name"));
        _Models.freq = pCursor.getLong(pCursor.getColumnIndex("freq"));
        _Models.span = pCursor.getInt(pCursor.getColumnIndex("span"));
        _Models.lon = (pCursor.getDouble(pCursor.getColumnIndex("lon")));
        _Models.lat = (pCursor.getDouble(pCursor.getColumnIndex("lat")));
        _Models.address = pCursor.getString(pCursor.getColumnIndex("address"));
        _Models.power = pCursor.getInt(pCursor.getColumnIndex("power"));
        _Models.tag = pCursor.getString(pCursor.getColumnIndex("tag"));
        _Models.beginTime = pCursor.getString(pCursor.getColumnIndex("begintime"));
        _Models.engTime = pCursor.getString(pCursor.getColumnIndex("endtime"));
        return _Models;
    }

    @Override
    protected String[] GetTableNameAndPK() {
        return new String[]{"radio","id"};
    }

    @Override
    public boolean Update(String pCondition, RadioModels pT) {
        return false;
    }


    @Override
    public void OnCreate(SQLiteDatabase p_DataBase) {
        String _SqlText = "create table radio(" +
                "[id] integer primary key autoincrement not null," +
                "[name] text not null," +
                "[freq] long not null," +
                "[span] long not null," +
                "[lon] double not null," +
                "[lat] double not null," +
                "[address] text not null," +
                "[power] integer not null," +
                "[begintime] datetime not null," +
                "[endtime] datetime not null," +
                "[tag] text);";
        try{
            p_DataBase.execSQL(_SqlText);
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override
    public void OnUpgrade(SQLiteDatabase p_DataBase) {

    }
}
