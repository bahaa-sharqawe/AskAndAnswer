package com.orchidatech.askandanswer.View.Adapter;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.orchidatech.askandanswer.Activity.CategoryPosts;
import com.orchidatech.askandanswer.Activity.MainScreen;
import com.orchidatech.askandanswer.Activity.SplashScreen;
import com.orchidatech.askandanswer.Activity.ViewPost;
import com.orchidatech.askandanswer.Constant.*;
import com.orchidatech.askandanswer.Constant.Enum;
import com.orchidatech.askandanswer.Database.DAO.CategoriesDAO;
import com.orchidatech.askandanswer.Database.DAO.Post_FavoriteDAO;
import com.orchidatech.askandanswer.Database.DAO.PostsDAO;
import com.orchidatech.askandanswer.Database.DAO.UsersDAO;
import com.orchidatech.askandanswer.Database.Model.Category;
import com.orchidatech.askandanswer.Database.Model.Post_Favorite;
import com.orchidatech.askandanswer.Database.Model.Posts;
import com.orchidatech.askandanswer.Database.Model.Users;
import com.orchidatech.askandanswer.Fragment.Comments;
import com.orchidatech.askandanswer.Fragment.Profile;
import com.orchidatech.askandanswer.R;
import com.orchidatech.askandanswer.View.Animation.ViewAnimation;
import com.orchidatech.askandanswer.View.Interface.OnLastListReachListener;
import com.orchidatech.askandanswer.View.Interface.OnPostEventListener;
import com.orchidatech.askandanswer.View.Interface.OnPostFavoriteListener;
import com.orchidatech.askandanswer.View.Interface.OnUserActionsListener;
import com.orchidatech.askandanswer.WebService.WebServiceFunctions;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Bahaa on 18/11/2015.
 */
public class MyFavoritesRecViewAdapter extends RecyclerView.Adapter<MyFavoritesRecViewAdapter.FavoriteViewHolder> {
    private static final int TYPE_HEADER = 0;  // Declaring Variable to Understand which View is being worked on
    private static final int TYPE_FOOTER = 1;
    private final OnLastListReachListener lastListReachListener;
    private final Animation mAnimation;
    private SharedPreferences pref;
    private View parent;
    private ProgressBar pv_load;
    private Button btn_reload;
    private ArrayList<Post_Favorite> posts;
    private Activity activity;
    private boolean loading = false;
    private boolean isFoundData = true;
    private boolean isCommentDialogShown = Boolean.FALSE;

    public MyFavoritesRecViewAdapter(Activity activity, ArrayList<Post_Favorite> posts, View parent,
                                      OnLastListReachListener lastListReachListener) {
        this.activity = activity;
        this.posts = posts;
        this.parent = parent;
        this.lastListReachListener = lastListReachListener;
        pref = activity.getSharedPreferences(GNLConstants.SharedPreference.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        mAnimation = AnimationUtils.loadAnimation(activity, R.anim.zoom_enter);

    }

    @Override
    public FavoriteViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;
        if (viewType == TYPE_FOOTER) {
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.footer_progress, parent, false);
            return new FavoriteViewHolder(itemView, TYPE_FOOTER);
        } else {
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_item, parent, false);
            return new FavoriteViewHolder(itemView, TYPE_HEADER);
        }
    }

    @Override
    public void onBindViewHolder(final FavoriteViewHolder holder, final int position) {

        if (holder.viewType == TYPE_FOOTER) {
            btn_reload.setVisibility(View.GONE);
            if (!loading && isFoundData && posts.size() > 0) {
                pv_load.setVisibility(View.VISIBLE);
                loading = true;
                lastListReachListener.onReached();
            }
        }else {
            Post_Favorite currentPostFavorite = posts.get(position);
            final Posts currentPost = PostsDAO.getPost(currentPostFavorite.getPostID());
            final Users postOwner = UsersDAO.getUser(currentPost.getUserID());

            final Category postCategory = CategoriesDAO.getCategory(currentPost.getCategoryID());
            holder.tv_post_category.setText(postCategory.getName());
            holder.tv_person_name.setText(postOwner.getFname() + " " + postOwner.getLname());
            holder.tv_person_name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    if (postOwner.getServerID() == SplashScreen.pref.getLong(GNLConstants.SharedPreference.ID_KEY, -1)
//                            || postOwner.getIsPublicProfile() == 0)
                    goToProfile(postOwner.getServerID());
                }
            });
            holder.tv_postDate.setText(GNLConstants.DateConversion.getDate(currentPost.getDate()));
            holder.tv_postContent.setText(currentPost.getText());
            holder.ll_comment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    commentPost(posts.get(position).getPostID());
                }
            });
            holder.ll_share.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sharePost(currentPost, holder.iv_postImage.getDrawable());
                }
            });
            holder.ll_comment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isCommentDialogShown)
                        return;
                    mAnimation.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {
                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            commentPost(posts.get(position).getServerID());
//                        holder.ll_comment.setEnabled(true);
//                        holder.card_post.setEnabled(true);

                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });
                    holder.iv_comment.startAnimation(mAnimation);
                }
            });
            holder.ll_share.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isCommentDialogShown)
                        return;
