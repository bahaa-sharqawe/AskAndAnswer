package com.orchidatech.askandanswer.Activity;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Animatable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.BitmapAjaxCallback;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.controller.ControllerListener;
import com.facebook.drawee.drawable.ProgressBarDrawable;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.facebook.imagepipeline.image.ImageInfo;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.orchidatech.askandanswer.Constant.AppSnackBar;
import com.orchidatech.askandanswer.Constant.GNLConstants;
import com.orchidatech.askandanswer.Database.DAO.CategoriesDAO;
import com.orchidatech.askandanswer.Database.DAO.PostsDAO;
import com.orchidatech.askandanswer.Database.Model.Posts;
import com.orchidatech.askandanswer.Fragment.DeletePost;
import com.orchidatech.askandanswer.Logic.LollipopBitmapMemoryCacheParamsSupplier;
import com.orchidatech.askandanswer.R;
import com.orchidatech.askandanswer.View.Interface.OnPostDeletedListener;
import com.orchidatech.askandanswer.WebService.WebServiceFunctions;

import dmax.dialog.SpotsDialog;
import uk.co.senab.photoview.PhotoViewAttacher;

public class ViewPost extends AppCompatActivity implements DeletePost.OnDeleteListener{
    public static final String POST_ID = "POST_ID";
    long postId;
    Posts post;
    SimpleDraweeView iv_post;
    TextView tv_post;
    TextView tv_android_cateogry;
    RelativeLayout ll_parent;
    private SharedPreferences pref;
    private AlertDialog dialog;
    private PhotoViewAttacher mAttacher;
    private AQuery aq;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initFresco();
//        Fresco.initialize(getApplicationContext());
        setContentView(R.layout.activity_view_post);
        setCustomActionBar();
        postId = getIntent().getLongExtra(POST_ID, -1);
        pref = getSharedPreferences(GNLConstants.SharedPreference.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        aq = new AQuery(this);

//        if(getIntent().getByteArrayExtra("picture") != null){
//            byte[] b = getIntent().getByteArrayExtra("picture");
//            Bitmap bmp = BitmapFactory.decodeByteArray(b, 0, b.length);
//            iv_post.setImageBitmap(bmp);
//        }
        post = PostsDAO.getPost(postId);
        if(post != null) {
            iv_post = (SimpleDraweeView) this.findViewById(R.id.iv_post);
            tv_post = (TextView) this.findViewById(R.id.tv_post);
            tv_android_cateogry = (TextView) this.findViewById(R.id.tv_android_cateogry);
            tv_android_cateogry.setText(CategoriesDAO.getCategory(post.getCategoryID()).getName());
            tv_post.setText(post.getText());
            if (!TextUtils.isEmpty(post.getImage()) && post.getImage() != "null"/* && !loadPhotoPos.contains(position)*/) {
                iv_post.setVisibility(View.VISIBLE);

                final AQuery aq = new AQuery(this);
                if (pref.getLong(post.getServerID() + "", -1) == post.getServerID()) {
                    if (!TextUtils.isEmpty(pref.getString("prevImage", null))) {
                        aq.invalidate(pref.getString("prevImage", null));
                    }
                    pref.edit().remove(post.getServerID() + "").commit();
                    pref.edit().remove("prevImage").commit();
                }
                GenericDraweeHierarchyBuilder builder =
                        new GenericDraweeHierarchyBuilder(getResources());
                GenericDraweeHierarchy hierarchy = builder
                        .setProgressBarImage(new ProgressBarDrawable())
                        .build();
                iv_post.setHierarchy(hierarchy);


                ControllerListener controllerListener = new BaseControllerListener<ImageInfo>() {
                    @Override
                    public void onFinalImageSet(
                            String id,
                            @Nullable ImageInfo imageInfo,
                            @Nullable Animatable anim) {
                        if (imageInfo == null) {
                            return;
                        }

                        iv_post.setAspectRatio((float) imageInfo.getWidth() / imageInfo.getHeight());
                    }

                    @Override
                    public void onIntermediateImageSet(String id, @Nullable ImageInfo imageInfo) {
                        iv_post.setAspectRatio((float) imageInfo.getWidth() / imageInfo.getHeight());

                    }

                    @Override
                    public void onFailure(String id, Throwable throwable) {
                    }
                };
                DraweeController controller = (DraweeController) Fresco.newDraweeControllerBuilder()
                        .setControllerListener(controllerListener)
                        .setUri(Uri.parse(post.getImage()))
                                // other setters
                        .build();
                iv_post.setController(controller);
                iv_post.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(ViewPost.this, PhotosGallery.class);
                        intent.putExtra(PhotosGallery.IMAGE_URL, post.getImage());
                        startActivity(intent);
                    }
                });
//                holder.iv_postImage.setImageURI(Uri.parse(currentPost.getImage()));

            } else {
                iv_post.setVisibility(View.GONE);
//                holder.pb_photo_load.setVisibility(View.GONE);
            }
        }
        ll_parent = (RelativeLayout) this.findViewById(R.id.ll_parent);
    }

    private void setCustomActionBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        toolbar.setTitle("");
        toolbar.setNavigationIcon(R.drawable.ic_back);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
