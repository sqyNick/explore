package com.fhzz.cn.exploremap.service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.support.v7.app.NotificationCompat;
import android.text.TextUtils;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.fhzz.cn.exploremap.R;
import com.fhzz.cn.exploremap.activity.MainActivity;
import com.fhzz.cn.exploremap.dbbean.ExplorePoint;
import com.fhzz.cn.exploremap.entity.ResultCodeResp;
import com.fhzz.cn.exploremap.util.DBUtil;
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

public class SubmitPointsService extends Service {
    int submitSuccess = 0 ,submitError = 0,count = 0,SIZE = 0;
    public void submitPoints(){
        List<ExplorePoint> lists = DBUtil.findAllUnSubmitPoint();
        notificationManager = (NotificationManager) getBaseContext().getSystemService(NOTIFICATION_SERVICE);
        if(lists == null){
            Toast.makeText(getBaseContext(),"没有未提交任务",Toast.LENGTH_SHORT).show();
            notificationManager.cancel(StaticValues.SUBMIT_NOTIFYCATION);
            return;
        }
        SIZE = lists.size();
        for (final ExplorePoint p : lists){
            synchronized (this){
                submit(p);
            }
        }

        if(lists.size() == (submitError + submitSuccess)){
            builder.setTicker("上传完成");
            remoteViews.setTextViewText(R.id.tv_cancle,"成功上传:"+submitSuccess+"个点位;上传失败:"+submitError+"个点位");
            notificationManager.notify(StaticValues.SUBMIT_NOTIFYCATION,builder.build());
        }

    }

    public void updateNotifycation(int size){
        count ++;
        remoteViews.setTextViewText(R.id.tv_progress,(count / size) * 100 +"%");
        remoteViews.setProgressBar(R.id.progress_bar,size,count,false);
        notificationManager.notify(StaticValues.SUBMIT_NOTIFYCATION,builder.build());
        if(count >= size){
            remoteViews.setTextViewText(R.id.tv_cancle,"上传完成");
        }
    }
    public SubmitPointsService() {

    }

    @Override
    public void onCreate() {
        super.onCreate();
        NotifySubmitStatus();
        submitPoints();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public void submit(final ExplorePoint p){
        PostFormBuilder formBuilder = OkHttpUtils.post()
                .url(StaticValues.ACTION_MODIFY_POINT)
                .addParams(PointParams.PAREA,TextUtils.isEmpty(p.parea)? "": ListDate.POINT_AREA_HASHMAP.get(p.parea))
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
                        submitError++;
                        updateNotifycation(SIZE);
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        ResultCodeResp resp = new Gson().fromJson(response,ResultCodeResp.class);
                        if(resp.code == 1000){
                            p.point_id = resp.message;
                            DBUtil.changeSubmit(p.id,1);
                            if(DBUtil.savePoint(p)){
                                submitSuccess++;
                                updateNotifycation(SIZE);
                            }
                        }else{
                            submitError++;
                            updateNotifycation(SIZE);
                        }
                    }
                });
    }

    NotificationCompat.Builder builder = null;
    NotificationManager notificationManager = null;
    RemoteViews remoteViews = null;
    public void NotifySubmitStatus(){
        if(notificationManager == null){
            notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        }
        if(builder == null){
            builder = new NotificationCompat.Builder(this);
        }
        if(remoteViews == null){
            remoteViews = new RemoteViews(getPackageName(), R.layout.reomte_view);
        }
        builder.setAutoCancel(true);
        builder.setOngoing(true);
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setContent(remoteViews);
        builder.setTicker("开始上传");
        Intent it = new Intent();
        it.setAction(StaticValues.CANCLE_SUBMIT_ACTION);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getBaseContext(),0,it,0);
        remoteViews.setOnClickPendingIntent(R.id.tv_cancle,pendingIntent);
        remoteViews.setProgressBar(R.id.progress_bar,100,0,false);
        remoteViews.setTextViewText(R.id.tv_progress,"0%");
        builder.setLargeIcon(BitmapFactory.decodeResource(getResources(),R.mipmap.user));
        notificationManager.notify(StaticValues.SUBMIT_NOTIFYCATION,builder.build());

    }
}
