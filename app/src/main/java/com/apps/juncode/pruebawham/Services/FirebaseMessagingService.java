package com.apps.juncode.pruebawham.Services;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.apps.juncode.pruebawham.Activities.MainActivity;
import com.apps.juncode.pruebawham.Model.Notif;
import com.apps.juncode.pruebawham.R;
import com.google.firebase.messaging.RemoteMessage;

/**
 * Created by Produccion 800N on 31/10/2017.
 */

public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService{

    private static final String TAG = "MessagingService";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        Log.d(TAG, "Desde: " + remoteMessage.getFrom());

        Notif notif = new Notif();
        notif.setId(remoteMessage.getFrom());
        notif.setTitulo(remoteMessage.getNotification().getTitle());
        notif.setDescripcion(remoteMessage.getNotification().getBody());

        showNotif(notif);
    }

    private void showNotif(Notif notificacion){
        Intent i = new Intent(this, MainActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, i, PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder notifBouilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.user_icon_2098873_960_720)
                .setContentTitle(notificacion.getTitulo())
                .setContentText(notificacion.getDescripcion())
                .setAutoCancel(true)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0, notifBouilder.build());
    }
}
