package com.mlearn.Entity;

import java.io.Serializable;


public class MaterialInfo implements Serializable {

    private int resID;
    private int courseID;
    private String teacherNumber;
    private String publishTime;
    private String resTitle;
    private String resUrl;//这是从服务器读取的url
    private long size = 0;//大小，这是从服务器读取的大小

    private String teacherName;
    private String courseName;

    private String state = "";  //下载状态
    private int progress = 0;   //下载进度
    private long length = 0;//长度,之后才会设置

    private String saveDir = "";//存储文件夹
    private String savePath = "";//存储路径，包括文件名
    private String downloadUrl = "";//这是客户端下载的url，由之后设置
    private int position = -1;//这是资源在recyclerview中的位置，由适配器给出，初始化为-1

    private boolean isExistLocal;

    public MaterialInfo() {
    }

    public MaterialInfo(int resID, int courseID, String teacherNumber, String publishTime, String resTitle, String resUrl, long size) {
        this.resID = resID;
        this.courseID = courseID;
        this.teacherNumber = teacherNumber;
        this.publishTime = publishTime;
        this.resTitle = resTitle;
        this.resUrl = resUrl;
        this.size = size;
    }

    public int getResID() {
        return resID;
    }

    public void setResID(int resID) {
        this.resID = resID;
    }

    public int getCourseID() {
        return courseID;
    }

    public void setCourseID(int courseID) {
        this.courseID = courseID;
    }

    public String getPublishTime() {
        return publishTime;
    }

    public void setPublishTime(String publishTime) {
        this.publishTime = publishTime;
    }

    public String getResTitle() {
        return resTitle;
    }

    public void setResTitle(String resTitle) {
        this.resTitle = resTitle;
    }

    public String getResUrl() {
        return resUrl;
    }

    public void setResUrl(String resUrl) {
        this.resUrl = resUrl;
    }

    public String getTeacherNumber() {
        return teacherNumber;
    }

    public void setTeacherNumber(String teacherNumber) {
        this.teacherNumber = teacherNumber;
    }

    public String getTeacherName() {
        return teacherName;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
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

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public boolean isExistLocal() {
        return isExistLocal;
    }

    public void setExistLocal(boolean existLocal) {
        isExistLocal = existLocal;
    }

    @Override
    public String toString() {
        return "MaterialInfo{" +
                "resID=" + resID +
                ", courseID=" + courseID +
                ", resTitle='" + resTitle + '\'' +
                ", resUrl='" + resUrl + '\'' +
                ", length=" + length +
                ", savePath='" + savePath + '\'' +
                ", downloadUrl='" + downloadUrl + '\'' +
                '}';
    }
}
