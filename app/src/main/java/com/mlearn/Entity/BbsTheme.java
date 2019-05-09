package com.mlearn.Entity;

import java.io.Serializable;

public class BbsTheme implements Serializable {
    private int postID;//帖子id
    private int courseID;
    private String studentNumber;
    private String postTitle;//帖子主题
    private String postContent;//帖子内容
    private String postTime;//发帖时间
    private String replyTime;//最后回复时间
    private int state;//状态，0表示老师未回复，1表示老师已回复

    private int replyCount;//回复数量

    private String studentPhotoURL;
    private String studentName;

    public BbsTheme() {
    }

    public BbsTheme(int postID, String studentNumber, String studentName, int courseID, String postTitle, String postContent,
                    int replyCount, String postTime, String replyTime, int state) {
        super();
        this.postID = postID;
        this.studentNumber = studentNumber;
        this.studentName = studentName;
        this.courseID = courseID;
        this.postTitle = postTitle;
        this.postContent = postContent;
        this.replyCount = replyCount;
        this.postTime = postTime;
        this.replyTime = replyTime;
        this.state = state;
    }

    public int getPostID() {
        return postID;
    }

    public void setPostID(int postID) {
        this.postID = postID;
    }

    public String getStudentNumber() {
        return studentNumber;
    }

    public void setStudentID(String studentNumber) {
        this.studentNumber = studentNumber;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public int getCourseID() {
        return courseID;
    }

    public void setCourseID(int courseID) {
        this.courseID = courseID;
    }

    public String getPostTitle() {
        return postTitle;
    }

    public void setPostTitle(String postTitle) {
        this.postTitle = postTitle;
    }

    public String getPostContent() {
        return postContent;
    }

    public void setPostContent(String postContent) {
        this.postContent = postContent;
    }

    public int getReplyCount() {
        return replyCount;
    }

    public void setReplyCount(int replyCount) {
        this.replyCount = replyCount;
    }

    public String getPostTime() {
        return postTime;
    }

    public void setPostTime(String postTime) {
        this.postTime = postTime;
    }

    public String getReplyTime() {
        return replyTime;
    }

    public void setReplyTime(String replyTime) {
        this.replyTime = replyTime;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public void setStudentNumber(String studentNumber) {
        this.studentNumber = studentNumber;
    }

    public String getStudentPhotoURL() {
        return studentPhotoURL;
    }

    public void setStudentPhotoURL(String studentPhotoURL) {
        this.studentPhotoURL = studentPhotoURL;
    }

    @Override
    public String toString() {
        return "BbsTheme [postID=" + postID + ", studentID=" + studentNumber + ", studentName=" + studentName
                + ", courseID=" + courseID + ", postTitle=" + postTitle + ", postContent=" + postContent
                + ", replyCount=" + replyCount + ", postTime=" + postTime + ", replyTime=" + replyTime + ", state="
                + state + "]";
    }


}