//        getSupportActionBar().setDisplayShowTitleEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.view_post_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.delete_post) {
            deletePost();
            return true;
        } else if (id == R.id.edit_post) {
            edit_post();
            return true;
        } else if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return false;
    }

    private void deletePost() {
        DeletePost deletePost = new DeletePost();
        Bundle args = new Bundle();
        args.putLong(POST_ID, postId);
        deletePost.setArguments(args);
        deletePost.show(getFragmentManager(), getString(R.string.delete_post));
    }
    private void edit_post() {
        Intent intent = new Intent(this, AddEditPost.class);
        intent.putExtra(POST_ID, postId);
        startActivity(intent);
    }
    private void performDeleting() {
//        final LoadingDialog loadingDialog = new LoadingDialog();
//        Bundle args = new Bundle();
//        args.putString(LoadingDialog.DIALOG_TEXT_KEY, getString(R.string.delteing));
//        loadingDialog.setArguments(args);
//        loadingDialog.setCancelable(false);
//        loadingDialog.show(getFragmentManager(), "deleting");
        dialog = new SpotsDialog(ViewPost.this, getString(R.string.delteing), R.style.SpotsDialogCustom);
        dialog.setCancelable(false);
        dialog.show();
        WebServiceFunctions.deletePost(this, pref.getLong(GNLConstants.SharedPreference.ID_KEY, -1), postId, new OnPostDeletedListener() {

            @Override
            public void onDeleted() {
                dialog.dismiss();
                Toast.makeText(ViewPost.this, getResources().getString(R.string.deleted), Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getApplicationContext(), MainScreen.class));
                finish();
//                AppSnackBar.show(ll_parent, getResources().getString(R.string.deleted), getResources().getColor(R.color.colorPrimary), Color.WHITE);
//                new Handler().postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        startActivity(new Intent(ViewPost.this, MainScreen.class));
//                    }
//                }, 3000);
            }

            @Override
            public void onFail(String error) {
                dialog.dismiss();
                AppSnackBar.show(ll_parent, error, Color.RED, Color.WHITE);
            }
        });
    }

    @Override
    public void onDelete() {
        performDeleting();
    }
    private void initFresco() {
        ActivityManager activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        ImagePipelineConfig imagePipelineConfig = ImagePipelineConfig
                .newBuilder(getApplicationContext())
                .setBitmapMemoryCacheParamsSupplier(new LollipopBitmapMemoryCacheParamsSupplier(activityManager))
                .build();

        Fresco.initialize(getApplicationContext(), imagePipelineConfig);
    }

}
