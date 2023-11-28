package com.example.trabalho02;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class NotificationReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String mensagem = intent.getStringExtra("mensagem");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("task_channel_id2", "Nome do Canal", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
        exibirNotificacao(context,  mensagem);
    }

    private void exibirNotificacao(Context context, String conteudo) {
        NotificationChannel channel = new NotificationChannel("task_channel_id2",
                "hello",
                NotificationManager.IMPORTANCE_HIGH);
        NotificationManagerCompat manager = NotificationManagerCompat.from(context);
        manager.createNotificationChannel(channel);

        NotificationCompat.Builder notification = new NotificationCompat.Builder(context, "task_channel_id2");
        notification.setContentTitle("Você tem um lembrete");
        notification.setContentText(conteudo);
        notification.setSmallIcon(R.drawable.ic_launcher_foreground);

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.VIBRATE) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        manager.notify(121, notification.build());
    }
}