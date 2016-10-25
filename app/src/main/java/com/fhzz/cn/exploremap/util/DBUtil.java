package com.fhzz.cn.exploremap.util;

import android.content.Context;
import android.os.Environment;

import com.fhzz.cn.exploremap.R;
import com.fhzz.cn.exploremap.dbbean.ExplorePoint;
import com.fhzz.cn.exploremap.value.StaticValues;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.db.sqlite.WhereBuilder;
import com.lidroid.xutils.exception.DbException;

import java.io.File;
import java.util.List;

/**
 * Created by Administrator on 2016/9/27.
 */
public class DBUtil {

    public static DbUtils dbUtils;
    /**
     * 创建数据库
     *@return 返回数据库操作类
     */
    public static DbUtils create(Context context,String phone){
        String sdStatus = Environment.getExternalStorageState();
        if (Environment.MEDIA_UNMOUNTED.equals(sdStatus)) {
            return DbUtils.create(context);
        }
        File dbTopDirDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + StaticValues.APP_DIRECTORY + "/" + phone + "/db");
        if (!dbTopDirDir.exists()) {
            dbTopDirDir.mkdirs();
        }
        LogUtil.d(dbTopDirDir.getAbsolutePath());
        return DbUtils.create(context, dbTopDirDir.getAbsolutePath() ,  StaticValues.DB_NAME);
    }

    public static void init(Context context,String phone){
        dbUtils = DBUtil.create(context,phone);
        try{
            dbUtils.createTableIfNotExist(ExplorePoint.class);
        }catch (DbException e){
            e.printStackTrace();
        }
    }

    public static boolean savePoint(ExplorePoint point){
        try {
            dbUtils.saveOrUpdate(point);
            return true;
        } catch (DbException e) {
            e.printStackTrace();
            return false;
        }
    }
    public static boolean changeSubmit(int id,int submitStatus){
        try {
            ExplorePoint point = dbUtils.findFirst(Selector.from(ExplorePoint.class).where("id", "==", id));
            if (null != point) {
                point.is_submite = submitStatus;
                dbUtils.saveOrUpdate(point);
                return  true;
            }
        } catch (DbException e) { e.printStackTrace();}
        return false;
    }

    public static List<ExplorePoint> getPointByPage(int pageIndex,int pageSize,int submitStatus)
    {
        try {
            return dbUtils.findAll(Selector.from(ExplorePoint.class).where("is_submite", "==", submitStatus).orderBy("id", true).limit(pageSize).offset(pageIndex));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<ExplorePoint> findAllUnSubmitPoint(){
        try {
            return dbUtils.findAll(Selector.from(ExplorePoint.class).where("is_submite","==",StaticValues.UN_SUBMIT_POINT));
        } catch (DbException e) {
            e.printStackTrace();
            return null;
        }
    }
}
