package com.sdgd.news.ui.fragment;

import android.view.View;

import com.sdgd.news.ui.base.BaseFragment;
import com.sdgd.news.ui.base.BasePresenter;


/**
 * @author ChayChan
 * @description: 我的fragment
 * @date 2017/6/12  21:47
 */

public class MineFragment extends BaseFragment {

    @Override
    protected BasePresenter createPresenter() {

        //KLog.i("createPresenter");
        return null;
    }

    @Override
    protected int provideContentViewId() {
        //KLog.i("provideContentViewId");
        return  com.sdgd.news.R.layout.fragment_mine;
    }

    @Override
    public void initView(View rootView) {
        //KLog.i("initView");
    }

    @Override
    public void initData() {
        //KLog.i("initData");
    }

    @Override
    public void initListener() {
        //KLog.i("initListener");
    }

    @Override
    public void loadData() {
        //KLog.i("loadData");
    }
}
