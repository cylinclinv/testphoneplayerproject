package cn.clyde.mobilephoneproject.service;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.os.RemoteException;
import android.provider.MediaStore;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import cn.clyde.mobilephoneproject.R;
import cn.clyde.mobilephoneproject.activity.SystemAudioPlayer;
import cn.clyde.mobilephoneproject.domain.MediaItem;
import cn.clyde.mobilephoneproject.utils.CacheUtils;

public class MusicPlayerService extends Service {

    public static final String OPENAUDIO = "cn.clyde.moblie_OPENAUDIO";
    private ArrayList<MediaItem> mediaItems;
    private int position;
    private MediaItem mediaItem;
    private MediaPlayer mediaPlayer;
    private NotificationManager manager;


    public static final int REPEAT_NORMAL=1;
    public static final int REPEAT_SINGLE=2;
    public static final int REPEAT_ALL=3;
    private int playmode=REPEAT_NORMAL;

    @Override
    public void onCreate() {
        super.onCreate();
        getDataFromLocal();
        CacheUtils.getplaymode(this, "playmode");
    }

    private void getDataFromLocal() {

        mediaItems=new ArrayList<MediaItem>();
        new Thread(){
            @Override
            public void run() {
                super.run();
                ContentResolver resolver=getContentResolver();
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

            }
        }.start();
    }


