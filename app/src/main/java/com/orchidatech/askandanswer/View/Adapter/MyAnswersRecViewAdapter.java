package com.orchidatech.askandanswer.View.Adapter;

import android.app.Activity;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.orchidatech.askandanswer.Activity.SplashScreen;
import com.orchidatech.askandanswer.Constant.*;
import com.orchidatech.askandanswer.Constant.Enum;
import com.orchidatech.askandanswer.Database.DAO.CategoriesDAO;
import com.orchidatech.askandanswer.Database.DAO.CommentsDAO;
import com.orchidatech.askandanswer.Database.DAO.PostsDAO;
import com.orchidatech.askandanswer.Database.DAO.User_ActionsDAO;
import com.orchidatech.askandanswer.Database.DAO.UsersDAO;
import com.orchidatech.askandanswer.Database.Model.Category;
import com.orchidatech.askandanswer.Database.Model.Comments;
import com.orchidatech.askandanswer.Database.Model.Posts;
import com.orchidatech.askandanswer.Database.Model.User_Actions;
import com.orchidatech.askandanswer.Database.Model.Users;
import com.orchidatech.askandanswer.R;
import com.orchidatech.askandanswer.View.Interface.OnCommentActionListener;
import com.orchidatech.askandanswer.View.Interface.OnCommentOptionListener;
import com.orchidatech.askandanswer.View.Interface.OnLastListReachListener;
import com.orchidatech.askandanswer.View.Interface.OnUserActionsListener;
import com.orchidatech.askandanswer.WebService.WebServiceFunctions;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Bahaa on 18/11/2015.
 */
public class MyAnswersRecViewAdapter extends RecyclerView.Adapter<MyAnswersRecViewAdapter.AnswerViewHolder> {
    private static final int TYPE_HEADER = 0;  // Declaring Variable to Understand which View is being worked on
    private static final int TYPE_FOOTER = 1;
    private OnCommentOptionListener commentOptionListener;
    private View parent;
    private OnUserActionsListener listener;
    private ProgressBar pv_load;
    private Button btn_reload;
    private List<Comments> comments;
    private Activity activity;
    private boolean loading = false;
    private boolean isFoundData = true;
    private final OnLastListReachListener lastListReachListener;
    private long current_user_id;


