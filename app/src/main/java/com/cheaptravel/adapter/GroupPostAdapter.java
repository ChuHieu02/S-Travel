package com.cheaptravel.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.cheaptravel.R;
import com.cheaptravel.interfaces.GetKeyPost;
import com.cheaptravel.model.Like;
import com.cheaptravel.model.Post;
import com.cheaptravel.ulti.Costant;
import com.like.LikeButton;

import java.util.ArrayList;
import java.util.List;
import java.util.List;
import java.util.logging.Handler;

public class GroupPostAdapter extends RecyclerView.Adapter<GroupPostAdapter.ViewHolder> {
    private static final String TAG = "Adapter post";
    private Context context;
    private List<Post> groupPosts;
    private List<String> listImages = new ArrayList<>();
    private GetKeyPost getKeyPost;

    public void setGetKeyPost(GetKeyPost getKeyPost) {
        this.getKeyPost = getKeyPost;
    }

    public GroupPostAdapter(Context context, List<Post> groupPosts) {
        this.context = context;
        this.groupPosts = groupPosts;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_group_post, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        final Post groupPost = groupPosts.get(position);

        holder.content.setText(groupPost.getContent());
        holder.name.setText(groupPost.getNameUser());
        holder.date.setText(groupPost.getDate());
        holder.location.setText(groupPost.getLocation());
        Glide.with(context).load(groupPost.getAvatarUser()).circleCrop().placeholder(R.drawable.ic_no_image).into(holder.avatar);

//        Log.e(TAG, groupPost.getTotalLike() + "");
        if (groupPost.getTotalLike()!=null){
            holder.totalLikePost.setText(groupPost.getTotalLike());
        }
        if (groupPost.getTotaComment() != null) {
            holder.totalCommentPost.setText(groupPost.getTotaComment() + Costant.KEY_COMMENT);
        }

        holder.viewLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (holder.acLike.isLiked()){
                    holder.acLike.setLiked(false);
                    getKeyPost.getKey(groupPost.getIdPost(), position,holder.acLike.isLiked());
                }else {
                    holder.acLike.setLiked(true);
                    getKeyPost.getKey(groupPost.getIdPost(), position,holder.acLike.isLiked());
                }

            }
        });
        holder.viewCommentPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });


        listImages = groupPost.getImages();
        PostAdapter postAdapter = new PostAdapter(context, listImages);
        holder.rv_list_images.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        holder.rv_list_images.setHasFixedSize(true);
        holder.rv_list_images.setAdapter(postAdapter);
        holder.rv_list_images.setNestedScrollingEnabled(false);

    }

    @Override
    public int getItemCount() {
        return (groupPosts != null ? groupPosts.size() : 0);
    }

    public void addAll(List<Post> list) {
        int size = groupPosts.size();
        groupPosts.addAll(list);
        notifyItemRangeChanged(size, list.size());
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout viewLike;
        private ImageView avatar;
        private TextView name;
        private TextView date;
        private RecyclerView rv_list_images;
        private TextView content;
        private TextView location;
        private TextView totalLikePost;
        private LikeButton acLike;
        private LinearLayout viewCommentPost;
        private TextView totalCommentPost;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            avatar = (ImageView) itemView.findViewById(R.id.avatar);
            name = (TextView) itemView.findViewById(R.id.name);
            date = (TextView) itemView.findViewById(R.id.date);
            rv_list_images = (RecyclerView) itemView.findViewById(R.id.rv_list_images);
            content = (TextView) itemView.findViewById(R.id.content);
            location = (TextView) itemView.findViewById(R.id.location);
            viewLike = (LinearLayout) itemView.findViewById(R.id.view_like_post);
            totalLikePost = (TextView) itemView.findViewById(R.id.total_like_post);
            acLike = (LikeButton) itemView.findViewById(R.id.ac_like);
            viewCommentPost = (LinearLayout) itemView.findViewById(R.id.view_comment_post);
            totalCommentPost = (TextView) itemView.findViewById(R.id.total_comment_post);

        }
    }
}
