package com.pythonanywhere.tpaule.musicrequests;

import android.graphics.Color;

public class MyListItem {

    String artist;
    String songName;
    int upvotes;
    boolean upvoted;
    String songID;


    public MyListItem(String artist, String songName, int upvotes, String songID){
        this.artist = artist;
        this.songName = songName;
        this.upvotes = upvotes;
        this.upvoted = false;
        this.songID = songID;
    }


}