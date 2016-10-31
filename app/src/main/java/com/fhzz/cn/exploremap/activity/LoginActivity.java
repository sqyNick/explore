package com.fhzz.cn.exploremap.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.fhzz.cn.exploremap.R;
import com.fhzz.cn.exploremap.adapter.PopWindowListviewAdapter;
import com.fhzz.cn.exploremap.entity.ResultCodeResp;
import com.fhzz.cn.exploremap.util.LogUtil;
import com.fhzz.cn.exploremap.util.SPUtil;
import com.fhzz.cn.exploremap.util.ToastUtil;
import com.fhzz.cn.exploremap.value.BaseInfo;
import com.fhzz.cn.exploremap.value.PointParams;
import com.fhzz.cn.exploremap.value.StaticValues;
import com.google.gson.Gson;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.zhy.http.okhttp.OkHttpUtils;

import java.io.File;
import java.util.ArrayList;
import com.fhzz.cn.*;
import com.zhy.http.okhttp.callback.StringCallback;

import okhttp3.Call;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{

    @ViewInject(R.id.et_user_name)
    EditText et_user_name;
    @ViewInject(R.id.img_delete)
    ImageView img_delete;
    @ViewInject(R.id.img_drop_more)
    ImageView img_drop_more;
    @ViewInject(R.id.btn_login)
    Button btn_login;
    @ViewInject(R.id.login_linear_layout)
    LinearLayout login_linear_layout;

    @ViewInject(R.id.et_psd)
    EditText et_psd;

    PopupWindow popupWindow;
    Boolean show_drop = true;
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        init();
    }

    public void init(){
        ViewUtils.inject(this);
        initListener();
    }

    public void initListener(){
        img_delete.setOnClickListener(this);
        img_drop_more.setOnClickListener(this);
        btn_login.setOnClickListener(this);
        et_user_name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }
            @Override
            public void afterTextChanged(Editable editable) {
                if(TextUtils.isEmpty(editable.toString().trim())){
                    img_delete.setVisibility(View.INVISIBLE);
                    btn_login.setClickable(false);
                    btn_login.setBackground(getResources().getDrawable(R.drawable.login_btn_unclickable_shape));
                }else{
                    img_delete.setVisibility(View.VISIBLE);
                    btn_login.setClickable(true);
                    btn_login.setBackground(getResources().getDrawable(R.drawable.login_btn_shape));
                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.img_delete:
                et_user_name.setText(StaticValues.EMPTY_VALUE);
                break;
            case R.id.img_drop_more:
                if(show_drop){
                    show_drop = false;
                    img_drop_more.setImageResource(R.mipmap.jiantou_up);
                    showPopWindow(login_linear_layout);
                }else{
                    img_drop_more.setImageResource(R.mipmap.jiantou);
                    show_drop = true;
                    if(popupWindow.isShowing()){
                        popupWindow.dismiss();
                    }
                }
                break;
            case R.id.btn_login:
                String userName = et_user_name.getText().toString().trim();
                String psd = et_psd.getText().toString().trim();
                if(!TextUtils.isEmpty(userName) && !TextUtils.isEmpty(psd)){
                    showDialog();
                    LogRequest(userName,psd);
                }else{
                    ToastUtil.show(getBaseContext(),"信息填写不完整");
                }
                break;
        }
    }

    public void showPopWindow(ViewGroup viewGroup){
        View root = LayoutInflater.from(this).inflate(R.layout.login_pop_window,null);
        ListView listView = (ListView) root.findViewById(R.id.login_listview);
        final ArrayList<String> lists = new ArrayList<>();
        String spLoginedPhone = SPUtil.getString(this,StaticValues.LOGINED_USER);
        if(TextUtils.isEmpty(spLoginedPhone)){
        }else{
            if(spLoginedPhone.contains(StaticValues.SPLIT)){
                String[] array = spLoginedPhone.split(StaticValues.SPLIT);
                for(String s : array){
                    lists.add(s);
                }
            }else{
                lists.add(spLoginedPhone);
            }
        }
        PopWindowListviewAdapter adapter = new PopWindowListviewAdapter(lists,this,et_user_name.getText().toString().trim());
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                popupWindow.dismiss();
                et_user_name.setText(lists.get(i));
            }
        });
        popupWindow = new PopupWindow(root, viewGroup.getWidth(), LinearLayout.LayoutParams.WRAP_CONTENT,true);
        popupWindow.setTouchable(true);
        popupWindow.setOutsideTouchable(false);
        popupWindow.setBackgroundDrawable(getResources().getDrawable(R.drawable.login_pop_window_bg_drawable));
        popupWindow.showAsDropDown(viewGroup);
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                if(show_drop){
                    show_drop = false;
                    img_drop_more.setImageResource(R.mipmap.jiantou_up);
                    showPopWindow(login_linear_layout);
                }else{
                    img_drop_more.setImageResource(R.mipmap.jiantou);
                    show_drop = true;
                    if(popupWindow.isShowing()){
                        popupWindow.dismiss();
                    }
                }
            }
        });
    }

    public void showDialog(){
        progressDialog =  ProgressDialog.show(this,"登录中","",true,false, new OnCancelListener(){
            @Override
            public void onCancel(DialogInterface dialogInterface) {


            }
        });
    }

    public void LogRequest(final String userName,final String psd){
        String url = StaticValues.ACTION_LOGIN;
        OkHttpUtils
                .get()
                .url(url)
                .addParams(PointParams.USER_NAME, userName)
                .addParams(PointParams.PSD,psd)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        e.printStackTrace();
                        ToastUtil.show(getBaseContext(),"服务器异常");
                        progressDialog.dismiss();
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        ResultCodeResp resp = new Gson().fromJson(response, ResultCodeResp.class);
                        progressDialog.dismiss();
                        if(resp.code == 1000){
                            startActivity(new Intent(LoginActivity.this,MainActivity.class));
                            SPUtil.putLoginedPhone(getBaseContext(),userName);
                            SPUtil.put(getBaseContext(),StaticValues.LAST_LOGINED_USER,userName);
                            SPUtil.put(getBaseContext(),StaticValues.NOW_LOGIN_USER,userName);
                            SPUtil.put(getBaseContext(),StaticValues.LAST_LOGINED_PSD,psd);
                            BaseInfo.USER_ID = resp.message;
                            LoginActivity.this.finish();
                        }else if(resp.code == 1005){
                            ToastUtil.show(getBaseContext(),"用户未授权");
                        }else if(resp.code == 1006){
                            ToastUtil.show(getBaseContext(),"密码错误");
                        }
                    }
                });

//

    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        return super.onTouchEvent(event);
    }
}
