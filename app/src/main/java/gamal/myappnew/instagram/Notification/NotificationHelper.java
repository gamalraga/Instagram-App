package gamal.myappnew.instagram.Notification;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.util.Random;

import gamal.myappnew.instagram.R;

public class NotificationHelper extends ContextWrapper {

    public NotificationHelper(Context base) {
        super(base);
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.O)
        {
            createchannels();

        }
    }
    private String CHANEL_NAME="high priority channels";
    private String CHANEL_ID="com.example.notification"+CHANEL_NAME;
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createchannels()
    {

        NotificationChannel notificationChannel=new NotificationChannel(CHANEL_ID,CHANEL_NAME
        , NotificationManager.IMPORTANCE_HIGH);
        notificationChannel.enableLights(true);
        notificationChannel.enableVibration(true);
        notificationChannel.setDescription("This is Discription");
        notificationChannel.setLightColor(Color.LTGRAY);
        notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);

        NotificationManager manger= (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manger.createNotificationChannel(notificationChannel);

    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    public  void sendhightproirityNotification(String title, String body,Class c)
    {
        Intent intent=new Intent(this,c);
        PendingIntent intent1= PendingIntent.getActivity(this,267,intent,PendingIntent.FLAG_UPDATE_CURRENT);
        @SuppressLint("WrongConstant") Notification notification=new Notification.Builder(this,CHANEL_ID)
                .setContentTitle(title)
                .setContentText(body)
                .setSmallIcon(android.R.drawable.stat_notify_chat)
                .setContentIntent(intent1)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH)

                .build();
        NotificationManagerCompat.from(this).notify(new Random().nextInt(),notification);

    }
}
