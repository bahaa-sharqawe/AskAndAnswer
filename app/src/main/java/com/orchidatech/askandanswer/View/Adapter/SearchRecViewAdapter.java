package com.orchidatech.askandanswer.View.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.orchidatech.askandanswer.Constant.AppSnackBar;
import com.orchidatech.askandanswer.Database.Model.Posts;
import com.orchidatech.askandanswer.R;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Bahaa on 7/11/2015.
 */
public class SearchRecViewAdapter extends RecyclerView.Adapter<SearchRecViewAdapter.PostViewHolder> {
    private static final int TYPE_HEADER = 0;  // Declaring Variable to Understand which View is being worked on
    private static final int TYPE_FOOTER = 1;
    private final View parent;

    private ArrayList<Posts> posts;
    private Context context;
    private boolean loading = false;
    private boolean isFoundData = true;

    public SearchRecViewAdapter(Context context, ArrayList<Posts> posts, View parent) {
        this.context = context;
        this.posts = posts;
        this.parent = parent;
    }

    @Override
    public PostViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;
       /* if (viewType == TYPE_FOOTER) {
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.footer_progress, parent, false);
            return new PostViewHolder(itemView, TYPE_FOOTER);
        } else*/ {
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_item, parent, false);
            return new PostViewHolder(itemView, TYPE_HEADER);
        }
    }

    @Override
    public void onBindViewHolder(final PostViewHolder holder, int position) {

//        if (holder.viewType == TYPE_FOOTER) {
//            if (!loading && isFoundData) {
//                loading = true;
//
//            }
//        } else {
//
//        }
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    public class PostViewHolder extends RecyclerView.ViewHolder {
        TextView tv_person_name;
        TextView tv_postDate;
        TextView tv_postContent;
        TextView tv_post_category;  //change visibility
        ImageView iv_postImage;
        RelativeLayout rl_postEvents; //change visibility
        LinearLayout ll_share;
        LinearLayout ll_favorite;
        LinearLayout ll_comment;
        CircleImageView iv_profile;
        int viewType;

        public PostViewHolder(View itemView, int viewType) {
            super(itemView);
            this.viewType = viewType;
           /* if (viewType == TYPE_FOOTER) {
                pv_load = (ProgressBar) itemView.findViewById(R.id.pv_load);
                pv_load.getIndeterminateDrawable().setColorFilter(Color.parseColor("#249885"), android.graphics.PorterDuff.Mode.MULTIPLY);
            } else*/ {
                tv_person_name = (TextView) itemView.findViewById(R.id.tv_person_name);
                tv_postDate = (TextView) itemView.findViewById(R.id.tv_postDate);
                tv_postContent = (TextView) itemView.findViewById(R.id.tv_postContent);
                iv_postImage = (ImageView) itemView.findViewById(R.id.iv_postImage);
                iv_profile = (CircleImageView) itemView.findViewById(R.id.iv_profile);
                tv_post_category = (TextView) itemView.findViewById(R.id.tv_post_category);
                rl_postEvents = (RelativeLayout) itemView.findViewById(R.id.rl_postEvents);
                ll_comment = (LinearLayout) itemView.findViewById(R.id.ll_comment);
                ll_share = (LinearLayout) itemView.findViewById(R.id.ll_share);
                ll_favorite = (LinearLayout) itemView.findViewById(R.id.ll_favorite);
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }
}
