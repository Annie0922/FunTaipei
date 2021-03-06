package com.example.funtaipei.travelCollection;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.BinderThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.funtaipei.Common;
import com.example.funtaipei.R;
import com.example.funtaipei.task.CommonTask;
import com.example.funtaipei.task.ImageTask;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.List;

import static androidx.constraintlayout.widget.Constraints.TAG;



public class TravelCollectionFragment extends Fragment {
    private Activity activity;
    private RecyclerView travelCollectionRecycleView;
    private ImageTask travelCollectionImageTask;
    private CommonTask travelCollectionGetAllTask;
    private CommonTask travelCollectionDeleteTask;
    private ImageView travelColleationImage, travelCollection_Memberimageview;
    private TextView travelCollection_MemberName, travelCollection_MemberEmail, travelCollection_MemberId;
    private List<TravelCollection> travelCollections;


    private FloatingActionButton travelCollectionbtnAdd;

    public static TravelCollectionFragment newInstance(){
        return new TravelCollectionFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View v = inflater.inflate(R.layout.fragment_travel_collection, container, false);
        return v;
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);





        travelCollectionbtnAdd = view.findViewById(R.id.travelCollectionbtnAdd);
        travelCollectionbtnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigate(R.id.travel_collection_insert);
            }
        });



        //TravelCollection資料
        travelCollectionRecycleView = view.findViewById(R.id.travelCollectionRecycleview);
        travelCollectionRecycleView.setLayoutManager(new LinearLayoutManager(activity));
        travelCollections = getTravelCollections();
        showTravelCollections(travelCollections);


    }

    //--------------------------------以下是TravelCollection資料-------------------------------------------------------------


    private List<TravelCollection> getTravelCollections() {
        List<TravelCollection> travelCollections = null;
        if (Common.networkConnected(activity)) {
            String url = Common.URL_SERVER + "TravelCollectionServlet";
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "getAll");
            String jsonOut = jsonObject.toString();
            travelCollectionGetAllTask = new CommonTask(url, jsonOut);
            try {
                String jsonIn = travelCollectionGetAllTask.execute().get();
                Type listType = new TypeToken<List<TravelCollection>>() {
                }.getType();
                travelCollections = new Gson().fromJson(jsonIn, listType);
            } catch (Exception e) {
                Log.d(TAG, "getTravelCollections: ");
            }
        } else {
            Common.showToast(activity, R.string.textNoNetwork);
        }
        return travelCollections;
    }

    private void showTravelCollections(List<TravelCollection> travelCollections) {
        if (travelCollections == null || travelCollections.isEmpty()) {
            Common.showToast(activity, "No TravelCollections Found");
            return;
        }
        TravelCollectionAdapter travelCollectionAdapter = (TravelCollectionAdapter) travelCollectionRecycleView.getAdapter();

        if (travelCollectionAdapter == null) {
            travelCollectionRecycleView.setAdapter(new TravelCollectionAdapter(activity, travelCollections));
        } else {
            travelCollectionAdapter.setTravelCollections(travelCollections);
            travelCollectionAdapter.notifyDataSetChanged();
        }
    }

    private class TravelCollectionAdapter extends RecyclerView.Adapter<TravelCollectionAdapter.MyViewHolder> {

        private LayoutInflater layoutInflater;
        private List<TravelCollection> travelCollections;
        private int imageSize;

        TravelCollectionAdapter(Context context, List<TravelCollection> travelCollections) {
            layoutInflater = LayoutInflater.from(context);
            this.travelCollections = travelCollections;
            //螢幕寬度 / 3 當圖片尺寸
            imageSize = getResources().getDisplayMetrics().widthPixels / 3;
        }

        void setTravelCollections(List<TravelCollection> travelCollections) {
            this.travelCollections = travelCollections;
        }


        @Override
        public int getItemCount() {
            return travelCollections.size();
        }

        class MyViewHolder extends RecyclerView.ViewHolder {
            ImageView imageView;
            TextView travelName;

            MyViewHolder(View itemView) {
                super(itemView);
                travelColleationImage = itemView.findViewById(R.id.imageView);
                travelName = itemView.findViewById(R.id.travelName);
            }

        }


        @NonNull
        @Override
        public TravelCollectionAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = layoutInflater.inflate(R.layout.travelcollection_item, parent, false);
            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull TravelCollectionAdapter.MyViewHolder holder, int position) {
            final TravelCollection travelCollection = travelCollections.get(position);
            String url = Common.URL_SERVER + "TravelCollectionServlet";
            int id = travelCollection.getMb_no();
            travelCollectionImageTask = new ImageTask(url, id, imageSize, holder.imageView);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            travelCollectionImageTask.execute();
            holder.travelName.setText(travelCollection.getGp_name());
//            holder.Group_ID.setText(String.valueOf(travelCollection.getGp_id()));
//            holder.Group_Name.setText(travelCollection.getGp_name());
//            holder.gp_datestart.setText(simpleDateFormat.format(travelCollection.getGP_DATESTART()));
//            holder.gp_dateend.setText(simpleDateFormat.format(travelCollection.getGP_DATEEND()));
//            holder.gp_eventstart.setText(simpleDateFormat.format(travelCollection.getGP_EVENTDATE()));
            //增加行程按鈕監聽
//            holder.itemView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//
//                }
//            });
            //增加按鈕常按事件
        }

    }



    @Override
    public void onStop() {
        super.onStop();
        if (travelCollectionGetAllTask != null) {
            travelCollectionGetAllTask.cancel(true);
            travelCollectionGetAllTask = null;
        }
        if (travelCollectionImageTask != null) {
            travelCollectionImageTask.cancel(true);
            travelCollectionImageTask = null;
        }
        if (travelCollectionDeleteTask != null) {
            travelCollectionDeleteTask.cancel(true);
            travelCollectionDeleteTask = null;
        }
    }
}


