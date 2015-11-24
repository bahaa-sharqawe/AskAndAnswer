package com.orchidatech.askandanswer.View.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RatingBar;
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
    int tempNum;
    private Context context;
    private boolean loading = false;
    private boolean isFoundData = true;

    public SearchRecViewAdapter(Context context, ArrayList<Posts> posts, int tempNum, View parent) {
        this.context = context;
        this.posts = posts;
        this.tempNum = tempNum;
        this.parent = parent;
    }

    @Override
    public PostViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;
        if (viewType == TYPE_FOOTER) {
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.footer_progress, parent, false);
            return new PostViewHolder(itemView, TYPE_FOOTER);
        } else {
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_item, parent, false);
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
                            AppSnackBar.show(parent, context.getString(R.string.BR_GNL_005), Color.RED, Color.WHITE);
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
        CircleImageView iv_person;
        TextView tv_person_name;
        RatingBar rating_post;
        TextView tv_postDate;
        TextView tv_post_category;
        TextView tv_postDesc;
        TextView tv_comments;
        TextView tv_likes;
        TextView tv_unlikes;
        ProgressBar pv_load;
        ;
        int viewType;

        public PostViewHolder(View itemView, int viewType) {
            super(itemView);
            this.viewType = viewType;
            if (viewType == TYPE_FOOTER) {
                pv_load = (ProgressBar) itemView.findViewById(R.id.pv_load);
                pv_load.getIndeterminateDrawable().setColorFilter(Color.parseColor("#249885"), android.graphics.PorterDuff.Mode.MULTIPLY);
            } else {
                iv_person = (CircleImageView) itemView.findViewById(R.id.iv_person);
                tv_person_name = (TextView) itemView.findViewById(R.id.tv_person_name);
                rating_post = (RatingBar) itemView.findViewById(R.id.rating_post);
//            LayerDrawable stars = (LayerDrawable) rating_post.getProgressDrawable();
//            stars.getDraewable(2).setColorFilter(Color.parseColor("#f1ad24"), PorterDuff.Mode.SRC_ATOP);
                tv_postDate = (TextView) itemView.findViewById(R.id.tv_postDate);
                tv_post_category = (TextView) itemView.findViewById(R.id.tv_post_category);
                tv_postDesc = (TextView) itemView.findViewById(R.id.tv_postDate);
                tv_comments = (TextView) itemView.findViewById(R.id.tv_comments);
                tv_likes = (TextView) itemView.findViewById(R.id.tv_likes);
                tv_unlikes = (TextView) itemView.findViewById(R.id.tv_unlikes);
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
