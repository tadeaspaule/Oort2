package com.pythonanywhere.tpaule.musicrequests;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class CustomAdapter2 extends RecyclerView.Adapter<CustomAdapter2.ViewHolder>  {

    private ArrayList<MyListItem> mData;
    private LayoutInflater mInflater;
    Context mContext;
    CustomAdapter2.ListItemListener listener;

    // data is passed into the constructor
    CustomAdapter2(Context context, ArrayList<MyListItem> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
        this.mContext = context;
        this.listener = (ListItemListener)context;
    }

    public void setList(ArrayList<MyListItem> newList) {
        ArrayList<MyListItem> oldList = this.mData;
        this.mData = newList;
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new MyDiffUtilCallback(newList, oldList), true);
        diffResult.dispatchUpdatesTo(this);
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.list_view_template, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        MyListItem thisItem = mData.get(position);
        final MyListItem fItem = mData.get(position);
        final ViewHolder fHolder = holder;
        holder.txtArtist.setText(thisItem.artist);
        holder.txtSongName.setText(thisItem.songName);
        holder.txtNumUpvotes.setText(String.valueOf(thisItem.upvotes));

        int heartColor = mContext.getResources().getColor(R.color.colorHeart);
        if (thisItem.upvoted) {
            Drawable d = mContext.getResources().getDrawable(R.drawable.heart_filled);
            holder.buttonUpvote.setImageDrawable(d);
            holder.buttonUpvote.getDrawable().mutate().setColorFilter(heartColor, PorterDuff.Mode.MULTIPLY);

        }
        else {
            Drawable d = mContext.getResources().getDrawable(R.drawable.heart_outline);
            holder.buttonUpvote.setImageDrawable(d);
            holder.buttonUpvote.getDrawable().mutate().setColorFilter(heartColor, PorterDuff.Mode.MULTIPLY);

        }
        holder.buttonUpvote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.upvoteItem(fItem,fHolder.getAdapterPosition());
            }
        });
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mData.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtArtist;
        TextView txtSongName;
        TextView txtNumUpvotes;
        ImageView buttonUpvote;


        ViewHolder(View itemView) {
            super(itemView);
            txtArtist = itemView.findViewById(R.id.artist_name);
            txtSongName = itemView.findViewById(R.id.track_name);
            txtNumUpvotes = itemView.findViewById(R.id.num_of_upvotes);
            buttonUpvote = itemView.findViewById(R.id.upvote_song_button);
        }
    }

    // convenience method for getting data at click position
    MyListItem getItem(int id) {
        return mData.get(id);
    }

    public interface ListItemListener {
        void upvoteItem(MyListItem itemToUpvote, int itemPosition);
    }
}
