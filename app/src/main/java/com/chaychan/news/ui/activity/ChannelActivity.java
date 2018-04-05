package com.chaychan.news.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

import com.chad.library.adapter.base.BaseViewHolder;
import com.chaychan.news.R;
import com.chaychan.news.listener.ItemDragHelperCallBack;
import com.chaychan.news.listener.OnChannelDragListener;
import com.chaychan.news.listener.OnChannelListener;
import com.chaychan.news.model.entity.Channel;
import com.chaychan.news.ui.adapter.ChannelAdapter;
import com.chaychan.news.ui.base.BaseActivity;
import com.chaychan.news.ui.base.BasePresenter;
import com.chaychan.news.utils.ConstanceValue;
import com.socks.library.KLog;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;


public class ChannelActivity extends BaseActivity implements OnChannelDragListener {

    @Bind(R.id.recyclerView)
    RecyclerView mRecyclerView;
    private List<Channel> mDatas = new ArrayList<>();
    private ChannelAdapter mAdapter;
   // private final String[] titles = new String[]{"推荐", "视频", "热点", "社会", "娱乐", "科技", "汽车", "体育", "财经", "军事", "国际", "时尚", "游戏", "旅游", "历史", "探索", "美食", "育儿", "养生", "故事", "美文"};
    private ItemTouchHelper mHelper;

    public static void setmOnChannelListener(OnChannelListener mOnChannelListener1) {
         mOnChannelListener = mOnChannelListener1;
    }

    private static OnChannelListener mOnChannelListener;


    public static void start(Context context,List<Channel> selectedDatas,List<Channel> unselectedDatas,OnChannelListener mOnChannelListener) {
        start(context, selectedDatas,unselectedDatas, -1,mOnChannelListener);
    }

    public static void start(Context context, List<Channel> selectedDatas,List<Channel> unselectedDatas, int requestCode,OnChannelListener mOnChannelListener) {
        Intent intent = new Intent(context, ChannelActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(ConstanceValue.DATA_SELECTED, (Serializable) selectedDatas);
        bundle.putSerializable(ConstanceValue.DATA_UNSELECTED, (Serializable) unselectedDatas);
        intent.putExtras(bundle);
        setmOnChannelListener(mOnChannelListener);
       // intent.putExtra(ConstanceValue.DATA, (Serializable) list);
        if (context instanceof Activity) {
            Activity activity = (Activity) context;
            activity.startActivityForResult(intent, requestCode);
        }

    }

    private void setDataType(List<Channel> datas, int type) {
        for (int i = 0; i < datas.size(); i++) {
            datas.get(i).setItemType(type);
        }
    }



    @Override
    public void initView() {
        super.initView();
        KLog.i("MARK processLogic");

        mDatas.add(new Channel(Channel.TYPE_MY, "我的频道", ""));

        List<Channel> selectedDatas = (List<Channel>) getIntent().getExtras().getSerializable(ConstanceValue.DATA_SELECTED);
        List<Channel> unselectedDatas = (List<Channel>) getIntent().getExtras().getSerializable(ConstanceValue.DATA_UNSELECTED);


        //setDataType(selectedDatas, Channel.TYPE_MY_CHANNEL);
        setDataType(unselectedDatas, Channel.TYPE_OTHER_CHANNEL);

        mDatas.addAll(selectedDatas);
        mDatas.add(new Channel(Channel.TYPE_OTHER, "频道推荐", ""));
        mDatas.addAll(unselectedDatas);


        mAdapter = new ChannelAdapter(mDatas);
        GridLayoutManager manager = new GridLayoutManager(this, 4);
        mRecyclerView.setLayoutManager(manager);

        mRecyclerView.setAdapter(mAdapter);
        manager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                int itemViewType = mAdapter.getItemViewType(position);
                return itemViewType == Channel.TYPE_FIXED_MY_CHANNEL || itemViewType == Channel.TYPE_MY_CHANNEL || itemViewType == Channel.TYPE_OTHER_CHANNEL ? 1 : 4;
            }
        });

        ItemDragHelperCallBack callBack = new ItemDragHelperCallBack(this);
        mHelper = new ItemTouchHelper(callBack);
        mAdapter.setOnChannelDragListener(this);
        //attachRecyclerView
        mHelper.attachToRecyclerView(mRecyclerView);
    }

    @Override
    public void initListener() {
    }


    @Override
    public void onStarDrag(BaseViewHolder baseViewHolder) {
        //开始拖动
        KLog.i("开始拖动");
        mHelper.startDrag(baseViewHolder);
    }

    @Override
    public void onItemMove(int starPos, int endPos) {
        //        if (starPos < 0||endPos<0) return;
        //我的频道之间移动
        if (mOnChannelListener != null)
            mOnChannelListener.onItemMove(starPos - 1, endPos - 1);//去除标题 我的频道 所占的一个index
        onMove(starPos, endPos);
    }

    private void onMove(int starPos, int endPos) {
        Channel startChannel = mDatas.get(starPos);
        //先删除之前的位置
        mDatas.remove(starPos);
        //添加到现在的位置
        mDatas.add(endPos, startChannel);
        mAdapter.notifyItemMoved(starPos, endPos);
    }

    @Override
    public void onMoveToMyChannel(int starPos, int endPos) {
        //移动到我的频道
        onMove(starPos, endPos);

        if (mOnChannelListener != null)
            mOnChannelListener.onMoveToMyChannel(starPos - 1 - mAdapter.getMyChannelSize(), endPos - 1); //1 去除 频道推荐 所占的index
    }

    @Override
    public void onMoveToOtherChannel(int starPos, int endPos) {
        //移动到推荐频道
        onMove(starPos, endPos);
        if (mOnChannelListener != null)
            mOnChannelListener.onMoveToOtherChannel(starPos - 1, endPos - 2 - mAdapter.getMyChannelSize()); //2 是 标题个数 我的频道 频道推荐
    }

    @Override
    public void onFinish() {

    }

    @Override
    public void onBackPressed() {   //频道调整完毕 返回刷新 home 首页
        super.onBackPressed();
        if (mOnChannelListener != null)
            mOnChannelListener.onFinish();
    }

    @Override
    protected BasePresenter createPresenter() {
        return null;
    }

    @Override
    protected int provideContentViewId() {
        KLog.i("MARK activity_channel!");
        return R.layout.activity_channel;
    }

    @OnClick(R.id.icon_collapse)
    public void onViewClicked() {  //关闭  相当于 返回键

        if (mOnChannelListener != null)
            mOnChannelListener.onFinish();
        finish();
    }
}
