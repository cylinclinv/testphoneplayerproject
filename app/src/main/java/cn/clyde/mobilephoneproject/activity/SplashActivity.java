package cn.clyde.mobilephoneproject.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;

import cn.clyde.mobilephoneproject.R;


public class SplashActivity extends AppCompatActivity {

    private static final String TAG = SplashActivity.class.getSimpleName();
    private Handler handler=new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //两秒后执行,执行在主线程
                stratMainActivity();
                Log.e(TAG,"当前线程的名称=="+Thread.currentThread().getName());

            }
        },2000);
    }

    //有两种方法防止跳转多次
    //1.设置boolean值
    //2.设置activity为单例模式
    private boolean isStartMainActivity=false;

    //跳转到主页面,关闭当前页面
    private void stratMainActivity() {

        if(!isStartMainActivity) {
            isStartMainActivity=true;
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        Log.e(TAG,"onTouchEvent==Action"+event.getAction());
        stratMainActivity();
        return super.onTouchEvent(event);

    }

    @Override
    protected void onDestroy() {
        handler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }
}
