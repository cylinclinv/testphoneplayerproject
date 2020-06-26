package cn.clyde.mobilephoneproject.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;


import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import cn.clyde.mobilephoneproject.R;
import cn.clyde.mobilephoneproject.domain.MediaItem;
import cn.clyde.mobilephoneproject.utils.LogUtil;
import cn.clyde.mobilephoneproject.utils.Utils;
import cn.clyde.mobilephoneproject.view.VideoView;

public class SystemVideoPlayer extends Activity implements View.OnClickListener {
    private static final int HIDEMEDIACONTROLLER = 2;
    private static final int FULL_SCREEN = 3;
    private Utils utils;
    private static final int DEFAULT_SCREEN = 4;
    private static final int PROGRESSCU=1;
    private static final int SHOW_SPEED=3;

    private VideoView videoView;
    private  Uri uri;
    private MyReceiver myReceiver;
    private ArrayList<MediaItem> mediaItems;
    private int position;

    private boolean isUseSystem=false;
    private int precurrentPosition;


    private GestureDetector detector;

    private LinearLayout llTop;
    private LinearLayout llContent;
    private TextView tvName;
    private ImageView ivBattery;
    private TextView tvSystemTime;
    private Button btnVoice;
    private SeekBar seekbarVoice;
    private Button btnSwitchPlayer;
    private LinearLayout llBottom;
    private TextView tvCurrentTime;
    private SeekBar seekbarVideo;
    private TextView tvDuration;
    private Button btnExit;
    private Button btnVideoPre;
    private Button btnVideoPaly;
    private Button btnVideoNext;
    private Button btnSwitchScreen;
    private RelativeLayout media_controller;
    private boolean isShowMediaController=false;
    private boolean isFullScreen=true;
    private int screenHeight=0;
    private int screenWidth=0;
    private int videoWidth;
    private int videoHeight;

    private AudioManager am;
    private int currentVoice;
    private int maxVoice;
    private boolean isMute=false;
    private boolean isNetUri;

    private TextView tv_netspeed;
    private LinearLayout ll_buffer;
    private LinearLayout ll_loading;
    private  TextView tv_loading_netspeed;

    private  Vibrator vibrator;
    private int duration;
    private float downX;
    private boolean isRight;
    private float downY;
    private boolean isLeft;
    private boolean isAll;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initData();


        findViews();
        Log.e("videoUri","视频地址为");
        uri = this.getIntent().getData();

        getData();
        setData();

        //准备好的监听
        videoView.setOnPreparedListener(new MyOnPreparedListener());
        //播放出错的监听
        videoView.setOnErrorListener(new MyOnErrorListener());
        //播放完成的监听
        videoView.setOnCompletionListener(new MyOnCompleteListener());
        seekbarVideo.setOnSeekBarChangeListener(new VideoSeekBarChangeListener());
        seekbarVoice.setOnSeekBarChangeListener(new VoiceSeekBarChangeListener());

