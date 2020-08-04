package com.l900.master.tools;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME="EmergencyContact_db";
    private static final String TABLE_NAME="Emergency_Contact";
    private static final int DATABASE_VERSION = 2 ;
    private static DatabaseHelper instance;

    private SQLiteDatabase db;
    private DatabaseHelper(Context context){
        super(context,DB_NAME,null,DATABASE_VERSION);
        Log.e("1900",TABLE_NAME+"DatabaseHelper");
    }

    public static DatabaseHelper getInstance(Context context){
        Log.e("1900",TABLE_NAME+"getInstance");
        if(instance==null)
            instance=new DatabaseHelper(context);
        return instance;
    }


    public void insert(ContentValues values)
    {
        Log.e("1900",TABLE_NAME+"insert");
        if(db==null)
            db=getWritableDatabase();
        db.insert(TABLE_NAME,null,values);
    }

    public void del(int id)
    {
        if(db==null)
            db=getWritableDatabase();
        db.delete(TABLE_NAME,"_id = ?",new String[]{String.valueOf(id)});
    }
    public void del(String name,String tel)
    {Log.e("1900",TABLE_NAME+"del");
        if(db==null)
            db=getWritableDatabase();
        db.execSQL("delete from "+TABLE_NAME+" where name='"+name+"' and tel='"+tel+"'");
    }


    public Cursor query()
    {
        Log.e("1900",TABLE_NAME+"query"+(db==null));
        if(db==null)
            db=getReadableDatabase();
        Cursor cursor=db.query(TABLE_NAME,null,null,null,null,null,null);
        return cursor;
    }

    public void update(ContentValues values,String tp)
    { Log.e("1900",TABLE_NAME+"update");
        if(db==null)
            db=getWritableDatabase();
        db.update(TABLE_NAME,values,"name=?",new String[]{tp});
    }

    public void close()
    {
        Log.e("1900",TABLE_NAME+" close ");
        if(db!=null)
        {
            db.close();
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        this.db=db;
        db.execSQL("create table Emergency_Contact (id integer primary key autoincrement,name varchar(20),tel varchar(20))");
        Log.e("1900",TABLE_NAME+" onCreate ");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.e("1900",TABLE_NAME+" onUpgrade ");
    }
}
