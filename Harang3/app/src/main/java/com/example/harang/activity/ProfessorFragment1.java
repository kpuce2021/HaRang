package com.example.harang.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
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
import java.util.regex.Pattern;


public class ProfessorFragment1 extends Fragment {


    private EditText textfield;
    private EditText title;
    private EditText v_concent1_start;
    private EditText v_concent1_stop;
    private EditText v_concent2_start;
    private EditText v_concent2_stop;
    private ImageButton btn_concent1;
    private ImageButton btn_concent2;
    private View view;
    private Button btnselectVideo, btn_upload;
    private TextView concentFlag;
    private LinearLayout layoutConcent1, layoutConcent2;
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
        btn_concent1 = view.findViewById(R.id.btn_concent1);
        btn_concent2 = view.findViewById(R.id.btn_concent2);
        concentFlag = view.findViewById(R.id.concent_flag);
        layoutConcent1 = view.findViewById(R.id.layout_concent1);
        layoutConcent2 = view.findViewById(R.id.layout_concent2);


        btn_upload = view.findViewById(R.id.btn_upload);




        btnselectVideo.setOnClickListener(view -> startActivity(new Intent(getContext(), UploadActivity.class)));
        //imageView.setImageBitmap(bitmap);
        btn_upload.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                upload_video(p_id);
            }
        });

        btn_concent1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(concentFlag.getText().toString().equals("0")){ //둘다 보임 상태 --> 2번째만 보이게
                    layoutConcent1.setVisibility(View.GONE);
                    btn_concent1.setImageDrawable(getResources().getDrawable(R.drawable.plus));
                    concentFlag.setText("2");

                }else if(concentFlag.getText().toString().equals("1")){ //첫번째만 보임 상태 --> 둘다 안보이게
                    layoutConcent1.setVisibility(View.GONE);
                    btn_concent1.setImageDrawable(getResources().getDrawable(R.drawable.plus));
                    concentFlag.setText("3");

                }else if(concentFlag.getText().toString().equals("2")){ //두번째만 보임 상태 --> 둘다 보이게
                    layoutConcent1.setVisibility(View.VISIBLE);
                    btn_concent1.setImageDrawable(getResources().getDrawable(R.drawable.minus));
                    concentFlag.setText("0");

                }else if(concentFlag.getText().toString().equals("3")){ //둘다 안보임 상태 --> 1번째만 보이게
                    layoutConcent1.setVisibility(View.VISIBLE);
                    btn_concent1.setImageDrawable(getResources().getDrawable(R.drawable.minus));
                    concentFlag.setText("1");

                }
            }
        });


        btn_concent2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(concentFlag.getText().toString().equals("0")){ //둘다 보임 상태 --> 1번째만 보이게
                    layoutConcent2.setVisibility(View.GONE);
                    btn_concent2.setImageDrawable(getResources().getDrawable(R.drawable.plus));
                    concentFlag.setText("1");

                }else if(concentFlag.getText().toString().equals("1")){ //첫번째만 보임 상태 --> 둘다 보이게
                    layoutConcent2.setVisibility(View.VISIBLE);
                    btn_concent2.setImageDrawable(getResources().getDrawable(R.drawable.minus));
                    concentFlag.setText("0");

                }else if(concentFlag.getText().toString().equals("2")){ //두번째만 보임 상태 --> 둘다 안보이게
                    layoutConcent2.setVisibility(View.GONE);
                    btn_concent2.setImageDrawable(getResources().getDrawable(R.drawable.plus));
                    concentFlag.setText("3");

                }else if(concentFlag.getText().toString().equals("3")){ //둘다 안보임 상태 --> 2번째만 보이게
                    layoutConcent2.setVisibility(View.VISIBLE);
                    btn_concent2.setImageDrawable(getResources().getDrawable(R.drawable.minus));
                    concentFlag.setText("2");

                }
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
        String V_textfield = textfield.getText().toString();
        String V_Calendar = date_text;



        String V_concent1_start = v_concent1_start.getText().toString();
        String V_concent1_stop = v_concent1_stop.getText().toString();
        String V_concent2_start = v_concent2_start.getText().toString();
        String V_concent2_stop = v_concent2_stop.getText().toString();


        //예외처리
        /*
        1. 강의 이름 빈칸일때
        2. 강의 설명 빈칸일때
        3. 강의 선택(s3 업로드) 안했을 때
        4. 집중구간 시간설정 잘못됬을 때
        4-0. 숫자가 아닌걸 입력했을 때
        4-1. 뒤쪽 숫자가 더 클때
        4-2. 집중구간 길이가 30초 미만알때
         */

        //4-0
        if(Pattern.matches("^[0-9][0-9]:[0-5][0-9]:[0-5][0-9]$",V_concent1_start) && Pattern.matches("^[0-9][0-9]:[0-5][0-9]:[0-5][0-9]$",V_concent1_stop)){
            //형식이 00:00:00인지 체크
            //4-1,2. 뒤쪽 숫자가 더 클때 & 집중구간이 30초 미만일 때
           /* if(Integer.parseInt(V_concent1_start.substring(0,2))*3600 + Integer.parseInt(V_concent1_start.substring(3,5))*60 + Integer.parseInt(V_concent1_start.substring(6,8)) -
                    Integer.parseInt(V_concent1_stop.substring(0,2))*3600 + Integer.parseInt(V_concent1_stop.substring(3,5))*60 + Integer.parseInt(V_concent1_stop.substring(6,8))<30){
                Toast.makeText(getContext(),"집중구간을 20초 이상으로 설정해주세요",Toast.LENGTH_SHORT).show();
                return;
            }*/
            //성공
        }else{
            //형식은 틀렸지만 -1인지 체크
            //입력버튼 클릭 or 빈칸으로 제출
            if( (concentFlag.getText().toString().equals("2") || concentFlag.getText().toString().equals("3")) || (V_concent1_start.equals("") ||V_concent1_start.equals("-1")) || (V_concent1_stop.equals("") ||V_concent1_stop.equals("-1")) ){
                V_concent1_start = "-1";
                V_concent1_stop = "-1";
            }
        }

        if(Pattern.matches("^[0-9][0-9]:[0-5][0-9]:[0-5][0-9]$",V_concent2_start) && Pattern.matches("^[0-9][0-9]:[0-5][0-9]:[0-5][0-9]$",V_concent2_stop)){
            //형식이 00:00:00인지 체크
            //4-1,2. 뒤쪽 숫자가 더 클때 & 집중구간이 30초 미만일 때
            if(Integer.parseInt(V_concent2_start.substring(0,2))*3600 + Integer.parseInt(V_concent2_start.substring(3,5))*60 + Integer.parseInt(V_concent2_start.substring(6,8)) -
                    Integer.parseInt(V_concent2_stop.substring(0,2))*3600 + Integer.parseInt(V_concent2_stop.substring(3,5))*60 + Integer.parseInt(V_concent2_stop.substring(6,8))<30){
                Toast.makeText(getContext(),"집중구간을 20초 이상으로 설정해주세요",Toast.LENGTH_SHORT).show();
                return;
            }
            //성공
        }else{
            //형식은 틀렸지만 -1인지 체크
            //입력버튼 클릭 or 빈칸으로 제출
            if ((concentFlag.getText().toString().equals("1") || concentFlag.getText().toString().equals("3")) || (V_concent2_start.equals("") || V_concent2_start.equals("-1")) || (V_concent2_stop.equals("") || V_concent2_stop.equals("-1"))) {
                V_concent2_start = "-1";
                V_concent2_stop = "-1";
            }
        }



        if(title.getText().toString().equals("")){ //1. 강의 이름 빈칸일때
            Toast.makeText(getContext(),"강의 제목을 입력해주세요",Toast.LENGTH_SHORT).show();
            return;
        }else if(textfield.getText().toString().equals("")){ //2. 강의 설명 빈칸일때
            Toast.makeText(getContext(),"강의 설명을 입력해주세요",Toast.LENGTH_SHORT).show();
            return;
        }




        //입력하지 않았을 경우

        Log.d("dbTest",p_id);
        Log.d("dbTest",V_name);
        Log.d("dbTest",V_textfield);
        Log.d("dbTest",V_Calendar);
        Log.d("dbTest","1start : "+V_concent1_start);
        Log.d("dbTest","1stop : "+V_concent1_stop);
        Log.d("dbTest","2start : "+V_concent2_start);
        Log.d("dbTest","2stop : "+V_concent2_stop);



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