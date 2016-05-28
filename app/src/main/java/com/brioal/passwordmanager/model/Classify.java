package com.brioal.passwordmanager.model;

import java.io.Serializable;

import cn.bmob.v3.BmobObject;

/**
 * Created by brioal on 16-4-22.
 */
public class Classify extends BmobObject implements Serializable{
    private String mText;
    private int mNum;

    public Classify(String mText,int mNum)
    {
        this.mText = mText;
        this.mNum = mNum;
    }


    public int getmNum() {
        return mNum;
    }

    public void setmNum(int mNum) {
        this.mNum = mNum;
    }

    public String getmText() {
        return mText;
    }

    public void setmText(String mText) {

        this.mText = mText;
    }

    @Override
    public boolean equals(Object o) {
        Classify other = (Classify) o;
        return this.mText.equals(other.getmText());
    }
}
