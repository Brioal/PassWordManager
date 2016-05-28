package com.brioal.passwordmanager.view.lockview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

/**
 * 圆圈view
 * Created by Brioal on 2016/4/5.
 */
public class MarkView extends View {
    private int mRadius; // 外部圆的半径
    private int mWidth; // 组件的宽度
    private Paint mPaintStrike; // 绘制外部圆圈的画笔
    private Paint mPaintCircle; // 绘制内部圆的画笔
    private Paint mPaintInSide; // 绘制内部区域的画笔
    private int mStrikeWidth; //外部圆的线条宽度
    private LockState mCurrentState = LockState.STATE_NORMAL; // 默认状态
    //默认的颜色
    private int mColorNoramlStrike = Color.parseColor("#757575"); // 外部圆颜色
    private int mColorNoramlInSIde = Color.parseColor("#64757575");//中间区域填充颜色
    private int mColorNormalCircle = Color.parseColor("#757575"); // 中间小圆的颜色

    //选中时的颜色
    private int mColorSelectStrike = Color.parseColor("#7ECEF4"); // 外部圆颜色
    private int mColorSelectInSIde = Color.parseColor("#647ECEF4");//中间区域填充颜色
    private int mColorSelectCircle = Color.parseColor("#7ECEF4"); // 中间小圆的颜色


    //错误时候的颜色
    private int mColorErroeStrike = Color.parseColor("#EC6941"); // 外部圆颜色
    private int mColorErrorInSIde = Color.parseColor("#64EC6941");//中间区域填充颜色
    private int mColorErrorCircle = Color.parseColor("#EC6941"); // 中间小圆的颜色
    private int mPadding = 80; // 外部圆圈离组件边界的边距
    private int mInsodePadding; // 内圆离外圆的边距
    private int mTouchRatio;// 触摸有效的范围
    private int mNum; // 当前位置的编号

    public MarkView(Context context) {
        this(context,null);

    }

    public MarkView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
        initPaints();
    }

    private void init() {
        setPadding(mPadding, mPadding, mPadding, mPadding); // 设置padding
    }

    //初始化画笔
    public void initPaints() {
        // 外部圆画笔
        mPaintStrike = new Paint();
        mPaintStrike.setAntiAlias(true); // 设置抗锯齿
        mPaintStrike.setDither(true); // 设置图像防抖动处理
        mPaintStrike.setStrokeWidth(mStrikeWidth); // 设置线条宽度
        mPaintStrike.setColor(mColorNoramlStrike); // 设置默认颜色
        mPaintStrike.setStyle(Paint.Style.STROKE); // 设置风格为边框
        //内部填充画笔
        mPaintInSide = new Paint();
        mPaintInSide.setColor(mColorNoramlInSIde); // 设置颜色
        mPaintInSide.setAntiAlias(true); // 设置邝钜炽
        mPaintInSide.setDither(true);//设置图像防抖动处理
        mPaintInSide.setStyle(Paint.Style.FILL); //设置风格为填充
        //内部圆画笔
        mPaintCircle = new Paint();
        mPaintCircle.setColor(mColorNormalCircle); // 设置颜色
        mPaintCircle.setAntiAlias(true); // 设置抗锯齿
        mPaintCircle.setDither(true); // 设置图像防抖动处理
        mPaintCircle.setStyle(Paint.Style.FILL); // 设置风格为填充
    }

    //取当前透明度的百分比
    public int getFullAlpha(int color, float ratio) {
        return Color.argb((int) (Color.alpha(color) * ratio), Color.red(color), Color.green(color), Color.blue(color));
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = getWidth(); // 组件的宽度
        mRadius = (mWidth - mPadding - mStrikeWidth) / 2; // 当前外部圆的半径为组件宽度减去边距再减去边界线宽度,然后的一半
        mInsodePadding = mRadius * 3 / 4; //内部圆只是外部圆半径的1/4
    }


    //设置状态，并且重绘
    public void setState(LockState CurrentState) {
        mCurrentState = CurrentState;
        invalidate();
    }

    //是否选中 ,选中或者错误的时候高亮
    public boolean isHighLighted() {
        if (mCurrentState == LockState.STATE_SELECT || mCurrentState == LockState.STATE_ERROR) {
            return true;
        }
        return false;
    }

    //中心点X
    public int getCenterX() {
        return (getLeft() + getRight()) / 2;
    }
    //中心点Y
    public int getCenterY() {
        return (getTop() + getBottom()) / 2;
    }
    //设置圈圈在手势锁当中的位置
    protected void setNum(int num) {
        mNum = num;
    }
    //返回当前view的编号
    protected int getNum() {
        return mNum;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawCircle(canvas);
    }

    //绘制图像
    private void drawCircle(Canvas canvas) {
        switch (mCurrentState) {
            case STATE_NORMAL: // 普通状态
                mPaintStrike.setColor(mColorNoramlStrike); // 设置颜色
                mPaintInSide.setColor(mColorNoramlInSIde);
                mPaintCircle.setColor(mColorNormalCircle);

                //绘制外圆
                canvas.drawCircle(mWidth / 2, mWidth / 2, mRadius, mPaintStrike);
                //绘制填充区域
                canvas.drawCircle(mWidth / 2, mWidth / 2, mRadius - mStrikeWidth, mPaintInSide);
                //绘制内部圆
                canvas.drawCircle(mWidth / 2, mWidth / 2, mRadius - mInsodePadding, mPaintCircle);
                break;
            case STATE_SELECT: // 普通状态
                mPaintStrike.setColor(mColorSelectStrike); // 设置颜色
                mPaintInSide.setColor(mColorSelectInSIde);
                mPaintCircle.setColor(mColorSelectCircle);

                //绘制外圆
                canvas.drawCircle(mWidth / 2, mWidth / 2, mRadius, mPaintStrike);
                //绘制填充区域
                canvas.drawCircle(mWidth / 2, mWidth / 2, mRadius - mStrikeWidth, mPaintInSide);
                //绘制内部圆
                canvas.drawCircle(mWidth / 2, mWidth / 2, mRadius - mInsodePadding, mPaintCircle);
                break;

            case STATE_ERROR: // 普通状态
                mPaintStrike.setColor(mColorErroeStrike); // 设置颜色
                mPaintInSide.setColor(mColorErrorInSIde);
                mPaintCircle.setColor(mColorErrorCircle);

                //绘制外圆
                canvas.drawCircle(mWidth / 2, mWidth / 2, mRadius, mPaintStrike);
                //绘制填充区域
                canvas.drawCircle(mWidth / 2, mWidth / 2, mRadius - mStrikeWidth, mPaintInSide);
                //绘制内部圆
                canvas.drawCircle(mWidth / 2, mWidth / 2, mRadius - mInsodePadding, mPaintCircle);
                break;
        }
    }
}
