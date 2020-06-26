package cn.clyde.mobilephoneproject.utils;

import android.content.Context;
import android.content.SharedPreferences;

import cn.clyde.mobilephoneproject.service.MusicPlayerService;

public class CacheUtils {

    public static void putString(Context context,String key,String values){
        SharedPreferences sharedPreferences=context.getSharedPreferences("clyde",Context.MODE_PRIVATE);
        sharedPreferences.edit().putString(key,values).commit();

    }

    public static String getString(Context context,String key){
        SharedPreferences sharedPreferences=context.getSharedPreferences("clyde",Context.MODE_PRIVATE);
        return sharedPreferences.getString(key,"");
    }

    public static void putplaymode(Context context,String key,int values){
        SharedPreferences sharedPreferences=context.getSharedPreferences("playmode",context.MODE_PRIVATE);
        sharedPreferences.edit().putInt(key,values).commit();
    }
    public static int getplaymode(Context context,String key){
        SharedPreferences sharedPreferences=context.getSharedPreferences("playmode",context.MODE_PRIVATE);
        return sharedPreferences.getInt(key, MusicPlayerService.REPEAT_NORMAL);
    }

}

