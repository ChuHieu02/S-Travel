package com.cheaptravel.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.cheaptravel.MainActivity;
import com.cheaptravel.R;
import com.cheaptravel.model.User;
import com.cheaptravel.ulti.Constants;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import static com.cheaptravel.activity.SignUpActivity.username;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText etPassword;
    private EditText etUsername;
    private Button btnLogin;
    private Button btnSignUp;
    private static final String TAG = "SignIn";
    private FirebaseAuth mAuth;
    private ImageView logo;
    private TextView tvRespon;
    private String passwordRes;
    private ProgressBar progessLogin;
    private TextView tvSolgan;
    private String keyUser;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();
        map();




    }


    private void map() {
        progessLogin = (ProgressBar) findViewById(R.id.progess_login);
        logo = (ImageView) findViewById(R.id.logo);
        etPassword = (EditText) findViewById(R.id.et_password);
        etUsername = (EditText) findViewById(R.id.et_username);
        btnLogin = (Button) findViewById(R.id.btn_login);
        btnSignUp = (Button) findViewById(R.id.btn_signUp);
        tvSolgan = (TextView) findViewById(R.id.tv_solgan);
        tvSolgan.setSelected(true);
        btnSignUp.setOnClickListener(this);
        btnLogin.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_login:
                login();
                break;
            case R.id.btn_signUp:
                startActivity(new Intent(LoginActivity.this, SignUpActivity.class));
                break;

        }
    }

    private void login() {
        progessLogin.setVisibility(View.VISIBLE);
        String checkUser = etUsername.getText().toString().trim();
        String checkPassword = etPassword.getText().toString().trim();
        Query queryUsername = FirebaseDatabase.getInstance().getReference(Constants.KEY_USER).orderByChild(Constants.KEY_USERNAME).equalTo(checkUser);
            queryUsername.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()){
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            User user = snapshot.getValue(User.class);
                            keyUser = snapshot.getKey();
                            Log.e(TAG, keyUser);
                            passwordRes = user.getPassword();

                        }
                        if (checkPassword.equals(passwordRes)){
                            showToas(getString(R.string.login_success));
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            sharedPreferences = getSharedPreferences(Constants.KEY_SHARE_PRE, Context.MODE_PRIVATE);
                            editor = sharedPreferences.edit();
                            editor.putString(Constants.KEY_SHARE_PRE_USER, keyUser);
                            editor.apply();
                            finish();
                            progessLogin.setVisibility(View.GONE);
                        }else {
                            progessLogin.setVisibility(View.GONE);
                            showToas(getString(R.string.login_failed_password));
                        }

                    }else {
                        progessLogin.setVisibility(View.GONE);

                        showToas(getString(R.string.notify_user_is_not_exists));
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    progessLogin.setVisibility(View.GONE);
                    showToas("Lỗi xảy ra");
                }
            });

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (etUsername != null && etPassword != null) {
            etUsername.setText(username);
            etPassword.setText(SignUpActivity.password);
        }
    }
    private void showToas(String s) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }

}
