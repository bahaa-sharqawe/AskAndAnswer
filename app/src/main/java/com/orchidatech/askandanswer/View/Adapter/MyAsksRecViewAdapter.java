package com.orchidatech.askandanswer.View.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.orchidatech.askandanswer.Activity.CategoryPosts;
import com.orchidatech.askandanswer.Activity.SplashScreen;
import com.orchidatech.askandanswer.Activity.ViewPost;
import com.orchidatech.askandanswer.Constant.AppSnackBar;
import com.orchidatech.askandanswer.Constant.GNLConstants;
import com.orchidatech.askandanswer.Database.DAO.CategoriesDAO;
import com.orchidatech.askandanswer.Database.DAO.Post_FavoriteDAO;
import com.orchidatech.askandanswer.Database.DAO.PostsDAO;
import com.orchidatech.askandanswer.Database.DAO.UsersDAO;
import com.orchidatech.askandanswer.Database.Model.Category;
import com.orchidatech.askandanswer.Database.Model.Post_Favorite;
import com.orchidatech.askandanswer.Database.Model.Posts;
import com.orchidatech.askandanswer.Database.Model.Users;
import com.orchidatech.askandanswer.Fragment.Comments;
import com.orchidatech.askandanswer.R;
import com.orchidatech.askandanswer.View.Interface.OnLastListReachListener;
import com.orchidatech.askandanswer.View.Interface.OnPostEventListener;
import com.orchidatech.askandanswer.View.Interface.OnPostFavoriteListener;
import com.orchidatech.askandanswer.WebService.WebServiceFunctions;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Bahaa on 24/11/2015.
 */
public class MyAsksRecViewAdapter  extends RecyclerView.Adapter<MyAsksRecViewAdapter.AskViewHolder> {
    private static final int TYPE_HEADER = 0;  // Declaring Variable to Understand which View is being worked on
    private static final int TYPE_FOOTER = 1;
    private final OnLastListReachListener lastListReachListener;
    private long user_id;
    private SharedPreferences pref;
    private View parent;
    private ProgressBar pv_load;
    private Button btn_reload;
    private ArrayList<Posts> posts;
    private Activity activity;
    private boolean loading = false;
    private boolean isFoundData = true;

