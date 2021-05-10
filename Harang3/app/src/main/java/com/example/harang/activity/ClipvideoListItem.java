package com.example.harang.activity;

public class ClipvideoListItem {
    private String videoName; //강의명
    private int startTime; //클립 시작 시간
    private int videoLength;//클립 영상 시간
    private int endTime;//클립 끝나는 시간

    public void setVideoName(String name){ videoName = name; }
    public void setStartTime(int time){ startTime = time; }
    public void setVideoLength(int length){ videoLength = length; }
    public void setEndTime(int time){ endTime = time; }

    public String getVideoName(){ return this.videoName; }
    public int getStartTime(){ return this.startTime; }
    public int getVideoLength(){ return this.videoLength; }
    public int getEndTime(){ return this.endTime; }

}
