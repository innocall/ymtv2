package com.lemon95.ymtv.view.activity;

import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.lemon95.androidtvwidget.bridge.EffectNoDrawBridge;
import com.lemon95.androidtvwidget.bridge.OpenEffectBridge;
import com.lemon95.androidtvwidget.view.GridViewTV;
import com.lemon95.androidtvwidget.view.LabelView;
import com.lemon95.androidtvwidget.view.MainLayout;
import com.lemon95.androidtvwidget.view.MainUpView;
import com.lemon95.androidtvwidget.view.ReflectItemView;
import com.lemon95.ymtv.R;
import com.lemon95.ymtv.adapter.GenresMovieAdapter;
import com.lemon95.ymtv.bean.Favorite;
import com.lemon95.ymtv.bean.GenresMovie;
import com.lemon95.ymtv.bean.MoviesLog;
import com.lemon95.ymtv.bean.SerialDitions;
import com.lemon95.ymtv.common.AppConstant;
import com.lemon95.ymtv.db.DataBaseDao;
import com.lemon95.ymtv.presenter.MovieDetailsPresenter;
import com.lemon95.ymtv.utils.AppSystemUtils;
import com.lemon95.ymtv.utils.ImageUtils;
import com.lemon95.ymtv.utils.LogUtils;
import com.lemon95.ymtv.utils.PreferenceUtils;
import com.lemon95.ymtv.utils.StringUtils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by WXT on 2016/7/18.
 */
public class MovieDetailsActivity extends BaseActivity implements View.OnClickListener{

    private ReflectItemView details_play,details_serial,details_sc;
    private MainUpView mainUpView2;
    View mOldFocus; // 4.3以下版本需要自己保存.
    OpenEffectBridge mOpenEffectBridge;
    private ProgressBar lemon_movie_details_pro;
    private LinearLayout lemon_movie_details_main;
    private LabelView lemon_image_icon;
    private com.lemon95.ymtv.bean.Movie.Data data; //影片数据
    private List<GenresMovie.Data> videoList = new ArrayList<>(); //相关影视
    private MovieDetailsPresenter movieDetailsActivity = new MovieDetailsPresenter(this);
    private boolean isKeyDown = false;
    private SerialDitions.Data serialData; //电视剧数据数据
    String videoType = "1";
    private String userId;
    private DisplayImageOptions options;
    private String videoId;
    private Boolean isPersonal = false;
    private GridViewTV gridView;
    private GenresMovieAdapter genresMovieAdapter;
    private boolean isStart = false;
    private int point = 0;  //gridview 位置
    private DataBaseDao dataBaseDao;
    private int index = 1; //第几集

    @Override
    protected int getLayoutId() {
        return R.layout.activity_moviedetails;
    }

    @Override
    protected void setupViews() {
        initViewMove();
        initView();
        initOnclick();
    }

    private void initView() {
        options = new DisplayImageOptions.Builder()
                .showStubImage(R.drawable.lemon_details_small_def)          // 设置图片下载期间显示的图片
                .showImageForEmptyUri(R.drawable.lemon_details_small_def)  // 设置图片Uri为空或是错误的时候显示的图片
                .showImageOnFail(R.drawable.lemon_details_small_def)       // 设置图片加载或解码过程中发生错误显示的图片
                .build();
        gridView = (GridViewTV) findViewById(R.id.gridView);
        gridView.setIsSearch(true);
        genresMovieAdapter = new GenresMovieAdapter(videoList,context);
        gridView.setAdapter(genresMovieAdapter);
        gridView.setSelector(new ColorDrawable(Color.TRANSPARENT));
        lemon_movie_details_pro = (ProgressBar)findViewById(R.id.lemon_movie_details_pro);
        lemon_movie_details_main = (LinearLayout)findViewById(R.id.lemon_movie_details_main);
        details_serial = (ReflectItemView) findViewById(R.id.details_serial);
        details_sc = (ReflectItemView) findViewById(R.id.details_sc);
        lemon_image_icon = (LabelView) findViewById(R.id.lemon_image_icon);
        dataBaseDao = new DataBaseDao(context);
    }