    public MyAsksRecViewAdapter(Activity activity, ArrayList<Posts> posts, View parent,
                                OnLastListReachListener lastListReachListener) {
        this.activity = activity;
        this.posts = posts;
        this.parent = parent;
        this.lastListReachListener = lastListReachListener;
        pref = activity.getSharedPreferences(GNLConstants.SharedPreference.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        user_id = pref.getLong(GNLConstants.SharedPreference.ID_KEY, -1);
    }

    @Override
    public AskViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;
        if (viewType == TYPE_FOOTER) {
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.footer_progress, parent, false);
            return new AskViewHolder(itemView, TYPE_FOOTER);
        } else {
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_item, parent, false);
            return new AskViewHolder(itemView, TYPE_HEADER);
        }
    }

    @Override
    public void onBindViewHolder(final AskViewHolder holder, final int position) {

        if (holder.viewType == TYPE_FOOTER) {
            btn_reload.setVisibility(View.GONE);
            if (!loading && isFoundData && posts.size() > 0) {
                pv_load.setVisibility(View.VISIBLE);
                loading = true;
                lastListReachListener.onReached();
            }
        }else {
            Posts currentPost = posts.get(position);
            final Users postOwner = UsersDAO.getUser(currentPost.getUserID());
            final Category postCategory = CategoriesDAO.getCategory(currentPost.getCategoryID());
            holder.tv_post_category.setText(postCategory.getName());
            holder.tv_person_name.setText(postOwner.getFname() + " " + postOwner.getLname());
            holder.tv_postDate.setText(GNLConstants.DateConversion.getDate(currentPost.getDate()));
            holder.tv_postContent.setText(currentPost.getText());

            holder.ll_comment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    commentPost(posts.get(position).getServerID());
                }
            });

            holder.ll_share.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sharePost(posts.get(position), holder.iv_postImage.getDrawable());
                }
            });
            holder.ll_favorite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    favoritePost(position, posts.get(position).getServerID(), pref.getLong(GNLConstants.SharedPreference.ID_KEY, -1), holder.iv_favorite);
                }
            });
            holder.tv_post_category.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                   categoryClick(postCategory.getServerID(), postOwner.getServerID());
                }
            });
            holder.card_post.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    viewPost(posts.get(position).getServerID(), holder.iv_postImage.getDrawable());
                }
            });
            String postImage = currentPost.getImage();
            if(!TextUtils.isEmpty(postImage) && postImage != "null") {
                Picasso.with(activity).load(Uri.parse(currentPost.getImage())).into(holder.iv_postImage);
                holder.iv_postImage.setVisibility(View.VISIBLE);
                Log.i("jggn", currentPost.getImage());
            }else
                holder.iv_postImage.setVisibility(View.GONE);

            Picasso.with(activity).load(Uri.parse(postOwner.getImage())).into(holder.iv_profile);
            if(Post_FavoriteDAO.getPost_FavoriteByPostId(currentPost.getServerID(),
                    user_id) != null)
                holder.iv_favorite.setImageResource(R.drawable.ic_fav_on);
            else
                holder.iv_favorite.setImageResource(R.drawable.ic_favorite);

        }
    }



    @Override
    public int getItemCount() {
        return posts.size() + 1;
    }

    public class AskViewHolder extends RecyclerView.ViewHolder {
        TextView tv_person_name;
        TextView tv_postDate;
        TextView tv_postContent;
        TextView tv_post_category;  //change visibility
        ImageView iv_postImage;
        RelativeLayout rl_postEvents; //change visibility
        LinearLayout ll_share;
        LinearLayout ll_favorite;
        LinearLayout ll_comment;
        CardView card_post;
        CircleImageView iv_profile;
        ImageView iv_favorite;
        int viewType;

        public AskViewHolder(View itemView, int viewType) {
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
                rl_postEvents = (RelativeLayout) itemView.findViewById(R.id.rl_postEvents);
                ll_comment = (LinearLayout) itemView.findViewById(R.id.ll_comment);
                ll_share = (LinearLayout) itemView.findViewById(R.id.ll_share);
                ll_favorite = (LinearLayout) itemView.findViewById(R.id.ll_favorite);
                iv_favorite = (ImageView) itemView.findViewById(R.id.iv_favorite);
                card_post = (CardView) itemView.findViewById(R.id.card_post);
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
    public void addFromServer(ArrayList<Posts> newPosts, boolean isErrorConnection) {
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
                if(pv_load != null && btn_reload != null)
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
        if(pv_load != null)
        pv_load.setVisibility(View.GONE);
        isFoundData = false;
        notifyDataSetChanged();
    }

    private void sharePost(Posts post, Drawable postPhoto) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("*/*");
        intent.putExtra(Intent.EXTRA_TEXT, post.getText());
        Log.i("GHJHGJGHH", post.getImage());
        if(!TextUtils.isEmpty(post.getImage()) && post.getImage() != "null") {
            String path = MediaStore.Images.Media.insertImage(activity.getContentResolver(), GNLConstants.drawableToBitmap(postPhoto), "", null);
            intent.putExtra(Intent.EXTRA_STREAM, Uri.parse(path));
        }
        activity.startActivity(Intent.createChooser(intent, "Share using"));


    }

    private void favoritePost(final int position, final long post_id, long user_id, final ImageView iv_favorite) {
        if(Post_FavoriteDAO.getPost_FavoriteByPostId(post_id, user_id) == null){
            //add to favorite
            WebServiceFunctions.addPostFavorite(activity, post_id, user_id, new OnPostFavoriteListener() {

                @Override
                public void onSuccess() {
                    AppSnackBar.show(parent, activity.getString(R.string.post_favorite_added), activity.getResources().getColor(R.color.colorPrimary), Color.WHITE);
//                    adapter.notifyDataSetChanged();
                    iv_favorite.setImageResource(R.drawable.ic_fav_on);
                }

                @Override
                public void onFail(String error) {
                    AppSnackBar.show(parent, error, Color.RED, Color.WHITE);
                }
            });
        }else{
            //remove from favorite
            WebServiceFunctions.removePostFavorite(activity, post_id, user_id, new OnPostFavoriteListener() {

                @Override
                public void onSuccess() {
                    AppSnackBar.show(parent, activity.getString(R.string.post_favorite_removed), activity.getResources().getColor(R.color.colorPrimary), Color.WHITE);
                    iv_favorite.setImageResource(R.drawable.ic_favorite);
                }

                @Override
                public void onFail(String error) {
                    AppSnackBar.show(parent, error, Color.RED, Color.WHITE);

                }
            });

        }
    }

    private void commentPost(long postId) {
        Bundle args = new Bundle();
        args.putLong(ViewPost.POST_ID, postId);
        Comments comments = new Comments();
        comments.setArguments(args);
        comments.show(activity.getFragmentManager(), "Comments");
    }

    private void categoryClick(long category_id, long user_id) {
        Intent intent = new Intent(activity, CategoryPosts.class);
        intent.putExtra(CategoryPosts.CATEGORY_KEY, category_id);
        intent.putExtra(CategoryPosts.USER_ID, user_id);
        activity.startActivity(intent);
    }

    private void viewPost(long post_id, Drawable postPhoto) {
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

