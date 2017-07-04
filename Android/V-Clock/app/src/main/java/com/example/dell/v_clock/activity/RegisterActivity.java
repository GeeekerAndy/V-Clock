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

import org.json.JSONException;
import org.json.JSONObject;

/**
 * The staff can register account here.
 * 工作人员能够在此注册。
*/
public class RegisterActivity extends AppCompatActivity {

    private AlphaAnimation buttonClick = new AlphaAnimation(1F, 0.8F);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        final RequestQueue requestQueue  = Volley.newRequestQueue(this);

        final EditText employeeName = (EditText)findViewById(R.id.et_employee_name);
        RadioGroup sexGroup = (RadioGroup)findViewById(R.id.rg_sex_group);
        final RadioButton sexMan = (RadioButton)findViewById(R.id.rb_sex_man);
        RadioButton sexWoman = (RadioButton)findViewById(R.id.rb_sex_woman);
        final EditText employeePhone = (EditText)findViewById(R.id.et_employee_phone);
        final JSONObject employeeInfo = new JSONObject();

        sexMan.setChecked(true);

        Button goToSelectRegisterPhoto = (Button)findViewById(R.id.bt_go_to_select_register_photo);
        goToSelectRegisterPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.startAnimation(buttonClick);

                //Go to select photos activity.
                //跳转到选择用头像的活动
                Intent intent = new Intent(RegisterActivity.this, SelectPhotoActivity.class);
                startActivity(intent);

                if(employeeName.getText().toString().equals("")) {
                    Toast.makeText(RegisterActivity.this, "姓名为空!", Toast.LENGTH_SHORT).show();
                } else if(employeePhone.getText().length() != 11) {
                    Toast.makeText(RegisterActivity.this, "手机号格式错误！", Toast.LENGTH_SHORT).show();
                } else {
                    //The information of employee if legal. Judge whether employee tel is registered.
                    // 数据已合法。判断工作人员手机是否被注册。
                    String url = "http://192.168.2.101:80/serlet/loginServlet";
                    StringRequest registerStat = new StringRequest(url, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
//                            if(requestQueue.equals())
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                        }
                    });
                    requestQueue.add(registerStat);


                    //employee tel is not registered.
                    try {
                        employeeInfo.put("ename", employeeName.getText().toString());
                        if(sexMan.isSelected()) {
                            employeeInfo.put("esex", "男");
                        } else {
                            employeeInfo.put("esex", "女");
                        }
                        employeeInfo.put("etel", employeePhone.getText().toString());

//                        //Go to select photos activity.
//                        //跳转到选择用头像的活动
//                        Intent intent = new Intent(RegisterActivity.this, SelectPhotoActivity.class);
//                        startActivity(intent);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }
        });
    }
}
