package models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CommentReport {

    @SerializedName("id")
    @Expose
    private int id;
    @SerializedName("comment")
    @Expose
    private int comment;
    @SerializedName("user")
    @Expose
    private int user;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getComment() {
        return comment;
    }

    public void setComment(int note) {
        this.comment = note;
    }

    public int getUser() {
        return user;
    }

    public void setUser(int user) {
        this.user = user;
    }

}