    @Override
    protected void initialized() {
        isPersonal = getIntent().getBooleanExtra("isPersonal", false);
        videoId = getIntent().getStringExtra("videoId");
        userId = PreferenceUtils.getString(context, AppConstant.USERID, ""); //获取用户ID
        videoType = getIntent().getStringExtra("videoType");
        String isNew = getIntent().getStringExtra("isNew");
        if("true".equalsIgnoreCase(isNew)) {
            lemon_image_icon.setVisibility(View.VISIBLE);
        } else {
            lemon_image_icon.setVisibility(View.GONE);
        }
        TextView textView = (TextView)findViewById(R.id.lemon95_movie_title_id);
        LogUtils.i(TAG, videoId + ";" + userId);
        if (AppConstant.MOVICE.equals(videoType)) {
            textView.setText(getString(R.string.lemon95_movie));
            movieDetailsActivity.initPageData(videoId, userId,isPersonal);
            details_serial.setVisibility(View.GONE);
        } else if(AppConstant.SERIALS.equals(videoType)) {
            textView.setText("电视剧");
            details_serial.setVisibility(View.VISIBLE);
            movieDetailsActivity.initSerialData(videoId,userId);
        } else if ( AppConstant.ZONGYI.equals(videoType)) {
            textView.setText("综艺");
            details_serial.setVisibility(View.VISIBLE);
            movieDetailsActivity.initSerialData(videoId,userId);
        } else if ( AppConstant.DONGMAN.equals(videoType)) {
            textView.setText("动漫");
            details_serial.setVisibility(View.VISIBLE);
            movieDetailsActivity.initSerialData(videoId,userId);
        }
    }

    public void initViewMove() {
        switchNoDrawBridgeVersion();
        /*if (Utils.getSDKVersion() == 17) { // 测试 android 4.2版本.
        } else { // 其它版本（android 4.3以上）.
            mainUpView1.setUpRectResource(R.drawable.test_rectangle); // 设置移动边框的图片.
            mainUpView1.setShadowResource(R.drawable.item_shadow); // 设置移动边框的阴影.
            mainUpView2.setUpRectResource(R.drawable.test_rectangle); // 设置移动边框的图片.
            mainUpView2.setShadowResource(R.drawable.item_shadow); // 设置移动边框的阴影.
        }*/
        MainLayout main_lay11 = (MainLayout) findViewById(R.id.main_lay);
        main_lay11.getViewTreeObserver().addOnGlobalFocusChangeListener(new ViewTreeObserver.OnGlobalFocusChangeListener() {
            @Override
            public void onGlobalFocusChanged(final View oldFocus, final View newFocus) {
                if (newFocus != null)
                    newFocus.bringToFront(); // 防止放大的view被压在下面. (建议使用MainLayout)
                float scale = 1.1f;
                mainUpView2.setFocusView(newFocus, mOldFocus, scale);
                mOldFocus = newFocus; // 4.3以下需要自己保存.
                // 测试是否让边框绘制在下面，还是上面. (建议不要使用此函数)
                /*if (newFocus != null) {
                    testTopDemo(newFocus, scale);
                }*/
                LogUtils.i(TAG,"大小改变");
            }
        });
        //初始化焦点
        details_play = (ReflectItemView)findViewById(R.id.details_play);
        mainUpView2.setFocusView(details_play, mOldFocus, 1.1f);
        mOldFocus = details_play;
        details_play.setFocusableInTouchMode(true);
        details_play.requestFocus();
        details_play.setFocusable(true);
        mOpenEffectBridge.setVisibleWidget(false);
        mainUpView2.setUpRectResource(R.drawable.test_rectangle); // 设置移动边框的图片.
    }

