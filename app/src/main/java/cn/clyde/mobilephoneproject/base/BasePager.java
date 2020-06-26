package cn.clyde.mobilephoneproject.base;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

public abstract  class BasePager {
    //上下文
    public Context context;

    public  View rootview;
    public boolean isInitData;
    public BasePager(Context context){
        this.context=context;
        rootview=initView();
    }


    public abstract View initView() ;

    //子页面初始化数据,联网请求数据,绑定数据的时候重写该方法
    public void initData(){

    }

}
