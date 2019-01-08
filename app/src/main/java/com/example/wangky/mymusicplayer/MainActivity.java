package com.example.wangky.mymusicplayer;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private final int WRITE_PERMISSION_REQUEST =1;

    List<Map<String,Object>> audioList = new ArrayList<>();

    private ChangeMediaReceiver changeMediaReceiver;

    LocalBroadcastManager localBroadcastManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        localBroadcastManager = LocalBroadcastManager.getInstance(this);

        IntentFilter intentFilter = new IntentFilter();

        intentFilter.addAction("com.example.wangky.mymusicplayer.MainActivity.changeMedia");

        changeMediaReceiver = new ChangeMediaReceiver();
        //注册广播
        localBroadcastManager.registerReceiver(changeMediaReceiver,intentFilter);

        if(ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){

            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},WRITE_PERMISSION_REQUEST);

        }else{

            this.remainOperation();

        }


        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
                Intent intent = new Intent(MainActivity.this,CustomSurfaceViewActivity.class);
                startActivity(intent);

            }
        });

    }



    public  void remainOperation(){
        ListView listview = findViewById(R.id.playlist);

        audioList = this.getAudioList();

        PlaylistSimpleAdapter playlistSimpleAdapter = new PlaylistSimpleAdapter(MainActivity.this,audioList,
                    R.layout.audio_item,new String[]{"title","artist"},new int[]{R.id.audio_title,R.id.audio_author});

        listview.setAdapter(playlistSimpleAdapter);

        listview.setOnItemClickListener(new ListView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
             ListView listview = (ListView) parent;

             Map<String,Object> data = (Map<String, Object>) listview.getItemAtPosition(position);

                Intent intent = new Intent(MainActivity.this,MusicPlayActivity.class);
                intent.putExtra("position",position);
                intent.putExtra("id",(String)data.get("id"));
                intent.putExtra("uri",(String)data.get("data"));
                intent.putExtra("duration",(int) data.get("duration"));
                intent.putExtra("title",(String)data.get("title"));
                intent.putExtra("artist",(String)data.get("artist"));
                intent.putExtra("albumArt",(String)data.get("albumArt"));
                startActivity(intent);
                overridePendingTransition(R.anim.side_in_right,R.anim.animo_no);

            }
        });

    }



    public List<Map<String,Object>> getAudioList(){

        List<Map<String,Object>> list = new ArrayList<>();

        String[] projection = new String[]{
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.DISPLAY_NAME,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.ALBUM_ID
        };

        Cursor cursor =getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,projection,null,null,null);


        if(cursor != null && cursor.moveToFirst()){

            while (cursor.moveToNext()){

                Map<String,Object> map = new HashMap<>();
                map.put("id",cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media._ID)));
                map.put("title",cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE)));
                map.put("data",cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA)));
                map.put("artist",cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)));
                map.put("displayName",cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME)));
                map.put("duration",cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION)));


                int album_id =cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID));

                String albumArt =getAlbumArt(album_id);

                map.put("albumArt",albumArt);

                if((int)map.get("duration") >= 60000){
                 list.add(map);
                }
            }
        }

       return list;

    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull int[] grantResults) {
            switch (requestCode){
                case WRITE_PERMISSION_REQUEST:
                    if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){

                        this.remainOperation();

                    }else{
                        Toast.makeText(MainActivity.this,"未授予权限",Toast.LENGTH_LONG).show();
                        return;
                    }
            }
    }


    @Override
    protected void onDestroy() {
        //取消注册
        localBroadcastManager.unregisterReceiver(changeMediaReceiver);

        super.onDestroy();
    }



    class ChangeMediaReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
           String type = intent.getStringExtra("type");
           int position = intent.getIntExtra("position",0);
           if("previous".equalsIgnoreCase(type)){
               findMediaAndPlay(position >= 1 ? position -1 : 0);
           }else{
               findMediaAndPlay(position +1 >= audioList.size() ? audioList.size()-1: position +1);
           }

        }

    }


    public void findMediaAndPlay(int position){
        Map<String,Object> target;
        target = audioList.get(position);

        Intent intent = new Intent(MainActivity.this,MusicPlayActivity.class);
        intent.putExtra("position",position);
        intent.putExtra("id",(String)target.get("id"));
        intent.putExtra("uri",(String)target.get("data"));
        intent.putExtra("duration",(int) target.get("duration"));
        intent.putExtra("title",(String)target.get("title"));
        intent.putExtra("artist",(String)target.get("artist"));
        intent.putExtra("albumArt",(String)target.get("albumArt"));
        startActivity(intent);

    }



    private String getAlbumArt(int album_id) {
        String mUriAlbums = "content://media/external/audio/albums";
        String[] projection = new String[] { "album_art" };
        Cursor cur = this.getContentResolver().query(Uri.parse(mUriAlbums + "/" + Integer.toString(album_id)),projection, null, null, null);
        String album_art = null;
        if (cur.getCount() > 0 && cur.getColumnCount() > 0){
            cur.moveToNext();
            album_art = cur.getString(0);
        }
        cur.close();
        cur = null;
        return album_art;
    }


}
