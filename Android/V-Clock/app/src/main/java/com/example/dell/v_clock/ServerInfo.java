package com.example.dell.v_clock;

/**
 * Created by andy on 7/7/17.
 */

public class ServerInfo {

//    public static String SERVER_URL_1 = "http://121.250.222.39:8080/V-Clock";
    public static String SERVER_URL_2 = "http://121.250.222.75:8080/V-Clock";


    public static String LOGIN_URL = SERVER_URL_2 + "/servlet/LoginServlet";
    public static String REGISTER_URL = SERVER_URL_2 + "/servlet/RegisterServlet";
    public static String MODIFY_EMPLOYEE_INFO_URL = SERVER_URL_2 + "/servlet/ModifyEmployeeInfoServlet";
    public static String DISPLAY_EMPLOYEE_INFO_URL = SERVER_URL_2 + "/servlet/DisplayEmployeeInfoServlet";
    public static String CREATE_NEW_GUEST_URL = SERVER_URL_2 + "/servlet/CreateNewGuestServlet";
    public static String SEARCH_GUEST_URL = SERVER_URL_2 + "/servlet/SearchGuestServlet";
    public static String MODIFY_GUEST_INFO_URL = SERVER_URL_2 + "/servlet/ModifyGuestInfoServlet";
    public static String ADD_TO_GUEST_LIST_URL = SERVER_URL_2 + "/servlet/AddtoGuestListServlet";
    public static String DELETE_FROM_GUEST_LIST_URL = SERVER_URL_2 +"/servlet/DeleteFromGuestListServlet";
    public static String DISPLAY_VISITING_RECORD_URL = SERVER_URL_2 + "/servlet/DisplayVisitingRecordServlet";
    public static String PREPARE_FOR_PUSH_URL = SERVER_URL_2 + "/servlet/PrepareForPushServlet";
    public static String PUSH_MESSAGE_URL = SERVER_URL_2 + "/servlet/PushMessageServlet";

    public static String VISITING_RECORD_KEY = "VisitingRecord";
}
