package com.pythonanywhere.tpaule.musicrequests;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.opencsv.CSVReader;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class MainActivity extends AppCompatActivity implements CustomAdapter2.ListItemListener {

    TextView currentArtist;
    TextView currentSong;
    RecyclerView songList;
    Button suggestButton;
    RelativeLayout suggestionBox;
    AutoCompleteTextView suggestionSong;
    AutoCompleteTextView suggestionArtist;


    ArrayList<MyListItem> listItems = new ArrayList<>();
    CustomAdapter2 adapter;
    RecyclerView.LayoutManager mLayoutManager;

    Random random = new Random();

    SharedPreferences sharedPreferences;
    int roomID;
    ViewFlipper flipper;

    private Handler handler;
    private Runnable runnable;

    static int maxCharacters = 18;

    String[] artists;
    String[] songs;
    HashMap<String,ArrayList<String>> artistSongMap = new HashMap<>();


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sharedPreferences = getSharedPreferences("main",MODE_PRIVATE);

        extractPopular();
        Set<String> artistSet = sharedPreferences.getStringSet("artists",null);
        Set<String> songSet = sharedPreferences.getStringSet("songs",null);
        artists = artistSet.toArray(new String[artistSet.size()]);
        songs = songSet.toArray(new String[songSet.size()]);

        flipper = findViewById(R.id.flipper);

        currentSong = findViewById(R.id.current_song);
        currentArtist = findViewById(R.id.current_artist);
        songList = findViewById(R.id.song_list);
        songList.setHasFixedSize(true);
        Typeface face=Typeface.createFromAsset(getAssets(),
                "Comic.ttf");
        suggestButton = findViewById(R.id.suggest_button);
        suggestionBox = findViewById(R.id.suggestion_box);
        suggestionSong = findViewById(R.id.suggestion_song_input);
        suggestionArtist = findViewById(R.id.suggestion_artist_input);

        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, songs);
        suggestionSong.setThreshold(1);
        suggestionSong.setAdapter(adapter1);
        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, artists);
        suggestionArtist.setThreshold(1);
        suggestionArtist.setAdapter(adapter2);

        currentSong.setTypeface(face);
        currentArtist.setTypeface(face);
        suggestButton.setTypeface(face);

        setDetails("Super long song name",maxCharacters);
        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                refreshUI();
                handler.postDelayed(this, 200);
            }
        };


        suggestionSong.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    // code to execute when EditText loses focus
                    updateSuggestionBoxes(true);
                }
            }
        });
        suggestionArtist.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    // code to execute when EditText loses focus
                    updateSuggestionBoxes(false);
                }
            }
        });


        handler.postDelayed(runnable, 200);


        mLayoutManager = new LinearLayoutManager(this);
        songList.setLayoutManager(mLayoutManager);


    }

    public void enteredRoomID(View view) {
        EditText edit = findViewById(R.id.room_id_edit);
        try {
            roomID = Integer.parseInt(edit.getText().toString());
            flipper.showNext();
            setupList();
            hideSoftKeyboard(this);
        }
        catch (Exception e) {
            edit.setText("");

        }

    }

    private void setupList(){
        // room id stored in roomID variable
        // get current song & artist
        // MARIUS
        String currentSongName = "PLACEHOLDER SONG"; // replace with current song name
        String currentArtistName = "PLACEHOLDER ARTIST"; // replace with current artist name
        setDetails(currentSongName,maxCharacters);
        setDisplayText();
        currentArtist.setText(currentArtistName);

        listItems = new ArrayList<>();
        // get a list of songs from the server
        // MARIUS, replace the for loop below (only placeholder so there are items in the list)
        for (int i = 0; i < 10; i++) {
            String song = getRandomString();
            String artist = getRandomString();
            int upvotes = random.nextInt(10);
            listItems.add(new MyListItem(artist,song,upvotes));
        }
        sortList();

        adapter = new CustomAdapter2(this,listItems);
        songList.setAdapter(adapter);

        refreshList();
    }

    private void updateSuggestionBoxes(boolean songEdited) {
        String song = suggestionSong.getText().toString();
        String artist = suggestionArtist.getText().toString();
        if (song.equals("") && artist.equals("")){
            // both boxes are empty, reset autocomplete dropdown boxes
            ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, songs);
            suggestionSong.setThreshold(1);
            suggestionSong.setAdapter(adapter1);
            ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, artists);
            suggestionArtist.setThreshold(1);
            suggestionArtist.setAdapter(adapter2);
        }
        else if (song.equals("")) {
            // song is empty, if the artist is in the hashmap, add their songs to dropdown
            ArrayList<String> songsAL = artistSongMap.get(artist);
            if (songsAL != null && songsAL.size() > 0) {
                String[] artistSongs = songsAL.toArray(new String[songsAL.size()]);
                ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, artistSongs);
                suggestionSong.setThreshold(1);
                suggestionSong.setAdapter(adapter1);
            }
            else {
                String[] empty = {};
                ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, empty);
                suggestionSong.setThreshold(1);
                suggestionSong.setAdapter(adapter1);
            }


            ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, artists);
            suggestionArtist.setThreshold(1);
            suggestionArtist.setAdapter(adapter2);
        }
        else if (artist.equals("")) {
            // artist is empty, if song is valid, add its artist to dropdown
            for (String a : artistSongMap.keySet()) {
                for (String sng : artistSongMap.get(a)){
                    if (sng.equals(song)) {
                        String[] artist_arr = {a};
                        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, artist_arr);
                        suggestionArtist.setThreshold(1);
                        suggestionArtist.setAdapter(adapter2);
                        return;
                    }
                }
            }
            String[] empty = {};
            ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, empty);
            suggestionArtist.setThreshold(1);
            suggestionArtist.setAdapter(adapter2);

        }
    }

    public void suggestSong(View view) {

        String songName = suggestionSong.getText().toString();
        String artistName = suggestionArtist.getText().toString();
        suggestionBox.setVisibility(View.GONE);

        if (songName.equals("") || artistName.equals("")) {
            // some field was left empty, not sending
            return;
        }

        // MARIUS
        // send songName & artistName

        hideSoftKeyboard(this);
    }

    public void openSuggestBox(View view) {
        suggestionBox.setVisibility(View.VISIBLE);
    }

    @Override
    public void upvoteItem(MyListItem itemToUpvote, int itemPosition) {
        if (itemToUpvote.upvoted){
            itemToUpvote.upvoted = false;
            itemToUpvote.upvotes--;
            // send to say that we unupvoted
            // MARIUS
        }
        else {
            // first check if we had some other song upvoted
            // we want to unupvote that, and notify the server
            for (MyListItem item : listItems) {
                if (item.upvoted && item != itemToUpvote) {
                    item.upvoted = false;
                    item.upvotes--;
                    // send to say we unupvoted
                    // MARIUS
                    break;
                }
            }
            itemToUpvote.upvoted = true;
            itemToUpvote.upvotes++;
        }
        sortList();
        adapter.notifyItemRangeChanged(0,listItems.size());
    }

    private String getRandomString() {
        String res = "";
        String alphabet = "abcdefghijklmnopqrstuvwxyz";
        for (int i = 0; i < 10; i++){
            res += alphabet.charAt(random.nextInt(26));
        }
        return res;
    }

    private void sortList(){
        /*
        Sorts the song list from most upvoted to least upvoted
        Priority given to the song user upvoted
         */
        ArrayList<MyListItem> newList = new ArrayList<>();
        while (listItems.size() > 0) {
            int maxItemIndex = 0;
            for (int i = 0; i < listItems.size(); i++) {
                if (listItems.get(i).upvotes > listItems.get(maxItemIndex).upvotes
                        || (listItems.get(i).upvotes == listItems.get(maxItemIndex).upvotes && listItems.get(i).upvoted)) {
                    maxItemIndex = i;
                }
            }
            newList.add(listItems.get(maxItemIndex));
            listItems.remove(maxItemIndex);

        }
        listItems.addAll(newList);
    }

    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager)  activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
    }

    public void refreshList() {
        /*
        Here the app queries the site and gets the most recent version of the song list
        Current song / artist is updated
        Song list is sorted and updated
         */


        // get current song & artist name here
        // MARIUS
        String currentSongName = "PLACEHOLDER SONG"; // replace with current song name
        String currentArtistName = "PLACEHOLDER ARTIST"; // replace with current artist name
        //currentSong.setText(currentSongName);
        if (!fullText.equals(currentSongName)) {
            // have to update current song text
            setDetails(currentSongName,maxCharacters);
            setDisplayText();
        }
        currentArtist.setText(currentArtistName);

        // here get list from server
        // MARIUS
        //listItems = newListFromServer;
        sortList();
        adapter.notifyDataSetChanged();
    }

    public void refreshUI() {
        scrollByOne();
        setDisplayText();

    }

    // getting some popular artists / songs

    private void extractPopular() {

        Set<String> songs = new HashSet<>();
        Set<String> artists = new HashSet<>();

        InputStream inputStream = getResources().openRawResource(R.raw.popular);
        CSVFile csvFile = new CSVFile(inputStream);
        ArrayList<String[]> resultList = csvFile.read();

        for(String[] entry : resultList ) {
            String songName = entry[1];
            if (songName.length() > 1 && songName.charAt(0) == '\"'){
                songName = songName.substring(1,songName.length()-1);
            }
            songName = songName.replace('^',',');
            String artist = entry[2];
            if (artist.length() > 1 && artist.charAt(0) == '\"'){
                artist = artist.substring(1,artist.length()-1);
            }
            artist = artist.replace('^',',');
            if (artist.length() > 0 && !artist.equals("Artist")) {
                songs.add(songName);
                artists.add(artist);
                ArrayList<String[]> emptyTmp = new ArrayList<>();
                ArrayList<String> current = artistSongMap.get(artist);
                if (current == null) {
                    current = new ArrayList<>();
                }
                current.add(songName);
                artistSongMap.put(artist,current);
            }
        }

        for (String key : artistSongMap.keySet()) {
        }
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putStringSet("songs",songs);
        editor.putStringSet("artists",artists);
        editor.apply();
    }

    // scrolling text methods

    String fullText;
    int offset = 0;

    public void setDetails(String fullText, int maxChars) {
        this.fullText = fullText;
        maxCharacters = maxChars;
        this.offset = 0;
    }

    public void setDisplayText() {
        String doubleFullText = fullText + "       " + fullText + "       ";
        currentSong.setText(doubleFullText.substring(offset,offset+maxCharacters));

    }

    public void scrollByOne() {
        offset++;
        if (offset > fullText.length() + 7) {
            offset -= fullText.length() + 7;
        }
    }
}
