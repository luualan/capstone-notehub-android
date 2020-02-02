package models;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class NoteFile {

    @SerializedName("note")
    @Expose
    private int note;
    @SerializedName("index")
    @Expose
    private int index;
    @SerializedName("file")
    @Expose
    private String file;
    @SerializedName("created_at")
    @Expose
    private String createdAt;

    public int getNote() {
        return note;
    }

    public void setNote(int note) {
        this.note = note;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

}