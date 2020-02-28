package models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Note {

    @SerializedName("id")
    @Expose
    private int id;
    @SerializedName("author_username")
    @Expose
    private String authorUsername;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("university")
    @Expose
    private int university;
    @SerializedName("university_name")
    @Expose
    private String universityName;
    @SerializedName("course")
    @Expose
    private String course;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("updated_at")
    @Expose
    private String updatedAt;
    @SerializedName("group")
    @Expose
    private String group;
    @SerializedName("avg_rating")
    @Expose
    private float avgRating;
    @SerializedName("is_author")
    @Expose
    private boolean isAuthor;
    @SerializedName("has_rated")
    @Expose
    private boolean hasRated;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAuthorUsername() {
        return authorUsername;
    }

    public void setAuthorUsername(String authorUsername) {
        this.authorUsername = authorUsername;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getUniversity() {
        return university;
    }

    public void setUniversity(int university) {
        this.university = university;
    }

    public String getUniversityName() {
        return universityName;
    }

    public void setUniversityName(String universityName) {
        this.universityName = universityName;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getGroup() { return group; }

    public void setGroup(String group) { this.group = group; }

    public float getAvgRating() { return avgRating; }

    public void setAvgRating(float avg) { this.avgRating = avg; }

    public boolean isAuthor() {
        return isAuthor;
    }

    public void setIsAuthor(boolean author) {
        isAuthor = author;
    }

    public boolean hasRated() {
        return hasRated;
    }

    public void setHasRated(boolean hasRated) {
        this.hasRated = hasRated;
    }
}