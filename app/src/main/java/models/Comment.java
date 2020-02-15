package models;

public class Comment {
    private String name;
    private String description;
    private int photo;

    // Default Constructor
    public Comment() {

    }

    // Constructor
    public Comment(String name, String description, int photo) {
        this.name = name;
        this.description = description;
        this.photo = photo;
    }

    // Accessors
    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getPhoto() {
        return photo;
    }

    // Mutators
    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPhoto(int photo) {
        this.photo = photo;
    }
}
