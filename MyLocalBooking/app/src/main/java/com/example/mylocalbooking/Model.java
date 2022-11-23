package com.example.mylocalbooking;

public class Model {

    private int imagePeople;
    private String blackList_name;
    private String blackList_email;

    public Model(int imagePeople, String blackList_name, String blackList_email) {
        this.imagePeople = imagePeople;
        this.blackList_name = blackList_name;
        this.blackList_email = blackList_email;
    }

    public int getImagePeople() {
        return imagePeople;
    }

    public void setImagePeople(int imagePeople) {
        this.imagePeople = imagePeople;
    }

    public String getBlackList_name() {
        return blackList_name;
    }

    public void setBlackList_name(String blackList_name) {
        this.blackList_name = blackList_name;
    }

    public String getBlackList_email() {
        return blackList_email;
    }

    public void setBlackList_email(String blackList_email) {
        this.blackList_email = blackList_email;
    }



}
