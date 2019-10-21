package com.cheaptravel.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.cheaptravel.R;

import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.Viewholder> {
    private Context context;
    private List<String> listImages;

    public PostAdapter(Context context, List<String> listImages) {
        this.context = context;
        this.listImages = listImages;
    }

    @NonNull
    @Override
    public Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_post, parent, false);
        return new Viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Viewholder holder, int position) {
        if (listImages.get(position) != null) {
            Glide.with(context).load(listImages.get(position)).placeholder(R.drawable.ic_no_image).into(holder.ivPost);
            return;
        }
    }

    @Override
    public int getItemCount() {
        return (listImages != null ? listImages.size() : 0);
    }

    public class Viewholder extends RecyclerView.ViewHolder {
        private ImageView ivPost;

        public Viewholder(@NonNull View itemView) {
            super(itemView);
            ivPost = (ImageView) itemView.findViewById(R.id.iv_post);
        }
    }
}
