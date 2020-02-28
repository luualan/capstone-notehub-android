package models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Invitation {

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
    @SerializedName("moderator_username")
    @Expose
    private String moderatorUsername;

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

    public String getModeratorUsername() {
        return moderatorUsername;
    }

    public void setModeratorUsername(String moderatorUsername) {
        this.moderatorUsername = moderatorUsername;
    }
}
