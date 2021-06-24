package com.example.shoppingapp;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.ContextWrapper;

import androidx.core.app.NotificationCompat;

public class NotificationHelper  extends ContextWrapper {

    public static final String channelID = "channel1_ID";
    public static final String channelName = "channel1_Name";
    private NotificationManager notificationManager;


    public NotificationHelper(Context base) {
        super(base);
        createChannels();
    }

    private void createChannels() {
        NotificationChannel channel1 = new NotificationChannel(channelID,channelName, NotificationManager.IMPORTANCE_DEFAULT);
        channel1.enableLights(true);
        channel1.setLightColor(R.color.blue);
        channel1.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        getManager().createNotificationChannel(channel1);
    }

    public NotificationManager getManager(){
        if(notificationManager==null){
            notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return  notificationManager;
    }

    public NotificationCompat.Builder getChannelNotification(String title,String message){
        return new NotificationCompat.Builder(getApplicationContext(), channelID)
                .setContentTitle(title)
                .setContentText(" Not for Shopping: "+message)
                .setSmallIcon(R.drawable.ic_baseline_add_alert_24);
    }



}
