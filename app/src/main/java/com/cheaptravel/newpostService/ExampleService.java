package com.cheaptravel.newpostService;

import android.app.IntentService;
import android.app.Notification;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.PowerManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.bumptech.glide.Glide;
import com.cheaptravel.MainActivity;
import com.cheaptravel.R;
import com.cheaptravel.model.Post;
import com.cheaptravel.model.User;
import com.cheaptravel.ulti.Constants;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import static com.cheaptravel.newpostService.App.CHANNEL_ID;

public class ExampleService extends IntentService {
    private String nameUser;
    private String uriAvatar;
    private static final String TAG = "ExampleIntentService";
    private StorageReference mStorage;

    public ExampleService() {
        super("ExampleIntentService");
        setIntentRedelivery(true);
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


            startForeground(1, notification);
        }


    }

    @Override
    protected void onHandleIntent(@Nullable final Intent intent) {
        Log.i(TAG, "onHandleIntent");

        final ArrayList<String> listNameImage = intent.getStringArrayListExtra("listNameImage");
        final ArrayList<String> listUriRespon = intent.getStringArrayListExtra("listUriRespon");
        final ArrayList<Uri> listFileUri = intent.getParcelableArrayListExtra("listFileUri");
        final String keyUser = intent.getStringExtra("keyUser");
        final String content = intent.getStringExtra("content");
        final String location = intent.getStringExtra("location");
        final String homestay = intent.getStringExtra("homestay");
        Query mDatabase = FirebaseDatabase.getInstance().getReference(Constants.KEY_USER).orderByKey().equalTo(keyUser);
        Log.e("keyUser", keyUser);
        mDatabase.addValueEventListener(valueEventListener);


        for (int i = 0; i < listNameImage.size(); i++) {
            mStorage = FirebaseStorage.getInstance().getReference();

            final StorageReference fileToUpload = mStorage.child("Images").child(listNameImage.get(i));
            fileToUpload.putFile(listFileUri.get(i)).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {

                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    fileToUpload.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            listUriRespon.add(uri.toString());
                            Log.e("url", uri.toString() + "\n");

                            if (listUriRespon.size() == listNameImage.size()) {
                                Post post = new Post();
                                FirebaseDatabase database = FirebaseDatabase.getInstance();
                                DatabaseReference myRef = database.getReference("Post");
                                post.setImages(listUriRespon);
                                post.setNameUser(nameUser);
                                post.setAvatarUser(uriAvatar);
                                post.setDate(String.valueOf(System.currentTimeMillis()));
                                post.setContent(content);
                                post.setHomestay(homestay);
                                post.setLocation(location);
                                myRef.push().setValue(post);
                            }
                        }
                    });

                }
            });

        }


    }
// TODO get name , uri User
    ValueEventListener valueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                User user = snapshot.getValue(User.class);
                if (user.getFullName() != null) {
                    nameUser = user.getFullName();
                }
                if (user.getUriAvatar() != null) {
                    uriAvatar = user.getUriAvatar();
                }
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };

}

