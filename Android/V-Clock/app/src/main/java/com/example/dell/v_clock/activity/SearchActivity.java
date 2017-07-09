package com.example.dell.v_clock.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.dell.v_clock.R;
import com.example.dell.v_clock.ServerInfo;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SearchActivity extends AppCompatActivity implements View.OnClickListener, TextView.OnEditorActionListener, AdapterView.OnItemClickListener {

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

    final String MY_GEUST_SEATCH_TYPE = "0";
    final String PARTITIAL_NAME_SEATCH_TYPE = "1";
    final String WHOLE_NAME_SEATCH_TYPE = "2";

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

        from = new String[]{"iv_my_guest_avatar","tv_my_guest_name"};
        to = new int[]{R.id.iv_my_guest_avatar,R.id.tv_my_guest_name};
        simpleAdapter = new SimpleAdapter(this,dataList_guest,R.layout.item_children_all_guest,from,to);

        lv_search_result.setAdapter(simpleAdapter);
        lv_search_result.setOnItemClickListener(this);
    }

    /**
     *
     */
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            //更新UI  ListView信息
            refreshGuestList();
        }
    };

    /**
     * 显示搜索结果 刷新ListView
     */
    private void refreshGuestList() {

    }

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
            String name  = et_search.getText().toString();
            if (name.equals("") || name.equals(" "))
            {
                Toast.makeText(this, "请输入正确的姓名！", Toast.LENGTH_SHORT).show();
                return false;
            }
            //向后台发送请求
            transferRequest(name);
            //发送Message 更新ListView的显示结果
            handler.sendEmptyMessage(0);
            return true;
        }
        return false;
    }

    /**
     * 向后台发送请求
     */
    private void transferRequest(final String name) {
        JSONObject jsonObject = new JSONObject();
        JsonObjectRequest loginRequest = new JsonObjectRequest(Request.Method.POST, ServerInfo.CREATE_NEW_GUEST_URL, jsonObject,
                new SearchResponseListener(), new SearchResponseErrorListener()) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String,String> searchInfo = new  HashMap<>();
                searchInfo.put("gname",name);
                searchInfo.put("tip",WHOLE_NAME_SEATCH_TYPE);
                SharedPreferences sp = getSharedPreferences("loginInfo", MODE_PRIVATE);
                String eid = sp.getString("eid", null);
                searchInfo.put("eid",eid);
                return searchInfo;
            }
        };
        //访问服务器请求队列
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(loginRequest);
    }

    /**
     *
     */
    private class SearchResponseListener implements Response.Listener<JSONObject> {


        @Override
        public void onResponse(JSONObject response) {

        }
    }

    /**
     *
     */
    private class SearchResponseErrorListener implements Response.ErrorListener {
        @Override
        public void onErrorResponse(VolleyError error) {
            Log.i("Transfer", "收到服务器回复");
            //提示网络连接失败
            Toast.makeText(SearchActivity.this, "服务器连接失败", Toast.LENGTH_SHORT).show();
        }
    }



    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

    }
}
