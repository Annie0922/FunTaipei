package com.example.funtaipei.group;




import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.funtaipei.Common;
import com.example.funtaipei.R;

import com.example.funtaipei.task.ImageTask;

import java.text.SimpleDateFormat;


public class GroupDetailFragment extends Fragment {
    private FragmentActivity activity;
    private final static String TAG = "TAG_GroupDetailFragment";
    private ImageView imageView;
    private TextView tvName, tvDate, tvGroupNo, tvPeople;
    private Button btJoin;
    private Group group;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_group_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        activity.setTitle("旅遊團體內容");
        imageView = view.findViewById(R.id.imageView);
        tvName = view.findViewById(R.id.tvName);
        tvDate = view.findViewById(R.id.edDate);
        tvGroupNo = view.findViewById(R.id.tvGroupNo);
        tvPeople = view.findViewById(R.id.tvPeople);

        btJoin = view.findViewById(R.id.btJoin);
        btJoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        final NavController navController = Navigation.findNavController(view);
        Bundle bundle = getArguments();
        if(bundle == null || bundle.getSerializable("group") == null){
            Common.showToast(activity,R.string.textNoGroupsFound);
            navController.popBackStack();
            return;
        }
        group = (Group) bundle.getSerializable("group");
        showGroup();

    }

    private void showGroup() {
        String url = Common.URL_SERVER + "GroupServlet";
        int id = group.getGP_ID();
        int imageSize = getResources().getDisplayMetrics().widthPixels/2;
        Bitmap bitmap = null;
        try{
            bitmap = new ImageTask(url, id, imageSize).execute().get();
        }catch (Exception e){
            Log.e(TAG, e.toString());
        }
        if (bitmap != null){
            imageView.setImageBitmap(bitmap);
        }else{
            imageView.setImageResource(R.drawable.no_image);
        }
        tvName.setText(group.getGP_NAME());
        tvGroupNo.setText("團體編號：" + String.valueOf(group.getGP_ID()));
        tvDate.setText("活動日期：" + new SimpleDateFormat("yyyy/MM/dd").format(group.getGP_EVENTDATE())+ new SimpleDateFormat("（E）").format(group.getGP_EVENTDATE()));
        tvPeople.setText("可報 " + String.valueOf(group.getGP_UPPER()-group.getGP_ENROLLMENT()) + " 團位 " + String.valueOf(group.getGP_UPPER()));
    }
}
