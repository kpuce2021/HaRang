package com.example.harang.activity;

public class SClipVideoListItem {
    private String videoName; //강의명
    private String startTime; //클립 시작 시간
    private int videoLength;//클립 영상 시간
    private String endTime;//클립 끝나는 시간

    public void setVideoName(String name){ videoName = name; }
    public void setStartTime(String time){ startTime = time; }
    public void setVideoLength(int length){ videoLength = length; }
    public void setEndTime(String time){ endTime = time; }

    public String getVideoName(){ return this.videoName; }
    public String getStartTime(){ return this.startTime; }
    public int getVideoLength(){ return this.videoLength; }
    public String getEndTime(){ return this.endTime; }

}
