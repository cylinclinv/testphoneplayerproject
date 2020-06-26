// IMusicPlayerService.aidl
package cn.clyde.mobilephoneproject.service;

// Declare any non-default types here with import statements

interface IMusicPlayerService {



     void openAudio(int position);

     void  start();
     void  pause();

     void  stop();
     int  getCurrentPosition();

     int  getDuration();

     String getArtist();


     String getName();

     String getAudioPath();

     void next();

     void pre();


      void setPlayMode(int playMode);

     int getPlayMode();

      boolean isPlaying();

      void seekTo(int progress);
}
