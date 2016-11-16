package com.lemon95.ymtv.bean;

import java.util.List;

/**
 * Created by wuxiaotie on 2016/11/3.
 * 电视剧剧集
 */
public class SingleSerialEpisode {
    private String ReturnCode;
    private String ReturnMsg;
    private Data Data;

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

    public SingleSerialEpisode.Data getData() {
        return Data;
    }

    public void setData(SingleSerialEpisode.Data data) {
        Data = data;
    }

    public static class Data {
        private List<String> RealSources;
        private List<Adverts> Adverts;

        public List<String> getRealSources() {
            return RealSources;
        }

        public void setRealSources(List<String> realSources) {
            RealSources = realSources;
        }

        public List<SingleSerialEpisode.Data.Adverts> getAdverts() {
            return Adverts;
        }

        public void setAdverts(List<SingleSerialEpisode.Data.Adverts> adverts) {
            Adverts = adverts;
        }

        public static class Adverts {
            private String Id;
            private String AdvertType;
            private String AdvertPath;
            private String Url;
            private String Duration;
            private String PlayOpportunity;

            public String getId() {
                return Id;
            }

            public void setId(String id) {
                Id = id;
            }

            public String getAdvertType() {
                return AdvertType;
            }

            public void setAdvertType(String advertType) {
                AdvertType = advertType;
            }

            public String getAdvertPath() {
                return AdvertPath;
            }

            public void setAdvertPath(String advertPath) {
                AdvertPath = advertPath;
            }

            public String getUrl() {
                return Url;
            }

            public void setUrl(String url) {
                Url = url;
            }

            public String getDuration() {
                return Duration;
            }

            public void setDuration(String duration) {
                Duration = duration;
            }

            public String getPlayOpportunity() {
                return PlayOpportunity;
            }

            public void setPlayOpportunity(String playOpportunity) {
                PlayOpportunity = playOpportunity;
            }
        }
    }
}
