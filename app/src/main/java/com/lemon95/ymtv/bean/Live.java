package com.lemon95.ymtv.bean;

import java.util.List;

/**
 * Created by wuxiaotie on 2016/10/26.
 * 直播
 */
public class Live {
    private String ReturnCode;
    private String ReturnMsg;
    private List<Data> Data;

    public String getReturnCode() {
        return ReturnCode;
    }

    public void setReturnCode(String returnCode) {
        ReturnCode = returnCode;
    }

    public String getReturnMsg() {
        return ReturnMsg;
    }

    public void setReturnMsg(String returnMsg) {
        ReturnMsg = returnMsg;
    }

    public List<Live.Data> getData() {
        return Data;
    }

    public void setData(List<Live.Data> data) {
        Data = data;
    }

    public static class Data{
        private String Name;
        private String NickName;
        private String TypeId;

        public String getName() {
            return Name;
        }

        public void setName(String name) {
            Name = name;
        }

        public String getNickName() {
            return NickName;
        }

        public void setNickName(String nickName) {
            NickName = nickName;
        }

        public String getTypeId() {
            return TypeId;
        }

        public void setTypeId(String typeId) {
            TypeId = typeId;
        }
    }
}
