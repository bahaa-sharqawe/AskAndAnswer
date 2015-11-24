package com.orchidatech.askandanswer.View.Adapter;

import android.app.Activity;
import android.graphics.Color;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.orchidatech.askandanswer.Constant.AppSnackBar;
import com.orchidatech.askandanswer.Database.Model.Posts;
import com.orchidatech.askandanswer.R;
import com.orchidatech.askandanswer.View.Interface.OnPostEventListener;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Bahaa on 17/11/2015.
 */
public class CategoryPostRecViewAdapter extends RecyclerView.Adapter<CategoryPostRecViewAdapter.PostViewHolder> {
    private static final int TYPE_HEADER = 0;  // Declaring Variable to Understand which View is being worked on
    private static final int TYPE_FOOTER = 1;
    private final View parent;
    private final OnPostEventListener pe_listener;
    private ArrayList<Posts> posts;
    int tempNum;
    private Activity activity;
    private boolean loading = false;
    private boolean isFoundData = true;

    public CategoryPostRecViewAdapter(Activity activity, ArrayList<Posts> posts, int tempNum, View parent, OnPostEventListener pe_listener) {
        this.activity = activity;
        this.posts = posts;
        this.tempNum = tempNum;
        this.parent = parent;
        this.pe_listener = pe_listener;
    }

    @Override
    public PostViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;
        if (viewType == TYPE_FOOTER) {
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.footer_progress, parent, false);
            return new PostViewHolder(itemView, TYPE_FOOTER);
        } else {
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_item, parent, false);
            return new PostViewHolder(itemView, TYPE_HEADER);
        }
    }

    @Override
    public void onBindViewHolder(final PostViewHolder holder, int position) {

        if (holder.viewType == TYPE_FOOTER) {
            if (!loading && isFoundData) {
                loading = true;
                ///load data from server
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //                   listener.onReached();
                        if (tempNum == 30) {
                            holder.pv_load.setVisibility(View.GONE);
                            AppSnackBar.show(parent, activity.getString(R.string.BR_GNL_005), Color.RED, Color.WHITE);
                            isFoundData = false;
                            return;
                        }
                        tempNum += 10;
                        notifyDataSetChanged();
                        loading = false;
                    }
                }, 2000);
            }
        } else {

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
    }

    @Override
    public int getItemCount() {
        return tempNum + 1;
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
        ProgressBar pv_load;
        RelativeLayout rl_post_item;
        int viewType;

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
//                        pe_listener.onCategoryClick(1);
//                        postEventsListener.onCategoryClick(posts.get(getAdapterPosition()).getCategoryID());
                    }
                });
                rl_postEvents = (RelativeLayout) itemView.findViewById(R.id.rl_postEvents);
                ll_comment = (LinearLayout) itemView.findViewById(R.id.ll_comment);
                ll_comment.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        pe_listener.onCommentPost(1);
//                        postEventsListener.onCommentPost(posts.get(getAdapterPosition()).getServerID());
                    }
                });
                ll_share = (LinearLayout) itemView.findViewById(R.id.ll_share);
                ll_favorite = (LinearLayout) itemView.findViewById(R.id.ll_favorite);
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        pe_listener.onClick(1);
//                        pe_listener.onClick(posts.get(getAdapterPosition()).getServerID());
                    }
                });
            }

        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == tempNum)
            return TYPE_FOOTER;
        return TYPE_HEADER;
    }
}
