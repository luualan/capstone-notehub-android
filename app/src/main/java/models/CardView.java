package models;

import android.os.Parcel;
import android.os.Parcelable;

public class CardView implements Parcelable {
    private int noteId;
    private int imageFavorite;
    private String title;
    private String university;
    private String course;
    private String name;
    private float avgRating;
    private boolean isAuthor;

    public CardView(int noteId, String title, String university, String course, String name, float avgRating, boolean isAuthor, int imageFavorite) {
        this.noteId = noteId;
        this.title = title;
        this.university = university;
        this.course = course;
        this.name = name;
        this.avgRating = avgRating;
        this.isAuthor = isAuthor;
        this.imageFavorite = imageFavorite;
    }

    // order of this has to be same as writeToParcel (read/write in same order)
    public CardView(Parcel in) {
        imageFavorite = in.readInt();
        title = in.readString();
        university = in.readString();
        course = in.readString();
        name = in.readString();
        avgRating = in.readFloat();
        isAuthor = 0 < in.readInt();

       // files = new ArrayList<>();
       // in.readList(files, CardView.class.getClassLoader());
    }

    // The creator uses the constructor with Parcel in to read these values back in
    public static final Creator<CardView> CREATOR = new Creator<CardView>() {
        @Override
        public CardView createFromParcel(Parcel in) {
            return new CardView(in);
        }

        @Override
        public CardView[] newArray(int size) {
            return new CardView[size];
        }
    };

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

    public float getAvgRating() {
        return this.avgRating;
    }

    public void setAvgRating(float avg) {
        this.avgRating = avg;
    }

    public boolean getIsAuthor() { return this.isAuthor; }

    public void setIsAuthor(boolean isAuthor) { this.isAuthor = isAuthor; }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(imageFavorite);
        dest.writeString(title);
        dest.writeString(university);
        dest.writeString(course);
        dest.writeString(name);
        dest.writeFloat(avgRating);
        dest.writeInt(isAuthor ? 1 : 0);
    }
}
