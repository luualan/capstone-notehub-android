package models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Membership {

    @SerializedName("id")
    @Expose
    private int id;
    @SerializedName("group")
    @Expose
    private int group;
    @SerializedName("group_name")
    @Expose
    private String groupName;
    @SerializedName("user")
    @Expose
    private int user;
    @SerializedName("username")
    @Expose
    private String username;
    @SerializedName("role")
    @Expose
    private String role;
    @SerializedName("is_user")
    @Expose
    private Boolean isUser;
    @SerializedName("joined_at")
    @Expose
    private String joinedAt;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getGroup() {
        return group;
    }

    public void setGroup(int group) {
        this.group = group;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public int getUser() {
        return user;
    }

    public void setUser(int user) {
        this.user = user;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }


    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Boolean getIsUser() {
        return isUser;
    }

    public void setIsUser(Boolean user) {
        isUser = user;
    }

    public String getJoinedAt() {
        return joinedAt;
    }

    public void setJoinedAt(String joinedAt) {
        this.joinedAt = joinedAt;
    }
}
