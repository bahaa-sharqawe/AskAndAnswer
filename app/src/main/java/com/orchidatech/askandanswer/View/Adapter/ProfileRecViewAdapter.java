package com.orchidatech.askandanswer.View.Adapter;

import android.content.Context;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.orchidatech.askandanswer.Entity.Person;
import com.orchidatech.askandanswer.Entity.Post;
import com.orchidatech.askandanswer.R;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Bahaa on 7/11/2015.
 */
public class ProfileRecViewAdapter extends RecyclerView.Adapter<ProfileRecViewAdapter.PostViewHolder> {
    private ArrayList<Post> posts;
    private Context context;
    public ProfileRecViewAdapter(Context context, ArrayList<Post> posts) {
        this.context = context;
        this.posts = posts;
    }
    @Override
    public PostViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;
        itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_item_profile, parent, false);
//        itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_post_item_profile, parent, false);
        return new PostViewHolder(itemView);

    }

    @Override
    public void onBindViewHolder(PostViewHolder holder, int position) {
        Post post = posts.get(position);
        Person postOwner = post.getOwner();
        holder.tv_person_name.setText(postOwner.getName());
        //complete the code here
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    public static class PostViewHolder extends RecyclerView.ViewHolder {
        TextView tv_person_name;
        TextView tv_postDate;
        TextView tv_postContent;
        TextView tv_likes;
        TextView tv_unlikes;
        TextView tv_post_category;  //change visibility
        ImageView iv_postImage;
        FloatingActionButton fab_edit_post; //change visibility
        RelativeLayout rl_like_unlike; //change visibility
        RelativeLayout rl_postEvents; //change visibility
        LinearLayout ll_comment;
        LinearLayout ll_share;
        LinearLayout ll_favorite;
        CircleImageView iv_profile;


        public PostViewHolder(View itemView) {
            super(itemView);
            tv_person_name = (TextView) itemView.findViewById(R.id.tv_person_name);
            tv_postDate = (TextView) itemView.findViewById(R.id.tv_postDate);
            tv_postContent = (TextView) itemView.findViewById(R.id.tv_postContent);
            tv_likes = (TextView) itemView.findViewById(R.id.tv_likes);
            tv_unlikes = (TextView) itemView.findViewById(R.id.tv_unlikes);
            tv_post_category = (TextView) itemView.findViewById(R.id.tv_post_category);
            iv_postImage = (ImageView) itemView.findViewById(R.id.iv_postImage);
            fab_edit_post = (FloatingActionButton) itemView.findViewById(R.id.fab_edit_post);
            rl_like_unlike = (RelativeLayout) itemView.findViewById(R.id.rl_like_unlike);
            rl_postEvents = (RelativeLayout) itemView.findViewById(R.id.rl_postEvents);
            ll_comment = (LinearLayout) itemView.findViewById(R.id.ll_comment);
            ll_share = (LinearLayout) itemView.findViewById(R.id.ll_share);
            ll_favorite = (LinearLayout) itemView.findViewById(R.id.ll_favorite);
            iv_profile = (CircleImageView) itemView.findViewById(R.id.iv_profile);
        }
    }
}
