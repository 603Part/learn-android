package com.mlearn.Entity;


import java.io.Serializable;

public class ReplyPost implements Serializable {
    public static int TEACHER = 0;
    public static int STUDENT = 1;

    public int replyID;
    public int postID;
    public String userNumber;
    public String replyContent;
    public String replyTime;
    public int userType;//用户身份0表示学生，1表示老师
    public int starNum = 0;//赞数，默认为0

    public String userName;
    public String userPhotoURL;

    public ReplyPost() {
        super();
    }

    public ReplyPost(int replyID, int postID, String userNumber, String replyContent, String replyTime, int userType, int starNum) {
        this.replyID = replyID;
        this.postID = postID;
        this.userNumber = userNumber;
        this.replyContent = replyContent;
        this.replyTime = replyTime;
        this.userType = userType;
        this.starNum = starNum;
    }

    public int getReplyID() {
        return replyID;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserPhotoURL() {
        return userPhotoURL;
    }

    public void setUserPhotoURL(String userPhotoURL) {
        this.userPhotoURL = userPhotoURL;
    }

    public void setReplyID(int replyID) {
        this.replyID = replyID;
    }

    public int getPostID() {
        return postID;
    }

    public void setPostID(int postID) {
        this.postID = postID;
    }

    public String getUserNumber() {
        return userNumber;
    }

    public void setUserNumber(String userNumber) {
        this.userNumber = userNumber;
    }

    public String getReplyContent() {
        return replyContent;
    }

    public void setReplyContent(String replyContent) {
        this.replyContent = replyContent;
    }

    public String getReplyTime() {
        return replyTime;
    }

    public void setReplyTime(String replyTime) {
        this.replyTime = replyTime;
    }

    public int getUserType() {
        return userType;
    }

    public void setUserType(int userType) {
        this.userType = userType;
    }

    public int getStarNum() {
        return starNum;
    }

    public void setStarNum(int starNum) {
        this.starNum = starNum;
    }

    @Override
    public String toString() {
        return "ReplyPost{" +
                "replyID=" + replyID +
                ", postID=" + postID +
                ", studentNumber='" + userNumber + '\'' +
                ", replyContent='" + replyContent + '\'' +
                ", replyTime='" + replyTime + '\'' +
                ", userType=" + userType +
                ", starNum=" + starNum +
                '}';
    }
}
