package cn.clyde.mobilephoneproject.activity;


import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.util.ArrayList;

import cn.clyde.mobilephoneproject.R;
import cn.clyde.mobilephoneproject.base.BasePager;
import cn.clyde.mobilephoneproject.pager.AudioPager;
import cn.clyde.mobilephoneproject.pager.NetAudioPager;
import cn.clyde.mobilephoneproject.pager.NetVideoPager;
import cn.clyde.mobilephoneproject.pager.VideoPager;


//主页面
public class MainActivity extends FragmentActivity{

    private FrameLayout fl_main_content;
    private RadioGroup rg_bottom_tag;

    private static ArrayList<BasePager> basePagers;
    //radioGroup选中的位置
    private static int position=0;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fl_main_content=findViewById(R.id.fl_main_content);
        rg_bottom_tag=findViewById(R.id.rg_bottom_tag);
        rg_bottom_tag.check(R.id.rb_video);

        basePagers=new ArrayList<>();
        basePagers.add(new VideoPager(this));
        basePagers.add(new NetVideoPager(this));
        basePagers.add(new AudioPager(this));
        basePagers.add(new NetAudioPager(this));

        rg_bottom_tag.setOnCheckedChangeListener(new MyCheckedChangeListener());
        rg_bottom_tag.clearCheck();
        rg_bottom_tag.check(R.id.rb_video);

    }

    private class MyCheckedChangeListener implements RadioGroup.OnCheckedChangeListener {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            switch(checkedId){
                default:
                    position=0;
                    break;
                case R.id.rb_netvideo:
                    position=1;
                    break;
                case R.id.rb_audio:
                    position=2;
                    break;
//                case R.id.rb_netaudio:
//                    position=3;
//                    break;
            }
            setFragment();
        }

        private void setFragment() {
            //1.得到FragmentManager
            FragmentManager fm=getSupportFragmentManager();
            //2.开启事务
            FragmentTransaction ft=fm.beginTransaction();
            //3.替换
            ft.replace(R.id.fl_main_content,new BaseFragment());
            //4.提交事务
            ft.commit();
        }
    }

    public static class BaseFragment extends Fragment{
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

            BasePager basePager=getBasePager();
            if(basePager!=null){
                basePager.initData();
                return basePager.rootview;
            }
            return null;
        }

    }

    public  static BasePager getBasePager() {
        BasePager basePager=basePagers.get(position);
        if(basePager!=null&&basePager.isInitData){
            //这个判断初始化,第一次初始化fragmet的时候设true
//            basePager.isInitData=true;
            basePager.initData();//联网请求或绑定数据
            return basePager;
        }
        return  basePager;
    }

    private  boolean isExit=false;
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK){
            if(position!=0){
                position=0;
                rg_bottom_tag.check(R.id.rb_video);
                return true;
            }else if(!isExit){
                isExit=true;
                Toast.makeText(MainActivity.this,"再按一次退出",Toast.LENGTH_SHORT).show();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        isExit=false;
                    }
                },2000);
                return true;
            }
        }

        return super.onKeyUp(keyCode, event);
    }
}