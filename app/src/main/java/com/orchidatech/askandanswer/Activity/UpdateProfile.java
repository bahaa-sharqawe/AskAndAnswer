package com.orchidatech.askandanswer.Activity;

import android.content.Intent;
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
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.orchidatech.askandanswer.Constant.AppSnackBar;
import com.orchidatech.askandanswer.Constant.GNLConstants;
import com.orchidatech.askandanswer.Database.DAO.CategoriesDAO;
import com.orchidatech.askandanswer.Database.DAO.User_CategoriesDAO;
import com.orchidatech.askandanswer.Database.DAO.UsersDAO;
import com.orchidatech.askandanswer.Database.Model.Category;
import com.orchidatech.askandanswer.Database.Model.Users;
import com.orchidatech.askandanswer.Logic.HorizontalFlowLayout;
import com.orchidatech.askandanswer.R;
import com.orchidatech.askandanswer.View.Adapter.AutoCompleteAdapter;
import com.orchidatech.askandanswer.View.Interface.OnUpdateProfileListener;
import com.orchidatech.askandanswer.View.Utils.Validator;
import com.orchidatech.askandanswer.WebService.WebServiceFunctions;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class UpdateProfile extends AppCompatActivity {
    private static final int RESULT_LOAD_IMAGE = 1;

    private static final int WIDTH = RelativeLayout.LayoutParams.WRAP_CONTENT;
    private static final int HEIGHT = RelativeLayout.LayoutParams.WRAP_CONTENT;
    private static final int MARGIN = 5;

    LinearLayout ll_parent;
    AutoCompleteTextView auto_categories;
    EditText ed_fname;
    EditText ed_lname;
    EditText ed_email;
    EditText ed_password;
    EditText ed_new_password;
    EditText ed_confirm_new_password;

    ImageView iv_update_password;
    ImageView iv_camera;
    CircleImageView profile_image;
    LinearLayout ll_newPassword;
    HorizontalFlowLayout ll_categories;

    ArrayList<Category> categories;
    ArrayList<Category> selectedCategories;
    ArrayList<Category> unSelectedCategories;

    AutoCompleteAdapter ac_adapter;
    private ArrayList<String> unSelectedCategoriesTitles;
    private Validator mValidator;
    private String image_str;
    private String picturePath;
    long user_id;
    private Users user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);
        setCustomActionBar();
        initializeFields();
        iv_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickPhoto();
            }
        });
        auto_categories.setAdapter(ac_adapter);
        auto_categories.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Category selectedCategory = null;
                for (int i = 0; i < unSelectedCategories.size(); i++) {
                    if (unSelectedCategories.get(i).getName().equals(((TextView) view).getText().toString())) {
                        selectedCategory = unSelectedCategories.get(i);
                        break;
                    }
                }
                selectedCategory.setIsChecked(true);
                unSelectedCategories.remove(selectedCategory);
                selectedCategories.add(selectedCategory);
                ac_adapter.remove(unSelectedCategoriesTitles.remove(position));
//                ac_adapter.notifyDataSetChanged();
                auto_categories.setText("");
                final View item = LayoutInflater.from(UpdateProfile.this).inflate(R.layout.categories_grid_view_item, null, false);
                TextView tv_category = (TextView) item.findViewById(R.id.tv_category);
                tv_category.setText(selectedCategory.getName());
                LinearLayout ll_delete = (LinearLayout) item.findViewById(R.id.ll_delete);
                ll_delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        LinearLayout parent = (LinearLayout) v.getParent();
                        TextView targetCategory = (TextView) parent.findViewById(R.id.tv_category);
                        String targetCategoryTitle = targetCategory.getText().toString();
                        ll_categories.removeView(item);
                        Category deletedCategory = null;
                        for (int i = 0; i < selectedCategories.size(); i++) {
                            if (selectedCategories.get(i).getName().equals(targetCategoryTitle)) {
                                deletedCategory = selectedCategories.get(i);
                                break;
                            }
                        }
                        deletedCategory.setIsChecked(true);
                        unSelectedCategories.add(deletedCategory);
                        selectedCategories.remove(deletedCategory);
                        unSelectedCategoriesTitles.add(targetCategoryTitle);
                        ac_adapter.add(targetCategoryTitle);
