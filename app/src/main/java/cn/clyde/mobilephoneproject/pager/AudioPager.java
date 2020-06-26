package cn.clyde.mobilephoneproject.pager;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import java.util.ArrayList;

import cn.clyde.mobilephoneproject.R;
import cn.clyde.mobilephoneproject.activity.SystemAudioPlayer;
import cn.clyde.mobilephoneproject.activity.SystemVideoPlayer;
import cn.clyde.mobilephoneproject.adapter.VideoPagerAdapter;
import cn.clyde.mobilephoneproject.base.BasePager;
import cn.clyde.mobilephoneproject.domain.MediaItem;
import cn.clyde.mobilephoneproject.utils.LogUtil;

public class AudioPager extends BasePager {
    private ListView listview;
    private TextView tv_nomedia;
    private ProgressBar pb_loading;


    private ArrayList<MediaItem> mediaItems;
    private VideoPagerAdapter videoPagerAdapter;




    public AudioPager(Context context) {
        super(context);
    }

    private Handler handler=new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);

            if (mediaItems !=null&&mediaItems.size()>0) {
                //有数据,设置适配器
                videoPagerAdapter = new VideoPagerAdapter(context, mediaItems,true);
                listview.setAdapter(videoPagerAdapter);
                //把文本隐藏
                tv_nomedia.setVisibility(View.GONE);
            }else{

                //没有数据,
                tv_nomedia.setVisibility(View.VISIBLE);
            }


            //隐藏progressbar
            pb_loading.setVisibility(View.GONE);
        }
    };

    @Override
    public View initView() {
//        tv=new TextView(context);
//        tv.setText("本地视频");
//        LogUtil.e("本地视频界面被初始化了");
//        tv.setTextSize(35);
//        tv.setTextColor(Color.RED);
//        tv.setGravity(Gravity.CENTER);
        View view=View.inflate(context, R.layout.video_pager,null);
        listview=(ListView)view.findViewById(R.id.listview);
        tv_nomedia=(TextView) view.findViewById(R.id.tv_nomedia);
        pb_loading=(ProgressBar) view.findViewById(R.id.pb_loading);

        //设置ListView的Item的点击事件
        listview.setOnItemClickListener(new AudioPager.MyOnItemClickListener());

        return view;
    }


    class MyOnItemClickListener implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            MediaItem mediaItem = mediaItems.get(position);

            Intent intent = new Intent(context, SystemAudioPlayer.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("videolist",mediaItems);
            intent.putExtras(bundle);
            intent.putExtra("position",position);
            context.startActivity(intent);


        }
    }
    @Override
    public void initData() {
        super.initData();
        //加载数据,本地视频
        getDataFromLocal();
        LogUtil.e("本地视频界面被初始化了");
    }
    //从本地获得sd卡数据
    //两种方案:1遍历sd卡,耗时长;2去contentProvider获取
    private void getDataFromLocal() {
        mediaItems=new ArrayList<MediaItem>();
        new Thread(){
            @Override
            public void run() {
                super.run();

                isGrantExternalRW((Activity) context);
                ContentResolver resolver=context.getContentResolver();
                Uri uri= MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                String[] objs={
                        MediaStore.Audio.Media.DISPLAY_NAME,
                        MediaStore.Audio.Media.DURATION,
                        MediaStore.Audio.Media.SIZE,
                        MediaStore.Audio.Media.DATA,
                        MediaStore.Audio.Media.ARTIST
                };
                Cursor cursor=resolver.query(uri,objs,null,null,null);
                if(cursor!=null){
                    while(cursor.moveToNext()){
                        MediaItem mediaItem=new MediaItem();

                        mediaItems.add(mediaItem);

                        String name=cursor.getString(0);
                        mediaItem.setName(name);
                        long duration=cursor.getLong(1);
                        mediaItem.setDuration(duration);
                        long size=cursor.getLong(2);
                        mediaItem.setSize(size);
                        String data=cursor.getString(3);
                        mediaItem.setData(data);
                        String artist=cursor.getString(4);
                        mediaItem.setArtist(artist);


                    }
                }
                cursor.close();

                //发消息
                handler.sendEmptyMessage(10);
            }
        }.start();
    }
//    public static class ViewHoder{
//        public ImageView iv_icon;
//        public TextView tv_name;
//        public TextView tv_time;
//        public TextView tv_size;
//    }

    /**
     * 解决安卓6.0以上版本不能读取外部存储权限的问题
     * @param activity
     * @return
     */
    public static boolean isGrantExternalRW(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && activity.checkSelfPermission(
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            activity.requestPermissions(new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            }, 1);

            return false;
        }

        return true;
    }

}