    private IMusicPlayerService.Stub stub=new IMusicPlayerService.Stub() {
        MusicPlayerService service=MusicPlayerService.this;

        @Override
        public void openAudio(int position) throws RemoteException {
            service.openAudio(position);
        }

        @Override
        public void start() throws RemoteException {
            service.start();




        }

        @Override
        public void pause() throws RemoteException {
            service.pause();
        }

        @Override
        public void stop() throws RemoteException {
            service.stop();
        }

        @Override
        public int getCurrentPosition() throws RemoteException {
            return service.getCurrentPosition();
        }

        @Override
        public int getDuration() throws RemoteException {
            return service.getDuration();
        }

        @Override
        public String getArtist() throws RemoteException {
            return service.getArtist();
        }

        @Override
        public String getName() throws RemoteException {
            return service.getName();
        }

        @Override
        public String getAudioPath() throws RemoteException {
            return service.getAudioPath();
        }

        @Override
        public void next() throws RemoteException {
            service.next();
        }

        @Override
        public void pre() throws RemoteException {
            service.pre();
        }

        @Override
        public void setPlayMode(int playMode) throws RemoteException {
            service.setPlayMode(playMode);
        }

        @Override
        public int getPlayMode() throws RemoteException {
            return service.getPlayMode();
        }

        @Override
        public boolean isPlaying() throws RemoteException {
            return mediaPlayer.isPlaying();
        }

        @Override
        public void seekTo(int progress) throws RemoteException {
            mediaPlayer.seekTo(progress);
        }
    };
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return stub;
    }

    private void openAudio(int position){
        this.position=position;
        if(mediaItems!=null&&mediaItems.size()>0){
            mediaItem=mediaItems.get(position);
            if (mediaPlayer!=null){
//                mediaPlayer.release();
                mediaPlayer.reset();
            }

            try {

                mediaPlayer=new MediaPlayer();
                mediaPlayer.setOnPreparedListener(new MyOnPreparedListener());
                mediaPlayer.setOnCompletionListener(new MyOnCompletionListener());
                mediaPlayer.setOnErrorListener(new MyOnErrorListener());
                mediaPlayer.setDataSource(mediaItem.getData());
                mediaPlayer.prepareAsync();


                if(playmode==MusicPlayerService.REPEAT_SINGLE){
                    mediaPlayer.setLooping(true);
                }else{
                    mediaPlayer.setLooping(false);

                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else{
            Toast.makeText(MusicPlayerService.this,"没有音乐数据",Toast.LENGTH_SHORT).show();
        }

    }
class MyOnPreparedListener implements MediaPlayer.OnPreparedListener{

    @Override
    public void onPrepared(MediaPlayer mp) {

        notifyChange(OPENAUDIO);
        start();
    }
}

    private void notifyChange(String openaudio) {
        Intent intent=new Intent(openaudio);
        sendBroadcast(intent);
    }

    class MyOnErrorListener implements MediaPlayer.OnErrorListener{

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        next();
        return true;
    }
}

class  MyOnCompletionListener implements MediaPlayer.OnCompletionListener{

    @Override
    public void onCompletion(MediaPlayer mp) {
        next();
    }
}


    private void  start(){
        mediaPlayer.start();

//        manager=(NotificationManager)getSystemService(NOTIFICATION_SERVICE);
//        Intent intent=new Intent(this,SystemAudioPlayer.class);
//        intent.putExtra("Notification",true);
//        PendingIntent pendingIntent=PendingIntent.getActivity(this,11,intent,PendingIntent.FLAG_UPDATE_CURRENT);
//        Notification notification=new Notification.Builder(this)
//                .setSmallIcon(R.drawable.notification_music_playing)
//                .setContentTitle("CVideo播放器")
//                .setContentText("正在播放"+getName())
//                .setContentIntent(pendingIntent)
//                .build();
//        manager.notify(11,notification);


        String id = "channel_001";
        String name = "name";
        manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Notification notification = null;
        Intent intent=new Intent(this,SystemAudioPlayer.class);
        intent.putExtra("Notification",true);
        PendingIntent pendingIntent=PendingIntent.getActivity(this,11,intent,PendingIntent.FLAG_UPDATE_CURRENT);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            @SuppressLint("WrongConstant") NotificationChannel mChannel = new NotificationChannel(id, name, NotificationManager.IMPORTANCE_LOW);
            manager.createNotificationChannel(mChannel);
            notification = new Notification.Builder(this)
                    .setChannelId(id)
                    .setContentTitle("CVideo")
//                    .setcontent()//使用这个进行按钮的设置,具体可以查看保存的demo包,有相关示例
                    .setContentText("正在播放"+getName())
                    .setAutoCancel(false)
                    .setOngoing(false)
                    .setContentIntent(pendingIntent)
                    .setSmallIcon(R.drawable.notification_music_playing).build();
            notification.flags |= Notification.FLAG_NO_CLEAR;//不被清除
            notification.flags |= Notification.FLAG_ONGOING_EVENT;//不被侧滑删除


        } else {
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                    .setContentTitle("CVideo")
                    .setContentText("正在播放"+getName())
                    .setAutoCancel(false)
                    .setOngoing(false)
                    .setSmallIcon(R.drawable.notification_music_playing)
                    .setOngoing(true)
                    .setContentIntent(pendingIntent)
                    .setChannelId(id);//无效
            notification.flags |= Notification.FLAG_NO_CLEAR;
            notification.flags |= Notification.FLAG_ONGOING_EVENT;




            notification = notificationBuilder.build();

        }

        manager.notify(11, notification);



    }
    private void  pause(){
        mediaPlayer.pause();
        manager.cancel(11);

    }

    private void  stop(){

    }

    private int  getCurrentPosition(){
        return mediaPlayer.getCurrentPosition();
    }

    private int  getDuration(){
        return mediaPlayer.getDuration();
    }

    private String getArtist(){
        return mediaItem.getArtist();
    }


    private String getName(){
        return mediaItem.getName();
    }

    private String getAudioPath(){
        return mediaItem.getData();
    }

    private void next(){

        setNextPosition();
        openNextAudio();
    }

    private void openNextAudio() {

        int playmode = getPlayMode();

        if(playmode==MusicPlayerService.REPEAT_NORMAL){
            if(position<mediaItems.size()){
                openAudio(position);
            }else{
                position=mediaItems.size()-1;
            }
         }else if(playmode==MusicPlayerService.REPEAT_SINGLE){
            openAudio(position);

        }else if(playmode==MusicPlayerService.REPEAT_ALL){
            openAudio(position);

        }else{
            if(position<mediaItems.size()){
                openAudio(position);
            }else{
                position=mediaItems.size()-1;
            }
        }
    }

    private void setNextPosition() {
        int playmode = getPlayMode();

        if(playmode==MusicPlayerService.REPEAT_NORMAL){
            position++;
        }else if(playmode==MusicPlayerService.REPEAT_SINGLE){
//            position++;
//            if(position>=mediaItems.size()){
//                position=0;
//            }
        }else if(playmode==MusicPlayerService.REPEAT_ALL){
            position++;
            if(position>=mediaItems.size()){
                position=0;
            }
        }else{
            position++;
        }
    }


    private void pre(){
        setPrePosition();
        openPreAudio();
    }

    private void openPreAudio() {
        int playmode = getPlayMode();

        if(playmode==MusicPlayerService.REPEAT_NORMAL){
            if(position>=0){
                openAudio(position);
            }else{
                position=0;
            }
        }else if(playmode==MusicPlayerService.REPEAT_SINGLE){
            openAudio(position);
        }else if(playmode==MusicPlayerService.REPEAT_ALL){
            openAudio(position);
        }else{
            if(position>=0){
                openAudio(position);
            }else{
                position=0;
            }
        }
    }

    private void setPrePosition() {
        int playmode = getPlayMode();

        if(playmode==MusicPlayerService.REPEAT_NORMAL){
            position--;
        }else if(playmode==MusicPlayerService.REPEAT_SINGLE){
//            position--;
//            if(position<0){
//                position=mediaItems.size()-1;
//            }
        }else if(playmode==MusicPlayerService.REPEAT_ALL){
            position--;
            if(position<0){
                position=mediaItems.size()-1;
            }
        }else{
            position--;
        }
    }


    private  void setPlayMode(int playMode){
        this.playmode=playMode;
        CacheUtils.putplaymode(this,"playmode",playMode);
        if(playmode==MusicPlayerService.REPEAT_SINGLE){
            mediaPlayer.setLooping(true);
        }else{
            mediaPlayer.setLooping(false);

        }
    }

    private int getPlayMode(){
        return playmode;
    }

    private boolean isPlaying(){
        return mediaPlayer.isPlaying();
    }

}
