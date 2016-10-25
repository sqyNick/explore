package com.fhzz.cn.exploremap.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.PersistableBundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Property;
import android.view.Gravity;
import android.view.View;
import android.view.animation.AnimationSet;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.cunoraz.gifview.library.GifView;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.fhzz.cn.exploremap.R;
import com.fhzz.cn.exploremap.dbbean.ExplorePoint;
import com.fhzz.cn.exploremap.entity.ResultCodeResp;
import com.fhzz.cn.exploremap.util.DBUtil;
import com.fhzz.cn.exploremap.util.LogUtil;
import com.fhzz.cn.exploremap.util.SPUtil;
import com.fhzz.cn.exploremap.util.dpUtil;
import com.fhzz.cn.exploremap.value.ListDate;
import com.fhzz.cn.exploremap.value.PhotoConstant;
import com.fhzz.cn.exploremap.value.PointParams;
import com.fhzz.cn.exploremap.value.StaticValues;
import com.fhzz.cn.exploremap.view.TargetView;
import com.google.gson.Gson;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.animation.ValueAnimator;
import com.squareup.picasso.Picasso;
import com.wx.wheelview.adapter.ArrayWheelAdapter;
import com.wx.wheelview.widget.WheelView;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.builder.PostFormBuilder;
import com.zhy.http.okhttp.callback.FileCallBack;
import com.zhy.http.okhttp.callback.StringCallback;
import com.zhy.http.okhttp.request.PostFormRequest;
import com.zhy.http.okhttp.request.RequestCall;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;

public class InitPointInfoActivity extends AppCompatActivity implements View.OnClickListener{

    @ViewInject(R.id.img_back)
    ImageView img_back;
    @ViewInject(R.id.icon_camera_type)
    ImageView icon_camera_type;
    @ViewInject(R.id.linear_camera_type)
    LinearLayout linear_camera_type;
    @ViewInject(R.id.linear_point_area)
    LinearLayout linear_point_area;
    @ViewInject(R.id.tv_tg_btn)
    TextView tv_tg_btn;

    @ViewInject(R.id.p_lat)
    TextView p_lat;
    @ViewInject(R.id.p_lon)
    TextView p_lon;
    @ViewInject(R.id.img_install_capture)
    ImageView img_install_capture;
    @ViewInject(R.id.img_monitoring_perspective)
    ImageView img_monitoring_perspective;
    @ViewInject(R.id.img_install_capture_content)
    ImageView img_install_capture_content;
    @ViewInject(R.id.img_monitoring_perspective_content)
    ImageView img_monitoring_perspective_content;
    @ViewInject(R.id.rb_bolt_camera)
    RadioButton rb_bolt_camera;
    @ViewInject(R.id.rb_dome_camera)
    RadioButton rb_dome_camera;
    @ViewInject(R.id.linear_shader)
    LinearLayout linear_shader;
    @ViewInject(R.id.linear_whether_light)
    LinearLayout linear_whether_light;
    @ViewInject(R.id.linear_install_method)
    LinearLayout linear_install_method;
    @ViewInject(R.id.linear_bar_height)
    LinearLayout linear_bar_height;
    @ViewInject(R.id.linear_arm_length)
    LinearLayout linear_arm_length;
    @ViewInject(R.id.linear_install_height)
    LinearLayout linear_install_height;
    @ViewInject(R.id.tv_install_method_content)
    TextView tv_install_method_content;
    @ViewInject(R.id.tv_bar_height_content)
    TextView tv_bar_height_content;
    @ViewInject(R.id.tv_arm_length_content)
    TextView tv_arm_length_content;
    @ViewInject(R.id.tv_install_height_content)
    TextView tv_install_height_content;
    @ViewInject(R.id.tv_is_shade_content)
    TextView tv_is_shade_content;
    @ViewInject(R.id.tv_whether_light_content)
    TextView tv_whether_light_content;
    @ViewInject(R.id.btn_save)
    Button btn_save;
    @ViewInject(R.id.btn_submit)
    Button btn_submit;
    @ViewInject(R.id.img_point_map)
    ImageView img_point_map;
    @ViewInject(R.id.tv_point_area)
    TextView tv_point_area;
    @ViewInject(R.id.et_point_name)
    EditText et_point_name;
    @ViewInject(R.id.et_point_number)
    EditText et_point_number;
    @ViewInject(R.id.et_minitoring_scope)
    EditText et_minitoring_scope;
    @ViewInject(R.id.tv_excavation_env)
    TextView tv_excavation_env;
    @ViewInject(R.id.tv_power_access)
    TextView tv_power_access;
    @ViewInject(R.id.et_remarks)
    EditText et_remarks;

