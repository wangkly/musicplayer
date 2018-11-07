package com.example.wangky.mymusicplayer;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import java.io.File;
import java.net.URI;

public class MusicPlayActivity extends AppCompatActivity implements View.OnClickListener{


    private MediaPlayer mediaPlayer = new MediaPlayer();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_play);

        Intent intent = getIntent();

        String uri =intent.getStringExtra("uri");

        initMediaPlayer(uri);

        Button play =findViewById(R.id.play);
        Button pause =findViewById(R.id.pause);
        Button stop =findViewById(R.id.stop);

        play.setOnClickListener(this);
        pause.setOnClickListener(this);
        stop.setOnClickListener(this);




        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }




    public void initMediaPlayer(String uri){

        try {
            mediaPlayer.setDataSource(uri);
            mediaPlayer.prepare();

        }catch (Exception e){
            e.printStackTrace();
        }


    }


    @Override
    public void onClick(View v) {

            switch (v.getId()){

                case R.id.play:
                    if(!mediaPlayer.isPlaying()){
                        mediaPlayer.start();
                    }

                    break;

                case R.id.pause:
                    if(mediaPlayer.isPlaying()){
                        mediaPlayer.pause();
                    }
                    break;
                case R.id.stop:

                    if(mediaPlayer.isPlaying()){
                        mediaPlayer.stop();
                    }

                    break;

                 default:
                     break;
            }

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(mediaPlayer != null){
            mediaPlayer.stop();
            mediaPlayer.release();
        }
    }
}
