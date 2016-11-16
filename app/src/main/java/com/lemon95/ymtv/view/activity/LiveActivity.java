package com.lemon95.ymtv.view.activity;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lemon95.androidtvwidget.view.ListViewTV;
import com.lemon95.ymtv.R;
import com.lemon95.ymtv.adapter.LiveAdapter;
import com.lemon95.ymtv.adapter.LiveTypeAdapter;
import com.lemon95.ymtv.bean.Live;
import com.lemon95.ymtv.bean.LiveType;
import com.lemon95.ymtv.common.AppConstant;
import com.lemon95.ymtv.myview.LoadingView;
import com.lemon95.ymtv.myview.MsgView;
import com.lemon95.ymtv.presenter.LivePresenter;
import com.lemon95.ymtv.utils.LogUtils;
import com.lemon95.ymtv.utils.ToastUtils;
import com.starschina.abs.media.ThinkoPlayerListener;
import com.starschina.media.ThinkoEnvironment;
import com.starschina.media.ThinkoPlayerView;
import com.starschina.types.DChannel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wuxiaotie on 2016/10/24.
 * 直播
 */
public class LiveActivity extends BaseActivity {

    private static final int GONG = 1;
    private static final int GONG2 = 2;
    private long waitTime = 2000;
    private long touchTime = 0;
    private LoadingView mLoadingView;
    private MsgView msgView;
    private LivePresenter livePresenter = new LivePresenter(this);
    private ThinkoPlayerView mPlayerView;
    private DrawerLayout lemon95_menu;
    private LinearLayout lemon95_menu_left;
    private ListViewTV left_drawer;
    private ListViewTV left_drawer2;
    private LiveTypeAdapter typeAdapter;
    private RelativeLayout lemon95_loads;
    private RelativeLayout player;
    private LiveAdapter liveAdapter;
    private DChannel[] channellist; //CIBN接口获取全部
    private List<LiveType> list = new ArrayList<>();
    private View oldViewLeft;
            //,oldViewLeft2;
    //private String text = "全部";
    private int meun1 = 0;
    private Handler handler = new Handler(){

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case GONG:
                   // lemon95_menu.closeDrawer(lemon95_menu_left);
                    break;
                case GONG2:
                    msgView.setVisibility(View.GONE);
                    break;
            }
        }
    };

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            handler.sendEmptyMessage(GONG);
        }
    };

    Runnable runnable2 = new Runnable() {
        @Override
        public void run() {
            handler.sendEmptyMessage(GONG2);
        }
    };

    @Override
    protected int getLayoutId() {
        //直播初始化
        try{
            ThinkoEnvironment.setUp(getApplicationContext());
        }catch(IllegalArgumentException e){
            LogUtils.e("----","live appkey is null");
        }
        return R.layout.activity_live;
    }

    @Override
    protected void setupViews() {
        lemon95_menu = (DrawerLayout) findViewById(R.id.lemon95_menu);
        lemon95_menu_left = (LinearLayout) findViewById(R.id.lemon95_menu_left);
        left_drawer = (ListViewTV) findViewById(R.id.left_drawer);
        left_drawer2 = (ListViewTV) findViewById(R.id.left_drawer2);
        lemon95_loads = (RelativeLayout) findViewById(R.id.lemon95_loads);
        meun1 = getIntent().getIntExtra("meun1", 0);
        left_drawer.setItemsCanFocus(true);
        left_drawer.requestFocus();
        mPlayerView = new ThinkoPlayerView(this);
        RelativeLayout.LayoutParams lp22 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        mPlayerView.setLayoutParams(lp22);
        player = ((RelativeLayout)findViewById(R.id.player));
        player.addView(mPlayerView);
       // mPlayerView.setPlayerSize(480, 300);
        mPlayerView.setPlayerListener(mListener);
        lemon95_menu.setDrawerListener(drawerLister);
        left_drawer.setPoint(meun1);
        //自定义loading view
        mLoadingView = new LoadingView(this);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        mPlayerView.setLoadingView(mLoadingView, lp);
        msgView = new MsgView(this);
        mPlayerView.addView(msgView, lp);
        msgView.setVisibility(View.GONE);
    }

    @Override
    protected void initialized() {
        intiTypeDate();
        typeAdapter = new LiveTypeAdapter(context,list);
        left_drawer.setAdapter(typeAdapter);
        liveAdapter = new LiveAdapter(context);
        left_drawer2.setAdapter(liveAdapter);
        ThinkoEnvironment.getChannelList(mChannelsOnChangeListener);
        left_drawer.setFocusable(true);
        left_drawer.setFocusableInTouchMode(true);
        left_drawer.setSelection(meun1);
        left_drawer.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    // int i = left_drawer.getSelectedItemPosition();
                    // LogUtils.e("text:", i + "");
//                    left_drawer.setFocusable(true);
//                    left_drawer.setFocusableInTouchMode(true);
//                    left_drawer.setSelection(i);
                    left_drawer.setSelector(R.drawable.live_list_item);
                    LogUtils.e("焦点", "left_drawer获取焦点");
                } else {
                    left_drawer.setSelector(R.color.live_null);
                    LogUtils.e("焦点", "left_drawer失去焦点");
                }
            }
        });
        left_drawer.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                LogUtils.e("焦点left_drawer", "left_drawer");
                if (view != null) {
                    ImageView imageView = (ImageView) view.findViewById(R.id.lemon95_rig);
                    TextView lemon95_menu_item = (TextView) view.findViewById(R.id.lemon95_menu_item);
                    imageView.setVisibility(View.VISIBLE);
                    lemon95_menu_item.setTextColor(getResources().getColor(R.color.lemon_color3));
                    //  text = lemon95_menu_item.getText().toString().trim();
                    if (oldViewLeft != null) {
                        ImageView oldViewLeftVi = (ImageView) oldViewLeft.findViewById(R.id.lemon95_rig);
                        TextView lemon95_menu_itemOld = (TextView) oldViewLeft.findViewById(R.id.lemon95_menu_item);
                        lemon95_menu_itemOld.setTextColor(getResources().getColor(R.color.lemon_b3aeae));
                        oldViewLeftVi.setVisibility(View.GONE);
                    }
                    oldViewLeft = view;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        left_drawer2.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    left_drawer2.setSelector(R.drawable.live_list_item);
                    LogUtils.e("焦点", "left_drawer2获取焦点");
                    int i = left_drawer.getPoint();
                    View view = left_drawer.getChildAt(i - left_drawer.getFirstVisiblePosition());
                    if (view != null && view != oldViewLeft) {
                        ImageView imageView = (ImageView) view.findViewById(R.id.lemon95_rig);
                        TextView lemon95_menu_item = (TextView) view.findViewById(R.id.lemon95_menu_item);
                        imageView.setVisibility(View.VISIBLE);
                        lemon95_menu_item.setTextColor(getResources().getColor(R.color.lemon_color3));
                        //  text = lemon95_menu_item.getText().toString().trim();
                        if (oldViewLeft != null) {
                            ImageView oldViewLeftVi = (ImageView) oldViewLeft.findViewById(R.id.lemon95_rig);
                            TextView lemon95_menu_itemOld = (TextView) oldViewLeft.findViewById(R.id.lemon95_menu_item);
                            lemon95_menu_itemOld.setTextColor(getResources().getColor(R.color.lemon_b3aeae));
                            oldViewLeftVi.setVisibility(View.GONE);
                        }
                    }
                } else {
                    left_drawer2.setSelector(R.color.live_null);
                   /* if (oldViewLeft2 != null) {
                        TextView lemon95_menu_item_next2 = (TextView)oldViewLeft2.findViewById(R.id.lemon95_menu_item_next);
                        TextView lemon95_menu_itemOld = (TextView)oldViewLeft2.findViewById(R.id.lemon95_menu_item);
                        lemon95_menu_itemOld.setTextColor(getResources().getColor(R.color.lemon_b3aeae));
                        lemon95_menu_item_next2.setTextColor(getResources().getColor(R.color.lemon_b3aeae));
                    }*/
                    LogUtils.e("焦点", "left_drawer2失去焦点");
                }
            }
        });
        left_drawer2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                LogUtils.e("焦点left_drawer2", "left_drawer2");
                /*if (view != null) {
                    TextView lemon95_menu_item_next = (TextView)view.findViewById(R.id.lemon95_menu_item_next);
                    TextView lemon95_menu_item = (TextView)view.findViewById(R.id.lemon95_menu_item);
                    lemon95_menu_item.setTextColor(getResources().getColor(R.color.lemon_color3));
                    lemon95_menu_item_next.setTextColor(getResources().getColor(R.color.lemon_color3));
                    //  text = lemon95_menu_item.getText().toString().trim();
                    if (oldViewLeft2 != null) {
                        TextView lemon95_menu_item_next2 = (TextView)oldViewLeft2.findViewById(R.id.lemon95_menu_item_next);
                        TextView lemon95_menu_itemOld = (TextView)oldViewLeft2.findViewById(R.id.lemon95_menu_item);
                        lemon95_menu_itemOld.setTextColor(getResources().getColor(R.color.lemon_b3aeae));
                        lemon95_menu_item_next2.setTextColor(getResources().getColor(R.color.lemon_b3aeae));
                    }
                    oldViewLeft2 = view;
                }*/
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        left_drawer.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                left_drawer.setPoint(position);
                left_drawer2.setPoint(0);
                LiveType liveType = list.get(position);
                if (AppConstant.ALL.equals(liveType.getId())) {
                    getTV(channellist1, liveType);
                } else if (AppConstant.YANGSHI.equals(liveType.getId())) {
                    getTV(channellist2, liveType);
                } else if (AppConstant.WEISHI.equals(liveType.getId())) {
                    getTV(channellist3, liveType);
                } else if (AppConstant.DIFANGTAI.equals(liveType.getId())) {
                    getTV(channellist4, liveType);
                } else if (AppConstant.TESETAI.equals(liveType.getId())) {
                    getTV(channellist5, liveType);
                } else if (AppConstant.CIBN.equals(liveType.getId())) {
                    getTV(channellist6, liveType);
                }
            }
        });
        left_drawer2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                left_drawer2.setPoint(position);
                int i = left_drawer.getSelectedItemPosition();
                try{
                    if (i == 0) {
                        startPlay(channellist1, position);
                    } else if (i == 1) {
                        startPlay(channellist2, position);
                    } else if (i == 2) {
                        startPlay(channellist3, position);
                    } else if (i == 3) {
                        startPlay(channellist4, position);
                    } else if (i == 4) {
                        startPlay(channellist6, position);
                    }else if (i == 5) {
                        startPlay(channellist5, position);;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void getTV(List<DChannel> channell, LiveType liveType) {
        if (channell == null || channell.size() == 0) {
            livePresenter.getTvs(liveType.getId(),0); //点击获取
        } else {
            liveAdapter.setData(channell);
            liveAdapter.notifyDataSetChanged();
        }
    }

    private void intiTypeDate() {
        LiveType LiveType1 = new LiveType("全部", AppConstant.ALL);
        list.add(LiveType1);
        LiveType LiveType2 = new LiveType("央视",AppConstant.YANGSHI);
        list.add(LiveType2);
        LiveType LiveType3 = new LiveType("卫视",AppConstant.WEISHI);
        list.add(LiveType3);
        LiveType LiveType4 = new LiveType("地方台",AppConstant.DIFANGTAI);
        list.add(LiveType4);
        LiveType LiveType5 = new LiveType("CIBN",AppConstant.CIBN);
        list.add(LiveType5);
        LiveType LiveType6 = new LiveType("特色台",AppConstant.TESETAI);
        list.add(LiveType6);
    }
   // private boolean isParam = true;
    ThinkoEnvironment.OnGetChannelsListener mChannelsOnChangeListener = new ThinkoEnvironment.OnGetChannelsListener() {

        @Override
        public void getChannelList(DChannel[] channel) {
            if (channel != null && (channel[0].nextEpg != null || channel[1].nextEpg != null || channel[16].nextEpg != null)) {
                //isParam = false;
                LogUtils.e("demo", "channellist.size:" + channel.length);
               /* for (int i=0;i<channel.length;i++) {
                    LogUtils.e("---" + i,channel[i].name);
                }*/
                setData(channel);
                if (meun1 == 0) {
                    livePresenter.getTvs("0",1); //获取全部
                } else if (meun1 == 1) {
                    livePresenter.getTvs("2",1); //获取全部
                } else if (meun1 == 2) {
                    livePresenter.getTvs("1",1); //获取全部
                } else if (meun1 == 3) {
                    livePresenter.getTvs("3",1); //获取全部
                } else if (meun1 == 4) {
                    livePresenter.getTvs("4",1); //获取全部
                } else if (meun1 == 5) {
                    livePresenter.getTvs("5",1); //获取全部
                }
                player.setVisibility(View.VISIBLE);
                lemon95_loads.setVisibility(View.GONE);
                //显示菜单
                lemon95_menu.openDrawer(lemon95_menu_left);
                //自动隐藏
                handler.postDelayed(runnable, 10000);
            } else {
                LogUtils.e("提示","CIBN获取失败");
            }
        }
    };

    public void setData(DChannel[] chs){
        channellist = chs;
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK) {
            //后退键
            boolean isOpen = lemon95_menu.isDrawerOpen(lemon95_menu_left);
            if (isOpen) {
                lemon95_menu.closeDrawer(lemon95_menu_left);
            } else {
                //退出
                long currentTime = System.currentTimeMillis();
                if ((currentTime - touchTime) >= waitTime) {
                    showMsg("再按一次退出播放");
                    touchTime = currentTime;
                } else {
                    this.finish();
                }
            }
            return false;
        } else if(keyCode == KeyEvent.KEYCODE_MENU) {
            boolean isOpen = lemon95_menu.isDrawerOpen(lemon95_menu_left);
            if (isOpen) {
                lemon95_menu.closeDrawer(lemon95_menu_left);
            } else {
                lemon95_menu.openDrawer(lemon95_menu_left);
            }
        } else if (keyCode==KeyEvent.KEYCODE_DPAD_CENTER||keyCode==KeyEvent.KEYCODE_ENTER) {
            boolean isOpen = lemon95_menu.isDrawerOpen(lemon95_menu_left);
            if (!isOpen) {
                if (mPlayerView.isPlaying()) {
                    mPlayerView.pause();
                    msgView.isPuase(true);
                    handler.removeCallbacks(runnable2);
                    msgView.setVisibility(View.VISIBLE);
                } else {
                    mPlayerView.start();
                    msgView.isPuase(false);
                    msgView.setVisibility(View.GONE);
                }
            }
        } else if (keyCode ==KeyEvent.KEYCODE_DPAD_LEFT) {
            boolean isOpen = lemon95_menu.isDrawerOpen(lemon95_menu_left);
            if (!isOpen) {
                int i = left_drawer.getSelectedItemPosition();
                int position = left_drawer2.getSelectedItemPosition();
                try {
                    if (i == 0) {
                        position = position - 1;
                        if (position < 0) {
                            position = channellist1.size() - 1;
                        }
                        LogUtils.e("位置：",position + "");
                        left_drawer2.setSelection(position);
                        startPlay(channellist1, position);
                    } else if (i == 1) {
                        position = position - 1;
                        if (position < 0) {
                            position = channellist2.size() - 1;
                        }
                        LogUtils.e("位置：",position + "");
                        left_drawer2.setSelection(position);
                        startPlay(channellist2, position);
                    } else if (i == 2) {
                        position = position - 1;
                        if (position < 0) {
                            position = channellist3.size() - 1;
                        }
                        LogUtils.e("位置：",position + "");
                        left_drawer2.setSelection(position);
                        startPlay(channellist3, position);
                    } else if (i == 3) {
                        position = position - 1;
                        if (position < 0) {
                            position = channellist4.size() - 1;
                        }
                        LogUtils.e("位置：",position + "");
                        left_drawer2.setSelection(position);
                        startPlay(channellist4, position);
                    } else if (i == 4) {
                        position = position - 1;
                        if (position < 0) {
                            position = channellist6.size() - 1;
                        }
                        LogUtils.e("位置：",position + "");
                        left_drawer2.setSelection(position);
                        startPlay(channellist6, position);
                    } else if (i == 5) {
                        position = position - 1;
                        if (position < 0) {
                            position = channellist5.size() - 1;
                        }
                        LogUtils.e("位置：",position + "");
                        left_drawer2.setSelection(position);
                        startPlay(channellist5, position);
                        ;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }else if (keyCode ==KeyEvent.KEYCODE_DPAD_RIGHT) {
            boolean isOpen = lemon95_menu.isDrawerOpen(lemon95_menu_left);
            if (!isOpen) {
                int i = left_drawer.getSelectedItemPosition();
                int position = left_drawer2.getSelectedItemPosition();
                try {
                    if (i == 0) {
                        position = position + 1;
                        if (position >= channellist1.size()) {
                            position = 0;
                        }
                        LogUtils.e("位置：",position + "");
                        left_drawer2.setSelection(position);
                        startPlay(channellist1, position);
                    } else if (i == 1) {
                        position = position + 1;
                        if (position >= channellist2.size()) {
                            position = 0;
                        }
                        LogUtils.e("位置：",position + "");
                        left_drawer2.setSelection(position);
                        startPlay(channellist2, position);
                    } else if (i == 2) {
                        position = position + 1;
                        if (position >= channellist3.size()) {
                            position = 0;
                        }
                        LogUtils.e("位置：",position + "");
                        left_drawer2.setSelection(position);
                        startPlay(channellist3, position);
                    } else if (i == 3) {
                        position = position + 1;
                        if (position >= channellist4.size()) {
                            position = 0;
                        }
                        LogUtils.e("位置：",position + "");
                        left_drawer2.setSelection(position);
                        startPlay(channellist4, position);
                    } else if (i == 4) {
                        position = position + 1;
                        if (position >= channellist6.size()) {
                            position = 0;
                        }
                        LogUtils.e("位置：",position + "");
                        left_drawer2.setSelection(position);
                        startPlay(channellist6, position);
                    } else if (i == 5) {
                        position = position + 1;
                        if (position >= channellist5.size()) {
                            position = 0;
                        }
                        LogUtils.e("位置：",position + "");
                        left_drawer2.setSelection(position);
                        startPlay(channellist5, position);
                        ;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return super.onKeyUp(keyCode, event);
    }

    public void showMsg(String msg) {
        ToastUtils.showBgToast(msg, context);
    }

    private ThinkoPlayerListener mListener = new ThinkoPlayerListener() {

        @Override
        public void onPrepared() {
            // TODO Auto-generated method stub
            LogUtils.i("demo", "onPrepared");
        }

        @Override
        public void onNetworkSpeedUpdate(int arg0) {
            // TODO Auto-generated method stub
            LogUtils.i("demo", "onNetworkSpeedUpdate");
        }

        @Override
        public boolean onInfo(int arg0, int arg1) {
            // TODO Auto-generated method stub
            LogUtils.i("demo", "onInfo");
            return false;
        }

        @Override
        public boolean onError(int arg0, int arg1) {
            // TODO Auto-generated method stub
            LogUtils.e("demo", "onError[arg0:"+arg0+",arg1:"+arg1+"]");
            return false;
        }

        @Override
        public void onCompletion() {
            // TODO Auto-generated method stub
            LogUtils.i("demo", "onCompletion");
        }

        @Override
        public void onBuffer(float arg0) {
            // TODO Auto-generated method stub
            LogUtils.i("demo", "onBuffer:"+arg0);
        }
    };

    //菜单状态监听
    DrawerLayout.DrawerListener drawerLister = new DrawerLayout.DrawerListener() {
        @Override
        public void onDrawerSlide(View drawerView, float slideOffset) {

        }

        @Override
        public void onDrawerOpened(View drawerView) {
            LogUtils.i("LiveActivity", "onDrawerOpened");
            msgView.setVisibility(View.VISIBLE);
        }

        @Override
        public void onDrawerClosed(View drawerView) {
            LogUtils.i("LiveActivity", "onDrawerClosed:");
            //关闭菜单后6秒隐藏节目台提示
            handler.postDelayed(runnable2, 6000);
        }

        @Override
        public void onDrawerStateChanged(int newState) {

        }
    };

    private List<DChannel> channellist1 = new ArrayList<>();; //全部
    private List<DChannel> channellist2 = new ArrayList<>(); //央视
    private List<DChannel> channellist3 = new ArrayList<>(); //卫视
    private List<DChannel> channellist4 = new ArrayList<>(); //地方台
    private List<DChannel> channellist5 = new ArrayList<>(); //特色台
    private List<DChannel> channellist6 = new ArrayList<>(); //CIBN
    /**
     * 初始化数据
     * @param live
     */
    public void initLiveDate(Live live,String type,int state) {
        if (live != null) {
            List<Live.Data> datas =  live.getData();
            if (datas != null && datas.size() > 0) {
                if (AppConstant.ALL.equals(type)) {
                    //全部
                    setChannel(channellist1,datas);
                    if (state == 1) {
                        //第一次进来
                        startPlay(channellist1, 0);
                    }
                } else if (AppConstant.YANGSHI.equals(type)) {
                    setChannel(channellist2,datas);
                    if (state == 1) {
                        //第一次进来
                        startPlay(channellist2, 0);
                    }
                } else if (AppConstant.WEISHI.equals(type)) {
                    setChannel(channellist3,datas);
                    if (state == 1) {
                        //第一次进来
                        startPlay(channellist3, 0);
                    }
                } else if (AppConstant.DIFANGTAI.equals(type)) {
                    setChannel(channellist4,datas);
                    if (state == 1) {
                        //第一次进来
                        startPlay(channellist4,0);
                    }
                } else if (AppConstant.TESETAI.equals(type)) {
                    setChannel(channellist5,datas);
                    if (state == 1) {
                        //第一次进来
                        startPlay(channellist5,0);
                    }
                }else if (AppConstant.CIBN.equals(type)) {
                    setChannel(channellist6,datas);
                    if (state == 1) {
                        //第一次进来
                        startPlay(channellist6,0);
                    }
                }
            } else {

            }
        } else {

        }
    }

    /**
     * 播放视频
     * @param list
     * @param i
     */
    public void startPlay(List<DChannel> list,int i) {
        DChannel dChannel = list.get(i);
        if (dChannel != null) {
            mPlayerView.stop();
            mPlayerView.prepareToPlay(dChannel.id, dChannel.name);
            String next = "";
            if (dChannel.nextEpg != null) {
                next = dChannel.nextEpg.name;
            }
            String news = "";
            if (dChannel.currentEpg != null) {
                news = dChannel.currentEpg.name;
            }
            msgView.isPuase(false);
            msgView.setMsg(news,next);
            msgView.setVisibility(View.VISIBLE);
            handler.postDelayed(runnable2, 8000);
        }

    }
    private void setChannel(List<DChannel> channel,List<Live.Data> datas) {
        for (int i = 0;i<datas.size();i++) {
            for (int j=0; j<channellist.length;j++) {
                //LogUtils.i("CIBN:",channellist[j].name);
                if (datas.get(i).getName().equals(channellist[j].name)) {
                    DChannel dChannel = new DChannel();
                    dChannel.captureImg = channellist[j].captureImg;
                    dChannel.currentEpg = channellist[j].currentEpg;
                    dChannel.icon = channellist[j].icon;
                    dChannel.nextEpg = channellist[j].nextEpg;
                    dChannel.id = channellist[j].id;
                    dChannel.name = datas.get(i).getNickName();
                    channel.add(dChannel);
                }
            }
        }
        liveAdapter.setData(channel);
        liveAdapter.notifyDataSetChanged();
    }

    private int brightness = 50;

    //设置屏幕亮度
    private void setBrightness(int brightness) {
        this.brightness = brightness;
    }

    protected void setBrightness() {
        WindowManager.LayoutParams lp = this.getWindow().getAttributes();
       /* int brightness =  Settings.System.getInt(this.getContentResolver(),
                Settings.System.SCREEN_BRIGHTNESS,0);*/
        lp.screenBrightness = brightness/255.0f;
        this.getWindow().setAttributes(lp);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //sdk释放
        LogUtils.e(TAG,"释放SDK");
        mPlayerView.release();
        ThinkoEnvironment.tearDown();
    }
}
