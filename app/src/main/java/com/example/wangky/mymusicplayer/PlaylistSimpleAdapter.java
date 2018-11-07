package com.example.wangky.mymusicplayer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.List;
import java.util.Map;

public class PlaylistSimpleAdapter extends SimpleAdapter {

    Context context;

    LayoutInflater layoutInflater;

    List<Map<String, Object>> playlist;

    int resource ;


    private class ViewHolder{
        TextView title;
        TextView author;
    }



    public PlaylistSimpleAdapter(Context context,
                                 List<Map<String, Object>> data,
                                 int resource, String[] from, int[] to) {
        super(context, data, resource, from, to);
        this.context =context;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.playlist = data;
        this.resource =resource;

    }



    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
       ViewHolder holder;
        Map<String, Object> bean = playlist.get(position);

        if(convertView ==null){
            convertView= layoutInflater.inflate(R.layout.audio_item,parent,false);
            holder = new ViewHolder();
            holder.title = convertView.findViewById(R.id.audio_title);
            holder.author =convertView.findViewById(R.id.audio_author);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        if(null !=bean){
            holder.title.setText((String)bean.get("title"));
            holder.author.setText((String)bean.get("artist"));
        }

        return convertView;
    }

    @Override
    public int getCount() {
        return this.playlist.size();
    }

    @Override
    public Object getItem(int position) {
        return this.playlist.get(position);
    }


}
