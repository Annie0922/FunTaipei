package com.example.funtaipei.member;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.navigation.Navigation;

import com.example.funtaipei.Common;
import com.example.funtaipei.R;
import com.example.funtaipei.task.CommonTask;
import com.example.funtaipei.task.ImageTask;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import java.text.SimpleDateFormat;

import static androidx.constraintlayout.widget.Constraints.TAG;


public class MemberDetailFragment extends Fragment {
    private TextView tvEmail, tvMember, tvGender, tvBday;
    private ImageView ivPeopleimage;
    private Button btUpdate;
    private FragmentActivity activity;
    private int mb_no;
    private Member member;
    private CommonTask getMemberTask;
    private SharedPreferences pref;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
        member = (Member)(getArguments() != null? getArguments().getSerializable("member") : null);
    }

    public MemberDetailFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //return super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.fragment_member_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        pref = activity.getSharedPreferences(Common.PREFERENCES_MEMBER, Context.MODE_PRIVATE);
        mb_no = pref.getInt("mb_no", 0);
        tvEmail = view.findViewById(R.id.tvEmail);
        tvMember = view.findViewById(R.id.tvMember);
        tvGender = view.findViewById(R.id.tvGender);
        tvBday = view.findViewById(R.id.tvBday);
        ivPeopleimage = view.findViewById(R.id.ivpeopleimage);
        btUpdate = view.findViewById(R.id.btUpdate);
        btUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("member", member);
                Navigation.findNavController(v).navigate(R.id.action_memberDetailFragment_to_updateMemberFragment, bundle);
            }
        });
        showMember();
    }

    private void showMember() {
        String url = Common.URL_SERVER + "/MemberServlet";
        int mb_no = member.getMb_no();
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("action", "findById");
        jsonObject.addProperty("id", mb_no);
        //int id = mb_no;
        getMemberTask = new CommonTask(url, jsonObject.toString());
        int imageSize = getResources().getDisplayMetrics().widthPixels / 3;
        Bitmap bitmap = null;
        try {
            String memberJson = getMemberTask.execute().get();
            Gson gson = new GsonBuilder().setDateFormat("yyyy/MM/dd").create();
            member = gson.fromJson(memberJson, Member.class);
            bitmap = new ImageTask(url, mb_no, imageSize).execute().get();
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
        if (bitmap != null) {
            ivPeopleimage.setImageBitmap(bitmap);
        } else {
            ivPeopleimage.setImageResource(R.drawable.no_image);
        }
        tvEmail.setText(member.getMb_email());
        tvMember.setText(member.getMb_name());
        tvGender.setText(member.getMb_gender().equals("M") ? getString(R.string.textMale) : getString(R.string.textFemale));
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd");
        tvBday.setText(simpleDateFormat.format(member.getMb_birthday()));

    }
}


