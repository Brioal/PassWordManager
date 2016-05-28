package com.brioal.passwordmanager.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.brioal.passwordmanager.R;
import com.brioal.passwordmanager.model.Classify;
import com.brioal.passwordmanager.model.PassItem;
import com.brioal.passwordmanager.util.Constan;
import com.brioal.passwordmanager.util.DataBaseHelper;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import me.imid.swipebacklayout.lib.StatusBarUtils;
import me.imid.swipebacklayout.lib.app.SwipeBackActivity;

public class PassDetailActivity extends SwipeBackActivity {
    private final int EDIT = 0;
    public static final int DELETE = 90;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.pass_detail_account)
    TextView passDetailAccount;
    @Bind(R.id.pass_detail_pass)
    TextView passDetailPass;
    @Bind(R.id.pass_detail_desc)
    TextView passDetailDesc;
    @Bind(R.id.pass_detail_classify)
    TextView passDetailClassify;
    @Bind(R.id.fab)
    FloatingActionButton mFab;
    @Bind(R.id.toolbar_layout)
    CollapsingToolbarLayout toolbarLayout;
    @Bind(R.id.app_bar)
    AppBarLayout appBar;
    @Bind(R.id.pass_detail_container)
    NestedScrollView mContainer;


    private PassItem mItem;
    private ArrayList<Classify> mClassify;
    private Context mContext;
    private AlertDialog.Builder mBuild;
    private AlertDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.activity_pass_detail);
        ButterKnife.bind(this);
        initData();
        initView();
        setTheme();
    }


    private void initData() {
        mItem = (PassItem) getIntent().getSerializableExtra("Pass");
        mClassify = (ArrayList<Classify>) getIntent().getSerializableExtra("Classify");
    }

    private void initView() {
        mBuild = new AlertDialog.Builder(mContext);
        mBuild.setTitle("提示");
        mBuild.setMessage("是否删除当前记录?");
        mBuild.setNegativeButton("不删除", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        mBuild.setPositiveButton("删除", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                setResult(DELETE);
                finish();
            }
        });
        toolbar.setTitle(mItem.getmTitle());
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbarLayout.setTitle(mItem.getmTitle());
        passDetailAccount.setText(mItem.getmAccount());
        passDetailPass.setText(mItem.getmPass());
        passDetailDesc.setText(mItem.getmDesc());
        passDetailClassify.setText(mItem.getmClassify());

        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PassDetailActivity.this, AddPassItemActivity.class);
                intent.putExtra("Pass", mItem);
                intent.putExtra("Classify", mClassify);
                startActivityForResult(intent, EDIT);
            }
        });
    }

    public void setTheme() {
        SharedPreferences preferences = getSharedPreferences("PassWordManager", Context.MODE_APPEND);
        int mThemeIndex = preferences.getInt("ThemeIndex", 0);
        int themeColor = Constan.getThemeColor(mThemeIndex, mContext);
        mFab.setBackgroundColor(themeColor);
        toolbar.setTitleTextColor(getResources().getColor(R.color.colorWhite));
        StatusBarUtils.setColor(this, themeColor);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == EDIT && resultCode == AddPassItemActivity.EDIT_ITEM) {
            mItem = (PassItem) data.getSerializableExtra("Item");
            toolbar.setTitle(mItem.getmTitle());
            toolbarLayout.setTitle(mItem.getmTitle());
            setSupportActionBar(toolbar);
            passDetailAccount.setText(mItem.getmAccount());
            passDetailPass.setText(mItem.getmPass());
            passDetailDesc.setText(mItem.getmDesc());
            passDetailClassify.setText(mItem.getmClassify());
            Intent intent = new Intent();
            intent.putExtra("Item", mItem);
            saveData();
            setResult(AddPassItemActivity.EDIT_ITEM, intent);
        }
    }

    private void saveData() {
        DataBaseHelper mHelper = new DataBaseHelper(this, "Pass.db3", null, 1);
        SQLiteDatabase db = mHelper.getReadableDatabase();
        //更新数据
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.pass_detail_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.pass_detail_share:
                //分享操作
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, mItem.toString());
                sendIntent.setType("text/plain");
                startActivity(Intent.createChooser(sendIntent, "选择要分享的方式"));
                break;

            case R.id.pass_detail_delete:
                //删除操作
                if (mDialog == null) {
                    mDialog = mBuild.create();
                }
                mDialog.show();
                break;

            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
