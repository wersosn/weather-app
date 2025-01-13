package com.example.projektsm.ui;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.activity.result.ActivityResultLauncher;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;


import com.example.projektsm.R;

public class Notifications {
    private final Context context;
    private final ActivityResultLauncher<String> permissionLauncher;
    private UI UI;

    public Notifications(Context context, ActivityResultLauncher<String> permissionLauncher) {
        this.context = context;
        this.permissionLauncher = permissionLauncher;
        UI = new UI(context);
    }

    public void showWeatherNotification(String cityName, String temperature, String feelsLikeTemp, String iconCode) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                CharSequence name = context.getString(R.string.app_name);
                String description = "Notifications";
                int importance = NotificationManager.IMPORTANCE_DEFAULT;
                NotificationChannel channel = new NotificationChannel("weather_channel", name, importance);
                channel.setDescription(description);
                notificationManager.createNotificationChannel(channel);
            }
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "weather_channel")
                .setSmallIcon(UI.getWeatherIcon(iconCode))
                .setContentTitle(context.getString(R.string.notif_title, cityName))
                .setContentText(context.getString(R.string.notif_desc, temperature, feelsLikeTemp))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        notificationManager.notify(1001, builder.build());
    }
}
