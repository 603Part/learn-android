package com.mlearn.HttpUtil;


public interface ConstsUrl {
    String ip = "192.168.1.3";//
//    String ip = "192.168.0.101";
    String LOGIN_URL = "http://" + ip + ":8080/learn/UserServlet";
    String COURSE_URL = "http://"  + ip + ":8080/learn/CourseListServlet";
    String BASE_URL = "http://" + ip + ":8080/learn/";
    String upLoad_photo_URL = "http://" + ip + ":8080/learn/UserServlet?operation=updatePhoto";//上传头像专用
    String NOTICE_URL = "http://" + ip + ":8080/learn/NoticeServlet";//获取通知的urlupdatePhoto
    String TEST_URL = "http://" + ip + ":8080/learn/TestServlet";//获取测试的url
    String BBS_URL = "http://" + ip + ":8080/learn/BbsServlet";//师生交流的url
    String RES_URL = "http://" + ip + ":8080/learn/ResServlet";//获取课程资源的url
    String HW_URL = "http://" + ip + ":8080/learn/HomeworkServlet";//获取作业的url
}