    public void testTopDemo(View newView, float scale) {
        if (newView.getId() == R.id.details_play || newView.getId() == R.id.details_sc || newView.getId() == R.id.details_serial) { // 小人在外面的测试.
            LogUtils.e(TAG,"隐藏");
            mOpenEffectBridge.setVisibleWidget(false);
            mainUpView2.setUpRectResource(R.drawable.test_rectangle); // 设置移动边框的图片.
        } else {
            mOpenEffectBridge.setVisibleWidget(true);
            mainUpView2.setUpRectResource(R.drawable.health_focus_border); // 设置移动边框的图片.
            /*if (newView instanceof  TextView) {
                ((TextView)newView).setEllipsize(TextUtils.TruncateAt.MARQUEE);
                ((TextView)newView).setSingleLine(true);
                ((TextView)newView).setMarqueeRepeatLimit(6);
            }
            LogUtils.e(TAG, "显示");*/
        }
    }

    private void switchNoDrawBridgeVersion() {
        mainUpView2 = (MainUpView) findViewById(R.id.mainUpView2);
        mOpenEffectBridge = (OpenEffectBridge) mainUpView2.getEffectBridge();
        EffectNoDrawBridge effectNoDrawBridge = new EffectNoDrawBridge();
        effectNoDrawBridge.setTranDurAnimTime(10);
        mainUpView2.setEffectBridge(effectNoDrawBridge); // 4.3以下版本边框移动.
        mainUpView2.setUpRectResource(R.drawable.health_focus_border); // 设置移动边框的图片.
        mainUpView2.setDrawUpRectPadding(new Rect(7, -22, 0, -30)); // 边框图片设置间距.
    }

    /**
     * 显示页面数据
     * @param data
     */
    public void initViewMoveDate(com.lemon95.ymtv.bean.Movie.Data data) {
        this.data = data;
        ((TextView)findViewById(R.id.movie_details_name)).setText("《" + data.getMovieName() + "》");
        ((TextView)findViewById(R.id.movie_details_type)).setText("类型：" + data.getVideoGenres());
        String f = data.getScore();
        if (StringUtils.isNotBlank(f)) {
            f = f.substring(0,3);
        }
        ((TextView)findViewById(R.id.movie_details_grade)).setText("评分：" + f);
        ((TextView)findViewById(R.id.movie_details_direct)).setText("导演：" + data.getDirector());
        ((TextView)findViewById(R.id.movie_details_act)).setText("主演：" + data.getStarring());
        ((TextView)findViewById(R.id.movie_details_descri)).setText(data.getDescription());
        ImageView imageView = (ImageView)findViewById(R.id.movie_details_img_id);
        LogUtils.e(TAG, ImageUtils.getBigImg(data.getPicturePath()));
        ImageLoader.getInstance().displayImage(AppConstant.RESOURCE + ImageUtils.getBigImg(data.getPicturePath()), imageView, options);
    }

    public void showPro() {
        lemon_movie_details_pro.setVisibility(View.VISIBLE);
        lemon_movie_details_main.setVisibility(View.GONE);
    }

    public void hidePro() {
        lemon_movie_details_pro.setVisibility(View.GONE);
        lemon_movie_details_main.setVisibility(View.VISIBLE);
        isKeyDown = false;
    }

    /**
     * 显示相关影视
     * @param dataList
     */
    public void initViewByGenresMoveDate(List<GenresMovie.Data> dataList) {
        if (dataList != null) {
            this.videoList.clear();
            this.videoList.addAll(dataList);
            genresMovieAdapter.notifyDataSetChanged();
        }
    }

