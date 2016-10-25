package com.fhzz.cn.exploremap.activity;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import com.fhzz.cn.exploremap.R;
import com.fhzz.cn.exploremap.entity.ResultCodeResp;
import com.fhzz.cn.exploremap.util.LogUtil;
import com.fhzz.cn.exploremap.util.SPUtil;
import com.fhzz.cn.exploremap.value.PointParams;
import com.fhzz.cn.exploremap.value.StaticValues;
import com.google.gson.Gson;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.Callback;
import com.zhy.http.okhttp.callback.StringCallback;

import okhttp3.Call;
import okhttp3.Response;


public class SplashActivity extends AppCompatActivity {
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };
    Runnable r = new Runnable() {
        @Override
        public void run() {
            LogRequest();
//            startActivity(new Intent(SplashActivity.this,InitPointInfoActivity.class));
//            SplashActivity.this.finish();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        init();
    }

    public void init(){
        handler.postDelayed(r, StaticValues.SPLASH_TIME);
    }
    public void LogRequest(){
        String lastLoginedPhone = SPUtil.getString(this,StaticValues.LAST_LOGINED_PHONE);
        String lastLoginedPsd = SPUtil.getString(this,StaticValues.LAST_LOGINED_PSD);
        if(TextUtils.isEmpty(lastLoginedPhone) ||  TextUtils.isEmpty(lastLoginedPsd)){
            startActivity(new Intent(SplashActivity.this,LoginActivity.class));
            SplashActivity.this.finish();
        }else{
            LogUtil.d("TAG","lastLoginedPhone ----  " +lastLoginedPhone);
            OkHttpUtils.get()
                    .addParams(PointParams.PHONE,lastLoginedPhone.trim())
                    .addParams(PointParams.PSD,lastLoginedPsd)
                    .url(StaticValues.ACTION_LOGIN)
                    .build()
                    .execute(new StringCallback() {
                        @Override
                        public void onError(Call call, Exception e, int id) {
                            Toast.makeText(getBaseContext(),"服务器异常，请重新登录",Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(SplashActivity.this,LoginActivity.class));
                            SplashActivity.this.finish();
                        }

                        @Override
                        public void onResponse(String response, int id) {
                            ResultCodeResp resp = new Gson().fromJson(response,ResultCodeResp.class);
                            if(resp.code == 1000){
                                startActivity(new Intent(SplashActivity.this,MainActivity.class));
                                SplashActivity.this.finish();
                            }else{
                                Toast.makeText(getBaseContext(),"登录失败",Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(SplashActivity.this,LoginActivity.class));
                                SplashActivity.this.finish();
                            }
                        }
                    });
        }
    }

}
