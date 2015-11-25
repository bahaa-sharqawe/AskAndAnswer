package com.orchidatech.askandanswer.View.Adapter;

import android.app.Activity;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.orchidatech.askandanswer.Constant.AppSnackBar;
import com.orchidatech.askandanswer.Constant.GNLConstants;
import com.orchidatech.askandanswer.Database.DAO.CategoriesDAO;
import com.orchidatech.askandanswer.Database.DAO.UsersDAO;
import com.orchidatech.askandanswer.Database.Model.Category;
import com.orchidatech.askandanswer.Database.Model.Posts;
import com.orchidatech.askandanswer.Database.Model.Users;
import com.orchidatech.askandanswer.R;
import com.orchidatech.askandanswer.View.Interface.OnLastListReachListener;
import com.orchidatech.askandanswer.View.Interface.OnPostEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Bahaa on 17/11/2015.
 */
public class TimelineRecViewAdapter extends RecyclerView.Adapter<TimelineRecViewAdapter.PostViewHolder> {
    private static final int TYPE_HEADER = 0;  // Declaring Variable to Understand which View is being worked on
    private static final int TYPE_FOOTER = 1;
    private final View parent;
    private ProgressBar pv_load;
    private Button btn_reload;
    private ArrayList<Posts> posts;
    private Activity activity;
    private boolean loading = false;
    private boolean isFoundData = true;
    private OnPostEventListener pe_listener;
    OnLastListReachListener lastListReachListener;


    public TimelineRecViewAdapter(Activity activity, ArrayList<Posts> posts, View parent,
                                  OnPostEventListener pe_listener, OnLastListReachListener lastListReachListener) {
        this.activity = activity;
        this.posts = posts;
        this.parent = parent;
        this.pe_listener = pe_listener;
        this.lastListReachListener = lastListReachListener;
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
            btn_reload.setVisibility(View.GONE);
            if (!loading && isFoundData && posts.size() > 0) {
                pv_load.setVisibility(View.VISIBLE);
                loading = true;
                lastListReachListener.onReached();
            }
        } else {
            Posts currentPost = posts.get(position);
            Users postOwner = UsersDAO.getUser(currentPost.getUserID());
            Category postCategory = CategoriesDAO.getCategory(currentPost.getCategoryID());
            holder.tv_post_category.setText(postCategory.getName());
            holder.tv_person_name.setText(postOwner.getFname() + " " + postOwner.getLname());
            holder.tv_postDate.setText(GNLConstants.DateConversion.getDate(currentPost.getDate()));
            holder.tv_postContent.setText(currentPost.getText());
            String postImage = currentPost.getImage();
            if(postImage!=null && postImage.length()>0)
                 Picasso.with(activity).load(Uri.parse(currentPost.getImage())).into(holder.iv_postImage);
            Picasso.with(activity).load(Uri.parse(postOwner.getImage())).into(holder.iv_profile);
        }
    }

    @Override
    public int getItemCount() {
        return posts.size() + 1;
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
            if (viewType == TYPE_FOOTER) {
                pv_load = (ProgressBar) itemView.findViewById(R.id.pv_load);
                pv_load.getIndeterminateDrawable().setColorFilter(Color.parseColor("#249885"), android.graphics.PorterDuff.Mode.MULTIPLY);
                btn_reload = (Button) itemView.findViewById(R.id.btn_reload);
                btn_reload.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        v.setVisibility(View.GONE);
                        pv_load.setVisibility(View.VISIBLE);
                        loading = true;
                        lastListReachListener.onReached();
                    }
                });
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
                        pe_listener.onCategoryClick(posts.get(getAdapterPosition()).getCategoryID(), posts.get(getAdapterPosition()).getUserID());
                    }
                });
                rl_postEvents = (RelativeLayout) itemView.findViewById(R.id.rl_postEvents);
                ll_comment = (LinearLayout) itemView.findViewById(R.id.ll_comment);
                ll_comment.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        pe_listener.onCommentPost(posts.get(getAdapterPosition()).getServerID());
                    }
                });
                ll_share = (LinearLayout) itemView.findViewById(R.id.ll_share);
                ll_share.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        pe_listener.onSharePost(posts.get(getAdapterPosition()).getServerID());
                    }
                });
                ll_favorite = (LinearLayout) itemView.findViewById(R.id.ll_favorite);
                ll_favorite.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        pe_listener.onFavoritePost(getAdapterPosition(), posts.get(getAdapterPosition()).getServerID(), posts.get(getAdapterPosition()).getUserID());
                    }
                });
            }

        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == posts.size())
            return TYPE_FOOTER;
        return TYPE_HEADER;
    }
    public void addFromServer(ArrayList<Posts> newPosts, boolean isErrorConnection) {
        if (newPosts != null && newPosts.size() > 0) {
            posts.addAll(newPosts);
            pv_load.setVisibility(View.VISIBLE);
            isFoundData = true;
            notifyDataSetChanged();
        } else {
            if(isErrorConnection){
                btn_reload.setVisibility(View.VISIBLE);
                pv_load.setVisibility(View.GONE);
            }else {
                pv_load.setVisibility(View.GONE);
                btn_reload.setVisibility(View.GONE);
                isFoundData = false;
                AppSnackBar.show(parent, activity.getString(R.string.BR_GNL_005), Color.RED, Color.WHITE);
            }
        }
        loading = false;
    }

    public void addFromLocal(ArrayList<Posts> newPosts){
        posts.addAll(newPosts);
        pv_load.setVisibility(View.GONE);
        isFoundData = false;
        notifyDataSetChanged();
    }
}
