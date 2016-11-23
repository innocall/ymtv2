package com.lemon95.ymtv.common;

/**
 * Created by WXT on 2016/7/14.
 */
public interface AppConstant {

    //String RESOURCE = "http://app.lemon95.com:8011/"; //资源地址前部分
    //String VERSIONURL = "http://acadmin.lemon95.com:12345/App/Android/TV/Upgrade.xml"; //正规渠道版本更新地址
    String VERSIONURL = "http://acadmin.lemon95.com:12345/App/Android/TV01/Upgrade.xml"; //自己（影檬）版本更新地址
    String RESOURCE = "http://tupian.lemon95.com:8088/"; //资源地址前部分
   // String RESOURCE = "http://img.lemon95.com/"; //高防

    //影视类型
    String MOVICE = "1";
    String SERIALS = "2";
    String FUNNY = "7";  //搞笑
    String ZONGYI = "15"; //综艺
    String DONGMAN = "16"; //动漫

    //本地保存的用户信息key
    String USERNAME = "USERNAME";
    String USERID = "USERID";
    String USERIMG = "USERIMG";
    String USERMOBILE = "USERMOBILE";

    String PAGESIZE = "30";

    String DIRS = "/myImage/ymtv/qr/"; //登陆二维码保存路径
    String QRNAME = "qr.png";  //登陆二维码名称

    String PAGETYPE = "PAGETYPE";

    String partnerKey = "fL6GK1VAJ2OJtG7E5SKHWyfQ1GGsvdwD";
    String appid = "wx427eb35b163fb705";
    String mch_id = "1264108101";
    String spbillCreateIp = "121.40.187.98";
    String QR_TOP = "https://paya.swiftpass.cn/pay/qrcode?uuid=";
    String MACTOKEN = "MACTOKEN";

    //String spbillCreateIp = "120.76.76.120";

    String ALL = "0";  //全部
    String YANGSHI = "2"; //央视
    String WEISHI = "1"; //卫视
    String DIFANGTAI = "3"; //地方台
    String CIBN = "4";
    String TESETAI = "5"; //特色台

    public static final int PV_PLAYER__Auto = 0;
    public static final int PV_PLAYER__AndroidMediaPlayer = 1;
    public static final int PV_PLAYER__IjkMediaPlayer = 2;
    public static final int PV_PLAYER__IjkExoMediaPlayer = 3;

    /**
     * 解码方式
     */
    int MEDIACODEC = 0; //软件解码
    int VCODE = 1; //硬件解码
}

