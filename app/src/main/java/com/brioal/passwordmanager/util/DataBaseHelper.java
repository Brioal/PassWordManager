package com.brioal.passwordmanager.util;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * private String mTitle; //标题
 * private String mDesc; //备注
 * private int mHead; // 图标
 * private String mClassify; // 分类
 * private String mTime; //时间
 * private String mPassWord; //密码
 * private boolean mIsEncoded ; //是否加密
 * Created by Brioal on 2016/4/10.
 */
public class DataBaseHelper extends SQLiteOpenHelper {
    private String TABLE_PASS = "create table PassItems (mId integer primary key autoincrement ,mObject, mTitle , mDesc , mHead integer , mClassify , mTime,mAccount ,mPassWord,mIsEncodes integer)"; //创建密码表
    private String TABLE_PASS_CLASSIFY = "create table Classifies(mId integer primary key autoincrement , mName  )"; //创建密码分类表 分类的id , 分类的名称 ,当前分类的item数量 , 分类的类别


    public DataBaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {

        super(context, name, factory, version);
    }

    public DataBaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version, DatabaseErrorHandler errorHandler) {
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
