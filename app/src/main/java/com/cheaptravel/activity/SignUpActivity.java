package com.cheaptravel.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.cheaptravel.R;
import com.cheaptravel.model.Post;
import com.cheaptravel.model.User;
import com.cheaptravel.ulti.Costant;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class SignUpActivity extends AppCompatActivity {
    private EditText signupFullname;
    private EditText signupUsername;
    private EditText signupPassword;
    private EditText signupEmail;
    private DatabaseReference mDatabase;
    private String email, username, password, fullname;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        map();


    }

    private void map() {
        signupFullname = findViewById(R.id.signup_fullname);
        signupUsername = findViewById(R.id.signup_username);
        signupPassword = findViewById(R.id.signup_password);
        signupEmail = findViewById(R.id.signup_email);
    }

    public void SignUp(View view) {
        email = signupEmail.getText().toString().trim();
        username = signupUsername.getText().toString().trim();
        fullname = signupFullname.getText().toString().trim();
        password = signupPassword.getText().toString().trim();
        Query queryUsername = FirebaseDatabase.getInstance().getReference(Costant.KEY_USER).orderByChild(Costant.KEY_USERNAME).equalTo(username);
        queryUsername.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    User user = new User(username, password, email, fullname);
                    DatabaseReference createUser = FirebaseDatabase.getInstance().getReference(Costant.KEY_USER);
                    createUser.push().setValue(user, new DatabaseReference.CompletionListener() {

                        @Override
                        public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                            showToas("Tạo tài khoản thành công");
                        }
                    });
                    return;
                }
                    showToas("Tài khoản đã tồn tại");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void checkUser() {
        Query queryEmail = FirebaseDatabase.getInstance().getReference(Costant.KEY_USER).orderByChild(Costant.KEY_EMAIL).equalTo(email);
        queryEmail.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    User user = new User(username, password, email, fullname);
                    DatabaseReference createUser = FirebaseDatabase.getInstance().getReference(Costant.KEY_USER);
                    createUser.push().setValue(user, new DatabaseReference.CompletionListener() {

                        @Override
                        public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                            showToas("Tạo tài khoản thành công");
                        }
                    });
                } else {
                    showToas("Email đã tồn tại");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void showToas(String s) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }

}
