package com.pythonanywhere.tpaule.musicrequests;

import android.support.v7.util.DiffUtil;

import java.util.ArrayList;

public class MyDiffUtilCallback extends DiffUtil.Callback {
    ArrayList<MyListItem> oldList;
    ArrayList<MyListItem> newList;

    MyDiffUtilCallback(ArrayList<MyListItem> newList, ArrayList<MyListItem> oldList) {
        this.newList = newList;
        this.oldList = oldList;
    }

    @Override
    public int getOldListSize() {
        return oldList.size();
    }

    @Override
    public int getNewListSize() {
        return newList.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        boolean artistNameSame =
                oldList.get(oldItemPosition).artist.equals(newList.get(oldItemPosition).artist);
        boolean songNameSame =
                oldList.get(oldItemPosition).songName.equals(newList.get(oldItemPosition).songName);
        boolean numUpvotesSame =
                oldList.get(oldItemPosition).upvotes == newList.get(oldItemPosition).upvotes;
        boolean isUpvotedSame =
                oldList.get(oldItemPosition).upvoted == newList.get(oldItemPosition).upvoted;
        return artistNameSame && songNameSame && numUpvotesSame && isUpvotedSame;
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        boolean artistNameSame =
                oldList.get(oldItemPosition).artist.equals(newList.get(oldItemPosition).artist);
        boolean songNameSame =
                oldList.get(oldItemPosition).songName.equals(newList.get(oldItemPosition).songName);
        boolean numUpvotesSame =
                oldList.get(oldItemPosition).upvotes == newList.get(oldItemPosition).upvotes;
        boolean isUpvotedSame =
                oldList.get(oldItemPosition).upvoted == newList.get(oldItemPosition).upvoted;
        return artistNameSame && songNameSame && numUpvotesSame && isUpvotedSame;
    }

    @Override
    public Object getChangePayload(int oldItemPosition, int newItemPosition) {
        return super.getChangePayload(oldItemPosition, newItemPosition);
    }
}