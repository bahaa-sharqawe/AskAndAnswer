package com.orchidatech.askandanswer.View.Adapter;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
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

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.BitmapAjaxCallback;
import com.nineoldandroids.animation.Animator;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.orchidatech.askandanswer.Activity.CategoryPosts;
import com.orchidatech.askandanswer.Activity.MainScreen;
import com.orchidatech.askandanswer.Activity.Register;
import com.orchidatech.askandanswer.Activity.ViewPost;
import com.orchidatech.askandanswer.Constant.Enum;
import com.orchidatech.askandanswer.Constant.GNLConstants;
import com.orchidatech.askandanswer.Constant.URL;
import com.orchidatech.askandanswer.Database.DAO.CategoriesDAO;
import com.orchidatech.askandanswer.Database.DAO.Post_FavoriteDAO;
import com.orchidatech.askandanswer.Database.DAO.PostsDAO;
import com.orchidatech.askandanswer.Database.DAO.UsersDAO;
import com.orchidatech.askandanswer.Database.Model.Category;
import com.orchidatech.askandanswer.Database.Model.Posts;
import com.orchidatech.askandanswer.Database.Model.Users;
import com.orchidatech.askandanswer.Fragment.Comments;
import com.orchidatech.askandanswer.Fragment.Profile;
import com.orchidatech.askandanswer.R;
import com.orchidatech.askandanswer.View.Animation.ViewAnimation;
import com.orchidatech.askandanswer.View.Interface.OnLastListReachListener;
import com.orchidatech.askandanswer.View.Interface.OnPostFavoriteListener;
import com.orchidatech.askandanswer.View.Utils.FontManager;
import com.orchidatech.askandanswer.WebService.WebServiceFunctions;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Bahaa on 17/11/2015.
 */
public class TimelineRecViewAdapter extends RecyclerView.Adapter<TimelineRecViewAdapter.PostViewHolder> {
    private static final int TYPE_HEADER = 0;  // Declaring Variable to Understand which View is being worked on
    private static final int TYPE_FOOTER = 1;
    private final View parent;
    private final int fragment_numeric;
    private final long current_user_id;
    private final SharedPreferences pref;
    private final Animation mAnimation;
    private final FontManager fontManager;
    private ImageLoader imageLoader;
    private ProgressBar pv_load;
    private Button btn_reload;
    private List<Posts> posts;
    private Activity activity;
    private boolean loading = false;
    private boolean isFoundData = true;
    OnLastListReachListener lastListReachListener;
    private boolean isCommentDialogShown = false;


