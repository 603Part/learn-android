package com.mlearn.Entity;


public class Homework {
    public int hwID;
    public int courseID;
    public String teacherNumber;
    public String hwContent;
    public String publishTime;


    public String hwTitle;

    private String teacherName;
    private String courseName;

    public Homework() {
    }

    public Homework(int hwID, int courseID, String teacherNumber, String hwContent, String publishTime, String hwTitle) {
        this.hwID = hwID;
        this.courseID = courseID;
        this.teacherNumber = teacherNumber;
        this.hwContent = hwContent;
        this.publishTime = publishTime;
        this.hwTitle = hwTitle;
    }

    public Homework(int hwID, int courseID, String teacherNumber, String hwContent, String publishTime, String hwTitle, String teacherName, String courseName) {
        this.hwID = hwID;
        this.courseID = courseID;
        this.teacherNumber = teacherNumber;
        this.hwContent = hwContent;
        this.publishTime = publishTime;
        this.hwTitle = hwTitle;
        this.teacherName = teacherName;
        this.courseName = courseName;
    }

    public int getHwID() {
        return hwID;
    }

    public void setHwID(int hwID) {
        this.hwID = hwID;
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

    public int getCourseID() {
        return courseID;
    }

    public void setCourseID(int courseID) {
        this.courseID = courseID;
    }

    public String getHwContent() {
        return hwContent;
    }

    public void setHwContent(String hwContent) {
        this.hwContent = hwContent;
    }

    public String getPublishTime() {
        return publishTime;
    }

    public void setPublishTime(String publishTime) {
        this.publishTime = publishTime;
    }

    public String getHwTitle() {
        return hwTitle;
    }

    public void setHwTitle(String hwTitle) {
        this.hwTitle = hwTitle;
    }

    @Override
    public String toString() {
        return "Homework{" +
                "hwID=" + hwID +
                ", teacherNumber;=" + teacherNumber +
                ", courseID=" + courseID +
                ", hwContent='" + hwContent + '\'' +
                ", publishTime='" + publishTime + '\'' +
                ", hwTitle='" + hwTitle + '\'' +
                ", teacherName='" + teacherName + '\'' +
                ", courseName='" + courseName + '\'' +
                '}';
    }
}
