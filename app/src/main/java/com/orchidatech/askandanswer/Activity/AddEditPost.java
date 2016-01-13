package com.orchidatech.askandanswer.Activity;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.facebook.cache.common.SimpleCacheKey;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.orchidatech.askandanswer.Constant.AppSnackBar;
import com.orchidatech.askandanswer.Constant.GNLConstants;
import com.orchidatech.askandanswer.Database.DAO.PostsDAO;
import com.orchidatech.askandanswer.Database.DAO.User_CategoriesDAO;
import com.orchidatech.askandanswer.Database.Model.Posts;
import com.orchidatech.askandanswer.Database.Model.User_Categories;
import com.orchidatech.askandanswer.Logic.LollipopBitmapMemoryCacheParamsSupplier;
import com.orchidatech.askandanswer.R;
import com.orchidatech.askandanswer.View.Adapter.SpinAdapter;
import com.orchidatech.askandanswer.View.Interface.OnAddPostListener;
import com.orchidatech.askandanswer.View.Interface.OnEditPostListener;
import com.orchidatech.askandanswer.View.Utils.BitmapUtility;
import com.orchidatech.askandanswer.View.Utils.FontManager;
import com.orchidatech.askandanswer.View.Utils.Validator;
import com.orchidatech.askandanswer.WebService.WebServiceFunctions;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;

import dmax.dialog.SpotsDialog;

public class AddEditPost extends AppCompatActivity {
    private static final int RESULT_LOAD_IMAGE = 1;
    private static final int MAX_CHARACTERS = 8000;

