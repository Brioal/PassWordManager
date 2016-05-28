package me.imid.swipebacklayout.lib;

import android.support.v7.app.AppCompatActivity;


/**
 * Created by Jaeger on 16/2/14.
 * StatusBarDemo
 */
public class BaseActivity extends AppCompatActivity {


    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        StatusBarUtils.setColor(this, getResources().getColor(R.color.colorPrimary));
    }



}
