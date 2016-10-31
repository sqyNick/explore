package com.fhzz.cn.exploremap.value;

/**
 * Created by Administrator on 2016/9/27.
 */
public interface StaticValues {

    /**
     * 服务器配置
     */
    //服务器IP
    String IP = "10.1.106.16";
    //服务器端口
    int PORT = 8090;
    //工程名
    String PROJECT = "explore";
    //服务器基地址
    String SERVER = IP + ":" +PORT +"/" +PROJECT;
    /**登录请求*/
    String ACTION_LOGIN = "http://" + SERVER + "/login.do";
    String ACTION_MODIFY_POINT = "http://" + SERVER + "/modifyPoint.do";
    String ACTION_QUERY_POINT = "http://" + SERVER + "/queryPoint.do";
    String ACTION_QUREY_POINT_AREA = "http://" + SERVER + "/queryPointArea.do";
    String ACTION_QUREY_POINT_AREA_BY_LIST = "http://" + SERVER + "/queryPointByAreaList.do";
    String ACTION_FIND_POINT = "http://" + SERVER + "/pointListByMobil.do";

    /**服务器配置结束*/

    /**
     * 数据库配置
     */
    //数据库名
    String DB_NAME = "explore_db";

    /**数据库配置结束*/

    /**
     * SharedPreferenced配置
     */
    String SHARED_PREFERENCED_NAME = "CopyRight_SQY";

    /**SharedPreferenced配置结束*/

    /**空字符*/
    String EMPTY_VALUE = "";
    /**分隔符*/
    String SPLIT = ",";
    /**成功登录过的账号*/
    String LOGINED_USER = "LOGINED_USER";
    /**上次登录的账号*/
    String LAST_LOGINED_USER = "LAST_LOGINED_USER";
    String LAST_LOGINED_PSD = "LAST_LOGINED_PSD";
    /**现在登录的账号*/
    String NOW_LOGIN_USER = "NOW_LOGIN_USER";
    /**http请求超时时长*/
    long CONNECT_TIME_OUT = 6000L;
    /**http请求读取时长*/
    long READ_TIME_OUT = 6000L;
    /**引导界面显示时长*/
    long SPLASH_TIME = 1900L;
    /**应用根目录名*/
    String APP_DIRECTORY = "ExploreMap";

    String CAPTURE_PIC_DIR = "capture";

    int BLOT_CAMEAR = 1;
    int DOME_CAMERA = 0;

    double DEFAULT_LOCATION_LAT = 30.506111;
    double DEFAULT_LOCATION_LON = 114.399315;
    /**
     * 1 待添加的点
     * 2 搜索出的点
     * */
    int TO_ADD_MARKER = 1;
    int SEARCHED_MARKER = 2;
    /**默认一次加载条数*/
    int QUERY_DATE_PAGE_SIZE = 10;
    int SUBMITED_POINT = 1;
    int UN_SUBMIT_POINT = 0;

    int SUBMIT_NOTIFYCATION = 0;

    String CANCLE_SUBMIT_ACTION = "CANCLE_SUBMIT_ACTION";
}
