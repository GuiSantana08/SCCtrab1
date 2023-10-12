package scc.utils;

public class QuestionDAO {
    House house;
    User postUser;
    String textMessage;

    public QuestionDAO(House house, User postUser, String textMessage) {
        this.house = house;
        this.postUser = postUser;
        this.textMessage = textMessage;
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
