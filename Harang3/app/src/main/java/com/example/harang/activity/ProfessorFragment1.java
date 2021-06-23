package com.example.harang.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.example.harang.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


public class ProfessorFragment1 extends Fragment {


    private EditText textfield;
    private EditText title;
    private EditText v_concent1_start;
    private EditText v_concent1_stop;
    private EditText v_concent2_start;
    private EditText v_concent2_stop;
    private View view;
    private Button btnselectVideo, btn_upload;
    //private ImageView imageView;
    public static ProfessorFragment1 newInstance(){return new ProfessorFragment1();}

    public static String p_id;

    Date currentTime = Calendar.getInstance().getTime();
    String date_text = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(currentTime);

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.p_fragment1, container, false);
        p_id = p_BaseActivity.p_id;
        Log.d("ViewCreate",p_id);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        initUI();

    }

    private void initUI() {

        //Bitmap bitmap = ThumbnailUtils.createVideoThumbnail(path, MediaStore.Video.Thumbnails.FULL_SCREEN_KIND);




        btnselectVideo = view.findViewById(R.id.btnselectVideo);
        //imageView = view.findViewById(R.id.uploadThumbnail);
        title = view.findViewById(R.id.title);
        textfield = view.findViewById(R.id.textfield);
        v_concent1_start = view.findViewById(R.id.v_concent1_start);
        v_concent1_stop = view.findViewById(R.id.v_concent1_stop);
        v_concent2_start = view.findViewById(R.id.v_concent2_start);
        v_concent2_stop = view.findViewById(R.id.v_concent2_stop);

        btn_upload = view.findViewById(R.id.btn_upload);



        btnselectVideo.setOnClickListener(view -> startActivity(new Intent(getContext(), UploadActivity.class)));
        //imageView.setImageBitmap(bitmap);
        btn_upload.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                upload_video(p_id);
            }
        });
  /*      btn_upload.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                upload_video(p_id);
            }
        });*/

    }

    public void upload_video(String p_id){


       // String V_id = title.getText().toString();

        String V_name = title.getText().toString();
        //String V_time = ((VideoActivity)VideoActivity.context_main).timeInmillisec;
        String V_textfield = textfield.getText().toString();
        String V_Calendar = date_text;
        String V_concent1_start = v_concent1_start.getText().toString();
        String V_concent1_stop = v_concent1_stop.getText().toString();
        String V_concent2_start = v_concent2_start.getText().toString();
        String V_concent2_stop = v_concent2_stop.getText().toString();

        Log.d("dbTest",p_id);
        Log.d("dbTest",V_name);
        Log.d("dbTest",V_textfield);
        Log.d("dbTest",V_Calendar);
        Log.d("dbTest",V_concent1_start);
        Log.d("dbTest",V_concent1_stop);
        Log.d("dbTest",V_concent2_start);
        Log.d("dbTest",V_concent2_stop);



        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    boolean success = jsonObject.getBoolean("success");
                    if (success) {
                        Toast.makeText(getActivity(), "영상 등록 성공", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getActivity(), "영상 등록에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        };
        // 서버로 Volley를 이용해서 요청을 함.
        /*ProfessorFragmentRequest professorFragmentRequest = new ProfessorFragmentRequest(V_id, P_id, V_name, V_textfield, V_Calendar, V_concent1_start, V_concent1_stop, V_concent2_start, V_concent2_stop, responseListener);*/
        ProfessorFragmentRequest professorFragmentRequest = new ProfessorFragmentRequest(p_id, V_name, V_textfield, V_Calendar, V_concent1_start, V_concent1_stop, V_concent2_start, V_concent2_stop, responseListener);
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        queue.add(professorFragmentRequest);

    }

}