package com.lemon95.ymtv.bean.impl;

import com.lemon95.ymtv.dao.TvBean;

/**
 * Created by wuxiaotie on 2016/10/26.
 */
public interface ITvBean {

    public void getTVs(String type,TvBean.OnTVListener onTVListener);
}
