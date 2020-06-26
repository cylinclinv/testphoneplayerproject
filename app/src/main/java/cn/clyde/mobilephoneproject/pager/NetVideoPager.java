package cn.clyde.mobilephoneproject.pager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import cn.clyde.mobilephoneproject.R;
import cn.clyde.mobilephoneproject.activity.SystemVideoPlayer;
import cn.clyde.mobilephoneproject.adapter.NetVideoPagerAdapter;
import cn.clyde.mobilephoneproject.base.BasePager;
import cn.clyde.mobilephoneproject.domain.MediaItem;
import cn.clyde.mobilephoneproject.utils.CacheUtils;
import cn.clyde.mobilephoneproject.utils.Contants;
import cn.clyde.mobilephoneproject.utils.LogUtil;
import cn.clyde.mobilephoneproject.view.XListView;

public class NetVideoPager extends BasePager {
    @ViewInject(R.id.listview)
    private XListView mListView;

    @ViewInject(R.id.tv_nonet)
    private TextView tv_nonet;

    @ViewInject(R.id.pb_loading)
    private ProgressBar mprogressBar;

    private ArrayList<MediaItem> mediaItems;

    private NetVideoPagerAdapter adapter;

    private  boolean isLoadMore=false;



    public NetVideoPager(Context context) {
        super(context);
    }

    @Override
    public View initView() {

        View view= View.inflate(context, R.layout.netvideo_pager,null);

        x.view().inject(NetVideoPager.this,view);
        mListView.setOnItemClickListener(new MyOnItemClickListener());
        mListView.setPullLoadEnable(true);
        mListView.setXListViewListener(new MyXListListener());

        return view;
    }
    class MyXListListener implements XListView.IXListViewListener{

        @Override
        public void onRefresh() {
            getDataFromNet();
        }

        @Override
        public void onLoadMore() {
            getMoreDataFromNet();
        }
    }

    private void getMoreDataFromNet() {

        RequestParams params=new RequestParams(Contants.NET_URL);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                LogUtil.e("联网成功=="+result);
                processData(result);
                isLoadMore=true;
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                LogUtil.e("联网失败=="+ex.getMessage());
                Toast.makeText(context,"联网失败=="+ex.getMessage(),Toast.LENGTH_LONG).show();
                isLoadMore=false;
            }

            @Override
            public void onCancelled(CancelledException cex) {
                LogUtil.e("onCancelled=="+cex.getMessage());
                isLoadMore=false;
            }

            @Override
            public void onFinished() {
                LogUtil.e("onFinished==");
                isLoadMore=false;
            }
        });

    }

    private  void onLoad(){
            mListView.stopRefresh();
            mListView.stopLoadMore();
            mListView.setRefreshTime("更新事件:"+getSysteTime());
    }


    //得到系统的时间
    private String getSysteTime() {
        SimpleDateFormat formate=new SimpleDateFormat("HH:mm:ss");
        return formate.format(new Date());
    }
    class MyOnItemClickListener implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            MediaItem mediaItem = mediaItems.get(position);
            Intent intent = new Intent(context, SystemVideoPlayer.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("videolist",mediaItems);
            intent.putExtras(bundle);
            intent.putExtra("position",position-1);
            context.startActivity(intent);


        }
    }

    @Override
    public void initData() {
        super.initData();
        String saveJson=CacheUtils.getString(context,Contants.NET_URL);
        if (!TextUtils.isEmpty(saveJson)){
            processData(saveJson);
        }
        LogUtil.e("在线视频界面被初始化了2");
        getDataFromNet();

    }

    private void getDataFromNet() {
        RequestParams params=new RequestParams(Contants.NET_URL);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                LogUtil.e("联网成功=="+result);
                processData(result);
                CacheUtils.putString(context,Contants.NET_URL,result);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                LogUtil.e("联网失败=="+ex.getMessage());
                Toast.makeText(context,"联网失败=="+ex.getMessage(),Toast.LENGTH_LONG).show();
                showData();
            }

            @Override
            public void onCancelled(CancelledException cex) {
                LogUtil.e("onCancelled=="+cex.getMessage());

            }

            @Override
            public void onFinished() {
                LogUtil.e("onFinished==");

            }
        });
    }

    private void processData(String json) {

        if(!isLoadMore){

            mediaItems=paraseJson(json);
            showData();

        }else{

            isLoadMore=false;
            mediaItems.addAll(paraseJson(json));
            adapter.notifyDataSetChanged();
            onLoad();
        }

    }

    private void showData() {
        if (mediaItems!=null&&mediaItems.size()>0){
            adapter=new NetVideoPagerAdapter(context,mediaItems);
            mListView.setAdapter(adapter);
            onLoad();
            tv_nonet.setVisibility(View.GONE);
        }else{

            tv_nonet.setVisibility(View.VISIBLE);
        }

        mprogressBar.setVisibility(View.GONE);
    }

    private ArrayList<MediaItem> paraseJson(String json) {
        ArrayList<MediaItem> mediaItems=new ArrayList<>();
        try {
            JSONObject jsonObject=new JSONObject(json);
            JSONArray jsonArray=jsonObject.optJSONArray("trailers");
            if(jsonArray!=null&&jsonArray.length()>0){
                for (int i=0;i<jsonArray.length();i++){


                    JSONObject jsonObjectItem= (JSONObject) jsonArray.get(i);
                    if(jsonObjectItem!=null){
                        MediaItem mediaItem=new MediaItem();
                        String movieName=jsonObjectItem.optString("movieName");
                        mediaItem.setName(movieName);
                        String videoTitle=jsonObjectItem.optString("videoTitle");
                        mediaItem.setDesc(videoTitle);

                        String imageUrl=jsonObjectItem.optString("coverImg");
                        mediaItem.setImageurl(imageUrl);
                        String hightUrl=jsonObjectItem.optString("url");
                        mediaItem.setData(hightUrl);
                        mediaItems.add(mediaItem);
                    }

                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return mediaItems;
    }
}
