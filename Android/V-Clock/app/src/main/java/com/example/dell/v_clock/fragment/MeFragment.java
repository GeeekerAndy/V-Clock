package com.example.dell.v_clock.fragment;


import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.dell.v_clock.MessageDBHelper;
import com.example.dell.v_clock.R;
import com.example.dell.v_clock.ServerInfo;
import com.example.dell.v_clock.VClockContract;
import com.example.dell.v_clock.activity.LoginActivity;
import com.example.dell.v_clock.activity.UpdatePwdActivity;
import com.example.dell.v_clock.service.GetMessageService;
import com.example.dell.v_clock.util.CheckLegality;
import com.example.dell.v_clock.util.GuestListUtil;
import com.example.dell.v_clock.util.ImageUtil;
import com.example.dell.v_clock.util.JSONObjectRequestMapParams;
import com.org.afinal.simplecache.ACache;

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

    static String TAG = "MeFragment";

    boolean isOnEdit = false;
    String eid;
    RequestQueue requestQueue;
    HashMap<String, String> emploeeInfo;
    SharedPreferences sp;
    ImageView employeeAvatar;
    EditText employeeName;
    EditText employeeGender;
    TextView employeeID;
    EditText employeeTel;

    public MeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_me, container, false);
        emploeeInfo = new HashMap<>();
        sp = getContext().getSharedPreferences("loginInfo", MODE_PRIVATE);
        employeeAvatar = view.findViewById(R.id.iv_employee_avatar);
        employeeName = view.findViewById(R.id.tv_employee_name);
        employeeGender = view.findViewById(R.id.tv_employee_gender);
        employeeID = view.findViewById(R.id.tv_employee_id);
        employeeTel = view.findViewById(R.id.tv_employee_tel);
        eid = sp.getString("eid", null);
        final MessageDBHelper dbHelper = new MessageDBHelper(getContext());
        requestQueue = Volley.newRequestQueue(getContext());
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        final Button cancelEdit = view.findViewById(R.id.bt_cancel_edit_employee_info);

        Button signOut = view.findViewById(R.id.bt_sign_out);
        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                AlertDialog dialog = builder.setTitle("确定退出账号？")
                        .setMessage("这将删除所有账号相关数据")
                        .setIcon(R.drawable.ic_warning_black_24dp)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //清空内存数据
                                GuestListUtil.clearList();
                                //删除本地缓存
                                ACache aCache = ACache.get(getContext());
                                aCache.remove(GuestListUtil.ALL_GUEST_JSON_ARRAY_CACHE);
                                aCache.remove(GuestListUtil.MY_GUEST_JSON_ARRAY_CACHE);

                                //删除本地存储eid和历史记录，未读消息的数据库
                                SharedPreferences.Editor editor = sp.edit();
                                editor.remove("eid");
                                editor.apply();
                                SharedPreferences.Editor editor1 = getContext().getSharedPreferences("history", MODE_PRIVATE).edit();
                                editor1.remove("page");
                                editor1.apply();
                                SQLiteDatabase db = dbHelper.getWritableDatabase();
                                db.execSQL("DELETE FROM " + VClockContract.MessageInfo.TABLE_NAME);
                                dbHelper.close();
                                Intent messageService = new Intent(getContext(), GetMessageService.class);
                                getContext().stopService(messageService);
                                NotificationManager manager = (NotificationManager)getContext().getSystemService(Context.NOTIFICATION_SERVICE);
                                manager.cancelAll();

                                Intent intent = new Intent(getContext(), LoginActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Do nothing.
                            }
                        })
                        .create();
                dialog.show();
            }
        });
        ImageButton changePwd = view.findViewById(R.id.imb_change_pwd);
        changePwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), UpdatePwdActivity.class);
                startActivity(intent);
            }
        });
        final Button editEmployeeInfo = view.findViewById(R.id.bt_edit_employee_info);
        if (!isOnEdit) {
            editEmployeeInfo.setText("编辑");
        } else if (isOnEdit) {
            editEmployeeInfo.setText("完成");
        }
        editEmployeeInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isOnEdit) {
                    editEmployeeInfo.setText("完成");
                    employeeName.setEnabled(true);
                    employeeName.requestFocus();
                    InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(employeeName, InputMethodManager.SHOW_IMPLICIT);
                    employeeGender.setEnabled(true);
                    employeeTel.setEnabled(true);
                    cancelEdit.setEnabled(true);
                    cancelEdit.setText("取消");
                    isOnEdit = true;
                } else if (isOnEdit) {
                    if (!CheckLegality.isNameContainSpace(employeeName.getText().toString())) {
                        Toast.makeText(getContext(), "姓名为空或包含空格！", Toast.LENGTH_SHORT).show();
                        employeeName.setEnabled(true);
                        employeeName.requestFocus();
                        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.showSoftInput(employeeName, InputMethodManager.SHOW_IMPLICIT);
                    } else if (!(employeeGender.getText().toString().equals("男") || employeeGender.getText().toString().equals("女"))) {
                        Toast.makeText(getContext(), "性别只能为男或女！", Toast.LENGTH_SHORT).show();
                    } else if (employeeTel.getText().length() < 11) {
                        Toast.makeText(getContext(), "手机号格式错误！", Toast.LENGTH_SHORT).show();
                    } else {
                        editEmployeeInfo.setText("编辑");
                        employeeName.setEnabled(false);
                        employeeGender.setEnabled(false);
                        employeeTel.setEnabled(false);
                        cancelEdit.setEnabled(false);
                        cancelEdit.setText("");
                        emploeeInfo.put("tip", "ename;esex;etel");
                        emploeeInfo.put("ename", employeeName.getText().toString());
                        emploeeInfo.put("esex", employeeGender.getText().toString());
                        emploeeInfo.put("etel", employeeTel.getText().toString());
                        emploeeInfo.put("eid", eid);
                        isOnEdit = false;

                        StringRequest updateEmployeeInfoRequest = new StringRequest(Request.Method.POST, ServerInfo.MODIFY_EMPLOYEE_INFO_URL,
                                new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        if (response.length() > 0 && response.charAt(0) == '0') {
                                            Toast.makeText(getContext(), "更新信息成功！", Toast.LENGTH_SHORT).show();
                                            Log.d("TAG", response);
                                        } else if (response.length() > 0 && response.charAt(0) == '1') {
                                            Toast.makeText(getContext(), "不允许更改！", Toast.LENGTH_SHORT).show();
                                        } else if (response.length() > 0 && response.charAt(0) == '2') {
                                            Toast.makeText(getContext(), "数据错误！", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(getContext(), "发生未知错误！", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                try {
                                    Toast.makeText(getContext(), "服务器错误！", Toast.LENGTH_SHORT).show();
                                } catch (NullPointerException e) {
                                    Log.e(TAG, e.getMessage());
                                }
                            }
                        }) {
                            @Override
                            public Map<String, String> getParams() {
                                return emploeeInfo;
                            }
                        };
                        requestQueue.add(updateEmployeeInfoRequest);
                    }
                }
            }
        });

        cancelEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editEmployeeInfo.setText("编辑");
                employeeName.setEnabled(false);
                employeeGender.setEnabled(false);
                employeeTel.setEnabled(false);
                isOnEdit = false;
                cancelEdit.setEnabled(false);
                cancelEdit.setText("");
                onStart();
            }
        });
        return view;
    }


    @Override
    public void onStart() {
        JSONObjectRequestMapParams getEmployeeInfo = new JSONObjectRequestMapParams(Request.Method.POST, ServerInfo.DISPLAY_EMPLOYEE_INFO_URL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (response.getString("tip").equals("0")) {
                                Bitmap avatar = ImageUtil.convertImage(response.getString("ephoto"));
                                employeeAvatar.setImageBitmap(avatar);
                                employeeName.setText(response.getString("ename"));
                                employeeGender.setText(response.getString("esex"));
                                employeeID.setText(response.getString("eid"));
                                employeeTel.setText(response.getString("etel"));
                            } else if (response.getString("tip").equals("2")) {
                                Toast.makeText(getContext(), "数据错误", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            Log.e("ERROR", e.getMessage());
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
                eidMap.put("eid", eid);
                return eidMap;
            }
        };
        requestQueue.add(getEmployeeInfo);

        super.onStart();
    }

    @Override
    public void onDestroy() {
        isOnEdit = false;
        requestQueue.stop();
        super.onDestroy();
    }

}
