package scc.utils;

import java.util.List;

/**
 * Represents a User, as returned to the clients
 *
 * NOTE: array of house ids is shown as an example of how to store a list of
 * elements and
 * handle the empty list.
 */
public class User {
    private String id;
    private String name;
    private String pwd;
    private String photoId;
    private List<String> houseIds;

    public User(String id, String name, String pwd, String photoId, List<String> houseIds) {
        super();
        this.id = id;
        this.name = name;
        this.pwd = pwd;
        this.photoId = photoId;
        this.houseIds = houseIds;
    }

    public User() {
        super();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public String getPhotoId() {
        return photoId;
    }

    public void setPhotoId(String photoId) {
        this.photoId = photoId;
    }

    public List<String> getHouseIds() {
        return houseIds;
    }

    public void setHouseIds(List<String> houseIds) {
        this.houseIds = houseIds;
    }

    @Override
    public String toString() {
        return "User [id=" + id + ", name=" + name + ", pwd=" + pwd + ", photoId=" + photoId + ", houseIds="
                + houseIds.toString() + "]";
    }

}
