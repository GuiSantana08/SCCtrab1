package scc.utils;

public class House {

    private String id;
    private String name;
    private String location;
    private String description;
    private String photoId;
    // TODO: should have an associated user, the owner

    public House(String id, String name, String location, String description, String photoId) {
        super();
        this.id = id;
        this.name = name;
        this.location = location;
        this.description = description;
        this.photoId = photoId;

    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return this.location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPhotoId() {
        return this.photoId;
    }

    public void setPhotoId(String photoId) {
        this.photoId = photoId;
    }

    @Override
    public String toString() {
        return "House [id=" + id + ", name=" + name + ", location=" + location + ", description=" + description
                + ", photoId=" + photoId + "]";
    }
}
