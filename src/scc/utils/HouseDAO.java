package scc.utils;

import java.util.Map;

public class HouseDAO {
    private String _rid;
    private String _ts;
    private String id;
    private String name;
    private String location;
    private String description;
    private String photoId;
    private String userId;
    private int basePrice;
    private Map<String, Accessibility> available;
    // TODO: make the availability system

    public HouseDAO() {
    }

    public HouseDAO(House h) {
        this(h.getId(), h.getName(), h.getLocation(), h.getDescription(), h.getPhotoId(), h.getUserId(),
                h.getBasePrice(),
                h.getAvailable());
    }

    public HouseDAO(String id, String name, String location, String description, String photoId, String userId,
            int basePrice,
            Map<String, Accessibility> available) {
        super();
        this.id = id;
        this.name = name;
        this.location = location;
        this.description = description;
        this.photoId = photoId;
        this.userId = userId;
        this.basePrice = basePrice;
        this.available = available;
    }

    public String get_rid() {
        return this._rid;
    }

    public void set_rid(String _rid) {
        this._rid = _rid;
    }

    public String get_ts() {
        return this._ts;
    }

    public void set_ts(String _ts) {
        this._ts = _ts;
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

    public String getUserId() {
        return this.userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getBasePrice() {
        return this.basePrice;
    }

    public void setBasePrice(int basePrice) {
        this.basePrice = basePrice;
    }

    public Map<String, Accessibility> getAvailable() {
        return this.available;
    }

    public void setAvailable(Map<String, Accessibility> available) {
        this.available = available;
    }

    @Override
    public String toString() {
        return "HouseDAO [_rid=" + _rid + ", _ts=" + _ts + ", id=" + id + ", name=" + name + ", location=" + location
                + ", description=" + description + ", photoId=" + photoId + "]";
    }
}
