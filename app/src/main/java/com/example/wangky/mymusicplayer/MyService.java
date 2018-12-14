package com.example.wangky.mymusicplayer;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

import static android.content.ContentValues.TAG;

public class MyService extends Service {

    public MyService() {

    }

    public class  MyBinder extends Binder{
        public void showToast(){
            Toast.makeText(MyService.this.getApplicationContext(),"MyService",Toast.LENGTH_LONG);
        }

        public void UpdateSeekBarUi(final MediaPlayer mediaPlayer, final Handler handler){

            final Timer timer = new Timer();
                         new Thread(new Runnable() {
                @Override
                public void run() {
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {

                            if(null != mediaPlayer && mediaPlayer.isPlaying()){
                                int progress = mediaPlayer.getCurrentPosition();
                                handler.sendEmptyMessage(progress);
                            }
                        }
                    },0L,1000L);
                }
            }).start();






//                new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//
//                        while (null != mediaPlayer && mediaPlayer.isPlaying()){
//
//                            int progress = mediaPlayer.getCurrentPosition();
//
//                            handler.sendEmptyMessage(progress);
//
//                            try {
//                                Thread.sleep(1000);
//                            }catch (Exception e){
//                                e.printStackTrace();
//                            }
//
//                        }
//
//                    }
//                }).start();
        }



        public MyService getMyService(){
            return  MyService.this;
        }

    }



    @Override
    public void onCreate() {
        Log.i(TAG, "onCreate: trigger");
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "onStartCommand: trigger");

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "onDestroy: trigger");
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return new MyBinder();
    }
}
