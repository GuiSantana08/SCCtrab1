package scc.utils;

public class Question {
    private String id;
    private House house;
    private User postUser;
    private String textMessage;

    public Question(String id, House house, User postUser, String textMessage) {
        this.id = id;
        this.house = house;
        this.postUser = postUser;
        this.textMessage = textMessage;
    }

    public Question() {
        super();
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
