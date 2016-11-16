package com.lemon95.ymtv.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.lemon95.ymtv.bean.MoviesLog;
import com.lemon95.ymtv.bean.Video;
import com.lemon95.ymtv.bean.VideoType;
import com.lemon95.ymtv.bean.VideoWatchHistory;
import com.lemon95.ymtv.utils.LogUtils;
import com.lemon95.ymtv.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by WXT on 2016/7/14.
 */
public class SqliteDBHandler {

    public static SqliteHelper helper;

    /**
     * 保存影视数据
     * @param video
     */
    public static void addVideo(Video video) {
        SQLiteDatabase db = helper.getWritableDatabase();
        if (db.isOpen()) {
            String videoTypeId = video.getVideoTypeId();
            String videoId = video.getVideoId();
            String createTime = video.getCreateTime();
            String downImg = video.getDownImg();
            String orderNum = video.getOrderNum();
            String picturePath = video.getPicturePath();
            String tag = video.getTag();
            String title = video.getTitle();
            ContentValues values = new ContentValues();
            values.put("videoTypeId", videoTypeId);
            values.put("createTime", createTime);
            values.put("videoId", videoId);
            values.put("downImg", downImg);
            values.put("orderNum", orderNum);
            values.put("picturePath", picturePath);
            values.put("tag", tag);
            values.put("title", title);
            db.insert("tb_video", null, values);
            db.close();
        }
    }

    /**
     * 根据影视类型删除影视
     *
     */
    public static void deleteVideo() {
        SQLiteDatabase db = helper.getWritableDatabase();
        db.delete("tb_video", null, null);
        db.close();
    }

    /**
     * 获取每日推荐
     * @return
     */
    public static List<Video> getAllVideo() {
        SQLiteDatabase db = helper.getReadableDatabase();
        List<Video> instances = new ArrayList<Video>();
        Video instance = null;
        if (db.isOpen()) {
            Cursor cursor = db.rawQuery("Select * from tb_video", null);
            instances = new ArrayList<Video>();
            while (cursor.moveToNext()) {
                instance = new Video();
                instance.setVideoTypeId(cursor.getString(cursor.getColumnIndex("videoTypeId")));
                instance.setCreateTime(cursor.getString(cursor.getColumnIndex("createTime")));
                instance.setVideoId(cursor.getString(cursor.getColumnIndex("videoId")));
                instance.setDownImg(cursor.getString(cursor.getColumnIndex("downImg")));
                instance.setOrderNum(cursor.getString(cursor.getColumnIndex("orderNum")));
                instance.setPicturePath(cursor.getString(cursor.getColumnIndex("picturePath")));
                instance.setTag(cursor.getString(cursor.getColumnIndex("tag")));
                instance.setTitle(cursor.getString(cursor.getColumnIndex("title")));
                instances.add(instance);
            }
            cursor.close();
            db.close();
        }
        return instances;
    }

    public static Video findVideoByOrderId(String orderNum) {
        SQLiteDatabase db = helper.getReadableDatabase();
        Video instance = null;
        if (db.isOpen()) {
            Cursor cursor = db.rawQuery("Select * from tb_video where orderNum = ?", new String[]{orderNum});
            while (cursor.moveToNext()) {
                instance = new Video();
                instance.setVideoTypeId(cursor.getString(cursor.getColumnIndex("videoTypeId")));
                instance.setCreateTime(cursor.getString(cursor.getColumnIndex("createTime")));
                instance.setVideoId(cursor.getString(cursor.getColumnIndex("videoId")));
                instance.setDownImg(cursor.getString(cursor.getColumnIndex("downImg")));
                instance.setOrderNum(cursor.getString(cursor.getColumnIndex("orderNum")));
                instance.setPicturePath(cursor.getString(cursor.getColumnIndex("picturePath")));
                instance.setTag(cursor.getString(cursor.getColumnIndex("tag")));
                instance.setTitle(cursor.getString(cursor.getColumnIndex("title")));
            }
            cursor.close();
            db.close();
        }
        return instance;
    }

