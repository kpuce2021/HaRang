package com.example.harang.activity;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.harang.R;

import java.util.ArrayList;

public class VideoListViewAdapter extends ArrayAdapter{

    // 버튼 클릭 이벤트를 위한 Listener 인터페이스 정의.
    public interface ListBtnClickListener {
        void onListBtnClick(int position) ;
    }

    // 생성자로부터 전달된 resource id 값을 저장.
    int resourceId ;
    // 생성자로부터 전달된 ListBtnClickListener  저장.
    private ListBtnClickListener listBtnClickListener ;

    // Adapter에 추가된 데이터를 저장하기 위한 ArrayList
    private ArrayList<VideoListViewItem> listViewItemList = new ArrayList<VideoListViewItem>() ;


    // ListViewAdapter의 생성자
    public VideoListViewAdapter(Context context, int resource, ArrayList<VideoListViewItem> list) {
        super(context, resource, list);
        // resource id 값 복사. (super로 전달된 resource를 참조할 방법이 없음.)
        this.listViewItemList = list;
        this.resourceId = resource ;
    }



    // Adapter에 사용되는 데이터의 개수를 리턴. : 필수 구현
    @Override
    public int getCount() {
        return listViewItemList.size() ;
    }

    // position에 위치한 데이터를 화면에 출력하는데 사용될 View를 리턴. : 필수 구현
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final int pos = position;
        final Context context = parent.getContext();

        // "listview_item" Layout을 inflate하여 convertView 참조 획득.
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.video_listview_item, parent, false);
        }

        // 화면에 표시될 View(Layout이 inflate된)으로부터 위젯에 대한 참조 획득
        ImageView videoThumbnail = (ImageView) convertView.findViewById(R.id.video_thumbnail);
        TextView videoName = (TextView) convertView.findViewById(R.id.video_name);
        ProgressBar totalProgressBar = (ProgressBar) convertView.findViewById(R.id.total_progressBar);
        ProgressBar concentProgressBar = (ProgressBar) convertView.findViewById(R.id.concent_progressBar);

        // Data Set(listViewItemList)에서 position에 위치한 데이터 참조 획득
        final VideoListViewItem listViewItem = (VideoListViewItem) getItem(position);
        
        // Data Set(listViewItemList)에서 position에 위치한 데이터 참조 획득 - 원래꺼
        //VideoListViewItem listViewItem = listViewItemList.get(position);

        // 아이템 내 각 위젯에 데이터 반영
        videoThumbnail.setImageDrawable(listViewItem.getVideoThumbnail());
        videoName.setText(listViewItem.getVideoName());
        totalProgressBar.setProgress(listViewItem.getTotalProgress());
        concentProgressBar.setProgress(listViewItem.getConcentProgress());

        Button allVideo = (Button) convertView.findViewById(R.id.all_btn);
        Button clipVideo = (Button) convertView.findViewById(R.id.clip_btn);


        Bundle bundle = new Bundle();
        bundle.putString("videoName", listViewItem.getVideoName());
        //버튼 별 클릭 리스너
        allVideo.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {

                ((BaseActivity)getContext()).replaceFragment(DownloadFragment.newInstance(),bundle);
                Log.i("pageTest",Integer.toString(pos) + "번 전체재생 버튼 선택.");
            }
        });
        clipVideo.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                ((BaseActivity)getContext()).replaceFragment(DownloadFragment.newInstance(),bundle);
                Log.i("pageTest",Integer.toString(pos) + "번 클립재생 버튼 선택.");
            }
        });

        return convertView;
    }

    // 지정한 위치(position)에 있는 데이터와 관계된 아이템(row)의 ID를 리턴. : 필수 구현
    @Override
    public long getItemId(int position) {
        return position ;
    }

    // 지정한 위치(position)에 있는 데이터 리턴 : 필수 구현
    @Override
    public Object getItem(int position) {
        return listViewItemList.get(position) ;
    }

    // 아이템 데이터 추가를 위한 함수. 개발자가 원하는대로 작성 가능.
    public void addItem(Drawable thumbnail, String name, int total, int concent) {
        VideoListViewItem item = new VideoListViewItem();

        item.setVideoThumbnail(thumbnail);
        item.setVideoName(name);
        item.setTotalProgress(total);
        item.setConcentProgress(concent);

        listViewItemList.add(item);
    }
}
