package com.sdgd.news.ui.activity;

import android.content.Intent;

import com.sdgd.news.ui.base.BaseActivity;
import com.sdgd.news.ui.base.BasePresenter;
import com.sdgd.news.utils.UIUtils;

import flyn.Eyes;

/**
 * @author ChayChan
 * @description: 闪屏页
 * @date 2017/7/16  17:17
 */

public class SplashActivity extends BaseActivity {

    @Override
    protected BasePresenter createPresenter() {
        return null;
    }

    @Override
    public boolean enableSlideClose() {
        return false;
    }

    @Override
    protected int provideContentViewId() {
        return com.sdgd.news.R.layout.activity_splash;
    }

    @Override
    public void initView() {
        Eyes.translucentStatusBar(this,false);//
        UIUtils.postTaskDelay(new Runnable() {
            @Override
            public void run() {
                 startActivity(new Intent(SplashActivity.this,MainActivity.class));
                finish();
            }
        },2000);
    }
}
