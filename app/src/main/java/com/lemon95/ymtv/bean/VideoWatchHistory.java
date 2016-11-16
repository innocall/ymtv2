package com.lemon95.ymtv.bean;

/**
 * Created by WXT on 2016/7/27.
 * 添加观看记录
 */
public class VideoWatchHistory {

    private String UserId;
    private String MAC;
    private String VideoTypeId; //影片类型
    private String VideoId;
    private String SerialEpisodeId;
    private String WatchTime; //影片观看时间 单位 秒
    private boolean isPersonal;
    private int VideoIndex; //电视剧观看到多少集
    private String UserIP;
    private String movieName; //影片名称
    private String movieImage; //图片
    private String addTime;    //什么时候观看 格式 YYYY-MM-DD HH:MM:SS
    private String b1;   //影片记录的id

    public String getUserId() {
        return UserId;
    }

    public void setUserId(String userId) {
        UserId = userId;
    }

    public String getMAC() {
        return MAC;
    }

    public void setMAC(String MAC) {
        this.MAC = MAC;
    }

    public String getVideoTypeId() {
        return VideoTypeId;
    }

    public void setVideoTypeId(String videoTypeId) {
        VideoTypeId = videoTypeId;
    }

    public String getVideoId() {
        return VideoId;
    }

    public void setVideoId(String videoId) {
        VideoId = videoId;
    }

    public String getSerialEpisodeId() {
        return SerialEpisodeId;
    }

    public void setSerialEpisodeId(String serialEpisodeId) {
        SerialEpisodeId = serialEpisodeId;
    }

    public String getWatchTime() {
        return WatchTime;
    }

    public void setWatchTime(String watchTime) {
        WatchTime = watchTime;
    }

    public String getUserIP() {
        return UserIP;
    }

    public void setUserIP(String userIP) {
        UserIP = userIP;
    }

    public boolean isPersonal() {
        return isPersonal;
    }

    public void setIsPersonal(boolean isPersonal) {
        this.isPersonal = isPersonal;
    }

    public int getVideoIndex() {
        return VideoIndex;
    }

    public void setVideoIndex(int videoIndex) {
        VideoIndex = videoIndex;
    }

    public String getMovieName() {
        return movieName;
    }

    public void setMovieName(String movieName) {
        this.movieName = movieName;
    }

    public String getMovieImage() {
        return movieImage;
    }

    public void setMovieImage(String movieImage) {
        this.movieImage = movieImage;
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
