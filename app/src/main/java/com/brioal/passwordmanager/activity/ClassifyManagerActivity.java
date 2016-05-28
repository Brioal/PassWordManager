package com.brioal.passwordmanager.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.brioal.passwordmanager.R;
import com.brioal.passwordmanager.model.Classify;
import com.brioal.passwordmanager.util.Constan;

import java.util.ArrayList;
import java.util.Collections;

import butterknife.Bind;
import butterknife.ButterKnife;
import me.imid.swipebacklayout.lib.StatusBarUtils;

/**
 * Created by brioal on 16-4-27.
 */
public class ClassifyManagerActivity extends AppCompatActivity {

    public static final int EDIT_CLASSIFY = 23;
    private static final String TAG = "ManagerInfo";
    @Bind(R.id.toolBar)
    Toolbar mToolBar;
    @Bind(R.id.classify_recyclerView)
    RecyclerView mRecyclerView;
    @Bind(R.id.classify_fab)
    FloatingActionButton mFab;
    @Bind(R.id.classify_container)
    CoordinatorLayout mContainer;

    private ArrayList<Classify> mList;
    private ArrayList<Classify> mCopy;
    private ClassifyAdapter mAdapter;
    private Context mContext;
    private ItemTouchHelper mHellper;
    private ItemTouchHelper.Callback mCallBack;
    private onChangeListener onChangeListener;
    private onStateChangeListener onStateChangeListener;
    private int size = 0;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            initView();
        }
    };

    private interface onChangeListener {
        boolean onItemMove(int from, int to);

        void onItemDismiss(int position);
    }

    private interface onStateChangeListener {
        void onItemSelected();

        void onItemClean();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.activity_classify);
        ButterKnife.bind(this);
        intiData();
        setTheme();
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setTitle("添加分类");
                View addClassifyView = LayoutInflater.from(mContext).inflate(R.layout.item_add_classify, mContainer, false);
                final EditText et = (EditText) addClassifyView.findViewById(R.id.item_add_classify_et);

                builder.setView(addClassifyView);
                builder.setPositiveButton("添加", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String classify = et.getText().toString();
                        if (!classify.isEmpty()) {
                            mList.add(new Classify(classify, 0));
                        } else {
                            et.setError("清输入内容");
                        }
                    }
                });
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.create().show();
            }
        });
    }

    public void setTheme() {
        SharedPreferences mPreferences = getSharedPreferences("PassWordManager", Context.MODE_APPEND); //读取颜色配置
        int mThemeIndex = mPreferences.getInt("ThemeIndex", 0);
        int themeColor = Constan.getThemeColor(mThemeIndex, mContext); //获取保存的颜色
        mToolBar.setBackgroundColor(themeColor); // 标题栏设置颜色
        mToolBar.setTitle("分类管理");
        setSupportActionBar(mToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        StatusBarUtils.setColor(this, themeColor); // 状态栏设置颜色
        mFab.setBackgroundColor(themeColor); // fab设置颜色
    }

    private void initView() {
        if (mAdapter == null) {
            mAdapter = new ClassifyAdapter();
            mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
            mRecyclerView.setHasFixedSize(true); //固定大小
            mRecyclerView.setAdapter(mAdapter);
            mCallBack = new ClassifyTouchCallBack(mAdapter);
            mHellper = new ItemTouchHelper(mCallBack);
            mHellper.attachToRecyclerView(mRecyclerView);
        } else {
            mAdapter.notifyDataSetChanged();
        }
    }

    private void intiData() {
        mList = (ArrayList<Classify>) getIntent().getSerializableExtra("Classify");
        mCopy = new ArrayList<>();
        for (int i = 0; i < mList.size(); i++) {
            mCopy.add(mList.get(i));
        }
        size = mList.size();
        mHandler.sendEmptyMessage(0);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_classify, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                judgeFinish();
                break;

            case R.id.action_done:
                setResult();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    //判断是否有改动,弹出提示窗口
    private void judgeFinish() {
        if (size == mList.size()) {
            setResult();
            finish();
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setTitle("提示");
            builder.setMessage("是否保留当前更改,已删除的分类中的内容将会移到未分类中?");
            builder.setPositiveButton("保存", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    setResult();
                }
            });
            builder.setNegativeButton("不保存", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent();
                    intent.putExtra("Classify", mCopy);
                    setResult(EDIT_CLASSIFY, intent);
                    finish();
                }
            });
            builder.create().show();
        }
    }

    public void setResult() {
        Intent intent = new Intent();
        intent.putExtra("Classify", mList);
        setResult(EDIT_CLASSIFY, intent);
        finish();
    }

    private class ClassifyAdapter extends RecyclerView.Adapter<ClassifyHolder> implements onChangeListener {
        @Override
        public ClassifyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View rootView = LayoutInflater.from(mContext).inflate(R.layout.item_classify_edit, parent, false);
            return new ClassifyHolder(rootView);
        }

        @Override
        public void onBindViewHolder(final ClassifyHolder holder, int position) {
            Classify item = mList.get(position + 1);
            holder.mText.setText(item.getmText());
            holder.mFlag.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    int action = event.getAction();
                    if (action == MotionEvent.ACTION_MOVE) {
                        mHellper.startDrag(holder);
                    }
                    return true;
                }
            });

        }

        @Override
        public int getItemCount() {
            return mList.size() - 1;
        }

        @Override
        public boolean onItemMove(int from, int to) {
            Collections.swap(mList, from + 1, to + 1);
            notifyItemMoved(from, to);
            return false;
        }

        @Override
        public void onItemDismiss(int position) {
            mList.remove(position + 1);
            notifyItemRemoved(position);

        }
    }

    private class ClassifyHolder extends RecyclerView.ViewHolder implements onStateChangeListener {
        private TextView mText;
        private ImageView mFlag;
        private View itemView;

        public ClassifyHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            mText = (TextView) (itemView).findViewById(R.id.item_classify_edit_text);
            mFlag = (ImageView) itemView.findViewById(R.id.item_classify_edit_flag);
        }

        @Override
        public void onItemSelected() {
            itemView.setBackgroundColor(Color.LTGRAY);
        }

        @Override
        public void onItemClean() {
            itemView.setBackgroundColor(Color.WHITE);
        }
    }

    private class ClassifyTouchCallBack extends ItemTouchHelper.Callback {

        public ClassifyTouchCallBack(onChangeListener listener) {
            onChangeListener = listener;
        }

        @Override
        public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
            if (actionState != ItemTouchHelper.ACTION_STATE_IDLE) {
                //改变背景色
                if (viewHolder instanceof ClassifyHolder) {
                    onStateChangeListener = (ClassifyManagerActivity.onStateChangeListener) viewHolder;
                    onStateChangeListener.onItemSelected();
                }
            }
            super.onSelectedChanged(viewHolder, actionState);
        }

        @Override
        public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            if (viewHolder instanceof ClassifyHolder) {
                onStateChangeListener = (ClassifyManagerActivity.onStateChangeListener) viewHolder;
                onStateChangeListener.onItemClean();
            }
            super.clearView(recyclerView, viewHolder);
        }

        @Override
        public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) { //侧滑操作
                float alpha = 1 - Math.abs(dX) / viewHolder.itemView.getWidth();
                viewHolder.itemView.setAlpha(alpha);
                viewHolder.itemView.setTranslationX(dX);
            }
        }

        //拖动方向和侧滑方向
        @Override
        public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            int dragFlag = ItemTouchHelper.UP | ItemTouchHelper.DOWN;  //拖动方向
            int swipeFlag = ItemTouchHelper.START | ItemTouchHelper.END; //滑动方向
            return makeMovementFlags(dragFlag, swipeFlag);
        }

        //拖动时候调用
        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
            onChangeListener.onItemMove(viewHolder.getAdapterPosition(), target.getAdapterPosition());
            return true;
        }

        //侧滑时候调用
        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
            onChangeListener.onItemDismiss(viewHolder.getAdapterPosition());
        }
    }


}
