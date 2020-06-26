package cn.clyde.mobilephoneproject.pager;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import cn.clyde.mobilephoneproject.base.BasePager;
import cn.clyde.mobilephoneproject.utils.LogUtil;

public class NetAudioPager extends BasePager {
    private TextView tv;

    public NetAudioPager(Context context) {
        super(context);
    }

    @Override
    public View initView() {
        tv=new TextView(context);
//        tv.setText("在线音乐");
        LogUtil.e("在线音乐界面被初始化了");
        tv.setTextSize(35);
        tv.setTextColor(Color.RED);
        tv.setGravity(Gravity.CENTER);
        initData();
        return tv;
    }

    @Override
    public void initData() {
        super.initData();
        tv.setText("在线音乐页面");
        LogUtil.e("在线音乐界面被初始化了");
    }
}