    public static void updateVideoByOrderId(Video v) {
        SQLiteDatabase db = helper.getWritableDatabase();
        if(db.isOpen()) {
            db.execSQL("update tb_video set videoTypeId=?,videoId=?,title=?,picturePath=? where orderNum = ?", new Object[]{v.getVideoTypeId(), v.getVideoId(), v.getTitle(), v.getPicturePath(), v.getOrderNum()});
        }
        db.close();
    }

    public static void addOrUpdateVideo(Video v) {
        SQLiteDatabase db = helper.getWritableDatabase();
        if(db.isOpen()) {
            Video video = findVideoByOrderId(v.getOrderNum());
            if (video == null) {
                addVideo(v);
            } else {
                if (!db.isOpen()) {
                   db = helper.getWritableDatabase();
                }
                if (v.getPicturePath().equals(video.getPicturePath())) {
                    if (StringUtils.isBlank(v.getDownImg())) {
                        db.execSQL("update tb_video set videoTypeId=?,videoId=?,title=?,picturePath=?,downImg=? where orderNum = ?", new Object[]{v.getVideoTypeId(), v.getVideoId(), v.getTitle(), v.getPicturePath(),video.getDownImg(), v.getOrderNum()});
                    } else {
                        db.execSQL("update tb_video set videoTypeId=?,videoId=?,title=?,picturePath=?,downImg=? where orderNum = ?", new Object[]{v.getVideoTypeId(), v.getVideoId(), v.getTitle(), v.getPicturePath(),v.getDownImg(), v.getOrderNum()});
                    }
                } else {
                    //需要重新下载图片
                    db.execSQL("update tb_video set videoTypeId=?,videoId=?,title=?,picturePath=?,downImg=? where orderNum = ?", new Object[]{v.getVideoTypeId(), v.getVideoId(), v.getTitle(), v.getPicturePath(), "",v.getOrderNum()});
                }
            }
        }
        db.close();
    }

    public static void updateVideoImageByOrderId(String downPath, String orderNum) {
        SQLiteDatabase db = helper.getWritableDatabase();
        if(db.isOpen()) {
            db.execSQL("update tb_video set downImg=? where orderNum = ?", new Object[]{downPath, orderNum});
        }
        db.close();
    }

    public static Video findVideoTypeByOrderId(String orderNum) {
        SQLiteDatabase db = helper.getReadableDatabase();
        Video instance = null;
        if (db.isOpen()) {
            Cursor cursor = db.rawQuery("Select * from tb_video where orderNum = ?", new String[]{orderNum});
            while (cursor.moveToNext()) {
                instance = new Video();
                instance.setVideoTypeId(cursor.getString(cursor.getColumnIndex("videoTypeId")));
                instance.setCreateTime(cursor.getString(cursor.getColumnIndex("createTime")));
                instance.setVideoId(cursor.getString(cursor.getColumnIndex("videoId")));
                instance.setDownImg(cursor.getString(cursor.getColumnIndex("downImg")));
                instance.setOrderNum(cursor.getString(cursor.getColumnIndex("orderNum")));
                instance.setPicturePath(cursor.getString(cursor.getColumnIndex("picturePath")));
                instance.setTag(cursor.getString(cursor.getColumnIndex("tag")));
                instance.setTitle(cursor.getString(cursor.getColumnIndex("title")));
            }
            cursor.close();
            db.close();
        }
        return instance;
    }