    @ViewInject(R.id.linear_exc_env)
    LinearLayout linear_exc_env;
    @ViewInject(R.id.linear_power_access)
    LinearLayout linear_power_access;




    /**CameraType LinearLayout的高度*/
    int LayoutCameraHeight;

    /**1 表示监控点为安装图 2表示监控视角图*/
    int captureType = -1;
    /**点位信息保存对象*/
    ExplorePoint point;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_init_point_info);
        ViewUtils.inject(this);
        init();
    }

    public void init(){
        initListener();
        /**初始化摄像头类型布局高度*/
        LayoutCameraHeight = linear_camera_type.getLayoutParams().height;
        initDate();
    }
    public void initDate(){
        Intent it = getIntent();
        if(it == null){
            return;
        }
        point = it.getParcelableExtra("point");

        p_lat.setText(String.valueOf(point.lat));
        p_lon.setText(String.valueOf(point.lon));
        if(point.camera_type.equals("0")){
            rb_dome_camera.setChecked(true);
        }else{
            rb_bolt_camera.setChecked(true);
        }
        if(!TextUtils.isEmpty(point.point_map)){
            Picasso.with(this)
                    .load(new File(point.point_map))
                    .resize(dpUtil.dip2px(this,48),dpUtil.dip2px(this,48))
                    .into(img_point_map);
        }
        if(TextUtils.isEmpty(point.explore_status)){
            tv_tg_btn.setText("初堪");
            point.explore_status = String.valueOf(0);
        }else if(point.explore_status.equals("0")){
            tv_tg_btn.setText("复堪");
            point.explore_status = String.valueOf(0);
        }else if(point.explore_status.equals("1")){
            tv_tg_btn.setText("复堪");
            tv_tg_btn.setVisibility(View.GONE);
            point.explore_status = String.valueOf(1);
        }

        tv_point_area.setText(TextUtils.isEmpty(point.parea)?"":point.parea);
        et_point_name.setText(TextUtils.isEmpty(point.pname)?"":point.pname);
        et_point_number.setText(TextUtils.isEmpty(point.pnum)?"":point.pnum);
        if(("0").equals(point.camera_type)){
            rb_dome_camera.setChecked(true);
        }else{
            rb_bolt_camera.setChecked(true);
        }
        tv_install_method_content.setText(TextUtils.isEmpty(point.install_mode)?"":point.install_mode);
        tv_bar_height_content.setText(TextUtils.isEmpty(point.arm_height)?"":point.arm_height);
        tv_arm_length_content.setText(TextUtils.isEmpty(point.arm_length)?"":point.arm_length);
        tv_install_height_content.setText(TextUtils.isEmpty(point.install_height)?"":point.install_height);
        et_minitoring_scope.setText(((TextUtils.isEmpty(point.watch_area)?"":point.watch_area)));
        tv_excavation_env.setText(TextUtils.isEmpty(point.minne_enviriment)?"":point.minne_enviriment);
        tv_power_access.setText(TextUtils.isEmpty(point.charge_method)?"":point.charge_method);
        tv_is_shade_content.setText(TextUtils.isEmpty(point.whether_shader)?"":point.whether_shader);
        tv_whether_light_content.setText(TextUtils.isEmpty(point.whether_light)?"":point.whether_light);
        et_remarks.setText(TextUtils.isEmpty(point.remarks)?"":point.remarks);

        if(!TextUtils.isEmpty(point.install_map)){
            Picasso.with(this)
                    .load(new File(point.install_map))
                    .resize(dpUtil.dip2px(this,48),dpUtil.dip2px(this,48))
                    .into(img_install_capture_content);
        }
        if(!TextUtils.isEmpty(point.whatch_angle_map)){
            Picasso.with(this)
                    .load(new File(point.whatch_angle_map))
                    .resize(dpUtil.dip2px(this,48),dpUtil.dip2px(this,48))
                    .into(img_monitoring_perspective_content);
        }
        if(point.is_submite == 1){
            btn_save.setVisibility(View.GONE);
        }
    }
    public void initListener(){
        icon_camera_type.setOnClickListener(this);
        img_back.setOnClickListener(this);
        linear_point_area.setOnClickListener(this);
        tv_tg_btn.setOnClickListener(this);
        img_install_capture.setOnClickListener(this);
        img_monitoring_perspective.setOnClickListener(this);
        linear_shader.setOnClickListener(this);
        linear_whether_light.setOnClickListener(this);
        linear_install_method.setOnClickListener(this);
        linear_bar_height.setOnClickListener(this);
        linear_arm_length.setOnClickListener(this);
        linear_install_height.setOnClickListener(this);
        btn_save.setOnClickListener(this);
        btn_submit.setOnClickListener(this);
        linear_exc_env.setOnClickListener(this);
        linear_power_access.setOnClickListener(this);
    }

    /**
     * 显示或隐藏摄像头类型
     * */
    public void showOrHideCameraTypeLayout(){
        if(linear_camera_type.getVisibility() == View.VISIBLE){
            icon_camera_type.setImageResource(R.mipmap.drop_down_32);
            ObjectAnimator objectAnimator = ObjectAnimator.ofInt(new TargetView(linear_camera_type),"height",0);
            objectAnimator.setDuration(400);
            objectAnimator.start();
            objectAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    if((Integer)animation.getAnimatedValue() == 0)
                        linear_camera_type.setVisibility(View.GONE);
                }
            });
        }else{
            icon_camera_type.setImageResource(R.mipmap.up_32);
            linear_camera_type.setVisibility(View.VISIBLE);
            ObjectAnimator objectAnimator = ObjectAnimator.ofInt(new TargetView(linear_camera_type),"height",LayoutCameraHeight);
            objectAnimator.setDuration(400);
            objectAnimator.start();
        }
    }
    View rootWheelView;
    public void showWheelView(String title, final List<String> lists,final View view){
        if(rootWheelView == null){
            rootWheelView = getLayoutInflater().inflate(R.layout.layout_wheel_view,null);
        }
        Button btn_sure = (Button) rootWheelView.findViewById(R.id.wheel_view_btn);
        TextView tv_title = (TextView) rootWheelView.findViewById(R.id.wheel_view_title);
        tv_title.setText(title);
        final WheelView wheelView = (WheelView) rootWheelView.findViewById(R.id.wheelview);
        wheelView.setWheelAdapter(new ArrayWheelAdapter(this)); // 文本数据源
        wheelView.setSkin(WheelView.Skin.Holo); // common皮肤
        ArrayList<String> nullList = new ArrayList<>();
        nullList.add("数据加载失败");
        wheelView.setWheelData((lists == null) ? nullList : lists);  // 数据集合
        final PopupWindow popupWindow = new PopupWindow(rootWheelView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT,true);
        popupWindow.setTouchable(true);
        popupWindow.setOutsideTouchable(false);
        popupWindow.setBackgroundDrawable(getResources().getDrawable(R.drawable.login_pop_window_bg_drawable));
        popupWindow.showAtLocation(linear_point_area,Gravity.BOTTOM,0,0);
        YoYo.with(Techniques.FadeInUp)
                .duration(500)
                .playOn(rootWheelView);
        btn_sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (view.getId()){
                    case R.id.linear_point_area:
                        tv_point_area.setText(lists.get(wheelView.getCurrentPosition()));
                        point.parea = lists.get(wheelView.getCurrentPosition());
                        break;
                    case R.id.linear_install_method:
                        tv_install_method_content.setText(lists.get(wheelView.getCurrentPosition()));
                        point.install_mode = lists.get(wheelView.getCurrentPosition());
                        break;
                    case R.id.linear_bar_height:
                        tv_bar_height_content.setText(lists.get(wheelView.getCurrentPosition()));
                        point.arm_height = lists.get(wheelView.getCurrentPosition());
                        break;
                    case R.id.linear_arm_length:
                        tv_arm_length_content.setText(lists.get(wheelView.getCurrentPosition()));
                        point.arm_length = lists.get(wheelView.getCurrentPosition());
                        break;
                    case R.id.linear_install_height:
                        tv_install_height_content.setText(lists.get(wheelView.getCurrentPosition()));
                        point.install_height = lists.get(wheelView.getCurrentPosition());
                        break;
                    case R.id.linear_exc_env:
                        tv_excavation_env.setText(lists.get(wheelView.getCurrentPosition()));
                        point.minne_enviriment = lists.get(wheelView.getCurrentPosition());
                        break;
                    case R.id.linear_power_access:
                        tv_power_access.setText(lists.get(wheelView.getCurrentPosition()));
                        point.charge_method = lists.get(wheelView.getCurrentPosition());
                        break;
                    case R.id.linear_shader:
                        tv_is_shade_content.setText(lists.get(wheelView.getCurrentPosition()));
                        point.whether_shader = lists.get(wheelView.getCurrentPosition());
                        break;
                    case R.id.linear_whether_light:
                        tv_whether_light_content.setText(lists.get(wheelView.getCurrentPosition()));
                        point.whether_light = lists.get(wheelView.getCurrentPosition());
                        break;
                }
                popupWindow.dismiss();
            }
        });
    }


    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
    }



    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.linear_point_area:
                showWheelView("点位区域",ListDate.POINT_AREA_LIST,view);
                break;
            case R.id.icon_camera_type:
                showOrHideCameraTypeLayout();
                break;
            case R.id.img_back:
                this.finish();
                break;
            case R.id.tv_tg_btn:
                if(tv_tg_btn.getText().toString().equals("初堪")){
                    tv_tg_btn.setText("复堪");
                    point.explore_status = String.valueOf(1);
                }else if(tv_tg_btn.getText().toString().equals("复堪")){
                    tv_tg_btn.setText("初堪");
                    point.explore_status = String.valueOf(0);
                }
                break;
            case R.id.img_install_capture:
                captureType = 1;
                Intent intent = new Intent(InitPointInfoActivity.this,
                        UseCameraActivity.class);
                startActivityForResult(intent, PhotoConstant.CAPTURE_REQUEST_CODE);
                break;
            case R.id.img_monitoring_perspective:
                captureType = 2;
                Intent it = new Intent(InitPointInfoActivity.this,
                        UseCameraActivity.class);
                startActivityForResult(it, PhotoConstant.CAPTURE_REQUEST_CODE);
                break;
            case R.id.linear_exc_env:
                showWheelView("开挖环境", ListDate.getKWEnvList(),view);
                break;
            case R.id.linear_power_access:
                showWheelView("取电方式", ListDate.getChargeMothodList(),view);
                break;
            case R.id.linear_shader:
                showWheelView("有无遮挡", ListDate.getIsShaderList(),view);
                break;
            case R.id.linear_whether_light:
                showWheelView("是否补光", ListDate.getIsLightList(),view);
                break;
            case R.id.linear_install_method:
                showWheelView("安装方式", ListDate.getInstallMehodeList(),view);
                break;
            case R.id.linear_bar_height:
                showWheelView("杆高", ListDate.getArmHeightList(),view);
                break;
            case R.id.linear_arm_length:
                showWheelView("横臂长度", ListDate.getArmLengthList(),view);
                break;
            case R.id.linear_install_height:
                showWheelView("安装高度", ListDate.getInstallHeightList(),view);
                break;
            case R.id.btn_save:
                initBasePointInfo();
                if(DBUtil.savePoint(point)){
                    Toast.makeText(getBaseContext(),"暂存成功",Toast.LENGTH_SHORT).show();
                    InitPointInfoActivity.this.finish();
                }else{
                    Toast.makeText(getBaseContext(),"暂存失败",Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btn_submit:
                initBasePointInfo();
                submitePoint(point);
                break;
        }
    }
    public void initBasePointInfo(){
        point.parea = tv_point_area.getText().toString().trim();
        point.pname = et_point_name.getText().toString().trim();
        point.pnum = et_point_number.getText().toString().trim();
        point.watch_area = et_minitoring_scope.getText().toString().trim();
        point.minne_enviriment = tv_excavation_env.getText().toString().trim();
        point.charge_method = tv_power_access.getText().toString().trim();
        point.remarks = et_remarks.getText().toString().trim();
    }
    File file;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(data == null){
            Toast.makeText(this,"获取图片失败",Toast.LENGTH_SHORT).show();
            return;
        }
        if(requestCode == PhotoConstant.CAPTURE_REQUEST_CODE){
            if (data.getData() != null) {
                file=new File(getAbsoluteImagePath(data.getData()));
                if(captureType == 1){
                    Picasso.with(this)
                            .load(data.getData())
                            .resize(dpUtil.dip2px(InitPointInfoActivity.this, 48),dpUtil.dip2px(InitPointInfoActivity.this, 48) )
                            .into(img_install_capture_content);
                    point.install_map = file.getAbsolutePath();
                }else if (captureType == 2){
                    Picasso.with(this)
                            .load(data.getData())
                            .resize(dpUtil.dip2px(InitPointInfoActivity.this, 48),dpUtil.dip2px(InitPointInfoActivity.this, 48) )
                            .into(img_monitoring_perspective_content);
                    point.whatch_angle_map = file.getAbsolutePath();
                }
                return;
            }
            String path = data.getStringExtra(PhotoConstant.IMAGE_PATH);
            if(TextUtils.isEmpty(path)){
                Toast.makeText(getBaseContext(),"路径为空,请重新拍照",Toast.LENGTH_SHORT).show();
                return;
            }
            file = new File(path);
            if(captureType == 1){
                Picasso.with(this)
                        .load(file)
                        .resize(dpUtil.dip2px(InitPointInfoActivity.this, 48),dpUtil.dip2px(InitPointInfoActivity.this, 48) )
                        .into(img_install_capture_content);
                point.install_map = file.getAbsolutePath();
            }else if (captureType == 2){
                Picasso.with(this)
                        .load(file)
                        .resize(dpUtil.dip2px(InitPointInfoActivity.this, 48),dpUtil.dip2px(InitPointInfoActivity.this, 48) )
                        .into(img_monitoring_perspective_content);
                point.whatch_angle_map = file.getAbsolutePath();
            }
        }
    }
    public  String getAbsoluteImagePath( Uri uri) {
        String imagePath = "";
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = this.managedQuery(uri, proj, // Which columns to return
                null, // WHERE clause; which rows to return (all rows)
                null, // WHERE clause selection arguments (none)
                null); // Order-by clause (ascending by name)

        if (cursor != null) {
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            if (cursor.getCount() > 0 && cursor.moveToFirst()) {
                imagePath = cursor.getString(column_index);
            }
            if (Build.VERSION.SDK_INT<14) {
                cursor.close();
            }
        }
        return imagePath;
    }

    public void submitePoint(ExplorePoint p){
        showSubmitDialog();
         PostFormBuilder formBuilder = OkHttpUtils.post()
                .url(StaticValues.ACTION_MODIFY_POINT)
                .addParams(PointParams.PAREA,TextUtils.isEmpty(p.parea)? "":ListDate.POINT_AREA_HASHMAP.get(p.parea))
                .addParams(PointParams.PNAME,TextUtils.isEmpty(p.pname)?"":p.pname)
                .addParams(PointParams.PNUM,TextUtils.isEmpty(p.pnum)?"":p.pnum)
                .addParams(PointParams.CAMERA_TYPE,TextUtils.isEmpty(p.camera_type)?"":p.camera_type)
                .addParams(PointParams.INSTALL_MODE,TextUtils.isEmpty(p.install_mode)?"":p.install_mode)
                .addParams(PointParams.ARM_HEIGHT,TextUtils.isEmpty(p.arm_height)?"":p.arm_height)
                .addParams(PointParams.ARM_LENGTH,TextUtils.isEmpty(p.arm_length)?"":p.arm_length)
                .addParams(PointParams.INSTALL_HEIGHT,TextUtils.isEmpty(p.install_height)?"":p.install_height)
                .addParams(PointParams.WATCH_AREA,TextUtils.isEmpty(p.watch_area)?"":p.watch_area)
                .addParams(PointParams.MINNE_ENVIRIMENT,TextUtils.isEmpty(p.minne_enviriment)?"":p.minne_enviriment)
                .addParams(PointParams.CHARGE_METHOD,TextUtils.isEmpty(p.charge_method)?"":p.charge_method)
                .addParams(PointParams.WHETHER_SHADER,TextUtils.isEmpty(p.whether_shader)?"":p.whether_shader)
                .addParams(PointParams.WHETHER_LIGHT,TextUtils.isEmpty(p.whether_light)?"":p.whether_light)
                .addParams(PointParams.REMARKS,TextUtils.isEmpty(p.remarks)?"":p.remarks)
                .addParams(PointParams.EXPLORE_STATUS,TextUtils.isEmpty(p.explore_status)?"":p.explore_status)
                .addParams(PointParams.LAT,String.valueOf(p.lat))
                .addParams(PointParams.LON,String.valueOf(p.lon))
                .addParams(PointParams.PHONE, TextUtils.isEmpty(SPUtil.getString(getBaseContext(),StaticValues.NOW_LOGIN_PHONE))?"":SPUtil.getString(getBaseContext(),StaticValues.NOW_LOGIN_PHONE));
                if(!TextUtils.isEmpty(p.point_map)){
                    formBuilder.addFile(PointParams.POINT_MAP,new File(p.point_map).getName(),new File(p.point_map));
                }
                if(!TextUtils.isEmpty(p.install_map)){
                    formBuilder.addFile(PointParams.INSTALL_MAP,new File(p.install_map).getName(),new File(p.install_map));
                }
                if(!TextUtils.isEmpty(p.whatch_angle_map)){
                    formBuilder.addFile(PointParams.WHATCH_ANGLE_MAP,new File(p.whatch_angle_map).getName(),new File(p.whatch_angle_map));
                }
        formBuilder.build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        e.printStackTrace();
                        hideSubmitDialog();
                        Toast.makeText(getBaseContext(),"in error",Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        ResultCodeResp resp = new Gson().fromJson(response,ResultCodeResp.class);
                        if(resp.code == 1000){
                            point.point_id = resp.message;
                            point.is_submite = 1;
                            if(DBUtil.savePoint(point)){
                                Toast.makeText(getBaseContext(),"本地保存成功",Toast.LENGTH_SHORT).show();
                            }
                        }else if(resp.code == 1004){
                            Toast.makeText(getBaseContext(),"添加失败,图片上传失败",Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(getBaseContext(),"添加失败",Toast.LENGTH_SHORT).show();
                        }
                        hideSubmitDialog();
                    }
                });
    }

    ProgressDialog submitProgressDialog = null;
    GifView gifView = null;
    public void showSubmitDialog(){
        View root = getLayoutInflater().inflate(R.layout.submit_dialog_style,null);
        submitProgressDialog =  ProgressDialog.show(this,"提交中...","",true,false, new DialogInterface.OnCancelListener(){
            @Override
            public void onCancel(DialogInterface dialogInterface) {


            }
        });
        gifView = (GifView) root.findViewById(R.id.gif1);
        submitProgressDialog.setContentView(root);
    }
    public void hideSubmitDialog(){
        if(submitProgressDialog != null && submitProgressDialog.isShowing()){
            if(gifView != null && gifView.isPlaying()){
                gifView.pause();
            }
            submitProgressDialog.dismiss();
        }
    }
}
