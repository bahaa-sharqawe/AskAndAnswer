package com.orchidatech.askandanswer.View.Adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.internal.cu;
import com.orchidatech.askandanswer.Activity.SplashScreen;
import com.orchidatech.askandanswer.Constant.*;
import com.orchidatech.askandanswer.Constant.Enum;
import com.orchidatech.askandanswer.Database.DAO.CategoriesDAO;
import com.orchidatech.askandanswer.Database.DAO.CommentsDAO;
import com.orchidatech.askandanswer.Database.DAO.PostsDAO;
import com.orchidatech.askandanswer.Database.DAO.User_ActionsDAO;
import com.orchidatech.askandanswer.Database.DAO.UsersDAO;
import com.orchidatech.askandanswer.Database.Model.Category;
import com.orchidatech.askandanswer.Database.Model.Posts;
import com.orchidatech.askandanswer.Database.Model.User_Actions;
import com.orchidatech.askandanswer.Database.Model.Users;
import com.orchidatech.askandanswer.Fragment.CommentOptionsDialog;
import com.orchidatech.askandanswer.Fragment.Comments;
import com.orchidatech.askandanswer.Fragment.DeleteComment;
import com.orchidatech.askandanswer.Fragment.LoadingDialog;
import com.orchidatech.askandanswer.R;
import com.orchidatech.askandanswer.View.Interface.OnCommentActionListener;
import com.orchidatech.askandanswer.View.Interface.OnCommentOptionListener;
import com.orchidatech.askandanswer.View.Interface.OnDeleteCommentListener;
import com.orchidatech.askandanswer.View.Interface.OnLastListReachListener;
import com.orchidatech.askandanswer.WebService.WebServiceFunctions;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import dmax.dialog.SpotsDialog;

/**
 * Created by Bahaa on 15/11/2015.
 */
public class CommentsRecViewAdapter extends RecyclerView.Adapter<CommentsRecViewAdapter.CommentsViewHolder> implements View.OnCreateContextMenuListener {
    private static final int TYPE_HEADER = 0;  // Declaring Variable to Understand which View is being worked on
    private static final int TYPE_FOOTER = 1;
    private final int fragment_numeric;
    private final long current_user_id;
    private final Animation mAnimation;
    private final Map<com.orchidatech.askandanswer.Database.Model.Comments, Integer> data;
    private  SharedPreferences pref;
    private  OnCommentOptionListener commentOptionListener;
    private View parent;
    private ProgressBar pv_load;
    private Button btn_reload;
    private List<com.orchidatech.askandanswer.Database.Model.Comments> comments;
    private Activity activity;
    private boolean loading = false;
    private boolean isFoundData = true;
    private final OnLastListReachListener lastListReachListener;
    private int position;

