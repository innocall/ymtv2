package com.lemon95.ymtv.bean;

import java.util.List;

/**
 * Created by WXT on 2016/7/19.
 * 影视类型
 */
public class Conditions {

    private List<Groups> Groups;
    private List<Areas> Areas;
    private List<Genres> Genres;

    public List<Conditions.Groups> getGroups() {
        return Groups;
    }

    public void setGroups(List<Conditions.Groups> groups) {
        Groups = groups;
    }

    public List<Conditions.Areas> getAreas() {
        return Areas;
    }

    public void setAreas(List<Conditions.Areas> areas) {
        Areas = areas;
    }

    public List<Conditions.Genres> getGenres() {
        return Genres;
    }

    public void setGenres(List<Conditions.Genres> genres) {
        Genres = genres;
    }

    public static class Groups {
        private String GroupId;
        private String GroupName;

        public String getGroupId() {
            return GroupId;
        }

        public void setGroupId(String groupId) {
            GroupId = groupId;
        }

        public String getGroupName() {
            return GroupName;
        }

        public void setGroupName(String groupName) {
            GroupName = groupName;
        }
    }

    public static class Areas {
        private String Id;
        private String AreaName;

        public String getId() {
            return Id;
        }

        public void setId(String id) {
            Id = id;
        }

        public String getAreaName() {
            return AreaName;
        }

        public void setAreaName(String areaName) {
            AreaName = areaName;
        }
    }

    public static class Genres {
        private String Id;
        private String TypeName;

        public String getId() {
            return Id;
        }

        public void setId(String id) {
            Id = id;
        }

        public String getTypeName() {
            return TypeName;
        }

        public void setTypeName(String typeName) {
            TypeName = typeName;
        }
    }
}
