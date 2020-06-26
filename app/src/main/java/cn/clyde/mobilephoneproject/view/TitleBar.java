package cn.clyde.mobilephoneproject.view;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import cn.clyde.mobilephoneproject.R;
import cn.clyde.mobilephoneproject.activity.SearchActivity;

public class TitleBar extends LinearLayout implements View.OnClickListener {
    private View tv_seach;
    private View rl_game;
    private View  iv_record;

    public  Context context;



    //在代码中实例化该类
    public TitleBar(Context context) {
        this(context,null);
    }

    //在布局中使用该类,系统通过反射方式构造该类

    public TitleBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs,0);
        this.context=context;
    }

    //需要设置样式的时候,实例化该类

    public TitleBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
//        this.context = context;
    }

    //当布局文件加载完,调用该方法
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        //获得view的子实例
        tv_seach=getChildAt(1);
        rl_game=getChildAt(2);
        iv_record=getChildAt(3);

        tv_seach.setOnClickListener(this);
        rl_game.setOnClickListener(this);
        iv_record.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.tv_seach:
//                Toast.makeText(context, "搜索", Toast.LENGTH_SHORT).show();
                Intent intent=new Intent(context, SearchActivity.class);
                context.startActivity(intent);
                break;
            case R.id.rl_game:
                Toast.makeText(context, "游戏", Toast.LENGTH_SHORT).show();
                break;
            case R.id.iv_record:
                Toast.makeText(context, "播放历史", Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
