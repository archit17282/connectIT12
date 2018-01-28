package com.example.architpanwar.connected;

/**
 * Created by architpanwar on 15/04/17.
 */

public class freindlychat {


    private String text;
    private String name;
    private String photoUrl;


    public freindlychat() {
    }

    public freindlychat(String text, String name, String photoUrl) {
        this.text = text;
        this.name = name;
        this.photoUrl = photoUrl;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }


}
