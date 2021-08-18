package com.example.harangS.activity;

import android.graphics.drawable.Drawable;
import android.widget.Button;

public class VideoListViewItem {
    //진짜 사용할 거
    private Drawable videoThumbnail;
    private String videoName;
    private int totalProgress;
    private int concentProgress;
    private String v_id;

    private String v_concent1_start;
    private String v_concent1_stop;
    private String v_concent2_start;
    private String v_concent2_stop;
    private String clipCount;

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
    public void setConcentRange(String concent1_start, String concent1_stop,
                                String concent2_start, String concent2_stop, String count){
        v_concent1_start = concent1_start;
        v_concent1_stop = concent1_stop;
        v_concent2_start = concent2_start;
        v_concent2_stop = concent2_stop;
        clipCount = count;
    }

    public Drawable getVideoThumbnail() { return this.videoThumbnail; }
    public String getVideoName() { return this.videoName; }
    public int getTotalProgress(){ return this.totalProgress; }
    public int getConcentProgress(){ return this.concentProgress; }
    public String getVid() { return this.v_id; }
    public String getStart1(){
        if(v_concent1_start.equals("-00:00:00")){
            return v_concent2_start;
        }else{
            return v_concent1_start;
        }
    }
    public String getStart2(){
        return v_concent2_start;
    }
    public String getStop1(){
        if(v_concent1_start.equals("-00:00:00")){
            return v_concent2_stop ;
        }else{
            return v_concent1_stop;
        }
    }
    public String getStop2(){
        return v_concent2_stop;
    }

    public String getClipCount(){
        return clipCount;
    }

    //버튼
    public void setAllVideoPlay(Button btn){ AllVideoPlay = btn; }
    public void setClipVideoPlay(Button btn){ clipVideoPlay = btn; }





}
