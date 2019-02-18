package emdogan.projekt;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.NotificationCompat;

import static android.content.Context.NOTIFICATION_SERVICE;

@TargetApi(26)
public class MyBroadcastReceiver extends BroadcastReceiver {
    public int notificationID = 8;

    @Override
    public void onReceive(Context context, Intent intent) {

        Intent i = new Intent(context, MainActivity.class);

        i.putExtra("notificationID", notificationID);

        PendingIntent pendingIntent =
                PendingIntent.getActivity(context, 0, i, 0);

        long[] vibrate = new long[] { 100, 250, 100, 500};

        String NOTIFICATION_CHANNEL_ID = "my_channel_01";
        CharSequence channelName = "hr.math.karga.MYNOTIF";
        int importance = NotificationManager.IMPORTANCE_LOW;
        NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, importance);
        notificationChannel.enableLights(true);
        notificationChannel.setLightColor(Color.RED);
        notificationChannel.enableVibration(true);
        notificationChannel.setVibrationPattern(vibrate);

        NotificationManager nm = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);

        nm.createNotificationChannel(notificationChannel);

        Notification notif = new Notification.Builder(context, NOTIFICATION_CHANNEL_ID)
                .setContentTitle("Podsjetnik")
                .setContentText("Timer je završen")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setWhen(System.currentTimeMillis())
                .setShowWhen(true)
                .setContentIntent(pendingIntent)
                .setVibrate(vibrate)
                .setChannelId(NOTIFICATION_CHANNEL_ID)
                .build();

        nm.notify(notificationID, notif);
}


    /*

    // Anastasija(komentar): ova verzija radi na mojem laptopu (Android 7.0, API 24, Nexus 5X)

    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);

        Intent notificationIntent = new Intent(context, MainActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        long[] vibrate = new long[] { 100, 250, 100, 500};


        NotificationCompat.Builder mNotifyBuilder = new NotificationCompat.Builder(context)
                .setContentTitle("Podsjetnik")
                .setContentText("Timer je završen")
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.mipmap.ic_launcher)
                .setVibrate(vibrate)
                .setContentIntent(pendingIntent);

        notificationManager.notify(1, mNotifyBuilder.build());

    }
    */
}
