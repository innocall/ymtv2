package com.lemon95.ymtv.dao;

import com.lemon95.ymtv.api.ApiManager;
import com.lemon95.ymtv.bean.Live;
import com.lemon95.ymtv.bean.impl.ITvBean;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by wuxiaotie on 2016/10/26.
 */
public class TvBean implements ITvBean{

    private static final String TAG = "TvBean";

    @Override
    public void getTVs(String type, final OnTVListener onTVListener) {
        ApiManager.getTVs(type).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Live>() {

                    @Override
                    public void call(Live live) {
                        onTVListener.onSuccess(live);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        throwable.printStackTrace();
                        onTVListener.onFailure(throwable);
                    }
                });
    }

    public interface OnTVListener{
        void onSuccess(Live live);  //获取成功
        void onFailure(Throwable e);  //获取失败
    }
}
