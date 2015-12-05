package com.orchidatech.askandanswer.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
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
import android.widget.Toast;

import com.orchidatech.askandanswer.Constant.AppSnackBar;
import com.orchidatech.askandanswer.Constant.GNLConstants;
import com.orchidatech.askandanswer.Database.DAO.PostsDAO;
import com.orchidatech.askandanswer.Database.DAO.User_CategoriesDAO;
import com.orchidatech.askandanswer.Database.Model.Posts;
import com.orchidatech.askandanswer.Database.Model.User_Categories;
import com.orchidatech.askandanswer.Fragment.LoadingDialog;
import com.orchidatech.askandanswer.R;
import com.orchidatech.askandanswer.View.Adapter.SpinAdapter;
import com.orchidatech.askandanswer.View.Interface.OnAddPostListener;
import com.orchidatech.askandanswer.View.Interface.OnEditPostListener;
import com.orchidatech.askandanswer.WebService.WebServiceFunctions;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

public class AddEditPost extends AppCompatActivity {
    private static final int RESULT_LOAD_IMAGE = 1;
    private static final int MAX_CHARACTERS = 8000;

    ArrayList<User_Categories> spinnerItems;
    LinearLayout ll_parent;
    Spinner spinner;
    SpinAdapter adapter;
    ImageView iv_post;
    ImageView iv_delete;
    EditText ed_postDesc;
    RelativeLayout rl_post_photo;
    private ImageView iv_camera;
    private User_Categories selectedCategory;
    private long editPostId;
    private Posts editPost;
    private String image_str;
    long user_id;
    private String picturePath;
    private boolean isPostHasImagePrev;
    private SharedPreferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
            if(!TextUtils.isEmpty(picturePath)) {
                isPostHasImagePrev = true;
                Picasso.with(this).load(Uri.parse(editPost.getImage())).into(iv_post, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError() {
                        iv_post.setVisibility(View.INVISIBLE);
                    }
                });
            }
            else {
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
        iv_camera = (ImageView) this.findViewById(R.id.iv_camera);
        rl_post_photo = (RelativeLayout) this.findViewById(R.id.rl_post_photo);
        ed_postDesc = (EditText) this.findViewById(R.id.ed_postDesc);
        iv_post = (ImageView) this.findViewById(R.id.iv_post);
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

        setSupportActionBar(toolbar);
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
            String postDesc = ed_postDesc.getText().toString();
            if (verifyInputs(postDesc)) {
                ///SEND TO SERVER
                if (editPostId == -1) {//add case
                    addPost(user_id, selectedCategory.getCategoryID(), postDesc, picturePath, System.currentTimeMillis(), 0);
                } else {//edit case
                    editPost(editPostId, user_id, selectedCategory.getCategoryID(), postDesc, editPost.date, image_str == null ? null : picturePath/*to know if picture changed */, editPost.getIsHidden());
                }
            }
            return true;
        }
        return false;
    }

    private void editPost(final long postId, long user_id, long category_id, String postDesc, long date, String picturePath, int isHidden) {
        final int imageState;
        if(isPostHasImagePrev){
            if(picturePath == null)
                imageState = 0;//remove post photo from DB... do not send photo to server
            else {
                if (editPost.getImage() == picturePath)
                    imageState = 1;//post photo did not changed.. send photo url to server
                else
                    imageState = 2;//post photo changed.. send photo to server
            }
        } else {
            if(picturePath == null)
                imageState = 0;//post photo did not changed.. do not send photo to server
            else
                imageState = 2;//post photo changed.. send photo to server
        }
        final LoadingDialog loadingDialog = new LoadingDialog();
        Bundle args = new Bundle();
        args.putString(LoadingDialog.DIALOG_TEXT_KEY, getString(R.string.saving));
        loadingDialog.setArguments(args);
        loadingDialog.setCancelable(false);
        loadingDialog.show(getFragmentManager(), "Saving...");
        Log.i("imagestate", imageState + "");


        WebServiceFunctions.editPost(this, postId, user_id, category_id, postDesc, imageState, imageState==1?editPost.getImage():picturePath, date, isHidden, new OnEditPostListener() {
            @Override
            public void onSuccess(String message) {
                loadingDialog.dismiss();
                if(imageState == 2){
                    pref.edit().putLong("new_image",postId).commit();
                }
                AppSnackBar.show(ll_parent, message, getResources().getColor(R.color.colorPrimary), Color.WHITE);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startActivity(new Intent(AddEditPost.this, MainScreen.class));
                    }
                }, 3000);
            }

            @Override
            public void onFail(String error) {
                loadingDialog.dismiss();
                AppSnackBar.show(ll_parent, error, Color.RED, Color.WHITE);
            }
        });
    }


    private void addPost(final long user_id, long category_id, final String postDesc, String picturePath, final long date, int is_hidden) {
        final LoadingDialog loadingDialog = new LoadingDialog();
        Bundle args = new Bundle();
        args.putString(LoadingDialog.DIALOG_TEXT_KEY, getString(R.string.saving));
        loadingDialog.setArguments(args);
        loadingDialog.setCancelable(false);

        loadingDialog.show(getFragmentManager(), "Saving...");
        WebServiceFunctions.addPost(this, user_id, category_id, postDesc, picturePath, date, is_hidden, new OnAddPostListener() {
            @Override
            public void onSuccess(String message) {
                loadingDialog.dismiss();
                AppSnackBar.show(ll_parent, message, getResources().getColor(R.color.colorPrimary), Color.WHITE);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startActivity(new Intent(AddEditPost.this, MainScreen.class));
                    }
                }, 3000);
            }

            @Override
            public void onFail(String error) {
                loadingDialog.dismiss();
                AppSnackBar.show(ll_parent, error, getResources().getColor(R.color.colorPrimary), Color.WHITE);
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
            final Bitmap bitmap = ShrinkBitmap(picturePath, 300, 300);

            if (bitmap == null) {
                Toast.makeText(getApplicationContext(), getString(R.string.choose_valid_image), Toast.LENGTH_LONG).show();
                return;
            }
            rl_post_photo.setVisibility(View.VISIBLE);
            iv_post.setVisibility(View.VISIBLE);
            iv_camera.setEnabled(true);
            iv_post.setImageBitmap(bitmap);

        /*
         * Convert the image to a string
         * */
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 50, stream); //compress to which format you want.
            byte[] byte_arr = stream.toByteArray();
            image_str = Base64.encodeToString(byte_arr, 0);
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

    private void hideSoftKeyboard() {
        View view = AddEditPost.this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }

    }
}