    ArrayList<User_Categories> spinnerItems;
    LinearLayout ll_parent;
    Spinner spinner;
    SpinAdapter adapter;
    SimpleDraweeView iv_post;
    ImageView iv_delete;
    EditText ed_postDesc;
    RelativeLayout rl_post_photo;
    long user_id;
    TextView tv_addPost;
    private ImageView iv_camera;
    private User_Categories selectedCategory;
    private long editPostId;
    private Posts editPost;
    private String image_str;
    private String picturePath = null;
    private boolean isPostHasImagePrev;
    private SharedPreferences pref;
    private FontManager fontManager;
    private AlertDialog dialog;
    private Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initFresco();
//        Fresco.initialize(getApplicationContext());
        setContentView(R.layout.activity_add_edit_post);
        setCustomActionBar();
        editPostId = getIntent().getLongExtra(ViewPost.POST_ID, -1);
        initializeFields();
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedCategory = spinnerItems.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        iv_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickPhoto();
            }
        });
        iv_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rl_post_photo.setVisibility(View.GONE);
                image_str = null;
                picturePath = null;
                iv_camera.setEnabled(true);
            }
        });

        if (editPostId != -1) {
            editPost = PostsDAO.getPost(editPostId);
            rl_post_photo.setVisibility(View.VISIBLE);
            iv_camera.setEnabled(true);
            ed_postDesc.setText(editPost.getText());
            picturePath = editPost.getImage();
            if (!TextUtils.isEmpty(picturePath)) {
                isPostHasImagePrev = true;
                iv_post.setImageURI(Uri.parse(picturePath));
            } else {
                iv_post.setVisibility(View.INVISIBLE);
                isPostHasImagePrev = false;
            }
            for (int i = 0; i < spinnerItems.size(); i++) {
                if (editPost.getCategoryID() == spinnerItems.get(i).getCategoryID()) {
                    spinner.setSelection(i);
                    break;
                }
            }
            setTitle(getString(R.string.edit_post));
        } else {
            setTitle(getString(R.string.add_post_str));
            spinner.setSelection(0);
        }
    }

    private void initializeFields() {
        fontManager = FontManager.getInstance(getAssets());

        pref = getSharedPreferences(GNLConstants.SharedPreference.SHARED_PREF_NAME, MODE_PRIVATE);
        user_id = pref.getLong(GNLConstants.SharedPreference.ID_KEY, -1);
        ll_parent = (LinearLayout) this.findViewById(R.id.ll_parent);
        ll_parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSoftKeyboard();
            }
        });
        spinnerItems = new ArrayList<>(User_CategoriesDAO.getAllUserCategories(user_id));
        spinner = (Spinner) this.findViewById(R.id.spinner);
        adapter = new SpinAdapter(this, spinnerItems);
        tv_addPost = (TextView) findViewById(R.id.tv_addPost);
        tv_addPost.setTypeface(fontManager.getFont(FontManager.ROBOTO_LIGHT));

        iv_camera = (ImageView) this.findViewById(R.id.iv_camera);
        rl_post_photo = (RelativeLayout) this.findViewById(R.id.rl_post_photo);
        ed_postDesc = (EditText) this.findViewById(R.id.ed_postDesc);
        ed_postDesc.setTypeface(fontManager.getFont(FontManager.ROBOTO_LIGHT));

        iv_post = (SimpleDraweeView) this.findViewById(R.id.iv_post);
        iv_delete = (ImageView) this.findViewById(R.id.iv_delete);
    }

    @Override
    public void setTitle(CharSequence title) {
        getSupportActionBar().setTitle(title);
    }

    private void pickPhoto() {
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, RESULT_LOAD_IMAGE);
    }

    private void setCustomActionBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        toolbar.setNavigationIcon(R.drawable.ic_back);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_post_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.done) {
            hideSoftKeyboard();
            String postDesc = ed_postDesc.getText().toString();
            Uri selectedImage = null;
            if (verifyInputs(postDesc)) {
                if(picturePath != null){
                    if(!Validator.getInstance().isWebUrl(picturePath)) {
                        selectedImage = Uri.parse(MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, "", null));
                        String[] filePathColumn = {MediaStore.Images.Media.DATA};
                        Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                        cursor.moveToFirst();
                        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                        picturePath = cursor.getString(columnIndex);
//                        Log.i("rtrtgfg", picturePath);
//
//                        picturePath = MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, "", null);
                    }
                }
                ///SEND TO SERVER
                if (editPostId == -1) {//add case
                    addPost(user_id, selectedCategory.getCategoryID(), postDesc, System.currentTimeMillis(), 0, selectedImage);
                } else {//edit case
                    editPost(editPostId, user_id, selectedCategory.getCategoryID(), postDesc, editPost.date, picturePath/*to know if picture changed */, editPost.getIsHidden(), selectedImage);
                }
            }
            return true;
        } else if (id == android.R.id.home) {
            hideSoftKeyboard();
            onBackPressed();
            return true;
        }
        return false;
    }

    private void editPost(final long postId, long user_id, long category_id, String postDesc, long date, final String picturePath, int isHidden, final Uri selectedImage) {
        final int imageState;
        if (isPostHasImagePrev) {
            Log.i("vxvcv", editPost.getImage() + "xcx");

            if (TextUtils.isEmpty(picturePath))
                imageState = 0;//remove post photo from DB... do not send photo to server
            else {
                if (editPost.getImage() == picturePath)
                    imageState = 1;//post photo did not changed.. send photo url to server
                else
                    imageState = 2;//post photo changed.. send photo to server
            }
        } else {
            if (picturePath == null)
                imageState = 0;//post photo did not changed.. do not send photo to server
            else
                imageState = 2;//post photo changed.. send photo to server
        }
//        final LoadingDialog loadingDialog = new LoadingDialog();
//        Bundle args = new Bundle();
//        args.putString(LoadingDialog.DIALOG_TEXT_KEY, getString(R.string.saving));
//        loadingDialog.setArguments(args);
//        loadingDialog.setCancelable(false);
//        loadingDialog.show(getFragmentManager(), "Saving...");
//        Log.i("imagestate", imageState + "");
        dialog = new SpotsDialog(AddEditPost.this, getString(R.string.saving), R.style.SpotsDialogCustom);
        dialog.setCancelable(false);
        dialog.show();

        WebServiceFunctions.editPost(this, postId, user_id, category_id, postDesc, imageState, imageState == 1 ? editPost.getImage() : picturePath, date, isHidden, new OnEditPostListener() {
            @Override
            public void onSuccess(String message) {
                dialog.dismiss();
                if (imageState == 2 || imageState == 0) {
                    ///remove previous image from cache
                    if(picturePath != null){
                        if(!Validator.getInstance().isWebUrl(picturePath)){
//                            File  file  = new File(picturePath);
//                            file.delete();
                            getContentResolver().delete(selectedImage, null, null);
                        }
//                        picturePath = Me1diaStore.Images.Media.insertImage(getContentResolver(), bitmap, "", null);
                    }
                    new AQuery(AddEditPost.this).invalidate(editPost.getImage());
                    Fresco.getImagePipelineFactory().getMainDiskStorageCache().remove(new SimpleCacheKey(editPost.getImage().toString()));
                    Fresco.getImagePipelineFactory().getSmallImageDiskStorageCache().remove(new SimpleCacheKey(editPost.getImage().toString()));
                    Fresco.getImagePipeline().evictFromMemoryCache(Uri.parse(editPost.getImage()));
//                    Log.i("wilbedeleted", "true");
//                    pref.edit().putLong(postId+"",postId).commit();
//                    pref.edit().putString("prevImage",editPost.getImage()).commit();
                }
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                Intent intent = new Intent(AddEditPost.this, MainScreen.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }

            @Override
            public void onFail(String error) {
                dialog.dismiss();
                if(picturePath != null){
                    if(!Validator.getInstance().isWebUrl(picturePath)){
//                        File  file  = new File(picturePath);
//                        file.delete();
                        getContentResolver().delete(selectedImage, null, null);
//                        Toast.makeText(AddEditPost.this, "deleted", Toast.LENGTH_LONG).show();
                    }
//                        picturePath = Me1diaStore.Images.Media.insertImage(getContentResolver(), bitmap, "", null);
                }
                AppSnackBar.show(ll_parent, error, Color.RED, Color.WHITE);
            }
        });
    }


    private void addPost(final long user_id, long category_id, final String postDesc, final long date, int is_hidden, final Uri selectedImage) {
//        final LoadingDialog loadingDialog = new LoadingDialog();
//        Bundle args = new Bundle();
//        args.putString(LoadingDialog.DIALOG_TEXT_KEY, getString(R.string.saving));
//        loadingDialog.setArguments(args);
//        loadingDialog.setCancelable(false);

//        loadingDialog.show(getFragmentManager(), "Saving...");
        dialog = new SpotsDialog(AddEditPost.this, "Posting...", R.style.SpotsDialogCustom);
        dialog.setCancelable(false);
        dialog.show();
        WebServiceFunctions.addPost(this, user_id, category_id, postDesc, picturePath, date, is_hidden, new OnAddPostListener() {
            @Override
            public void onSuccess(String message) {
                dialog.dismiss();
                if (picturePath != null) {
                    if (!Validator.getInstance().isWebUrl(picturePath)) {
                        getContentResolver().delete(selectedImage, null, null);
                    }
//                        picturePath = Me1diaStore.Images.Media.insertImage(getContentResolver(), bitmap, "", null);
                }
                rl_post_photo.setVisibility(View.GONE);
                image_str = null;
                picturePath = null;
                iv_camera.setEnabled(true);
                ed_postDesc.setText("");
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                Intent intent = new Intent(AddEditPost.this, MainScreen.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
//                AppSnackBar.show(ll_parent, message, getResources().getColor(R.color.colorPrimary), Color.WHITE);
//                new Handler().postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        startActivity(new Intent(AddEditPost.this, MainScreen.class));
//                    }
//                }, 3000);
            }

            @Override
            public void onFail(String error) {
                dialog.dismiss();
                if (!Validator.getInstance().isWebUrl(picturePath)) {
//                    File  file  = new File(picturePath);
//                    file.delete();
                    getContentResolver().delete(selectedImage, null, null);
//                    Toast.makeText(AddEditPost.this, "deleted", Toast.LENGTH_LONG).show();
                }
                AppSnackBar.show(ll_parent, error, Color.RED, Color.WHITE);
            }
        });
    }

    private boolean verifyInputs(String postDesc) {
//        if (TextUtils.isEmpty(picturePath)) {
//            AppSnackBar.show(ll_parent, getString(R.string.BR_AEP_001), Color.RED, Color.WHITE);
//            return false;
//        }
        if (TextUtils.isEmpty(postDesc)) {
            AppSnackBar.show(ll_parent, getString(R.string.BR_AEP_002), Color.RED, Color.WHITE);
            return false;
        } else if (postDesc.length() > MAX_CHARACTERS) {
            AppSnackBar.show(ll_parent, getString(R.string.BR_AEP_003), Color.RED, Color.WHITE);
            return false;
        }
        return true;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && data != null) {

            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            picturePath = cursor.getString(columnIndex);
            cursor.close();
            try {
                downSampleImage(picturePath);
                if (bitmap == null) {
                    Toast.makeText(getApplicationContext(), getString(R.string.choose_valid_image), Toast.LENGTH_LONG).show();
                    picturePath = null;
                    return;
                }
                rl_post_photo.setVisibility(View.VISIBLE);
                iv_post.setVisibility(View.VISIBLE);
                iv_camera.setEnabled(true);
                iv_post.setImageBitmap(bitmap);

            }catch (Exception e){
                picturePath = null;
            }
//            final Bitmap bitmap = BitmapFactory.decodeFile(picturePath);


        /*
         * Convert the image to a string
         * */
//            ByteArrayOutputStream stream = new ByteArrayOutputStream();
//            bitmap.compress(Bitmap.CompressFormat.PNG, 50, stream); //compress to which format you want.
//            byte[] byte_arr = stream.toByteArray();
//            image_str = Base64.encodeToString(byte_arr, 0);
        }
    }

    Bitmap ShrinkBitmap(String file, int width, int height) {

        BitmapFactory.Options bmpFactoryOptions = new BitmapFactory.Options();
        bmpFactoryOptions.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeFile(file, bmpFactoryOptions);

        int heightRatio = (int) Math.ceil(bmpFactoryOptions.outHeight / (float) height);
        int widthRatio = (int) Math.ceil(bmpFactoryOptions.outWidth / (float) width);

        if (heightRatio > 1 || widthRatio > 1) {
            if (heightRatio > widthRatio) {
                bmpFactoryOptions.inSampleSize = heightRatio;
            } else {
                bmpFactoryOptions.inSampleSize = widthRatio;
            }
        }

        bmpFactoryOptions.inJustDecodeBounds = false;
        bitmap = BitmapFactory.decodeFile(file, bmpFactoryOptions);
        return bitmap;
    }
private  Bitmap downSampleImage(String filePath){
    BitmapFactory.Options bmpFactoryOptions = new BitmapFactory.Options();
//    bmpFactoryOptions.inJustDecodeBounds = true;
//    bmpFactoryOptions.inSampleSize = 8;
//    bmpFactoryOptions.inJustDecodeBounds = false;
    bitmap = BitmapUtility.resizeBitmap(BitmapFactory.decodeFile(filePath, bmpFactoryOptions), 1280, 800);
    return bitmap;


}
    private void hideSoftKeyboard() {
        View view = AddEditPost.this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }

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
