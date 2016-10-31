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
import com.fhzz.cn.exploremap.util.ToastUtil;
import com.fhzz.cn.exploremap.value.BaseInfo;
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
        String lastLoginedUser = SPUtil.getString(this,StaticValues.LAST_LOGINED_USER);
        String lastLoginedPsd = SPUtil.getString(this,StaticValues.LAST_LOGINED_PSD);
        if(TextUtils.isEmpty(lastLoginedUser) ||  TextUtils.isEmpty(lastLoginedPsd)){
            startActivity(new Intent(SplashActivity.this,LoginActivity.class));
            SplashActivity.this.finish();
        }else{
            OkHttpUtils.get()
                    .addParams(PointParams.USER_NAME,lastLoginedUser.trim())
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
                                BaseInfo.USER_ID = resp.message;
                                SplashActivity.this.finish();
                            }else{
                                ToastUtil.show(getBaseContext(),"登录失败");
                                startActivity(new Intent(SplashActivity.this,LoginActivity.class));
                                SplashActivity.this.finish();
                            }
                        }
                    });
        }
    }

}
