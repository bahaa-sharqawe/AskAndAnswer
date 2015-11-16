package com.orchidatech.askandanswer.View.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.orchidatech.askandanswer.Entity.Post;
import com.orchidatech.askandanswer.R;
import com.orchidatech.askandanswer.View.Interface.OnCategoryClickListener;
import com.orchidatech.askandanswer.View.Interface.OnPostEventListener;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Bahaa on 7/11/2015.
 */
public class ProfileRecViewAdapter extends RecyclerView.Adapter<ProfileRecViewAdapter.PostViewHolder> {
    private static final int TYPE_HEADER = 0;  // Declaring Variable to Understand which View is being worked on
    private static final int TYPE_FOOTER = 1;

    private static OnPostEventListener postEventsListener;
    private  static OnCategoryClickListener catListener;
    private int tempNum;


    private ArrayList<Post> posts;
    private Context context;
    public ProfileRecViewAdapter(Context context, ArrayList<Post> posts, int tempNum, OnPostEventListener postEventsListener, OnCategoryClickListener catListener) {
        this.context = context;
        this.posts = posts;
        this.postEventsListener = postEventsListener;
        this.catListener = catListener;
        this.tempNum = tempNum;
    }
    @Override
    public PostViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = null;
        //if (viewType == TYPE_HEADER)
        if(viewType == TYPE_FOOTER) {
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.footer_progress, parent, false);
            return new PostViewHolder(itemView, TYPE_FOOTER);
        }else {
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_item_profile, parent, false);
            return new PostViewHolder(itemView, TYPE_HEADER);
        }

    }

    @Override
    public void onBindViewHolder(PostViewHolder holder, int position) {
        if(holder.viewType == TYPE_FOOTER ){
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
//                   listener.onReached();
                    tempNum += 10;
                    notifyDataSetChanged();

                }
            }, 2000);
//            Toast.makeText(context, "last reached", Toast.LENGTH_LONG).show();
        }
//        Post post = posts.get(position);
//        Person postOwner = post.getOwner();
//        holder.tv_person_name.setText(postOwner.getName());
        //complete the code here

//holder.comment_ll.addView( LayoutInflater.from(context).inflate(R.layout.comment_item_profile, null, false));
    }

    @Override
    public int getItemCount() {
        return tempNum+1;
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
        LinearLayout ll_share;
        LinearLayout ll_favorite;
        LinearLayout ll_comment;
        CircleImageView iv_profile;
        int viewType;
        ProgressBar pv_load;;

        public PostViewHolder(View itemView, int viewType) {
            super(itemView);
            this.viewType = viewType;
            if (viewType == TYPE_FOOTER) {
                pv_load = (ProgressBar) itemView.findViewById(R.id.pv_load);
                pv_load.getIndeterminateDrawable().setColorFilter(Color.parseColor("#249885"), android.graphics.PorterDuff.Mode.MULTIPLY);
            } else {
                tv_person_name = (TextView) itemView.findViewById(R.id.tv_person_name);
                tv_postDate = (TextView) itemView.findViewById(R.id.tv_postDate);
                tv_postContent = (TextView) itemView.findViewById(R.id.tv_postContent);
                iv_postImage = (ImageView) itemView.findViewById(R.id.iv_postImage);
                iv_profile = (CircleImageView) itemView.findViewById(R.id.iv_profile);

                tv_post_category = (TextView) itemView.findViewById(R.id.tv_post_category);
                tv_post_category.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        catListener.onClick(1, 2);
                    }
                });
                rl_postEvents = (RelativeLayout) itemView.findViewById(R.id.rl_postEvents);
                ll_comment = (LinearLayout) itemView.findViewById(R.id.ll_comment);
                ll_comment.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        postEventsListener.onCommentPost();
                    }
                });
                ll_share = (LinearLayout) itemView.findViewById(R.id.ll_share);
                ll_favorite = (LinearLayout) itemView.findViewById(R.id.ll_favorite);
            }
        }
    }
    @Override
    public int getItemViewType(int position) {
        if(position == tempNum)
            return TYPE_FOOTER;
        return TYPE_HEADER;
    }
}
