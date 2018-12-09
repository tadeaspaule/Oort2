package com.pythonanywhere.tpaule.musicrequests;

import android.graphics.Color;

public class MyListItem {

    String artist;
    String songName;
    int upvotes;
    boolean upvoted;


    public MyListItem(String artist, String songName, int upvotes){
        this.artist = artist;
        this.songName = songName;
        this.upvotes = upvotes;
        this.upvoted = false;
    }


}