package com.cheaptravel.newpostService;

import android.app.IntentService;
import android.app.Notification;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.cheaptravel.R;
import com.google.firebase.storage.StorageReference;

import static com.cheaptravel.newpostService.App.CHANNEL_ID;

public class ChangeAvatarService extends IntentService {
    private StorageReference mStorage;
    private static final String TAG = "ExampleIntentService2";


    public ChangeAvatarService(String name) {
        super(name);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "onCreate");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setContentTitle("Travel")
                    .setContentText("Posting...")
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                    .build();
            Log.i(TAG, "noti");


            startForeground(2, notification);
        }


    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

    }
}
