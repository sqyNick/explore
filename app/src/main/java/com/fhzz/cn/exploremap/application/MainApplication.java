package com.fhzz.cn.exploremap.application;

import android.app.Application;

import com.fhzz.cn.exploremap.util.HttpUtil;


/**
 * Created by Administrator on 2016/9/27.
 */
public class MainApplication extends Application{
    @Override
    public void onCreate() {
        super.onCreate();
        HttpUtil.init();
    }

}
