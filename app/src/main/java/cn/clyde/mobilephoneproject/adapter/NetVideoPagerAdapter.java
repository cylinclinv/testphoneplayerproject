package cn.clyde.mobilephoneproject.adapter;

import android.content.Context;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import org.xutils.x;

import java.util.ArrayList;

import cn.clyde.mobilephoneproject.R;
import cn.clyde.mobilephoneproject.domain.MediaItem;
import cn.clyde.mobilephoneproject.pager.NetVideoPager;
import cn.clyde.mobilephoneproject.pager.VideoPager;
import cn.clyde.mobilephoneproject.utils.Utils;

public class NetVideoPagerAdapter extends BaseAdapter {


    private final ArrayList<MediaItem> mediaItems;
    private Context context;

    private Utils utils;

    public NetVideoPagerAdapter(Context context, ArrayList<MediaItem> mediaItems){
        this.context=context;
        this.mediaItems=mediaItems;

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
                convertView=View.inflate(context, R.layout.item_netvideo_pager,null);
                viewHoder=new ViewHoder();
                viewHoder.iv_icon=convertView.findViewById(R.id.iv_icon);
                viewHoder.tv_name=convertView.findViewById(R.id.tv_name);
                viewHoder.tv_desc=convertView.findViewById(R.id.tv_desc);

                convertView.setTag(viewHoder);
            }else {
                viewHoder= (ViewHoder) convertView.getTag();
            }
            //根据positio获得列表中对应的数据
            mediaItem=mediaItems.get(position);
            viewHoder.tv_name.setText(mediaItem.getName());
            viewHoder.tv_desc.setText(mediaItem.getDesc());
//            x.image().bind(viewHoder.iv_icon,mediaItem.getImageurl());

            Glide.with(context)
                    .load(mediaItem.getImageurl())
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .error(R.drawable.video_default)
                    .into(viewHoder.iv_icon);
            return convertView;

        }
    static class ViewHoder{
        ImageView iv_icon;
        TextView tv_name;
        TextView tv_desc;
    }
}