    public CommentsRecViewAdapter(Activity activity, List<com.orchidatech.askandanswer.Database.Model.Comments> comments, Map<com.orchidatech.askandanswer.Database.Model.Comments, Integer> data, View parent, OnLastListReachListener lastListReachListener,
                                  OnCommentOptionListener commentOptionListener, int fragment_numeric) {
        this.activity = activity;
        this.parent = parent;
        this.comments = comments;
        this.data = data;
        this.lastListReachListener = lastListReachListener;
        this.commentOptionListener = commentOptionListener;
        this.fragment_numeric = fragment_numeric;
        pref = activity.getSharedPreferences(GNLConstants.SharedPreference.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        this.current_user_id = pref.getLong(GNLConstants.SharedPreference.ID_KEY, -1);
        mAnimation = AnimationUtils.loadAnimation(activity, R.anim.zoom_enter);
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
    public void onBindViewHolder(final CommentsViewHolder holder, final int position) {
        if (holder.viewType == TYPE_FOOTER) {
            btn_reload.setVisibility(View.GONE);
            if (!loading && isFoundData && (fragment_numeric == Enum.COMMENTS_FRAGMENTS.COMMENTS.getNumericType()?Comments.last_id_server >0:comments.size()>0)) {

                pv_load.setVisibility(View.VISIBLE);
                loading = true;
                lastListReachListener.onReached();
            }
        }else {
            final com.orchidatech.askandanswer.Database.Model.Comments currentComment = comments.get(position);
            final Users commentOwner = UsersDAO.getUser(currentComment.getUserID());
//            Posts commentPost = PostsDAO.getPost(currentComment.getPostID());
//            User_Actions user_actions = User_ActionsDAO.getUserAction(current_user_id, currentComment.getServerID());
//            Category commentCategory = CategoriesDAO.getCategory(commentPost.getCategoryID());
            holder.tv_commentDate.setText(GNLConstants.DateConversion.getDate(currentComment.getDate()));
            holder.tv_commentDesc.setText(currentComment.getText());
            holder.tv_person_name.setText(commentOwner.getFname() + " " + commentOwner.getLname());
            holder.user_rating.setRating(commentOwner.getRating());
//            holder.tv_comment_category.setText(commentCategory.getName());
            if(commentOwner != null && !commentOwner.getImage().equals(URL.DEFAULT_IMAGE)) {
                Picasso.with(activity).load(Uri.parse(commentOwner.getImage())).into(holder.iv_person, new Callback() {
                    @Override
                    public void onSuccess() {
                        holder.tv_person_photo.setVisibility(View.INVISIBLE);
                        holder.iv_person.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onError() {
                        holder.iv_person.setVisibility(View.INVISIBLE);
                        holder.tv_person_photo.setVisibility(View.VISIBLE);
                        holder.tv_person_photo.setText(commentOwner.getFname().charAt(0) + "");
                    }
                });
            }else{
                holder.iv_person.setVisibility(View.INVISIBLE);
                holder.tv_person_photo.setVisibility(View.VISIBLE);
                holder.tv_person_photo.setText(commentOwner.getFname().charAt(0) + "");
            }
//            holder.pb_photo_load.setVisibility(View.VISIBLE);
            holder.iv_comment.setVisibility(View.GONE);
            if(!TextUtils.isEmpty(currentComment.getImage()) && currentComment.getImage() != "null") {
                Picasso.with(activity).load(Uri.parse(currentComment.getImage())).into(holder.iv_comment, new Callback() {
                    @Override
                    public void onSuccess() {
//                        holder.pb_photo_load.setVisibility(View.GONE);
                        holder.iv_comment.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onError() {
//                        holder.pb_photo_load.setVisibility(View.GONE);
                        holder.iv_comment.setVisibility(View.INVISIBLE);
                    }
                });
            }else{
//                holder.pb_photo_load.setVisibility(View.GONE);
                holder.iv_comment.setVisibility(View.GONE);

            }
            holder.tv_unlikes.setText(currentComment.getDisLikes()+"");
            holder.tv_likes.setText(currentComment.getLikes() + "");
            if(comments.get(position).getUser_action() == Enum.USER_ACTIONS.LIKE.getNumericType()){
                    holder.iv_unlike.setImageResource(R.drawable.unlike);
                    holder.iv_like.setImageResource(R.drawable.ic_like_on);
            }else if(comments.get(position).getUser_action() == Enum.USER_ACTIONS.DISLIKE.getNumericType()){
                    holder.iv_unlike.setImageResource(R.drawable.ic_dislike_on);
                    holder.iv_like.setImageResource(R.drawable.like);
            }else{
                    holder.iv_unlike.setImageResource(R.drawable.unlike);
                    holder.iv_like.setImageResource(R.drawable.like);
            }
            holder.ll_like.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mAnimation.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            likeComment(currentComment.getServerID(), position, holder.iv_like, holder.iv_unlike, holder.tv_unlikes, holder.tv_likes);
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }

                    });
                    holder.iv_like.startAnimation(mAnimation);
                }
            });
           holder.ll_unlike.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                   mAnimation.setAnimationListener(new Animation.AnimationListener() {
                       @Override
                       public void onAnimationStart(Animation animation) {

                       }

                       @Override
                       public void onAnimationEnd(Animation animation) {
                           dislikeComment(currentComment.getServerID(), position, holder.iv_like, holder.iv_unlike, holder.tv_unlikes, holder.tv_likes);
                       }

                       @Override
                       public void onAnimationRepeat(Animation animation) {

                       }
                   });
                   holder.iv_unlike.startAnimation(mAnimation);
               }
           });
            holder.rl_comment_item.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    setPosition(position);
                    return false;
                }
            });
