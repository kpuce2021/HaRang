package com.example.harang.activity;

import android.graphics.drawable.Drawable;
import android.widget.Button;

public class VideoListViewItem {
    //진짜 사용할 거
    private Drawable videoThumbnail;
    private String videoName;
    private int totalProgress;
    private int concentProgress;
    private String v_id;

    //button
    private Button AllVideoPlay;
    private Button clipVideoPlay;


    //진짜 사용할거
    public void setVideoThumbnail(Drawable capture) {
        videoThumbnail = capture;
    }
    public void setVideoName(String name){ videoName = name; }
    public void setTotalProgress(int progress){ totalProgress = progress; }
    public void setConcentProgress(int progress) { concentProgress = progress; }
    public void setVid(String vid) { v_id = vid; }

    public Drawable getVideoThumbnail() { return this.videoThumbnail; }
    public String getVideoName() { return this.videoName; }
    public int getTotalProgress(){ return this.totalProgress; }
    public int getConcentProgress(){ return this.concentProgress; }
    public String getVid() { return this.v_id; }

    //버튼
    public void setAllVideoPlay(Button btn){ AllVideoPlay = btn; }
    public void setClipVideoPlay(Button btn){ clipVideoPlay = btn; }





}
