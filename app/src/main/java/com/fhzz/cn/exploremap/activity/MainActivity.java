package com.fhzz.cn.exploremap.activity;

import android.animation.ObjectAnimator;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.services.geocoder.GeocodeAddress;
import com.amap.api.services.geocoder.GeocodeQuery;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.fhzz.cn.exploremap.R;
import com.fhzz.cn.exploremap.adapter.DateQueryAdapter;
import com.fhzz.cn.exploremap.dbbean.ExplorePoint;
import com.fhzz.cn.exploremap.entity.QueryPointResp;
import com.fhzz.cn.exploremap.service.SubmitPointsService;
import com.fhzz.cn.exploremap.util.DBUtil;
import com.fhzz.cn.exploremap.util.SPUtil;
import com.fhzz.cn.exploremap.util.ToastUtil;
import com.fhzz.cn.exploremap.util.dpUtil;
import com.fhzz.cn.exploremap.value.BaseInfo;
import com.fhzz.cn.exploremap.value.ListDate;
import com.fhzz.cn.exploremap.value.PointParams;
import com.fhzz.cn.exploremap.value.StaticValues;
import com.fhzz.cn.exploremap.view.XListView;
import com.google.gson.Gson;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.nineoldandroids.animation.Animator;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionButton;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionMenu;
import com.oguzdev.circularfloatingactionmenu.library.SubActionButton;
import com.wx.wheelview.adapter.ArrayWheelAdapter;
import com.wx.wheelview.widget.WheelView;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import okhttp3.Call;