//            if(fragment_numeric == Enum.COMMENTS_FRAGMENTS.MY_ANSSWERS.getNumericType()
//   /*                 || (fragment_numeric == Enum.COMMENTS_FRAGMENTS.COMMENTS.getNumericType() &&
//                         comments.get(position).getUserID() == current_user_id)*/)
//               holder.rl_comment_item.setOnCreateContextMenuListener(this);

            holder.iv_comment_options.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    viewOptionsMenu(position, v);
                }
            });
            if(commentOwner.getServerID() == current_user_id){
                holder.iv_comment_options.setVisibility(View.VISIBLE);
            }else
                holder.iv_comment_options.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        MenuInflater inflater = activity.getMenuInflater();
//            menu.setHeaderTitle("Comment Options:");
        inflater.inflate(R.menu.comment_menu, menu);
    }
    private void viewOptionsMenu(final int position, View v) {
        CommentOptionsDialog dialog = new CommentOptionsDialog(new CommentOptionsDialog.OnCommentOptionsListener() {
            @Override
            public void onDelete() {
                deleteComment(position);
            }
        });
        dialog.show(activity.getFragmentManager(), "Options");
//        PopupMenu pop = new PopupMenu(activity, v);
//        pop.inflate(R.menu.comment_menu);
////        pop.getMenu().add(Menu.NONE, R.id.delete_comment, Menu.NONE, "Delete");
//        pop.show();
//        pop.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
//            @Override
//            public boolean onMenuItemClick(MenuItem item) {
//                int id = item.getItemId();
//                /*if(id == R.id.edit_comment){
//                    commentOptionListener.onEditComment(commentId);
//                    return true;
//                }else */
//                if (id == R.id.delete_comment) {
//                    deleteComment(position);
//                    return true;
//                }
//                return false;
//            }
//        });
    }

    private void deleteComment(final int position) {
                DeleteComment deletePost = new DeleteComment(new DeleteComment.OnDeleteListener(){

                    @Override
                    public void onDelete() {
                        performDeleting(position);
                    }
                });
        deletePost.show(activity.getFragmentManager(), activity.getString(R.string.delete_comment));
    }

    public void performDeleting(final int position) {
//        final LoadingDialog loadingDialog = new LoadingDialog();
//        Bundle args = new Bundle();
//        args.putString(LoadingDialog.DIALOG_TEXT_KEY, activity.getString(R.string.delteing));
//        loadingDialog.setArguments(args);
//        loadingDialog.setCancelable(false);
//        loadingDialog.show(activity.getFragmentManager(), "deleting");
        Log.i("vcvc1", comments.size() + "");
        final AlertDialog dialog = new SpotsDialog(activity, activity.getString(R.string.delteing), R.style.SpotsDialogCustom);
        dialog.setCancelable(false);
        dialog.show();



        WebServiceFunctions.deletComment(activity, current_user_id, comments.get(position).getServerID(), new OnDeleteCommentListener() {

            @Override
            public void onDeleted() {
                dialog.dismiss();
                Log.i("vcvc2", comments.size() + "");
                data.remove(comments.get(position));
                comments.remove(position);
                notifyDataSetChanged();
                AppSnackBar.show(parent, activity.getResources().getString(R.string.deleted), activity.getResources().getColor(R.color.colorPrimary), Color.WHITE);
            }

            @Override
            public void onFail(String error) {
                dialog.dismiss();
                AppSnackBar.show(parent, error, Color.RED, Color.WHITE);
            }
        });
    }

    @Override
    public int getItemCount() {
        return comments.size() + 1;
    }




    public class CommentsViewHolder extends RecyclerView.ViewHolder {
        ImageView iv_comment_options;
        CircleImageView iv_person;
        TextView tv_person_photo;
        TextView tv_person_name;
        RatingBar rating_comment;
        TextView tv_commentDate;
//        TextView tv_comment_category;
        TextView tv_commentDesc;
        TextView tv_likes;
        TextView tv_unlikes;
        LinearLayout ll_unlike;
        LinearLayout ll_like;
        ImageView iv_comment;
        ImageView iv_like;
        ImageView iv_unlike;
        RelativeLayout rl_comment_item;
        RelativeLayout card_comment;
        RatingBar user_rating;
//        ProgressBar pb_photo_load;
        int viewType;

        public CommentsViewHolder(View itemView, int viewType) {
            super(itemView);
            this.viewType = viewType;
            if (viewType == TYPE_FOOTER) {

                pv_load = (ProgressBar) itemView.findViewById(R.id.pv_load);
                pv_load.getIndeterminateDrawable().setColorFilter(Color.parseColor("#249885"), android.graphics.PorterDuff.Mode.MULTIPLY);
                btn_reload = (Button) itemView.findViewById(R.id.btn_reload);
                btn_reload.setVisibility(View.GONE);

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
                rating_comment = (RatingBar) itemView.findViewById(R.id.rating_user);
                tv_commentDate = (TextView) itemView.findViewById(R.id.tv_commentDate);
                tv_person_photo = (TextView) itemView.findViewById(R.id.tv_person_photo);
//                tv_comment_category = (TextView) itemView.findViewById(R.id.tv_comment_category);
                tv_commentDesc = (TextView) itemView.findViewById(R.id.tv_comDesc);
                tv_likes = (TextView) itemView.findViewById(R.id.tv_likes);
                tv_unlikes = (TextView) itemView.findViewById(R.id.tv_unlikes);
                iv_like = (ImageView) itemView.findViewById(R.id.iv_like);
                iv_unlike = (ImageView) itemView.findViewById(R.id.iv_unlike);
                ll_like = (LinearLayout) itemView.findViewById(R.id.ll_like);
                ll_unlike = (LinearLayout) itemView.findViewById(R.id.ll_unlike);
                iv_comment_options = (ImageView) itemView.findViewById(R.id.iv_comment_options);
                card_comment = (RelativeLayout ) itemView.findViewById(R.id.card_comment);
                user_rating = (RatingBar) itemView.findViewById(R.id.rating_user);
                Log.i("Ffhghg", getAdapterPosition()+"");
//                pb_photo_load = (ProgressBar) itemView.findViewById(R.id.pb_photo_load);
//                pb_photo_load.getIndeterminateDrawable().setColorFilter(Color.parseColor("#249885"), android.graphics.PorterDuff.Mode.MULTIPLY);
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
//            if(comments.size() > GNLConstants.MAX_COMMENTS_ROWS)
//                comments = comments.subList(comments.size()-GNLConstants.MAX_COMMENTS_ROWS, comments.size());
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
//                AppSnackBar.show(parent, activity.getString(R.string.BR_GNL_005), Color.RED, Color.WHITE);
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
        comments.add(0, comment);
        data.put(comment, 2);
        notifyDataSetChanged();
    }

    private void dislikeComment(long commentId, final int position, final ImageView iv_like, final ImageView iv_unlike, final TextView tv_unlikes, final TextView tv_likes) {
//        final User_Actions user_actions = User_ActionsDAO.getUserAction(current_user_id, commentId);
        final int prevAction = Integer.valueOf(comments.get(position).user_action);
        final int action = (prevAction != Enum.USER_ACTIONS.DISLIKE.getNumericType()) ? 1 : 2;
    //////////////////////////////////////////////////////////////
        int currentLikes = Integer.parseInt(tv_likes.getText().toString());
        int currentDisLikes = Integer.parseInt(tv_unlikes.getText().toString());
        if (prevAction == Enum.USER_ACTIONS.NO_ACTIONS.getNumericType()) {
            currentDisLikes++;
            iv_like.setImageResource(R.drawable.like);
            iv_unlike.setImageResource(R.drawable.ic_dislike_on);
//            if(user_actions != null) {
//                user_actions.actionType = Enum.USER_ACTIONS.DISLIKE.getNumericType();
//                user_actions.save();
//            }

        } else if (prevAction == Enum.USER_ACTIONS.LIKE.getNumericType()) {
            currentLikes--;
            currentDisLikes++;
            iv_like.setImageResource(R.drawable.like);
            iv_unlike.setImageResource(R.drawable.ic_dislike_on);
//            if(user_actions != null) {
//                user_actions.actionType = Enum.USER_ACTIONS.LIKE.getNumericType();
//                user_actions.save();
//            }
        } else if (prevAction == Enum.USER_ACTIONS.DISLIKE.getNumericType()) {
            currentDisLikes--;
            iv_like.setImageResource(R.drawable.like);
            iv_unlike.setImageResource(R.drawable.unlike);
//            if(user_actions != null) {
//                user_actions.actionType = Enum.USER_ACTIONS.NO_ACTIONS.getNumericType();
//                 user_actions.save();
//            }

        }
        tv_likes.setText(currentLikes + "");
        tv_unlikes.setText(currentDisLikes + "");
        comments.get(position).disLikes = currentDisLikes;
        comments.get(position).likes = currentLikes;
        comments.get(position).user_action =  action;
        CommentsDAO.updateComment(comments.get(position));

        //////////////////////////////////////////////////////////////

        WebServiceFunctions.addCommentAction(activity, commentId, current_user_id, action, new OnCommentActionListener() {
            @Override
            public void onActionSent(User_Actions user_actions) {
            }

            @Override
            public void onFail(String error) {
                AppSnackBar.show(parent, error, Color.RED, Color.WHITE);
            }
        });
    }

    private void likeComment(long commentId, final int position, final ImageView iv_like, final ImageView iv_unlike, final TextView tv_unlikes, final TextView tv_likes) {
//        final User_Actions user_actions = User_ActionsDAO.getUserAction(current_user_id, commentId);
        final int prevAction = comments.get(position).user_action;
        final int action = prevAction != Enum.USER_ACTIONS.LIKE.getNumericType() ? 0 : 2;
       ////////////////////////////////////////////////
        int currentLikes = Integer.parseInt(tv_likes.getText().toString());
        int currentDisLikes = Integer.parseInt(tv_unlikes.getText().toString());
        if (prevAction == Enum.USER_ACTIONS.NO_ACTIONS.getNumericType()) {
            currentLikes++;
            iv_like.setImageResource(R.drawable.ic_like_on);
            iv_unlike.setImageResource(R.drawable.unlike);
//            if(user_actions != null) {
//                user_actions.actionType = Enum.USER_ACTIONS.LIKE.getNumericType();
//                user_actions.save();
//            }
        } else if (prevAction == Enum.USER_ACTIONS.DISLIKE.getNumericType()) {
            currentDisLikes--;
            currentLikes++;
            iv_like.setImageResource(R.drawable.ic_like_on);
            iv_unlike.setImageResource(R.drawable.unlike);
//            if(user_actions != null) {
//                user_actions.actionType = Enum.USER_ACTIONS.LIKE.getNumericType();
//                user_actions.save();
//            }
        } else if (prevAction == Enum.USER_ACTIONS.LIKE.getNumericType()) {
            currentLikes--;
            iv_like.setImageResource(R.drawable.like);
            iv_unlike.setImageResource(R.drawable.unlike);
//            if(user_actions != null) {
//                user_actions.actionType = Enum.USER_ACTIONS.NO_ACTIONS.getNumericType();
//                user_actions.save();
//            }
        }
        tv_likes.setText(currentLikes + "");
        tv_unlikes.setText(currentDisLikes + "");
        comments.get(position).disLikes = currentDisLikes;
        comments.get(position).likes = currentLikes;
        comments.get(position).user_action = action;
        CommentsDAO.updateComment(comments.get(position));

        ////////////////////////////////////////////

        WebServiceFunctions.addCommentAction(activity, commentId, current_user_id, action, new OnCommentActionListener() {

            @Override
            public void onActionSent(User_Actions user_actions) {
            }

            @Override
            public void onFail(String error) {
//                AppSnackBar.show(pawrent, error, Color.RED, Color.WHITE);
            }
        });
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

}
