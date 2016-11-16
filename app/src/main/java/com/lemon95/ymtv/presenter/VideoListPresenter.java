package com.lemon95.ymtv.presenter;

import com.lemon95.ymtv.bean.Conditions;
import com.lemon95.ymtv.bean.QueryConditions;
import com.lemon95.ymtv.bean.VideoSearchList;
import com.lemon95.ymtv.bean.impl.IMovieBean;
import com.lemon95.ymtv.common.AppConstant;
import com.lemon95.ymtv.dao.MovieDao;
import com.lemon95.ymtv.view.activity.VideoListActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by WXT on 2016/7/19.
 */
public class VideoListPresenter {

    private VideoListActivity videoListActivity;
    private IMovieBean iMovieBean;

    public VideoListPresenter(VideoListActivity videoListActivity) {
        this.videoListActivity = videoListActivity;
        iMovieBean = new MovieDao();
    }

    public void getCombQueryConditions(final String type) {
        iMovieBean.getCombQueryConditions(type, new MovieDao.OnConditionsListener() {
            @Override
            public void onSuccess(Conditions conditions) {
                Conditions data = conditions;
                ArrayList<QueryConditions> conditionsArrayList = new ArrayList<QueryConditions>();
                if (data != null) {
                    //全部
                    QueryConditions queryConditions1 = new QueryConditions();
                    queryConditions1.setName("全部");
                    queryConditions1.setType(type);
                    conditionsArrayList.add(queryConditions1);
                    List<Conditions.Groups> groupsList = data.getGroups();
                    for (int i=0; i<groupsList.size(); i++) {
                        String name = groupsList.get(i).getGroupName();
                        if (!"其他".equals(name)) {
                            QueryConditions queryConditions = new QueryConditions();
                            queryConditions.setGroupId(groupsList.get(i).getGroupId());
                            queryConditions.setName(name);
                            queryConditions.setType(type);
                            conditionsArrayList.add(queryConditions);
                        }
                    }
                    List<Conditions.Genres> genresList =  data.getGenres();
                    for (int i=0; i<genresList.size(); i++) {
                        String name = genresList.get(i).getTypeName();
                        if (!"其他".equals(name)) {
                            QueryConditions queryConditions = new QueryConditions();
                            queryConditions.setGenreId(genresList.get(i).getId());
                            queryConditions.setName(name);
                            queryConditions.setType(type);
                            conditionsArrayList.add(queryConditions);
                        }
                    }
                    List<Conditions.Areas> areasList = data.getAreas();
                    for (int i=0; i<areasList.size(); i++) {
                        String name = areasList.get(i).getAreaName();
                        if (!"其他".equals(name)) {
                            QueryConditions queryConditions = new QueryConditions();
                            queryConditions.setAreaId(areasList.get(i).getId());
                            queryConditions.setName(name);
                            queryConditions.setType(type);
                            conditionsArrayList.add(queryConditions);
                        }
                    }
                    videoListActivity.showListView(conditionsArrayList);
                    getCombSearch(conditionsArrayList.get(0).getAreaId(), conditionsArrayList.get(0).getGenreId(), conditionsArrayList.get(0).getGroupId(), conditionsArrayList.get(0).getChargeMethod(), conditionsArrayList.get(0).getVipLevel(), conditionsArrayList.get(0).getYear(), conditionsArrayList.get(0).getType(), "1", AppConstant.PAGESIZE);
                    //getCombSearch("0","0",conditionsArrayList.get(0).getGroupId(),conditionsArrayList.get(0).getChargeMethod(),conditionsArrayList.get(0).getVipLevel(),conditionsArrayList.get(0).getYear(),conditionsArrayList.get(0).getType(),"1", AppConstant.PAGESIZE);
                }
            }

            @Override
            public void onFailure(Throwable e) {

            }
        });
    }

    public void getCombSearch(String areaId, String genreId, String groupId, String chargeMethod, String vipLevel, String year, String type, String currentPage, String pageSize) {
        iMovieBean.getCombSearch(areaId,genreId,groupId,chargeMethod,vipLevel,year,type,currentPage,pageSize,new MovieDao.OnVideoSearchListListener(){

            @Override
            public void onSuccess(VideoSearchList videoSearchList) {
                VideoSearchList.Data data = videoSearchList.getData();
                List<VideoSearchList.Data.VideoBriefs> videoBriefsList =  data.getVideoBriefs();
                videoListActivity.showGridView(data);
            }

            @Override
            public void onFailure(Throwable e) {

            }
        });
    }

}
