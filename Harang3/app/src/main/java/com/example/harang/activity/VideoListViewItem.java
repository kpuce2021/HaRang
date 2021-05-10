package com.example.harang.activity;

import android.graphics.drawable.Drawable;
import android.widget.Button;

public class VideoListViewItem {
    private Drawable iconDrawable ;
    private String titleStr ;
    private String descStr ;

    //진짜 사용할 거
    private Drawable videoThumbnail;
    private String videoName;
    private int totalProgress;
    private int concentProgress;

    //button
    private Button AllVideoPlay;
    private Button clipVideoPlay;


    public void setIcon(Drawable icon) {
        iconDrawable = icon ;
    }
    public void setTitle(String title) {
        titleStr = title ;
    }
    public void setDesc(String desc) {
        descStr = desc ;
    }

    public Drawable getIcon() {
        return this.iconDrawable ;
    }
    public String getTitle() {
        return this.titleStr ;
    }
    public String getDesc() {
        return this.descStr ;
    }


    //진짜 사용할거
    public void setVideoThumbnail(Drawable capture) {
        videoThumbnail = capture;
    }
    public void setVideoName(String name){ videoName = name; }
    public void setTotalProgress(int progress){ totalProgress = progress; }
    public void setConcentProgress(int progress) { concentProgress = progress; }

    public Drawable getVideoThumbnail() { return this.videoThumbnail; }
    public String getVideoName() { return this.videoName; }
    public int getTotalProgress(){ return this.totalProgress; }
    public int getConcentProgress(){ return this.concentProgress; }

    //버튼
    public void setAllVideoPlay(Button btn){ AllVideoPlay = btn; }
    public void setClipVideoPlay(Button btn){ clipVideoPlay = btn; }





}
