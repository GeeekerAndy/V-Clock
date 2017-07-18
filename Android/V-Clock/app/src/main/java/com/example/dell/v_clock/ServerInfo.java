package com.example.dell.v_clock;

/**
 * Created by andy on 7/7/17.
 */

public class ServerInfo {


    private static String SERVER_URL_2 = "http://121.250.222.75:8080/V-Clock";
    private static String SERVER_URL_1 = "http://121.250.222.47:8080/V-Clock";

    public static String LOGIN_URL = SERVER_URL_2 + "/servlet/ALoginServlet";
    public static String REGISTER_URL = SERVER_URL_2 + "/servlet/ARegisterServlet";
    public static String MODIFY_EMPLOYEE_INFO_URL = SERVER_URL_2 + "/servlet/AModifyEmployeeInfoServlet";
    public static String DISPLAY_EMPLOYEE_INFO_URL = SERVER_URL_2 + "/servlet/ADisplayEmployeeInfoServlet";
    public static String CREATE_NEW_GUEST_URL = SERVER_URL_2 + "/servlet/ACreateNewGuestServlet";
    public static String SEARCH_GUEST_URL = SERVER_URL_2 + "/servlet/ASearchGuestServlet";
    public static String MODIFY_GUEST_INFO_URL = SERVER_URL_2 + "/servlet/AModifyGuestInfoServlet";
    public static String ADD_TO_GUEST_LIST_URL = SERVER_URL_2 + "/servlet/AAddtoGuestListServlet";
    public static String DELETE_FROM_GUEST_LIST_URL = SERVER_URL_2 +"/servlet/ADeleteFromGuestListServlet";
    public static String DISPLAY_VISITING_RECORD_URL = SERVER_URL_2 + "/servlet/ADisplayVisitingRecordServlet";
    public static String PREPARE_FOR_PUSH_URL = SERVER_URL_2 + "/servlet/PrepareForPushServlet";
    public static String PUSH_MESSAGE_URL = SERVER_URL_2 + "/servlet/PushMessageServlet";

    public static String VISITING_RECORD_KEY = "VisitingRecord";


}
