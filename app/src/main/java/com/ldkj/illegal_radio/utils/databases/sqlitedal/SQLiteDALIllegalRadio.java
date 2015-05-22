package com.ldkj.illegal_radio.utils.databases.sqlitedal;

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
                        "([uid],[handle],[freq],[time],[lon],[lat],[address],[tal]) " +
                        "values {0},{1},'{2}','{3}',{4},{5},'{6}','{7}';",
                radioModel.uid, radioModel.handle, radioModel.freq,
                radioModel.appeartime, radioModel.lon, radioModel.lat,
                radioModel.address, radioModel.tag);
        GetDataBase().execSQL(_sql);
        return radioModel;
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
        _RadioModels.appeartime = pCursor.getString(pCursor.getColumnIndex("appeartime"));
        _RadioModels.lat = pCursor.getDouble(pCursor.getColumnIndex("lat"));
        _RadioModels.lon = pCursor.getDouble(pCursor.getColumnIndex("lon"));
        _RadioModels.address = pCursor.getString(pCursor.getColumnIndex("address"));
        _RadioModels.tag = pCursor.getString(pCursor.getColumnIndex("tag"));
        return _RadioModels;
    }

    @Override
    protected String[] GetTableNameAndPK() {
        return new String[]{"IllegalTable", "_id"};
    }

    @Override
    public void OnCreate(SQLiteDatabase p_DataBase) {
        String _sql = "create table Illegal(" +
                "[uid] int primary key   autoincrement not null," +
                "[handle] integer not null," +
                "[freq] long not null," +
                "[time] datetime not null," +
                "[lon] real not null," +
                "[lat] real not null," +
                "[address] text," +
                "[tag] text)";
        p_DataBase.execSQL(_sql);
    }

    @Override
    public void OnUpgrade(SQLiteDatabase p_DataBase) {

    }
}
