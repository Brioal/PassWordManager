package com.brioal.passwordmanager.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.brioal.passwordmanager.R;
import com.brioal.passwordmanager.entity.HeadItem;
import com.brioal.passwordmanager.util.Constans;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import me.imid.swipebacklayout.lib.StatusBarUtils;

/**
 * Created by brioal on 16-4-28.
 */
public class HeadChooseActivity extends AppCompatActivity {
    public static final int CHOOSE_HEAD_DONE = 88;
    @Bind(R.id.toolBar)
    Toolbar mToolbar;
    @Bind(R.id.choose_head_recyclerView)
    RecyclerView mRecyclerVtew;
    private HeadAdapter mAdapter;
    private List<HeadItem> mHeads;
    private int mChooseIndex = 0;
    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            initData();
        }
    };
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            initView();
        }
    };
    private Context mContext;

    private void initView() {
        if (mAdapter == null) {
            mRecyclerVtew.setLayoutManager(new StaggeredGridLayoutManager(5, StaggeredGridLayoutManager.VERTICAL));
            mAdapter = new HeadAdapter();
            mRecyclerVtew.setAdapter(mAdapter);
        } else {
            mAdapter.notifyDataSetChanged();
        }

    }

    private void initData() {
        int index = getIntent().getIntExtra("Index", -1);
        if (index != -1) {
            mChooseIndex = index;
        }
        mHeads = new ArrayList<>();
        HeadItem item = null;
        for (int i = 0; i < Constans.getDataLoader(mContext).getHeads().length; i++) {
            item = new HeadItem(Constans.getDataLoader(mContext).getHeads()[i], i);
            mHeads.add(item);
        }
        mHandler.sendEmptyMessage(0);
    }

    public void setTheme() {
        mToolbar.setTitle("选择图标");
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mToolbar.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        mToolbar.setTitleTextColor(getResources().getColor(R.color.colorWhite));
        StatusBarUtils.setColor(this, getResources().getColor(R.color.colorPrimary));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_head);
        ButterKnife.bind(this);
        mContext = this;
        new Thread(mRunnable).start();
        setTheme();
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
                finish();
                break;
            case R.id.action_done:
                Intent intent = new Intent();
                intent.putExtra("Index", mChooseIndex);
                setResult(CHOOSE_HEAD_DONE, intent);
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private class HeadAdapter extends RecyclerView.Adapter<HeadViewHolder> {

        @Override
        public HeadViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View rootView = LayoutInflater.from(mContext).inflate(R.layout.item_choose_head, parent, false);
            return new HeadViewHolder(rootView);
        }

        @Override
        public void onBindViewHolder(final HeadViewHolder holder, final int position) {
            HeadItem item = mHeads.get(position);
            holder.mHead.setImageResource(item.getmHead());
            if (position == mChooseIndex) {
                holder.itemView.setBackgroundColor(Color.LTGRAY);
            } else {
                holder.itemView.setBackgroundColor(Color.WHITE);
            }
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mChooseIndex = position;
                    holder.itemView.setBackgroundColor(Color.LTGRAY);
                    mHandler.sendEmptyMessage(0);
                }
            });
        }


        @Override
        public int getItemCount() {
            return mHeads.size();
        }
    }

    private class HeadViewHolder extends RecyclerView.ViewHolder {
        private ImageView mHead;
        private View itemView;

        public HeadViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            mHead = (ImageView) itemView.findViewById(R.id.item_choose_head_image);
        }
    }

}
