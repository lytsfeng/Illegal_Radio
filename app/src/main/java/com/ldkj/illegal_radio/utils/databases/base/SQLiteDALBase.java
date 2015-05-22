package com.ldkj.illegal_radio.utils.databases.base;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by john on 15-5-20.
 */
public abstract class SQLiteDALBase<T> implements SQLiteHelper.SQLiteDataTable{

    private Context m_Context;
    private SQLiteDatabase m_DataBase;

    public SQLiteDALBase(Context p_Context)
    {
        m_Context = p_Context;
    }

    protected Context GetContext()
    {
        return m_Context;
    }

    public SQLiteDatabase GetDataBase()
    {
        if(m_DataBase == null)
        {
            m_DataBase = SQLiteHelper.GetInstance(m_Context).getWritableDatabase();
        }

        return m_DataBase;
    }



    protected List<T> CursorToList(Cursor p_Cursor)
    {
        List<T> _List = new ArrayList<T>();
        while(p_Cursor.moveToNext())
        {
            T _Object = FindModel(p_Cursor);
            _List.add(_Object);
        }
        p_Cursor.close();
        return _List;
    }

    public Cursor ExecSql(String p_SqlText)
    {
        return GetDataBase().rawQuery(p_SqlText, null);
    }

    public List<T> queryAll(String pCondition){
        String[] _NameAndPk = GetTableNameAndPK();
        String _SqlText = MessageFormat.format("select * from {0} where 1=1 {1}",_NameAndPk[0],pCondition);
        return CursorToList(ExecSql(_SqlText));
    }
    public Boolean Delete(String pCondition)
    {
        return GetDataBase().delete(GetTableNameAndPK()[0], " 1=1 " + pCondition, null) >= 0;
    }

    public abstract T install(T t);

    protected abstract T FindModel(Cursor pCursor);
    protected abstract String[] GetTableNameAndPK();


}
