package com.orchidatech.askandanswer.View.Adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.orchidatech.askandanswer.Constant.AppSnackBar;
import com.orchidatech.askandanswer.Constant.GNLConstants;
import com.orchidatech.askandanswer.Database.DAO.CategoriesDAO;
import com.orchidatech.askandanswer.Database.DAO.PostsDAO;
import com.orchidatech.askandanswer.Database.DAO.UsersDAO;
import com.orchidatech.askandanswer.Database.Model.Category;
import com.orchidatech.askandanswer.Database.Model.Posts;
import com.orchidatech.askandanswer.Database.Model.Users;
import com.orchidatech.askandanswer.Fragment.Comments;
import com.orchidatech.askandanswer.R;
import com.orchidatech.askandanswer.View.Interface.OnLastListReachListener;
import com.orchidatech.askandanswer.View.Interface.OnUserActionsListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Bahaa on 15/11/2015.
 */
public class CommentsRecViewAdapter extends RecyclerView.Adapter<CommentsRecViewAdapter.CommentsViewHolder> {
    private static final int TYPE_HEADER = 0;  // Declaring Variable to Understand which View is being worked on
    private static final int TYPE_FOOTER = 1;
    private View parent;
    private OnUserActionsListener listener;
    private ProgressBar pv_load;
    private Button btn_reload;
    private ArrayList<com.orchidatech.askandanswer.Database.Model.Comments> comments;
    private Activity activity;
    private boolean loading = false;
    private boolean isFoundData = true;
    private final OnLastListReachListener lastListReachListener;
    public CommentsRecViewAdapter(Activity activity, ArrayList<com.orchidatech.askandanswer.Database.Model.Comments> comments, View parent, OnUserActionsListener listener, OnLastListReachListener lastListReachListener) {
        this.activity = activity;
        this.parent = parent;
        this.listener = listener;
        this.comments = comments;
        this.lastListReachListener = lastListReachListener;
    }


    @Override
    public CommentsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = null;
        if (viewType == TYPE_FOOTER) {
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.footer_progress, parent, false);
            return new CommentsViewHolder(itemView, TYPE_FOOTER);
        } else {
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_item, parent, false);
            return new CommentsViewHolder(itemView, TYPE_HEADER);
        }

    }

    @Override
    public void onBindViewHolder(CommentsViewHolder holder, final int position) {
        if (holder.viewType == TYPE_FOOTER) {
            btn_reload.setVisibility(View.GONE);
            if (!loading && isFoundData && comments.size() > 0) {
                pv_load.setVisibility(View.VISIBLE);
                loading = true;
                lastListReachListener.onReached();
            }
        }else {
            com.orchidatech.askandanswer.Database.Model.Comments currentComment = comments.get(position);
            Users commentOwner = UsersDAO.getUser(currentComment.getUserID());
            Posts commentPost = PostsDAO.getPost(currentComment.getPostID());
            Category commentCategory = CategoriesDAO.getCategory(commentPost.getCategoryID());
            holder.tv_commentDate.setText(GNLConstants.DateConversion.getDate(currentComment.getDate()));
            holder.tv_commentDesc.setText(currentComment.getText());
            holder.tv_person_name.setText(commentOwner.getFname() + " " + commentOwner.getLname());
            holder.tv_comment_category.setText(commentCategory.getName());
            Picasso.with(activity).load(Uri.parse(commentOwner.getImage())).into(holder.iv_person);
            if (!TextUtils.isEmpty(currentComment.getImage())) {
                Picasso.with(activity).load(Uri.parse(currentComment.getImage())).into(holder.iv_comment);
                holder.iv_comment.setVisibility(View.VISIBLE);
            } else
                holder.iv_comment.setVisibility(View.GONE);
            holder.tv_unlikes.setText(currentComment.getDisLikes()+"");
            holder.tv_likes.setText(currentComment.getLikes()+"");
            holder.tv_likes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onLike(comments.get(position).getServerID());
                }
            });
            holder.tv_unlikes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onDislike(comments.get(position).getServerID());
                }
            });

        }
    }

    @Override
    public int getItemCount() {
        return comments.size() + 1;
    }


    public class CommentsViewHolder extends RecyclerView.ViewHolder {
        CircleImageView iv_person;
        TextView tv_person_name;
        RatingBar rating_comment;
        TextView tv_commentDate;
        TextView tv_comment_category;
        TextView tv_commentDesc;
        TextView tv_likes;
        TextView tv_unlikes;
        ImageView iv_comment;
        RelativeLayout rl_comment_item;
        int viewType;

        public CommentsViewHolder(View itemView, int viewType) {
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
            }else {
                rl_comment_item = (RelativeLayout) itemView.findViewById(R.id.rl_comment_item);
                iv_person = (CircleImageView) itemView.findViewById(R.id.iv_person);
                iv_comment = (ImageView) itemView.findViewById(R.id.iv_comment);
                tv_person_name = (TextView) itemView.findViewById(R.id.tv_person_name);
                rating_comment = (RatingBar) itemView.findViewById(R.id.rating_comment);
                tv_commentDate = (TextView) itemView.findViewById(R.id.tv_commentDate);
                tv_comment_category = (TextView) itemView.findViewById(R.id.tv_comment_category);
                tv_commentDesc = (TextView) itemView.findViewById(R.id.tv_comDesc);
                tv_likes = (TextView) itemView.findViewById(R.id.tv_likes);
                tv_unlikes = (TextView) itemView.findViewById(R.id.tv_unlikes);
            }
        }

    }

    @Override
    public int getItemViewType(int position) {
        if (position == comments.size())
            return TYPE_FOOTER;
        return TYPE_HEADER;
    }


    public void addFromServer(ArrayList<com.orchidatech.askandanswer.Database.Model.Comments> _comments, boolean isErrorConnection) {
        if (_comments != null && _comments.size() > 0) {
            comments.addAll(_comments);
            if(pv_load != null)
                pv_load.setVisibility(View.VISIBLE);
            isFoundData = true;
            notifyDataSetChanged();
        } else {
            if (isErrorConnection) {
                if(pv_load != null && btn_reload != null) {
                    btn_reload.setVisibility(View.VISIBLE);
                    pv_load.setVisibility(View.GONE);
                }
            } else {
                if(pv_load != null && btn_reload != null) {
                    pv_load.setVisibility(View.GONE);
                    btn_reload.setVisibility(View.GONE);
                }
                isFoundData = false;
                AppSnackBar.show(parent, activity.getString(R.string.BR_GNL_005), Color.RED, Color.WHITE);
            }
        }
        loading = false;
    }
    public void addFromLocal(ArrayList<com.orchidatech.askandanswer.Database.Model.Comments> postComments) {
        comments.addAll(postComments);
        if(pv_load != null)
            pv_load.setVisibility(View.GONE);
        isFoundData = false;
        notifyDataSetChanged();
    }

    public void addComment(com.orchidatech.askandanswer.Database.Model.Comments comment) {
        comments.add(comment);
        notifyDataSetChanged();
    }
}
