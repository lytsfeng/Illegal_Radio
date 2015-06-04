package com.ldkj.illegal_radio.utils.databases.sqlitedal;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.ldkj.illegal_radio.models.RadioModels;
import com.ldkj.illegal_radio.utils.databases.base.SQLiteDALBase;

import java.text.MessageFormat;

/**
 * Created by john on 15-5-28.
 */
public class SQLiteDALRadio extends SQLiteDALBase<RadioModels> {
    public SQLiteDALRadio(Context p_Context) {
        super(p_Context);
    }
    @Override
    public RadioModels install(RadioModels radioModel) {
        String _sql = MessageFormat.format("insert into radio " +
                        "([name],[freq],[span],[lon],[lat],[address],[beginTime],[endTime],[power],[tag]) " +
                        "values (''{0}'',''{1}'',''{2}'',''{3}'',''{4}'',''{5}'',''{6}'',''{7}'',{8},''{9}'');",
                radioModel.name, radioModel.freq,
                radioModel.span, radioModel.lon * 10000, radioModel.lat * 10000,
                radioModel.address, radioModel.beginTime,radioModel.engTime,radioModel.power,radioModel.tag);
        try{
            GetDataBase().execSQL(_sql);
        }catch (Exception r){
            r.printStackTrace();
        }
        radioModel.id =getMaxId();
        return radioModel;
    }

    @Override
    protected RadioModels FindModel(Cursor pCursor) {
        RadioModels _Models = new RadioModels();
        _Models.id = pCursor.getInt(pCursor.getColumnIndex("id"));
        _Models.name = pCursor.getString(pCursor.getColumnIndex("name"));
        _Models.freq = pCursor.getString(pCursor.getColumnIndex("freq"));
        _Models.span = pCursor.getString(pCursor.getColumnIndex("span"));
        String _lat = pCursor.getString(pCursor.getColumnIndex("lat"));
        _lat = _lat.replaceAll(",","");
        _Models.lat = Double.parseDouble(_lat)/10000.0;
        String _lon = pCursor.getString(pCursor.getColumnIndex("lon"));
        _lon = _lon.replaceAll(",","");
        _Models.lon = Double.parseDouble(_lon)/10000.0;
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
                "[freq] text not null," +
                "[span] text not null," +
                "[lon] text not null," +
                "[lat] text not null," +
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
