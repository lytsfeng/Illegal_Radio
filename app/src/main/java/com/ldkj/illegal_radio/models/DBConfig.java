package com.ldkj.illegal_radio.models;

import android.content.Context;
import android.content.SharedPreferences;

import com.ldkj.illegal_radio.OwnApplication;

import org.xmlpull.v1.XmlPullParser;

/**
 * Created by john on 15-4-23.
 */
public class DBConfig {

    private static final String DBCONFIG_FILE_NAME="com_ldkj_illegal_radio_dbconfig";
    private static final String DBCONFIG_KEY_ADDRESS = "address";
    private static final String DBCONFIG_KEY_PORT = "port";
    private static final String DBCONFIG_KEY_ISUPDATE = "isupdate";
    private static DBConfig dbConfig = new DBConfig();

    public String address = "127.0.0.1";
    public int port = 65001;
    public boolean isUpdate = false;


    public DBConfig() {


    }
    public DBConfig(String address, int port) {
        this.address = address;
        this.port = port;
    }

    public static DBConfig getDBConfig()
    {
        SharedPreferences _Preferences = OwnApplication.getContext().getSharedPreferences(DBCONFIG_FILE_NAME, Context.MODE_APPEND);
        dbConfig.address = _Preferences.getString(DBCONFIG_KEY_ADDRESS,dbConfig.address);
        dbConfig.port = _Preferences.getInt(DBCONFIG_KEY_PORT, dbConfig.port);
        dbConfig.isUpdate = _Preferences.getBoolean(DBCONFIG_KEY_ISUPDATE, dbConfig.isUpdate);
        return dbConfig;
    }
    public static void saveDBConfig(DBConfig config){
        SharedPreferences _Preferences = OwnApplication.getContext().getSharedPreferences(DBCONFIG_FILE_NAME, Context.MODE_APPEND);
        SharedPreferences.Editor _Editor = _Preferences.edit();
        _Editor.putString(DBCONFIG_KEY_ADDRESS, config.address);
        _Editor.putInt(DBCONFIG_KEY_PORT, config.port);
        _Editor.putBoolean(DBCONFIG_KEY_ISUPDATE,config.isUpdate);
        _Editor.commit();
        dbConfig = config;
    }

    /**
     * 用于本地数据库有数据添加是调用，更新isupdate为true
     */
    public static void updateDB(){
       if(dbConfig.isUpdate){
           return;
       }else {
           dbConfig = getDBConfig();
           dbConfig.isUpdate = true;
           saveDBConfig(dbConfig);
       }
    }


}
