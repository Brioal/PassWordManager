package com.brioal.passwordmanager.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.brioal.passwordmanager.R;
import com.brioal.passwordmanager.entity.ClassifyEntity;
import com.brioal.passwordmanager.entity.PassEntity;
import com.brioal.passwordmanager.util.Constans;
import com.brioal.passwordmanager.view.CircleHead;
import com.brioal.passwordmanager.view.CircleImageView;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import me.imid.swipebacklayout.lib.StatusBarUtils;

public class AddPassItemActivity extends AppCompatActivity implements View.OnClickListener {
    public static final int NEW_ITEM = 100;
    public static final int EDIT_ITEM = 101;

    @Bind(R.id.add_toolBar)
    Toolbar mToolBar;
    @Bind(R.id.add_image)
    CircleHead mCirclrHead;
    @Bind(R.id.add_title)
    EditText mTitle;
    @Bind(R.id.add_account)
    EditText mAccount;
    @Bind(R.id.add_pass)
    EditText mPass;
    @Bind(R.id.add_desc)
    EditText mDesc;
    @Bind(R.id.add_cancel)
    Button mCancel;
    @Bind(R.id.add_save)
    Button mSave;
    @Bind(R.id.add_classify)
    Spinner mSpinner;
    @Bind(R.id.add_container)
    CoordinatorLayout mContainer;
    @Bind(R.id.add_head)
    CircleImageView mHead;
    private SharedPreferences mPreferences;
    private int selectIndex = -1;
    private Context mContext;
    private PassEntity mItem;
    private ArrayList<ClassifyEntity> mClassifyEntities;
    private String mCurrentClassify = "";
    private AlertDialog.Builder mBuild;
    private AlertDialog mDialog;
    private boolean isNew = true; //是否是新建

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.activity_add_pass_item);
        ButterKnife.bind(this);
        initData();
        initView();
        setTheme();
    }

    public void initData() {
        if (getIntent().getSerializableExtra("Pass") != null) {
            mItem = (PassEntity) getIntent().getSerializableExtra("Pass");
            mTitle.setText(mItem.getTitle());
            mAccount.setText(mItem.getAccount());
            mPass.setText(mItem.getPass());
            mDesc.setText(mItem.getDesc());
            if (mItem.getHead() != -1) {
                mCirclrHead.setVisibility(View.GONE);
                mHead.setVisibility(View.VISIBLE);
                mHead.setImageResource(Constans.getDataLoader(mContext).getHeads()[mItem.getHead()]);
                mHead.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        chooseHead(mItem.getHead());
                    }
                });
            } else {
                mCirclrHead.setVisibility(View.VISIBLE);
                mCirclrHead.setmText(mItem.getTitle().toCharArray()[0]);
            }

            isNew = false;
        } else {
            mItem = new PassEntity("", "",  0, "",  "", "", -1); //返回一个空的内容
            mCirclrHead.setVisibility(View.GONE);
            mHead.setVisibility(View.VISIBLE);
            mHead.setImageResource(Constans.getDataLoader(mContext).getHeads()[mItem.getHead()]);
            mHead.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    chooseHead(-1);
                }
            });
        }
        mClassifyEntities = (ArrayList<ClassifyEntity>) getIntent().getSerializableExtra("ClassifyEntity");
        mSpinner.setAdapter(new ClassifyAdapter());
    }

    public void initView() {
        mToolBar.setTitle("新建");
        setSupportActionBar(mToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mSave.setOnClickListener(this);
        mCancel.setOnClickListener(this);
        String classify = mItem.getClassify();
        for (int i = 0; i < mClassifyEntities.size(); i++) {
            if (mClassifyEntities.get(i).getmText().equals(classify)) {
                mSpinner.setSelection(i);
                break;
            }
        }
        mBuild = new AlertDialog.Builder(mContext);
        mBuild.setTitle("提示");
        mBuild.setMessage("是否保存当前内容,选择否将丢失已编辑的内容?");
        mBuild.setNegativeButton("不保存", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                finish();
            }
        });
        mBuild.setPositiveButton("保存", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                returnData(); //返回当前数据
            }
        });
        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mCurrentClassify = mClassifyEntities.get(position + 1).getmText();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mCurrentClassify = mItem.getClassify();

            }
        });
    }

    //返回当前的数据
    private void returnData() {
        Intent intent = new Intent();
        PassEntity item = new PassEntity();
        item.setTitle(mTitle.getText().toString());
        item.setAccount(mAccount.getText().toString());
        item.setPass(mPass.getText().toString());
        item.setDesc(mDesc.getText().toString());
        item.setTime(System.currentTimeMillis());
        item.setClassify(mCurrentClassify);
        item.setHead(selectIndex);
        intent.putExtra("Item", item);
        setResult(isNew ? NEW_ITEM : EDIT_ITEM, intent);
        finish();
    }

    public void setTheme() {
        mToolBar.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        mToolBar.setTitleTextColor(getResources().getColor(R.color.colorPrimary));
        mContainer.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        StatusBarUtils.setColor(this, getResources().getColor(R.color.colorPrimary));
    }

    public void chooseHead(int index) {
        Intent intent = new Intent(mContext, HeadChooseActivity.class);
        intent.putExtra("Index", index);
        startActivityForResult(intent, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0 && resultCode == HeadChooseActivity.CHOOSE_HEAD_DONE) {
            int index = data.getIntExtra("Index", -1);
            if (index != -1) {
                selectIndex = index;
                mHead.setImageResource(Constans.getDataLoader(mContext).getHeads()[index]);
                mCirclrHead.setVisibility(View.GONE);
                mHead.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                judgeEmpty();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    //判断是否存在编辑中的内容或者内容有修改并提示
    private void judgeEmpty() {
        if (!mTitle.getText().toString().equals(mItem.getTitle()) || !mAccount.getText().toString().equals(mItem.getAccount()) || !mPass.getText().toString().equals(mItem.getPass()) || !mDesc.getText().toString().equals(mItem.getDesc())) {  //存在改动
            if (mDialog == null) {
                mDialog = mBuild.create();
            }
            mDialog.show();
        } else {
            finish();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add_save:
                returnData();
                break;
            case R.id.add_cancel:
                judgeEmpty();
                break;
            case android.R.id.home:
                judgeEmpty();
                break;
        }
    }

    private class ClassifyAdapter implements SpinnerAdapter {

        @Override
        public void registerDataSetObserver(DataSetObserver observer) {

        }

        @Override
        public void unregisterDataSetObserver(DataSetObserver observer) {

        }

        @Override
        public int getCount() {
            return mClassifyEntities.size() - 1;
        }

        @Override
        public Object getItem(int position) {
            return mClassifyEntities.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            SpinnerViewHolder holder;
            if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.item_spinner, parent, false);
                holder = new SpinnerViewHolder(convertView);
                convertView.setTag(holder);
            } else {
                holder = (SpinnerViewHolder) convertView.getTag();
            }
            ClassifyEntity classifyEntity = mClassifyEntities.get(position);
            holder.itemView.setBackgroundColor(getResources().getColor(R.color.colorTrans));
            holder.mName.setText(classifyEntity.getmText());
            return convertView;
        }

        @Override
        public int getItemViewType(int position) {
            return 1;
        }

        @Override
        public int getViewTypeCount() {
            return 1;
        }

        @Override
        public boolean isEmpty() {
            return false;
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            SpinnerViewHolder holder;
            if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.item_spinner, parent, false);
                holder = new SpinnerViewHolder(convertView);
                convertView.setTag(holder);
            } else {
                holder = (SpinnerViewHolder) convertView.getTag();
            }
            holder.mName.setTextColor(getResources().getColor(R.color.colorBlank));
            holder.itemView.setBackgroundColor(getResources().getColor(R.color.colorWhite));
            ClassifyEntity classifyEntity = mClassifyEntities.get(position + 1);
            holder.mName.setText(classifyEntity.getmText());
            return convertView;
        }
    }

    private class SpinnerViewHolder {
        private TextView mName;
        private View itemView;

        public SpinnerViewHolder(View itemView) {
            this.itemView = itemView;
            mName = (TextView) itemView.findViewById(R.id.item_spinner_name);
        }
    }

}


