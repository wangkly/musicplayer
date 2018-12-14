package com.example.wangky.mymusicplayer;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.File;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MusicPlayActivity extends AppCompatActivity implements View.OnClickListener{


    private MediaPlayer mediaPlayer = new MediaPlayer();

    SeekBar seekBar;



    ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {

            MyService.MyBinder myBinder = (MyService.MyBinder) service;

            myBinder.UpdateSeekBarUi(MusicPlayActivity.this.mediaPlayer,MusicPlayActivity.this.handler);


        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };


    //接受多线程信息，安卓中不允许主线程实现UI更新
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            seekBar.setProgress(msg.what);

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_play);

        Intent intent = getIntent();

        String uri =intent.getStringExtra("uri");

        int duration = intent.getIntExtra("duration",0);

        initMediaPlayer(uri);

        Button play =findViewById(R.id.play);
        Button pause =findViewById(R.id.pause);
        Button stop =findViewById(R.id.stop);
        TextView durationMax = findViewById(R.id.durationMax);

        seekBar = findViewById(R.id.seekBar);

        String time = this.formatTime(duration);
        durationMax.setText(time);


        seekBar.setMax(duration);

        play.setOnClickListener(this);
        pause.setOnClickListener(this);
        stop.setOnClickListener(this);


        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(

        ) {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser && mediaPlayer.isPlaying()){
                    mediaPlayer.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });



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



    private String formatTime(int length) {

        Date date = new Date(length);//调用Date方法获值

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("mm:ss");//规定需要形式

        String TotalTime = simpleDateFormat.format(date);//转化为需要形式

        return TotalTime;

    }



    @Override
    public void onClick(View v) {

            switch (v.getId()){

                case R.id.play:
                    if(!mediaPlayer.isPlaying()){
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                mediaPlayer.start();
                            }
                        }).start();

//                        new Thread(new SeekBarThread()).start();

                        Intent intent = new Intent(MusicPlayActivity.this,MyService.class);

                        bindService(intent,connection,BIND_AUTO_CREATE);

                    }

                    break;

                case R.id.pause:
                    if(mediaPlayer.isPlaying()){
                        mediaPlayer.pause();

                        unbindService(connection);
                    }
                    break;
                case R.id.stop:

                    if(mediaPlayer.isPlaying()){
                        mediaPlayer.stop();

                        unbindService(connection);
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






    class SeekBarThread implements  Runnable{

        @Override
        public void run() {

            while (null != mediaPlayer && mediaPlayer.isPlaying()){

               int progress = mediaPlayer.getCurrentPosition();

               handler.sendEmptyMessage(progress);

               try {
                   Thread.sleep(100);
               }catch (Exception e){
                   e.printStackTrace();
               }

            }


        }
    }

}