    /**
     * 添加或者保存影视分类
     * @param v
     */
    public static void addOrUpdateVideoType(VideoType.Data v) {
        SQLiteDatabase db = helper.getWritableDatabase();
        if(db.isOpen()) {
            VideoType.Data videoT = findVideoTypeById(v.getVideoTypeId());
            if (videoT == null) {
                addVideoType(v);
            } else {
                if (!db.isOpen()) {
                    db = helper.getWritableDatabase();
                }
                if (v.getPicturePath().equals(videoT.getPicturePath())) {
                    if (StringUtils.isBlank(v.getDownImg())) {
                        db.execSQL("update tb_videoVideo set title=?,picturePath=?,downImg=? where videoTypeId = ?", new Object[]{ v.getTitle(), v.getPicturePath(),videoT.getDownImg(),v.getVideoTypeId()});
                    } else {
                        db.execSQL("update tb_videoVideo set title=?,picturePath=?,downImg=? where videoTypeId = ?", new Object[]{ v.getTitle(), v.getPicturePath(),v.getDownImg(),v.getVideoTypeId()});
                    }
                } else {
                    //需要重新下载图片
                    db.execSQL("update tb_videoVideo set title=?,picturePath=?,downImg=? where videoTypeId = ?", new Object[]{ v.getTitle(), v.getPicturePath(), "",v.getVideoTypeId()});
                }
            }
        }
        db.close();
    }

    /**
     * 添加视频类型
     * @param v
     */
    private static void addVideoType(VideoType.Data v) {
        SQLiteDatabase db = helper.getWritableDatabase();
        if (db.isOpen()) {
            String videoTypeId = v.getVideoTypeId();
            String picturePath = v.getPicturePath();
            String title = v.getTitle();
            String downImg = v.getDownImg();
            ContentValues values = new ContentValues();
            values.put("videoTypeId", videoTypeId);
            values.put("picturePath", picturePath);
            values.put("title", title);
            values.put("downImg", downImg);
            db.insert("tb_videoVideo", null, values);
            db.close();
        }
    }

    /**
     * 通过类型ID查询
     * @param videoTypeId
     * @return
     */
    private static VideoType.Data findVideoTypeById(String videoTypeId) {
        SQLiteDatabase db = helper.getReadableDatabase();
        VideoType.Data instance = null;
        if (db.isOpen()) {
            Cursor cursor = db.rawQuery("Select * from tb_videoVideo where videoTypeId = ?", new String[]{videoTypeId});
            while (cursor.moveToNext()) {
                instance = new VideoType.Data();
                instance.setVideoTypeId(cursor.getString(cursor.getColumnIndex("videoTypeId")));
                instance.setPicturePath(cursor.getString(cursor.getColumnIndex("picturePath")));
                instance.setTitle(cursor.getString(cursor.getColumnIndex("title")));
                instance.setDownImg(cursor.getString(cursor.getColumnIndex("downImg")));
            }
            cursor.close();
            db.close();
        }
        return instance;
    }

    /**
     * 获取所有影视分类
     * @return
     */
    public static List<VideoType.Data> getAllVideoTypeList() {
        SQLiteDatabase db = helper.getReadableDatabase();
        List<VideoType.Data> instances = new ArrayList<VideoType.Data>();
        VideoType.Data instance = null;
        if (db.isOpen()) {
            Cursor cursor = db.rawQuery("Select * from tb_videoVideo", null);
            instances = new ArrayList<VideoType.Data>();
            while (cursor.moveToNext()) {
                instance = new VideoType.Data();
                instance.setVideoTypeId(cursor.getString(cursor.getColumnIndex("videoTypeId")));
                instance.setDownImg(cursor.getString(cursor.getColumnIndex("downImg")));
                instance.setPicturePath(cursor.getString(cursor.getColumnIndex("picturePath")));
                instance.setTitle(cursor.getString(cursor.getColumnIndex("title")));
                instances.add(instance);
            }
            cursor.close();
            db.close();
        }
        return instances;
    }

    public static void deleteVideoType() {
        SQLiteDatabase db = helper.getWritableDatabase();
        db.delete("tb_videoVideo", null, null);
        db.close();
    }

