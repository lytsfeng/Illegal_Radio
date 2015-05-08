package com.ldkj.illegal_radio.utils.databases.sqlitedal;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.ldkj.illegal_radio.models.StationModel;
import com.ldkj.illegal_radio.utils.DateTools;
import com.ldkj.illegal_radio.utils.databases.base.SQLiteDALBase;

import java.util.List;


/**
 * Created by john on 15-3-20.
 */
public class SQLiteDALStation extends SQLiteDALBase {

    public SQLiteDALStation(Context p_Context) {
        super(p_Context);
    }

    @Override
    protected String[] GetTableNameAndPK() {
        return new String[0];
    }
    @Override
    protected Object FindModel(Cursor p_Cursor) {
        return null;
    }


    public boolean find(String freq,String currentDate){

        String _sql = String.format("select * from stations s where" +
                "%s > t.freq - t.span and %s < t.freq + t.span", freq, freq);
        List<StationModel> list = CursorToList(ExecSql(_sql));
        if(list.size() == 0){
            return false;
        }
        for (StationModel m : list) {
//            if(currentDate >= m.getBegindate() && currentDate <= m.getEnddate()){
            if(true){
                return true;
            }
        }
        return false;
    }

    @Override
    public void OnCreate(SQLiteDatabase p_DataBase) {
        StringBuffer _SB = new StringBuffer();
        _SB.append("create table stations(");
        _SB.append("[id] double  PRIMARY KEY not null");
        _SB.append("[name] varchar(64) not null");
        _SB.append("[freq] double not null");
        _SB.append("[span] integer not null");
        _SB.append("[lon] double not null");
        _SB.append("[lat] double not null");
        _SB.append("[address] text");
        _SB.append("[begindate] datetime not null");
        _SB.append("[enddate] datetime not null");
        _SB.append("[tag] text");
        p_DataBase.execSQL(_SB.toString());
        _SB.reverse();

    }

    @Override
    public void OnUpgrade(SQLiteDatabase p_DataBase) {

    }


    public ContentValues CreateParms(StationModel pMode){
        ContentValues _contentValues = new ContentValues();
        _contentValues.put("id",pMode.getId());
        _contentValues.put("name",pMode.getName());
        _contentValues.put("freq",pMode.getFreq());
        _contentValues.put("span",pMode.getSpan());
        _contentValues.put("lon", pMode.getLon());
        _contentValues.put("lat",pMode.getLat());
        _contentValues.put("address",pMode.getAddress());
        _contentValues.put("power",pMode.getPower());
        _contentValues.put("begindate", DateTools.getFormatDateTime(pMode.getBegindate(), "yyyy-MM-dd HH:mm:ss"));
        _contentValues.put("enddate", DateTools.getFormatDateTime(pMode.getEnddate(),"yyyy-MM-dd HH:mm:ss"));
        _contentValues.put("tag",pMode.getTag());
        return _contentValues;
    }
}
