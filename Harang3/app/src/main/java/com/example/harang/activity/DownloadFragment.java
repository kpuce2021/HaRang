package com.example.harang.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.content.Intent;
import android.webkit.DownloadListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.ListFragment;

import com.amazonaws.mobile.config.AWSConfiguration;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferType;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.example.harang.R;

import java.io.File;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;


public class DownloadFragment extends ListFragment {
    // 각각의 Fragment마다 Instance를 반환해 줄 메소드를 생성합니다.
    public static DownloadFragment newInstance() {
        return new DownloadFragment();
    }

    private static final String TAG = "db_test";
    private static final int INDEX_NOT_CHECKED = -1;
    //check video list
    private static AmazonS3Client s3;
    private static ArrayList<HashMap<String, Object>> transferRecordMaps;
    private static Util util;
    private static String bucket;
    //context 정의
    private static Context mContext;
    private static Bundle bundle;
    private static String VideoName;
    private static String studentId;
    private static String s_id;
    private static String v_id;

    //Download Video
    private static View view;
    private static SimpleAdapter simpleAdapter;
    private static TransferUtility transferUtility;
    private static List<TransferObserver> observers;
    private static ArrayList<HashMap<String, Object>> downloadMaps;

    @Override
    public void onAttach(@NonNull Context context) {
        mContext = context;
        super.onAttach(context);
    }

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.stu_fragment_download, container, false);

        //get List
        util = new Util();
        bucket = new AWSConfiguration(mContext).optJsonObject("S3TransferUtility").optString("Bucket");
        s3 = util.getS3Client(mContext);
        transferRecordMaps = new ArrayList<>();
        bundle = getArguments();  //번들 받기. getArguments() 메소드로 받음.
        VideoName = bundle.getString("videoName");
        studentId = bundle.getString("studentId");
        s_id = bundle.getString("s_id");
        v_id = bundle.getString("v_id");



        //Download
        downloadMaps = new ArrayList<>();
        transferUtility = util.getTransferUtility(mContext);
        initUI();


        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }


    @Override
    public void onResume() {
        super.onResume();
        //getList & check Bundle
        new GetFileListTask().execute();

        initData();
        //beginDownload(VideoName+".mp4");
    }


    @Override
    public void onPause() {
        super.onPause();
        /*
        if (observers != null && !observers.isEmpty()) {
            for (TransferObserver observer : observers) {
                observer.cleanTransferListener();
            }
        }
        */
    }



    private void checkBundleInList(){
        if(bundle != null){
            //Name 받기.
            boolean temp = false;

            for(int i=0;i<transferRecordMaps.size();i++){
                if((VideoName+".mp4").equals((String)transferRecordMaps.get(i).get("key"))){
                    temp = true;
                }
            }
            if(temp){//해당 영상이 있을 경우
                Log.i(TAG,VideoName+".mp4 있음");
                beginDownload(VideoName+".mp4");

            }else{
                Log.i(TAG,VideoName+".mp4 XXXXX");
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.main_frame, StudentFragment1.newInstance())
                        .addToBackStack(null)
                        .commit();
                onDestroy();
                Log.i(TAG,"영상이 존재하지 않습니다.");
            }
        }
    }

    private void initUI(){
        simpleAdapter = new SimpleAdapter(mContext, downloadMaps,
                R.layout.record_item, new String[] {
                "checked", "fileName", "progress", "bytes", "state", "percentage"
        },
                new int[] {
                        R.id.radioButton1, R.id.textFileName, R.id.progressBar1, R.id.textBytes,
                        R.id.textState, R.id.textPercentage
                });
        simpleAdapter.setViewBinder(new SimpleAdapter.ViewBinder() {
            @Override
            public boolean setViewValue(View view, Object data, String textRepresentation) {
                if(view.getId() == R.id.radioButton1){
                    RadioButton radio = (RadioButton) view;
                    radio.setChecked((Boolean) data);
                    return true;
                }
                if(view.getId() == R.id.textFileName){}
                if(view.getId() == R.id.progressBar1){
                    ProgressBar progress = (ProgressBar) view;
                    progress.setProgress((Integer) data);
                    return true;
                }
                if(view.getId() == R.id.textBytes){}
                if(view.getId() == R.id.textState){}
                if(view.getId() == R.id.textPercentage){
                    TextView text = (TextView) view;
                    text.setText(data.toString());
                    return true;
                }
                return false;
            }
        });
        setListAdapter(simpleAdapter);

        Button buttonOK = (Button) view.findViewById(R.id.buttonOK);
        buttonOK.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, VideoActivity.class);
                intent.putExtra("VideoName", VideoName+".mp4");
                intent.putExtra("studentId", studentId);
                intent.putExtra("s_id", s_id);
                intent.putExtra("v_id", v_id);
                startActivity(intent);
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                fragmentManager.beginTransaction().remove(DownloadFragment.this).commit();


            }
        });
    }
    private class GetFileListTask extends AsyncTask<Void, Void, Void> {
        private ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            dialog = ProgressDialog.show(mContext, getString(R.string.refreshing), getString(R.string.please_wait));
        }

        @Override
        protected Void doInBackground(Void... inputs) {
            // Queries files in the bucket from S3.
            List<S3ObjectSummary> s3ObjList = s3.listObjects(bucket).getObjectSummaries();
            transferRecordMaps.clear();
            for (S3ObjectSummary summary : s3ObjList) {
                HashMap<String, Object> map = new HashMap<>();
                map.put("key", summary.getKey());
                transferRecordMaps.add(map);
            }
            checkBundleInList();

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            dialog.dismiss();
        }
    }
    public static void initData(){
        downloadMaps.clear();
        // Uses TransferUtility to get all previous download records.
        observers = transferUtility.getTransfersWithType(TransferType.DOWNLOAD);
        TransferListener listener = new DownloadFragment.DownloadListener();
        for (TransferObserver observer : observers) {
            observer.refresh();
            HashMap<String, Object> map = new HashMap<>();
            util.fillMap(map, observer, false);
            downloadMaps.add(map);

            // Sets listeners to in progress transfers
            if (TransferState.WAITING.equals(observer.getState())
                    || TransferState.WAITING_FOR_NETWORK.equals(observer.getState())
                    || TransferState.IN_PROGRESS.equals(observer.getState())) {
                observer.setTransferListener(listener);
            }
        }
        simpleAdapter.notifyDataSetChanged();
    }

    public static void beginDownload(String key){
        File file = new File(mContext.getExternalFilesDir(null).toString() + "/" + key);

        //TransferObserver observer = transferUtility.download(key, file);


        Intent intent = new Intent(mContext, MyService.class);
        intent.putExtra(MyService.INTENT_KEY_NAME, key);
        intent.putExtra(MyService.INTENT_TRANSFER_OPERATION, MyService.TRANSFER_OPERATION_DOWNLOAD);
        intent.putExtra(MyService.INTENT_FILE, file);
        mContext.startService(intent);
        //observer.setTransferListener(new DownloadListener());

    }

    private static class DownloadListener implements TransferListener, Serializable {
        // Simply updates the list when notified.
        @Override
        public void onError(int id, Exception e) {
            Log.e(TAG, "onError: " + id, e);
            updateList();
        }

        @Override
        public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
            Log.d(TAG, String.format("onProgressChanged: %d, total: %d, current: %d",
                    id, bytesTotal, bytesCurrent));
            updateList();
        }

        @Override
        public void onStateChanged(int id, TransferState state) {
            Log.d(TAG, "onStateChanged: " + id + ", " + state);
            updateList();
        }
    }

    static void updateList() {
        TransferObserver observer;
        HashMap<String, Object> map;
        for (int i = 0; i < observers.size(); i++) {
            observer = observers.get(i);
            observer.setTransferListener(new DownloadListener());
        }
        simpleAdapter.notifyDataSetChanged();
    }

}