package com.orchidatech.askandanswer.View.Adapter;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Animatable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.androidquery.AQuery;
import com.facebook.cache.common.SimpleCacheKey;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.controller.ControllerListener;
import com.facebook.drawee.drawable.ProgressBarDrawable;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.image.ImageInfo;
import com.github.rahatarmanahmed.cpv.CircularProgressView;
import com.orchidatech.askandanswer.Activity.CategoryPosts;
import com.orchidatech.askandanswer.Activity.CommentsScreen;
import com.orchidatech.askandanswer.Activity.MainScreen;
import com.orchidatech.askandanswer.Activity.PhotosGallery;
import com.orchidatech.askandanswer.Activity.ViewPost;
import com.orchidatech.askandanswer.Constant.AppSnackBar;
import com.orchidatech.askandanswer.Constant.GNLConstants;
import com.orchidatech.askandanswer.Constant.URL;
import com.orchidatech.askandanswer.Database.DAO.CategoriesDAO;
import com.orchidatech.askandanswer.Database.DAO.UsersDAO;
import com.orchidatech.askandanswer.Database.Model.Category;
import com.orchidatech.askandanswer.Database.Model.Posts;
import com.orchidatech.askandanswer.Database.Model.Users;
import com.orchidatech.askandanswer.Fragment.Comments;
import com.orchidatech.askandanswer.Fragment.Profile;
import com.orchidatech.askandanswer.R;
import com.orchidatech.askandanswer.View.Interface.OnLastListReachListener;
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
    private final OnLastListReachListener lastListReachListener;
    private final SharedPreferences pref;
    private  ColorGenerator generator;
    private CircularProgressView pv_load;

    private ArrayList<Posts> posts;
    Activity activity;
    private boolean loading = false;
    private boolean isFoundData = true;
    private Button btn_reload;
    int last_fetched_posts_count;

    public SearchRecViewAdapter(Activity activity, ArrayList<Posts> posts, View parent, OnLastListReachListener lastListReachListener) {
        this.activity = activity;
        this.posts = posts;
        this.parent = parent;
        fontManager = FontManager.getInstance(activity.getAssets());
        this.lastListReachListener = lastListReachListener;
        generator = ColorGenerator.MATERIAL;
        last_fetched_posts_count = 0;
        pref = activity.getSharedPreferences(GNLConstants.SharedPreference.SHARED_PREF_NAME, Context.MODE_PRIVATE);


    }

    @Override
    public PostViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;
        if (viewType == TYPE_FOOTER) {
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.footer_progress, parent, false);
            return new PostViewHolder(itemView, TYPE_FOOTER);
        } else {
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_result_item, parent, false);
            return new PostViewHolder(itemView, TYPE_HEADER);
        }
    }

    @Override
    public void onBindViewHolder(final PostViewHolder holder, final int position) {
        if (holder.viewType == TYPE_FOOTER) {
            btn_reload.setVisibility(View.GONE);
//            pv_load.setVisibility(View.GONE);
            if (!loading && isFoundData && last_fetched_posts_count >= GNLConstants.POST_LIMIT) {
                pv_load.setVisibility(View.VISIBLE);
                loading = true;
                lastListReachListener.onReached();
            }
        }else {
            final Posts currentPost = posts.get(position);
            final Users postOwner = UsersDAO.getUser(currentPost.getUserID());
            final Category postCategory = CategoriesDAO.getCategory(currentPost.getCategoryID());
            holder.tv_post_category.setText(postCategory.getName());

            holder.tv_person_name.setText(postOwner.getFname() + " " + postOwner.getLname());

            holder.tv_postDate.setText(GNLConstants.DateConversion.getDate(currentPost.getDate()));

            holder.tv_postContent.setText(currentPost.getText());

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

            if (!TextUtils.isEmpty(postImage) && postImage != "null"/* && !loadPhotoPos.contains(position)*/) {
                holder.iv_postImage.setVisibility(View.VISIBLE);

                final AQuery aq = new AQuery(activity);
//                if (pref.getLong(currentPost.getServerID() + "", -1) == currentPost.getServerID()) {
//                    if (!TextUtils.isEmpty(pref.getString("prevImage", null))) {
//                        aq.invalidate(pref.getString("prevImage", null));
//                        Uri uri = Uri.parse(pref.getString("prevImage", null));
//                        Fresco.getImagePipelineFactory().getMainDiskStorageCache().remove(new SimpleCacheKey(uri.toString()));
//                        Fresco.getImagePipelineFactory().getSmallImageDiskStorageCache().remove(new SimpleCacheKey(uri.toString()));
//                        Fresco.getImagePipeline().evictFromMemoryCache(uri);
//                    }
//                    pref.edit().remove(currentPost.getServerID() + "").commit();
//                    pref.edit().remove("prevImage").commit();
//                }
/* Getting Images from Server and stored in cache */

//                Bitmap preset = aq.getCachedImage(currentPost.getImage());
//                    aq.id(holder.iv_postImage).image(currentPost.getImage(), false, true, 0, 0, new BitmapAjaxCallback() {
//                        @Override
//                        public void callback(String url, final ImageView iv, Bitmap bm, AjaxStatus status) {
//
//                            int screenWidth = DeviceDimensionsHelper.getDisplayWidth(activity);
//
//                            Log.i("dimensd", bm.getHeight() + " x " + bm.getWidth());
//                            holder.tv_postContent.measure(0, 0);       //must call measure!
//                            bm = BitmapUtility.resizeBitmap(bm, holder.tv_postContent.getWidth(), 400);
//                            Log.i("dimensd", bm.getHeight() + " x " + bm.getWidth() + ", " + holder.tv_postContent.getWidth());
//                            final Bitmap finalBm = bm;
//        iv.setImageBitmap(finalBm);
//                            iv.startAnimation(AnimationUtils.loadAnimation(activity, R.anim.images_fade));
//Log.i("preset", "reloaded");
//
//                            iv.setVisibility(View.VISIBLE);
//                        }
//
//                    });
//                }
                GenericDraweeHierarchyBuilder builder =
                        new GenericDraweeHierarchyBuilder(activity.getResources());
                GenericDraweeHierarchy hierarchy = builder
                        .setProgressBarImage(new ProgressBarDrawable())
                        .build();
                holder.iv_postImage.setHierarchy(hierarchy);


                ControllerListener controllerListener = new BaseControllerListener<ImageInfo>() {
                    @Override
                    public void onFinalImageSet(
                            String id,
                            @Nullable ImageInfo imageInfo,
                            @Nullable Animatable anim) {
                        if (imageInfo == null) {
                            return;
                        }

                        holder.iv_postImage.setAspectRatio((float) imageInfo.getWidth() / imageInfo.getHeight());
                    }

                    @Override
                    public void onIntermediateImageSet(String id, @Nullable ImageInfo imageInfo) {
                        holder.iv_postImage.setAspectRatio((float) imageInfo.getWidth() / imageInfo.getHeight());

                    }

                    @Override
                    public void onFailure(String id, Throwable throwable) {
                    }
                };
                DraweeController controller = (DraweeController) Fresco.newDraweeControllerBuilder()
                        .setControllerListener(controllerListener)
                        .setUri(Uri.parse(currentPost.getImage()))
                                // other setters
                        .build();
                holder.iv_postImage.setController(controller);

//                holder.iv_postImage.setImageURI(Uri.parse(currentPost.getImage()));
                holder.iv_postImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        viewPhoto(currentPost.getImage());
                    }
                });
            } else {
                holder.iv_postImage.setVisibility(View.GONE);
//                holder.pb_photo_load.setVisibility(View.GONE);
            }

            holder.tv_comments.setText(posts.get(position).getComments_no() > 0 ? activity.getString(R.string.tv_comments_count, posts.get(position).getComments_no()) : activity.getString(R.string.no_comments));

            holder.tv_unlikes.setText(posts.get(position).getNum_dislikes() + "");

            holder.tv_likes.setText(posts.get(position).getNum_likes() + "");

            holder.iv_profile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    goToProfile(postOwner.getServerID());
                }
            });
            String letter = postOwner.getFname().charAt(0) + " " + postOwner.getLname().charAt(0);

            final TextDrawable drawable = TextDrawable.builder().beginConfig().fontSize((int) activity.getResources().getDimension(R.dimen.user_letters_font_size)).endConfig()
                    .buildRound(letter.toUpperCase(), holder.text_draw_color);

            if (postOwner != null && !postOwner.getImage().equals(URL.DEFAULT_IMAGE))
                Picasso.with(activity).load(Uri.parse(postOwner.getImage())).into(holder.iv_profile, new Callback() {
                @Override
                public void onSuccess() {
                    holder.tv_person_photo.setVisibility(View.INVISIBLE);
                    holder.iv_profile.setVisibility(View.VISIBLE);
                }

                @Override
                public void onError() {
                    holder.iv_profile.setVisibility(View.INVISIBLE);
                    holder.tv_person_photo.setImageDrawable(drawable);
                    holder.tv_person_photo.setVisibility(View.VISIBLE);
//                    holder.tv_person_photo.setText(postOwner.getFname().charAt(0) + " " + postOwner.getLname().charAt(0));
                }
            });
            else{
                holder.iv_profile.setVisibility(View.INVISIBLE);
                holder.tv_person_photo.setImageDrawable(drawable);
                holder.tv_person_photo.setVisibility(View.VISIBLE);
//                holder.tv_person_photo.setText(postOwner.getFname().charAt(0)+" "+postOwner.getLname().charAt(0));
            }
        }
    }

    @Override
    public int getItemCount() {
        return posts.size()+1;
    }

    public class PostViewHolder extends RecyclerView.ViewHolder {
        ImageView tv_person_photo;
        TextView tv_person_name;
        TextView tv_postDate;
        TextView tv_postContent;
        TextView tv_post_category;  //change visibility
        SimpleDraweeView iv_postImage;
        RelativeLayout rl_postEvents; //change visibility
        CircleImageView iv_profile;
        TextView tv_likes;
        TextView tv_unlikes;
        TextView tv_comments;
//        ProgressBar pb_photo_load;
        RelativeLayout card_post;
        int viewType;
        public int text_draw_color;


        public PostViewHolder(View itemView, int viewType) {
            super(itemView);
            this.viewType = viewType;
            if (viewType == TYPE_FOOTER) {
                pv_load = (CircularProgressView) itemView.findViewById(R.id.pv_load);
//                pv_load.getIndeterminateDrawable().setColorFilter(Color.parseColor("#249885"), android.graphics.PorterDuff.Mode.MULTIPLY);
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
                iv_postImage = (SimpleDraweeView) itemView.findViewById(R.id.iv_postImage);
                iv_profile = (CircleImageView) itemView.findViewById(R.id.iv_profile);
                tv_post_category = (TextView) itemView.findViewById(R.id.tv_post_category);
                tv_likes = (TextView) itemView.findViewById(R.id.tv_likes);
                tv_unlikes = (TextView) itemView.findViewById(R.id.tv_unlikes);
                tv_comments = (TextView) itemView.findViewById(R.id.tv_comments);
                rl_postEvents = (RelativeLayout) itemView.findViewById(R.id.rl_postEvents);
                tv_person_photo = (ImageView) itemView.findViewById(R.id.tv_person_photo);

//                pb_photo_load = (ProgressBar) itemView.findViewById(R.id.pb_photo_load);
                card_post = (RelativeLayout) itemView.findViewById(R.id.card_post);
                tv_person_name.setTypeface(fontManager.getFont(FontManager.ROBOTO_MEDIUM));
                tv_post_category.setTypeface(fontManager.getFont(FontManager.ROBOTO_LIGHT));
                tv_postDate.setTypeface(fontManager.getFont(FontManager.ROBOTO_LIGHT));
                tv_postContent.setTypeface(fontManager.getFont(FontManager.ROBOTO_LIGHT));
                tv_unlikes.setTypeface(fontManager.getFont(FontManager.ROBOTO_LIGHT));
                tv_comments.setTypeface(fontManager.getFont(FontManager.ROBOTO_LIGHT));
                tv_likes.setTypeface(fontManager.getFont(FontManager.ROBOTO_LIGHT));
                text_draw_color =  generator.getRandomColor();


//                ll_comment = (LinearLayout) itemView.findViewById(R.id.ll_comment);
//                ll_share = (LinearLayout) itemView.findViewById(R.id.ll_share);
//                ll_favorite = (LinearLayout) itemView.findViewById(R.id.ll_favorite);
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == posts.size())
            return TYPE_FOOTER;
        return TYPE_HEADER;
    }


    private void commentPost(long postId) {
//        Bundle args = new Bundle();
//        args.putLong(ViewPost.POST_ID, postId);
//        Comments comments = new Comments(new TimelineRecViewAdapter.OnDialogDismiss() {
//            @Override
//            public void onDismiss() {
//
//            }
//        });
//        comments.setArguments(args);
//        comments.show(activity.getFragmentManager(), "Comments");
        Intent intent = new Intent(activity, CommentsScreen.class);
        intent.putExtra(ViewPost.POST_ID, postId);
        activity.startActivity(intent);
    }
    private void categoryClick(long category_id, long user_id) {
        Intent intent = new Intent(activity, CategoryPosts.class);
        intent.putExtra(CategoryPosts.CATEGORY_KEY, category_id);
        intent.putExtra(CategoryPosts.USER_ID, user_id);
        activity.startActivity(intent);
    }
    private void viewPhoto(String image) {
        Intent intent = new Intent(activity, PhotosGallery.class);
        intent.putExtra(PhotosGallery.IMAGE_URL, image);
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
        ft.addToBackStack("4");
        ft.commit();
        mFragmentManager.executePendingTransactions();
    }
    public void addFromServer(ArrayList<Posts> newPosts, boolean isErrorConnection) {
        if (newPosts != null && newPosts.size() > 0) {
            posts.addAll(newPosts);
            if (pv_load != null && newPosts.size() >= GNLConstants.POST_LIMIT)
                pv_load.setVisibility(View.VISIBLE);
            else if(pv_load != null)
                pv_load.setVisibility(View.GONE);
            isFoundData = true;
            last_fetched_posts_count = newPosts.size();
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

}