    public TimelineRecViewAdapter(Activity activity, List<Posts> posts, View parent,
                                  OnLastListReachListener lastListReachListener, int fragment_numeric) {
        this.activity = activity;
        this.posts = posts;
        this.parent = parent;
        this.lastListReachListener = lastListReachListener;
        this.fragment_numeric = fragment_numeric;
        pref = activity.getSharedPreferences(GNLConstants.SharedPreference.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        this.current_user_id = pref.getLong(GNLConstants.SharedPreference.ID_KEY, -1);
        imageLoader = ImageLoader.getInstance();
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(activity)
                .memoryCache(new LruMemoryCache(GNLConstants.MAX_IMAGE_LOADER_CACH_SIZE)).build();
        ImageLoader.getInstance().init(config);
        mAnimation = AnimationUtils.loadAnimation(activity, R.anim.zoom_enter);
        fontManager = FontManager.getInstance(activity.getAssets());
        AjaxCallback.setNetworkLimit(8);

//set the max number of icons (image width <= 50) to be cached in memory, default is 20
        BitmapAjaxCallback.setIconCacheLimit(50);

//set the max number of images (image width > 50) to be cached in memory, default is 20
        BitmapAjaxCallback.setCacheLimit(50);
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
    public void onBindViewHolder(final PostViewHolder holder, final int position) {

        if (holder.viewType == TYPE_FOOTER) {
            btn_reload.setVisibility(View.GONE);
            if (!loading && isFoundData && posts.size() > 0) {
                pv_load.setVisibility(View.VISIBLE);
                loading = true;
                lastListReachListener.onReached();
            } else
                pv_load.setVisibility(View.GONE);

        } else {
            final Posts currentPost = posts.get(position);
            final Users postOwner = UsersDAO.getUser(currentPost.getUserID());
            final Category postCategory = CategoriesDAO.getCategory(currentPost.getCategoryID());
            holder.tv_post_category.setText(postCategory.getName());

            holder.tv_person_name.setText(postOwner.getFname() + " " + postOwner.getLname());

            holder.tv_person_name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (fragment_numeric != Enum.POSTS_FRAGMENTS.PROFILE.getNumericType())
                        goToProfile(postOwner.getServerID());
                }
            });

            if (posts.get(position).getIsFavorite() == 1)
                holder.iv_favorite.setImageResource(R.drawable.ic_fav_on);
            else
                holder.iv_favorite.setImageResource(R.drawable.ic_favorite);

            holder.tv_postDate.setText(GNLConstants.DateConversion.getDate(currentPost.getDate()));
            holder.tv_postContent.setText(currentPost.getText());
            String postImage = currentPost.getImage();

//            if (pref.getLong(currentPost.getServerID() + "", -1) == currentPost.getServerID()) {
//                if (!TextUtils.isEmpty(pref.getString("prevImage", null))) {
//                    File imageFile = imageLoader.getDiscCache().get(pref.getString("prevImage", null));
//                    if (imageFile.exists()) {
//                        imageFile.delete();
//                        Log.i("xcxc", "xxz2");
//                    }
//                }
//                Log.i("xcxc", "xxz: " + postImage);
//                pref.edit().remove(currentPost.getServerID() + "").commit();
//                pref.edit().remove("prevImage").commit();
//            }

            holder.iv_postImage.setVisibility(View.INVISIBLE);
            AQuery aq = new AQuery(activity);

            if (!TextUtils.isEmpty(postImage) && postImage != "null"/* && !loadPhotoPos.contains(position)*/) {
                if (pref.getLong(currentPost.getServerID() + "", -1) == currentPost.getServerID()) {
                    if (!TextUtils.isEmpty(pref.getString("prevImage", null))) {
                        aq.invalidate(pref.getString("prevImage", null));
                    }
                    pref.edit().remove(currentPost.getServerID() + "").commit();
                pref.edit().remove("prevImage").commit();
                }

/* Getting Images from Server and stored in cache */
                aq.id(holder.iv_postImage)/*.progress(convertView.findViewById(R.id.progressBar1))*/.image(currentPost.getImage(), true, true, 0, R.drawable.ic_user, null, AQuery.FADE_IN);
//
//                imageLoader.displayImage(currentPost.getImage(), holder.iv_postImage, new ImageLoadingListener() {
//                    @Override
//                    public void onLoadingStarted(String imageUri, View view) {
//                        view.setVisibility(View.VISIBLE);
//                    }
//
//                    @Override
//                    public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
//                        view.setVisibility(View.VISIBLE);
//
//                    }
//
//                    @Override
//                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
//                        Animation anim = AnimationUtils.loadAnimation(activity, R.anim.fade);
//                        view.setAnimation(anim);
//                        anim.start();
//                        view.setVisibility(View.VISIBLE);
//
//
//                    }
//
//                    @Override
//                    public void onLoadingCancelled(String imageUri, View view) {
////                       holder.iv_postImage.setVisibility(View.INVISIBLE);
//
//                    }
//                });
            } else {
                holder.iv_postImage.setVisibility(View.GONE);
//                holder.pb_photo_load.setVisibility(View.GONE);
            }

            if (postOwner != null && !postOwner.getImage().equals(URL.DEFAULT_IMAGE)
                    && !postOwner.getFname().equals("بهاء"))
                Picasso.with(activity).load(Uri.parse(postOwner.getImage())).into(holder.iv_profile, new Callback() {
                    @Override
                    public void onSuccess() {
                        holder.tv_person_photo.setVisibility(View.INVISIBLE);
                        holder.iv_profile.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onError() {
                        holder.iv_profile.setVisibility(View.INVISIBLE);
                        holder.tv_person_photo.setVisibility(View.VISIBLE);
                        holder.tv_person_photo.setText(postOwner.getFname().charAt(0)+" "+postOwner.getLname().charAt(0));
                    }
                });
            else{
                holder.iv_profile.setVisibility(View.INVISIBLE);
                holder.tv_person_photo.setVisibility(View.VISIBLE);
                holder.tv_person_photo.setText(postOwner.getFname().charAt(0)+" "+postOwner.getLname().charAt(0));
            }

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
                    sharePost(posts.get(position), holder.iv_postImage.getDrawable());
//                        pe_listener.onSharePost(posts.get(getAdapterPosition()).getServerID());
                }
            });
            holder.ll_favorite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isCommentDialogShown)
                        return;
                    favoritePost(position, posts.get(position).getServerID(), current_user_id, holder.iv_favorite);
                }
            });
            holder.card_post.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (fragment_numeric != Enum.POSTS_FRAGMENTS.MY_ASKS.getNumericType() && !isCommentDialogShown)
                        commentPost(posts.get(position).getServerID());
                    else if (fragment_numeric == Enum.POSTS_FRAGMENTS.MY_ASKS.getNumericType() && !isCommentDialogShown)
                        viewPost(posts.get(position).getServerID());
                }
            });
            holder.tv_post_category.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (fragment_numeric != Enum.POSTS_FRAGMENTS.CATEGORY_POST.getNumericType() && !isCommentDialogShown)
                        categoryClick(postCategory.getServerID(), postOwner.getServerID());
                }
            });

            holder.iv_profile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (fragment_numeric != Enum.POSTS_FRAGMENTS.PROFILE.getNumericType() &&
                            fragment_numeric != Enum.POSTS_FRAGMENTS.CATEGORY_POST.getNumericType())
                        goToProfile(postOwner.getServerID());
                }
            });
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
        TextView tv_post_category;
        TextView tv_favorite;
        TextView tv_share;
        TextView tv_comment;
        TextView tv_person_photo;
        ImageView iv_postImage;
        LinearLayout rl_postEvents;
        LinearLayout ll_share;
        LinearLayout ll_favorite;
        LinearLayout ll_comment;
        RelativeLayout card_post;
        CircleImageView iv_profile;
        ImageView iv_favorite;
        //        ProgressBar  pb_photo_load;
        int viewType;
        public ImageView iv_comment;
        public ImageView iv_share;

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
                tv_comment = (TextView) itemView.findViewById(R.id.tv_comment);
                tv_favorite = (TextView) itemView.findViewById(R.id.tv_favorite);
                tv_share = (TextView) itemView.findViewById(R.id.tv_share);
                tv_person_photo = (TextView) itemView.findViewById(R.id.tv_person_photo);
                iv_postImage = (ImageView) itemView.findViewById(R.id.iv_postImage);
                iv_comment = (ImageView) itemView.findViewById(R.id.iv_comment);
                iv_share = (ImageView) itemView.findViewById(R.id.iv_share);
                iv_profile = (CircleImageView) itemView.findViewById(R.id.iv_profile);
                iv_favorite = (ImageView) itemView.findViewById(R.id.iv_favorite);
                tv_post_category = (TextView) itemView.findViewById(R.id.tv_post_category);
                rl_postEvents = (LinearLayout) itemView.findViewById(R.id.rl_postEvents);
                ll_comment = (LinearLayout) itemView.findViewById(R.id.ll_comment);
                ll_share = (LinearLayout) itemView.findViewById(R.id.ll_share);
                ll_favorite = (LinearLayout) itemView.findViewById(R.id.ll_favorite);
                card_post = (RelativeLayout) itemView.findViewById(R.id.card_post);
                tv_postDate.setTypeface(fontManager.getFont(FontManager.ROBOTO_LIGHT));
                tv_person_name.setTypeface(fontManager.getFont(FontManager.ROBOTO_LIGHT));
                tv_postContent.setTypeface(fontManager.getFont(FontManager.ROBOTO_LIGHT));
                tv_share.setTypeface(fontManager.getFont(FontManager.ROBOTO_LIGHT));
                tv_favorite.setTypeface(fontManager.getFont(FontManager.ROBOTO_LIGHT));
                tv_comment.setTypeface(fontManager.getFont(FontManager.ROBOTO_LIGHT));
                tv_person_name.setTypeface(fontManager.getFont(FontManager.ROBOTO_MEDIUM));
                tv_person_photo.setTypeface(fontManager.getFont(FontManager.ROBOTO_MEDIUM));


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

    public void addFromServer(ArrayList<Posts> newPosts, boolean isErrorConnection) {
        if (newPosts != null && newPosts.size() > 0) {
            posts.addAll(newPosts);
            if (pv_load != null)
                pv_load.setVisibility(View.VISIBLE);
            isFoundData = true;
            notifyDataSetChanged();

        } else {
            if (isErrorConnection) {
                if (pv_load != null && btn_reload != null) {
                    btn_reload.setVisibility(View.VISIBLE);
                    btn_reload.setEnabled(true);
                    pv_load.setVisibility(View.GONE);
                }
            } else {
                if (pv_load != null && btn_reload != null) {
                    pv_load.setVisibility(View.GONE);
                    btn_reload.setVisibility(View.GONE);
                }
                isFoundData = false;
//                AppSnackBar.show(parent, activity.getString(R.string.BR_GNL_005), Color.RED, Color.WHITE);
//                optimizeLocalDB();
            }
        }
        loading = false;
    }

    private void optimizeLocalDB() {
        String s = "(";
        for (int i = 0; i < posts.size(); i++) {
            s += posts.get(i).getServerID();
            s += i != posts.size() - 1 ? "," : "";
        }
        s += ")";
        PostsDAO.deleteUnNeeded(s);
    }

    public void addFromLocal(List<Posts> newPosts) {
        posts.addAll(newPosts);
        if (pv_load != null)
            pv_load.setVisibility(View.GONE);
        isFoundData = false;
        notifyDataSetChanged();
    }

    private void sharePost(Posts post, Drawable postPhoto) {

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("*/*");
        intent.putExtra(Intent.EXTRA_TEXT, post.getText());
        if (postPhoto != null && !TextUtils.isEmpty(post.getImage()) && post.getImage() != "null") {
            String path = MediaStore.Images.Media.insertImage(activity.getContentResolver(), GNLConstants.drawableToBitmap(postPhoto), "", null);
            intent.putExtra(Intent.EXTRA_STREAM, Uri.parse(path));
        }
        activity.startActivity(Intent.createChooser(intent, "Share using"));
    }

    private void favoritePost(final int position, final long post_id, long user_id, final ImageView iv_favorite) {

        if (posts.get(position).getIsFavorite() != 1) {
            //add to favorite
            mAnimation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    iv_favorite.setImageResource(R.drawable.ic_fav_on);
                    posts.get(position).isFavorite = 1;
                    posts.get(position).save();

                    Log.i("FAVDFD", PostsDAO.getPost(posts.get(position).getServerID()).getIsFavorite() + "");
//                    PostsDAO.updatePost(posts.get(position));

                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            iv_favorite.startAnimation(mAnimation);
            WebServiceFunctions.addPostFavorite(activity, post_id, user_id, new OnPostFavoriteListener() {

                @Override
                public void onSuccess() {
//                    AppSnackBar.show(parent, activity.getString(R.string.post_favorite_added), activity.getResources().getColor(R.color.colorPrimary), Color.WHITE);
                }

                @Override
                public void onFail(String error) {
//                    AppSnackBar.show(parent, error, Color.RED, Color.WHITE);
                }
            });
        } else {
            //remove from favorite
            mAnimation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    iv_favorite.setImageResource(R.drawable.ic_favorite);
                    posts.get(position).isFavorite=0;
                    posts.get(position).save();
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            iv_favorite.startAnimation(mAnimation);
            WebServiceFunctions.removePostFavorite(activity, post_id, user_id, new OnPostFavoriteListener() {

                @Override
                public void onSuccess() {
//                    AppSnackBar.show(parent, activity.getString(R.string.post_favorite_removed), activity.getResources().getColor(R.color.colorPrimary), Color.WHITE);
//                    iv_favorite.setImageResource(R.drawable.ic_favorite);
                }

                @Override
                public void onFail(String error) {
//                    AppSnackBar.show(parent, error, Color.RED, Color.WHITE);

                }
            });

        }
    }

    private void commentPost(long postId) {
        isCommentDialogShown = true;
        Bundle args = new Bundle();
        args.putLong(ViewPost.POST_ID, postId);
        Comments comments = new Comments(new OnDialogDismiss() {

            @Override
            public void onDismiss() {
                isCommentDialogShown = false;
            }
        });
        comments.setArguments(args);
        FragmentTransaction ft = activity.getFragmentManager().beginTransaction();
//        ft.setCustomAnimations(R.animator.slide_up, R.animator.slide_down,R.animator.slide_up, R.animator.slide_down);
        comments.show(ft, "Comments");

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

    public interface OnDialogDismiss {
        void onDismiss();
    }
}