    private void initOnclick() {
        details_play.setOnClickListener(this);
        details_serial.setOnClickListener(this);
        details_sc.setOnClickListener(this);
        gridView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                /**
                 * 这里注意要加判断是否为NULL.
                 * 因为在重新加载数据以后会出问题.
                 */
                LogUtils.i(TAG, "焦点改变" + position);
               /* if (19 < Build.VERSION.SDK_INT) {
                    isStart = true;
                }*/
                if (view != null && isStart) {
                    view.bringToFront();
                    mainUpView2.setFocusView(view, mOldFocus, 1.1f);
                }
                point = position;
                mOldFocus = view;
                isStart = true;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        gridView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    //if (gridView.getChildCount() > 0) {
                    mainUpView2.setUnFocusView(gridView);
                    mainUpView2.setUpRectResource(R.drawable.health_focus_border);
                    // gridView.setSelection(0);
                    myHandler.postAtFrontOfQueue(new Runnable() {
                        public void run() {
                            LogUtils.i(TAG, "空");
                            gridView.setSelection(point);
                            mOldFocus = gridView.getChildAt(point);
                            mainUpView2.setFocusView(mOldFocus, 1.1f);
                        }
                    });
                    // View newView = gridView.getChildAt(0);
                    // newView.bringToFront();
                    // mainUpView2.setFocusView(newView, 1.1f);
                    // mOldFocus = gridView.getChildAt(0);
                    // }
                    LogUtils.i(TAG, "gridView 获取焦点");
                } else {
                    mOpenEffectBridge.setVisibleWidget(true); // 隐藏
                    mainUpView2.setUpRectResource(R.drawable.test_rectangle); // 设置移动边框的图片.
                    mainUpView2.setUnFocusView(mOldFocus);
                }
            }
        });
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                isKeyDown = true;
                isPersonal = false;
                videoId = videoList.get(position).getVideoId();
                face(videoList.get(position), videoId);
                lemon_movie_details_pro.setVisibility(View.VISIBLE);
            }
        });
    }

    private Handler myHandler = new Handler();

    @Override
    public void onClick(View v) {
        if (lemon_movie_details_pro.getVisibility() == View.GONE) {
            switch (v.getId()) {
                case R.id.details_play:
                    Bundle bundle = new Bundle();
                    if (AppConstant.MOVICE.equals(videoType)) {
                        bundle.putString("videoId", data.getId());
                        bundle.putString("SerialEpisodeId", "");
                        bundle.putBoolean("isPersonal", isPersonal);
                        bundle.putString("videoName", data.getMovieName());
                        bundle.putString("videoImage",data.getPicturePath());
                    } else if(AppConstant.SERIALS.equals(videoType) || AppConstant.DONGMAN.equals(videoType) || AppConstant.ZONGYI.equals(videoType)) {
                        bundle.putString("SerialEpisodeId", serialData.getSerialEpisodes().get(0).getId());  //剧集ID
                        bundle.putString("videoId", serialData.getSerialEpisodes().get(0).getSerialId());
                        bundle.putString("videoName", serialData.getSerialName());
                        bundle.putString("lastEpisode", serialData.getLastEpisode());
                       // bundle.putInt("index", index);
                        bundle.putString("videoImage", serialData.getPicturePath());
                     //   bundle.putParcelableArrayList("SerialEpisodes",serialData.getSerialEpisodes());
                    }
                    bundle.putString("videoType", videoType);
                  //  startActivity(PlayActivity.class,bundle);
                    startActivity(BdPalyActivity.class,bundle);
                    break;
                case R.id.details_serial:
                    //选择剧集
                    Bundle bundle1 = new Bundle();
                   // bundle1.putParcelableArrayList("SerialEpisodes",serialData.getSerialEpisodes());
                   // bundle1.putParcelableArrayList("SerialEpisodes",serialData.getLastEpisode());
                    bundle1.putString("PicturePath", serialData.getPicturePath());
                    bundle1.putString("lastEpisode", serialData.getLastEpisode());
                    bundle1.putString("videoName", serialData.getSerialName());
                    bundle1.putString("SerialEpisodeId", serialData.getSerialEpisodes().get(0).getId());  //剧集ID
                    bundle1.putString("videoId", serialData.getSerialEpisodes().get(0).getSerialId());
                    bundle1.putString("videoImage", serialData.getPicturePath());
                    bundle1.putString("videoType", videoType);
                    startActivity(SerialActivity.class,bundle1);
                    break;
                case R.id.details_sc:
                    //添加收藏
                    isKeyDown = true;
                    lemon_movie_details_pro.setVisibility(View.VISIBLE);
                    String deviceId = AppSystemUtils.getDeviceId();
                    Favorite favorite = new Favorite();
                    favorite.setMAC(deviceId);
                    favorite.setUserId(userId);
                    favorite.setVideoId(videoId);
                    favorite.setVideoTypeId(videoType);
                    movieDetailsActivity.addFavorite(favorite);
                    break;
            }
        }
    }

    public void face(GenresMovie.Data data ,String videoId) {
        if (AppConstant.SERIALS.equals(data.getVideoTypeId())|| AppConstant.ZONGYI.equals(data.getVideoTypeId())||AppConstant.DONGMAN.equals(data.getVideoTypeId())) {
            movieDetailsActivity.initSerialData(videoId,userId);
        } else {
            movieDetailsActivity.initPageData(videoId, userId, false);
        }
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        /*if(keyCode==KeyEvent.KEYCODE_DPAD_UP){
        } else if (keyCode==KeyEvent.KEYCODE_DPAD_LEFT) {
        } else if (keyCode==KeyEvent.KEYCODE_DPAD_RIGHT) {
        } else if (keyCode==KeyEvent.KEYCODE_DPAD_DOWN) {
        } else if (keyCode==KeyEvent.KEYCODE_DPAD_CENTER||keyCode==KeyEvent.KEYCODE_ENTER) {
        }*/
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return super.onKeyDown(keyCode, event);
        }
        return isKeyDown;
    }

    /**
     * 初始化电视剧数据
     * @param data
     */
    public void initViewSerialDate(SerialDitions.Data data) {
        this.serialData = data;
        //获取播放记录
        if(AppConstant.SERIALS.equals(videoType) || AppConstant.ZONGYI.equals(videoType) || AppConstant.DONGMAN.equals(videoType)) {
            MoviesLog moviesLog = dataBaseDao.findMoviesLogByMovieId(videoId, videoType);
            if (moviesLog != null && StringUtils.isNotBlank(moviesLog.getSerialsPoint())) {
                index = Integer.parseInt(moviesLog.getSerialsPoint());
                if (index <=0) {
                    index = 1;
                }
            } else {
                index = 1;
            }
           // ((TextView)findViewById(R.id.lemon95_text_play)).setText("第"+ index +"集");
        }
        ((TextView)findViewById(R.id.movie_details_name)).setText("《" + data.getSerialName() + "》");
        ((TextView)findViewById(R.id.movie_details_type)).setText("类型：" + data.getVideoGenres());
        String f = data.getScore();
        if (StringUtils.isNotBlank(f)) {
            f = f.substring(0,3);
        }
        ((TextView)findViewById(R.id.movie_details_grade)).setText("评分：" + f);
        String daoyan = data.getDirector();
        if ("".equals(daoyan)) {
            ((TextView)findViewById(R.id.movie_details_direct)).setText("导演：" + daoyan);
        } else {
            ((TextView)findViewById(R.id.movie_details_direct)).setText("导演：" + daoyan);
        }
        String zhuyan = data.getStarring();
        if ("".equals(zhuyan)) {
            ((TextView)findViewById(R.id.movie_details_act)).setText("主演：" + zhuyan);
        } else {
            ((TextView)findViewById(R.id.movie_details_act)).setText("主演：" + zhuyan);
        }
        ((TextView)findViewById(R.id.movie_details_descri)).setText(data.getDescription());
        ImageView imageView = (ImageView)findViewById(R.id.movie_details_img_id);
        LogUtils.e(TAG, ImageUtils.getBigImg(data.getPicturePath()));
        ImageLoader.getInstance().displayImage(AppConstant.RESOURCE + ImageUtils.getBigImg(data.getPicturePath()), imageView,options);
    }
}
