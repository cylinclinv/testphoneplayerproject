package cn.clyde.mobilephoneproject.adapter;

import android.content.Context;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import java.util.ArrayList;

import cn.clyde.mobilephoneproject.R;
import cn.clyde.mobilephoneproject.domain.MediaItem;
import cn.clyde.mobilephoneproject.utils.Utils;
public class VideoPagerAdapter extends BaseAdapter {


    private final ArrayList<MediaItem> mediaItems;
    private final Context context;
    private boolean isVideo;


    private Utils utils;

    public VideoPagerAdapter(Context context, ArrayList<MediaItem> mediaItems,boolean isVideo){
        this.context=context;
        this.mediaItems=mediaItems;
        utils=new Utils();
        this.isVideo=isVideo;
    }

        @Override
        public int getCount() {
            return mediaItems.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
             ViewHoder viewHoder;
            MediaItem mediaItem;
            if(convertView ==null)
            {
                convertView=View.inflate(context, R.layout.item_video_pager,null);
                viewHoder=new ViewHoder();
                viewHoder.iv_icon=convertView.findViewById(R.id.iv_icon);
                viewHoder.tv_name=convertView.findViewById(R.id.tv_name);
                viewHoder.tv_size=convertView.findViewById(R.id.tv_size);
                viewHoder.tv_time=convertView.findViewById(R.id.tv_time);

                convertView.setTag(viewHoder);
            }else {
                viewHoder= (ViewHoder) convertView.getTag();
            }
            //根据positio获得列表中对应的数据
            mediaItem=mediaItems.get(position);
            viewHoder.tv_name.setText(mediaItem.getName());
            viewHoder.tv_size.setText(Formatter.formatFileSize(context,mediaItem.getSize()));
            viewHoder.tv_time.setText(utils.stringForTime((int)mediaItem.getDuration()));
            if(isVideo){
                viewHoder.iv_icon.setImageResource(R.drawable.music_default_bg);
            }
            return convertView;
        }

    static class ViewHoder{
        ImageView iv_icon;
        TextView tv_name;
        TextView tv_time;
        TextView tv_size;
    }


}