    public MyAnswersRecViewAdapter(Activity activity, List<Comments> comments, View parent, OnLastListReachListener lastListReachListener, OnCommentOptionListener commentOptionListener) {
        this.activity = activity;
        this.parent = parent;
        this.comments = comments;
        this.lastListReachListener = lastListReachListener;
        this.commentOptionListener = commentOptionListener;
        current_user_id = SplashScreen.pref.getLong(GNLConstants.SharedPreference.ID_KEY, -1);
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
    public void onBindViewHolder(final AnswerViewHolder holder, final int position) {
        if (holder.viewType == TYPE_FOOTER) {
            btn_reload.setVisibility(View.GONE);
            if (!loading && isFoundData && comments.size() > 0) {
                pv_load.setVisibility(View.VISIBLE);
                loading = true;
                lastListReachListener.onReached();
            }
        }else {
            final com.orchidatech.askandanswer.Database.Model.Comments currentComment = comments.get(position);
            Users commentOwner = UsersDAO.getUser(currentComment.getUserID());
            Posts commentPost = PostsDAO.getPost(currentComment.getPostID());
            User_Actions user_actions = User_ActionsDAO.getUserAction(SplashScreen.pref.getLong(GNLConstants.SharedPreference.ID_KEY, -1), currentComment.getServerID());
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
            holder.tv_likes.setText(currentComment.getLikes() + "");
//            holder.tv_likes.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    listener.onLike(comments.get(position).getServerID());
//                }
//            });
//            holder.tv_unlikes.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    listener.onDislike(comments.get(position).getServerID());
//                }
//            });
            if(user_actions != null && user_actions.getActionType() == Enum.USER_ACTIONS.LIKE.getNumericType()){
                holder.iv_unlike.setImageResource(R.drawable.unlike);
                holder.iv_like.setImageResource(R.drawable.ic_like_on);
            }else if(user_actions != null && user_actions.getActionType() == Enum.USER_ACTIONS.DISLIKE.getNumericType()){
                holder.iv_unlike.setImageResource(R.drawable.ic_dislike_on);
                holder.iv_like.setImageResource(R.drawable.like);
            }else{
                holder.iv_unlike.setImageResource(R.drawable.unlike);
                holder.iv_like.setImageResource(R.drawable.like);
            }
            holder.ll_like.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.i("vcvc", currentComment.getServerID() + "");
                    likeComment(currentComment.getServerID(), SplashScreen.pref.getLong(GNLConstants.SharedPreference.ID_KEY, -1), position, holder.iv_like, holder.iv_unlike, holder.tv_unlikes, holder.tv_likes);
//                    listener.onLike(currentComment.getServerID(), position);
                }
            });
            holder.ll_unlike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.i("vcvc", currentComment.getServerID() + "");
                    dislikeComment(currentComment.getServerID(), SplashScreen.pref.getLong(GNLConstants.SharedPreference.ID_KEY, -1), position, holder.iv_like, holder.iv_unlike, holder.tv_unlikes, holder.tv_likes);
//                   listener.onDislike(currentComment.getServerID(), position);
                }
            });
            holder.iv_comment_options.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PopupMenu pop = new PopupMenu(activity.getApplicationContext(), v);
                    pop.inflate(R.menu.comment_menu);
                    pop.show();
                    pop.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            int id = item.getItemId();
                         /*   if(id == R.id.edit_comment){
                                commentOptionListener.onEditComment(comments.get(position).getServerID());
                                return true;
                            }else */if(id == R.id.delete_comment){
                                commentOptionListener.onDeleteComment(comments.get(position).getServerID());
                                return true;
                            }
                            return false;
                        }
                    });

                }
            });


        }    }

    @Override
    public int getItemCount() {
        return comments.size() + 1;
    }

    public class AnswerViewHolder extends RecyclerView.ViewHolder {
        ImageView iv_comment_options;
        CircleImageView iv_person;
        TextView tv_person_name;
        RatingBar rating_comment;
        TextView tv_commentDate;
        TextView tv_comment_category;
        TextView tv_commentDesc;
        TextView tv_likes;
        TextView tv_unlikes;
        LinearLayout ll_unlike;
        LinearLayout ll_like;
        ImageView iv_comment;
        ImageView iv_like;
        ImageView iv_unlike;
        RelativeLayout rl_comment_item;
        int viewType;

        public AnswerViewHolder(View itemView, int viewType) {
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
                iv_like = (ImageView) itemView.findViewById(R.id.iv_like);
                iv_unlike = (ImageView) itemView.findViewById(R.id.iv_unlike);
                ll_like = (LinearLayout) itemView.findViewById(R.id.ll_like);
                ll_unlike = (LinearLayout) itemView.findViewById(R.id.ll_unlike);
                iv_comment_options = (ImageView) itemView.findViewById(R.id.iv_comment_options);
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
            Log.i("fdfd", comments.size() + "");
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
    public void addFromLocal(ArrayList<com.orchidatech.askandanswer.Database.Model.Comments> myComments) {
        comments.addAll(myComments);
        if(pv_load != null)
             pv_load.setVisibility(View.GONE);
        isFoundData = false;
        notifyDataSetChanged();
    }

    public void addComment(com.orchidatech.askandanswer.Database.Model.Comments comment) {
        comments.add(0, comment);
        notifyDataSetChanged();
    }

    private void dislikeComment(long commentId, long user_id, final int position, final ImageView iv_like, final ImageView iv_unlike, final TextView tv_unlikes, final TextView tv_likes) {
        final User_Actions user_actions = User_ActionsDAO.getUserAction(SplashScreen.pref.getLong(GNLConstants.SharedPreference.ID_KEY, -1), commentId);
        final int prevAction = user_actions == null ? -1 : user_actions.getActionType();
        final int action = (user_actions == null || user_actions.getActionType() != Enum.USER_ACTIONS.DISLIKE.getNumericType()) ? 1 : 2;
        final com.orchidatech.askandanswer.Database.Model.Comments comment = CommentsDAO.getComment(commentId);
        WebServiceFunctions.addCommentAction(activity, commentId, user_id, action, new OnCommentActionListener() {
            @Override
            public void onActionSent(User_Actions user_actions) {
                AppSnackBar.show(parent, "dislike", Color.RED, Color.WHITE);

                if (prevAction == -1 || prevAction == Enum.USER_ACTIONS.NO_ACTIONS.getNumericType()) {
                    comment.disLikes++;
                    iv_like.setImageResource(R.drawable.like);
                    iv_unlike.setImageResource(R.drawable.ic_dislike_on);

                } else if (prevAction == Enum.USER_ACTIONS.LIKE.getNumericType()) {
                    comment.likes--;
                    comment.disLikes++;
                    iv_like.setImageResource(R.drawable.like);
                    iv_unlike.setImageResource(R.drawable.ic_dislike_on);
                } else if (prevAction == Enum.USER_ACTIONS.DISLIKE.getNumericType()) {
                    comment.disLikes--;
                    iv_like.setImageResource(R.drawable.like);
                    iv_unlike.setImageResource(R.drawable.unlike);
                }
                tv_likes.setText(comment.likes + "");
                tv_unlikes.setText(comment.disLikes + "");
                comments.get(position).disLikes = comment.disLikes;
                comments.get(position).likes = comment.likes;
                CommentsDAO.updateComment(comment);
//                        Log.i("vcvc", "dilike: " + comment.likes + " , " + comment.disLikes + ",---" + user_actions.getActionType() + ", " + action);
//                        adapter.notifyDataSetChanged();
            }

            @Override
            public void onFail(String error) {
                AppSnackBar.show(parent, error, Color.RED, Color.WHITE);
            }
        });
    }

    private void likeComment(long commentId, long user_id, final int position, final ImageView iv_like, final ImageView iv_unlike, final TextView tv_unlikes, final TextView tv_likes) {
        final User_Actions user_actions = User_ActionsDAO.getUserAction(SplashScreen.pref.getLong(GNLConstants.SharedPreference.ID_KEY, -1), commentId);
        final int prevAction = user_actions == null ? -1 : user_actions.getActionType();
        final int action = (user_actions == null || user_actions.getActionType() != Enum.USER_ACTIONS.LIKE.getNumericType()) ? 0 : 2;
        final com.orchidatech.askandanswer.Database.Model.Comments comment = CommentsDAO.getComment(commentId);

        WebServiceFunctions.addCommentAction(activity, commentId, user_id, action, new OnCommentActionListener() {

            @Override
            public void onActionSent(User_Actions user_actions) {
                AppSnackBar.show(parent, "like", Color.RED, Color.WHITE);

                if (prevAction == -1 || prevAction == Enum.USER_ACTIONS.NO_ACTIONS.getNumericType()) {
                    comment.likes++;
                    Log.i("vcvc", "null: " + comment.likes);
                    iv_like.setImageResource(R.drawable.ic_like_on);
                    iv_unlike.setImageResource(R.drawable.unlike);

                } else if (prevAction == Enum.USER_ACTIONS.DISLIKE.getNumericType()) {
                    comment.disLikes--;
                    comment.likes++;
                    iv_like.setImageResource(R.drawable.ic_like_on);
                    iv_unlike.setImageResource(R.drawable.unlike);
                    Log.i("vcvc", "like: " + comment.likes + " , " + comment.disLikes);
                } else if (prevAction == Enum.USER_ACTIONS.LIKE.getNumericType()) {
                    comment.likes--;
                    Log.i("vcvc", "dilike: " + comment.likes);
                    iv_like.setImageResource(R.drawable.like);
                    iv_unlike.setImageResource(R.drawable.unlike);
                }
//                        comments.get(position).setDisLikes(comment.disLikes);
//                        comments.get(position).setLikes(comment.likes);
                tv_likes.setText(comment.likes + "");
                tv_unlikes.setText(comment.disLikes + "");
                comments.get(position).disLikes = comment.disLikes;
                comments.get(position).likes = comment.likes;
                Log.i("vcvc", "like: " + comment.likes + " , " + comment.disLikes + ",---" + prevAction + ", " + action);
                CommentsDAO.updateComment(comment);

//                        adapter.notifyDataSetChanged();
            }

            @Override
            public void onFail(String error) {
                AppSnackBar.show(parent, error, Color.RED, Color.WHITE);
            }
        });
    }


}
