package com.cheaptravel.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.cheaptravel.MainActivity;
import com.cheaptravel.R;
import com.cheaptravel.activity.NewPostActivity;
import com.cheaptravel.adapter.GroupPostAdapter;
import com.cheaptravel.model.Comment;
import com.cheaptravel.model.Like;
import com.cheaptravel.model.Post;
import com.cheaptravel.model.User;
import com.cheaptravel.ulti.Constants;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FragmentHome extends Fragment implements View.OnClickListener {
    private String TAG = "PostFrag";

    private DatabaseReference mDatabase;
    private GroupPostAdapter groupPostAdapter;
    private RecyclerView recyclerView;
    private List<Like> likes = new ArrayList<>();
    private List<Comment> comments = new ArrayList<>();
    private List<Post> postArrayList = new ArrayList<>();
    private SharedPreferences sharedPreferences;
    private TextView etHomeNewpost;
    private ImageView avatarHome;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        map(view);
        setAdapter(postArrayList);
        new getPosts().execute();
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

    private void map(View view) {
        etHomeNewpost = view.findViewById(R.id.et_home_newpost);
        avatarHome = (ImageView) view.findViewById(R.id.avatar_home);
        recyclerView = view.findViewById(R.id.rv_fr_group_post);

        etHomeNewpost.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.et_home_newpost:
                startActivity(new Intent(getContext(), NewPostActivity.class));
                break;
        }
    }

    private class getPosts extends AsyncTask<String, String, List<Post>> {
        @Override
        protected List<Post> doInBackground(String... strings) {
            Query mDatabase = FirebaseDatabase.getInstance().getReference("Post");
            mDatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    postArrayList.clear();
                    if (dataSnapshot.exists()) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            final Post post = snapshot.getValue(Post.class);
                            post.setIdPost(snapshot.getKey());
                            postArrayList.add(post);
                            getTotalLike(post, snapshot);
                            getTotalComment(post, snapshot);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            return postArrayList;
        }

        @Override
        protected void onPostExecute(List<Post> list) {
            super.onPostExecute(list);
            groupPostAdapter.addAll(list);
        }
    }

    private void setAdapter(List<Post> list) {
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);
        groupPostAdapter = new GroupPostAdapter(getContext(), list);
        recyclerView.setAdapter(groupPostAdapter);
        groupPostAdapter.setGetKeyPost((key, position, b) -> {
            mDatabase = FirebaseDatabase.getInstance().getReference("Post").child(key).child("Like");
            if (b) {
                Like like = new Like("1");
                mDatabase.child(Constants.KEY_ID_USER_DEFAULT).setValue(like);
                groupPostAdapter.notifyItemChanged(position);
                return;
            }
            Like like = new Like("0");
            mDatabase.child(Constants.KEY_ID_USER_DEFAULT).setValue(like);
            groupPostAdapter.notifyItemChanged(position);

        });
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
                        if (user.getUriAvatar() != null) {
                            Glide.with(getContext()).load(user.getUriAvatar()).circleCrop().error(R.mipmap.user_profile).placeholder(R.mipmap.user_profile)
                                    .into(avatarHome);
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

    //TODO: get all like
    private void getTotalLike(final Post post, final DataSnapshot snapshot) {
        Query mDatabase2 = FirebaseDatabase.getInstance().getReference("Post").child(snapshot.getKey()).child("Like").orderByChild("value").equalTo("1");
        mDatabase2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                likes.clear();
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Like like = snapshot.getValue(Like.class);
                        likes.add(like);
                    }
                    post.setTotalLike(String.valueOf(likes.size()));
                    groupPostAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    //TODO: get all comment
    private void getTotalComment(final Post post, DataSnapshot snapshot) {
        Query mDatabase3 = FirebaseDatabase.getInstance().getReference("Post").child(snapshot.getKey()).child("Comment");
        mDatabase3.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                likes.clear();
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Comment comment = snapshot.getValue(Comment.class);
                        comments.add(comment);
                    }
                    post.setTotaComment(String.valueOf(comments.size()));
                    groupPostAdapter.notifyDataSetChanged();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
