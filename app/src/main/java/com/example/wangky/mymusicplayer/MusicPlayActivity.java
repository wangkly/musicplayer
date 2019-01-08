package com.example.wangky.mymusicplayer;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MusicPlayActivity extends AppCompatActivity implements View.OnClickListener{


    private MediaPlayer mediaPlayer = new MediaPlayer();

    SeekBar seekBar;

    ImageButton play ;

    ImageButton last;

    ImageButton next ;

    TextView start;

    TextView durationMax;

    Toolbar toolbar;

    CircleImageView albumImg;

    private Boolean isPlaying =false;

    private String mediaId ="";

    private int position=0;

    private String albumArt;

    LocalBroadcastManager localBroadcastManager;

    ServiceConnection connection = new ServiceConnection() {
        MyService.MyBinder myBinder;
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {

            myBinder = (MyService.MyBinder) service;

            myBinder.UpdateSeekBarUi(MusicPlayActivity.this.mediaPlayer,MusicPlayActivity.this.handler);


        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

            myBinder.unbindCallback();

        }
    };


    //接受多线程信息，安卓中不允许主线程实现UI更新
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            seekBar.setProgress(msg.what);
            String time = formatTime(msg.what);
            start.setText(time);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i("MusicPlayActivity", "onCreate: MusicPlayActivity ");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_play);

        localBroadcastManager = LocalBroadcastManager.getInstance(this);

        toolbar = findViewById(R.id.toolbar);
        play =findViewById(R.id.play);
        last =findViewById(R.id.last);
        next =findViewById(R.id.next);
        start = findViewById(R.id.start);
        durationMax = findViewById(R.id.durationMax);
        seekBar = findViewById(R.id.seekBar);
        albumImg = findViewById(R.id.albumImg);

        play.setOnClickListener(this);
        last.setOnClickListener(this);
        next.setOnClickListener(this);

        this.prepareMedia(true);


        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
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

    }


    public void prepareMedia(boolean isFirstTime){

        Intent intent = getIntent();

        String uri =intent.getStringExtra("uri");

        int duration = intent.getIntExtra("duration",0);

        String title = intent.getStringExtra("title");

        String artist = intent.getStringExtra("artist");

        String id = intent.getStringExtra("id");

        position= intent.getIntExtra("position",0);

        albumArt = intent.getStringExtra("albumArt");

        Bitmap bm;
        if (albumArt == null){
            albumImg.setImageDrawable(ContextCompat.getDrawable(MusicPlayActivity.this, R.drawable.music));
        } else{
            bm = BitmapFactory.decodeFile(albumArt);
            BitmapDrawable bmpDraw = new BitmapDrawable(bm);
             albumImg.setImageDrawable(bmpDraw);
        }

        //旋转动画
        Animation rotation = AnimationUtils.loadAnimation(this,R.anim.rotation);
        LinearInterpolator interpolator = new LinearInterpolator();
        rotation.setInterpolator(interpolator);
        albumImg.startAnimation(rotation);


        mediaId = id;

        initMediaPlayer(uri);

        toolbar.setTitle(title);
        toolbar.setSubtitle(artist);

        String time = this.formatTime(duration);
        start.setText("00:00");
        durationMax.setText(time);

        seekBar.setMax(duration);

        this.playMusic();

        if(isFirstTime){
            Intent ServiceIntent = new Intent(MusicPlayActivity.this,MyService.class);
            bindService(ServiceIntent,connection,BIND_AUTO_CREATE);
        }

    }



    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        String nid= intent.getStringExtra("id");
        if(null != nid && !mediaId.equals(nid)){
            setIntent(intent);
            mediaPlayer.reset();
            this.prepareMedia(false);

        }

    }

    @Override
    protected void onResume() {

        Log.i("MusicPlayActivity", "onResume: MusicPlayActivity" );

        super.onResume();
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


    public void playMusic(){
        if(!mediaPlayer.isPlaying()){
            mediaPlayer.start();
            play.setImageDrawable(getResources().getDrawable(R.drawable.pause));
        }

    }



    @Override
    public void onClick(View v) {
        Intent intent;

        switch (v.getId()){

                case R.id.play:
                    if(mediaPlayer.isPlaying()){
                        mediaPlayer.pause();
                        play.setImageDrawable(getResources().getDrawable(R.drawable.play));

                    }else{
                        mediaPlayer.start();
                        play.setImageDrawable(getResources().getDrawable(R.drawable.pause));
                    }

                    break;

                case R.id.last:
                    intent= new Intent("com.example.wangky.mymusicplayer.MainActivity.changeMedia");
                    intent.putExtra("type","previous");
                    intent.putExtra("position",position);

                    localBroadcastManager.sendBroadcast(intent);

                    break;
                case R.id.next:
                     intent = new Intent("com.example.wangky.mymusicplayer.MainActivity.changeMedia");
                    intent.putExtra("type","next");
                    intent.putExtra("position",position);
                    localBroadcastManager.sendBroadcast(intent);

                    break;

                 default:
                     break;
            }

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(connection);

        if(mediaPlayer != null){
            mediaPlayer.stop();
            mediaPlayer.reset();
            mediaPlayer.release();
        }
    }




    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
        Log.i("MusicPlayActivity", "onBackPressed: press");

        Intent intent = new Intent(MusicPlayActivity.this,MainActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.side_out,R.anim.animo_no);

    }
}
