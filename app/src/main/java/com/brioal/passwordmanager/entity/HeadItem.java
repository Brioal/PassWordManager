package com.brioal.passwordmanager.entity;

/**头像实体类
 * Created by brioal on 16-4-28.
 */
public class HeadItem {
    private int mHead ;
    private int mId ;

    public HeadItem(int mHead, int mId) {
        this.mHead = mHead;
        this.mId = mId;
    }

    public int getmHead() {
        return mHead;
    }

    public void setmHead(int mHead) {
        this.mHead = mHead;
    }

    public int getmId() {
        return mId;
    }

    public void setmId(int mId) {
        this.mId = mId;
    }
}
