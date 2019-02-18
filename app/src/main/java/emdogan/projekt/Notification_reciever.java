package emdogan.projekt;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by anpeter on 2/18/19.
 */

public class Notification_reciever extends BroadcastReceiver {

    public int notificationID = 9;
    DBAdapter db;
    boolean flag = true;
    Context con;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onReceive(Context context, Intent intent) {

        con = context;
        String ispis = "";
        db =  new DBAdapter(context);

        Calendar calendar = Calendar.getInstance();

        calendar.add(Calendar.DAY_OF_YEAR, 1);
        int dan = calendar.get(Calendar.DAY_OF_MONTH);
        int mjesec = calendar.get(Calendar.MONTH);
        int godina = calendar.get(Calendar.YEAR);

        db.open();
        Cursor cursor = db.getOnDay2(godina, mjesec, dan);
        //ako za sutra nije nista upisano u kalendaru
        if(cursor.getCount() == 0) ispis = "Sutra nemate nikakvih obaveza. Kliknite ako ih želite dodati.";

        //ako je onda se ispise u obliku npr. kolokvij(12:00)
        if (cursor.moveToFirst())
        {
            do {
                String min;
                String sat;
                String obaveza;

                if (cursor.getInt(1) < 10)
                    min = "0" + cursor.getInt(1);
                else
                    min = String.valueOf(cursor.getInt(1));

                if (cursor.getInt(0) < 10)
                    sat = "0" + cursor.getInt(0);
                else
                    sat = String.valueOf(cursor.getInt(0));

                obaveza = cursor.getString(2);

                if(flag)
                {
                    ispis = obaveza + " (" + sat + ":" + min + ")";
                    flag = false;
                }
                else ispis += ", " + obaveza + " (" + sat + ":" + min + ")";

            } while (cursor.moveToNext());
        }
        db.close();

        Intent i = new Intent(context, Kalendar.class);

        i.putExtra("notificationID", notificationID);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, i, 0);

        long[] vibrate = new long[] { 100, 250, 100, 500};

        String NOTIFICATION_CHANNEL_ID = "my_channel_02";
        CharSequence channelName = "hr.math.karga.MYNOTIFI";
        int importance = NotificationManager.IMPORTANCE_LOW;
        NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, importance);
        notificationChannel.enableLights(true);
        notificationChannel.setLightColor(Color.RED);
        notificationChannel.enableVibration(true);
        notificationChannel.setVibrationPattern(vibrate);

        NotificationManager nm = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);

        nm.createNotificationChannel(notificationChannel);

        Notification notif = new Notification.Builder(context, NOTIFICATION_CHANNEL_ID)
                .setContentTitle("Sutrašnje obaveze")
                .setContentText(ispis)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setWhen(System.currentTimeMillis())
                .setShowWhen(true)
                .setContentIntent(pendingIntent)
                .setVibrate(vibrate)
                .setChannelId(NOTIFICATION_CHANNEL_ID)
                .build();

        nm.notify(notificationID, notif);
    }
}