//                    ViewAnimation.blink(activity, holder.iv_share);
                    ViewAnimation.bounce(activity, holder.iv_share);
                    sharePost(currentPost, holder.iv_postImage.getDrawable());
//                        pe_listener.onSharePost(posts.get(getAdapterPosition()).getServerID());
                }
            });
            holder.ll_favorite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    favoritePost(position, posts.get(position).getPostID(), pref.getLong(GNLConstants.SharedPreference.ID_KEY, -1));
                }
            });
            holder.card_post.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                        viewPost(posts.get(position).getPostID());
                }
            });
            holder.tv_post_category.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    categoryClick(postCategory.getServerID(), postOwner.getServerID());
                }
            });
            holder.iv_profile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                        if (postOwner.getServerID() == SplashScreen.pref.getLong(GNLConstants.SharedPreference.ID_KEY, -1)
//                                || postOwner.getIsPublicProfile() == 0)
                        goToProfile(postOwner.getServerID());
                }
            });
            String postImage = currentPost.getImage();
            ImageLoader imageLoader = ImageLoader.getInstance();

            if(!TextUtils.isEmpty(postImage) && postImage != "null"){
                holder.iv_postImage.setVisibility(View.GONE);
                Picasso.with(activity).load(Uri.parse(currentPost.getImage())).into(holder.iv_postImage, new Callback() {
                    @Override
                    public void onSuccess() {
//                        holder.pb_photo_load.setVisibility(View.GONE);
                        Animation anim = AnimationUtils.loadAnimation(activity, R.anim.fade);
                        holder.iv_postImage.setAnimation(anim);
                        anim.start();
                        holder.iv_postImage.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onError() {
//                        holder.pb_photo_load.setVisibility(View.GONE);
                        holder.iv_postImage.setVisibility(View.VISIBLE);

                    }
                });
                Log.i("fhfhjgh", postImage);
            }else {
                holder.iv_postImage.setVisibility(View.GONE);
//                holder.pb_photo_load.setVisibility(View.GONE);

            }

            holder.iv_favorite.setImageResource(R.drawable.ic_fav_on);
            Picasso.with(activity).load(Uri.parse(postOwner.getImage())).skipMemoryCache().into(holder.iv_profile, new Callback() {
                @Override
                public void onSuccess() {
                }

                @Override
                public void onError() {
                    holder.iv_profile.setImageResource(R.drawable.ic_user);
                }
            });


        }
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

    @Override
    public int getItemCount() {
        return posts.size() + 1;
    }

    public class FavoriteViewHolder extends RecyclerView.ViewHolder {
        TextView tv_person_name;
        TextView tv_postDate;
        TextView tv_postContent;
        TextView tv_post_category;  //change visibility
        ImageView iv_postImage;
        CardView card_post;
        RelativeLayout rl_postEvents; //change visibility
        LinearLayout ll_share;
        LinearLayout ll_favorite;
        LinearLayout ll_comment;
        CircleImageView iv_profile;
        ImageView iv_favorite;
//        ProgressBar  pb_photo_load;
        int viewType;
        public ImageView iv_comment;
        public ImageView iv_share;
        public FavoriteViewHolder(View itemView, int viewType) {
            super(itemView);
            this.viewType = viewType;
            if (viewType == TYPE_FOOTER) {
                pv_load = (ProgressBar) itemView.findViewById(R.id.pv_load);
                pv_load.getIndeterminateDrawable().setColorFilter(Color.parseColor("#249885"), android.graphics.PorterDuff.Mode.MULTIPLY);
                btn_reload = (Button) itemView.findViewById(R.id.btn_reload);
            } else {
                tv_person_name = (TextView) itemView.findViewById(R.id.tv_person_name);
                tv_postDate = (TextView) itemView.findViewById(R.id.tv_postDate);
                tv_postContent = (TextView) itemView.findViewById(R.id.tv_postContent);
                iv_postImage = (ImageView) itemView.findViewById(R.id.iv_postImage);
                iv_comment = (ImageView) itemView.findViewById(R.id.iv_comment);
                iv_share = (ImageView) itemView.findViewById(R.id.iv_share);
                iv_profile = (CircleImageView) itemView.findViewById(R.id.iv_profile);

                tv_post_category = (TextView) itemView.findViewById(R.id.tv_post_category);

                rl_postEvents = (RelativeLayout) itemView.findViewById(R.id.rl_postEvents);
                ll_comment = (LinearLayout) itemView.findViewById(R.id.ll_comment);
                ll_share = (LinearLayout) itemView.findViewById(R.id.ll_share);
                ll_favorite = (LinearLayout) itemView.findViewById(R.id.ll_favorite);
                iv_favorite = (ImageView) itemView.findViewById(R.id.iv_favorite);
                card_post = (CardView) itemView.findViewById(R.id.card_post);
//                pb_photo_load = (ProgressBar) itemView.findViewById(R.id.pb_photo_load);
//                pb_photo_load.getIndeterminateDrawable().setColorFilter(Color.parseColor("#249885"), android.graphics.PorterDuff.Mode.MULTIPLY);

            }

        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == posts.size())
            return TYPE_FOOTER;
        return TYPE_HEADER;
    }

    public void removePost(int position){
        posts.remove(position);
        notifyDataSetChanged();
    }
    public void addFromServer(ArrayList<Post_Favorite> newPosts, boolean isErrorConnection) {
        if (newPosts != null && newPosts.size() > 0) {
            posts.addAll(newPosts);
            if(pv_load != null)
                 pv_load.setVisibility(View.VISIBLE);
            isFoundData = true;
            notifyDataSetChanged();
        } else {
            if(isErrorConnection){
                if(pv_load != null && btn_reload != null) {
                    btn_reload.setVisibility(View.VISIBLE);
                    pv_load.setVisibility(View.GONE);
                }
            }else {
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

    public void addFromLocal(ArrayList<Post_Favorite> newPosts){
        posts.addAll(newPosts);
        if(pv_load != null)
            pv_load.setVisibility(View.GONE);
        isFoundData = false;
        notifyDataSetChanged();
    }

    private void sharePost(Posts post, Drawable postPhoto) {

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("*/*");
        intent.putExtra(Intent.EXTRA_TEXT, post.getText());
        if(!TextUtils.isEmpty(post.getImage()) && post.getImage() != "null") {
            String path = MediaStore.Images.Media.insertImage(activity.getContentResolver(), GNLConstants.drawableToBitmap(postPhoto), "", null);
            intent.putExtra(Intent.EXTRA_STREAM, Uri.parse(path));
        }
        activity.startActivity(Intent.createChooser(intent, "Share using"));
    }

    private void favoritePost(final int position, final long post_id, long user_id) {
            //remove from favorite
        WebServiceFunctions.removePostFavorite(activity, post_id, user_id, new OnPostFavoriteListener() {

                @Override
                public void onSuccess() {
//                    AppSnackBar.show(parent, activity.getString(R.string.post_favorite_removed), activity.getResources().getColor(R.color.colorPrimary), Color.WHITE);
                }

                @Override
                public void onFail(String error) {
//                    AppSnackBar.show(parent, error, Color.RED, Color.WHITE);

                }
            });
        removePost(position);
    }
    private void commentPost(long postId) {
        isCommentDialogShown = true;
        Bundle args = new Bundle();
        args.putLong(ViewPost.POST_ID, postId);
        Comments comments = new Comments(new TimelineRecViewAdapter.OnDialogDismiss() {
            @Override
            public void onDismiss() {
                isCommentDialogShown = false;
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
    private void viewPost(long post_id) {
        Intent intent = new Intent(activity, ViewPost.class);
        intent.putExtra(ViewPost.POST_ID, post_id);
//        if(postPhoto != null){
//            Bitmap bitmap = GNLConstants.drawableToBitmap(postPhoto);
//            ByteArrayOutputStream baos = new ByteArrayOutputStream();
//            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
//            byte[] b = baos.toByteArray();
//            intent.putExtra("picture", b);
//        }
        activity.startActivity(intent);
    }


}

