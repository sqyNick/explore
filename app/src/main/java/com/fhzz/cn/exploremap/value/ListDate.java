package com.fhzz.cn.exploremap.value;

import com.fhzz.cn.exploremap.entity.PointAreaResp;
import com.fhzz.cn.exploremap.entity.QueryPointAreaResp;
import com.google.gson.Gson;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.Call;

/**
 * Created by Administrator on 2016/10/18.
 */

public class ListDate {
    public static List<String> POINT_AREA_LIST = new ArrayList<>();
    public static HashMap<String,String> POINT_AREA_HASHMAP = new HashMap<>();
    public static void init(){
        if(POINT_AREA_LIST.size() > 0){
            return;
        }
        OkHttpUtils.post().url(StaticValues.ACTION_QUREY_POINT_AREA).build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {

            }
            @Override
            public void onResponse(String response, int id) {
                QueryPointAreaResp resp = new Gson().fromJson(response,QueryPointAreaResp.class);
                if(resp.code == 1000){
                    PointAreaResp[] pointAreaResps = resp.message;
                    for(PointAreaResp pa : pointAreaResps){
                        POINT_AREA_LIST.add(pa.areaName);
                        POINT_AREA_HASHMAP.put(pa.areaName,pa.id);
                    }
                }else{

                }
            }
        });
    }

    public static List<String> getInstallMehodeList(){
        List<String> list = new ArrayList<>();
        list.add("立杆");
        list.add("壁装");
        list.add("借杆");
        return list;
    }
    public static List<String> getArmHeightList(){
        List<String> list = new ArrayList<>();
        list.add("4m");
        list.add("4.5m");
        list.add("5m");
        list.add("5.5");
        list.add("6m");
        list.add("6.5");
        list.add("8m");
        list.add("9m");
        list.add("10m");
        list.add("12m");
        return list;
    }
    public static List<String> getArmLengthList(){
        List<String> list = new ArrayList<>();
        list.add("0.6m");
        list.add("0.8m");
        list.add("1m");
        list.add("1.2m");
        list.add("1.5m");
        list.add("2m");
        list.add("3m");
        return list;
    }
    public static List<String> getInstallHeightList(){
        List<String> list = new ArrayList<>();
        list.add("1.5M");
        list.add("2.0M");
        list.add("2.5M");
        list.add("3.0M");
        list.add("3.5M");
        list.add("4.0M");
        list.add("4.5M");
        list.add("5.0M");
        list.add("5.5M");
        list.add("6.0M");
        return list;
    }
    public static List<String> getIsShaderList(){
        List<String> list = new ArrayList<>();
        list.add("无");
        list.add("有");
        return list;
    }
    public static List<String> getIsLightList(){
        List<String> list = new ArrayList<>();
        list.add("是");
        list.add("否");
        return list;
    }
    public static List<String> getChargeMothodList(){
        List<String> list = new ArrayList<>();
        list.add("集中供电");
        list.add("就近取电");
        return list;
    }
    public static List<String> getKWEnvList(){
        List<String> list = new ArrayList<>();
        list.add("地砖");
        list.add("泥土");
        list.add("混泥土");
        list.add("沥青");
        return list;
    }
}
