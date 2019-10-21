package com.cheaptravel.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.cheaptravel.R;
import com.cheaptravel.adapter.GroupPostAdapter;
import com.cheaptravel.interfaces.GetKeyPost;
import com.cheaptravel.model.Comment;
import com.cheaptravel.model.Like;
import com.cheaptravel.model.Post;
import com.cheaptravel.ulti.Costant;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class PostActivity extends AppCompatActivity {
    private String TAG = "PostActivity";

    private DatabaseReference mDatabase;

    private GroupPostAdapter groupPostAdapter;
    private RecyclerView recyclerView;
    private List<String> listImage = new ArrayList<>();
    private List<Like> likes = new ArrayList<>();
    private List<Comment> comments = new ArrayList<>();
    private List<Post> postArrayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        recyclerView = findViewById(R.id.rv_group_post);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        groupPostAdapter = new GroupPostAdapter(this, postArrayList);
        recyclerView.setAdapter(groupPostAdapter);

        groupPostAdapter.setGetKeyPost(new GetKeyPost() {
            @Override
            public void getKey(String key, int position , boolean b) {
                mDatabase = FirebaseDatabase.getInstance().getReference("Post").child(key).child("Like");
                if (b){
                    Like like = new Like("1");
                    mDatabase.child(Costant.KEY_ID_USER_DEFAULT).setValue(like);
                    groupPostAdapter.notifyItemChanged(position);
                    return;
                }
                Like like = new Like("0");
                mDatabase.child(Costant.KEY_ID_USER_DEFAULT).setValue(like);
                groupPostAdapter.notifyItemChanged(position);

            }
        });

        mDatabase = FirebaseDatabase.getInstance().getReference("Post");
        mDatabase.addValueEventListener(valueEventListener);

    }


    //TODO: get all post
    ValueEventListener valueEventListener = new ValueEventListener() {
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
                groupPostAdapter.notifyDataSetChanged();
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {
        }
    };

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


    private void addData() {
        listImage.add("https://photo-3-baomoi.zadn.vn/w1000_r1/2018_06_10_329_26365751/c9e2663dad7b44251d6a.jpg");
        listImage.add("http://img.docbao.vn/images/fullsize/2019/02/25/hot-girl.jpg");
        listImage.add("https://i.pinimg.com/originals/41/6f/c9/416fc91d496ed8bcc3c05c9bdcb80119.jpg");
        listImage.add("https://danongonline.com.vn/wp-content/uploads/2017/10/20293101-1792343441096406-8899654375848631269-n-1503761737042.jpg");
        listImage.add("https://congngheads.com/media/images/anh-dep/tai-anh-nen-dien-thoai-di-dong-1556698466/album-gai-xinh-lam-hinh-nen-dien-thoai-dep-cua-nu-sinh-trung-hoc-de-thuong-full-hd-8.jpg");
        Post post = new Post("https://photo-3-baomoi.zadn.vn/w1000_r1/2018_06_10_329_26365751/c9e2663dad7b44251d6a.jpg",
                "Dungg",
                "Bong dang ai do nhe nhang vut qua noi day , tieng hat ngay ngat nhon nhip tim me say",
                "1 minute",
                "hanoi", listImage);
        mDatabase.push().setValue(post);
    }

}
