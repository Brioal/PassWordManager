package com.brioal.passwordmanager.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.brioal.passwordmanager.R;
import com.brioal.passwordmanager.util.Constans;
import com.brioal.passwordmanager.util.ImageReader;
import com.brioal.passwordmanager.view.lockview.LockView;

import java.util.Timer;
import java.util.TimerTask;

import butterknife.Bind;
import butterknife.ButterKnife;


public class LauncherActivity extends AppCompatActivity {

    private static final String TAG = "LauncherIfo";
    @Bind(R.id.launcher_back)
    ImageView mBack;
    @Bind(R.id.launcher_logo)
    ImageView mLogo;
    @Bind(R.id.launcher_lock)
    LockView mLockView;
    @Bind(R.id.launcher_lock_layout)
    LinearLayout mLockLayout;
    @Bind(R.id.launcher_msg)
    TextView mMsg;
    private Context mContent;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private boolean isFirst; // 存储是否是第一次进入
    private String mPassWord; //本地的面密码
    private String mPassWordTemp; // 存储新建密码时候的temp值
    private boolean isHavePassWord; // 是否存在密码
    private int mMax_error = 0; // 存储当前失败的次数
    private int mLockTime = 300; // 锁定界面的秒数
    Timer timer = new Timer();

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0) {
                mMsg.setText("已解锁,请重新输入");
            } else {
                int seonds = msg.what;
                int minute = seonds / 60; // 获取分钟数
                int second = seonds % 60; // 获取秒数
                mMsg.setText("离锁定结束还剩" + minute + "分 " + second + "秒");
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);
        mContent = this;
        ButterKnife.bind(this);
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN); // 设置全屏
        init();

    }

    private void init() {
        mBack.setImageBitmap(ImageReader.readBitmap(mContent, R.drawable.launcher_back));
        preferences = mContent.getSharedPreferences(Constans.SHAREPREFEREMCE_KEY, Context.MODE_PRIVATE);
        editor = preferences.edit();
        isFirst = preferences.getBoolean(Constans.SHAREPREFEREMCE_KEY_ISFIRST, true); //获取是否是第一次进入
        mPassWord = preferences.getString(Constans.SHAREPREFERENCE_KEY_PASSWORD, "0");//获取本地存储的密码
        if (mPassWord.equals("0")) { // 未保存密码
            isHavePassWord = false;
            mMsg.setText("首次使用,请设置手势密码(不少于4位)");
        } else {
            isHavePassWord = true;
            mMsg.setText("请输入密码");
        }
        if (!isFirst) {
            mLockLayout.setVisibility(View.VISIBLE);
            startLockAnimation();
        }
        mLockView.setOnFinishListener(new LockView.onFinishListener() {
            @Override
            public void Success(String PassWord) {
                //成功输入大于四位的密码
                if (!isHavePassWord) { //密码不存在
                    if (mPassWordTemp == null) {//这是第一次设置密码
                        mPassWordTemp = PassWord;
                        mMsg.setText("再次输入相同密码");
                        mLockView.resetDefault();
                    } else { // 这是第二次设置密码
                        if (PassWord.equals(mPassWordTemp)) {
                            //设置密码成功
                            mPassWord = PassWord;
                            editor.putString(Constans.SHAREPREFERENCE_KEY_PASSWORD, PassWord);
                            editor.apply();
                            mMsg.setText("密码设置成功");
                            startMainActivity();
                        } else {
                            //第二次密码与第一次不相同,重新设置密码
                            mPassWordTemp = null;
                            mMsg.setText("密码错误,请重新输入");
                            mLockView.setError();
                        }

                    }

                } else {
                    //密码存在
                    if (PassWord.equals(mPassWord)) {
                        //密码正确
                        mMsg.setText("密码正确");
                        startMainActivity();
                    } else {
                        mMax_error++;
                        if (mMax_error == Constans.LAUNCHER_MAX_ERROR_TIMES) { //失败次数达到上限
                            mLockView.setCanUnlock(false);
                            mLockView.resetDefault();
                            TimerTask timerTask = new TimerTask() {
                                @Override
                                public void run() {
                                    mLockTime--;
                                    handler.sendEmptyMessage(mLockTime);
                                    if (mLockTime == 0) {
                                        handler.sendEmptyMessage(0);
                                        mLockView.setCanUnlock(true);
                                        mLockView.resetDefault();
                                        timer.cancel();
                                    }
                                }
                            };

                            timer.schedule(timerTask, 1000, 1000);
                        }

                        if (mMax_error >= 2) {
                            mMsg.setText("密码错误,请重试," + (Constans.LAUNCHER_MAX_ERROR_TIMES - mMax_error) + "次后锁定5分钟");
                        } else {
                            mMsg.setText("密码错误,请重试");
                        }
                        mLockView.setError();
                    }
                }
            }

            @Override
            public void Failed() {
                //输入失败,位数小于4位
                mLockView.setError();
                mMsg.setText("至少连接四个点,请重试");

            }

        });
        startAnimation();
        startAnimationBAck();
    }

    private void startMainActivity() {
        startActivity(new Intent(LauncherActivity.this, MainActivity.class));
        this.finish();
    }

    private void startLockAnimation() {
        Animation animation = AnimationUtils.loadAnimation(mContent, R.anim.launcher_lock);
        animation.setFillAfter(true);
        animation.setDuration(1500);
        mLockView.setAnimation(animation);
        mMsg.setAnimation(animation);
        animation.start();
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                mLockLayout.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }


    private void startAnimationBAck() {
        Animation animation = AnimationUtils.loadAnimation(mContent, R.anim.launcher_image);
        animation.setFillAfter(true);
        animation.setDuration(5000);
        animation.setRepeatCount(-1);
        animation.setRepeatMode(Animation.REVERSE);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        mBack.setAnimation(animation);
        animation.start();
    }

    private void startAnimation() {
        Animation animation = AnimationUtils.loadAnimation(mContent, R.anim.launcher_logo);
        animation.setFillAfter(true);
        animation.setDuration(1500);
        mLogo.setAnimation(animation);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (isFirst) { // 第一次进入 ,进入引导页
                    mLockLayout.setVisibility(View.VISIBLE);
                }
                editor.putBoolean(Constans.SHAREPREFEREMCE_KEY_ISFIRST, false);
                editor.apply(); // 存储为不是第一次进入
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        animation.start();
    }
}
