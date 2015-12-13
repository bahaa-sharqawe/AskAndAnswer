package com.orchidatech.askandanswer.View.Adapter;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.CardView;
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

import com.orchidatech.askandanswer.Activity.CategoryPosts;
import com.orchidatech.askandanswer.Activity.MainScreen;
import com.orchidatech.askandanswer.Activity.ViewPost;
import com.orchidatech.askandanswer.Constant.AppSnackBar;
import com.orchidatech.askandanswer.Constant.GNLConstants;
import com.orchidatech.askandanswer.Database.DAO.CategoriesDAO;
import com.orchidatech.askandanswer.Database.DAO.UsersDAO;
import com.orchidatech.askandanswer.Database.Model.Category;
import com.orchidatech.askandanswer.Database.Model.Posts;
import com.orchidatech.askandanswer.Database.Model.Users;
import com.orchidatech.askandanswer.Fragment.Comments;
import com.orchidatech.askandanswer.Fragment.Profile;
import com.orchidatech.askandanswer.R;
import com.orchidatech.askandanswer.View.Interface.OnPostEventListener;
import com.orchidatech.askandanswer.View.Utils.FontManager;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Bahaa on 7/11/2015.
 */
public class SearchRecViewAdapter extends RecyclerView.Adapter<SearchRecViewAdapter.PostViewHolder> {
    private static final int TYPE_HEADER = 0;  // Declaring Variable to Understand which View is being worked on
    private static final int TYPE_FOOTER = 1;
    private final View parent;
    private final FontManager fontManager;

    private ArrayList<Posts> posts;
    Activity activity;
    private boolean loading = false;
    private boolean isFoundData = true;

    public SearchRecViewAdapter(Activity activity, ArrayList<Posts> posts, View parent) {
        this.activity = activity;
        this.posts = posts;
        this.parent = parent;
        fontManager = FontManager.getInstance(activity.getAssets());

    }

    @Override
    public PostViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;
       /* if (viewType == TYPE_FOOTER) {
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.footer_progress, parent, false);
            return new PostViewHolder(itemView, TYPE_FOOTER);
        } else*/ {
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_result_item, parent, false);
            return new PostViewHolder(itemView, TYPE_HEADER);
        }
    }

    @Override
    public void onBindViewHolder(final PostViewHolder holder, final int position) {
        Posts currentPost = posts.get(position);
        final Users postOwner = UsersDAO.getUser(currentPost.getUserID());
        final Category postCategory = CategoriesDAO.getCategory(currentPost.getCategoryID());
        holder.tv_post_category.setText(postCategory.getName());
        holder.tv_post_category.setTypeface(fontManager.getFont(FontManager.ROBOTO_LIGHT));

        holder.tv_person_name.setText(postOwner.getFname() + " " + postOwner.getLname());
        holder.tv_person_name.setTypeface(fontManager.getFont(FontManager.ROBOTO_MEDIUM));

        holder.tv_postDate.setText(GNLConstants.DateConversion.getDate(currentPost.getDate()));
        holder.tv_postDate.setTypeface(fontManager.getFont(FontManager.ROBOTO_LIGHT));

        holder.tv_postContent.setText(currentPost.getText());
        holder.tv_postContent.setTypeface(fontManager.getFont(FontManager.ROBOTO_LIGHT));

        holder.card_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                commentPost(posts.get(position).getServerID());
            }
        });
        holder.tv_post_category.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                categoryClick(postCategory.getServerID(), postOwner.getServerID());
            }
        });

//        holder.ll_comment.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                pe_listener.onCommentPost(posts.get(position).getServerID());
//            }
//        });
//        holder.ll_share.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                pe_listener.onSharePost(posts.get(position).getServerID());
//            }
//        });
//        holder.ll_favorite.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                pe_listener.onFavoritePost(position, posts.get(position).getServerID(), posts.get(position).getUserID());
//            }
//        });
        String postImage = currentPost.getImage();
