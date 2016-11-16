package com.lemon95.ymtv.view.activity;


import android.app.Service;
import android.graphics.Color;
import android.media.AudioManager;
import android.os.*;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.cyberplayer.core.BVideoView;
import com.lemon95.ymtv.R;
import com.lemon95.ymtv.adapter.MyWheelAdapter;
import com.lemon95.ymtv.bean.MoviesLog;
import com.lemon95.ymtv.bean.SerialDitions;
import com.lemon95.ymtv.bean.VideoWatchHistory;
import com.lemon95.ymtv.common.AppConstant;
import com.lemon95.ymtv.db.DataBaseDao;
import com.lemon95.ymtv.myview.VerticalProgressBar;
import com.lemon95.ymtv.presenter.BdPlayMoviePresenter;
import com.lemon95.ymtv.utils.AppSystemUtils;
import com.lemon95.ymtv.utils.LogUtils;
import com.lemon95.ymtv.utils.MenuDataUtils;
import com.lemon95.ymtv.utils.PreferenceUtils;
import com.lemon95.ymtv.utils.StringUtils;
import com.wx.wheelview.widget.WheelView;

/**
 * Created by wuxiaotie on 2016/11/2.
 */
public class BdPalyActivity extends BaseActivity implements BVideoView.OnPreparedListener,
        BVideoView.OnCompletionListener,
        BVideoView.OnErrorListener,
        BVideoView.OnInfoListener,
        BVideoView.OnPlayingBufferCacheListener,
        BVideoView.OnCompletionWithParamListener {
    private HandlerThread mHandlerThread;
    private EventHandler mEventHandler;
    private final int EVENT_PLAY = 0;
    private final int UI_EVENT_UPDATE_CURRPOSITION = 1;
    private final static int VOLUME_HIDE = 2;
    private final static int HIDE_LOAD = 3;
    private final static int SHOW_LOAD = 4;
    private final static int LOAD = 5;
    private static final int LOAD_SEEP = 6;
    private static final int SHOW_TITLE = 7; //显示头部和底部
    private static final int HIDE_TITLE = 8; //隐藏头部和底部
    private static final int VIDEO_INFO = 9; //视频支持码率
    private static final long DEFAULT_TIME_OUT = 8000; //显示时间
    private BVideoView mVV = null;
    private LinearLayout mController = null;
    private SeekBar mProgress = null;
    private TextView mDuration = null;
    private TextView mCurrPostion = null;
    private TextView lemon95_menus;
    private BdPlayMoviePresenter playMoviePresenter = new BdPlayMoviePresenter(this);
    private String AK = "813fa4bc60594baebbf362a9ba8b849d";   // 请录入您的AK !!!
    private String mVideoSource = null;  //播放地址
    private String videoId;
    private String videoType;
//    private String SerialEpisodeId;
    private Boolean isPersonal;
    private TextView lemon_pay_title;
    private int index = 1;  //当前第几集
   // private ArrayList<SerialDitions.Data.SerialEpisodes> serialEpisodes; //电视剧剧集
    private String lastEpisode = "1";
    private DataBaseDao dataBaseDao;
    public boolean isPro = false;
    private boolean mIsHwDecode = true;
    private boolean barShow = true;
    private long waitTime = 2000;
    private long touchTime = 0;
    private LinearLayout lemon_volume;
    private ImageView lemon_volume_img;
    private VerticalProgressBar lemon_volume_seek;
    private AudioManager mAM;
    private int mMaxVolume;
    private int mVolume = 0;
    private ImageView lemon_play_img;
    private RelativeLayout lemon95_loads; //加载
    private RelativeLayout lemon95_play_title;
    private TextView sdk_ijk_progress_bar_text; //加载文字
    private TextView lemon95_seep; //加载网速
    private DrawerLayout lemon95_play_menu;
    private LinearLayout lemon95_menu_end;
    private WheelView mainWheelView,subWheelView;
    private int jiema = 0,huafu = 0,shengyi=0; //记录菜单位置
    /**
     * 播放状态
     */
    private  enum PLAYER_STATUS {
        PLAYER_IDLE, PLAYER_PREPARING, PLAYER_PREPARED,
    }
    private PLAYER_STATUS mPlayerStatus = PLAYER_STATUS.PLAYER_IDLE;
    /**
     * 记录播放位置
     */
    private int mLastPos = 0;
    private final Object SYNC_Playing = new Object();

    private Handler mHandler = new Handler(){

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case VOLUME_HIDE:
                    lemon_volume.setVisibility(View.GONE);
                    break;
                case HIDE_LOAD:
                    lemon95_loads.setVisibility(View.GONE);
                    break;
                case SHOW_LOAD:
                    lemon95_loads.setVisibility(View.VISIBLE);
                    break;
                case LOAD:
                   // lemon95_loads.setVisibility(View.VISIBLE);
                    String i = msg.getData().getString("load","0");
                    sdk_ijk_progress_bar_text.setText("加载进度" + i + "%");
                    break;
                case LOAD_SEEP:
                    String see = msg.getData().getString("loads","0");
                    lemon95_seep.setText(see + "kb/s");
                    break;
                case SHOW_TITLE:
                    mController.setVisibility(View.VISIBLE);
                    lemon95_play_title.setVisibility(View.VISIBLE);
                    break;
                case HIDE_TITLE:
                    mController.setVisibility(View.GONE);
                    lemon95_play_title.setVisibility(View.GONE);
                    break;
//                case VIDEO_INFO:
//                    if (mAvailableBitrateKb != null) {
//                        for (int j=0; j<mAvailableBitrateKb.length; j++) {
//                            LogUtils.e("mAvailableBitrateKb",mAvailableBitrateKb[j] + "");
//                        }
//                    }
//                    if (mAvailableResolution != null) {
//                        for (int j=0; j<mAvailableResolution.length; j++) {
//                            LogUtils.e("mAvailableResolution",mAvailableResolution[j] + "");
//                        }
//                    }
//                    break;
            }
        }
    };

    class EventHandler extends Handler {
        public EventHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case EVENT_PLAY:
                    //如果已经播放了，等待上一次播放结束
                    if (mPlayerStatus != PLAYER_STATUS.PLAYER_IDLE) {
                        synchronized (SYNC_Playing) {
                            try {
                                SYNC_Playing.wait();
                                Log.v(TAG, "wait player status to idle");
                            } catch (InterruptedException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                        }
                    }
                    //设置播放url
                    mVV.setVideoPath(mVideoSource);
                    //续播，如果需要如此
                    if (mLastPos > 0) {
                        mVV.seekTo(mLastPos);
                        showMsg("定位到上次播放位置");
                        mLastPos = 0;
                    }
                    //显示或者隐藏缓冲提示
                    mVV.showCacheInfo(false);
                    //开始播放
                    mVV.start();
                    mPlayerStatus = PLAYER_STATUS.PLAYER_PREPARING;
                    break;
                default:
                    break;
            }
        }
    }

    Handler mUIHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                //更新进度及时间
                case UI_EVENT_UPDATE_CURRPOSITION:
                    int currPosition = mVV.getCurrentPosition();
                    int duration = mVV.getDuration();
                    updateTextViewWithTimeFormat(mDuration, duration);
                    mProgress.setMax(duration);
                    if (mVV.isPlaying() && isParam2){
                        updateTextViewWithTimeFormat(mCurrPostion, currPosition);
                        mProgress.setProgress(currPosition);
                    }
                    mUIHandler.sendEmptyMessageDelayed(UI_EVENT_UPDATE_CURRPOSITION, 200);
                    if (currPosition + 2 > duration) {
                        isParam = true;
                    } else {
                        isParam = false;
                    }
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected int getLayoutId() {
        return R.layout.dbplaying;
    }

    @Override
    protected void setupViews() {
       // serialEpisodes = getIntent().getParcelableArrayListExtra("SerialEpisodes");
        lastEpisode = getIntent().getStringExtra("lastEpisode"); //多少集
        if (StringUtils.isBlank(lastEpisode)) {
            lastEpisode = "0";
        }
        isPersonal = getIntent().getBooleanExtra("isPersonal", false);
        videoId = getIntent().getStringExtra("videoId");
        videoType = getIntent().getStringExtra("videoType");
       // SerialEpisodeId = getIntent().getStringExtra("SerialEpisodeId");
        //获取播放记录
        dataBaseDao = new DataBaseDao(context);
        MoviesLog moviesLog = null;
        moviesLog = dataBaseDao.findMoviesLogByMovieId(videoId, videoType);
        int logIndex = 0;
        index = getIntent().getIntExtra("index", logIndex);
        if (moviesLog != null && StringUtils.isNotBlank(moviesLog.getWatchTime())) {
            if (!AppConstant.MOVICE.equals(videoType)) {
                logIndex = Integer.parseInt(moviesLog.getSerialsPoint());
                LogUtils.e("上次播放：",logIndex + "集");
            }
            if (index == logIndex || index == 0) {
                mLastPos = Integer.parseInt(moviesLog.getWatchTime()) / 1000;
                index = logIndex;
                LogUtils.e("上次播放时间：",mLastPos + "");
            }
        }
        if(index == 0) {
            index = 1;
        }
        /*if (!AppConstant.MOVICE.equals(videoType)) {
            SerialEpisodeId = serialEpisodes.get(index).getId();
        }*/
        LogUtils.e("播放：",index + "集");
        initUI();
        //开启后台事件处理线程
        mHandlerThread = new HandlerThread("event handler thread",
                android.os.Process.THREAD_PRIORITY_BACKGROUND);
        mHandlerThread.start();
        mEventHandler = new EventHandler(mHandlerThread.getLooper());
    }

    @Override
    protected void initialized() {
        //获取播放地址
        if (AppConstant.MOVICE.equals(videoType) || AppConstant.FUNNY.equals(videoType)) {
            //playMoviePresenter.getPlayUrl(videoId);
            String userId = PreferenceUtils.getString(context, AppConstant.USERID, "");
            playMoviePresenter.initPageData(videoId, userId, isPersonal);
        } else if(AppConstant.SERIALS.equals(videoType)||AppConstant.ZONGYI.equals(videoType)||AppConstant.DONGMAN.equals(videoType)) {
            playMoviePresenter.getPlaySerialUrl(videoId,index);
        }
    }

    /**
     * 初始化界面
     */
    private void initUI() {
        mController = (LinearLayout)findViewById(R.id.controlbar);
        mProgress = (SeekBar)findViewById(R.id.media_progress);
        mDuration = (TextView)findViewById(R.id.time_total);
        mCurrPostion = (TextView)findViewById(R.id.time_current);
        lemon95_seep = (TextView)findViewById(R.id.lemon95_seep);
        lemon95_menus = (TextView)findViewById(R.id.lemon95_menus);
        lemon95_play_menu = (DrawerLayout)findViewById(R.id.lemon95_play_menu);
        sdk_ijk_progress_bar_text = (TextView) findViewById(R.id.sdk_ijk_progress_bar_text);
        lemon_volume_seek = (VerticalProgressBar) findViewById(R.id.lemon_volume_seek);
        mAM = (AudioManager) getSystemService(Service.AUDIO_SERVICE);
        mMaxVolume = mAM.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        lemon_volume = (LinearLayout)findViewById(R.id.lemon_volume);
        lemon95_menu_end = (LinearLayout)findViewById(R.id.lemon95_menu_end);
        lemon_volume_img = (ImageView)findViewById(R.id.lemon_volume_img);
        lemon_play_img = (ImageView)findViewById(R.id.lemon_play_img);
        lemon95_loads = (RelativeLayout)findViewById(R.id.lemon95_loads);
        lemon95_play_title = (RelativeLayout)findViewById(R.id.lemon95_play_title);
        mainWheelView = (WheelView) findViewById(R.id.main_wheelview);
        mainWheelView.setWheelAdapter(new MyWheelAdapter(this));
        mainWheelView.setWheelSize(9);
        mainWheelView.setSkin(WheelView.Skin.Holo);
        mainWheelView.setWheelData(MenuDataUtils.createMainDatas(videoType));
        //mainWheelView.setSelection(2);
        WheelView.WheelViewStyle style = new WheelView.WheelViewStyle();
        style.backgroundColor = Color.parseColor("#00000000");
        style.holoBorderColor = Color.parseColor("#3F51B5");
        style.textColor = Color.WHITE;
        style.textSize = 26;
        mainWheelView.setLoop(false); //是否循环显示
        style.selectedTextColor = Color.WHITE;
       // style.selectedTextZoom = 1.05f;
        mainWheelView.setStyle(style);

        subWheelView = (WheelView) findViewById(R.id.sub_wheelview);
        subWheelView.setWheelAdapter(new MyWheelAdapter(this));
        subWheelView.setSkin(WheelView.Skin.Holo);
        subWheelView.setWheelSize(9);
        subWheelView.setItemsCanFocus(false);
      /*  if (!videoType.equals(AppConstant.MOVICE)) {
            subWheelView.setWheelSize(Integer.parseInt(lastEpisode));
        }*/
        subWheelView.setWheelData(MenuDataUtils.createSubDatas(videoType,Integer.parseInt(lastEpisode)).get(MenuDataUtils.createMainDatas(videoType).get(mainWheelView.getSelection())));
        WheelView.WheelViewStyle style2 = new WheelView.WheelViewStyle();
        style2.backgroundColor = Color.parseColor("#00000000");
        style2.holoBorderColor = Color.parseColor("#00000000");
        style2.textColor = Color.WHITE;
        style2.textSize = 26;
        style2.selectedTextColor = Color.parseColor("#eda1dffc");
        subWheelView.setStyle(style2);
        subWheelView.setLoop(false); //是否循环显示
        mainWheelView.join(subWheelView);
        mainWheelView.joinDatas(MenuDataUtils.createSubDatas(videoType, Integer.parseInt(lastEpisode)));
        subWheelView.setFocusableInTouchMode(false);
        subWheelView.setFocusable(false);
        mainWheelView.setFocusable(true);
        mainWheelView.setFocusableInTouchMode(true);
        lemon95_play_menu.setDrawerListener(drawerLister);
        lemon_volume_seek.setMax(mMaxVolume);
        mVolume = mAM.getStreamVolume(AudioManager.STREAM_MUSIC);
        lemon_volume_seek.setProgress(mVolume);
        registerCallbackForControl();
        BVideoView.setAK(AK);//设置ak
        mVV = (BVideoView) findViewById(R.id.video_view); //获取BVideoView对象
        //注册listener
        mVV.setOnPreparedListener(this);   //媒体源已准备好,即将播放时被调用的回调函数接口定义
        mVV.setOnCompletionListener(this);  //媒体源播放结束后被调用的回调函数接口定义
        mVV.setOnErrorListener(this);     //异步操作时，如果有错误发生时被调用的回调函数接口定义，（有的函数调用时会抛出异常)
        mVV.setOnInfoListener(this);    //接收关于媒体或播放的信息(和／或)警告的回调函数接口定义
        //设置解码模式
      //  mVV.setDecodeMode(mIsHwDecode ? BVideoView.DECODE_HW : BVideoView.DECODE_SW);
       // mVV.setWatermarkText("懒猫TV");  //跑马灯
        mVV.setCacheTime(3); //播放网络视频时自定义缓冲等待的最大时长（单位：秒），没有设置的情况下，默认为1秒，最小可设置为0.5秒，最大可设置为4秒。
       // mVV.setCacheBufferSize(50 * 1024 * 1024); //播放网络视频时自定义网络缓冲区的大小，如无特殊需求时不建议设置。
        mVV.setDecodeMode(BVideoView.DECODE_SW);
        mVV.setVideoScalingMode(BVideoView.VIDEO_SCALING_MODE_SCALE_TO_FIT); //视频缩放模式VIDEO_SCALING_MODE_SCALE_TO_FIT(视频原大小) VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING
        mVV.setRetainLastFrame(true); //启用或禁用保留最后呈现的视频帧
        mVV.setEnableFastStart(false);
        mVV.setEnableDolby(true);
        mVV.setOnTotalCacheUpdateListener(new BVideoView.OnTotalCacheUpdateListener() {
            @Override
            public void onTotalCacheUpdate(long l) {
               // LogUtils.e("缓存大小", l + "s");
                int s = (int) l;
                mProgress.setSecondaryProgress(s);
            }
        });
        mVV.setOnPlayingBufferCacheListener(new BVideoView.OnPlayingBufferCacheListener() {
            @Override
            public void onPlayingBufferCache(int i) {
               // LogUtils.e("缓存百分比", i + "");
                Message msg = new Message();
                msg.what = LOAD;
                Bundle bundle = new Bundle();
                bundle.putString("load", i + "");
                msg.setData(bundle);
                mHandler.sendMessage(msg);
            }
        });
        mVV.setOnNetworkSpeedListener(new BVideoView.OnNetworkSpeedListener() {
            @Override
            public void onNetworkSpeedUpdate(int i) {
                Message msg = new Message();
                msg.what = LOAD_SEEP;
                Bundle bundle = new Bundle();
                bundle.putString("loads", i / 1024 + "");
                msg.setData(bundle);
                mHandler.sendMessage(msg);
              //  LogUtils.e("网速", i / 1024 + "kb/s");
            }
        });
      /*  mVV.setOnPositionUpdateListener(new BVideoView.OnPositionUpdateListener() {
            @Override
            public boolean onPositionUpdate(long l) {
                //  LogUtils.e("接收当前播放时长的回调函数接口定义", l/1000 + "s");
                return false;
            }
        });*/
       /* mVV.setOnSeekCompleteListener(new BVideoView.OnSeekCompleteListener() {
            @Override
            public void onSeekComplete() {
                LogUtils.e("----","跳位置");
            }
        });*/
        mainWheelView.setOnWheelItemSelectedListener(new WheelView.OnWheelItemSelectedListener() {
            @Override
            public void onItemSelected(int position, Object o) {
                LogUtils.e("1菜单位置:", position + "");
                String s = (String) o;
                if (s == null) {
                    return;
                }
                lemon95_menus.setText(s);
                if (s.equals("选集")) {
                    subWheelView.setSelection(index - 1);
                } else if(s.equals("解码器")) {
                    subWheelView.setSelection(jiema);
                } else if(s.equals("画幅")) {
                    subWheelView.setSelection(huafu);
                } else if(s.equals("杜比音效")) {
                    subWheelView.setSelection(shengyi);
                }
            }
        });
        subWheelView.setOnWheelItemClickListener(new WheelView.OnWheelItemClickListener() {
            @Override
            public void onItemClick(int position, Object o) {
                String s = (String) o;
                if (s == null) {
                    return;
                }
                if (s.startsWith("第")) {
                    index = position + 1;
                    if(mPlayerStatus != PLAYER_STATUS.PLAYER_IDLE){
                        mVV.stopPlayback();
                    }
                    if(mEventHandler.hasMessages(EVENT_PLAY))
                        mEventHandler.removeMessages(EVENT_PLAY);
                    playMoviePresenter.getPlaySerialUrl(videoId,index);
                    mPlayerStatus = PLAYER_STATUS.PLAYER_IDLE;
                } else if (s.equals("软解码")) {
                    jiema = 0;
                    mVV.setDecodeMode(BVideoView.DECODE_SW);
                } else if (s.equals("硬解码")) {
                    jiema = 1;
                    mVV.setDecodeMode(BVideoView.DECODE_HW);
                } else if (s.equals("拉伸")) {
                    huafu = 1;
                    mVV.setVideoScalingMode(BVideoView.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING);
                } else if (s.equals("原画")) {
                    huafu = 0;
                    mVV.setVideoScalingMode(BVideoView.VIDEO_SCALING_MODE_SCALE_TO_FIT);
                } else if (s.equals("开启")) {
                    shengyi = 0;
                    mVV.setEnableDolby(true);
                } else if (s.equals("关闭")) {
                    shengyi = 1;
                    mVV.setEnableDolby(false);
                }
            }
        });
    }

   //为控件注册回调处理函数
    private void registerCallbackForControl() {
        mProgress.setOnSeekBarChangeListener(osbc1);
    }

    SeekBar.OnSeekBarChangeListener osbc1 = new SeekBar.OnSeekBarChangeListener() {
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            // TODO Auto-generated method stub
           // LogUtils.e(TAG, "progress: " + progress);
            updateTextViewWithTimeFormat(mCurrPostion, progress);
            //mVV.seekTo(progress);
            // mUIHandler.sendEmptyMessage(UI_EVENT_UPDATE_CURRPOSITION);
        }

        public void onStartTrackingTouch(SeekBar seekBar) {
            // TODO Auto-generated method stub
            //SeekBar开始seek时停止更新
            LogUtils.e(TAG,"暂停进度监控");
            mUIHandler.removeMessages(UI_EVENT_UPDATE_CURRPOSITION);
        }

        public void onStopTrackingTouch(SeekBar seekBar) {
            // TODO Auto-generated method stub
            int iseekPos = seekBar.getProgress();
            //SeekBark完成seek时执行seekTo操作并更新界面
            mVV.seekTo(iseekPos);
            LogUtils.e(TAG, "seek to " + iseekPos);
            LogUtils.e(TAG,"开始进度监控");
            mUIHandler.sendEmptyMessage(UI_EVENT_UPDATE_CURRPOSITION);
        }
    };

    private void startPlay() {
        if(mPlayerStatus != PLAYER_STATUS.PLAYER_IDLE){
            mVV.stopPlayback();
        }
        mHandler.sendEmptyMessage(SHOW_LOAD);
        mLastPos = mVV.getCurrentPosition();
        if(mEventHandler.hasMessages(EVENT_PLAY))
            mEventHandler.removeMessages(EVENT_PLAY);
        mPlayerStatus = PLAYER_STATUS.PLAYER_IDLE;
        mEventHandler.sendEmptyMessage(EVENT_PLAY);
    }

    @Override
    protected void onStop(){
        super.onStop();
        LogUtils.e(TAG, "onStop");
        // 在停止播放前 你可以先记录当前播放的位置,以便以后可以续播
        if (mPlayerStatus == PLAYER_STATUS.PLAYER_PREPARED) {
            mLastPos = mVV.getCurrentPosition();
            mVV.stopPlayback();
        }
        //上传播放记录
        String name = getIntent().getStringExtra("videoName");
        String img = getIntent().getStringExtra("videoImage");
        VideoWatchHistory videoWatchHistory = new VideoWatchHistory();
        String userId = PreferenceUtils.getString(context, AppConstant.USERID, ""); //获取用户ID;
        String mac = AppSystemUtils.getDeviceId();
        videoWatchHistory.setUserId(userId);
        videoWatchHistory.setMAC(mac);
        videoWatchHistory.setVideoId(videoId);
        videoWatchHistory.setVideoTypeId(videoType);
        videoWatchHistory.setIsPersonal(isPersonal);
       // videoWatchHistory.setSerialEpisodeId(SerialEpisodeId);
        videoWatchHistory.setWatchTime(mVV.getCurrentPosition() * 1000 + "");
        videoWatchHistory.setUserIP("");
        videoWatchHistory.setVideoIndex(index);
        videoWatchHistory.setMovieImage(img);
        videoWatchHistory.setMovieName(name);
        //上传播放记录
        dataBaseDao.addMovieLog(videoWatchHistory);
        playMoviePresenter.addVideoHistory(videoWatchHistory,dataBaseDao);
        hideMsg();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //退出后台事件处理线程
        hideMsg();
        if (mVV != null) {
            if(mPlayerStatus != PLAYER_STATUS.PLAYER_IDLE){
                mVV.stopPlayback();
            }
            mVV.destroyDrawingCache();
            mHandlerThread.quit();
            //mVV = null;
        }
        LogUtils.e(TAG, "onDestroy");
    }

    /**
     * 播放完成
     */
    @Override
    public void onCompletion() {
        // TODO Auto-generated method stub
        LogUtils.e(TAG, "onCompletion播放完成");
        if (isParam) {
            isParam = false;
            isParam2 = true;
            if (Integer.parseInt(lastEpisode) > index && (AppConstant.SERIALS.equals(videoType)|| AppConstant.ZONGYI.equals(videoType) || AppConstant.DONGMAN.equals(videoType))) {
                //showMsg("自动进入下一集");
                //SerialEpisodeId = serialEpisodes.get(index).getId();
                index = index + 1;
                playMoviePresenter.getPlaySerialUrl(videoId,index);
                mPlayerStatus = PLAYER_STATUS.PLAYER_IDLE;
               // hideMsg();
            } else {
//                synchronized (SYNC_Playing) {
//                    SYNC_Playing.notify();
//                }
//                mPlayerStatus = PLAYER_STATUS.PLAYER_IDLE;
//                mUIHandler.removeMessages(UI_EVENT_UPDATE_CURRPOSITION);
                //showMsg("播放完毕");
               // hideMsg();
                finish();
            }
        }
    }

    /**
     * 播放准备就绪
     */
    @Override
    public void onPrepared() {
        LogUtils.e(TAG, "onPrepared播放准备就绪");
        mPlayerStatus = PLAYER_STATUS.PLAYER_PREPARED;
        mUIHandler.sendEmptyMessage(UI_EVENT_UPDATE_CURRPOSITION);
        mHandler.sendEmptyMessage(HIDE_LOAD);
        LogUtils.e("视频大小", mVV.getVideoWidth() + "*" + mVV.getVideoHeight());
//        //获取视频支持的分辨率（或者码率）
//        BVideoView.getMediaInfo(BdPalyActivity.this, mVideoSource);
//        mAvailableResolution = BVideoView.getSupportedResolution();
//        mAvailableBitrateKb = BVideoView.getSupportedBitrateKb();
//        mHandler.sendEmptyMessage(VIDEO_INFO);
    }

    @Override
    public void OnCompletionWithParam(int param) {
        LogUtils.e(TAG, "OnCompletionWithParam=" + param);
    }

    private int isParam3 = 0;
    /**
     * 播放出错
     */
    @Override
    public boolean onError(int what, int extra) {
        LogUtils.e(TAG, "onError");
        synchronized (SYNC_Playing) {
            SYNC_Playing.notify();
        }
        mPlayerStatus = PLAYER_STATUS.PLAYER_IDLE;
        mUIHandler.removeMessages(UI_EVENT_UPDATE_CURRPOSITION);
        //重新解析
        if (isParam3 < 2) {
            isParam3 = isParam3 + 1;
            if (AppConstant.MOVICE.equals(videoType)) {
                playMoviePresenter.getPlayUrl(videoId,1);
            } else {
                playMoviePresenter.getPlaySerialUrl2(videoId, index);
            }
        }
        return true;
    }

    @Override
    public boolean onInfo(int what, int extra) {
        switch(what){
            //开始缓冲
            case BVideoView.MEDIA_INFO_BUFFERING_START:
                mHandler.sendEmptyMessage(SHOW_LOAD);
                LogUtils.e(TAG, "开始缓冲caching start,now playing url : " + extra);
                break;
            //结束缓冲
            case BVideoView.MEDIA_INFO_BUFFERING_END:
                isParam2 = true;
                mHandler.sendEmptyMessage(HIDE_LOAD);
                LogUtils.e(TAG, "结束缓冲caching start,now playing url : " + extra);
               // mVV.onSeekCompleted();
                //mVV.seekTo(mVV.getCurrentPosition());
               // mVV.
                break;
            default:
                break;
        }
        return false;
    }

    public void updateControlBar(boolean show) {
        if (show) {
            mController.setVisibility(View.VISIBLE);
        } else {
            mController.setVisibility(View.INVISIBLE);
        }
        barShow = show;
    }

    //前缓冲的百分比， 可以配合onInfo中的开始缓冲和结束缓冲来显示百分比到界面
    @Override
    public void onPlayingBufferCache(int i) {
        LogUtils.e(TAG,i + "");
    }

    /**
     * 开始播放
     * @param url
     */
    public void startPlay(String url) {
        lemon_play_img.setVisibility(View.GONE);
        sdk_ijk_progress_bar_text.setText("视频连接中...");
        mHandler.sendEmptyMessage(SHOW_LOAD);
        mHandler.sendEmptyMessage(SHOW_TITLE);
        mHandler.sendEmptyMessageDelayed(HIDE_TITLE,DEFAULT_TIME_OUT);
        //显示名字
        String name = getIntent().getStringExtra("videoName");
        TextView textView = (TextView) findViewById(R.id.lemon95_play_name);
        if (AppConstant.MOVICE.equals(videoType)) {
            textView.setText(name);
        } else {
            textView.setText(name + "(第" + index + "集)");
        }
        mVideoSource = url;
        // 发起一次播放任务,当然您不一定要在这发起
        if(mPlayerStatus != PLAYER_STATUS.PLAYER_IDLE){
            mVV.stopPlayback();
        }
        if(mEventHandler.hasMessages(EVENT_PLAY))
            mEventHandler.removeMessages(EVENT_PLAY);
        mPlayerStatus = PLAYER_STATUS.PLAYER_IDLE;
        mEventHandler.sendEmptyMessage(EVENT_PLAY);
    }
