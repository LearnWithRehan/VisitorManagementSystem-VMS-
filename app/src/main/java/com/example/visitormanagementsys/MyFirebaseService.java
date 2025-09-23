package com.example.visitormanagementsys;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;
import java.util.Random;

public class MyFirebaseService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        if (remoteMessage.getData().size() > 0) {
            String name = remoteMessage.getData().get("name");
            String mobile = remoteMessage.getData().get("mobile");
            String address = remoteMessage.getData().get("address");
            String company = remoteMessage.getData().get("company");
            String purpose = remoteMessage.getData().get("purpose");
            String department = remoteMessage.getData().get("department");
            String employee = remoteMessage.getData().get("employee");
            String status = remoteMessage.getData().get("status");

            showVisitorNotification(name, mobile, address, company, purpose, department, employee, status);
        }
    }

    private void showVisitorNotification(String name, String mobile, String address,
                                         String company, String purpose, String department,
                                         String employee, String status) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "visitor_channel")
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle("Visitor " + status)
                .setContentText(name + " (" + mobile + ") - " + purpose)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText("Name: " + name + "\nMobile: " + mobile +
                                "\nAddress: " + address + "\nCompany: " + company +
                                "\nDepartment: " + department + "\nEmployee: " + employee +
                                "\nStatus: " + status))
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        NotificationManagerCompat manager = NotificationManagerCompat.from(this);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        manager.notify(new Random().nextInt(), builder.build());
    }
}
