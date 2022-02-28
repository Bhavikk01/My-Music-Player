package com.example.mymusicplr;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

public class PlaySong extends AppCompatActivity {

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mediaPlayer.stop();
        mediaPlayer.release();
        UpdateSeek.interrupt();

    }

    TextView songName;
    String songname;
    ImageView play, next, previous;
    ArrayList<File> songs;
    MediaPlayer mediaPlayer;
    int position;
    SeekBar seekbar;
    Thread UpdateSeek;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_song);

        songName = findViewById(R.id.songName);
        play = findViewById(R.id.play);
        next = findViewById(R.id.next);
        previous = findViewById(R.id.previous);
        seekbar = findViewById(R.id.seekBar);

        Intent intent = getIntent();

        Bundle bundle = intent.getExtras();

        songs = (ArrayList) bundle.getParcelableArrayList("SongList");
        songname = intent.getStringExtra("currentSong");
        position = intent.getIntExtra("position", 0);
        songName.setText(songname);
        songName.setSelected(true);

        // Setting up MediaPlayer
        Uri uri = Uri.parse(songs.get(position).toString());
        mediaPlayer = MediaPlayer.create(this, uri);
        mediaPlayer.start();
        seekbar.setMax(mediaPlayer.getDuration());

        //Code for SeekBar management
        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

                mediaPlayer.seekTo(seekbar.getProgress());
            }
        });

        // Code for Updating SeekBar
        UpdateSeek = new Thread() {
            @Override
            public void run() {
                int currentPosition = 0;
                try {

                    while (currentPosition < mediaPlayer.getDuration()) {
                        currentPosition = mediaPlayer.getCurrentPosition();
                        seekbar.setProgress(currentPosition);
                        sleep(800);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        UpdateSeek.start();

        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(mediaPlayer.isPlaying()){
                    play.setImageResource(R.drawable.play);
                    mediaPlayer.pause();
                }
                else
                {
                    play.setImageResource(R.drawable.pause);
                    mediaPlayer.start();
                }
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mediaPlayer.stop();
                mediaPlayer.release();
                UpdateSeek.interrupt();

                if(position!= songs.size()-1){
                    position++;
                }
                else
                {
                    position = 0;
                }
                Uri uri = Uri.parse(songs.get(position).toString());
                mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
                mediaPlayer.start();
                seekbar.setMax(mediaPlayer.getDuration());
                seekbar.setProgress(0);
                play.setImageResource(R.drawable.pause);
                songname = songs.get(position).getName();
                songName.setText(songname);
            }
        });

        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mediaPlayer.stop();
                mediaPlayer.release();
                UpdateSeek.interrupt();

                if(position!=0){
                    position--;
                }
                else
                {
                    position = songs.size()-1;
                }
                Uri uri = Uri.parse(songs.get(position).toString());
                mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
                mediaPlayer.start();
                seekbar.setProgress(0);
                seekbar.setMax(mediaPlayer.getDuration());
                songname = songs.get(position).getName();
                play.setImageResource(R.drawable.pause);
                songName.setText(songname);
            }
        });

    }
}