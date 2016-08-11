package com.brioal.passwordmanager.util;

import android.content.Context;


/**
 * Created by brioal on 16-4-21.
 */
public class Constans {
    public static final String SHAREPREFEREMCE_KEY = "EasyPassWord"; // sharepreference的名称
    public static final String SHAREPREFEREMCE_KEY_ISFIRST = "IsFirst"; // 存储是否是第一次进入
    public static final String SHAREPREFERENCE_KEY_PASSWORD = "PassWord"; // 存储密码的key
    public static final int LAUNCHER_MAX_ERROR_TIMES = 5; // 解锁失败多少次后锁定


    private static DataLoader mDataLoader ;

    public static DataLoader getDataLoader(Context context) {
        if (mDataLoader == null) {
            mDataLoader = new DataLoader(context);
        }
        return mDataLoader;
    }




}
