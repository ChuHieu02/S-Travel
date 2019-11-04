package com.cheaptravel.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
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
        Glide.with(context)
                .load(listImages.get(position))
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        holder.pbLoadProfile.setVisibility(View.GONE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        holder.pbLoadProfile.setVisibility(View.GONE);
                        return false;
                    }
                })
                .apply(new RequestOptions().placeholder(R.drawable.ic_place_holder).error(R.drawable.ic_place_holder))
                .into(holder.ivPost);


    }

    @Override
    public int getItemCount() {
        return (listImages != null ? listImages.size() : 0);
    }

    public class Viewholder extends RecyclerView.ViewHolder {
        private ProgressBar pbLoadProfile;
        private ImageView ivPost;

        public Viewholder(@NonNull View itemView) {
            super(itemView);
            ivPost = (ImageView) itemView.findViewById(R.id.iv_post);
            pbLoadProfile = itemView.findViewById(R.id.pb_load_profile);

        }
    }
}