//                        ac_adapter.notifyDataSetChanged();
                    }
                });

                HorizontalFlowLayout.LayoutParams params = new HorizontalFlowLayout.LayoutParams(WIDTH, HEIGHT);
                params.setMargins(MARGIN, MARGIN, MARGIN, MARGIN);
                ll_categories.addView(item, params);
            }
        });

        iv_update_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ed_password.setEnabled(true);
                ed_password.setHint(getString(R.string.current_password));
                v.setVisibility(View.GONE);
                ll_newPassword.setVisibility(View.VISIBLE);
            }
        });

    }

    private void initializeFields() {
        user_id = SplashScreen.pref.getLong(GNLConstants.SharedPreference.ID_KEY, -1);
        user = UsersDAO.getUser(user_id);
        picturePath = user.getImage();
        mValidator = Validator.getInstance();
        ll_parent = (LinearLayout) this.findViewById(R.id.ll_parent);
        ed_fname = (EditText) this.findViewById(R.id.ed_fname);
        ed_fname.setText(user.getFname());
        ed_lname = (EditText) this.findViewById(R.id.ed_lname);
        ed_lname.setText(user.getLname());
        ed_email = (EditText) this.findViewById(R.id.ed_email);
        ed_email.setText(user.getEmail());
        ed_password = (EditText) this.findViewById(R.id.ed_password);
        ed_new_password = (EditText) this.findViewById(R.id.ed_new_password);
        ed_confirm_new_password = (EditText) this.findViewById(R.id.ed_confirm_new_password);
        ll_newPassword = (LinearLayout) findViewById(R.id.ll_newPassword);
        iv_camera = (ImageView) this.findViewById(R.id.iv_camera);
        profile_image = (CircleImageView) this.findViewById(R.id.profile_image);
        Picasso.with(this).load(Uri.parse(user.getImage())).into(profile_image);
        auto_categories = (AutoCompleteTextView) this.findViewById(R.id.auto_categories);
        ll_categories = (HorizontalFlowLayout) findViewById(R.id.hf_categories);
        iv_update_password = (ImageView) findViewById(R.id.iv_update_password);
        categories = new ArrayList<>(CategoriesDAO.getAllCategories());
        selectedCategories = new ArrayList<>();
        unSelectedCategories = new ArrayList<>();
        unSelectedCategoriesTitles = new ArrayList<>();
        for (Category category : categories) {
            if (User_CategoriesDAO.getUserCategory(user_id, category.getServerID()) != null)//1 is current user id
                selectedCategories.add(category);
            else {
                unSelectedCategories.add(category);
                unSelectedCategoriesTitles.add(category.getName());
            }
        }
        ac_adapter = new AutoCompleteAdapter(this, unSelectedCategoriesTitles);
    }

    private void pickPhoto() {
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, RESULT_LOAD_IMAGE);
    }

    private void setCustomActionBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.edit_profile));
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        toolbar.setNavigationIcon(R.drawable.ic_back);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_profile_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.done) {
            String fname = ed_fname.getText().toString().trim();
            String lname = ed_lname.getText().toString().trim();
            String email = ed_email.getText().toString().trim();
            String password = ed_password.getText().toString();
            String newPassword = ed_new_password.getText().toString();
            String confirm_new_password = ed_confirm_new_password.getText().toString();
            long uid = SplashScreen.pref.getLong(GNLConstants.SharedPreference.ID_KEY, -1);
            if (verifyInputs(fname, lname, email, password, newPassword, confirm_new_password, selectedCategories)) {
                //save
                WebServiceFunctions.updateProfile(this, uid, fname, lname, password, image_str==null?null:picturePath, selectedCategories , new OnUpdateProfileListener() {
                    @Override
                    public void onSuccess() {
                        AppSnackBar.show(ll_parent, getString(R.string.saved), getResources().getColor(R.color.colorPrimary), Color.WHITE);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                finish();
                            }
                        }, 3000);
                    }

                    @Override
                    public void onFail(String cause) {
                        AppSnackBar.show(ll_parent, cause, Color.RED, Color.WHITE);
                    }
                });
            }
            return true;
        } else if (id == android.R.id.home) {
            finish();
            return true;
        }
        return false;
    }

    private boolean verifyInputs(String fname, String lname, String email, String password, String newPassword, String confirm_new_password, ArrayList<Category> selectedCategories) {
        if (TextUtils.isEmpty(fname)) {
            AppSnackBar.show(ll_parent, getString(R.string.BR_SIGN_001), Color.RED, Color.WHITE);
            return false;
        } else if (!mValidator.isValidUserName(fname)) {
            AppSnackBar.show(ll_parent, getString(R.string.BR_GNL_004), Color.RED, Color.WHITE);
            return false;
        }  if (TextUtils.isEmpty(lname)) {
            AppSnackBar.show(ll_parent, getString(R.string.BR_SIGN_007), Color.RED, Color.WHITE);
            return false;
        } else if (!mValidator.isValidUserName(lname)) {
            AppSnackBar.show(ll_parent, getString(R.string.BR_GNL_004), Color.RED, Color.WHITE);
            return false;
        } else if (!TextUtils.isEmpty(password) || !TextUtils.isEmpty(newPassword) || !TextUtils.isEmpty(confirm_new_password)) {
            if (TextUtils.isEmpty(password)) {
                AppSnackBar.show(ll_parent, getString(R.string.BR_EP_002), Color.RED, Color.WHITE);
                return false;
            } else if (!SplashScreen.pref.getString(GNLConstants.SharedPreference.PASSWORD_KEY, null).equals(password)) {
                AppSnackBar.show(ll_parent, getString(R.string.BR_EP_003), Color.RED, Color.WHITE);
                return false;
            } else if (TextUtils.isEmpty(password)) {
                AppSnackBar.show(ll_parent, getString(R.string.BR_EP_004), Color.RED, Color.WHITE);
                return false;
            } else if (!mValidator.isValidPassword(password)) {
                AppSnackBar.show(ll_parent, getString(R.string.BR_GNL_003), Color.RED, Color.WHITE);
                return false;
            } else if (mValidator.isPasswordsMatched(newPassword, confirm_new_password)) {
                AppSnackBar.show(ll_parent, getString(R.string.BR_SIGN_006), Color.RED, Color.WHITE);
                return false;
            }else if(!validCategoriesCount(selectedCategories)){
                return false;
            }
        }
        return true;
    }

    private boolean validCategoriesCount(ArrayList<Category> selectedCategories) {
        if(selectedCategories.size() < SelectCategory.MIN_CATEGORY){
            AppSnackBar.show(ll_parent, getString(R.string.BR_CATS_001), Color.RED, Color.WHITE);
            return false;
        }else if(selectedCategories.size() > SelectCategory.MAX_CATEGORY){
            AppSnackBar.show(ll_parent, getString(R.string.BR_CATS_002), Color.RED, Color.WHITE);
            return false;
        }
        return true;
    }

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
            profile_image.setImageBitmap(bitmap);

        /*
         * Convert the image to a string
         * */
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 50, stream); //compress to which format you want.
            byte[] byte_arr = stream.toByteArray();
            image_str = Base64.encodeToString(byte_arr, Base64.DEFAULT);

//            byte[] recovered_byte_arr = Base64.decode(image_str, Base64.DEFAULT);
//           Bitmap bm = BitmapFactory.decodeByteArray(recovered_byte_arr, 0, recovered_byte_arr.length);

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

}