package cn.clyde.mobilephoneproject.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.File;

import cn.clyde.mobilephoneproject.R;
import cn.clyde.mobilephoneproject.service.IMusicPlayerService;
import cn.clyde.mobilephoneproject.service.MusicPlayerService;
import cn.clyde.mobilephoneproject.utils.LyricUtils;
import cn.clyde.mobilephoneproject.utils.Utils;
import cn.clyde.mobilephoneproject.view.ShowLyricView;

public class SystemAudioPlayer extends Activity implements View.OnClickListener {
    public static final int PROGRESS = 1;
    private int position;
    private IMusicPlayerService service;
    private MyReceiver myReceiver;
    private Utils utils;
    private boolean notification;
    private static final int SHOW_LYRIC=2;


    private ImageView iv_icon;
    private TextView tv_artist;
    private TextView tv_name;
    private TextView tv_time;
    private SeekBar seekbar_audio;
    private Button btn_playmode;
    private Button btn_audio_pre;
    private Button btn_audio_paly;
    private Button btn_audio_next;
    private Button btn_lyric;
    private ShowLyricView showLyricView;
    public  LyricUtils lyricUtils=new LyricUtils();

    /**
     * Find the Views in the layout<br />
     * <br />
     * Auto-created on 2020-04-22 14:41:45 by Android Layout Finder
     * (http://www.buzzingandroid.com/tools/android-layout-finder)
     */
    private void findViews() {
        setContentView(R.layout.activity_audioplayer);


        iv_icon = (ImageView)findViewById( R.id.iv_icon );
        tv_artist = (TextView)findViewById( R.id.tv_artist );
        tv_name = (TextView)findViewById( R.id.tv_name );
        tv_time = (TextView)findViewById( R.id.tv_time );
        seekbar_audio = (SeekBar)findViewById( R.id.seekbar_audio );
        btn_playmode = (Button)findViewById( R.id.btn_playmode );
        btn_audio_pre = (Button)findViewById( R.id.btn_audio_pre );
        btn_audio_paly = (Button)findViewById( R.id.btn_audio_paly );
        btn_audio_next = (Button)findViewById( R.id.btn_audio_next );
        btn_lyric = (Button)findViewById( R.id.btn_lyric );
        showLyricView=(ShowLyricView) findViewById(R.id.showLyricView);

        btn_playmode.setOnClickListener( this );
        btn_audio_pre.setOnClickListener( this );
        btn_audio_paly.setOnClickListener( this );
        btn_audio_next.setOnClickListener( this );
        btn_lyric.setOnClickListener( this );
        seekbar_audio.setOnSeekBarChangeListener(new MyOnSeekBarChangeListener());
    }

