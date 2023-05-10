package com.joevirn.joyce.melanie.ibm2105_group3_code;

public class HistoryItem {
    private String mUUserName;
    private String mUserPW;
    private int mHistoryID;
    private String mHUserName;
    private String mDate;
    private String mTime;
    private String mTotalRoundPlayed;
    private String mTotalRoundWon;
    private double mWinningPercentage;

    public  HistoryItem(){

    }

    public String getUUserName() {
        return mUUserName;
    }

    public void setUUserName(String mUUserName) {
        this.mUUserName = mUUserName;
    }

    public String getUserPW() {
        return mUserPW;
    }

    public void setUserPW(String mUserPW) {
        this.mUserPW = mUserPW;
    }

    public int getHistoryID() {
        return mHistoryID;
    }

    public void setHistoryID(int mHistoryID) {
        this.mHistoryID = mHistoryID;
    }

    public String getHUserName() {
        return mHUserName;
    }

    public void setHUserName(String mHUserName) {
        this.mHUserName = mHUserName;
    }

    public String getDate() {
        return mDate;
    }

    public void setDate(String mDate) {
        this.mDate = mDate;
    }

    public String getTime() {
        return mTime;
    }

    public void setTime(String mTime) {
        this.mTime = mTime;
    }

    public String getTotalRoundPlayed() {
        return mTotalRoundPlayed;
    }

    public void setTotalRoundPlayed(String mTotalRoundPlayed) {
        this.mTotalRoundPlayed = mTotalRoundPlayed;
    }

    public String getTotalRoundWon() {
        return mTotalRoundWon;
    }

    public void setTotalRoundWon(String mTotalRoundWon) {
        this.mTotalRoundWon = mTotalRoundWon;
    }

    public double getWinningPercentage() {
        return mWinningPercentage;
    }

    public void setWinningPercentage(double mWinningPercentage) {
        this.mWinningPercentage = mWinningPercentage;
    }
}
