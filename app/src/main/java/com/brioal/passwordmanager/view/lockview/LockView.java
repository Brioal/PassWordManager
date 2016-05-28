package com.brioal.passwordmanager.view.lockview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * 显示密码的界面
 * Created by Brioal on 2016/4/5.
 */
public class LockView extends ViewGroup {

    private static final int MAX_VIEW = 9;
    private Paint mPaintLine; //绘制连接线的画笔
    private float mTouchRadicus; // 可触摸的区域百分比
    private int mLineColor; // 线的颜色
    private int mLineWidth = 5; // 线的宽度

    private List<MarkView> mSelects; // 已选中的view
    private StringBuilder mSb; // 存储已选中的view的编号

    protected int mColorSleect = Color.parseColor("#7ECEF4"); // 选中的颜色
    protected int mColorError = Color.parseColor("#64EC6941"); // 错误的颜色

    private boolean isCanUnlock = true; // 是否可以解锁
    private boolean isTouch = false; // 是否正在触摸


    private float x; //触摸的x坐标
    private float y;  // 触摸的y坐标
    private float areaWidth;// view的宽度
    private int lineCount; // 点的行列数量

    private int Max_Error = 5;
    private int mErrorTimer = 0;
    private onFinishListener onFinishListener;

    public void setError() {
        for (int i = 0; i < mSelects.size(); i++) {
            MarkView view = mSelects.get(i);
            view.setState(LockState.STATE_ERROR);
        }
        invalidate();
    }

    public void setCanUnlock(boolean canUnlock) {
        isCanUnlock = canUnlock;
    }

    public interface onFinishListener {
        void Success(String mPassWord);

        void Failed();

    }

    public void setOnFinishListener(LockView.onFinishListener onFinishListener) {
        this.onFinishListener = onFinishListener;
    }

    public LockView(Context context) {
        this(context, null);
    }

    public LockView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (changed) {
            areaWidth = (float) ((r - l - getPaddingLeft() * 2) / Math.sqrt(MAX_VIEW));
            mTouchRadicus = areaWidth / 2 * 2 / 4;

            for (int i = 0; i < MAX_VIEW; i++) {
                int row = i / lineCount;
                int clo = i % lineCount;
                int left = (int) (getPaddingLeft() + clo * areaWidth);
                int top = (int) (getPaddingTop() + row * areaWidth);
                int right = (int) (left + areaWidth);
                int bottom = (int) (top + areaWidth);
                getChildAt(i).layout(left, top, right, bottom);
            }
        }
    }

    private void init() {
        mPaintLine = new Paint();
        mPaintLine.setAntiAlias(true);
        mPaintLine.setDither(true);
        mPaintLine.setColor(mColorSleect);
        mPaintLine.setStyle(Paint.Style.FILL);
        mPaintLine.setStrokeWidth(mLineWidth);
        mPaintLine.setStrokeCap(Paint.Cap.ROUND);
        mPaintLine.setStrokeJoin(Paint.Join.ROUND);
        addAll();
        // 清除FLAG，否则 onDraw() 不会调用，原因是 ViewGroup 默认透明背景不需要调用 onDraw()
        setWillNotDraw(false);
        lineCount = (int) Math.sqrt(MAX_VIEW);
        mSb = new StringBuilder();
        mSelects = new ArrayList<>();
    }

    public void addAll() {
        //添加所有点
        for (int i = 0; i < MAX_VIEW; i++) {
            MarkView view = new MarkView(getContext());
            view.setNum(i + 1);
            LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
            view.setLayoutParams(params);
            addView(view);
        }
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = Math.min(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.getSize(heightMeasureSpec));
        setMeasuredDimension(width, width);
        for (int i = 0; i < getChildCount(); i++) {
            measureChild(getChildAt(i), widthMeasureSpec, heightMeasureSpec);
        }
    }

    //恢复之前的状态
    public void resetDefault() {
        mPaintLine.setColor(mColorSleect);
        for (int i = 0; i < mSelects.size(); i++) {
            MarkView view = mSelects.get(i);
            view.setState(LockState.STATE_NORMAL);
        }
        mSelects.clear();
        invalidate();
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isCanUnlock) {
            invalidate();
            return false;
        }

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                resetDefault();
                x = event.getX();
                y = event.getY();
                isTouch = true;
                break;
            case MotionEvent.ACTION_MOVE:
                x = event.getX();
                y = event.getY();
                MarkView markView = (MarkView) getChildAt(getView(x, y));
                if (markView != null && !markView.isHighLighted()) {
                    markView.setState(LockState.STATE_SELECT);
                    if (!mSelects.contains(markView)) {
                        mSelects.add(markView);
                    }
                    mSb.setLength(0);
                    for (MarkView view : mSelects) {
                        mSb.append(view.getNum());
                    }
                }
                if (mSelects.size() > 0) {
                    invalidate();
                }
                break;
            case MotionEvent.ACTION_UP:
                String currentPass = mSb.toString();
                System.out.println(mSb.toString());

                if (currentPass.length() < 4) {
                    onFinishListener.Failed();
                } else {
                    onFinishListener.Success(currentPass);
                }

                isTouch = false;
                invalidate();
                break;
        }
        return true;
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        drawLine(canvas);
    }

    public void drawLine(Canvas canvas) {
        if (isCanUnlock) { // 如果可以绘制
            onDrawNodeViewLock(canvas); // 则连接所有已连接的点
            if (isTouch) { // 如果正在绘制,则另外绘制最后一点到手指的位置
                if (mSelects.size() > 0) {
                    MarkView view = mSelects.get(mSelects.size() - 1);
                    canvas.drawLine(view.getCenterX(), view.getCenterY(), x, y, mPaintLine);
                }
            }
        }
    }

    //按顺序绘制,连接已连接的所有点
    private void onDrawNodeViewLock(Canvas canvas) {
        //从第一个和最后一个的连接线
        for (int i = 1; i < mSelects.size(); i++) {
            MarkView frontNode = mSelects.get(i - 1);
            MarkView backNode = mSelects.get(i);
            canvas.drawLine(frontNode.getCenterX(), frontNode.getCenterY(), backNode.getCenterX(), backNode.getCenterY(), mPaintLine);
        }
    }

    //返回当前触摸点的子view
    public int getView(float x, float y) {
        int row = (int) ((x - getPaddingLeft()) / areaWidth);
        int clo = (int) ((y - getPaddingTop()) / areaWidth);
        MarkView markView = (MarkView) getChildAt(clo * lineCount + row);
        if (x - markView.getCenterX() <= mTouchRadicus && y - markView.getCenterY() <= mTouchRadicus) {
            return clo * lineCount + row;
        }
        return -1;
    }


}
