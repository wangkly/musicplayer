package com.example.wangky.mymusicplayer;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){

            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},WRITE_PERMISSION_REQUEST);

        }else{

            this.remainOperation();

        }

    }



    public  void remainOperation(){
        ListView listview = findViewById(R.id.playlist);

        List<Map<String,Object>> data = null;

        data = this.getAudioList();


        PlaylistSimpleAdapter playlistSimpleAdapter = new PlaylistSimpleAdapter(MainActivity.this,data,
                    R.layout.audio_item,new String[]{"title","artist"},new int[]{R.id.audio_title,R.id.audio_author});


        listview.setAdapter(playlistSimpleAdapter);

        listview.setOnItemClickListener(new ListView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
             ListView listview = (ListView) parent;

             Map<String,Object> data = (Map<String, Object>) listview.getItemAtPosition(position);

                Intent intent = new Intent(MainActivity.this,MusicPlayActivity.class);

                intent.putExtra("uri",(String)data.get("data"));

                startActivity(intent);

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
                MediaStore.Audio.Media.ARTIST
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
                map.put("duration",cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION)));

                list.add(map);
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
}