        if(isUseSystem){
//            if(Build.VERSION.SDK_INT==Build.VERSION_CODES.JELLY_BEAN_MR1) {
                videoView.setOnInfoListener(new MyInfoListener());
//            }
        }


//        videoView.setMediaController(new MediaController(this));


    }

    /**
     * Find the Views in the layout<br />
     * <br />
     * Auto-created on 2020-04-13 02:45:48 by Android Layout Finder
     * (http://www.buzzingandroid.com/tools/android-layout-finder)
     */
    private void findViews() {
        setContentView(R.layout.activity_system_video_player);

        llTop = (LinearLayout)findViewById( R.id.ll_top );
        llContent = (LinearLayout)findViewById( R.id.ll_content );
        tvName = (TextView)findViewById( R.id.tv_name );
        ivBattery = (ImageView)findViewById( R.id.iv_battery );
        tvSystemTime = (TextView)findViewById( R.id.tv_system_time );
        btnVoice = (Button)findViewById( R.id.btn_voice );
        seekbarVoice = (SeekBar)findViewById( R.id.seekbar_voice );
        btnSwitchPlayer = (Button)findViewById( R.id.btn_switch_player );
        llBottom = (LinearLayout)findViewById( R.id.ll_bottom );
        tvCurrentTime = (TextView)findViewById( R.id.tv_current_time );
        seekbarVideo = (SeekBar)findViewById( R.id.seekbar_video );
        tvDuration = (TextView)findViewById( R.id.tv_duration );
        btnExit = (Button)findViewById( R.id.btn_exit );
        btnVideoPre = (Button)findViewById( R.id.btn_video_pre );
        btnVideoPaly = (Button)findViewById( R.id.btn_video_paly );
        btnVideoNext = (Button)findViewById( R.id.btn_video_next );
        btnSwitchScreen = (Button)findViewById( R.id.btn_switch_screen );
        media_controller=(RelativeLayout) findViewById(R.id.media_controller);

        btnVoice.setOnClickListener( this );
        btnSwitchPlayer.setOnClickListener( this );
        btnExit.setOnClickListener( this );
        btnVideoPre.setOnClickListener( this );
        btnVideoPaly.setOnClickListener( this );
        btnVideoNext.setOnClickListener( this );
        btnSwitchScreen.setOnClickListener( this );
        videoView = (VideoView) findViewById(R.id.videoview);


        tv_netspeed= (TextView) findViewById(R.id.tv_netspeed);
        ll_buffer= (LinearLayout) findViewById(R.id.ll_buffer);
        ll_loading= (LinearLayout) findViewById(R.id.ll_loading);
        tv_loading_netspeed= (TextView) findViewById(R.id.tv_loading_netspeed);

        seekbarVoice.setMax(maxVoice);
        seekbarVoice.setProgress(currentVoice);

        handler.sendEmptyMessage(SHOW_SPEED);
    }

    /**
     * Handle button click events<br />
     * <br />
     * Auto-created on 2020-04-13 02:45:48 by Android Layout Finder
     * (http://www.buzzingandroid.com/tools/android-layout-finder)
     */
    @Override
    public void onClick(View v) {
        handler.removeMessages(HIDEMEDIACONTROLLER);

        if ( v == btnVoice ) {
            isMute=!isMute;
            updataVoice(currentVoice,isMute);


            // Handle clicks for btnVoice
        } else if ( v == btnSwitchPlayer ) {
            startVitamioPlayer();

            // Handle clicks for btnSwitchPlayer
        } else if ( v == btnExit ) {

            // Handle clicks for btnExit
            finish();
        } else if ( v == btnVideoPre ) {

            // Handle clicks for btnVideoPre
            playPreVideo();

        } else if ( v == btnVideoPaly ) {

            // Handle clicks for btnVideoPaly
            startAndPause();
        } else if ( v == btnVideoNext ) {

            // Handle clicks for btnVideoNext
            playNextVideo();
        } else if ( v == btnSwitchScreen ) {
            // Handle clicks for btnSwitchScreen
            if(!isFullScreen){
                setVideoType(DEFAULT_SCREEN);
            }else{
                setVideoType(FULL_SCREEN);
            }
        }
        handler.sendEmptyMessageDelayed(HIDEMEDIACONTROLLER,4000);

    }

    private void setVideoType(int defaultScreen) {
        switch (defaultScreen){
            case FULL_SCREEN:
                isFullScreen=false;
                videoView.setVideoSize(screenWidth,screenHeight);
                btnSwitchScreen.setBackgroundResource(R.drawable.btn_switch_screen_selector);
                break;
            case DEFAULT_SCREEN:
                int mVideoWidth=videoWidth;
                int mVideoHeight=videoHeight;
                int width=screenWidth;
                int height=screenHeight;
                // for compatibility, we adjust size based on aspect ratio
                if ( mVideoWidth * height  < width * mVideoHeight ) {
                    //Log.i("@@@", "image too wide, correcting");
                    width = height * mVideoWidth / mVideoHeight;
                } else if ( mVideoWidth * height  > width * mVideoHeight ) {
                    //Log.i("@@@", "image too tall, correcting");
                    height = width * mVideoHeight / mVideoWidth;
                }
                isFullScreen=true;
                videoView.setVideoSize(width,height);

                btnSwitchScreen.setBackgroundResource(R.drawable.btn_switch_default_selector);

                break;

        }
    }

    private void startAndPause() {
        if(videoView.isPlaying()){
            videoView.pause();
            btnVideoPaly.setBackgroundResource(R.drawable.btn_play_selector);
        }else{
            videoView.start();
            btnVideoPaly.setBackgroundResource(R.drawable.btn_stop_selector);

        }
    }

    private void playPreVideo() {
        if (mediaItems!=null&&mediaItems.size()>0){
            position--;
            if(position>=0){
                ll_loading.setVisibility(View.GONE);
                MediaItem mediaItem=mediaItems.get(position);
                tvName.setText(mediaItem.getName());
                isNetUri=utils.isNetUri(mediaItem.getData());

                videoView.setVideoPath(mediaItem.getData());
                setButtonState();
            }
        }else if(uri!=null){
            setButtonState();
        }
    }

    private void playNextVideo() {
        if (mediaItems!=null&&mediaItems.size()>0){
            position++;
            if(position<mediaItems.size()){
                ll_loading.setVisibility(View.GONE);
                MediaItem mediaItem=mediaItems.get(position);
                tvName.setText(mediaItem.getName());
                isNetUri=utils.isNetUri(mediaItem.getData());
                videoView.setVideoPath(mediaItem.getData());
                setButtonState();
            }
        }else if(uri!=null){

        }
    }

    private void setButtonState() {
        if(mediaItems!=null&&mediaItems.size()>0){
            if(mediaItems.size()==1){

                setEnable(false);
            }else if (mediaItems.size()==2){

                if(position==0){
                    btnVideoPre.setBackgroundResource(R.drawable.btn_pre_gray);
                    btnVideoPre.setEnabled(false);

                    btnVideoNext.setBackgroundResource(R.drawable.btn_next_selector);
                    btnVideoNext.setEnabled(true);
                }else if(position==mediaItems.size()-1){
                    btnVideoNext.setBackgroundResource(R.drawable.btn_next_gray);
                    btnVideoNext.setEnabled(false);
                                        btnVideoPre.setBackgroundResource(R.drawable.btn_pre_selector);
                    btnVideoPre.setEnabled(true);
                }

            }else{
                if(position==0){
                    btnVideoPre.setBackgroundResource(R.drawable.btn_pre_gray);
                    btnVideoPre.setEnabled(false);
                }else if(position==mediaItems.size()-1){
                    btnVideoNext.setBackgroundResource(R.drawable.btn_next_gray);
                    btnVideoNext.setEnabled(false);
                }else{
                    setEnable(true);
                }
            }

        }else if (uri!=null){
            setEnable(false);
        }
    }

    private void setEnable(boolean isEnable) {
        if(isEnable) {

            btnVideoNext.setBackgroundResource(R.drawable.btn_next_selector);
            btnVideoNext.setEnabled(true);

            btnVideoPre.setBackgroundResource(R.drawable.btn_pre_selector);
            btnVideoPre.setEnabled(true);

        }else{

            btnVideoPre.setBackgroundResource(R.drawable.btn_pre_gray);
            btnVideoPre.setEnabled(false);
            btnVideoNext.setBackgroundResource(R.drawable.btn_next_gray);
            btnVideoNext.setEnabled(false);
        }
    }

    Handler handler=new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch(msg.what){
                case SHOW_SPEED:
                    String netSpeed=utils.getNetSpeed(SystemVideoPlayer.this);

                    tv_loading_netspeed.setText("玩命加载中..."+netSpeed);
                    tv_netspeed.setText("缓存中..."+netSpeed);

                    handler.removeMessages(SHOW_SPEED);
                    handler.sendEmptyMessageDelayed(SHOW_SPEED,2000);

                    //2秒调用一次
                    break;
                case HIDEMEDIACONTROLLER://隐藏控制面板
                    hideMediaController();
                    break;
                case PROGRESSCU:
                    int currentPosition=videoView.getCurrentPosition();
                    seekbarVideo.setProgress(currentPosition);
                    tvCurrentTime.setText(utils.stringForTime(currentPosition));
                    //设置系统时间
                    tvSystemTime.setText(getSysteTime());
                    //设置进度条的第二缓冲区
                    if(isNetUri){
                        int buffer=videoView.getBufferPercentage();//0-100
                        int totalBuffer=buffer*seekbarVideo.getMax();
                        int secondaryProgress=totalBuffer/100;
                        seekbarVideo.setSecondaryProgress(secondaryProgress);
                    }else{
                        seekbarVideo.setSecondaryProgress(0);
                    }
                    if(!isUseSystem && videoView.isPlaying()){
                        int buffer=currentPosition-precurrentPosition;
                        if(buffer<500){
                            ll_buffer.setVisibility(View.VISIBLE);
                        }else{
                            ll_buffer.setVisibility(View.GONE);
                        }
                    }

                    precurrentPosition=currentPosition;
                    removeMessages(PROGRESSCU);
                    sendEmptyMessageDelayed(PROGRESSCU,1000);
                    break;
            }
        }

    };

    //得到系统的时间
    private String getSysteTime() {
        SimpleDateFormat formate=new SimpleDateFormat("HH:mm:ss");
        return formate.format(new Date());
    }



    private void setData() {
        if (mediaItems !=null && mediaItems.size() >0) {
           MediaItem mediaItem=mediaItems.get(position);
           tvName.setText(mediaItem.getName());
           isNetUri=utils.isNetUri(mediaItem.getData());
           videoView.setVideoPath(mediaItem.getData());
        }else if(uri!=null){

            videoView.setVideoURI(uri);
            tvName.setText(uri.toString());
            isNetUri=utils.isNetUri(uri.toString());
            Toast.makeText(SystemVideoPlayer.this,"uri链接为:"+uri,Toast.LENGTH_LONG).show();
            Log.i("videoUri","uri的内容00000000000000000000000000000000000000000为:"+uri);

        }else{
            Toast.makeText(SystemVideoPlayer.this,"没有数据"+mediaItems.get(0).getData(), Toast.LENGTH_SHORT).show();
        }
        setButtonState();
    }



    public void getData() {
        //获取播放地址
//        uri = getIntent().getData();
//        Log.e("videoUri","视频地址555555555555555555为"+uri.toString());
        //传入的视频列表
        mediaItems= (ArrayList<MediaItem>) getIntent().getSerializableExtra("videolist");
        position = getIntent().getIntExtra("position",0);

    }

    private void initData() {
        utils=new Utils();
        myReceiver=new MyReceiver();
        IntentFilter intentFiler=new IntentFilter();
        //当电量变化的时候,发送广播
        intentFiler.addAction(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(myReceiver,intentFiler);
        //实例化手势识别器,重写长按,双击,单击
        detector=new GestureDetector(this,new GestureDetector.SimpleOnGestureListener(){
            @Override
            public void onLongPress(MotionEvent e) {
                super.onLongPress(e);
                Toast.makeText(SystemVideoPlayer.this,"长按",Toast.LENGTH_SHORT).show();
                if (isFullScreen){
                    setVideoType(DEFAULT_SCREEN);
                    isFullScreen=false;
                }else{
                    setVideoType(FULL_SCREEN);
                    isFullScreen=true;
                }
            }

            @Override
            public boolean onDoubleTap(MotionEvent e) {
                Toast.makeText(SystemVideoPlayer.this,"双击",Toast.LENGTH_SHORT).show();
                startAndPause();
                return super.onDoubleTap(e);

            }

            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                Toast.makeText(SystemVideoPlayer.this,"单击",Toast.LENGTH_SHORT).show();
                if(isShowMediaController) {
                    shouMediaController();
                    handler.sendEmptyMessageDelayed(HIDEMEDIACONTROLLER,4000);
                    isShowMediaController=false;
                }else{
                    hideMediaController();
                    handler.removeMessages(HIDEMEDIACONTROLLER);
                    isShowMediaController=true;
              }

                return super.onSingleTapConfirmed(e);
            }

        });
        //得到屏幕宽高的方式
        DisplayMetrics dm=new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        screenWidth=dm.widthPixels;
        screenHeight=dm.heightPixels;
        am= (AudioManager) getSystemService(AUDIO_SERVICE);
        currentVoice=am.getStreamVolume(AudioManager.STREAM_MUSIC);
        maxVoice=am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);


    }

    private void shouMediaController() {
            media_controller.setVisibility(View.VISIBLE);
            isShowMediaController=false;
    }
    private void hideMediaController() {
            media_controller.setVisibility(View.GONE);
            isShowMediaController=true;
    }


    class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            int level=intent.getIntExtra("level",0);//0-100

            setBattery(level);
        }
    }

    private void setBattery(int level) {
        if (level<=0){
            ivBattery.setImageResource(R.drawable.ic_battery_0);
        }else if(level<=10){
            ivBattery.setImageResource(R.drawable.ic_battery_10);
        }else if(level<=20){
            ivBattery.setImageResource(R.drawable.ic_battery_20);
        }else if(level<=40){
            ivBattery.setImageResource(R.drawable.ic_battery_40);
        }else if(level<=60){
            ivBattery.setImageResource(R.drawable.ic_battery_60);
        }else if(level<=80){
            ivBattery.setImageResource(R.drawable.ic_battery_80);
        }else{
            ivBattery.setImageResource(R.drawable.ic_battery_100);
        }


    }

    class VideoSeekBarChangeListener implements SeekBar.OnSeekBarChangeListener{

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if(fromUser) {
                videoView.seekTo(progress);

            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
                handler.removeMessages(HIDEMEDIACONTROLLER);
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
                handler.sendEmptyMessageDelayed(HIDEMEDIACONTROLLER,4000);
        }
    }



    class MyOnPreparedListener implements MediaPlayer.OnPreparedListener{
        //当底层解码准本好的时候
        @Override
        public void onPrepared(MediaPlayer mp) {
            videoWidth=mp.getVideoWidth();
            videoHeight=mp.getVideoHeight();
            videoView.start();
            videoView.requestFocus();
            duration=videoView.getDuration();
            seekbarVideo.setMax(duration);
            handler.sendEmptyMessage(PROGRESSCU);
            tvDuration.setText(utils.stringForTime(duration));
            handler.sendEmptyMessageDelayed(HIDEMEDIACONTROLLER,4000);


//            videoView.setVideoSize(mp.getVideoWidth(),mp.getVideoHeight());
            setVideoType(DEFAULT_SCREEN);

            ll_loading.setVisibility(View.GONE);
//            mp.setOnSeekCompleteListener(new MediaPlayer.OnSeekCompleteListener() {
//                @Override
//                public void onSeekComplete(MediaPlayer mp) {
//                    Toast.makeText(SystemVideoPlayer.this,"拖动完成",Toast.LENGTH_SHORT).show();
//                }
//            });


        }
    }

    class MyOnErrorListener implements MediaPlayer.OnErrorListener{
        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {
//            Toast.makeText(SystemVideoPlayer.this,"播放出错",Toast.LENGTH_SHORT).show();

            startVitamioPlayer();
            return true;
        }
    }

    private void startVitamioPlayer() {
//
//
//        if(videoView!=null) {
//            videoView.stopPlayback();
//        }
//            Intent intent = new Intent(this,VitamioSystemVideoPlayer.class);
//
//            if (mediaItems != null && mediaItems.size() > 0) {
//                Bundle bundle = new Bundle();
//                bundle.putSerializable("videolist", mediaItems);
//                intent.putExtra("position", position);
//            } else if (uri != null) {
//                intent.setData(uri);
//            }
//            startActivity(intent);
        Toast.makeText(this,"切换播放器",Toast.LENGTH_SHORT).show();


    }

    class MyOnCompleteListener implements MediaPlayer.OnCompletionListener{
        @Override
        public void onCompletion(MediaPlayer mp) {
//            Toast.makeText(SystemVideoPlayer.this,"播放完成"+uri,Toast.LENGTH_SHORT).show();
            playNextVideo();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        LogUtil.e("onstart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        LogUtil.e("onResume");

    }

    @Override
    protected void onPause() {
        super.onPause();
        LogUtil.e("onPause");

    }

    @Override
    protected void onStop() {
        super.onStop();
        LogUtil.e("onStop");

    }

    private float startY;
    private float startX;
    private float touchRang;
    private float touchRang1;
    private int mVol;

    private int x;
    private int y;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
       //把事件传递给手势识别器
        detector.onTouchEvent(event);
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:



                //设置快进快退



                //设置音量和亮度
                handler.removeMessages(HIDEMEDIACONTROLLER);
                startY=event.getY();
                startX=event.getX();
                mVol=am.getStreamVolume(AudioManager.STREAM_MUSIC);
                touchRang=Math.min(screenWidth,screenHeight);
                touchRang1=Math.min(screenWidth,screenHeight);


                break;

            case MotionEvent.ACTION_MOVE:
                float endY=event.getY();
                float endX=event.getX();

                int currentposition=videoView.getCurrentPosition();

                float disX = endX - startX;
                float dixY = endY - startY;
                float distanceX=startX-endX;
                float distanceY = startY - endY;
                //设置快进快退


                    float delta = (distanceX / touchRang1) * duration;
                    int current = (int) Math.min(Math.max(currentposition + delta, 0), duration);
                    if (delta != 0) {
                        seekbarVideo.setProgress(current);
                        currentposition = current;
                        videoView.seekTo(current);
                    }

                    //设置音量和亮度



                    if (endX < screenWidth / 2) {
                        //左边屏幕-调节亮度
                        final double FLING_MIN_DISTANCE = 0.5;
                        final double FLING_MIN_VELOCITY = 0.5;
                        if (distanceY > FLING_MIN_DISTANCE
                                && Math.abs(distanceY) > FLING_MIN_VELOCITY) {
//                        Log.e(TAG, "up");
                            setBrightness(20);
                        }
                        if (distanceY < FLING_MIN_DISTANCE
                                && Math.abs(distanceY) > FLING_MIN_VELOCITY) {
//                        Log.e(TAG, "down");
                            setBrightness(-20);
                        }
                    } else {

                        float delta1 = (distanceY / touchRang) * maxVoice;
                        int voice = (int) Math.min(Math.max(mVol + delta1, 0), maxVoice);
                        if (delta1 != 0) {
                            isMute = false;
                            updataVoice(voice, isMute);
                        }
                    }


                break;
            case MotionEvent.ACTION_UP:


                handler.sendEmptyMessageDelayed(HIDEMEDIACONTROLLER,4000);
                break;

        }
        return super.onTouchEvent(event);
    }


    /*
     *
     * 设置屏幕亮度 lp = 0 全暗 ，lp= -1,根据系统设置， lp = 1; 最亮
     */
    public void setBrightness(float brightness) {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        // if (lp.screenBrightness <= 0.1) {
        // return;
        // }
        lp.screenBrightness = lp.screenBrightness + brightness / 255.0f;
        if (lp.screenBrightness > 1) {
            lp.screenBrightness = 1;
            vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
            long[] pattern = { 10, 200 }; // OFF/ON/OFF/ON...
            vibrator.vibrate(pattern, -1);
        } else if (lp.screenBrightness < 0.2) {
            lp.screenBrightness = (float) 0.2;
            vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
            long[] pattern = { 10, 200 }; // OFF/ON/OFF/ON...
            vibrator.vibrate(pattern, -1);
        }
//        Log.e(TAG, "lp.screenBrightness= " + lp.screenBrightness);
        getWindow().setAttributes(lp);
    }
    @Override
    protected void onDestroy() {

        handler.removeCallbacksAndMessages(null);
        //释放资源的时候要在super之上
        if(myReceiver!=null){
            unregisterReceiver(myReceiver);
            myReceiver=null;
        }
        LogUtil.e("onDestory");
        super.onDestroy();


    }

    private class VoiceSeekBarChangeListener implements SeekBar.OnSeekBarChangeListener {

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            handler.removeMessages(HIDEMEDIACONTROLLER);
            if(fromUser){
//                if (progress>0){
//                    isMute=false;
//                }else{
//                    isMute=true;
//                }
                isMute=false;

                updataVoice(progress,isMute);

            }

        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            handler.removeMessages(HIDEMEDIACONTROLLER);

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            handler.removeMessages(HIDEMEDIACONTROLLER);

        }
    }

    private void updataVoice(int progress,boolean isMute) {
        if(isMute) {
            am.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0);

            seekbarVoice.setProgress(0);
            currentVoice=progress;
        }else{

            am.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);
            seekbarVoice.setProgress(progress);
            currentVoice = progress;

        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if(keyCode==KeyEvent.KEYCODE_VOLUME_DOWN){
            currentVoice--;
            updataVoice(currentVoice,false);
            return true;
        }else if(keyCode==KeyEvent.KEYCODE_VOLUME_UP) {
            currentVoice++;
            updataVoice(currentVoice,false);
            return true;
        }
            return super.onKeyDown(keyCode, event);
    }

    private class MyInfoListener implements MediaPlayer.OnInfoListener {

        @Override
        public boolean onInfo(MediaPlayer mp, int what, int extra) {
            switch (what){
                case MediaPlayer.MEDIA_INFO_BUFFERING_START:
                    Toast.makeText(SystemVideoPlayer.this,"卡顿", Toast.LENGTH_SHORT).show();;
                    ll_buffer.setVisibility(View.VISIBLE);
                    break;
                case MediaPlayer.MEDIA_INFO_BUFFERING_END:
                    Toast.makeText(SystemVideoPlayer.this,"不卡顿了", Toast.LENGTH_SHORT).show();;
                    ll_buffer.setVisibility(View.GONE);
                    break;
            }
            return false;
        }
    }
}
