package com.orchidatech.askandanswer.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Point;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.orchidatech.askandanswer.Constant.AppSnackBar;
import com.orchidatech.askandanswer.Constant.GNLConstants;
import com.orchidatech.askandanswer.Database.DAO.CategoriesDAO;
import com.orchidatech.askandanswer.Database.DAO.User_CategoriesDAO;
import com.orchidatech.askandanswer.Database.Model.Category;
import com.orchidatech.askandanswer.Database.Model.User_Categories;
import com.orchidatech.askandanswer.Fragment.LoadingDialog;
import com.orchidatech.askandanswer.R;
import com.orchidatech.askandanswer.View.Adapter.CategoriesAdapter;
import com.orchidatech.askandanswer.View.Interface.OnCategoriesFetchedListener;
import com.orchidatech.askandanswer.View.Interface.OnDisabledCategorieslistener;
import com.orchidatech.askandanswer.View.Interface.OnSendCategoriesListener;
import com.orchidatech.askandanswer.WebService.WebServiceFunctions;

import java.security.UnresolvedPermission;
import java.util.ArrayList;
import java.util.Arrays;

import de.keyboardsurfer.android.widget.crouton.Crouton;

public class UpdateCategory extends AppCompatActivity {
    private String TAG = SelectCategory.class.getSimpleName();
    public static  final int MIN_CATEGORY = 1;
    public static final int MAX_CATEGORY = 20;

