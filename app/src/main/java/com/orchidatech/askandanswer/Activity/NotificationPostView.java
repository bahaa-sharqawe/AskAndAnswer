package com.orchidatech.askandanswer.Activity;

import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.androidquery.AQuery;
import com.github.rahatarmanahmed.cpv.CircularProgressView;
import com.orchidatech.askandanswer.Constant.*;
import com.orchidatech.askandanswer.Constant.Enum;
import com.orchidatech.askandanswer.Database.DAO.PostsDAO;
import com.orchidatech.askandanswer.Database.DAO.UsersDAO;
import com.orchidatech.askandanswer.Database.Model.Posts;
import com.orchidatech.askandanswer.Database.Model.Users;
import com.orchidatech.askandanswer.Fragment.Comments;
import com.orchidatech.askandanswer.R;
import com.orchidatech.askandanswer.View.Adapter.TimelineRecViewAdapter;
import com.orchidatech.askandanswer.View.Interface.OnPostDataListener;
import com.orchidatech.askandanswer.View.Interface.OnPostFavoriteListener;
import com.orchidatech.askandanswer.View.Utils.BitmapUtility;
import com.orchidatech.askandanswer.WebService.WebServiceFunctions;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class NotificationPostView extends AppCompatActivity {
    public static final String OBJECT_ID = "object_id";
    public static final String TYPE = "object_tpe";
    Toolbar toolbar;
        CircleImageView iv_profile;
        ImageView tv_person_photo;
        TextView tv_person_name;
        TextView tv_postDate;
        TextView tv_post;
        ImageView iv_post;
    CircularProgressView pb_loading;
         SharedPreferences pref;
         long object_id;
        int object_type;
        RelativeLayout rl_error;
        ImageView uncolored_logo;
        TextView tv_error;
    long user_id;
    private long owner_id;
    private Animation mAnimation;
    private TextView tv_comment;
    private TextView tv_favorite;
    private TextView tv_share;
    private ImageView iv_comment;
    private ImageView iv_share;
    private ImageView iv_favorite;
    private LinearLayout ll_comment;
    private LinearLayout ll_share;
    private LinearLayout ll_favorite;
    private long post_id;
    private Posts post_item;
    RelativeLayout rl_post_info;
    private ColorGenerator generator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pref = getSharedPreferences(GNLConstants.SharedPreference.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        if((user_id = pref.getLong(GNLConstants.SharedPreference.ID_KEY, -1)) == -1){
            final Intent intent = new Intent(this, Login.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }

        setContentView(R.layout.activity_notification_post_view);
        Intent intent = getIntent();
        object_id = intent.getLongExtra(OBJECT_ID, -1);
        object_type = intent.getIntExtra(TYPE, -1);
        setCustomActionBar();
        generator = ColorGenerator.MATERIAL;

        rl_post_info = (RelativeLayout) this.findViewById(R.id.rl_post_info);
        iv_profile = (CircleImageView) this.findViewById(R.id.iv_profile);
        tv_person_photo = (ImageView) this.findViewById(R.id.tv_person_photo);
        tv_person_name = (TextView) this.findViewById(R.id.tv_person_name);
        tv_postDate = (TextView) this.findViewById(R.id.tv_postDate);
        tv_post = (TextView) this.findViewById(R.id.tv_post);
        iv_post = (ImageView) this.findViewById(R.id.iv_post);
        pb_loading = (CircularProgressView) this.findViewById(R.id.pb_loading);
        rl_error = (RelativeLayout) this.findViewById(R.id.rl_error);
        rl_error.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getData();
            }
        });
        uncolored_logo = (ImageView) this.findViewById(R.id.uncolored_logo);
        tv_error = (TextView) this.findViewById(R.id.tv_error);

        ll_comment = (LinearLayout) findViewById(R.id.ll_comment);
        ll_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                commentPost(post_item.getServerID());
            }
        });
        ll_share = (LinearLayout) findViewById(R.id.ll_share);
        ll_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sharePost(post_item, iv_post.getDrawable());
            }
        });
        ll_favorite = (LinearLayout) findViewById(R.id.ll_favorite);
        ll_favorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                favoritePost(post_item.getServerID(), user_id, iv_favorite);
            }
        });
        tv_comment = (TextView) findViewById(R.id.tv_comment);
        tv_favorite = (TextView) findViewById(R.id.tv_favorite);
        tv_share = (TextView) findViewById(R.id.tv_share);
        iv_comment = (ImageView) findViewById(R.id.iv_comment);
        iv_share = (ImageView) findViewById(R.id.iv_share);
        iv_favorite = (ImageView) findViewById(R.id.iv_favorite);
        mAnimation = AnimationUtils.loadAnimation(this, R.anim.zoom_enter);
        rl_post_info.setVisibility(View.INVISIBLE);
        resizeLogo();
        getData();
    }

    private void getData() {
        rl_error.setVisibility(View.GONE);
        pb_loading.setVisibility(View.VISIBLE);
        if(object_type == Enum.NOTIFICATIONS.NEW_POST_ADDED.getNumericType()){
            getPostInfo();
        }else{
            getCommentInfo();
        }

    }

    private void getCommentInfo() {
        WebServiceFunctions.getCommentInfo(NotificationPostView.this, user_id, object_id, new OnPostDataListener() {
            @Override
            public void onSuccess(Posts post, Users owner) {
                owner_id = owner.getServerID();
                post_item = post;
                rl_post_info.setVisibility(View.VISIBLE);
                pb_loading.setVisibility(View.GONE);
                rl_error.setVisibility(View.GONE);
                fillFields(post, owner);
            }

            @Override
            public void onFail(String cause) {
                rl_post_info.setVisibility(View.INVISIBLE);
                pb_loading.setVisibility(View.GONE);
                rl_error.setVisibility(View.VISIBLE);
                tv_error.setText(cause);
            }
        });
    }

    private void getPostInfo() {
        WebServiceFunctions.getPostInfo(this, user_id, object_id, new OnPostDataListener() {

            @Override
            public void onSuccess(Posts post, final Users owner) {
                owner_id = owner.getServerID();
                post_item = post;
                rl_post_info.setVisibility(View.VISIBLE);
                pb_loading.setVisibility(View.GONE);
                rl_error.setVisibility(View.GONE);
                fillFields(post, owner);
            }

            @Override
            public void onFail(String error) {
                rl_post_info.setVisibility(View.INVISIBLE);
                pb_loading.setVisibility(View.GONE);
                rl_error.setVisibility(View.VISIBLE);
                tv_error.setText(error);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home)
            onBackPressed();
        return true;
    }

    private void fillFields(final Posts post, final Users owner) {
        pb_loading.setVisibility(View.GONE);
        tv_person_name.setText(owner.getFname() + " " + owner.getLname());
        tv_postDate.setText(GNLConstants.DateConversion.getDate(post.getDate()));
        tv_post.setText(post.getText());
        if (!TextUtils.isEmpty(post.getImage()) && post.getImage() != "null") {
            AQuery aq = new AQuery(NotificationPostView.this);
            Bitmap preset = aq.getCachedImage(post.getImage());

            aq.id(iv_post)/*.progress(convertView.findViewById(R.id.progressBar1))*/.image(post.getImage(), true, true, 0, 0, preset, AQuery.FADE_IN);
            iv_post.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                        PhotosGallery photosGallery = new PhotosGallery();
//                        Bundle args = new Bundle();
//                        args.putString(PhotosGallery.IMAGE_URL, post.getImage());
//                        photosGallery.setArguments(args);
//                        photosGallery.show(getFragmentManager(), "gallery");
                    Intent intent = new Intent(NotificationPostView.this, PhotosGallery.class);
                    intent.putExtra(PhotosGallery.IMAGE_URL, post.getImage());
                    startActivity(intent);
                }
            });
        }else{
            iv_post.setVisibility(View.GONE);
        }
        String letter = owner.getFname().charAt(0) + " " + owner.getLname().charAt(0);

        final TextDrawable drawable = TextDrawable.builder().beginConfig().fontSize((int) getResources().getDimension(R.dimen.user_letters_font_size)).endConfig()
                .buildRound(letter.toUpperCase(), generator.getRandomColor());

        if (owner != null && !owner.getImage().equals(URL.DEFAULT_IMAGE))
            Picasso.with(NotificationPostView.this).load(Uri.parse(owner.getImage())).into(iv_profile, new Callback() {
                @Override
                public void onSuccess() {
                    tv_person_photo.setVisibility(View.INVISIBLE);
                    iv_profile.setVisibility(View.VISIBLE);
                }

                @Override
                public void onError() {
                    iv_profile.setVisibility(View.INVISIBLE);
                    tv_person_photo.setVisibility(View.VISIBLE);
                    tv_person_photo.setImageDrawable(drawable);
                }
            });
        else{
            iv_profile.setVisibility(View.INVISIBLE);
            tv_person_photo.setVisibility(View.VISIBLE);
            tv_person_photo.setImageDrawable(drawable);
        }

    }

    private void setCustomActionBar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Timeline Post");
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        toolbar.setNavigationIcon(R.drawable.ic_back);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onBackPressed() {
        final Intent intent = new Intent(this, MainScreen.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
    private void resizeLogo() {
        Display display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        Point screenSize = new Point(); // used to store screen size
        display.getSize(screenSize); // store size in screenSize
        uncolored_logo.getLayoutParams().height = (int) (screenSize.y * 0.25);
        uncolored_logo.getLayoutParams().width = (int) (screenSize.y * 0.25);
    }
    private void sharePost(Posts post, Drawable postPhoto) {

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("*/*");
        intent.putExtra(Intent.EXTRA_TEXT, post.getText());
        if (postPhoto != null && !TextUtils.isEmpty(post.getImage()) && post.getImage() != "null") {
            String path = MediaStore.Images.Media.insertImage(getContentResolver(), BitmapUtility.drawableToBitmap(postPhoto), "", null);
            intent.putExtra(Intent.EXTRA_STREAM, Uri.parse(path));
        }
        startActivity(Intent.createChooser(intent, "Share using"));
    }
    private void favoritePost(final long post_id, long user_id, final ImageView iv_favorite) {
        final Posts posts = PostsDAO.getPost(post_id);
        if (posts.getIsFavorite() != 1) {
            //add to favorite
            mAnimation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    iv_favorite.setImageResource(R.drawable.ic_fav_on);
                    posts.isFavorite = 1;
                    posts.save();

//                    PostsDAO.updatePost(posts.get(position));

                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            iv_favorite.startAnimation(mAnimation);
            WebServiceFunctions.addPostFavorite(this, post_id, user_id, new OnPostFavoriteListener() {

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
                    posts.isFavorite=0;
                    posts.save();
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            iv_favorite.startAnimation(mAnimation);
            WebServiceFunctions.removePostFavorite(this, post_id, user_id, new OnPostFavoriteListener() {

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
//        Bundle args = new Bundle();
//        args.putLong(ViewPost.POST_ID, postId);
//        Comments comments = new Comments(new TimelineRecViewAdapter.OnDialogDismiss() {
//
//            @Override
//            public void onDismiss() {
//            }
//        });
//        comments.setArguments(args);
//        FragmentTransaction ft = getFragmentManager().beginTransaction();
////        ft.setCustomAnimations(R.animator.slide_up, R.animator.slide_down,R.animator.slide_up, R.animator.slide_down);
//        comments.show(ft, "Comments");
        Intent intent = new Intent(NotificationPostView.this, CommentsScreen.class);
        intent.putExtra(ViewPost.POST_ID, postId);
        startActivity(intent);

    }

}