    /**
     * Handle button click events<br />
     * <br />
     * Auto-created on 2020-04-22 14:41:45 by Android Layout Finder
     * (http://www.buzzingandroid.com/tools/android-layout-finder)
     */
    @Override
    public void onClick(View v) {
        if (v == btn_playmode) {

            setPlaymode();
            // Handle clicks for btn_playmode

        }else if ( v == btn_audio_pre ) {
            if(service!=null){
                try {
                    service.pre();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        } else if ( v == btn_audio_paly ) {

            if(service != null){
            try {
                    if(service.isPlaying()){
                        service.pause();
                        btn_audio_paly.setBackgroundResource(R.drawable.btn_audio_pause_selector);
                        iv_icon.setBackgroundResource(R.drawable.now_playing_matrix_01);

                    }else{
                        service.start();
                        btn_audio_paly.setBackgroundResource(R.drawable.btn_audio_play_selector);
                        iv_icon.setBackgroundResource(R.drawable.animation_list);
                        AnimationDrawable rocketAnimation= (AnimationDrawable) iv_icon.getBackground();
                        rocketAnimation.start();

                    }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            }
        } else if ( v == btn_audio_next ) {
            if(service!=null){
                try {
                    service.next();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        } else if ( v == btn_lyric ) {

        }
    }

    private void setPlaymode() {
        try {
            int playmode=service.getPlayMode();
            if(playmode==MusicPlayerService.REPEAT_NORMAL){
                playmode=MusicPlayerService.REPEAT_SINGLE;
            }else if(playmode==MusicPlayerService.REPEAT_SINGLE){
                playmode=MusicPlayerService.REPEAT_ALL;
            }else if(playmode==MusicPlayerService.REPEAT_ALL){
                playmode=MusicPlayerService.REPEAT_NORMAL;
            }else{
                playmode=MusicPlayerService.REPEAT_NORMAL;
            }
            service.setPlayMode(playmode);

            showPlaymode();
        } catch (RemoteException e) {
            e.printStackTrace();
        }

    }

    private void showPlaymode() {
        try {
            int playmode = service.getPlayMode();

            if(playmode==MusicPlayerService.REPEAT_NORMAL){
                btn_playmode.setBackgroundResource(R.drawable.btn_audio_playmode_normal);
                Toast.makeText(SystemAudioPlayer.this,"顺序播放",Toast.LENGTH_SHORT).show();
            }else if(playmode==MusicPlayerService.REPEAT_SINGLE){

                btn_playmode.setBackgroundResource(R.drawable.btn_audio_playmode_single);
                Toast.makeText(SystemAudioPlayer.this,"单曲播放",Toast.LENGTH_SHORT).show();
            }else if(playmode==MusicPlayerService.REPEAT_ALL){
                btn_playmode.setBackgroundResource(R.drawable.btn_audio_playmode_all);
                Toast.makeText(SystemAudioPlayer.this,"全部循环",Toast.LENGTH_SHORT).show();
            }else{
                btn_playmode.setBackgroundResource(R.drawable.btn_audio_playmode_normal);
                Toast.makeText(SystemAudioPlayer.this,"顺序播放",Toast.LENGTH_SHORT).show();
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }



    private void checkPlaymode() {
        int playmode= 0;
        try {
            playmode = service.getPlayMode();

            if(playmode==MusicPlayerService.REPEAT_NORMAL){
                btn_playmode.setBackgroundResource(R.drawable.btn_audio_playmode_normal);
            }else if(playmode==MusicPlayerService.REPEAT_SINGLE){

                btn_playmode.setBackgroundResource(R.drawable.btn_audio_playmode_single);
            }else if(playmode==MusicPlayerService.REPEAT_ALL){
                btn_playmode.setBackgroundResource(R.drawable.btn_audio_playmode_all);
            }else{
                btn_playmode.setBackgroundResource(R.drawable.btn_audio_playmode_normal);
            }


            if(service.isPlaying()){
                btn_audio_paly.setBackgroundResource(R.drawable.btn_audio_play_selector);

            }else{
                btn_audio_paly.setBackgroundResource(R.drawable.btn_audio_pause_selector);


            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private ServiceConnection con=new ServiceConnection() {
        //当链接成功的时候回调这个方法
        @Override
        public void onServiceConnected(ComponentName name, IBinder iBinder) {
            service= IMusicPlayerService.Stub.asInterface(iBinder);
            try {
                if (service!=null) {
                    if (!notification) {
                        service.openAudio(position);
                    }else{
                        showViewData();
                    }
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        //当链接断开的时候回调这个方法
        @Override
        public void onServiceDisconnected(ComponentName name) {
            try {
                if (service!=null){
                        service.stop();
                    service=null;
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    };

    private Handler handler=new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);

            switch (msg.what){
                case SHOW_LYRIC:
                    try {
                        int currentPosition=service.getCurrentPosition();
                        showLyricView.setshowNextLyric(currentPosition);


                        handler.removeMessages(SHOW_LYRIC);
                        handler.sendEmptyMessage(SHOW_LYRIC);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }

                    break;
                case PROGRESS:
                    int currentPosition= 0;
                    try {
                        currentPosition = service.getCurrentPosition();
                        seekbar_audio.setProgress(currentPosition);
                        tv_time.setText(utils.stringForTime(currentPosition)+"/"+utils.stringForTime(service.getDuration()));
                        handler.removeMessages(PROGRESS);
                        handler.sendEmptyMessageDelayed(PROGRESS,1000);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }



                    break;
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initData();
        findViews();
        getData();
        bindAndStartService();

    }

    private void initData() {
        utils=new Utils();
        myReceiver=new MyReceiver();
        IntentFilter intentFilter=new IntentFilter();
        intentFilter.addAction(MusicPlayerService.OPENAUDIO);
        registerReceiver(myReceiver,intentFilter);

    }

    class MyReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            showLyric();
            showViewData();
            checkPlaymode();
        }
    }

    private void showLyric() {
        try {
            String path=service.getAudioPath();

            path=path.substring(0,path.lastIndexOf("."));
            File file=new File(path+".lrc");
            if(!file.exists()){
                file=new File(path+".txt");
            }
            lyricUtils.readLyricFile(file);
            showLyricView.setLyrics(lyricUtils.getLyrics());
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        if(lyricUtils.isExistsLyric()){
            handler.sendEmptyMessage(SHOW_LYRIC);

        }
    }

    private void showViewData() {
        try {
            tv_artist.setText(service.getArtist());
            tv_name.setText(service.getName());


            seekbar_audio.setMax(service.getDuration());
            handler.sendEmptyMessageDelayed(PROGRESS,1000);

        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void bindAndStartService(){
        Intent intent=new Intent(this, MusicPlayerService.class);
        intent.setAction("cn.clyde.mobliephone_openaudio");
        bindService(intent,con, Context.BIND_AUTO_CREATE);
        startService(intent);//多次调用不会实例化多个服务
    }
    private void getData() {
        notification=getIntent().getBooleanExtra("Notification",false);
        if(!notification) {
            position = getIntent().getIntExtra("position", 0);
            iv_icon.setBackgroundResource(R.drawable.animation_list);
            AnimationDrawable rocketAnimation = (AnimationDrawable) iv_icon.getBackground();
            rocketAnimation.start();
        }
    }

    @Override
    protected void onDestroy() {
        handler.removeCallbacksAndMessages(null);
        if(myReceiver!=null){
            unregisterReceiver(myReceiver);
            myReceiver=null;
        }
        if(con!=null) {
            unbindService(con);
            con=null;
        }
        super.onDestroy();
    }

    private class MyOnSeekBarChangeListener implements SeekBar.OnSeekBarChangeListener {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if(fromUser){
                try {
                    service.seekTo(progress);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    }
}
