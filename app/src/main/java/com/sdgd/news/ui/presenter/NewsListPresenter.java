package com.sdgd.news.ui.presenter;


import com.google.gson.Gson;
import com.sdgd.news.model.entity.News;
import com.sdgd.news.model.entity.NewsData;
import com.sdgd.news.model.response.NewsResponse;
import com.sdgd.news.ui.base.BasePresenter;
import com.sdgd.news.utils.ListUtils;
import com.sdgd.news.utils.PreUtils;
import com.sdgd.news.view.lNewsListView;
import com.socks.library.KLog;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.observers.DisposableObserver;

/**
 * @author ChayChan
 * @description: 新闻列表的presenter
 * @date 2017/6/18  10:04
 */

public class NewsListPresenter extends BasePresenter<lNewsListView> {

    private long lastTime;

    public NewsListPresenter(lNewsListView view) {
        super(view);
    }


    public void getNewsList(String channelCode){
        lastTime = PreUtils.getLong(channelCode,0);//读取对应频道下最后一次刷新的时间戳
        if (lastTime == 0){
            //如果为空，则是从来没有刷新过，使用当前时间戳
            lastTime = System.currentTimeMillis() / 1000;
        }

        addSubscription(mApiService.getNewsList(channelCode,lastTime,System.currentTimeMillis()/1000), new DisposableObserver<NewsResponse>() {
            @Override
            public void onComplete() {

            }

            @Override
            public void onError(Throwable e) {
                KLog.e(e.getLocalizedMessage());
                mView.onError();
            }

            @Override
            public void onNext(NewsResponse response) {
                lastTime = System.currentTimeMillis() / 1000;
                PreUtils.putLong(channelCode,lastTime);//保存刷新的时间戳

                List<NewsData> data = response.data;
                List<News> newsList = new ArrayList<>();
                if (!ListUtils.isEmpty(data)){
                    for (NewsData newsData : data) {
                        News news = new Gson().fromJson(newsData.content, News.class);
                        newsList.add(news);
                    }
                }
                KLog.e(newsList);
                mView.onGetNewsListSuccess(newsList,response.tips.display_info);
            }
        });
    }
}