public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    MapView mapView;
    AMap aMap;
    UiSettings uiSettings;
    @ViewInject(R.id.draw_layout)
    DrawerLayout drawerLayout;

    @ViewInject(R.id.menu)
    ImageView menu;
    @ViewInject(R.id.tv_add_explore_point)
    TextView tv_add_explore_point;
    @ViewInject(R.id.tv_search_point)
    TextView tv_search_point;
    @ViewInject(R.id.tv_display_point)
    TextView tv_display_point;
    @ViewInject(R.id.img_search_icon)
    ImageView img_search_icon;
    @ViewInject(R.id.tv_search_input)
    TextView tv_search_input;

    @ViewInject(R.id.linear)
    LinearLayout linear;
    @ViewInject(R.id.tv_search_condition)
    TextView tv_search_condition;

    @ViewInject(R.id.et_search_content)
    EditText et_search_content;

    @ViewInject(R.id.submit_all_saved_points)
    LinearLayout submit_all_saved_points;
    @ViewInject(R.id.btn_switch_user)
    Button btn_switch_user;
    /**
     * 默认进入时操作页面
     * 1  勘点录入
     * 2  数据查询
     * 3  点位展示
     */
    int STATUS = 1;
    /**
     * 1  枪机
     * 0  球机
     */
    int MARKER_TYPE = -1;
    /**地图marker的类型
     * 1 代表待添加的点
     * 2 代表搜索出的点
     * */
    int MAP_MARKER_STATUE = 1;



    FloatingActionButton actionButton;
    FloatingActionMenu actionMenu;

    double lat,lon;

    AMapLocationClient aMapLocationClient = null;
    AMapLocationClientOption aMapLocationClientOption = null;
    /**定位监听*/
    AMapLocationListener aMapLocationListener = new AMapLocationListener() {
        @Override
        public void onLocationChanged(AMapLocation aMapLocation) {
            if(aMapLocation != null){
                if(aMapLocation.getErrorCode() == 0){
                    //定位成功回调信息，设置相关消息
                    aMapLocation.getLocationType();//获取当前定位结果来源，如网络定位结果，详见定位类型表
                    aMapLocation.getLatitude();//获取纬度
                    aMapLocation.getLongitude();//获取经度
                    aMapLocation.getAccuracy();//获取精度信息
                    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Date date = new Date(aMapLocation.getTime());
                    df.format(date);//定位时间
                    aMapLocation.getAddress();//地址，如果option中设置isNeedAddress为false，则没有此结果，网络定位结果中会有地址信息，GPS定位不返回地址信息。
                    aMapLocation.getCountry();//国家信息
                    aMapLocation.getProvince();//省信息
                    aMapLocation.getCity();//城市信息
                    aMapLocation.getDistrict();//城区信息
                    aMapLocation.getStreet();//街道信息
                    aMapLocation.getStreetNum();//街道门牌号信息
                    aMapLocation.getCityCode();//城市编码
                    aMapLocation.getAdCode();//地区编码
                    aMapLocation.getAoiName();//获取当前定位点的AOI信息
                    lat = aMapLocation.getLatitude();
                    lon = aMapLocation.getLongitude();
                    LatLng latLng = new LatLng(lat,lon);
                    addCameraMarker(MARKER_TYPE,latLng);
                    ToastUtil.show(getBaseContext(),"定位成功 ："+aMapLocation.getCity() + ":"+aMapLocation.getStreet());
                    aMapLocationClient.stopLocation();
                    hideLocationDialog();
                }else{
                    ToastUtil.show(getBaseContext(),"定位失败 ："+aMapLocation.getErrorCode() + ":"+aMapLocation.getErrorInfo());
                    hideLocationDialog();
                }
            }
        }
    };


    public void initLocation(){
        aMapLocationClient = new AMapLocationClient(this);
        aMapLocationClient.setLocationListener(aMapLocationListener);

        //初始化定位参数
        aMapLocationClientOption = new AMapLocationClientOption();
        //设置定位模式为高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
        aMapLocationClientOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //设置是否返回地址信息（默认返回地址信息）
        aMapLocationClientOption.setNeedAddress(true);
        //设置是否只定位一次,默认为false
        aMapLocationClientOption.setOnceLocation(false);
        //设置是否强制刷新WIFI，默认为强制刷新
        aMapLocationClientOption.setWifiActiveScan(true);
        //设置是否允许模拟位置,默认为false，不允许模拟位置
        aMapLocationClientOption.setMockEnable(false);
        //设置定位间隔,单位毫秒,默认为2000ms
        aMapLocationClientOption.setInterval(2000);
        //给定位客户端对象设置定位参数
        aMapLocationClient.setLocationOption(aMapLocationClientOption);
        //启动定位
        aMapLocationClient.startLocation();
    }
    GeocodeSearch geocodeSearch;
    GeocodeQuery geocodeQuery;
    public void initGeoSearch(String address){
        geocodeSearch = new GeocodeSearch(this);
        geocodeQuery = new GeocodeQuery(address,"武汉");
        geocodeSearch.getFromLocationNameAsyn(geocodeQuery);
        geocodeSearch.setOnGeocodeSearchListener(new GeocodeSearch.OnGeocodeSearchListener() {
            @Override
            public void onRegeocodeSearched(RegeocodeResult regeocodeResult, int i) {

            }

            @Override
            public void onGeocodeSearched(GeocodeResult geocodeResult, int i) {
                if (i == 1000) {
                    if (geocodeResult != null && geocodeResult.getGeocodeAddressList() != null
                            && geocodeResult.getGeocodeAddressList().size() > 0) {
                        GeocodeAddress address = geocodeResult.getGeocodeAddressList().get(0);
                        aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                                new LatLng(address.getLatLonPoint().getLatitude(),address.getLatLonPoint().getLongitude()), 15));
//                        geoMarker.setPosition(AMapUtil.convertToLatLng(address
//                                .getLatLonPoint()));
//                        addressName = "经纬度值:" + address.getLatLonPoint() + "\n位置描述:"
//                                + address.getFormatAddress();

                        DisplayMetrics dm = new DisplayMetrics();
                        getWindowManager().getDefaultDisplay().getMetrics(dm);
                        int width = dm.widthPixels;
                        int height = dm.heightPixels;
                        LatLng centerLatlng = aMap.getProjection().fromScreenLocation(new Point(width/2,height/2));
                        LatLng leftTop = aMap.getProjection().fromScreenLocation(new Point(0,0));
                        LatLng rightBottom = aMap.getProjection().fromScreenLocation(new Point(width,height));
                        queryPoint(String.valueOf(centerLatlng.latitude),String.valueOf(centerLatlng.longitude),
                                String.valueOf(leftTop.latitude),String.valueOf(leftTop.longitude),
                                String.valueOf(rightBottom.latitude),String.valueOf(rightBottom.longitude));
                    } else {
                        ToastUtil.show(getBaseContext(),"no result "+ i);
                    }
                } else {
                    ToastUtil.show(getBaseContext(),"error "+ i);
                }
            }
        });
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init(savedInstanceState);
    }
    public void init(Bundle savedInstanceState){
        ViewUtils.inject(this);
        initMap(savedInstanceState);
        initView();
        initListener();
        initDB();
        ListDate.init();
    }
    public void initListener(){
        tv_add_explore_point.setOnClickListener(this);
        tv_search_point.setOnClickListener(this);
        tv_display_point.setOnClickListener(this);
        menu.setOnClickListener(this);
        img_search_icon.setOnClickListener(this);
        submit_all_saved_points.setOnClickListener(this);
        tv_search_input.setOnClickListener(this);
        tv_search_condition.setOnClickListener(this);
        btn_switch_user.setOnClickListener(this);
    }
    public void initDB(){
        DBUtil.init(this, SPUtil.getString(this, StaticValues.NOW_LOGIN_USER));
    }
    private void initView(){
        initAddExplorePointFAB();
    }
    ExplorePoint point;
    private void initMap(Bundle savedInstanceState){
        mapView = (MapView) findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);// 此方法必须重写
        aMap = mapView.getMap();
        aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(StaticValues.DEFAULT_LOCATION_LAT,StaticValues.DEFAULT_LOCATION_LON),15));
        uiSettings = aMap.getUiSettings();
        uiSettings.setZoomControlsEnabled(false);
        aMap.setOnMarkerClickListener(new AMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(final Marker marker) {
                if(MAP_MARKER_STATUE == StaticValues.TO_ADD_MARKER){ //添加的marker点击
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle("确定去录入该点位信息？");
                    builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });
                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            aMap.getMapScreenShot(new AMap.OnMapScreenShotListener() {
                                @Override
                                public void onMapScreenShot(Bitmap bitmap) {

                                }

                                @Override
                                public void onMapScreenShot(Bitmap bitmap, int i) {
                                    if(bitmap == null){
                                        progressDialog.dismiss();
                                        ToastUtil.show(getBaseContext(),"获取地图失败");
                                        return;
                                    }else{
                                        Intent it = new Intent(MainActivity.this,InitPointInfoActivity.class);
                                        point = new ExplorePoint();
                                        point.lat = marker.getPosition().latitude;
                                        point.lon = marker.getPosition().longitude;
                                        point.camera_type =MARKER_TYPE+"";
                                        it.putExtra("point",point);
                                        point.point_map = getMapShortScreen(bitmap);
                                        progressDialog.dismiss();
                                        startActivity(it);
                                    }
                                }
                            });
                            showDialog();
                        }
                    });
                    builder.create().show();
                }else if(MAP_MARKER_STATUE == StaticValues.SEARCHED_MARKER){//搜索的marker点击

                }
                return false;
            }
        });
        aMap.setOnMarkerDragListener(new AMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker marker) {

            }

            @Override
            public void onMarkerDrag(Marker marker) {

            }

            @Override
            public void onMarkerDragEnd(Marker marker) {

            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        mapView.onDestroy();
        if(aMapLocationClient != null){
            aMapLocationClient.onDestroy();
        }
    }
    ProgressDialog progressDialog;
    public void showDialog(){
        progressDialog =  ProgressDialog.show(this,"截取地图中","",true,false, new DialogInterface.OnCancelListener(){
            @Override
            public void onCancel(DialogInterface dialogInterface) {


            }
        });
    }
    public String getMapShortScreen(Bitmap bitmap){
        String sdStatus = Environment.getExternalStorageState();
        boolean success = false;
        if (Environment.MEDIA_UNMOUNTED.equals(sdStatus)) {

        }
        File dbTopDirDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + StaticValues.APP_DIRECTORY + "/" + SPUtil.getString(getBaseContext(),StaticValues.NOW_LOGIN_USER) + "/map");
        if (!dbTopDirDir.exists()) {
            dbTopDirDir.mkdirs();
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        File map = new File(dbTopDirDir.getAbsolutePath() + "/" + sdf.format(new Date()) + ".png");
        try {
            FileOutputStream fos = new FileOutputStream(map);
            success = bitmap.compress(Bitmap.CompressFormat.PNG,100,fos);
            fos.flush();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if(success){
                return map.getAbsolutePath();
            }
        }
        return map.getAbsolutePath();
    }
    @Override
    protected void onStop() {
        super.onStop();
        if(aMapLocationClient != null && aMapLocationClient.isStarted()){
            aMapLocationClient.stopLocation();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView.onResume ()，实现地图生命周期管理
        mapView.onResume();
    }
    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView.onPause ()，实现地图生命周期管理
        mapView.onPause();
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //在activity执行onSaveInstanceState时执行mMapView.onSaveInstanceState (outState)，实现地图生命周期管理
        mapView.onSaveInstanceState(outState);
    }
    public void initAddExplorePointFAB(){
        if(actionButton != null){
            actionButton.detach();
        }
        ImageView icon = new ImageView(this); // Create an icon
        icon.setImageDrawable(getResources().getDrawable(R.mipmap.icon_add));
        actionButton = new FloatingActionButton.Builder(this)
                .setContentView(icon)
                .build();
        int redActionButtonSize = getResources().getDimensionPixelSize(R.dimen.my_action_btn_size);
        FloatingActionButton.LayoutParams starParams = new FloatingActionButton.LayoutParams(redActionButtonSize, redActionButtonSize);
        DisplayMetrics metric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metric);
        starParams.setMargins(metric.widthPixels-dpUtil.dip2px(this,72),metric.heightPixels-dpUtil.dip2px(this,150),0,0);
        actionButton.setLayoutParams(starParams);

        SubActionButton.Builder itemBuilder = new SubActionButton.Builder(this);
        ImageView itemIcon = new ImageView(this);
        itemIcon.setImageDrawable( getResources().getDrawable(R.mipmap.icon_gun_video) );
        SubActionButton boltCamera = itemBuilder.setContentView(itemIcon).build();
        boltCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //marker的类型
                MARKER_TYPE = StaticValues.BLOT_CAMEAR;
                //marker的状态，1代表添加的marker，2代表搜索出的marker
                MAP_MARKER_STATUE = StaticValues.TO_ADD_MARKER;
                //显示定位对话框
                showLocationDialog();
                if(aMapLocationClient == null){
                    initLocation();
                }else{
                    aMapLocationClient.startLocation();
                }
                actionMenu.close(true);
            }
        });

        SubActionButton.Builder itemBuilder2 = new SubActionButton.Builder(this);
        ImageView itemIcon2 = new ImageView(this);
        itemIcon2.setImageDrawable( getResources().getDrawable(R.mipmap.icon_circle_video) );
        SubActionButton domeCamera = itemBuilder.setContentView(itemIcon2).build();
        domeCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //marker的类型
                MARKER_TYPE = StaticValues.DOME_CAMERA;
                //marker的状态，1代表添加的marker，2代表搜索出的marker
                MAP_MARKER_STATUE = StaticValues.TO_ADD_MARKER;
                //显示定位对话框
                showLocationDialog();
                if(aMapLocationClient == null){
                    initLocation();
                }else{
                    aMapLocationClient.startLocation();
                }
                actionMenu.close(true);
            }
        });
        actionMenu = new FloatingActionMenu.Builder(this)
                .addSubActionView(boltCamera)
                .addSubActionView(domeCamera)
                .attachTo(actionButton)
                .build();
        showFAB();
        actionMenu.setStateChangeListener(new FloatingActionMenu.MenuStateChangeListener() {
            AnimationSet animationSet;
            RotateAnimation rotateAnimation;
            @Override
            public void onMenuOpened(FloatingActionMenu menu) {
                animationSet = new AnimationSet(true);
                rotateAnimation = new RotateAnimation(0,45,Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
                rotateAnimation.setDuration(400);
                animationSet.addAnimation(rotateAnimation);
                animationSet.setFillAfter(true);
                actionButton.startAnimation(animationSet);
            }

            @Override
            public void onMenuClosed(FloatingActionMenu menu) {
                rotateAnimation = new RotateAnimation(0,45,Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
                rotateAnimation.setDuration(400);
                animationSet.addAnimation(rotateAnimation);
                animationSet.setFillAfter(true);
                actionButton.startAnimation(animationSet);
            }
        });

    }

    public void clearMap(){
        aMap.clear();
    }

    public void addCameraMarker(int type,LatLng latLng){
        MarkerOptions markerOptions = new MarkerOptions();
        switch (type){
            case StaticValues.BLOT_CAMEAR: // 枪机
                clearMap();
                markerOptions = new MarkerOptions();
                markerOptions.position(latLng);
                markerOptions.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(),R.mipmap.map_blot)));
                markerOptions.draggable(true);
                Marker blotMarker = aMap.addMarker(markerOptions);
                break;
            case StaticValues.DOME_CAMERA://球机
                clearMap();
                markerOptions.position(latLng);
                markerOptions.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(),R.mipmap.map_dome)));
                markerOptions.draggable(true);
                Marker domeMarker = aMap.addMarker(markerOptions);
                break;
        }
        aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,19));
    }

    ProgressDialog locationDialog;
    public void showLocationDialog(){
            locationDialog =  ProgressDialog.show(this,"定位中...","",true,false, new DialogInterface.OnCancelListener(){
                @Override
                public void onCancel(DialogInterface dialogInterface) {

                }
            });
    }
    public void hideLocationDialog(){
        if(locationDialog != null && locationDialog.isShowing()){
            locationDialog.dismiss();
        }
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.tv_add_explore_point:
                hideSearchLayout();
                hideDateQueryWindow();
                clearMap();
                if(actionMenu.isOpen())
                    actionMenu.close(true);
                if(STATUS == 1){

                }else{
                    initAddExplorePointFAB();
                    STATUS = 1;
                    tv_add_explore_point.setTextColor(getResources().getColor(R.color.white));
                    tv_add_explore_point.setBackgroundColor(getResources().getColor(R.color.blue));
                    tv_search_point.setTextColor(getResources().getColor(R.color.textColor));
                    tv_search_point.setBackgroundColor(getResources().getColor(R.color.white));
                    tv_display_point.setTextColor(getResources().getColor(R.color.textColor));
                    tv_display_point.setBackgroundColor(getResources().getColor(R.color.white));
                }
                break;
            case R.id.tv_search_point:
                clearMap();
                showDateQueryWindow();
                MAP_MARKER_STATUE = StaticValues.SEARCHED_MARKER;
                if(actionMenu.isOpen())
                    actionMenu.close(true);
                if(STATUS == 2){

                }else{
                    STATUS = 2;
                    hideFAB();
                    hideSearchLayout();
                    tv_search_point.setTextColor(getResources().getColor(R.color.white));
                    tv_search_point.setBackgroundColor(getResources().getColor(R.color.blue));
                    tv_add_explore_point.setTextColor(getResources().getColor(R.color.textColor));
                    tv_add_explore_point.setBackgroundColor(getResources().getColor(R.color.white));
                    tv_display_point.setTextColor(getResources().getColor(R.color.textColor));
                    tv_display_point.setBackgroundColor(getResources().getColor(R.color.white));
                }
                break;
            case R.id.tv_display_point:
                clearMap();
                if(actionMenu.isOpen())
                    actionMenu.close(true);
                if(STATUS == 3){

                }else{
                    STATUS = 3;
                    hideFAB();
                    hideDateQueryWindow();
                    showSearchLayout();
                    tv_display_point.setTextColor(getResources().getColor(R.color.white));
                    tv_display_point.setBackgroundColor(getResources().getColor(R.color.blue));
                    tv_search_point.setTextColor(getResources().getColor(R.color.textColor));
                    tv_search_point.setBackgroundColor(getResources().getColor(R.color.white));
                    tv_add_explore_point.setTextColor(getResources().getColor(R.color.textColor));
                    tv_add_explore_point.setBackgroundColor(getResources().getColor(R.color.white));
                }
                break;
            case R.id.menu:
                drawerLayout.openDrawer(Gravity.LEFT);
                break;
            case R.id.img_search_icon:
                MAP_MARKER_STATUE = StaticValues.SEARCHED_MARKER;
                String searchCondition = tv_search_condition.getText().toString().trim();
                if("区域".equals(searchCondition)){
                    String searchContent = tv_search_input.getText().toString().trim();
                    if(TextUtils.isEmpty(searchContent)){
                        queryPoint(null,null,null,null,null,null);
                    }else{
                        findPoints(searchContent,null,null,null);
                    }
                }else if(searchCondition.contains("点位名称")){
                    String searchContent = et_search_content.getText().toString().trim();
                    ToastUtil.show(getBaseContext(),searchContent);
                    if(TextUtils.isEmpty(searchContent)){
                        queryPoint(null,null,null,null,null,null);
                    }else{
                        findPoints(null,searchContent,null,null);
                    }
                }else if("设备类型".equals(searchCondition)){
                    String searchContent = et_search_content.getText().toString().trim();
                    if(TextUtils.isEmpty(searchContent)){
                        queryPoint(null,null,null,null,null,null);
                    }else{
                        if(searchContent.contains("球")){
                            findPoints(null,null,"0",null);
                        }else if(searchContent.contains("枪")){
                            findPoints(null,null,"1",null);
                        }else{
                            ToastUtil.show(getBaseContext(),"无此设备类型");
                        }
                    }
                }else if("编号".equals(searchCondition)){
                    String searchContent = et_search_content.getText().toString().trim();
                    if(TextUtils.isEmpty(searchContent)){
                        queryPoint(null,null,null,null,null,null);
                    }else{
                        findPoints(null,null,null,searchContent);
                    }
                }

//                if(TextUtils.isEmpty(searchContent)){
//                    /**查询条件为空，则查询所有*/
////                    queryPoint(null,null,null,null,null,null);
//                }else{
////                    initGeoSearch(searchContent);
////                    queryPointByArea(searchContent);
//
//                }
                break;
            case R.id.tv_search_input:
                showWheelView("输入区域",ListDate.POINT_AREA_LIST,tv_search_input);
                break;
            case R.id.submit_all_saved_points:
                List<ExplorePoint> points = DBUtil.findAllUnSubmitPoint();
                if(points == null || points.size() == 0){
                    ToastUtil.show(getBaseContext(),"没有未提交点位数据");
                    drawerLayout.closeDrawer(Gravity.LEFT);
                    return;
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("是否一键提交暂存数据，该过程可能会消耗较多时间");
                builder.setPositiveButton("提交", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        drawerLayout.closeDrawer(Gravity.LEFT);
                        startService(new Intent(MainActivity.this, SubmitPointsService.class));
                    }
                });
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                builder.create().show();
                break;
            case R.id.tv_search_condition:
                showSearchConditionPopwindow(tv_search_condition);
                break;
            case R.id.btn_switch_user:
                startActivity(new Intent(MainActivity.this,LoginActivity.class));
                this.finish();
                break;
        }
    }
    PopupWindow searchConditionPopwindow = null;
    View contentView;
    public void showSearchConditionPopwindow(View parent){
        if(contentView == null){
            contentView = getLayoutInflater().inflate(R.layout.search_condition_popwindow,null);
        }
        if(searchConditionPopwindow == null){
            searchConditionPopwindow = new PopupWindow(contentView,
                    parent.getLayoutParams().width, LinearLayout.LayoutParams.WRAP_CONTENT,true);
            searchConditionPopwindow.setBackgroundDrawable(getResources().getDrawable(R.drawable.search_condation_popwindow_style));
        }
        searchConditionPopwindow.showAsDropDown(parent);

        final TextView tvArea = (TextView) contentView.findViewById(R.id.tvArea);
        final TextView tvName = (TextView) contentView.findViewById(R.id.tvName);
        final TextView tvType = (TextView) contentView.findViewById(R.id.tvType);
        final TextView tvNum = (TextView) contentView.findViewById(R.id.tvNum);

        tvArea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tv_search_condition.setText(tvArea.getText());
                tv_search_input.setText(StaticValues.EMPTY_VALUE);
                hideSearchConditionPopwindow();
                tv_search_input.setVisibility(View.VISIBLE);
                et_search_content.setVisibility(View.GONE);
            }
        });
        tvName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tv_search_condition.setText(tvName.getText());
                hideSearchConditionPopwindow();
                tv_search_input.setVisibility(View.GONE);
                et_search_content.setVisibility(View.VISIBLE);
            }
        });
        tvType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tv_search_condition.setText(tvType.getText());
                hideSearchConditionPopwindow();
                tv_search_input.setVisibility(View.GONE);
                et_search_content.setVisibility(View.VISIBLE);
            }
        });
        tvNum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tv_search_condition.setText(tvNum.getText());
                hideSearchConditionPopwindow();
                tv_search_input.setVisibility(View.GONE);
                et_search_content.setVisibility(View.VISIBLE);
            }
        });

    }
    public void hideSearchConditionPopwindow(){
        if(searchConditionPopwindow != null && searchConditionPopwindow.isShowing()){
            searchConditionPopwindow.dismiss();
        }
    }

    public void queryPoint(String lat ,String lon,String topLeft,String topRight,String bottomLeft,String bottomRight){
        if(!TextUtils.isEmpty(lat) && !TextUtils.isEmpty(lon)){
            aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                    new LatLng(Double.valueOf(lat),Double.valueOf(lon)), 12));
        }
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        LatLng leftTop = aMap.getProjection().fromScreenLocation(new Point(0,0));
        LatLng rightBottom = aMap.getProjection().fromScreenLocation(new Point(width,height));
        OkHttpUtils.post()
                .url(StaticValues.ACTION_QUERY_POINT)
                .addParams(PointParams.USER_ID,TextUtils.isEmpty(BaseInfo.USER_ID)?"":BaseInfo.USER_ID)
                .addParams(PointParams.LAT,TextUtils.isEmpty(lat)?"":lat)
                .addParams(PointParams.LON,TextUtils.isEmpty(lon)?"":lon)
                .addParams("topLat",TextUtils.isEmpty(String.valueOf(leftTop.latitude))?"":String.valueOf(leftTop.latitude))
                .addParams("topLon",TextUtils.isEmpty(String.valueOf(leftTop.longitude))?"":String.valueOf(leftTop.longitude))
                .addParams("bottomLat",TextUtils.isEmpty(String.valueOf(rightBottom.latitude))?"":String.valueOf(rightBottom.latitude))
                .addParams("bottomLon",TextUtils.isEmpty(String.valueOf(rightBottom.longitude))?"":String.valueOf(rightBottom.longitude))
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        ToastUtil.show(getBaseContext(),"服务器异常");
                    }
                    @Override
                    public void onResponse(String response, int id) {
                        QueryPointResp resp = new Gson().fromJson(response,QueryPointResp.class);
                        if(resp.code == 1000){
                            aMap.clear();
                            ExplorePoint[] points = resp.points;
                            if(points.length > 1){
                                aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                                        new LatLng(Double.valueOf(points[0].lat),Double.valueOf(points[0].lon)), 12));
                            }else{

                            }
                            for(ExplorePoint point : points){
                                MarkerOptions options = new MarkerOptions();
                                options.draggable(false);
                                options.title(point.pname);
                                options.position(new LatLng(point.lat,point.lon));
                                if(point.camera_type.equals("0")){
                                    if(point.explore_status.equals("0")){
                                        //初勘
                                        options.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(),R.mipmap.map_dome_edit)));
                                    }else{
                                        //复勘
                                        options.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(),R.mipmap.map_dome)));
                                    }
                                }else if(point.camera_type.equals("1")){
                                    if(point.explore_status.equals("0")){
                                        //初勘
                                        options.icon( BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(),R.mipmap.map_blot_edit)));
                                    }else{
                                        //复勘
                                        options.icon( BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(),R.mipmap.map_blot)));
                                    }
                                }
                                Marker marker = aMap.addMarker(options);
                            }

                        }else if(resp.code == 1001){
                            ToastUtil.show(getBaseContext(),"参数为空");
                        }else {
                            ToastUtil.show(getBaseContext(),"没有查询到数据");
                        }
                    }
                });
    }
    public void queryPointByArea(String area){
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        OkHttpUtils.post()
                .url(StaticValues.ACTION_QUREY_POINT_AREA_BY_LIST)
                .addParams(PointParams.USER_ID,TextUtils.isEmpty(BaseInfo.USER_ID)?"":BaseInfo.USER_ID)
                .addParams(PointParams.PAREAID,ListDate.POINT_AREA_HASHMAP.get(area))
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        ToastUtil.show(getBaseContext(),"服务器异常");
                    }
                    @Override
                    public void onResponse(String response, int id) {
                        QueryPointResp resp = new Gson().fromJson(response,QueryPointResp.class);
                        if(resp.code == 1000){
                            aMap.clear();
                            ExplorePoint[] points = resp.points;
                            if(points.length > 1){
                                aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                                        new LatLng(Double.valueOf(points[0].lat),Double.valueOf(points[0].lon)), 12));
                            }else{
                                ToastUtil.show(getBaseContext(),"没有查询结果");
                            }
                            for(ExplorePoint point : points){
                                MarkerOptions options = new MarkerOptions();
                                options.draggable(false);
                                options.title(point.pname);
                                options.position(new LatLng(point.lat,point.lon));
                                if(point.camera_type.equals("0")){
                                    if(point.explore_status.equals("0")){
                                        //初勘
                                        options.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(),R.mipmap.map_dome_edit)));
                                    }else{
                                        //复勘
                                        options.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(),R.mipmap.map_dome)));
                                    }
                                }else if(point.camera_type.equals("1")){
                                    if(point.explore_status.equals("0")){
                                        //初勘
                                        options.icon( BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(),R.mipmap.map_blot_edit)));
                                    }else{
                                        //复勘
                                        options.icon( BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(),R.mipmap.map_blot)));
                                    }
                                }
                                Marker marker = aMap.addMarker(options);
                            }
                        }else if(resp.code == 1001){
                            ToastUtil.show(getBaseContext(),"参数为空");
                        }else {
                            ToastUtil.show(getBaseContext(),"没有查询到数据");
                        }
                    }
                });
    }
    public void showSearchLayout(){
        linear.setVisibility(View.VISIBLE);
        YoYo.with(Techniques.SlideInDown)
                .duration(500)
                .playOn(findViewById(R.id.linear));
    }
    public void hideSearchLayout(){
        if(linear.getVisibility() == View.GONE){
            return;
        }
        YoYo.with(Techniques.SlideOutUp)
                .duration(500)
                .interpolate(new AccelerateDecelerateInterpolator())
                .withListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        linear.setVisibility(View.GONE);
                        YoYo.with(Techniques.SlideInUp)
                                .duration(500)
                                .playOn(menu);
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                })
                .playOn(findViewById(R.id.linear));
    }

    public void findPoints(String area,String pname,String cameraType,String pnum){
        OkHttpUtils.post()
                .url(StaticValues.ACTION_FIND_POINT)
                .addParams(PointParams.USER_ID,TextUtils.isEmpty(BaseInfo.USER_ID)?"":BaseInfo.USER_ID)
                .addParams(PointParams.PAREAID,TextUtils.isEmpty(ListDate.POINT_AREA_HASHMAP.get(area))?"":ListDate.POINT_AREA_HASHMAP.get(area))
                .addParams(PointParams.PNAME,TextUtils.isEmpty(pname)?"":pname)
                .addParams(PointParams.CAMERA_TYPE,TextUtils.isEmpty(cameraType)?"":cameraType)
                .addParams(PointParams.PNUM,TextUtils.isEmpty(pnum)?"":pnum)
                .addParams(PointParams.PAGE,"1")
                .addParams(PointParams.ROWS,"0")
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        ToastUtil.show(getBaseContext(),"服务器异常");
                    }
                    @Override
                    public void onResponse(String response, int id) {
                        QueryPointResp resp = new Gson().fromJson(response,QueryPointResp.class);
                        if(resp.code == 1000){
                            aMap.clear();
                            ExplorePoint[] points = resp.points;
                            if(points.length > 1){
                                aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                                        new LatLng(Double.valueOf(points[0].lat),Double.valueOf(points[0].lon)), 12));
                            }else{
                                ToastUtil.show(getBaseContext(),"没有查询结果");
                            }
                            for(ExplorePoint point : points){
                                MarkerOptions options = new MarkerOptions();
                                options.draggable(false);
                                options.title(point.pname);
                                options.position(new LatLng(point.lat,point.lon));
                                if(point.camera_type.equals("0")){
                                    if(point.explore_status.equals("0")){
                                        //初勘
                                        options.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(),R.mipmap.map_dome_edit)));
                                    }else{
                                        //复勘
                                        options.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(),R.mipmap.map_dome)));
                                    }
                                }else if(point.camera_type.equals("1")){
                                    if(point.explore_status.equals("0")){
                                        //初勘
                                        options.icon( BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(),R.mipmap.map_blot_edit)));
                                    }else{
                                        //复勘
                                        options.icon( BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(),R.mipmap.map_blot)));
                                    }
                                }
                                Marker marker = aMap.addMarker(options);
                            }
                        }else if(resp.code == 1001){
                            ToastUtil.show(getBaseContext(),"参数为空");
                        }else {
                            ToastUtil.show(getBaseContext(),"没有查询到数据");
                        }
                    }
                });
    }

    public void showFAB(){
        YoYo.with(Techniques.SlideInRight)
                .duration(500)
                .playOn(actionButton);
    }
    public void hideFAB(){
        if(! actionButton.isAttachedToWindow()){
            return;
        }
        YoYo.with(Techniques.SlideOutRight)
                .duration(500)
                .withListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        actionButton.detach();
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                })
                .playOn(actionButton);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_toolbar,menu);
        return true;
    }
    PopupWindow dateQueryWindow = null;
    XListView listView;
    DateQueryAdapter adapter;
    List<ExplorePoint> pointLists;
    TextView switch_view;
    View emptyView = null;
    public void showDateQueryWindow(){
        if(dateQueryWindow != null && dateQueryWindow.isShowing()){
            return;
        }
        View root = getLayoutInflater().inflate(R.layout.pop_window_date_query,null);
        emptyView =root.findViewById(R.id.empty_view);
        listView = (XListView) root.findViewById(R.id.date_query_listview);
        switch_view = (TextView) root.findViewById(R.id.switch_view);
        LinearLayout linearSwicth = (LinearLayout) root.findViewById(R.id.linear_switch);
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        dateQueryWindow = new PopupWindow(root,(dm.widthPixels / 10) * 8 , (dm.heightPixels / 10) * 7,true);
        dateQueryWindow.setTouchable(true);
        dateQueryWindow.setOutsideTouchable(false);
        dateQueryWindow.setBackgroundDrawable(getResources().getDrawable(R.drawable.date_query_pop_window_bg_style));
        dateQueryWindow.showAtLocation(menu,Gravity.CENTER,0,0);
        YoYo.with(Techniques.ZoomInDown)
                .duration(400)
                .playOn(root);
        pointLists = new ArrayList<>();
        pointLists = DBUtil.getPointByPage(0,StaticValues.QUERY_DATE_PAGE_SIZE,StaticValues.UN_SUBMIT_POINT);
        adapter = new DateQueryAdapter(this,pointLists,getLayoutInflater(),aMap,dateQueryWindow);
        listView.setPullRefreshEnable(false);
        listView.setPullLoadEnable(true);
        listView.setAdapter(adapter);
        listView.setEmptyView(emptyView);
        loadMoreTask(StaticValues.UN_SUBMIT_POINT);
        linearSwicth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(switch_view.getText().equals("未提交")){
                    ObjectAnimator oa = ObjectAnimator.ofFloat(switch_view, "translationX", switch_view.getTranslationX(),switch_view.getTranslationX() +switch_view.getWidth());
                    oa.setDuration(400);
                    oa.start();
                    switch_view.setText("已提交");
                    pointLists = null;
                    pointLists = DBUtil.getPointByPage(0,StaticValues.QUERY_DATE_PAGE_SIZE,StaticValues.SUBMITED_POINT);
                    adapter = new DateQueryAdapter(getBaseContext(),pointLists,getLayoutInflater(),aMap,dateQueryWindow);
                    listView.setAdapter(adapter);
                    listView.setEmptyView(emptyView);
                    listView.setPullLoadEnable(true);
                    loadMoreTask(StaticValues.SUBMITED_POINT);
                }else{
                    ObjectAnimator oa = ObjectAnimator.ofFloat(switch_view, "translationX", switch_view.getTranslationX(),switch_view.getTranslationX() -switch_view.getWidth());
                    oa.setDuration(400);
                    oa.start();
                    switch_view.setText("未提交");
                    pointLists = null;
                    pointLists = DBUtil.getPointByPage(0,StaticValues.QUERY_DATE_PAGE_SIZE,StaticValues.UN_SUBMIT_POINT);
                    adapter = new DateQueryAdapter(getBaseContext(),pointLists,getLayoutInflater(),aMap,dateQueryWindow);
                    listView.setAdapter(adapter);
                    listView.setEmptyView(emptyView);
                    listView.setPullLoadEnable(true);
                    loadMoreTask(StaticValues.UN_SUBMIT_POINT);
                }
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent it = new Intent(MainActivity.this,InitPointInfoActivity.class);
                it.putExtra("point",pointLists.get(i-1));
                startActivity(it);
                hideDateQueryWindow();
            }
        });


    }
    public void hideDateQueryWindow(){
        if(dateQueryWindow != null && dateQueryWindow.isShowing()){
            dateQueryWindow.dismiss();
        }
    }

    long currentTime = 0;
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (System.currentTimeMillis() - currentTime > 2000) {
                currentTime = System.currentTimeMillis();
                ToastUtil.show(this,"再按一次退出");
            } else {
                this.finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void loadMoreTask(final int submitStatus)
    {
        listView.setXListViewListener(new XListView.IXListViewListener() {

            @Override
            public void onRefresh() {

            }

            @Override
            public void onLoadMore() {
                List<ExplorePoint> points=DBUtil.getPointByPage(pointLists.size(), 10,submitStatus );
                if(points != null && points.size()>0)
                {
                    pointLists.addAll(points);
                    adapter.notifyDataSetChanged();
                }else {
                    ToastUtil.show(getBaseContext(), "没有更多数据");
                    listView.setPullLoadEnable(false);
                }
                listView.stopLoadMore();
            }
        });
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
        popupWindow.showAtLocation(view,Gravity.BOTTOM,0,0);
        YoYo.with(Techniques.FadeInUp)
                .duration(500)
                .playOn(rootWheelView);
        btn_sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv_search_input.setText(lists.get(wheelView.getCurrentPosition()));
                popupWindow.dismiss();
            }
        });
    }

}
