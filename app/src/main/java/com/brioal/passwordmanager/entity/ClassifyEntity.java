package com.brioal.passwordmanager.entity;

import java.io.Serializable;

/**分类实体类
 * Created by brioal on 16-4-22.
 */
public class ClassifyEntity implements Serializable {
    private String mText;
    private int mNum;

    public ClassifyEntity( int mNum,String mText) {
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
        ClassifyEntity other = (ClassifyEntity) o;
        return this.mText.equals(other.getmText());
    }
}
