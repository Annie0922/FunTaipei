package com.example.funtaipei.member;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.os.Environment;
import android.provider.MediaStore;
import android.text.InputType;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.funtaipei.Common;
import com.example.funtaipei.R;
import com.example.funtaipei.task.CommonTask;
import com.example.funtaipei.task.ImageTask;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static android.app.Activity.RESULT_OK;


public class UpdateMemberFragment extends Fragment {
    private static final String TAG = "TAG_UpdateMFragment";
    private Activity activity;
    private ImageView ivMember;
    private Member member;
    private TextView tvEmail;
    private EditText etName, etBdy;
    private byte[] image;
    private RadioGroup rgGender;
    private static final int REQ_TAKE_PICTURE = 0;
    private static final int REQ_PICK_IMAGE = 1;
    private static final int REQ_CROP_PICTURE = 2;
    private Uri contentUri, croppedImageUri;
    private SimpleDateFormat simpleDateFormat;
    private Date Date1;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.fragment_update_member, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ivMember = view.findViewById(R.id.ivMember);
        tvEmail = view.findViewById(R.id.tvEmail);
        etName = view.findViewById(R.id.etName);
        rgGender = view.findViewById(R.id.rgGender);
        rgGender.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton radioButton = group.findViewById(checkedId);
            }
        });

        etBdy = view.findViewById(R.id.etBdy);
        simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd");
        etBdy.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Log.d(TAG, String.valueOf(event.getAction()));
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    int inType = etBdy.getInputType();
                    etBdy.setInputType(InputType.TYPE_NULL);
                    etBdy.onTouchEvent(event);
                    etBdy.setInputType(inType);
                    Calendar calendar = Calendar.getInstance();
                    DatePickerDialog datePickerDialog = new DatePickerDialog(activity, new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                            Date1 = new Date(year - 1900, month, dayOfMonth);
                            etBdy.setText(simpleDateFormat.format(Date1));
                            //btDatePicker2.setEnabled(true);
                        }
                    },
                            calendar.getTime().getYear() + 1900, calendar.getTime().getMonth(), calendar.getTime().getDate());
                    datePickerDialog.show();
                }
                return true;
            }
        });