//    private String[] mAvailableResolution;
//    private int[] mAvailableBitrateKb;

    private void updateTextViewWithTimeFormat(TextView view, int second){
        int hh = second / 3600;
        int mm = second % 3600 / 60;
        int ss = second % 60;
        String strTemp = null;
        if (0 != hh) {
            strTemp = String.format("%02d:%02d:%02d", hh, mm, ss);
        } else {
            strTemp = String.format("%02d:%02d", mm, ss);
        }
        view.setText(strTemp);
    }

    private  Toast toast;
    public void showMsg(String msg) {
        if (toast == null) {
            toast = new Toast(context);
        }
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.lemon_toast, null);
        TextView chapterNameTV = (TextView) view.findViewById(R.id.chapterName);
        chapterNameTV.setText(msg);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(view);
        toast.show();
    }

    public void hideMsg() {
        if (toast != null) {
            toast.cancel();
        }
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        try{
            if (!lemon95_play_menu.isDrawerOpen(lemon95_menu_end)) {
                if(keyCode== KeyEvent.KEYCODE_DPAD_UP || keyCode==KeyEvent.KEYCODE_VOLUME_UP) {
                    mHandler.removeMessages(VOLUME_HIDE);
                    mHandler.sendEmptyMessageDelayed(HIDE_TITLE, DEFAULT_TIME_OUT);
                    mHandler.sendEmptyMessageDelayed(VOLUME_HIDE, DEFAULT_TIME_OUT);
                } else if (keyCode ==KeyEvent.KEYCODE_DPAD_LEFT) {
                    mHandler.sendEmptyMessageDelayed(HIDE_TITLE,DEFAULT_TIME_OUT);
                    osbc1.onStopTrackingTouch(mProgress);
                } else if (keyCode==KeyEvent.KEYCODE_DPAD_RIGHT) {
                    isParam = true;
                    mHandler.sendEmptyMessageDelayed(HIDE_TITLE,DEFAULT_TIME_OUT);
                    osbc1.onStopTrackingTouch(mProgress);
                } else if (keyCode==KeyEvent.KEYCODE_DPAD_DOWN || keyCode==KeyEvent.KEYCODE_VOLUME_DOWN) {
                    mHandler.sendEmptyMessageDelayed(HIDE_TITLE,DEFAULT_TIME_OUT);
                    mHandler.removeMessages(VOLUME_HIDE);
                    mHandler.sendEmptyMessageDelayed(VOLUME_HIDE, DEFAULT_TIME_OUT);
                } else if(keyCode == KeyEvent.KEYCODE_MENU) {
                    boolean isOpen = lemon95_play_menu.isDrawerOpen(lemon95_menu_end);
                    if (isOpen) {
                        lemon95_play_menu.closeDrawer(lemon95_menu_end);
                    } else {
                        lemon95_play_menu.openDrawer(lemon95_menu_end);
                    }
                }
            } else {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    lemon95_play_menu.closeDrawer(lemon95_menu_end);
                    return false;
                } else if(keyCode == KeyEvent.KEYCODE_MENU) {
                    boolean isOpen = lemon95_play_menu.isDrawerOpen(lemon95_menu_end);
                    if (isOpen) {
                        lemon95_play_menu.closeDrawer(lemon95_menu_end);
                    } else {
                        lemon95_play_menu.openDrawer(lemon95_menu_end);
                    }
                } else if(keyCode ==KeyEvent.KEYCODE_DPAD_LEFT){
                    mainWheelView.setFocusable(false);
                    mainWheelView.setFocusableInTouchMode(false);
                    subWheelView.setFocusable(true);
                    subWheelView.setFocusableInTouchMode(true);
                    WheelView.WheelViewStyle style = new WheelView.WheelViewStyle();
                    style.backgroundColor = Color.parseColor("#00000000");
                    style.holoBorderColor = Color.parseColor("#3F51B5");
                    style.textSize = 26;
                    style.textColor = Color.WHITE;
                    style.selectedTextColor = Color.WHITE;
                    subWheelView.setStyle(style);
                    subWheelView.setBackgrounds();

                    WheelView.WheelViewStyle style2 = new WheelView.WheelViewStyle();
                    style2.backgroundColor = Color.parseColor("#00000000");
                    style2.holoBorderColor = Color.parseColor("#00000000");
                    style2.textColor = Color.WHITE;
                    style2.textSize = 26;
                    style2.selectedTextColor = Color.parseColor("#eda1dffc");
                    mainWheelView.setStyle(style2);
                    mainWheelView.setBackgrounds();
                } else if (keyCode ==KeyEvent.KEYCODE_DPAD_RIGHT) {
                    mainWheelView.setFocusable(true);
                    mainWheelView.setFocusableInTouchMode(true);
                    subWheelView.setFocusable(false);
                    subWheelView.setFocusableInTouchMode(false);
                    WheelView.WheelViewStyle style = new WheelView.WheelViewStyle();
                    style.backgroundColor = Color.parseColor("#00000000");
                    style.holoBorderColor = Color.parseColor("#3F51B5");
                    style.textSize = 26;
                    style.textColor = Color.WHITE;
                    style.selectedTextColor = Color.WHITE;
                    mainWheelView.setStyle(style);
                    mainWheelView.setBackgrounds();

                    WheelView.WheelViewStyle style2 = new WheelView.WheelViewStyle();
                    style2.backgroundColor = Color.parseColor("#00000000");
                    style2.holoBorderColor = Color.parseColor("#00000000");
                    style2.textColor = Color.WHITE;
                    style2.textSize = 26;
                    style2.selectedTextColor = Color.parseColor("#eda1dffc");
                    subWheelView.setStyle(style2);
                    subWheelView.setBackgrounds();
                }
            }
        } catch (Exception e) {
        }
        return super.onKeyUp(keyCode, event);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (!lemon95_play_menu.isDrawerOpen(lemon95_menu_end)) {
            LogUtils.e("点击1", event.getKeyCode() + "");
            if ((event.getKeyCode()==KeyEvent.KEYCODE_DPAD_LEFT && event.getAction() != KeyEvent.ACTION_UP) || (event.getKeyCode()==KeyEvent.KEYCODE_DPAD_RIGHT && event.getAction() != KeyEvent.ACTION_UP)) {
                osbc1.onStartTrackingTouch(mProgress);
                isParam2 = false;
            }
        } else {
            if(event.getKeyCode()==KeyEvent.KEYCODE_DPAD_UP && event.getAction() != KeyEvent.ACTION_UP) {
                if (mainWheelView.isFocusable()) {
                    int i =  mainWheelView.getCurrentPosition();
                    LogUtils.e(TAG,"位置1：" + i);
                    mainWheelView.toSelect(mainWheelView.getItemH(), i);
                } else if (subWheelView.isFocusable()) {
                    int i =  subWheelView.getCurrentPosition();
                    subWheelView.toSelect(mainWheelView.getItemH(),i);
                }
                return false;
            } else if (event.getKeyCode()==KeyEvent.KEYCODE_DPAD_DOWN && event.getAction() != KeyEvent.ACTION_UP) {
                if (mainWheelView.isFocusable()) {
                    int i =  mainWheelView.getCurrentPosition();
                    LogUtils.e(TAG,"位置2：" + i);
                    mainWheelView.toSelect(-mainWheelView.getItemH(),i);
                } else if (subWheelView.isFocusable()) {
                    int i =  subWheelView.getCurrentPosition();
                    subWheelView.toSelect(-mainWheelView.getItemH(),i);
                }
                return false;
            }
        }
        return super.dispatchKeyEvent(event);
    }

    private Boolean isParam = false; //控制是否是播放结束还是按钮返回
    private Boolean isParam2 = true; //控制快进时缓存完成后调整进度条
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        try{
            if (!lemon95_play_menu.isDrawerOpen(lemon95_menu_end)) {
                LogUtils.e("菜单关闭点击点击", keyCode + "");
                if(keyCode==KeyEvent.KEYCODE_DPAD_UP || keyCode==KeyEvent.KEYCODE_VOLUME_UP){
                    mHandler.removeMessages(HIDE_TITLE);
                    mHandler.sendEmptyMessage(SHOW_TITLE);
                    //加声音
                    lemon_volume.setVisibility(View.VISIBLE);
                    mVolume = mVolume + 1;
                    if (mVolume >= mMaxVolume) {
                        mVolume = mMaxVolume;
                    }
                    lemon_volume_seek.setProgress(mVolume);
                    lemon_volume_img.setImageResource(R.drawable.icon_volume);
                    mAM.setStreamVolume(AudioManager.STREAM_MUSIC, mVolume, 0);
                    return false;
                } else if (keyCode==KeyEvent.KEYCODE_DPAD_LEFT) {
                    mHandler.removeMessages(HIDE_TITLE);
                    mHandler.sendEmptyMessage(SHOW_TITLE);
                    osbc1.onStartTrackingTouch(mProgress);
                } else if (keyCode==KeyEvent.KEYCODE_DPAD_RIGHT) {
                    mHandler.removeMessages(HIDE_TITLE);
                    mHandler.sendEmptyMessage(SHOW_TITLE);
                    osbc1.onStartTrackingTouch(mProgress);
                } else if (keyCode==KeyEvent.KEYCODE_DPAD_DOWN || keyCode==KeyEvent.KEYCODE_VOLUME_DOWN) {
                    lemon_volume.setVisibility(View.VISIBLE);
                    mHandler.removeMessages(HIDE_TITLE);
                    mHandler.sendEmptyMessage(SHOW_TITLE);
                    mVolume = mVolume - 1;
                    if (mVolume <= 0) {
                        mVolume = 0;
                        lemon_volume_img.setImageResource(R.drawable.icon_novolume);
                    }
                    lemon_volume_seek.setProgress(mVolume);
                    mAM.setStreamVolume(AudioManager.STREAM_MUSIC, mVolume, 0);
                    return false;
                } else if (keyCode==KeyEvent.KEYCODE_DPAD_CENTER||keyCode==KeyEvent.KEYCODE_ENTER) {
                    //videoJjMediaContoller.enter(mVideoView,lemon_play_img);
                    if (mVV.isPlaying()) {
                        mVV.pause();
                        mHandler.removeMessages(HIDE_TITLE);
                        mHandler.sendEmptyMessage(SHOW_TITLE);
                        lemon_play_img.setVisibility(View.VISIBLE);
                        lemon_play_img.setImageResource(R.drawable.icon_pause);
                    } else {
                        mHandler.sendEmptyMessage(SHOW_TITLE);
                        mHandler.sendEmptyMessageDelayed(HIDE_TITLE,DEFAULT_TIME_OUT);
                        lemon_play_img.setVisibility(View.GONE);
                        mVV.resume();
                    }
                } else if (keyCode == KeyEvent.KEYCODE_BACK) {
                    long currentTime = System.currentTimeMillis();
                    if ((currentTime - touchTime) >= waitTime) {
                        showMsg("再按一次退出播放");
                        touchTime = currentTime;
                    } else {
                        isParam = false;
                        this.finish();
                    }
                    return false;
                }
            }
        } catch (Exception e) {
        }
        return super.onKeyDown(keyCode, event);
    }

    //菜单状态监听
    DrawerLayout.DrawerListener drawerLister = new DrawerLayout.DrawerListener() {
        @Override
        public void onDrawerSlide(View drawerView, float slideOffset) {
            String s = (String) mainWheelView.getSelectionItem();
            if ("选集".equals(s)) {
                subWheelView.setSelection(index - 1);
            }
        }

        @Override
        public void onDrawerOpened(View drawerView) {
            LogUtils.i("BdPlayActivity", "onDrawerOpened");
            String s = (String) mainWheelView.getSelectionItem();
            LogUtils.e(TAG, s);
            if (!videoType.equals(AppConstant.MOVICE) && "选集".equals(s)) {
                subWheelView.setSelection(index - 1);
            }
        }

        @Override
        public void onDrawerClosed(View drawerView) {
            LogUtils.i("BdPlayActivity", "onDrawerClosed:");
        }

        @Override
        public void onDrawerStateChanged(int newState) {

        }
    };
}
