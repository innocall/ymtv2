package com.lemon95.ymtv.utils;

import com.lemon95.ymtv.common.AppConstant;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Created by wuxiaotie on 2016/11/7.
 */
public class MenuDataUtils {

    public static List<String> createMainDatas(String videoType) {
        if (AppConstant.MOVICE.equals(videoType)) {
            String[] strings = {"解码器","画幅","杜比音效"};
            return Arrays.asList(strings);
        } else {
            String[] strings = {"选集", "解码器","画幅","杜比音效"};
            return Arrays.asList(strings);
        }
    }

    public static HashMap<String, List<String>> createSubDatas(String videoType,int last) {
        HashMap<String, List<String>> map = new HashMap<String, List<String>>();
        if (AppConstant.MOVICE.equals(videoType)) {
            String[] strings = { "解码器","画幅","杜比音效"};
          //  String[] s2 = {"视频源1", "视频源2"};
            String[] s3 = {"软解码", "硬解码"};
            String[] s4 = { "原画","拉伸","16:9","4:3"};
            String[] s6 = {"开启", "关闭"};
            String[][] ss = { s3, s4,s6};
            for (int i = 0; i < strings.length; i++) {
                map.put(strings[i], Arrays.asList(ss[i]));
            }
        } else {
            String[] strings = {"选集", "解码器","画幅","杜比音效"};
            String[] s1 = new String[last];
            if (last > 0) {
                for (int i=1;i<=last;i++) {
                    s1[i - 1] = "第" + i + "集";
                }
            }
           // String[] s2 = {"视频源1", "视频源2"};
            String[] s3 = {"软解码", "硬解码"};
            String[] s4 = { "原画","拉伸","16:9","4:3"};
            String[] s6 = {"开启", "关闭"};
            String[][] ss = {s1, s3, s4,s6};
            for (int i = 0; i < strings.length; i++) {
                map.put(strings[i], Arrays.asList(ss[i]));
            }
        }
        return map;
    }

}
