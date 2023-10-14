package scc.utils;

public class QuestionDAO {
    private String _rid;
    private String _ts;
    private String id;
    private House house;
    private User postUser;
    private String textMessage;

    public QuestionDAO(Question q) {
        this(q.getId(), q.getHouse(), q.getPostUser(), q.getTextMessage());
    }

    public QuestionDAO(String id, House house, User postUser, String textMessage) {
        this.id = id;
        this.house = house;
        this.postUser = postUser;
        this.textMessage = textMessage;
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

    public House getHouse() {
        return this.house;
    }

    public void setHouse(House house) {
        this.house = house;
    }

    public User getPostUser() {
        return this.postUser;
    }

    public void setPostUser(User postUser) {
        this.postUser = postUser;
    }

    public String getTextMessage() {
        return this.textMessage;
    }

    public void setTextMessage(String textMessage) {
        this.textMessage = textMessage;
    }

}
