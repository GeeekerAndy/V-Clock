package com.example.dell.v_clock.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.example.dell.v_clock.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SearchActivity extends AppCompatActivity implements View.OnClickListener, TextView.OnEditorActionListener {

    //“取消”字样
    TextView tv_cancel;
    //显示搜索结果的的列表
    ListView lv_search_result;
    //搜索框
    EditText et_search;
    //数据源
    List<Map<String, Object>> dataList_guest;
    //数据对应的标识
    String[] from;
    //将数据添加到的view组件
    int[] to;
    //ListView 适配器
    SimpleAdapter simpleAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        et_search = (EditText) findViewById(R.id.et_search);
        et_search.setOnEditorActionListener(this);

        tv_cancel = (TextView) findViewById(R.id.tv_cancel);
        tv_cancel.setOnClickListener(this);

        lv_search_result = (ListView) findViewById(R.id.lv_search_result);
        dataList_guest = new ArrayList<>();


    }

    /**
     *
     */
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            //更新UI  ListView信息

        }
    };

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_cancel:
                this.finish();
                break;
        }
    }

    @Override
    public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
        if (i == EditorInfo.IME_ACTION_SEARCH) {
            //发送Message 更新ListView的显示结果
            handler.sendEmptyMessage(0);
        }
        return false;
    }
}
