package com.fhzz.cn.exploremap.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.cunoraz.gifview.library.GifView;
import com.daimajia.swipe.SwipeLayout;
import com.fhzz.cn.exploremap.R;
import com.fhzz.cn.exploremap.dbbean.ExplorePoint;
import com.fhzz.cn.exploremap.entity.ResultCodeResp;
import com.fhzz.cn.exploremap.util.DBUtil;
import com.fhzz.cn.exploremap.util.LogUtil;
import com.fhzz.cn.exploremap.util.SPUtil;
import com.fhzz.cn.exploremap.value.ListDate;
import com.fhzz.cn.exploremap.value.PointParams;
import com.fhzz.cn.exploremap.value.StaticValues;
import com.google.gson.Gson;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.builder.PostFormBuilder;
import com.zhy.http.okhttp.callback.StringCallback;

import java.io.File;
import java.util.List;

import okhttp3.Call;

/**
 * Created by Administrator on 2016/10/20.
 */

public class DateQueryAdapter extends BaseAdapter {
    List<ExplorePoint> mLists;
    Context mContext;
    LayoutInflater mInflater;
    AMap mMap;
    PopupWindow mPopWindow;

    public DateQueryAdapter(Context context, List<ExplorePoint> lists, LayoutInflater inflater, AMap aMap, PopupWindow popupWindow){
        mContext =context;
        mLists = lists;
        mInflater = inflater;
        mMap = aMap;
        mPopWindow = popupWindow;
    }
    @Override
    public int getCount() {
        return (mLists == null) ? 0 : mLists.size();
    }

    @Override
    public Object getItem(int i) {
        return i;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder = null;
        if(view == null){
            view = mInflater.inflate(R.layout.item_date_listview,viewGroup,false);
            holder = new ViewHolder(view);
            view.setTag(holder);
        }
        holder = (ViewHolder) view.getTag();
        initDate(holder,i);
        return view;
    }
    public void initDate(ViewHolder holder , final int i){
        holder.item_tv_p_name.setText(mLists.get(i).pname);
        holder.item_tv_p_num.setText("编号: "+mLists.get(i).pnum);
        String camera = mLists.get(i).camera_type.equals("0") ? "球机" : "枪机";
        holder.item_tv_p_camera_type.setText("摄像头类型: " +camera);
        holder.item_tv_p_install_method.setText("安装方式: "+mLists.get(i).install_mode);
        holder.item_tv_p_area.setText("所属地区: "+mLists.get(i).parea);
        holder.item_tv_p_height.setText("杆高: "+mLists.get(i).install_height);
        holder.swipeLayout.setShowMode(SwipeLayout.ShowMode.PullOut);
        holder.swipeLayout.addDrag(SwipeLayout.DragEdge.Left,holder.bottom);
        if(mLists.get(i).is_submite == 1){
            /**已提交*/
            holder.item_img_submit.setVisibility(View.GONE);
        }
        holder.item_img_show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addMarker(mLists.get(i));
                if(mPopWindow != null && mPopWindow.isShowing()){
                    mPopWindow.dismiss();
                }
            }
        });
        holder.item_img_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitePoint(mLists.get(i));
            }
        });
        holder.item_img_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    static class ViewHolder{
        TextView item_tv_p_name;
        TextView item_tv_p_num;
        TextView item_tv_p_camera_type;
        TextView item_tv_p_install_method;
        TextView item_tv_p_area;
        TextView item_tv_p_height;

        ImageView item_img_show;
        ImageView item_img_submit;
        ImageView item_img_delete;

        LinearLayout surface,bottom;
        SwipeLayout swipeLayout;

        ViewHolder(View view){
            item_tv_p_name = (TextView) view.findViewById(R.id.item_tv_p_name);
            item_tv_p_num = (TextView) view.findViewById(R.id.item_tv_p_num);
            item_tv_p_camera_type = (TextView) view.findViewById(R.id.item_tv_p_camera_type);
            item_tv_p_install_method = (TextView) view.findViewById(R.id.item_tv_p_install_method);
            item_tv_p_area = (TextView) view.findViewById(R.id.item_tv_p_area);
            item_tv_p_height = (TextView) view.findViewById(R.id.item_tv_p_height);
            item_img_show = (ImageView) view.findViewById(R.id.item_img_show);
            item_img_submit = (ImageView) view.findViewById(R.id.item_img_submit);
            item_img_delete = (ImageView) view.findViewById(R.id.item_img_delete);

            surface = (LinearLayout) view.findViewById(R.id.linear_surface);
            bottom = (LinearLayout) view.findViewById(R.id.bottom_wrapper);
            swipeLayout = (SwipeLayout) view.findViewById(R.id.swipe);
        }
    }

    public void addMarker(ExplorePoint point){
        MarkerOptions options = new MarkerOptions();
        options.draggable(false);
        options.title(point.pname);
        options.position(new LatLng(point.lat,point.lon));
        LogUtil.d("adapter " + point.camera_type);
        if(("0").equals(point.camera_type)){
            if(("0").equals(point.explore_status)){
                //初堪
                options.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(mContext.getResources(),R.mipmap.map_dome_edit)));
            }else{
                //复堪
                options.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(mContext.getResources(),R.mipmap.map_dome)));
            }
        }else if(("1").equals(point.camera_type)){
            if(("0").equals(point.explore_status)){
                //初堪
                options.icon( BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(mContext.getResources(),R.mipmap.map_blot_edit)));
            }else{
                //复堪
                options.icon( BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(mContext.getResources(),R.mipmap.map_blot)));
            }
        }
        mMap.addMarker(options);
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(Double.valueOf(point.lat),Double.valueOf(point.lon)), 15));
    }

    public void submitePoint(final ExplorePoint p){
        showSubmitDialog();
        PostFormBuilder formBuilder = OkHttpUtils.post()
                .url(StaticValues.ACTION_MODIFY_POINT)
                .addParams(PointParams.PAREA, TextUtils.isEmpty(p.parea)? "": ListDate.POINT_AREA_HASHMAP.get(p.parea))
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
                .addParams(PointParams.PHONE, TextUtils.isEmpty(SPUtil.getString(mContext,StaticValues.NOW_LOGIN_PHONE))?"":SPUtil.getString(mContext,StaticValues.NOW_LOGIN_PHONE));
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
                        Toast.makeText(mContext,"in error",Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        ResultCodeResp resp = new Gson().fromJson(response,ResultCodeResp.class);
                        if(resp.code == 1000){
                           DBUtil.changeSubmit(p.id,1);
                        }else if(resp.code == 1004){
                            Toast.makeText(mContext,"添加失败,图片上传失败",Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(mContext,"添加失败",Toast.LENGTH_SHORT).show();
                        }
                        hideSubmitDialog();
                    }
                });
    }
    ProgressDialog submitProgressDialog = null;
    GifView gifView = null;
    public void showSubmitDialog(){
        View root = mInflater.inflate(R.layout.submit_dialog_style,null);
        submitProgressDialog =  ProgressDialog.show(mContext,"提交中...","",true,false, new DialogInterface.OnCancelListener(){
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
