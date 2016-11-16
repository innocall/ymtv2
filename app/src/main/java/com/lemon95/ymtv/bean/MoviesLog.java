package com.lemon95.ymtv.bean;

/**
 * Created by WXT on 2016/10/10.
 * 影片播放记录
 */
public class MoviesLog {
    private int id;
    private String userId;  //用户Id
    private String movieId;  //影片ID
    private String movieName; //影片名称
    private String movieType; //影片类型
    private String movieImage; //图片
    private String serialsId; //电视剧Id
    private String serialsPoint; //电视剧观看到多少集
    private String watchTime;   //影片观看时间 单位 秒
    private String addTime;    //什么时候观看 格式 YYYY-MM-DD HH:MM:SS
    private String b1;   //影片记录的id

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMovieId() {
        return movieId;
    }

    public void setMovieId(String movieId) {
        this.movieId = movieId;
    }

    public String getMovieName() {
        return movieName;
    }

    public void setMovieName(String movieName) {
        this.movieName = movieName;
    }

    public String getMovieType() {
        return movieType;
    }

    public void setMovieType(String movieType) {
        this.movieType = movieType;
    }

    public String getMovieImage() {
        return movieImage;
    }

    public void setMovieImage(String movieImage) {
        this.movieImage = movieImage;
    }

    public String getSerialsId() {
        return serialsId;
    }

    public void setSerialsId(String serialsId) {
        this.serialsId = serialsId;
    }

    public String getSerialsPoint() {
        return serialsPoint;
    }

    public void setSerialsPoint(String serialsPoint) {
        this.serialsPoint = serialsPoint;
    }

    public String getWatchTime() {
        return watchTime;
    }

    public void setWatchTime(String watchTime) {
        this.watchTime = watchTime;
    }

    public String getAddTime() {
        return addTime;
    }

    public void setAddTime(String addTime) {
        this.addTime = addTime;
    }

    public String getB1() {
        return b1;
    }

    public void setB1(String b1) {
        this.b1 = b1;
    }
}
