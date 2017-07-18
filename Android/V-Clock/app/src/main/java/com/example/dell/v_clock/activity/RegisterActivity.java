package com.example.dell.v_clock.activity;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.dell.v_clock.R;
import com.example.dell.v_clock.ServerInfo;
import com.example.dell.v_clock.util.CheckLegality;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * The staff can register account here.
 * 工作人员能够在此注册。
 */
public class RegisterActivity extends AppCompatActivity {

    final String TAG = "RegisterActivity";

    private AlphaAnimation buttonClick = new AlphaAnimation(1F, 0.8F);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

//        Save async checking phone number
//        异步检测手机号保留
//        final RequestQueue requestQueue = Volley.newRequestQueue(this);

        final EditText employeeName = (EditText) findViewById(R.id.et_employee_name);
        final RadioButton sexMan = (RadioButton) findViewById(R.id.rb_sex_man);
        final RadioButton sexWoman = (RadioButton) findViewById(R.id.rb_sex_woman);
        final EditText employeePhone = (EditText) findViewById(R.id.et_employee_phone);

        sexMan.setChecked(true);

        Button goToSelectRegisterPhoto = (Button) findViewById(R.id.bt_go_to_select_register_photo);
        goToSelectRegisterPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.startAnimation(buttonClick);
                if (!CheckLegality.isNameContainSpace(employeeName.getText().toString())) {
                    Toast.makeText(RegisterActivity.this, "姓名为空或包含空格!", Toast.LENGTH_SHORT).show();
                } else if(employeeName.getText().toString().length() >= 20) {
                    Toast.makeText(RegisterActivity.this, "姓名长度过长！", Toast.LENGTH_SHORT).show();
                } else if (employeePhone.getText().length() != 11) {
                    Toast.makeText(RegisterActivity.this, "手机号格式错误！", Toast.LENGTH_SHORT).show();
                } else {
                    //The information of employee if legal. Judge whether employee tel is registered.
                    // 数据已合法。判断工作人员手机是否被注册。异步检测保留
//                    StringRequest registerStat = new StringRequest(ServerInfo.LOGIN_URL, new Response.Listener<String>() {
//                        @Override
//                        public void onResponse(String response) {
//                            //Check whether etel is registered here.
//                            //在此处检测手机号是否已经注册。
//                        }
//                    }, new Response.ErrorListener() {
//                        @Override
//                        public void onErrorResponse(VolleyError error) {
//
//                        }
//                    });
//                    requestQueue.add(registerStat);

                    //employee tel is not registered.
                    //员工手机号未注册
                    HashMap<String, String> employeeInfoMap = new HashMap<>();
                    employeeInfoMap.put("ename", employeeName.getText().toString());
                    if (sexMan.isChecked()) {
                        employeeInfoMap.put("esex", "男");
                    } else if(sexWoman.isChecked()){
                        employeeInfoMap.put("esex", "女");
                    }
                    employeeInfoMap.put("etel", employeePhone.getText().toString());

                    //Go to select photos activity.
                    //跳转到选择用头像的活动
                    Intent intent = new Intent(RegisterActivity.this, SelectPhotoActivity.class);
                    intent.putExtra("employeeInfoHashMap", employeeInfoMap);
                    startActivity(intent);
                }
            }
        });
    }
}
