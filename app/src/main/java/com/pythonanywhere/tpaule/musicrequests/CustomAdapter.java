package com.pythonanywhere.tpaule.musicrequests;

import android.content.Context;
import android.graphics.PorterDuff;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class CustomAdapter extends ArrayAdapter<MyListItem> implements View.OnClickListener{

    private ArrayList<MyListItem> dataSet;
    Context mContext;
    ListItemListener listener;

    // View lookup cache
    private static class ViewHolder {
        TextView txtArtist;
        TextView txtTrackName;
        TextView txtUpvotes;
        ImageView buttonUpvote;
    }

    public CustomAdapter(ArrayList<MyListItem> data, Context context) {
        super(context, R.layout.list_view_template, data);
        this.dataSet = data;
        this.mContext=context;
        this.listener = (ListItemListener)context;
    }

    @Override
    public void onClick(View v) {

        int position=(Integer) v.getTag();
        Object object= getItem(position);
        MyListItem thisItem=(MyListItem)object;

    }

    private int lastPosition = -1;

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        final MyListItem thisItem = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored fadeInAnimation tag

        //final View result;

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.list_view_template, parent, false);
            viewHolder.txtArtist = convertView.findViewById(R.id.artist_name);
            viewHolder.txtTrackName = convertView.findViewById(R.id.track_name);
            viewHolder.txtUpvotes = convertView.findViewById(R.id.num_of_upvotes);
            viewHolder.buttonUpvote = convertView.findViewById(R.id.upvote_song_button);


            //result=convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            //result=convertView;
        }


        lastPosition = position;

        viewHolder.txtArtist.setText(thisItem.artist);
        viewHolder.txtTrackName.setText(thisItem.songName);
        viewHolder.txtUpvotes.setText(String.valueOf(thisItem.upvotes));

        int notUpvotedColor = getContext().getResources().getColor(R.color.colorNotUpvoted);
        int upvotedColor = getContext().getResources().getColor(R.color.colorUpvoted);
        if (thisItem.upvoted) {
            viewHolder.buttonUpvote.getDrawable().mutate().setColorFilter(upvotedColor, PorterDuff.Mode.MULTIPLY);
        }
        else {
            viewHolder.buttonUpvote.getDrawable().mutate().setColorFilter(notUpvotedColor, PorterDuff.Mode.MULTIPLY);
        }
        viewHolder.buttonUpvote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.upvoteItem(thisItem);
            }
        });



        return convertView;
    }

    public interface ListItemListener {
        void upvoteItem(MyListItem itemToUpvote);
    }


}


