package cn.clyde.mobilephoneproject.activity;

import android.app.Activity;
import android.os.Bundle;
import android.provider.SyncStateContract;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.google.gson.Gson;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

import cn.clyde.mobilephoneproject.R;
import cn.clyde.mobilephoneproject.adapter.SearchAdapter;
import cn.clyde.mobilephoneproject.domain.SearchBean;
import cn.clyde.mobilephoneproject.utils.Contants;


public class SearchActivity extends Activity {

    private EditText etInput;
    private ImageView ivVoice;
    private TextView tvSearch;
    private ListView listview;
    private ProgressBar progressBar;
    private TextView tvNodata;


    private String url;
    private List<SearchBean.ItemData> items;

    private SearchAdapter searchAdapter;



    private void findViews() {
        etInput = (EditText)findViewById( R.id.et_input );
        ivVoice = (ImageView)findViewById( R.id.iv_voice );
        tvSearch = (TextView)findViewById( R.id.tv_searchin );
        listview = (ListView)findViewById( R.id.listview );
        progressBar = (ProgressBar)findViewById( R.id.progressBar );
        tvNodata = (TextView)findViewById( R.id.tv_nodata );

        MyOnClickListener myOnClickListener=new MyOnClickListener();
        ivVoice.setOnClickListener(myOnClickListener);
        tvSearch.setOnClickListener(myOnClickListener);
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        findViews();
    }

    private class MyOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.iv_voice:
                    Toast.makeText(SearchActivity.this,"语言输入",Toast.LENGTH_SHORT).show();
                    break;
                case R.id.tv_searchin:
//                    Toast.makeText(SearchActivity.this,"搜索",Toast.LENGTH_SHORT).show();
                    searchText();
                    break;

            }
        }
    }

    private void searchText() {
        String text=etInput.getText().toString().trim();
        Toast.makeText(SearchActivity.this,"开始搜索",Toast.LENGTH_SHORT).show();

        if(!TextUtils.isEmpty(text)){
            try {
                text= URLEncoder.encode(text,"utf-8");
                url= Contants.SEARCH_URL+text;
                getDataFromNet();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

        }
    }

    private void getDataFromNet() {
        progressBar.setVisibility(View.VISIBLE);
        RequestParams params=new RequestParams(url);
        x.http().get(params,new Callback.CommonCallback<String>(){
            @Override
            public void onSuccess(String result) {
                processData(result);
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {

            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {
                progressBar.setVisibility(View.GONE);

            }
        });
    }

    private void processData(String result) {
        SearchBean searchBean=parsedJson(result);
        items=searchBean.getItems();

        if(items!=null&&items.size()>0){
            //设置适配器

            searchAdapter=new SearchAdapter(this,items);
            listview.setAdapter(searchAdapter);
            progressBar.setVisibility(View.GONE);
        }else {
            tvNodata.setVisibility(View.VISIBLE);
        }
        progressBar.setVisibility(View.GONE);



    }

    private SearchBean parsedJson(String result) {
        Gson gson=new Gson();

        return gson.fromJson(result,SearchBean.class);
    }
}