//        etBdy.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Calendar calendar = Calendar.getInstance();
//                DatePickerDialog datePickerDialog = new DatePickerDialog(activity, new DatePickerDialog.OnDateSetListener() {
//                    @Override
//                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
//                        Date1 = new Date(year - 1900, month, dayOfMonth);
//                        etBdy.setText(simpleDateFormat.format(Date1));
//                        //btDatePicker2.setEnabled(true);
//                    }
//                },
//                        calendar.getTime().getYear() + 1900, calendar.getTime().getMonth(), calendar.getTime().getDate());
//                datePickerDialog.show();
//            }
//        });
        final NavController navController = Navigation.findNavController(view);
        Bundle bundle = getArguments();
        if (bundle == null && bundle.getSerializable("member") == null) {
            //Common.showToast(activity, R.string.textNoMembersFound);
            navController.popBackStack();
            return;

        }
        member = (Member) bundle.getSerializable("member");
        showMember();

        Button btTakePicture = view.findViewById(R.id.btTakePicture);
        btTakePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                File file = activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
                file = new File(file,"picture.jpg");
                contentUri = FileProvider.getUriForFile(activity,activity.getPackageName() + ".provider", file);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, contentUri);
                if (intent.resolveActivity(activity.getPackageManager()) != null){
                    startActivityForResult(intent, REQ_TAKE_PICTURE);
                }else{
                    Common.showToast(activity,R.string.textNoCameraApp);
                }
            }
        });

        Button btPickPicture = view.findViewById(R.id.btPickPicture);
        btPickPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent,REQ_PICK_IMAGE);
            }
        });

        Button btFinishUpdate = view.findViewById(R.id.btFinishUpdate);
        btFinishUpdate.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("LongLogTag")
            @Override
            public void onClick(View v) {
                String MB_NAME = etName.getText().toString();
                if (MB_NAME.length() <= 0) {
                    Common.showToast(activity, R.string.textNameIsInvalid);
                    return;
                }
                String MB_GENDER = rgGender.getCheckedRadioButtonId() == R.id.rbMale ? "M" : "F";
                if (MB_GENDER.length() <= 0) {
                    Common.showToast(activity, R.string.textGenderIsInvalid);
                    return;
                }
                Date MB_BIRTHDAY = Date1;
                if (Common.networkConnected(activity)){
                    String url = Common.URL_SERVER + "/MemberServlet";
                    member.setFields(MB_NAME,MB_GENDER,MB_BIRTHDAY);
//                    Log.d(TAG, simpleDateFormat.format(member.getMb_birthday()));
                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("action","memberUpdate");
                    jsonObject.addProperty("member",Common.GSON.toJson(member));

                    if (image != null){
                        jsonObject.addProperty("imageBase64", Base64.encodeToString(image,Base64.DEFAULT));
                    }
                    int count = 0;
                    try {
                        String result = new CommonTask(url, jsonObject.toString()).execute().get();
                        count = Integer.valueOf(result);
                    }catch(Exception e){
                        Log.e(TAG, e.toString());
                    }
                    if (count == 0){
                        Common.showToast(activity, R.string.textUpdateFail);
                    }else{
                        Common.showToast(activity,R.string.textUpdateSuccess);
                    }
                }else{
                    Common.showToast(activity,R.string.textNoNetwork);
                }
                navController.popBackStack();
            }
        });
        Button btCancel = view.findViewById(R.id.btCancel);
        btCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController.popBackStack();
            }
        });

    }

    @SuppressLint("LongLogTag")
    private void showMember() {
        String url = Common.URL_SERVER + "/MemberServlet";
        int mb_no = member.getMb_no();
        int imageSize = getResources().getDisplayMetrics().widthPixels / 3;
        Bitmap bitmap = null;
        try {
            bitmap = new ImageTask(url, mb_no, imageSize).execute().get();
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }if (bitmap != null){
            ivMember.setImageBitmap(bitmap);
        } else {
            ivMember.setImageResource(R.drawable.no_image);
        }
        tvEmail.setText(member.getMb_email());
        etName.setText(member.getMb_name());
        Date1 = member.getMb_birthday();
        etBdy.setText(simpleDateFormat.format(Date1));
        rgGender.check(member.getMb_gender().equals("M") ? R.id.rbMale : R.id.rbFemale);
    }
    @SuppressLint("LongLogTag")
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQ_TAKE_PICTURE:
                    crop(contentUri);
                    break;
                case REQ_PICK_IMAGE:
                    Uri uri = intent.getData();
                    crop(uri);
                    break;
                case REQ_CROP_PICTURE:
                    Log.d(TAG, "REQ_CROP_PICTURE: " + croppedImageUri.toString());
                    try {
                        Bitmap picture = BitmapFactory.decodeStream(
                                activity.getContentResolver().openInputStream(croppedImageUri));
                        ivMember.setImageBitmap(picture);
                        ByteArrayOutputStream out = new ByteArrayOutputStream();
                        picture.compress(Bitmap.CompressFormat.JPEG, 100, out);
                        image = out.toByteArray();
                    } catch (FileNotFoundException e) {
                        Log.e(TAG, e.toString());
                    }
                    break;
            }
        }
    }

    private void crop(Uri sourceImageUri) {
        File file = activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        file = new File(file, "picture_cropped.jpg");
        croppedImageUri = Uri.fromFile(file);
        // take care of exceptions
        try {
            // call the standard crop action intent (the user device may not support it)
            Intent cropIntent = new Intent("com.android.camera.action.CROP");
            // the recipient of this Intent can read soruceImageUri's data
            cropIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            // set image source Uri and type
            cropIntent.setDataAndType(sourceImageUri, "image/*");
            // send crop message
            cropIntent.putExtra("crop", "true");
            // aspect ratio of the cropped area, 0 means user define
            cropIntent.putExtra("aspectX", 0); // this sets the max width
            cropIntent.putExtra("aspectY", 0); // this sets the max height
            // output with and height, 0 keeps original size
            cropIntent.putExtra("outputX", 0);
            cropIntent.putExtra("outputY", 0);
            // whether keep original aspect ratio
            cropIntent.putExtra("scale", true);
            cropIntent.putExtra(MediaStore.EXTRA_OUTPUT, croppedImageUri);
            // whether return data by the intent
            cropIntent.putExtra("return-data", true);
            // start the activity - we handle returning in onActivityResult
            startActivityForResult(cropIntent, REQ_CROP_PICTURE);
        }
        // respond to users whose devices do not support the crop action
        catch (ActivityNotFoundException anfe) {
            Common.showToast(activity, "This device doesn't support the crop action!");
        }
    }

}
