package com.mlearn.Entity;

import java.io.Serializable;

public class User implements Serializable {
    public static int LOGIN = 1;
    public static int LOGOUT = 0;

    private int userId;

    private String sId;

    private String password;

    private String name;

    private String sex;

    private String email;

    private String phone;

    private String photo;

    private String signature;

    private int type;

    private int islogin;

    private String college;

    private String specialty;



    public User() {
        super();
    }


    public User(int userId, String sId, String password, String sex,
                String email, String photo, String signature, int type) {
        super();
        this.userId = userId;
        this.sId = sId;
        this.password = password;
        this.sex = sex;
        this.email = email;
        this.photo = photo;
        this.signature = signature;
        this.type = type;
    }

    public User(int userId, String sId, String password, String name, String sex,
                String email, String photo, String phone, String signature, int type) {
        super();
        this.userId = userId;
        this.sId = sId;
        this.password = password;
        this.name = name;
        this.sex = sex;
        this.email = email;
        this.phone = phone;
        this.photo = photo;
        this.signature = signature;
        this.type = type;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getsId() {
        return sId;
    }

    public void setsId(String sId) {
        this.sId = sId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getSignature() {
        if(signature == null) return "";
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getIslogin() {
        return islogin;
    }

    public void setIslogin(int islogin) {
        this.islogin = islogin;
    }

    public String getCollege() {
        return college;
    }

    public void setCollege(String college) {
        this.college = college;
    }

    public String getSpecialty() {
        return specialty;
    }

    public void setSpecialty(String specialty) {
        this.specialty = specialty;
    }

    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", sId='" + sId + '\'' +
                ", password='" + password + '\'' +
                ", name='" + name + '\'' +
                ", sex='" + sex + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", photo='" + photo + '\'' +
                ", signature='" + signature + '\'' +
                ", type=" + type +
                ", college=" + college +
                ", specialty=" + specialty +
                '}';
    }
}