    /**
     * 添加影片播放记录
     *
     */
    public static void addMovieLog(VideoWatchHistory videoWatchHistory) {
        SQLiteDatabase db = helper.getWritableDatabase();
        if (db.isOpen()) {
            String movieId = videoWatchHistory.getVideoId();
            boolean contain = isContain(db, "tb_movie_log ", "movieId=?", new String[]{movieId});
            if (contain) {
                //更新
                LogUtils.d("gengxin", "更新");
                ContentValues values = new ContentValues();
                values.put("addTime", videoWatchHistory.getAddTime());
                values.put("movieId", movieId);
                values.put("movieName", videoWatchHistory.getMovieName());
                values.put("movieType", videoWatchHistory.getVideoTypeId());
                values.put("movieImage", videoWatchHistory.getMovieImage());
                values.put("serialsId", videoWatchHistory.getSerialEpisodeId());
                values.put("serialsPoint", videoWatchHistory.getVideoIndex());
                values.put("watchTime", videoWatchHistory.getWatchTime());
                values.put("b1", videoWatchHistory.getB1());
                db.update("tb_movie_log", values, "movieId=?", new String[]{movieId});
            } else {
                //添加
                LogUtils.d("gengxin", "添加");
                ContentValues values = new ContentValues();
                values.put("addTime", videoWatchHistory.getAddTime());
                values.put("movieId", movieId);
                values.put("movieName", videoWatchHistory.getMovieName());
                values.put("movieType", videoWatchHistory.getVideoTypeId());
                values.put("movieImage", videoWatchHistory.getMovieImage());
                values.put("serialsId", videoWatchHistory.getSerialEpisodeId());
                values.put("serialsPoint", videoWatchHistory.getVideoIndex());
                values.put("watchTime", videoWatchHistory.getWatchTime());
                values.put("b1", videoWatchHistory.getB1());
                db.insert("tb_movie_log", null, values);
            }
            db.close();
        }
    }

    /**
     * 添加影片播放记录集合
     *
     * @param moviesLogs
     */
    public static void addMovieLogs(List<VideoWatchHistory> moviesLogs) {
        SQLiteDatabase db = helper.getWritableDatabase();
        if (db.isOpen()) {
            for (VideoWatchHistory moviesLog : moviesLogs) {
                String movieId = moviesLog.getVideoId();
                boolean contain = isContain(db, "tb_movie_log ", "movieId=?", new String[]{movieId});
                if (contain) {
                    //更新
                    ContentValues values = new ContentValues();
                    values.put("addTime", moviesLog.getAddTime());
                    values.put("movieId", moviesLog.getVideoId());
                    values.put("movieName", moviesLog.getMovieName());
                    values.put("movieType", moviesLog.getVideoTypeId());
                    values.put("movieImage", moviesLog.getMovieImage());
                    values.put("serialsId", moviesLog.getSerialEpisodeId());
                    values.put("serialsPoint", moviesLog.getVideoIndex());
                    values.put("watchTime", moviesLog.getWatchTime());
                    values.put("b1", moviesLog.getB1());
                    db.update("tb_movie_log", values, "movieId=?", new String[]{ movieId});
                } else {
                    //添加
                    ContentValues values = new ContentValues();
                    values.put("addTime", moviesLog.getAddTime());
                    values.put("movieId", moviesLog.getVideoId());
                    values.put("movieName", moviesLog.getMovieName());
                    values.put("movieType", moviesLog.getVideoTypeId());
                    values.put("movieImage", moviesLog.getMovieImage());
                    values.put("serialsId", moviesLog.getSerialEpisodeId());
                    values.put("serialsPoint", moviesLog.getVideoIndex());
                    values.put("watchTime", moviesLog.getWatchTime());
                    values.put("b1", moviesLog.getB1());
                    db.insert("tb_movie_log", null, values);
                }
            }
            db.close();
        }
    }

