package scc.utils;

import java.util.Arrays;

public class House {

    private String id;
    private String name;
    private String location;
    private String description;
    private String photoId;

    private String[] houseLocations;


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

    public String[] getHouseLocations() {
        return houseLocations == null ? new String[0] : houseLocations;
    }

    public void setHouseLocations(String[] houseLocations) {
        this.houseLocations = houseLocations;
    }

    @Override
    public String toString() {
        return "House [id=" + id + ", name=" + name + ", location=" + location + ", description=" + description
                + ", photoId=" + photoId + ", houseLocations=" + Arrays.toString(houseLocations) + "]";
    }
}
