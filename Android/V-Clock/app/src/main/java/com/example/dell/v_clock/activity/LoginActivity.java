package com.example.dell.v_clock.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.dell.v_clock.R;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * This activity is the interface for the stuff to login which can jump to RegisterActivity.
 * 这个活动是工作人员登录的接口，能够跳转到注册界面。
*/

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Button tempButton = (Button)findViewById(R.id.bt_temp_button);
        tempButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

/*
Just test function.
 */
        final TextView tempTextView = (TextView)findViewById(R.id.tv_temp_show_json);
        RequestQueue queue = Volley.newRequestQueue(this);
//        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest("http://httpbin.org/delay/3", null,
//                new Response.Listener<JSONObject>() {
//                    @Override
//                    public void onResponse(JSONObject response) {
//                        tempTextView.setText(response.toString());
//                    }
//                }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                Log.e("TAG", error.getMessage(), error);
//            }
//        });
//        queue.add(jsonObjectRequest);



        StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://121.250.222.39:8080/VClock_wolf/servlet/RegisterServlet", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                tempTextView.setText(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                tempTextView.setText(error.toString());
            }
        }) {
//            @Override
//            public byte[] getBody() throws AuthFailureError {
//                String httpPostBody;
//                String httpPostBody2;
//                try {
//                    httpPostBody = "12345678910";
//                    httpPostBody2 = "12345678910"+ URLEncoder.encode("{{%stuffToBe Escaped/","UTF-8");
//                } catch (UnsupportedEncodingException exception) {
//                    Log.e("ERROR", "exception", exception);
//                    return null;
//                }
//                return httpPostBody2.getBytes();
//            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<String, String>();
                map.put("etel", "12345678910");
                map.put("etel1", "12345678911");
                return map;
            }
        };
        queue.add(stringRequest);
    }
}
