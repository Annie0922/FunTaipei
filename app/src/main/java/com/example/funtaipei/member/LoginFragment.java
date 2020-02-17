package com.example.funtaipei.member;


import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.funtaipei.Common;
import com.example.funtaipei.R;
import com.example.funtaipei.task.CommonTask;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.List;


public class LoginFragment extends Fragment {
    private static final String TAG = "TAG_LoginFragment";
    private Activity activity;
    private Button btLogin,btRegister;
    private EditText etEmail,etPassword;
    private List<Member> members;
    private TextView tvMsg;
    private CommonTask memberGetIdTask, loginTask;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        activity.setTitle("會員專區");
        etEmail = view.findViewById(R.id.etEmail);
        etPassword = view.findViewById(R.id.etPassword);
        btLogin = view.findViewById(R.id.btLogin);
        btLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String MB_EMAIL = etEmail.getText().toString();
                String MB_PASSWORD = etPassword.getText().toString();
                String url = Common.URL_SERVER + "/MemberServlet";
                JsonObject jsonObject = new JsonObject();
                //對應server的login
                jsonObject.addProperty("action", "login");
                jsonObject.addProperty("email", MB_EMAIL);
                jsonObject.addProperty("password", MB_PASSWORD);

                loginTask = new CommonTask(url, new Gson().toJson(jsonObject));
                boolean isValid = false;
                try {
                    String inStr = loginTask.execute().get();
                    isValid = Boolean.parseBoolean(inStr);
                } catch (Exception e) {
                    Log.e(TAG, e.toString());
                }
//                JsonObject jsonObject1 = new JsonObject();
                //登入成功
                if(isValid){
                    Common.showToast(getActivity(),R.string.textLoginSuccess);
                    //偏好設定欓
                    SharedPreferences pref = activity.getSharedPreferences(Common.PREFERENCES_MEMBER, Context.MODE_PRIVATE);
                    pref.edit().putString("email",MB_EMAIL).putString("password", MB_PASSWORD).putInt("mb_no",getUserIdByEmail(MB_EMAIL)).apply();
                    Navigation.findNavController(v).popBackStack(R.id.placeListFragment, false);
                } else {
                    Common.showToast(getActivity(),R.string.textLoginFail);
                }
                //帳號空白
//                if (etEmail.length() <= 0 || etPassword.length() <= 0) {
//                    tvMsg.setText("帳號和密碼不可空白");
//                    return;
//                }
                //tvMsg.setText("");
                //Navigation.findNavController(v).navigate((R.id.action_loginFragment_to_listviewFragment));

            }
        });
        btRegister = view.findViewById(R.id.btRegister);
        btRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigate(R.id.action_loginFragment_to_registerFragment);
            }
        });

    }

    private int getUserIdByEmail(String mb_email) {
        int mb_no = 0;
        if(Common.networkConnected(activity)){
            String url = Common.URL_SERVER + "/MemberServlet";
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "getUserIdByEmail");
            jsonObject.addProperty("email", mb_email);
            String jsonOut = jsonObject.toString();
            memberGetIdTask = new CommonTask(url , jsonOut);
            try {
                //傳入string 回傳string轉型 int(id)
                String result = memberGetIdTask.execute().get();
                Log.d(TAG, result);
                mb_no = Integer.parseInt(result);
            }catch (Exception e){
                Log.e(TAG, e.toString());
            }
        }else{
            Common.showToast(activity, R.string.textNoNetwork);
        }
        return mb_no;
    }
}

