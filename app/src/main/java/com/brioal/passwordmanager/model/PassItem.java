package com.brioal.passwordmanager.model;

import java.io.Serializable;

import cn.bmob.v3.BmobObject;

/**
 * Created by brioal on 16-4-21.
 */
public class PassItem extends BmobObject implements Serializable {
    private String mId; //唯一的表示符
    private String mTitle; //标题
    private String mDesc; //描述
    private int mHead; //头像编号
    private String mClassify; // 分类
    private long mTime; //时间
    private String mAccount; //账号
    private String mPass; //密码
    private int isEncode; //是否已加密

    public PassItem() {
    }

    public PassItem(String mId, String mTitle, String mDesc, int mHead, String mClassify, long mTime, String mAccount, String mPass, int isEncode) {
        this.mId = mId;
        this.mTitle = mTitle;
        this.mDesc = mDesc;
        this.mClassify = mClassify;
        this.mTime = mTime;
        this.mHead = mHead;
        this.mAccount = mAccount;
        this.mPass = mPass;
        this.isEncode = isEncode;
    }


    public String getmId() {
        return mId;
    }

    public void setmId(String mId) {
        this.mId = mId;
    }

    public void setmAccount(String mAccount) {
        this.mAccount = mAccount;
    }

    public String getmAccount() {
        return mAccount;
    }

    public String getmTitle() {
        return mTitle;
    }

    public void setmTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public String getmDesc() {
        return mDesc;
    }

    public void setmDesc(String mDesc) {
        this.mDesc = mDesc;
    }

    public int getmHead() {
        return mHead;
    }

    public void setmHead(int mHead) {
        this.mHead = mHead;
    }

    public String getmClassify() {
        return mClassify;
    }

    public void setmClassify(String mClassify) {
        this.mClassify = mClassify;
    }

    public long getmTime() {
        return mTime;
    }

    public void setmTime(long mTime) {
        this.mTime = mTime;
    }

    public String getmPass() {
        return mPass;
    }

    public void setmPass(String mPass) {
        this.mPass = mPass;
    }

    public int getIsEncode() {
        return isEncode;
    }

    public void setIsEncode(int isEncode) {
        this.isEncode = isEncode;
    }

    @Override
    public boolean equals(Object o) {
        PassItem other = (PassItem) o;

        if (!this.mTitle.equals(other.getmTitle())) {
            return false;
        } else if (!this.mDesc.equals(other.getmDesc())) {
            return false;
        } else if (!this.mAccount.equals(other.getmAccount())) {
            return false;
        } else if (!this.mPass.equals(other.getmPass())) {
            return false;
        } else if (!this.mClassify.equals(other.getmClassify())) {
            return false;
        } else if (this.mTime != other.getmTime()) {
            return false;
        } else if (this.mHead != other.getmHead()) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "标题:"+getmTitle()+"\n账号:"+getmAccount()+"\n密码:"+getmPass()+"\n备注:"+getmDesc();
    }
}
