package com.mlearn.Entity;

/**
 * Created by AIRCode on 2018/11/13.
 */

public class Test {
    public static final int PAN_DUAN = 0;
    public static final int DAN_XUAN = 1;
    public static final int DUO_XUAN = 2;

    private int testID;
    private int courseID;
    private String teacherNumber;
    private String testContent;
    private int type;
    private String testAnswer;
    private String testOption;
    private String userAnswer = "未作答";

    private String teacherName;
    private String courseName;

    public Test() {
    }

    public Test(int testID, int courseID, String teacherNumber, String testContent, String testAnswer, String testOption, String userAnswer) {
        this.testID = testID;
        this.courseID = courseID;
        this.teacherNumber = teacherNumber;
        this.testContent = testContent;
        this.testAnswer = testAnswer;
        this.testOption = testOption;
        this.userAnswer = userAnswer;
    }

    public int getTestID() {
        return testID;
    }

    public void setTestID(int testID) {
        this.testID = testID;
    }

    public String getTeacherNumber() {
        return teacherNumber;
    }

    public void setTeacherNumber(String teacherNumber) {
        this.teacherNumber = teacherNumber;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
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

    public String getTestContent() {
        return testContent;
    }

    public void setTestContent(String testContent) {
        this.testContent = testContent;
    }

    public String getTestAnswer() {
        return testAnswer;
    }

    public void setTestAnswer(String testAnswer) {
        this.testAnswer = testAnswer;
    }

    public String getTestOption() {
        return testOption;
    }

    public void setTestOption(String testOption) {
        this.testOption = testOption;
    }

    public String getUserAnswer() {
        return userAnswer;
    }

    public void setUserAnswer(String userAnswer) {
        this.userAnswer = userAnswer;
    }
}
