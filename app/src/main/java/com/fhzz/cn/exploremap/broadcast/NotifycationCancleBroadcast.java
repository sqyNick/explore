package com.fhzz.cn.exploremap.broadcast;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.fhzz.cn.exploremap.service.SubmitPointsService;
import com.fhzz.cn.exploremap.value.StaticValues;

import static android.content.Context.NOTIFICATION_SERVICE;

/**
 * Created by Administrator on 2016/10/24.
 */

public class NotifycationCancleBroadcast extends BroadcastReceiver {
    NotificationManager notificationManager;
    @Override
    public void onReceive(Context context, Intent intent) {
        notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancel(StaticValues.SUBMIT_NOTIFYCATION);
        context.stopService(new Intent(context,SubmitPointsService.class));
    }

}
