package com.fhzz.cn.exploremap.dbbean;

import android.os.Parcel;
import android.os.Parcelable;

import com.lidroid.xutils.db.annotation.Column;
import com.lidroid.xutils.db.annotation.Id;

/**
 * Created by Administrator on 2016/9/27.
 */
public class ExplorePoint implements Parcelable {
    @Id
    public int id;
    @Column(column = "point_id")
    public String point_id;
    @Column(column = "parea")
    public String parea;
    @Column(column = "pname")
    public String pname;
    @Column(column = "pnum")
    public String pnum;
    /*
     * 0表示球机，1表示枪机
     * */
    @Column(column = "camera_type")
    public String camera_type;
    @Column(column = "install_mode")
    public String install_mode;
    @Column(column = "arm_height")
    public String arm_height;
    @Column(column = "arm_length")
    public String arm_length;
    @Column(column = "install_height")
    public String install_height;
    @Column(column = "watch_area")
    public String watch_area;
    @Column(column = "minne_enviriment")
    public String minne_enviriment;
    @Column(column = "charge_method")
    public String charge_method;
    @Column(column = "whether_shader")
    public String whether_shader;
    /*
     * 0 表示无补光，1表示有补光
     * */
    @Column(column = "whether_light")
    public String whether_light;
    @Column(column = "explore_date")
    public String explore_date;
    @Column(column = "remote")
    public String remarks;
    @Column(column = "point_map")
    public String point_map;
    @Column(column = "install_map")
    public String install_map;
    @Column(column = "whatch_angle_map")
    public String whatch_angle_map;
    @Column(column = "phone")
    public String phone;
    /*
     * 0 表示初堪 ，1表复堪 ，默认 -1
     * */
    @Column(column = "explore_status")
    public String explore_status;
    @Column(column = "lat")
    public double lat;
    @Column(column = "lon")
    public double lon;
    /**
     * 0 暂存 ， 1 提交
     * */
    @Column(column = "is_submite")
    public int is_submite;

    public ExplorePoint(){

    }
    public ExplorePoint(Parcel source){
        id = source.readInt();
        point_id = source.readString();
        parea = source.readString();
        pname = source.readString();
        pnum = source.readString();
        camera_type = source.readString();
        install_mode = source.readString();
        arm_height = source.readString();
        arm_length = source.readString();
        install_height = source.readString();
        watch_area = source.readString();
        minne_enviriment = source.readString();
        charge_method = source.readString();
        whether_shader = source.readString();
        whether_light = source.readString();
        explore_date = source.readString();
        remarks = source.readString();
        point_map = source.readString();
        install_map = source.readString();
        whatch_angle_map = source.readString();
        phone = source.readString();
        explore_status = source.readString();
        lat = source.readDouble();
        lon = source.readDouble();
        is_submite = source.readInt();
    }
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeString(point_id);
        parcel.writeString(parea);
        parcel.writeString(pname);
        parcel.writeString(pnum);
        parcel.writeString(camera_type);
        parcel.writeString(install_mode);
        parcel.writeString(arm_height);
        parcel.writeString(arm_length);
        parcel.writeString(install_height);
        parcel.writeString(watch_area);
        parcel.writeString(minne_enviriment);
        parcel.writeString(charge_method);
        parcel.writeString(whether_shader);
        parcel.writeString(whether_light);
        parcel.writeString(explore_date);
        parcel.writeString(remarks);
        parcel.writeString(point_map);
        parcel.writeString(install_map);
        parcel.writeString(whatch_angle_map);
        parcel.writeString(phone);
        parcel.writeString(explore_status);
        parcel.writeDouble(lat);
        parcel.writeDouble(lon);
        parcel.writeInt(is_submite);
    }
    public static Parcelable.Creator<ExplorePoint> CREATOR
            = new Parcelable.Creator<ExplorePoint>()
    {
        @Override
        public ExplorePoint createFromParcel(Parcel source) {
            return new ExplorePoint(source);
        }
        @Override
        public ExplorePoint[] newArray(int size) {
            return new ExplorePoint[size];
        }

    };
}
