package com.brioal.passwordmanager.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.brioal.passwordmanager.R;
import com.brioal.passwordmanager.entity.ClassifyEntity;
import com.brioal.passwordmanager.entity.PassEntity;
import com.brioal.passwordmanager.util.Constans;
import com.brioal.passwordmanager.util.DBHelper;
import com.brioal.passwordmanager.view.CircleHead;
import com.brioal.passwordmanager.view.ClassifyListView;
import com.brioal.passwordmanager.view.Fab;
import com.gordonwong.materialsheetfab.DimOverlayFrameLayout;
import com.gordonwong.materialsheetfab.MaterialSheetFab;
import com.gordonwong.materialsheetfab.MaterialSheetFabEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import me.imid.swipebacklayout.lib.StatusBarUtils;

public class MainActivity extends AppCompatActivity
        implements View.OnClickListener {

    private static final String TAG = "MainInfo";
    private static final int UPDATE_ITEM = 1;
    private static final int ADD_ITEM = 2;
    private static final int SELECT_INDEX = 3;
    private static final int INIT_SUCCESS = 4;
    private static final int NOTIFIED_CLASSIFY = 5;
    private static final int NOTIDIED_PASS = 6;
    private static final int MANAGER_CLASSIFY = 7;
    @Bind(R.id.menu_add)
    ImageButton menuAdd; //添加分类按钮
    @Bind(R.id.menu_listView)
    ClassifyListView menuListView; //分类列表
    @Bind(R.id.main_toolBar)
    Toolbar mToolBar;
    @Bind(R.id.main_appBar)
    AppBarLayout mAppBar;
    @Bind(R.id.main_recyclerView)
    RecyclerView mRecyclerView;
    @Bind(R.id.main_fab)
    Fab mFab;
    @Bind(R.id.fragment_passList_overly)
    DimOverlayFrameLayout overlay;
    @Bind(R.id.fragment_passList_sheet)
    CardView mSheetView;
    @Bind(R.id.fragment_passList_container)
    LinearLayout mContainer;
    @Bind(R.id.main_drawer)
    DrawerLayout mDrawer;
    @Bind(R.id.menu_head_layout)
    LinearLayout mHeadLayout; //头像背景
    @Bind(R.id.main_container)
    CoordinatorLayout mMainContainer;


    private MaterialSheetFab materialSheetFab;

    private int mCurrentIndex = 0; //当前编辑的item下表
    private Context mContext;
    private SharedPreferences mPreferences;
    private SharedPreferences.Editor mEditor;
    long mPressTime = 0;


    private List<PassEntity> mAllPass; //密码信息
    private List<PassEntity> mCurrentPass; //密码信息

    private ArrayList<ClassifyEntity> mClassifies; //分类信息
    private PassItemAdapter mPassAdapter;
    private MyBaseAdapter mClassifyAdapter;
    private DBHelper mHelper;
    //item的资源图片
    private int[] mImages = new int[]{
            R.mipmap.ic_text,
    };
    //item的文字
    private int[] mNames = new int[]{

            R.string.item_creat
    };
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == INIT_SUCCESS) { //读取所有数据成功
                //初始化菜单
                initClassifyMenu();
                //初始化列表
                initRecyclerView();
            } else if (msg.what == SELECT_INDEX) { //选择当前选中项
                for (int i = 0; i < menuListView.getChildCount(); i++) {
                    int color = -1;
                    if (i == mCurrentIndex) {
                        color = getResources().getColor(R.color.color_blank);
                    } else {
                        color = getResources().getColor(R.color.colorWhite);
                    }
                    menuListView.getChildAt(i).setBackgroundColor(color);
                }
            } else if (msg.what == NOTIFIED_CLASSIFY) { //更新分类列表
                mClassifyAdapter.notifyDataSetChanged();
            } else if (msg.what == NOTIDIED_PASS) { //更新密码列表
                mPassAdapter.notifyDataSetChanged();
            }

        }
    };


    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            initData();
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        mContext = this;
        mHelper = new DBHelper(mContext, "Pass.db3", null, 1);
        new Thread(mRunnable).start();
        initView();
    }

    public void initView() {
        mToolBar.setTitle("全部");
        setSupportActionBar(mToolBar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawer, mToolBar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawer.setDrawerListener(toggle);
        toggle.syncState();
        initFab();
        setTheme();
        initActions();
    }

    public void initFab() {
        int sheetColor = getResources().getColor(R.color.colorWhite);
        addView();
        materialSheetFab = new MaterialSheetFab<>(mFab, mSheetView, overlay,
                sheetColor, getResources().getColor(R.color.colorPrimary));
        materialSheetFab.setEventListener(new MaterialSheetFabEventListener() {
            @Override
            public void onShowSheet() {
                mRecyclerView.clearFocus();
                mSheetView.requestFocus();
            }

            @Override
            public void onHideSheet() {
            }
        });
    }

    public void setTheme() {
        mToolBar.setBackgroundColor(getResources().getColor(R.color.colorPrimary)); // 标题栏设置颜色
        mHeadLayout.setBackgroundColor(getResources().getColor(R.color.colorPrimary)); //抽屉菜单设置颜色
        StatusBarUtils.setColor(this, getResources().getColor(R.color.colorPrimary)); // 状态栏设置颜色
        mFab.setColor(getResources().getColor(R.color.colorPrimary)); // fab设置颜色
    }

    private void initActions() {
        menuAdd.setOnClickListener(this);
    }

    //从网络或者本地获取到所有的数据
    private void initData() {
        readLocalData();
    }



    //初始化列表项
    public void initRecyclerView() {
        mPassAdapter = new PassItemAdapter();
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mRecyclerView.setAdapter(mPassAdapter);
        mRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (Math.abs(dy) > 30) {
                    materialSheetFab.hideSheetThenFab();
                }
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) { //停止
                    materialSheetFab.showFab();
                }
            }
        });
    }

    //初始化菜单项目
    private void initClassifyMenu() {
        mClassifyAdapter = new MyBaseAdapter();
        menuListView.setAdapter(mClassifyAdapter);
        menuListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mDrawer.closeDrawer(GravityCompat.START);
                mCurrentIndex = position;
                getSupportActionBar().setTitle(mClassifies.get(mCurrentIndex).getmText());
                initCurrentPass();
                mHandler.sendEmptyMessage(NOTIDIED_PASS);
                mHandler.sendEmptyMessage(SELECT_INDEX);
            }
        });
        menuListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog.Builder build = new AlertDialog.Builder(mContext);
                build.setTitle("确认操作");
                build.setMessage("删除分类后其内容会自动合并到全部,是否删除此分类");
                build.setPositiveButton("删除", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mClassifies.remove(position);
                        //清空分类信息
                        cleanClassifyMessage();
                        //保存分类数据
                        mHandler.sendEmptyMessage(NOTIFIED_CLASSIFY);
                    }
                });
                build.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                build.create().show();
                return true;
            }
        });
        initClassifyCount();
    }


    private void cleanClassifyMessage() {
        for (int i = 0; i < mAllPass.size(); i++) {
            PassEntity item = mAllPass.get(i);
            if (!mClassifies.contains(new ClassifyEntity(0,item.getClassify()))) {
                item.setClassify("");
            }
        }
    }

    public void initCurrentPass() {
        mCurrentPass.clear();
        if (mClassifies.get(mCurrentIndex).getmText().equals("全部")) {
            for (int i = 0; i < mAllPass.size(); i++) {
                mCurrentPass.add(mAllPass.get(i));
            }
        }
        for (int i = 0; i < mAllPass.size(); i++) {
            if (mAllPass.get(i).getClassify().equals(mClassifies.get(mCurrentIndex).getmText())) {
                mCurrentPass.add(mAllPass.get(i));
            }
        }
    }

    public void initClassifyCount() {
        mClassifies.get(0).setmNum(mAllPass.size());
        for (int i = 1; i < mClassifies.size(); i++) {
            mClassifies.get(i).setmNum(0);
        }
        for (int i = 0; i < mAllPass.size(); i++) {
            int index = mClassifies.indexOf(new ClassifyEntity( 0,mAllPass.get(i).getClassify()));
            if (index != -1) {
                int count = mClassifies.get(index).getmNum();
                mClassifies.get(index).setmNum(count + 1);
            }

        }
    }


    //添加菜单项到view
    private void addView() {
        View itemView = null;
        for (int i = 0; i < mImages.length; i++) {
            itemView = LayoutInflater.from(mContext).inflate(R.layout.item_fab, mContainer, false);
            ImageView mImage = (ImageView) itemView.findViewById(R.id.item_fab_image);
            TextView mName = (TextView) itemView.findViewById(R.id.item_fab_name);
            mImage.setImageResource(mImages[i]);
            mName.setText(mNames[i]);
            mContainer.addView(itemView);
            itemView.setId(Integer.valueOf(i));
            itemView.setOnClickListener(this);
        }
    }


    //读取本地文件 ,只执行一次
    public void readLocalData() {
        mClassifies = Constans.getDataLoader(mContext).getClassify();
        //如果大小为0 ,添加一个全部item
        if (mClassifies.size() == 0) {
            mClassifies.add(new ClassifyEntity(0,"全部" ));
            mClassifies.add(new ClassifyEntity(0,"默认"));
        }
        mAllPass = Constans.getDataLoader(mContext).getPassWords(null);
        mCurrentPass = Constans.getDataLoader(mContext).getPassWords(null);
        mHandler.sendEmptyMessage(INIT_SUCCESS);
    }

    //保存密码数据到本地
    public void savePass() {
        Constans.getDataLoader(mContext).savePassWordLocal(mAllPass);
    }

    //保存分类数据到本地
    private void saveClassify() {
       Constans.getDataLoader(mContext).saveClassifyLocal(mClassifies);
    }




    @Override
    public void onBackPressed() {
        if (mDrawer.isDrawerOpen(GravityCompat.START)) {
            mDrawer.closeDrawer(GravityCompat.START);
        } else if (materialSheetFab.isSheetVisible()) {
            materialSheetFab.hideSheet();
        } else if (System.currentTimeMillis() - mPressTime < 1500) {
            super.onBackPressed();
        } else {
            mFab.hide();
            Snackbar snackbar = Snackbar.make(mRecyclerView, "再按一次退出", Snackbar.LENGTH_SHORT);
            snackbar.setCallback(new Snackbar.Callback() {
                @Override
                public void onDismissed(Snackbar snackbar, int event) {
                    super.onDismissed(snackbar, event);
                    mFab.show();
                }

                @Override
                public void onShown(Snackbar snackbar) {
                    super.onShown(snackbar);
                }
            });
            snackbar.show();
            mPressTime = System.currentTimeMillis();
        }
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case 0: //新建item
                materialSheetFab.hideSheet();
                Intent intent = new Intent(mContext, AddPassItemActivity.class);
                intent.putExtra("ClassifyEntity", mClassifies);
                startActivityForResult(intent, ADD_ITEM);
                break;

            case R.id.menu_add:
//
                Intent i = new Intent(mContext, ClassifyManagerActivity.class);
                i.putExtra("ClassifyEntity", mClassifies);
                startActivityForResult(i, MANAGER_CLASSIFY);
                break;
        }
        if (mDrawer.isDrawerOpen(GravityCompat.START)) {
            mDrawer.closeDrawer(GravityCompat.START);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == AddPassItemActivity.NEW_ITEM) { //新建
            PassEntity item = (PassEntity) data.getSerializableExtra("Item");
            mAllPass.add(item); //新建
            initCurrentPass();
            initClassifyCount();
            savePass();
            mHandler.sendEmptyMessage(NOTIFIED_CLASSIFY);
            mHandler.sendEmptyMessage(NOTIDIED_PASS);
        } else if (resultCode == AddPassItemActivity.EDIT_ITEM) { //编辑
            PassEntity item = (PassEntity) data.getSerializableExtra("Item");
            mAllPass.remove(mCurrentIndex);
            mAllPass.add(mCurrentIndex, item);
            initCurrentPass();
            initClassifyCount();
            savePass();
            mHandler.sendEmptyMessage(NOTIFIED_CLASSIFY);
            mHandler.sendEmptyMessage(NOTIDIED_PASS);
        } else if (resultCode == PassDetailActivity.DELETE) {
            mAllPass.remove(mCurrentIndex);
            initCurrentPass();
            initClassifyCount();
            savePass();
            mHandler.sendEmptyMessage(NOTIFIED_CLASSIFY);
            mHandler.sendEmptyMessage(NOTIDIED_PASS);
        } else if (resultCode == ClassifyManagerActivity.EDIT_CLASSIFY) {
            mClassifies.clear();

            ArrayList<ClassifyEntity> datas = (ArrayList<ClassifyEntity>) data.getSerializableExtra("ClassifyEntity");
            for (int i = 0; i < datas.size(); i++) {
                mClassifies.add(datas.get(i));
            }
            cleanClassifyMessage();
            initClassifyCount();
            saveClassify();
            mHandler.sendEmptyMessage(NOTIFIED_CLASSIFY);
            mHandler.sendEmptyMessage(NOTIDIED_PASS);
        }

    }

    private class PassItemAdapter extends RecyclerView.Adapter<PassItemHolder> {

        @Override
        public PassItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(mContext).inflate(R.layout.item_pass, parent, false);
            return new PassItemHolder(itemView);
        }

        @Override
        public void onBindViewHolder(PassItemHolder holder, final int position) {
            final PassEntity item = mCurrentPass.get(position);
            if (item.getHead() == -1) {
                holder.mHead.setVisibility(View.GONE);
                holder.mCircleHead.setVisibility(View.VISIBLE);
                holder.mCircleHead.setmText(item.getTitle().toCharArray()[0]);
            } else {
                holder.mHead.setVisibility(View.VISIBLE);
                holder.mCircleHead.setVisibility(View.GONE);
                //从本地获取图片序号
                holder.mHead.setImageResource(Constans.getDataLoader(mContext).getHeads()[item.getHead()]);
            }
            holder.mTitle.setText(item.getTitle());
            holder.mDesc.setText(item.getDesc());
            holder.mClassify.setText(item.getClassify());
            setTime(item.getTime(), holder.mTime);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mCurrentIndex = position;
                    Intent intent = new Intent(mContext, PassDetailActivity.class);
                    intent.putExtra("Position", position);
                    intent.putExtra("Pass", item);
                    intent.putExtra("ClassifyEntity", mClassifies);
                    startActivityForResult(intent, UPDATE_ITEM);
                }
            });

        }

        @Override
        public int getItemCount() {
            return mCurrentPass.size();
        }

        public void setTime(long time, TextView mText) {
            Calendar calender = Calendar.getInstance();
            Date date = new Date();
            date.setYear(calender.YEAR);
            date.setMonth(calender.MONTH);
            date.setDate(calender.DAY_OF_MONTH);
            long today = date.getTime(); //今天0点的时间
            date.setDate(calender.DAY_OF_MONTH - 1);
            long yestaday = date.getTime(); //z昨天0点的时间
            SimpleDateFormat format = new SimpleDateFormat("HH:mm");
            if (time < yestaday) { //昨天之前
                SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                mText.setText(f.format(time));
            } else if (time > today) {
                mText.setText("今天 " + format.format(time));
            } else {
                mText.setText("昨天 " + format.format(time));
            }
        }
    }


    private class PassItemHolder extends RecyclerView.ViewHolder {
        private View itemView;
        private TextView mTitle;
        private TextView mDesc;
        private TextView mClassify;
        private TextView mTime;
        private ImageView mHead;
        private CircleHead mCircleHead;

        public PassItemHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            mTitle = (TextView) itemView.findViewById(R.id.item_pass_title);
            mDesc = (TextView) itemView.findViewById(R.id.item_pass_desc);
            mClassify = (TextView) itemView.findViewById(R.id.item_pass_classify);
            mTime = (TextView) itemView.findViewById(R.id.item_pass_time);
            mHead = (ImageView) itemView.findViewById(R.id.item_pass_head);
            mCircleHead = (CircleHead) itemView.findViewById(R.id.item_pass_circleHead);
        }
    }


    public class MyBaseAdapter extends BaseAdapter {
        @Override
        public int getCount() {

            return mClassifies.size();
        }

        @Override
        public Object getItem(int position) {
            return mClassifies.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        //添加列表前面的图片
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            MyHolder holder;
            if (convertView == null) {
                convertView = LayoutInflater.from(MainActivity.this).inflate(R.layout.item_classify, null);
                holder = new MyHolder(convertView);
                convertView.setTag(holder);
            } else {
                holder = (MyHolder) convertView.getTag();
            }
            if (position == mCurrentIndex) {
                convertView.setBackgroundColor(getResources().getColor(R.color.color_blank));
            } else {
                convertView.setBackgroundColor(getResources().getColor(R.color.colorWhite));
            }
            ClassifyEntity index = mClassifies.get(position);
            holder.mText.setText(index.getmText());
            holder.mNum.setText(index.getmNum() + "");
            return convertView;
        }
    }

    public class MyHolder {
        private TextView mText;
        private TextView mNum;

        public MyHolder(View convertView) {
            mText = (TextView) convertView.findViewById(R.id.item_classify_text);
            mNum = (TextView) convertView.findViewById(R.id.item_classify_num);
        }
    }

}

