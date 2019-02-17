package emdogan.projekt;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

public class MyBroadcastReceiver extends BroadcastReceiver {

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
}
