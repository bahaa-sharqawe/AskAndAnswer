package com.orchidatech.askandanswer.View.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import com.orchidatech.askandanswer.Entity.Post;
import com.orchidatech.askandanswer.R;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Bahaa on 7/11/2015.
 */
public class SearchRecViewAdapter extends RecyclerView.Adapter<SearchRecViewAdapter.PostViewHolder> {
    private ArrayList<Post> posts;
    private Context context;
    public SearchRecViewAdapter(Context context, ArrayList<Post> posts) {
        this.context = context;
        this.posts = posts;
    }

    @Override
    public PostViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_item_search, parent, false);
        return new PostViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(PostViewHolder holder, int position) {
        holder.rating_post.setRating(3.67f);
//        Post post = posts.get(position);
//        Person postOwner = post.getOwner();
//        holder.tv_postDesc.setText(post.getDesc());
//        holder.tv_post_category.setText(post.getCategory());
//        holder.tv_postDate.setText(GNLConstants.DateConversion.getDate(post.getDate()));
//        holder.rating_post.setRating(post.getRate());
//        holder.tv_comments.setText(context.getResources().getString(R.string.tv_comments_count, post.getComments()));
//        holder.tv_likes.setText(String.valueOf(post.getLikes()));
//        holder.tv_unlikes.setText(String.valueOf(post.getUnlikes()));
//        holder.tv_person_name.setText(postOwner.getName());
//
//        ImageLoader.getInstance().displayImage(String.valueOf(Uri.parse(postOwner.getPhoto())), holder.iv_person,
//                null, new ImageLoadingListener() {
//            @Override
//            public void onLoadingStarted(String imageUri, View view) {
//
//            }
//
//            @Override
//            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
//
//            }
//
//            @Override
//            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
//
//            }
//
//            @Override
//            public void onLoadingCancelled(String imageUri, View view) {
//
//            }
//        }, new ImageLoadingProgressListener() {
//            @Override
//            public void onProgressUpdate(String imageUri, View view, int current, int total) {
//
//            }
//        });
//
    }

    @Override
    public int getItemCount() {
        return 3;
    }

    public static class PostViewHolder extends RecyclerView.ViewHolder {
        CircleImageView iv_person;
        TextView tv_person_name;
        RatingBar rating_post;
        TextView tv_postDate;
        TextView tv_post_category;
        TextView tv_postDesc;
        TextView tv_comments;
        TextView tv_likes;
        TextView tv_unlikes;

        public PostViewHolder(View itemView) {
            super(itemView);
            iv_person = (CircleImageView) itemView.findViewById(R.id.iv_person);
            tv_person_name = (TextView) itemView.findViewById(R.id.tv_person_name);
            rating_post = (RatingBar) itemView.findViewById(R.id.rating_post);
            LayerDrawable stars = (LayerDrawable) rating_post.getProgressDrawable();
            stars.getDrawable(2).setColorFilter(Color.parseColor("#f1ad24"), PorterDuff.Mode.SRC_ATOP);
            tv_postDate = (TextView) itemView.findViewById(R.id.tv_postDate);
            tv_post_category = (TextView) itemView.findViewById(R.id.tv_post_category);
            tv_postDesc = (TextView) itemView.findViewById(R.id.tv_postDate);
            tv_comments = (TextView) itemView.findViewById(R.id.tv_comments);
            tv_likes = (TextView) itemView.findViewById(R.id.tv_likes);
            tv_unlikes = (TextView) itemView.findViewById(R.id.tv_unlikes);
        }
    }
}
