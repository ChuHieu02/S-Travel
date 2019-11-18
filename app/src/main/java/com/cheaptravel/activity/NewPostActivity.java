package com.cheaptravel.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.cheaptravel.MainActivity;
import com.cheaptravel.R;
import com.cheaptravel.adapter.UploadListAdapter;
import com.cheaptravel.model.User;
import com.cheaptravel.newpostService.ExampleService;
import com.cheaptravel.ulti.Constants;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class NewPostActivity extends AppCompatActivity implements View.OnClickListener {
    private FloatingActionButton floatingActionButton;
    private ImageView avatarUser;
    private TextView username;
    private TextView btnNewPost;
    private EditText etEnterContent;
    private RecyclerView rvChooseImage;
    private static final int RESULT_LOAD_IMAGE = 1;
    private ArrayList<String> listUriRespon = new ArrayList<>();
    private ArrayList<Uri> listFileUri = new ArrayList<>();
    private ArrayList<String> listNameImage = new ArrayList<>();
    private UploadListAdapter uploadListAdapter;
    private List<String> listPathFile = new ArrayList<>();
    private SharedPreferences sharedPreferences;
    private EditText etNameHomestay;
    private EditText etLocation;
    private StorageReference mStorage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);
        map();
        mStorage = FirebaseStorage.getInstance().getReference();
        new getUser().execute();
        rvChooseImage.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
        uploadListAdapter = new UploadListAdapter(listPathFile, this);
        rvChooseImage.setAdapter(uploadListAdapter);
    }

    private String getKeyUser() {
        String key = "";
        sharedPreferences = this.getSharedPreferences(Constants.KEY_SHARE_PRE, Context.MODE_PRIVATE);
        if (sharedPreferences != null) {
            key = sharedPreferences.getString(Constants.KEY_SHARE_PRE_USER, "");
        }
        return key;
    }

    private class getUser extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... strings) {
            Query mDatabase = FirebaseDatabase.getInstance().getReference(Constants.KEY_USER).orderByKey().equalTo(getKeyUser());
//            Log.e("keyUser", getKeyUser());
            mDatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        User user = snapshot.getValue(User.class);
                        if (user.getFullName() != null) {
                            username.setText(user.getFullName());
                        }
                        if (user.getUriAvatar() != null) {
                            Glide.with(NewPostActivity.this).load(user.getUriAvatar()).circleCrop().error(R.mipmap.user_profile).placeholder(R.mipmap.user_profile)
                                    .into(avatarUser);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            return null;
        }
    }

    private void map() {
        etNameHomestay = (EditText) findViewById(R.id.et_name_homestay);
        etLocation = (EditText) findViewById(R.id.et_location);
        floatingActionButton = findViewById(R.id.floatingActionButton);
        avatarUser = findViewById(R.id.avatar_user);
        username = findViewById(R.id.username);
        btnNewPost = findViewById(R.id.btn_new_post);
        etEnterContent = findViewById(R.id.et_enter_content);
        rvChooseImage = findViewById(R.id.rv_choose_image);
        floatingActionButton.setOnClickListener(this);
        btnNewPost.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.floatingActionButton:
                chooseImage();
                break;
            case R.id.btn_new_post:
                if (!etEnterContent.getText().toString().isEmpty() && !etNameHomestay.getText().toString().isEmpty() && !etLocation.getText().toString().isEmpty()) {

                    Intent serviceIntent = new Intent(NewPostActivity.this, ExampleService.class);
                    serviceIntent.putStringArrayListExtra("listNameImage", listNameImage);
                    serviceIntent.putParcelableArrayListExtra("listFileUri", listFileUri);
                    serviceIntent.putStringArrayListExtra("listUriRespon", listUriRespon);
                    serviceIntent.putExtra("keyUser", getKeyUser());
                    serviceIntent.putExtra("content", etEnterContent.getText().toString().trim());
                    serviceIntent.putExtra("location", etLocation.getText().toString().trim());
                    serviceIntent.putExtra("homestay", etNameHomestay.getText().toString().trim());
                    ContextCompat.startForegroundService(NewPostActivity.this, serviceIntent);
                    onBackPressed();
                }else {
                    Toast.makeText(this, "Bạn phải điền đủ nội dung", Toast.LENGTH_SHORT).show();
                }

                break;

        }
    }


    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), RESULT_LOAD_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK) {
            if (data.getClipData() != null) {

                int totalItemsSelected = data.getClipData().getItemCount();
                for (int i = 0; i < totalItemsSelected; i++) {
                    Uri fileUri = data.getClipData().getItemAt(i).getUri();
                    this.listFileUri.add(fileUri);
                    String fileName = getFileName(fileUri);
                    this.listNameImage.add(fileName);
                    uploadListAdapter.notifyDataSetChanged();
                }

            } else if (data.getData() != null) {

                this.listFileUri.add(data.getData());
                String fileName = getFileName(data.getData());
                this.listNameImage.add(fileName);
                uploadListAdapter.notifyDataSetChanged();

            }

        }

    }

    public String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                    String data = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
                    listPathFile.add(data);
//                    Log.e("path", data + "\n");
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }
}
