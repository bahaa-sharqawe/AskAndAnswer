package com.orchidatech.askandanswer.Activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.orchidatech.askandanswer.Constant.AppSnackBar;
import com.orchidatech.askandanswer.Constant.GNLConstants;
import com.orchidatech.askandanswer.Database.Model.Posts;
import com.orchidatech.askandanswer.Fragment.LoadingDialog;
import com.orchidatech.askandanswer.R;
import com.orchidatech.askandanswer.View.Adapter.SearchRecViewAdapter;
import com.orchidatech.askandanswer.View.Interface.OnSearchCompleted;
import com.orchidatech.askandanswer.WebService.WebServiceFunctions;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.ArrayList;

public class Search extends AppCompatActivity {

MaterialSearchView searchView;
    private Toolbar toolbar;
    RecyclerView rv_posts;
    SearchRecViewAdapter adapter;
    ArrayList<Posts> posts;
    RelativeLayout rl_parent;
    private SharedPreferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        setCustomActionBar();
        posts = new ArrayList<>();
        pref = getSharedPreferences(GNLConstants.SharedPreference.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        rl_parent = (RelativeLayout) findViewById(R.id.rl_parent);
        rv_posts = (RecyclerView) findViewById(R.id.rv_posts);
        rv_posts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSoftKeyboard();
            }
        });
        rv_posts.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        rv_posts.setLayoutManager(llm);
        adapter = new SearchRecViewAdapter(this, posts, rl_parent);
        rv_posts.setAdapter(adapter);

        searchView = (MaterialSearchView) this.findViewById(R.id.search_view);
        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (query.trim().length() > 0) {
                    posts.clear();
                    hideSoftKeyboard();
                    performSearching(query.trim());
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_and_questions_menu, menu);
        MenuItem item = menu.findItem(R.id.search);
        searchView.setMenuItem(item);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
         if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return false;
    }

    private void setCustomActionBar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Search");
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        toolbar.setNavigationIcon(R.drawable.ic_back);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


    }

    private void performSearching(String s) {
        final LoadingDialog loadingDialog = new LoadingDialog();
        Bundle args = new Bundle();
        args.putString(LoadingDialog.DIALOG_TEXT_KEY, getString(R.string.search_questions));
        loadingDialog.setArguments(args);
        loadingDialog.show(getFragmentManager(), "search");
        loadingDialog.setCancelable(false);
        WebServiceFunctions.search(this, s, pref.getLong(GNLConstants.SharedPreference.ID_KEY, -1), new OnSearchCompleted() {

            @Override
            public void onSuccess(ArrayList<Posts> searchResult) {
                loadingDialog.dismiss();
                posts.addAll(searchResult);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onFail(String error) {
                loadingDialog.dismiss();
                adapter.notifyDataSetChanged();
                AppSnackBar.show(rl_parent, error, Color.RED, Color.WHITE);

            }
        });
    }

    private void hideSoftKeyboard() {
        View view = getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}