    RelativeLayout rl_parent;
    ListView lv_categories;
    //    CircularProgressView pv_load;
    ProgressBar pv_load;
    CategoriesAdapter adapter;
    ArrayList<Category> categories;
    ArrayList<Category> original_categories;
    ArrayList<String> titles;
    EditText ed_search;
    long uid;
    RelativeLayout rl_error;
    ImageView uncolored_logo;
    TextView tv_error;
    private SharedPreferences pref;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_category);
        setCustomActionBar();
        initializeFields();
        loadCategories();
    }

    private void filterList(String s) {
        categories.clear();
        for (Category category : original_categories) {
            if (category.getName().toLowerCase().indexOf(s.toString().toLowerCase()) != -1) {
                categories.add(category);
            }
        }
        pv_load.setVisibility(View.GONE);
        lv_categories.setVisibility(View.VISIBLE);
        adapter.notifyDataSetChanged();
    }

    private void loadCategories() {
        rl_error.setVisibility(View.GONE);
        pv_load.setVisibility(View.VISIBLE);
        lv_categories.setVisibility(View.GONE);
        WebServiceFunctions.getCategories(this, new OnCategoriesFetchedListener() {
            @Override
            public void onSuccess(ArrayList<Category> newCategories) {

                categories.addAll(newCategories);
                original_categories.addAll(newCategories);
                loadDisabledCategories();
            }

            @Override
            public void onFail(String cause) {
//                fetchFromLocal(cause);
                AppSnackBar.showTopSnackbar(UpdateCategory.this, cause, Color.RED, Color.WHITE);
                rl_error.setVisibility(View.VISIBLE);
                pv_load.setVisibility(View.GONE);
                lv_categories.setVisibility(View.GONE);

            }
        });
    }


        private void loadDisabledCategories() {
                     WebServiceFunctions.loadDisabledCategories(UpdateCategory.this, uid, new OnDisabledCategorieslistener() {

                         @Override
                         public void onSuccess(ArrayList<Category> disabledCategories) {
                             pv_load.setVisibility(View.GONE);
                             lv_categories.setVisibility(View.VISIBLE);
                             rl_error.setVisibility(View.GONE);
                             setSelectedCategories(disabledCategories);
                         }

                         @Override
                         public void onFail(String cause) {
                             AppSnackBar.showTopSnackbar(UpdateCategory.this, cause, Color.RED, Color.WHITE);
                             rl_error.setVisibility(View.VISIBLE);
                             pv_load.setVisibility(View.GONE);
                             lv_categories.setVisibility(View.GONE);
                         }
                     });


    }

    private void fetchFromLocal(String error) {
        categories.addAll(CategoriesDAO.getAllCategories());
        original_categories.addAll(categories);
        pv_load.setVisibility(View.GONE);
        lv_categories.setVisibility(View.VISIBLE);
        if(categories.size() > 0) {

            setSelectedCategories(categories);
//            original_categories.get(0).setIsChecked(true);
//            categories.get(0).setIsChecked(true);
            adapter.notifyDataSetChanged();
        } else {
            rl_error.setVisibility(View.VISIBLE);
            tv_error.setText(error);
            AppSnackBar.show(rl_parent, error, Color.RED, Color.WHITE);
        }
    }

    private void setSelectedCategories(ArrayList<Category> disabledCategories) {
        ArrayList<User_Categories> allUserCategories = new ArrayList<>(User_CategoriesDAO.getAllUserCategories(uid));
        for(Category category : categories) {
            category.setIsChecked(false);
            category.setEnabled(true);
        }

        for(Category category : categories){
            for(User_Categories user_category : allUserCategories){
                if(user_category.getCategoryID() == category.getServerID()) {
                    category.setIsChecked(true);
                    break;
                }
            }
        }
        for(Category category : categories){
            for(Category disabledCategory : disabledCategories){
                if(disabledCategory.getServerID() == category.getServerID()) {
                    category.setEnabled(false);
                    break;
                }
            }
        }
        adapter.notifyDataSetChanged();
    }

    private void initializeFields() {
        rl_parent = (RelativeLayout) this.findViewById(R.id.rl_parent);
        rl_parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSoftKeyboard();
            }
        });
        pv_load = (ProgressBar) this.findViewById(R.id.pv_load);
        pv_load.getIndeterminateDrawable().setColorFilter(Color.parseColor("#249885"), android.graphics.PorterDuff.Mode.MULTIPLY);
        rl_error = (RelativeLayout) this.findViewById(R.id.rl_error);
        rl_error.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadCategories();
            }
        });
        tv_error = (TextView) this.findViewById(R.id.tv_error);
        uncolored_logo = (ImageView) this.findViewById(R.id.uncolored_logo);
        ed_search = (EditText) this.findViewById(R.id.ed_search);
        ed_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                pv_load.resetAnimation();
                pv_load.setVisibility(View.VISIBLE);
                lv_categories.setVisibility(View.GONE);
                filterList(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        lv_categories = (ListView) this.findViewById(R.id.lv_categories);
        lv_categories.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(categories.get(position).isEnabled()) {
                    categories.get(position).setIsChecked(!categories.get(position).isChecked());
                    adapter.notifyDataSetChanged();
                }else{
                    Crouton.cancelAllCroutons();
                    AppSnackBar.showTopSnackbar(UpdateCategory.this, getString(R.string.unallow_delete_category, categories.get(position).getName()), Color.RED, Color.WHITE);
                }
            }
        });
        titles = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.categories)));
        categories = new ArrayList<>();
        original_categories = new ArrayList<>();
        adapter = new CategoriesAdapter(this, categories);
        lv_categories.setAdapter(adapter);
        pref = getSharedPreferences(GNLConstants.SharedPreference.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        uid = pref.getLong(GNLConstants.SharedPreference.ID_KEY, -1);
        resizeLogo();

    }

    private void setCustomActionBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.update_categories));
        toolbar.setTitleTextColor(this.getResources().getColor(R.color.white));
        toolbar.setNavigationIcon(R.drawable.ic_search);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);

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
            if (validSelectedCount() && isChanged()) {
                sendSelectedCategories();
            }
            return true;
        } else if (id == android.R.id.home) {//search icon
            if (ed_search.getVisibility() == View.GONE) {
                getSupportActionBar().setTitle("");
                ed_search.setVisibility(View.VISIBLE);
                return true;
            } else {
                //perform searching
                pv_load.setVisibility(View.VISIBLE);
                lv_categories.setVisibility(View.GONE);
                filterList(ed_search.getText().toString());
                return true;
            }
        }

        return false;
    }

    private boolean isChanged() {
        ArrayList<Category> selected = getSelectedCats();
        ArrayList<User_Categories> user_categories = new ArrayList<>(User_CategoriesDAO.getAllUserCategories(uid));
        if(selected.size() != user_categories.size())
            return true;
        int counter = user_categories.size();
        for(Category category : selected){
            for(User_Categories user_category : user_categories){
                if(user_category.getCategoryID() == category.getServerID()) {
                    counter--;
                }
            }
        }
        return counter==0?false:true;
    }

    private void sendSelectedCategories() {
               final LoadingDialog loadingDialog = new LoadingDialog();
        Bundle args = new Bundle();
        args.putString(LoadingDialog.DIALOG_TEXT_KEY, getString(R.string.sending));
        loadingDialog.setArguments(args);
        loadingDialog.setCancelable(false);
        loadingDialog.show(getFragmentManager(), "sending");
        //send selected categories to server
        WebServiceFunctions.updateUserCategories(this, uid, getSelectedCats(), new OnSendCategoriesListener() {

            @Override
            public void onSendingSuccess() {
                loadingDialog.dismiss();
//                        AppSnackBar.show(rl_parent, "saved successfully", Color.GREEN, Color.WHITE);
                startActivity(new Intent(UpdateCategory.this, MainScreen.class));
                finish();
            }

            @Override
            public void onSendingFail(String error) {
                loadingDialog.dismiss();
                AppSnackBar.show(rl_parent, error, Color.RED, Color.WHITE);

            }
        });
    }

    private ArrayList<Category> getSelectedCats() {
        final ArrayList<Category> selectedCats = new ArrayList<>();
        for (Category category : original_categories) {
            if (category.isChecked())
                selectedCats.add(category);
        }
        return selectedCats;
    }

    private boolean validSelectedCount() {
        int numSelected = 0;
        for (Category category : original_categories) {
            if (category.isChecked())
                numSelected++;

        }
        if (numSelected < MIN_CATEGORY) {
            AppSnackBar.show(rl_parent, getString(R.string.BR_CATS_001), Color.RED, Color.WHITE);
            return false;
        } else if (numSelected > MAX_CATEGORY) {
            AppSnackBar.show(rl_parent, getString(R.string.BR_CATS_002), Color.RED, Color.WHITE);
            return false;
        }
        return true;
    }
    private void resizeLogo() {
        Display display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        Point screenSize = new Point(); // used to store screen size
        display.getSize(screenSize); // store size in screenSize
        uncolored_logo.getLayoutParams().height = (int) (screenSize.y * 0.25);
        uncolored_logo.getLayoutParams().width = (int) (screenSize.y * 0.25);
    }


    private void hideSoftKeyboard() {
        View view = UpdateCategory.this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Crouton.cancelAllCroutons();
    }
}
