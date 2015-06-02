package com.ldkj.illegal_radio.utils.databases.sqlitedal;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.ldkj.illegal_radio.models.IllegalRadioModel;
import com.ldkj.illegal_radio.utils.databases.base.SQLiteDALBase;

import java.text.MessageFormat;

/**
 * Created by john on 15-5-20.
 */
public class SQLiteDALIllegalRadio extends SQLiteDALBase<IllegalRadioModel> {
    public SQLiteDALIllegalRadio(Context p_Context) {
        super(p_Context);
    }

    @Override
    public IllegalRadioModel install(IllegalRadioModel radioModel) {
//        radioModel.appeartime = getTime();
        String _sql = MessageFormat.format("insert into Illegal " +
                        "([handle],[freq],[time],[lon],[lat],[address],[tag]) " +
                        "values ({0},''{1}'',''{2}'',''{3}'',''{4}'',''{5}'',''{6}'');",
                radioModel.handle, radioModel.freq,
                radioModel.appeartime, radioModel.lon*10000 , radioModel.lat*10000,
                radioModel.address, radioModel.tag);
        try{
            GetDataBase().execSQL(_sql);
        }catch (Exception r){
            r.printStackTrace();
        }
        radioModel.uid = getMaxId();
        return radioModel;
    }


    @Override
    public Boolean Delete(String pCondition) {
        ContentValues cv = new ContentValues();
        cv.put("[handle]",1);
        return GetDataBase().update(GetTableNameAndPK()[0],cv," 1=1 " + pCondition, null) >= 0;
    }

    public boolean Update(String pCondition,IllegalRadioModel illegalRadioModel){
        ContentValues cv = new ContentValues();
        cv.put("[handle]",illegalRadioModel.handle);
        cv.put("[freq]",illegalRadioModel.freq);
        cv.put("[address]",illegalRadioModel.address);
        cv.put("[tag]",illegalRadioModel.tag);
        return GetDataBase().update(GetTableNameAndPK()[0],cv," 1=1 " + pCondition, null) >= 0;
    }

    private String getTime() {
        return ExecSql("select datetime('now','localtime') as appeartime").getString(0);
    }

    @Override
    protected IllegalRadioModel FindModel(Cursor pCursor) {
        IllegalRadioModel _RadioModels = new IllegalRadioModel();
        _RadioModels.uid = pCursor.getInt(pCursor.getColumnIndex("uid"));
        _RadioModels.handle = pCursor.getInt(pCursor.getColumnIndex("handle"));
        _RadioModels.freq = pCursor.getString(pCursor.getColumnIndex("freq"));
        _RadioModels.appeartime = pCursor.getString(pCursor.getColumnIndex("time"));
        String _lat = pCursor.getString(pCursor.getColumnIndex("lat"));
        _lat = _lat.replaceAll(",","");
        _RadioModels.lat = Double.parseDouble(_lat)/10000.0;
        String _lon = pCursor.getString(pCursor.getColumnIndex("lon"));
        _lon = _lon.replaceAll(",","");
        _RadioModels.lon = Double.parseDouble(_lon)/10000.0;
        _RadioModels.address = pCursor.getString(pCursor.getColumnIndex("address"));
        _RadioModels.tag = pCursor.getString(pCursor.getColumnIndex("tag"));
        return _RadioModels;
    }

    @Override
    protected String[] GetTableNameAndPK() {
        return new String[]{"Illegal", "uid"};
    }

    @Override
    public void OnCreate(SQLiteDatabase p_DataBase) {
        String _sql = "create table Illegal(" +
                "[uid] integer primary key autoincrement not null," +
                "[handle] integer not null," +
                "[freq] long not null," +
                "[time] datetime not null," +
                "[lon] text not null," +
                "[lat] text not null," +
                "[address] text," +
                "[tag] text)";
        p_DataBase.execSQL(_sql);
    }

    @Override
    public void OnUpgrade(SQLiteDatabase p_DataBase) {

    }
}
