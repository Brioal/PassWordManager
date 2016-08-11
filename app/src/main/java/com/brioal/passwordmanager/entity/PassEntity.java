package com.brioal.passwordmanager.entity;

import java.io.Serializable;

/**
 * 密码实体类
 * Created by brioal on 16-4-21.
 */
public class PassEntity implements Serializable {
    private String mTitle; //标题
    private String mDesc; //描述
    private int mHead; //头像编号
    private String mClassify; // 分类
    private String mAccount; //账号
    private String mPass; //密码
    private long mTime; //时间

    public PassEntity() {

    }

    public PassEntity(String title, String desc, int head, String classify, String account, String pass, long time) {
        mTitle = title;
        mDesc = desc;
        mHead = head;
        mClassify = classify;
        mAccount = account;
        mPass = pass;
        mTime = time;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PassEntity passEntity = (PassEntity) o;

        if (mHead != passEntity.mHead) return false;
        if (mTime != passEntity.mTime) return false;
        if (mTitle != null ? !mTitle.equals(passEntity.mTitle) : passEntity.mTitle != null)
            return false;
        if (mDesc != null ? !mDesc.equals(passEntity.mDesc) : passEntity.mDesc != null) return false;
        if (mClassify != null ? !mClassify.equals(passEntity.mClassify) : passEntity.mClassify != null)
            return false;
        if (mAccount != null ? !mAccount.equals(passEntity.mAccount) : passEntity.mAccount != null)
            return false;
        return mPass != null ? mPass.equals(passEntity.mPass) : passEntity.mPass == null;

    }

    @Override
    public int hashCode() {
        int result = mTitle != null ? mTitle.hashCode() : 0;
        result = 31 * result + (mDesc != null ? mDesc.hashCode() : 0);
        result = 31 * result + mHead;
        result = 31 * result + (mClassify != null ? mClassify.hashCode() : 0);
        result = 31 * result + (mAccount != null ? mAccount.hashCode() : 0);
        result = 31 * result + (mPass != null ? mPass.hashCode() : 0);
        result = 31 * result + (int) (mTime ^ (mTime >>> 32));
        return result;
    }

    @Override
    public String toString() {
        return "PassEntity{" +
                "mTitle='" + mTitle + '\'' +
                ", mDesc='" + mDesc + '\'' +
                ", mHead=" + mHead +
                ", mClassify='" + mClassify + '\'' +
                ", mAccount='" + mAccount + '\'' +
                ", mPass='" + mPass + '\'' +
                ", mTime=" + mTime +
                '}';
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public String getDesc() {
        return mDesc;
    }

    public void setDesc(String desc) {
        mDesc = desc;
    }

    public int getHead() {
        return mHead;
    }

    public void setHead(int head) {
        mHead = head;
    }

    public String getClassify() {
        return mClassify;
    }

    public void setClassify(String classify) {
        mClassify = classify;
    }

    public String getAccount() {
        return mAccount;
    }

    public void setAccount(String account) {
        mAccount = account;
    }

    public String getPass() {
        return mPass;
    }

    public void setPass(String pass) {
        mPass = pass;
    }

    public long getTime() {
        return mTime;
    }

    public void setTime(long time) {
        mTime = time;
    }
}
