package com.orchidatech.askandanswer.View.Adapter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.orchidatech.askandanswer.Activity.CategoryPosts;
import com.orchidatech.askandanswer.Activity.MainScreen;
import com.orchidatech.askandanswer.Activity.ViewPost;
import com.orchidatech.askandanswer.Constant.AppSnackBar;
import com.orchidatech.askandanswer.Database.Model.Posts;
import com.orchidatech.askandanswer.Fragment.Profile;
import com.orchidatech.askandanswer.R;
import com.orchidatech.askandanswer.View.Interface.OnUserActionsListener;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Bahaa on 18/11/2015.
 */
public class MyAnswersRecViewAdapter extends RecyclerView.Adapter<MyAnswersRecViewAdapter.AnswerViewHolder> {
    private static final int TYPE_HEADER = 0;  // Declaring Variable to Understand which View is being worked on
    private static final int TYPE_FOOTER = 1;
    private View parent;
    private OnUserActionsListener listener;

    private ArrayList<Posts> posts;
    int tempNum;
    private Activity activity;
    private boolean loading = false;
    private boolean isFoundData = true;

    public MyAnswersRecViewAdapter(Activity activity, ArrayList<Posts> posts, int tempNum, View parent, OnUserActionsListener listener) {
        this.activity = activity;
        this.posts = posts;
        this.tempNum = tempNum;
        this.parent = parent;
        this.listener = listener;
    }

    @Override
    public AnswerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;
        if (viewType == TYPE_FOOTER) {
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.footer_progress, parent, false);
            return new AnswerViewHolder(itemView, TYPE_FOOTER);
        } else {
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_item, parent, false);
            return new AnswerViewHolder(itemView, TYPE_HEADER);
        }
    }

    @Override
    public void onBindViewHolder(final AnswerViewHolder holder, int position) {

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
            holder.rl_post_item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    activity.startActivity(new Intent(activity, ViewPost.class));
                }
            });

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

    public class AnswerViewHolder extends RecyclerView.ViewHolder {
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
        RelativeLayout rl_post_item;
        int viewType;

        public AnswerViewHolder(View itemView, int viewType) {
            super(itemView);
            this.viewType = viewType;
            if (viewType == TYPE_FOOTER) {
                pv_load = (ProgressBar) itemView.findViewById(R.id.pv_load);
                pv_load.getIndeterminateDrawable().setColorFilter(Color.parseColor("#249885"), android.graphics.PorterDuff.Mode.MULTIPLY);
            } else {
                rl_post_item = (RelativeLayout) itemView.findViewById(R.id.rl_post_item);
                iv_person = (CircleImageView) itemView.findViewById(R.id.iv_person);
                iv_person.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        MainScreen.oldPosition = 3;
                        activity.getFragmentManager().beginTransaction().replace(R.id.fragment_host, new Profile()).addToBackStack(null).commit();
                        activity.getFragmentManager().executePendingTransactions();
                    }
                });
                tv_person_name = (TextView) itemView.findViewById(R.id.tv_person_name);
                tv_person_name.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        MainScreen.oldPosition = 3;
                        activity.getFragmentManager().beginTransaction().replace(R.id.fragment_host, new Profile()).addToBackStack(null).commit();
                        activity.getFragmentManager().executePendingTransactions();
                    }
                });
                rating_post = (RatingBar) itemView.findViewById(R.id.rating_post);
//            LayerDrawable stars = (LayerDrawable) rating_post.getProgressDrawable();
//            stars.getDraewable(2).setColorFilter(Color.parseColor("#f1ad24"), PorterDuff.Mode.SRC_ATOP);
                tv_postDate = (TextView) itemView.findViewById(R.id.tv_postDate);
                tv_post_category = (TextView) itemView.findViewById(R.id.tv_post_category);
                tv_post_category.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        activity.startActivity(new Intent(activity, CategoryPosts.class));
                    }
                });
                tv_postDesc = (TextView) itemView.findViewById(R.id.tv_postDate);
                tv_comments = (TextView) itemView.findViewById(R.id.tv_comments);
                tv_comments.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                        new Comments().show(activity.getFragmentManager(), "Comments");
                        listener.onComment();
                        ;
                    }
                });
                tv_likes = (TextView) itemView.findViewById(R.id.tv_likes);
                tv_likes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listener.onLike();
                    }
                });
                tv_unlikes = (TextView) itemView.findViewById(R.id.tv_unlikes);
                tv_unlikes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listener.onDislike();
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