    /**
     * 查询是否表内是否存在所在条目SQLiteDatabase 开启之后调用,本方法不关闭
     *
     * @param db         SQLiteDatabase
     * @param table      表名
     * @param conditions 查询条件
     * @param replace    条件数组
     * @return true 表内存在此条目,false 表内不存在此条目
     */
    public static boolean isContain(SQLiteDatabase db, String table, String conditions, String[] replace) {
        Cursor query = db.query(table, null, conditions, replace, null, null, null, null);
        if (query.getCount() > 0) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 根据影片Id和类型查询影片
     *
     * @param id
     * @return
     */
    public static MoviesLog findMoviesLogByMovieId(String id, String movieType) {
        SQLiteDatabase db = helper.getReadableDatabase();
        MoviesLog instance = null;
        if (db.isOpen()) {
            try{
                Cursor cursor = db.rawQuery("Select * from tb_movie_log where movieId=? and movieType=?", new String[]{id, movieType});
                while (cursor.moveToNext()) {
                    instance = new MoviesLog();
                    instance.setAddTime(cursor.getString(cursor.getColumnIndex("addTime")));
                    instance.setB1(cursor.getString(cursor.getColumnIndex("b1")));
                    instance.setId(cursor.getInt(cursor.getColumnIndex("id")));
                    instance.setMovieId(cursor.getString(cursor.getColumnIndex("movieId")));
                    instance.setMovieImage(cursor.getString(cursor.getColumnIndex("movieImage")));
                    instance.setMovieName(cursor.getString(cursor.getColumnIndex("movieName")));
                    instance.setMovieType(cursor.getString(cursor.getColumnIndex("movieType")));
                    instance.setSerialsId(cursor.getString(cursor.getColumnIndex("serialsId")));
                    instance.setWatchTime(cursor.getString(cursor.getColumnIndex("watchTime")));
                    instance.setSerialsPoint(cursor.getString(cursor.getColumnIndex("serialsPoint")));
                }
                cursor.close();
                db.close();
            } catch (Exception e) {
                e.printStackTrace();
                deleteMoviesLogByMovieId(id);
            }
        }
        return instance;
    }

    /**
     * 根据影片Id和类型查询影片
     *
     * @param id
     * @return
     */
    public static MoviesLog findMoviesLogByMovieId(String id, String movieType,int index) {
        SQLiteDatabase db = helper.getReadableDatabase();
        MoviesLog instance = null;
        if (db.isOpen()) {
            Cursor cursor = db.rawQuery("Select * from tb_movie_log where movieId=? and movieType=? and serialsPoint=?", new String[]{id, movieType,index + ""});
            while (cursor.moveToNext()) {
                instance = new MoviesLog();
                instance.setAddTime(cursor.getString(cursor.getColumnIndex("addTime")));
                instance.setB1(cursor.getString(cursor.getColumnIndex("b1")));
                instance.setId(cursor.getInt(cursor.getColumnIndex("id")));
                instance.setMovieId(cursor.getString(cursor.getColumnIndex("movieId")));
                instance.setMovieImage(cursor.getString(cursor.getColumnIndex("movieImage")));
                instance.setMovieName(cursor.getString(cursor.getColumnIndex("movieName")));
                instance.setMovieType(cursor.getString(cursor.getColumnIndex("movieType")));
                instance.setSerialsId(cursor.getString(cursor.getColumnIndex("serialsId")));
                instance.setWatchTime(cursor.getString(cursor.getColumnIndex("watchTime")));
                instance.setSerialsPoint(cursor.getString(cursor.getColumnIndex("serialsPoint")));
            }
            cursor.close();
            db.close();
        }
        return instance;
    }

    /**
     * 根据ID删除观看记录
     */
    public static void deleteMoviesLogByMovieId(String movieId) {
        SQLiteDatabase db = helper.getWritableDatabase();
        if (db.isOpen()) {
            LogUtils.e("DB","删除播放记录");
            db.delete("tb_movie_log ", "movieId = ?", new String[]{movieId});
            db.close();
        }
    }
}
