package com.brioal.passwordmanager.util;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * 数据库辅助类
 * Created by Brioal on 2016/4/10.
 */
public class DBHelper extends SQLiteOpenHelper {
    private String TABLE_PASS = "create table PassItems (mId integer primary key autoincrement , mTitle , mDesc , mHead integer , mClassify , mAccount ,mPass , mTime long)"; //创建密码表
    private String TABLE_PASS_CLASSIFY = "create table Classify (mId integer primary key autoincrement , mName )"; //分类数据表


    public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {

        super(context, name, factory, version);
    }

    public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version, DatabaseErrorHandler errorHandler) {
        super(context, name, factory, version, errorHandler);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_PASS);
        db.execSQL(TABLE_PASS_CLASSIFY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
