package models;

import android.os.Parcel;
import android.os.Parcelable;

public class CardView {
    private int noteId;
    private int imageFavorite;
    private String title;
    private String university;
    private String course;
    private String name;
    private String type;
    private float avgRating;
    private boolean isAuthor;
    private boolean isModerator;

    public CardView(int noteId, String title, String university, String course, String name, float avgRating, boolean isAuthor, boolean isModerator, int imageFavorite, String type) {
        this.noteId = noteId;
        this.title = title;
        this.university = university;
        this.course = course;
        this.name = name;
        this.type = type;
        this.avgRating = avgRating;
        this.isAuthor = isAuthor;
        this.isModerator = isModerator;
        this.imageFavorite = imageFavorite;
    }

    public int getNoteId() {
        return this.noteId;
    }

    public void changeTitle(String text) {
        this.title = text;
    }

    public int getimageFavorite() {
        return this.imageFavorite;
    }

    public void setNoteId(int noteId) {
        this.noteId = noteId;
    }

    public void setImageFavorite(int imageFavorite) {
        this.imageFavorite = imageFavorite;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUniversity() {
        return this.university;
    }

    public void setUniversity(String university) {
        this.university = university;
    }

    public String getCourse() {
        return this.course;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {return this.type;}

    public void setType(String type) {this.type = type;}

    public float getAvgRating() {
        return this.avgRating;
    }

    public void setAvgRating(float avg) {
        this.avgRating = avg;
    }

    public boolean getIsAuthor() { return this.isAuthor; }

    public void setIsAuthor(boolean isAuthor) { this.isAuthor = isAuthor; }

    public boolean getIsModerator() { return this.isModerator; }

    public void setIsModerator(boolean isModerator) { this.isModerator = isModerator; }

}
