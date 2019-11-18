package com.cheaptravel.fragment;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.cheaptravel.MainActivity;
import com.cheaptravel.R;
import com.cheaptravel.activity.LoginActivity;
import com.cheaptravel.model.User;
import com.cheaptravel.ulti.Constants;
import com.cheaptravel.ulti.Helper;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;
import static com.firebase.ui.auth.AuthUI.getApplicationContext;

public class FragmentUser extends Fragment implements View.OnClickListener {
    private CircleImageView profileImageUser;
    private TextView tvShare;
    private TextView tvReport;
    private TextView tvSetting;
    private TextView tvLogout;
    private TextView tvNameUser;
    private static final int RESULT_LOAD_IMAGE = 1;
    private SharedPreferences sharedPreferences;
    private DatabaseReference mDatabase;
    private StorageReference mStorage;
    private AlertDialog dialog;
    private String nameUser;




    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user, container, false);
        mStorage = FirebaseStorage.getInstance().getReference();

        map(view);
        new getUser().execute();
        return view;
    }


    private String getKeyUser() {
        String key = "";
        sharedPreferences = getContext().getSharedPreferences(Constants.KEY_SHARE_PRE, Context.MODE_PRIVATE);
        if (sharedPreferences != null) {
            key = sharedPreferences.getString(Constants.KEY_SHARE_PRE_USER, "");
        }
        return key;
    }

    private class getUser extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... strings) {
            Query mDatabase = FirebaseDatabase.getInstance().getReference(Constants.KEY_USER).orderByKey().equalTo(getKeyUser());
            Log.e("keyUser", getKeyUser());
            mDatabase.addValueEventListener(valueEventListener);

            return null;
        }
    }

    ValueEventListener valueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                User user = snapshot.getValue(User.class);
                nameUser = user.getFullName();
                if (user.getFullName() != null) {
                    tvNameUser.setText(user.getFullName());
                }
                if (user.getUriAvatar() != null) {
                    Glide.with(getContext()).load(user.getUriAvatar()).error(R.mipmap.user_profile).placeholder(R.mipmap.user_profile)
                            .into(profileImageUser);
                }
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK) {
            if (data.getClipData() != null) {
                showToas("Không thể tải lên nhiều ảnh");
            } else if (data.getData() != null) {
                Log.e("uriImage", data.getData().toString());
                final StorageReference fileToUpload = mStorage.child("Images").child(String.valueOf(System.currentTimeMillis()));
                fileToUpload.putFile(data.getData()).addOnSuccessListener(taskSnapshot -> fileToUpload.getDownloadUrl().addOnSuccessListener(uri -> {
                    DatabaseReference uploadAvatar = FirebaseDatabase.getInstance().getReference(Constants.KEY_USER).child(getKeyUser()).child(Constants.KEY_URI_AVTAR_USER);
                    uploadAvatar.setValue(uri.toString());
                    showToas(getString(R.string.update_avatar_user_success));
                }));


            }

        }

    }

    private void map(View view) {
        profileImageUser = (CircleImageView) view.findViewById(R.id.profile_image_user);
        tvShare = (TextView) view.findViewById(R.id.tv_share);
        tvReport = (TextView) view.findViewById(R.id.tv_report);
        tvSetting = (TextView) view.findViewById(R.id.tv_setting);
        tvLogout = (TextView) view.findViewById(R.id.tv_logout);
        tvNameUser = (TextView) view.findViewById(R.id.tv_name_user);

        tvLogout.setOnClickListener(this);
        tvReport.setOnClickListener(this);
        tvSetting.setOnClickListener(this);
        tvShare.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        switch (view.getId()) {
            case R.id.tv_share:
                break;
            case R.id.tv_report:
                break;
            case R.id.tv_setting:
                PopupMenu popup = new PopupMenu(getContext(), view);
                popup.getMenuInflater().inflate(R.menu.popup_menu, popup.getMenu());

                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch(menuItem.getItemId()){

                            case R.id.pp_delete_item_changeAvatar:
                                openFileChooser();
                                break;

                            case R.id.pp_share_item_changeName:
                                renameUser();
                                break;

                        }
                        return false;
                    }
                });
                popup.show();
                break;
            case R.id.tv_logout:
                logout();
                break;

        }
    }

    private void renameUser() {
        final AlertDialog.Builder builder2 = new AlertDialog.Builder(getContext());


        final View viewDialog = LayoutInflater.from(getContext()).inflate(R.layout.dialog_rename_user, null);
        builder2.setView(viewDialog);

        final EditText ed_name_item_library;
        TextView bt_yes, bt_no;

        ed_name_item_library = viewDialog.findViewById(R.id.ed_name_item_library);
        bt_yes = viewDialog.findViewById(R.id.bt_yes);
        bt_no = viewDialog.findViewById(R.id.bt_no);

        ed_name_item_library.setText(nameUser);
        bt_no.setOnClickListener(view -> dialog.dismiss());
        bt_yes.setOnClickListener(view -> {
            if (!ed_name_item_library.getText().toString().trim().equals("")){
                renameConfirm(ed_name_item_library);
                dialog.dismiss();
            }
            else {showToas("Không được để trống");}
        });
        ed_name_item_library.setOnEditorActionListener((textView, i, keyEvent) -> {
            if (i == EditorInfo.IME_ACTION_DONE) {
                if (!ed_name_item_library.getText().toString().trim().equals("")){
                    renameConfirm(ed_name_item_library);
                    dialog.dismiss();
                }
                else {showToas("Không được để trống");}
            }
            return false;
        });
        ed_name_item_library.requestFocus();
        dialog = builder2.create();
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        dialog.show();


    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), RESULT_LOAD_IMAGE);
    }

    private void renameConfirm(EditText editText) {
        DatabaseReference updateName = FirebaseDatabase.getInstance().getReference(Constants.KEY_USER).child(getKeyUser()).child(Constants.KEY_FULLNAME);
        updateName.setValue(editText.getText().toString().trim());
    }

    private void showToas(String s) {
        Toast.makeText(getContext(), s, Toast.LENGTH_SHORT).show();
    }

    private  void logout(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        String title = "Đăng xuất";
        ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(Color.BLACK);
        SpannableStringBuilder stringBuilder = new SpannableStringBuilder(title);
        stringBuilder.setSpan(foregroundColorSpan, 0, title.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        builder.setTitle(stringBuilder);
        builder.setMessage("Bạn có chắc chắn muốn đăng xuất không?");
        builder.setCancelable(false);
        builder.setNegativeButton("Không", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.setPositiveButton("Có", (dialog, which) -> listener.logOut());
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    };

    private FragmentUserLogout listener;
    public interface FragmentUserLogout {
        void logOut();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof FragmentUserLogout) {
            listener = (FragmentUserLogout) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement FragmentDetailListListener");
        }
    }
}
