package com.lemon95.ymtv.presenter;


import com.lemon95.ymtv.bean.Live;
import com.lemon95.ymtv.bean.impl.ITvBean;
import com.lemon95.ymtv.dao.TvBean;
import com.lemon95.ymtv.utils.LogUtils;
import com.lemon95.ymtv.view.activity.LiveActivity;

/**
 * Created by wuxiaotie on 2016/10/26.
 */
public class LivePresenter {

    private LiveActivity liveActivity;
    private ITvBean iTvBean;

    public LivePresenter(LiveActivity liveActivity) {
        this.liveActivity = liveActivity;
        this.iTvBean = new TvBean();
    }


    public void getTvs(final String type, final int state) {
        iTvBean.getTVs(type, new TvBean.OnTVListener() {
            @Override
            public void onSuccess(Live live) {
                liveActivity.initLiveDate(live,type,state);
            }

            @Override
            public void onFailure(Throwable e) {
                LogUtils.e("LivePresenter","获取直播失败");
            }
        });
    }
}
