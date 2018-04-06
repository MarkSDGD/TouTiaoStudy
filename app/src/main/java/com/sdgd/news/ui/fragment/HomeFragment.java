package com.sdgd.news.ui.fragment;

import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sdgd.news.R;
import com.sdgd.news.constants.Constant;
import com.sdgd.news.listener.OnChannelListener;
import com.sdgd.news.model.entity.Channel;
import com.sdgd.news.ui.activity.ChannelActivity;
import com.sdgd.news.ui.adapter.ChannelPagerAdapter;
import com.sdgd.news.ui.base.BaseFragment;
import com.sdgd.news.ui.base.BasePresenter;
import com.sdgd.news.utils.PreUtils;
import com.sdgd.news.utils.UIUtils;
import com.socks.library.KLog;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import fm.jiecao.jcvideoplayer_lib.JCVideoPlayer;
import me.weyye.library.colortrackview.ColorTrackTabLayout;

/**
 * @author ChayChan
 * @description: 首页fragment
 * @date 2017/6/12  21:47
 */

public class HomeFragment extends BaseFragment implements OnChannelListener {

    @Bind(R.id.tab_channel)
    ColorTrackTabLayout mTabChannel;

    @Bind(R.id.iv_operation)
    ImageView ivAddChannel;

    @Bind(R.id.vp_content)
    ViewPager mViewPager;

    private List<Channel> mSelectedChannels = new ArrayList<>();
    private List<Channel> mUnSelectedChannels = new ArrayList<>();
    private List<NewsListFragment> mChannelFragments = new ArrayList<>();
    private Gson mGson = new Gson();
    private ChannelPagerAdapter channelPagerAdapter;

    @Override
    protected BasePresenter createPresenter() {
        KLog.i("MARK","createPresenter");
        return null;
    }

    @Override
    protected int provideContentViewId() {
        KLog.i("MARK","1 provideContentViewId");
        return R.layout.fragment_home;
    }

    @Override
    public void initData() {
        KLog.i("MARK","3 initData -> initChannelData && initChannelFragments");
        initChannelData();
        initChannelFragments();
    }


    /**
     * 初始化已选频道和未选频道的数据
     */
    private void initChannelData() {
        KLog.i("MARK","initChannelData");
        String selectedChannelJson = PreUtils.getString(Constant.SELECTED_CHANNEL_JSON, "");
        String unselectChannel = PreUtils.getString(Constant.UNSELECTED_CHANNEL_JSON, "");

        if (TextUtils.isEmpty(selectedChannelJson) || TextUtils.isEmpty(unselectChannel)) {
            //本地没有title
            String[] channels = getResources().getStringArray(R.array.channel);
            String[] channelCodes = getResources().getStringArray(R.array.channel_code);
            mSelectedChannels.add(new Channel(Channel.TYPE_FIXED_MY_CHANNEL, "推荐","")); //默认固定推荐频道
            //默认添加了全部频道
            for (int i = 0; i < channelCodes.length; i++) {
                String title = channels[i];
                String code = channelCodes[i];
                mSelectedChannels.add(new Channel(Channel.TYPE_MY_CHANNEL,title, code));
            }

            selectedChannelJson = mGson.toJson(mSelectedChannels);//将集合转换成json字符串
            KLog.i("selectedChannelJson:" + selectedChannelJson);
            PreUtils.putString(Constant.SELECTED_CHANNEL_JSON, selectedChannelJson);//保存到sp
        } else {
            //之前添加过
            List<Channel> selectedChannel = mGson.fromJson(selectedChannelJson, new TypeToken<List<Channel>>() {
            }.getType());
            List<Channel> unselectedChannel = mGson.fromJson(unselectChannel, new TypeToken<List<Channel>>() {
            }.getType());
            mSelectedChannels.addAll(selectedChannel);
            KLog.i(" initChannelData  mSelectedChannels.size()=="+mSelectedChannels.size());

            mUnSelectedChannels.addAll(unselectedChannel);
        }
    }

