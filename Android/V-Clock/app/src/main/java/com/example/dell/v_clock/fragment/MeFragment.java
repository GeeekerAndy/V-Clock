package com.example.dell.v_clock.fragment;


import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.Image;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.example.dell.v_clock.R;
import com.example.dell.v_clock.ServerInfo;
import com.example.dell.v_clock.activity.LoginActivity;
import com.example.dell.v_clock.activity.UpdateEmployeePwdActivity;
import com.example.dell.v_clock.util.ImageUtil;
import com.example.dell.v_clock.util.JSONObjectRequestMapParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

/**
 * The fragment shows the detail of one stuff.
 * 这个碎片展示了一位工作人员的详细信息
 */
public class MeFragment extends Fragment {


    public MeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_me, container, false);

        final SharedPreferences sp = getContext().getSharedPreferences("loginInfo", MODE_PRIVATE);
        final ImageView employeeAvatar = view.findViewById(R.id.iv_employee_avatar);
        final TextView employeeName = view.findViewById(R.id.tv_employee_name);
        final TextView employeeGender = view.findViewById(R.id.tv_employee_gender);
        final TextView employeeID = view.findViewById(R.id.tv_employee_id);
        final TextView employeeTel = view.findViewById(R.id.tv_employee_tel);

        Button signOut = view.findViewById(R.id.bt_sign_out);
        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = sp.edit();
                editor.remove("eid");
                editor.apply();
                Intent intent = new Intent(getContext(), LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });
        ImageButton changePwd = view.findViewById(R.id.imb_change_pwd);
        changePwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), UpdateEmployeePwdActivity.class);
                startActivity(intent);
            }
        });

        final RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        JSONObjectRequestMapParams getEmployeeInfo = new JSONObjectRequestMapParams(Request.Method.POST, ServerInfo.DISPLAY_EMPLOYEE_INFO_URL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (response.getString("tip").equals("0")) {
                                Bitmap avatar = ImageUtil.convertImage(response.getString("ephoto"));
                                employeeAvatar.setBackground(new BitmapDrawable(avatar));
                                employeeName.setText(response.getString("ename"));
                                employeeGender.setText(response.getString("esex"));
                                employeeID.setText(response.getString("eid"));
                                employeeTel.setText(response.getString("etel"));
                            } else if(response.getString("tip").equals("2")) {
                                Toast.makeText(getContext(), "数据错误", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            Log.e("ERROE", e.getMessage());
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> eidMap = new HashMap<>();
                eidMap.put("eid", sp.getString("eid", null));
                return eidMap;
            }
        };
        requestQueue.add(getEmployeeInfo);

        return view;
    }

}
