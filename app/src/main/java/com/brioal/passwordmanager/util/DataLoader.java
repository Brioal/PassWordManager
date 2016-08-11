package com.brioal.passwordmanager.util;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.brioal.brioallib.klog.KLog;
import com.brioal.brioallib.util.DESUtil;
import com.brioal.passwordmanager.R;
import com.brioal.passwordmanager.entity.ClassifyEntity;
import com.brioal.passwordmanager.entity.PassEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Brioal on 2016/6/19.
 */

public class DataLoader {
    private Context mContext;
    private DBHelper mDBHelper;
    private SQLiteDatabase mDatabase ;

    public DataLoader(Context context) {
        mContext = context;
        mDBHelper = new DBHelper(context, "Pass.db3", null, 1);
        mDatabase = mDBHelper.getReadableDatabase();
    }

    //获取本地存储的分类信息
    public ArrayList<ClassifyEntity> getClassify() {
        ArrayList<ClassifyEntity> list = new ArrayList<>();
        Cursor cursor = null;
        try {
            cursor = mDatabase.rawQuery("select * from Classify", null);
            while (cursor.moveToNext()) {
                ClassifyEntity entity = new ClassifyEntity(cursor.getInt(0), DESUtil.decryptDES(cursor.getString(1)));
                list.add(entity);
            }
            KLog.i("读取本地分类信息成功:"+list.size());
        } catch (Exception execution) {
            KLog.i("读取本地分类信息失败" + execution.toString());
            execution.printStackTrace();
        }finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return list;
    }

    //保存分类数据到本地
    public void saveClassifyLocal(List<ClassifyEntity> entities) {
        try {
            for (int i = 0; i < entities.size(); i++) {
                mDatabase.execSQL("insert into Classify values ( ? , ? ) ",new Object[]{
                        entities.get(i).getmNum(),
                        DESUtil.encryptDES(entities.get(i).getmText())
                });

            }

            KLog.i("分类数据保存到本地成功");
        } catch (Exception e) {
            KLog.i("分类数据保存到本地数百" + e.toString());
            e.printStackTrace();
        }
    }

    //读取密码信息
    public List<PassEntity> getPassWords(String classify) {
        List<PassEntity> passEntities = new ArrayList<>();
        Cursor cursor = null;
        try {
            if (classify == null) {
                cursor = mDatabase.rawQuery("select * from PassItems ", null); //读取所有密码
            } else {
                cursor = mDatabase.rawQuery("select * from PassItems where mClassify = '" + classify + "'", null); //读取指定分类的密码
            }
            while (cursor.moveToNext()) {
                PassEntity entity = new PassEntity(DESUtil.decryptDES(cursor.getString(1)), DESUtil.decryptDES(cursor.getString(2)), cursor.getInt(3), DESUtil.decryptDES(cursor.getString(4)), DESUtil.decryptDES(cursor.getString(5)), DESUtil.decryptDES(cursor.getString(6)), cursor.getLong(7));
                passEntities.add(entity); //密码实体类添加到列表
            }
            KLog.i("读取本地密码数据成功"+passEntities.size());
        } catch (Exception e) {
            KLog.i("读取本地密码数据失败"+e.toString());
        }finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return passEntities;
    }

    //保存密码信息到本地
    public void savePassWordLocal(List<PassEntity> list) {
        try {
            for (int i = 0; i < list.size(); i++) {
                PassEntity entity = list.get(i);
                mDatabase.execSQL("insert into PassItems values ( null , ? , ? , ? , ? , ? , ? , ? )", new Object[]{

                        DESUtil.encryptDES(entity.getTitle()), //标题加密
                        DESUtil.encryptDES(entity.getDesc()), // 描述加密
                        entity.getHead(),
                        DESUtil.encryptDES(entity.getClassify()),//分类加密
                        DESUtil.encryptDES(entity.getAccount()),//账号加密
                        DESUtil.encryptDES(entity.getPass()), //密码加密
                        entity.getTime()
                });
            }
            KLog.i("密码保存到本地成功");
        } catch (Exception e) {
            KLog.i("密码保存到本地失败" + e.toString());
            e.printStackTrace();
        }

    }



    //获取头像数组
    public int[] getHeads() {
        return mImages;
    }


    public  int[] mImages = new int[]{
            R.mipmap.ic_app_00,
            R.mipmap.ic_app_01,
            R.mipmap.ic_app_02,
            R.mipmap.ic_app_03,
            R.mipmap.ic_app_04,
            R.mipmap.ic_app_05,
            R.mipmap.ic_app_06,
            R.mipmap.ic_app_07,
            R.mipmap.ic_app_08,
            R.mipmap.ic_app_09,
            R.mipmap.ic_app_10,
            R.mipmap.ic_app_11,
            R.mipmap.ic_app_12,
            R.mipmap.ic_app_13,
            R.mipmap.ic_app_14,
            R.mipmap.ic_app_15,
            R.mipmap.ic_app_16,
            R.mipmap.ic_app_17,
            R.mipmap.ic_app_18,
            R.mipmap.ic_app_19,
            R.mipmap.ic_app_20,
            R.mipmap.ic_app_21,
            R.mipmap.ic_app_22,
            R.mipmap.ic_app_23,
            R.mipmap.ic_app_24,
            R.mipmap.ic_app_25,
            R.mipmap.ic_app_26,
            R.mipmap.ic_app_27,
            R.mipmap.ic_app_28,
            R.mipmap.ic_app_29,
            R.mipmap.ic_app_30,
            R.mipmap.ic_app_31,
            R.mipmap.ic_app_32,
            R.mipmap.ic_app_33,
            R.mipmap.ic_app_34,
            R.mipmap.ic_app_35,
            R.mipmap.ic_app_36,
            R.mipmap.ic_app_37,
            R.mipmap.ic_app_38,
            R.mipmap.ic_app_39,
            R.mipmap.ic_app_40,
            R.mipmap.ic_app_41,
            R.mipmap.ic_app_42,
            R.mipmap.ic_app_43,
            R.mipmap.ic_app_44,
            R.mipmap.ic_app_45,
            R.mipmap.ic_app_46,
            R.mipmap.ic_app_47,
            R.mipmap.ic_app_48,
            R.mipmap.ic_app_49,
            R.mipmap.ic_app_50,
            R.mipmap.ic_app_51,
            R.mipmap.ic_app_52,
            R.mipmap.ic_app_53,
            R.mipmap.ic_app_54,
            R.mipmap.ic_app_55,
            R.mipmap.ic_app_56,
            R.mipmap.ic_app_57,
            R.mipmap.ic_app_58,
            R.mipmap.ic_app_59,
            R.mipmap.ic_app_60,
            R.mipmap.ic_app_61,
            R.mipmap.ic_app_62,
            R.mipmap.ic_app_63,
            R.mipmap.ic_app_64,
            R.mipmap.ic_app_65,
            R.mipmap.ic_app_66,
            R.mipmap.ic_app_67,
            R.mipmap.ic_app_68,
            R.mipmap.ic_app_69,
            R.mipmap.ic_app_70,
            R.mipmap.ic_app_71,
            R.mipmap.ic_app_72,
            R.mipmap.ic_app_73,
            R.mipmap.ic_app_74,
            R.mipmap.ic_app_75,
            R.mipmap.ic_app_76,
            R.mipmap.ic_app_77,
            R.mipmap.ic_app_78,

    };
}