    /**
     * 初始化已选频道的fragment的集合
     */
    private void initChannelFragments() {

       // String[] channelCodes = getResources().getStringArray(R.array.channel_code);
        for (Channel channel : mSelectedChannels) {
            /*NewsListFragment newsFragment = new NewsListFragment();
            Bundle bundle = new Bundle();
            bundle.putString(Constant.CHANNEL_CODE, channel.channelCode);
            bundle.putBoolean(Constant.IS_VIDEO_LIST, channel.channelCode.equals(channelCodes[1]));//是否是视频列表页面,根据判断频道号是否是视频
            newsFragment.setArguments(bundle);*/
            NewsListFragment newsFragment = NewsListFragment.newInstance(channel);
            mChannelFragments.add(newsFragment);//添加到集合中

        }
        KLog.i(" initChannelFragments  mChannelFragments.size()=="+mChannelFragments.size());

    }

    @Override
    public void initListener() {
        initViewPager();

    }

    private void initViewPager() {
        KLog.i("MARK","4 initListener");
        channelPagerAdapter = new ChannelPagerAdapter(mChannelFragments, mSelectedChannels,getChildFragmentManager());
        mViewPager.setAdapter(channelPagerAdapter);
        mViewPager.setOffscreenPageLimit(mSelectedChannels.size());

        mTabChannel.setTabPaddingLeftAndRight(UIUtils.dip2Px(10), UIUtils.dip2Px(10));
        mTabChannel.setupWithViewPager(mViewPager);
        mTabChannel.post(new Runnable() {
            @Override
            public void run() {
                //设置最小宽度，使其可以在滑动一部分距离
                ViewGroup slidingTabStrip = (ViewGroup) mTabChannel.getChildAt(0);
                slidingTabStrip.setMinimumWidth(slidingTabStrip.getMeasuredWidth() + ivAddChannel.getMeasuredWidth());
            }
        });
        //隐藏指示器
        mTabChannel.setSelectedTabIndicatorHeight(0);

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                //可以增加颜色渐变
            }

            @Override
            public void onPageSelected(int position) {
                //当页签切换的时候，如果有播放视频，则释放资源
                JCVideoPlayer.releaseAllVideos();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    public void initView(View rootView) {
        KLog.i("MARK","2 initView");

    }
    @Override
    public void loadData() {
        KLog.i("MARK","5 loadData");
    }

    public String getCurrentChannelCode(){
        int currentItem = mViewPager.getCurrentItem();
        return mSelectedChannels.get(currentItem).channelCode;
    }

    @OnClick({R.id.tv_search, R.id.iv_operation})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_search:
               //TODO
                break;
            case R.id.iv_operation:
                //TODO

                ChannelActivity.start(getActivity(),mSelectedChannels,mUnSelectedChannels,this);
                break;
        }

    }

    @Override
    public void onItemMove(int starPos, int endPos) {
      listMove(mSelectedChannels, starPos, endPos);
      listMove(mChannelFragments, starPos, endPos);
    }


    @Override
    public void onMoveToMyChannel(int starPos, int endPos) {
         //移动到我的频道
        Channel channel = mUnSelectedChannels.remove(starPos);
        mSelectedChannels.add(endPos, channel);
        mChannelFragments.add(NewsListFragment.newInstance(channel));

    }

    @Override
    public void onMoveToOtherChannel(int starPos, int endPos) {
       //移动到推荐频道
        mUnSelectedChannels.add(endPos, mSelectedChannels.remove(starPos));
        mChannelFragments.remove(starPos);

    }

    @Override
    public void onFinish() {
        mTabChannel.post(new Runnable() {
            @Override
            public void run() {
                //注意：因为最开始设置了最小宽度，所以重新测量宽度的时候一定要先将最小宽度设置为0
                ViewGroup slidingTabStrip = (ViewGroup) mTabChannel.getChildAt(0);
                slidingTabStrip.setMinimumWidth(0);
                slidingTabStrip.measure(0, 0);
            }
        });

        initViewPager();
        //保存选中和未选中的channel
        PreUtils.putString(Constant.SELECTED_CHANNEL_JSON, mGson.toJson(mSelectedChannels));//保存到sp
        PreUtils.putString(Constant.UNSELECTED_CHANNEL_JSON, mGson.toJson(mUnSelectedChannels));//保存到sp

     }

    private void listMove(List datas, int starPos, int endPos) {
      Object o = datas.get(starPos);
        //先删除之前的位置
        datas.remove(starPos);
        //添加到现在的位置
        datas.add(endPos, o);
    }

}
