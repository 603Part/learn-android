package com.mlearn.Entity;

import java.io.Serializable;

public class StuHomework implements Serializable {
    private int shwID;
    private int courseID;
    private int hwID;
    private String subTime;
    private String hwUrl;
    private String stuWorkTitle;
    private int userID;
    private long size;

    private String hwTitle;
    private String studentName;
    private String sid;
    private String courseName;

    private String state = "";  //下载状态
    private int progress = 0;   //下载进度
    private long length = 0;//长度,之后才会设置

    private String saveDir = "";//存储文件夹
    private String savePath = "";//存储路径，包括文件名
    private String downloadUrl = "";//这是客户端下载的url，由之后设置
    private int position = -1;//这是资源在recyclerview中的位置，由适配器给出，初始化为-1

    private boolean isExistLocal;

    public StuHomework() {
        super();
    }

    public StuHomework(int shwID, int courseID, int hwID, String subTime, String hwUrl, String stuWorkTitle, int userID,
                       String studentName) {
        super();
        this.shwID = shwID;
        this.courseID = courseID;
        this.hwID = hwID;
        this.subTime = subTime;
        this.hwUrl = hwUrl;
        this.stuWorkTitle = stuWorkTitle;
        this.userID = userID;
        this.studentName = studentName;
    }

    public StuHomework(int shwID, int courseID, int hwID, String subTime, String hwUrl, String stuWorkTitle,
                       String stuentNumber) {
        super();
        this.shwID = shwID;
        this.courseID = courseID;
        this.hwID = hwID;
        this.subTime = subTime;
        this.hwUrl = hwUrl;
        this.stuWorkTitle = stuWorkTitle;
        this.studentName = stuentNumber;
    }

    public String getHwTitle() {
        return hwTitle;
    }

    public void setHwTitle(String hwTitle) {
        this.hwTitle = hwTitle;
    }

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public int getShwID() {
        return shwID;
    }

    public void setShwID(int shwID) {
        this.shwID = shwID;
    }

    public int getCourseID() {
        return courseID;
    }

    public void setCourseID(int courseID) {
        this.courseID = courseID;
    }

    public int getHwID() {
        return hwID;
    }

    public void setHwID(int hwID) {
        this.hwID = hwID;
    }

    public String getSubTime() {
        return subTime;
    }

    public void setSubTime(String subTime) {
        this.subTime = subTime;
    }

    public String getHwUrl() {
        return hwUrl;
    }

    public void setHwUrl(String hwUrl) {
        this.hwUrl = hwUrl;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getStuWorkTitle() {
        return stuWorkTitle;
    }

    public void setStuWorkTitle(String stuWorkTitle) {
        this.stuWorkTitle = stuWorkTitle;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public long getLength() {
        return length;
    }

    public void setLength(long length) {
        this.length = length;
    }

    public String getSaveDir() {
        return saveDir;
    }

    public void setSaveDir(String saveDir) {
        this.saveDir = saveDir;
    }

    public String getSavePath() {
        return savePath;
    }

    public void setSavePath(String savePath) {
        this.savePath = savePath;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public boolean isExistLocal() {
        return isExistLocal;
    }

    public void setExistLocal(boolean existLocal) {
        isExistLocal = existLocal;
    }

    @Override
    public String toString() {
        return "StuHomework [shwID=" + shwID + ", courseID=" + courseID + ", hwID=" + hwID + ", subTime=" + subTime
                + ", hwUrl=" + hwUrl + ", stuWorkTitle=" + stuWorkTitle + ", userID =" + userID + ", studentName =" + studentName + ", size=" + size + "]";
    }

}