//        holder.pb_photo_load.setVisibility(View.VISIBLE);
        holder.iv_postImage.setVisibility(View.GONE);

        if(postImage!=null && postImage.length()>0) {
            Picasso.with(activity).load(Uri.parse(currentPost.getImage())).into(holder.iv_postImage, new Callback() {
                @Override
                public void onSuccess() {
//                    holder.pb_photo_load.setVisibility(View.GONE);
                    holder.iv_postImage.setVisibility(View.VISIBLE);
                }

                @Override
                public void onError() {
//                    holder.pb_photo_load.setVisibility(View.GONE);
                    holder.iv_postImage.setVisibility(View.VISIBLE);
                }
            });
        } else {
//            holder.pb_photo_load.setVisibility(View.GONE);
            holder.iv_postImage.setVisibility(View.GONE);
        }
        holder.tv_comments.setText(posts.get(position).getComments_no() > 0?activity.getString(R.string.tv_comments_count, posts.get(position).getComments_no()):activity.getString(R.string.no_comments));
        holder.tv_comments.setTypeface(fontManager.getFont(FontManager.ROBOTO_LIGHT));

        holder.tv_unlikes.setText(posts.get(position).getNum_dislikes() + "");
        holder.tv_unlikes.setTypeface(fontManager.getFont(FontManager.ROBOTO_LIGHT));

        holder.tv_likes.setText(posts.get(position).getNum_likes() + "");
        holder.tv_likes.setTypeface(fontManager.getFont(FontManager.ROBOTO_LIGHT));

        holder.iv_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToProfile(postOwner.getServerID());
            }
        });
        Picasso.with(activity).load(Uri.parse(postOwner.getImage())).into(holder.iv_profile, new Callback() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onError() {
                holder.iv_profile.setImageResource(R.drawable.ic_user);
            }
        });
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
        CircleImageView iv_profile;
        TextView tv_likes;
        TextView tv_unlikes;
        TextView tv_comments;
//        ProgressBar pb_photo_load;
RelativeLayout card_post;
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
                tv_likes = (TextView) itemView.findViewById(R.id.tv_likes);
                tv_unlikes = (TextView) itemView.findViewById(R.id.tv_unlikes);
                tv_comments = (TextView) itemView.findViewById(R.id.tv_comments);
                rl_postEvents = (RelativeLayout) itemView.findViewById(R.id.rl_postEvents);
//                pb_photo_load = (ProgressBar) itemView.findViewById(R.id.pb_photo_load);
                card_post = (RelativeLayout) itemView.findViewById(R.id.card_post);

//                ll_comment = (LinearLayout) itemView.findViewById(R.id.ll_comment);
//                ll_share = (LinearLayout) itemView.findViewById(R.id.ll_share);
//                ll_favorite = (LinearLayout) itemView.findViewById(R.id.ll_favorite);
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }


    private void commentPost(long postId) {
        Bundle args = new Bundle();
        args.putLong(ViewPost.POST_ID, postId);
        Comments comments = new Comments(new TimelineRecViewAdapter.OnDialogDismiss() {
            @Override
            public void onDismiss() {

            }
        });
        comments.setArguments(args);
        comments.show(activity.getFragmentManager(), "Comments");
    }
    private void categoryClick(long category_id, long user_id) {
        Intent intent = new Intent(activity, CategoryPosts.class);
        intent.putExtra(CategoryPosts.CATEGORY_KEY, category_id);
        intent.putExtra(CategoryPosts.USER_ID, user_id);
        activity.startActivity(intent);
    }
    private void goToProfile(long userId) {
        MainScreen.oldPosition = -1;
        Fragment fragment = new Profile();
        Bundle args = new Bundle();
        args.putLong(Profile.USER_ID_KEY, userId);
        fragment.setArguments(args);
        FragmentManager mFragmentManager = activity.getFragmentManager();
        FragmentTransaction ft = mFragmentManager.beginTransaction();
        ft.replace(R.id.fragment_host, fragment);
        ft.addToBackStack(null);
        ft.commit();
        mFragmentManager.executePendingTransactions();
    }

}
