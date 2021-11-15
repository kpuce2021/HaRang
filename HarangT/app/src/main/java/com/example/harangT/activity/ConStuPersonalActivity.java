package com.example.harangT.activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.example.harangT.R;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ConStuPersonalActivity extends AppCompatActivity {
    private static String StudentId;
    private static String s_id;
    private static String type;
    private static String v_id;
    private static String v_name;
    private static String p_id;
    private static String s_name;


    private static int listCount = 0;

    private static List<Entry> eyetrackConcentEntries;
    private static List<Entry> eyetrackEstateEntries;
    private static List<Entry> eyetrackMstateEntries;
    private static HashMap<String,Integer> personalInfo;
    private LineChart lineChart;


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.p_activity_concent_rate);


        //id 받아오기
        p_id = PBaseActivity.p_id;

        Intent intent = getIntent();
        type = intent.getStringExtra("type");
        v_id = intent.getStringExtra("v_id");
        v_name = intent.getStringExtra("v_name");
        StudentId = intent.getStringExtra("StudentId");
        s_id = intent.getStringExtra("s_id");
        s_name = intent.getStringExtra("s_name");



        lineChart = (LineChart)findViewById(R.id.chart);

        accessDB();

    }


    //accdssDB
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void accessDB(){
        Response.Listener<String> responseListener = response -> {
            try {
                JSONObject jsonObject = new JSONObject(response);
                boolean success = jsonObject.getBoolean("success");
                if (success) {

                    if(type.equals("normal")){
                        eyetrackConcentEntries = new ArrayList<>();
                        eyetrackEstateEntries = new ArrayList<>();
                        eyetrackMstateEntries = new ArrayList<>();
                        listCount = Integer.parseInt(jsonObject.getString("personal_eyetrack_count"));

                        personalInfo = new HashMap<>();
                        personalInfo.put("avg_c_total" , jsonObject.getInt("avg_c_total"));
                        personalInfo.put("avg_c_sep" , jsonObject.getInt("avg_c_sep"));
                        personalInfo.put("avg_c_total_count" , jsonObject.getInt("avg_c_total_count"));
                        personalInfo.put("total_enroll_count" , jsonObject.getInt("total_enroll_count"));

                        float cSum = 0f;
                        float eSum = 0f;
                        float mSum = 0f;

                        Log.i("db_test","listCount : "+listCount);
                        for(int i=0;i<listCount;i++){
                            JSONObject output = jsonObject.getJSONObject(String.valueOf(i));

                            cSum += output.getInt("concentration");
                            eSum += output.getInt("e_state");
                            mSum += output.getInt("m_state");

                            eyetrackConcentEntries.add(new Entry(output.getInt("d_time"), cSum/(i+1)));
                            eyetrackEstateEntries.add(new Entry(output.getInt("d_time"), eSum/(i+1)));
                            eyetrackMstateEntries.add(new Entry(output.getInt("d_time"), mSum/(i+1)));

                        }
                    }else if(type.equals("stream") || type.equals("file")){
                        eyetrackConcentEntries = new ArrayList<>();
                        listCount = Integer.parseInt(jsonObject.getString("personal_eyetrack_count"));

                        personalInfo = new HashMap<>();
                        personalInfo.put("avg_c_total" , jsonObject.getInt("avg_c_total"));
                        personalInfo.put("avg_c_total_count" , jsonObject.getInt("avg_c_total_count"));
                        personalInfo.put("total_enroll_count" , jsonObject.getInt("total_enroll_count"));

                        Log.i("db_test","listCount : "+listCount);
                        for(int i=0;i<listCount;i++){
                            JSONObject output = jsonObject.getJSONObject(String.valueOf(i));

                            Log.i("db_test", " e_id : " + output.getInt("e_id"));
                            Log.i("db_test", " outsideStamp : " + output.getInt("outsideStamp"));
                            Log.i("db_test", " ");

                        }
                    }

                    if(listCount == 0){
                        lineChart.setVisibility(View.GONE);
                        LinearLayout linearLayout = findViewById(R.id.concentRate);
                        TextView tv = new TextView(getApplicationContext());
                        tv.setText("아직 강의를 시청하지 않았습니다.");
                        tv.setTextSize(40);
                        tv.setGravity(View.TEXT_ALIGNMENT_CENTER);
                        tv.setTextColor(Color.BLACK);
                        Typeface typeface = getResources().getFont(R.font.font);
                        tv.setTypeface(typeface);

                        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                        lp.gravity = Gravity.CENTER;
                        tv.setLayoutParams(lp);

                        linearLayout.addView(tv);
                    }

                    initUI();
                } else {
                    Log.i("db_test", "server connect fail");
                    return;
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Log.i("db_test", "catch : " + e.getMessage());
                lineChart.setVisibility(View.GONE);
                LinearLayout linearLayout = findViewById(R.id.concentRate);
                TextView tv = new TextView(getApplicationContext());
                tv.setText("아직 강의를 시청하지 않았습니다.");
                tv.setTextSize(70);
                tv.setTextColor(Color.BLACK);
                Typeface typeface = getResources().getFont(R.font.font);
                tv.setTypeface(typeface);

                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                lp.gravity = Gravity.CENTER;
                tv.setLayoutParams(lp);

                linearLayout.addView(tv);
            }

        };

        StudentConcentRateByVideoRequest request = new StudentConcentRateByVideoRequest(type, s_id, v_id, p_id, responseListener);
        request.setShouldCache(false);
        RequestQueue queue = Volley.newRequestQueue(this.getApplicationContext());
        queue.add(request);


    }

    private void initUI() {
        LineDataSet lineCDataSet = new LineDataSet(eyetrackConcentEntries, "집중률");
        lineCDataSet.setLineWidth(3);
        lineCDataSet.setCircleRadius(2);
        lineCDataSet.setCircleColor(Color.parseColor("#F2D5BB"));
        lineCDataSet.setCircleHoleColor(Color.BLUE);
        lineCDataSet.setColor(Color.parseColor("#F2D5BB"));
        lineCDataSet.setDrawCircleHole(true);
        lineCDataSet.setDrawCircles(true);
        lineCDataSet.setDrawHorizontalHighlightIndicator(false);
        lineCDataSet.setDrawHighlightIndicators(false);
        lineCDataSet.setDrawValues(false);


        LineDataSet lineEDataSet = new LineDataSet(eyetrackEstateEntries, "응시율");
        lineEDataSet.setLineWidth(3);
        lineEDataSet.setCircleRadius(2);
        lineEDataSet.setCircleColor(Color.parseColor("#B0BFB5"));
        lineEDataSet.setCircleHoleColor(Color.BLUE);
        lineEDataSet.setColor(Color.parseColor("#B0BFB5"));
        lineEDataSet.setDrawCircleHole(true);
        lineEDataSet.setDrawCircles(true);
        lineEDataSet.setDrawHorizontalHighlightIndicator(false);
        lineEDataSet.setDrawHighlightIndicators(false);
        lineEDataSet.setDrawValues(false);


        LineDataSet lineMDataSet = new LineDataSet(eyetrackMstateEntries, "고정률");
        lineMDataSet.setLineWidth(3);
        lineMDataSet.setCircleRadius(2);
        lineMDataSet.setCircleColor(Color.parseColor("#F28177"));
        lineMDataSet.setCircleHoleColor(Color.BLUE);
        lineMDataSet.setColor(Color.parseColor("#F28177"));
        lineMDataSet.setDrawCircleHole(true);
        lineMDataSet.setDrawCircles(true);
        lineMDataSet.setDrawHorizontalHighlightIndicator(false);
        lineMDataSet.setDrawHighlightIndicators(false);
        lineMDataSet.setDrawValues(false);


        LineData lineData = new LineData();
        lineData.addDataSet(lineCDataSet);
        lineData.addDataSet(lineEDataSet);
        lineData.addDataSet(lineMDataSet);
        lineChart.setData(lineData);

        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextColor(Color.BLACK);
        xAxis.enableGridDashedLine(8, 24, 0);

        YAxis yLAxis = lineChart.getAxisLeft();
        yLAxis.setTextColor(Color.BLACK);

        YAxis yRAxis = lineChart.getAxisRight();
        yRAxis.setDrawLabels(false);
        yRAxis.setDrawAxisLine(false);
        yRAxis.setDrawGridLines(false);

        Description description = new Description();
        description.setText("");

        lineChart.setDoubleTapToZoomEnabled(false);
        lineChart.setDrawGridBackground(false);
        lineChart.setDescription(description);
//        lineChart.animateY(2000, Easing.EasingOption.EaseInCubic);
        lineChart.invalidate();
    }
}
