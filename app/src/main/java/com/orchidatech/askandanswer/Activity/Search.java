package com.orchidatech.askandanswer.Activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Handler;
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
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.orchidatech.askandanswer.Constant.AppSnackBar;
import com.orchidatech.askandanswer.Constant.GNLConstants;
import com.orchidatech.askandanswer.Database.Model.Posts;
import com.orchidatech.askandanswer.Fragment.LoadingDialog;
import com.orchidatech.askandanswer.R;
import com.orchidatech.askandanswer.View.Adapter.SearchRecViewAdapter;
import com.orchidatech.askandanswer.View.Interface.OnLastListReachListener;
import com.orchidatech.askandanswer.View.Interface.OnSearchCompleted;
import com.orchidatech.askandanswer.View.Utils.FontManager;
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
    private FontManager fontManager;
    private String filter;
    private long last_id_server = 0;
    ProgressBar pb_loading_main;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        setCustomActionBar();
        fontManager = FontManager.getInstance(getAssets());

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
        pb_loading_main = (ProgressBar) findViewById(R.id.pb_loading_main);
        pb_loading_main.getIndeterminateDrawable().setColorFilter(Color.parseColor("#249885"), android.graphics.PorterDuff.Mode.MULTIPLY);
        adapter = new SearchRecViewAdapter(this, posts, rl_parent, new OnLastListReachListener() {
            @Override
            public void onReached() {
                performSearching(filter);
            }
        });
        rv_posts.setAdapter(adapter);

        searchView = (MaterialSearchView) this.findViewById(R.id.search_view);
        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (query.trim().length() > 0) {
                    filter = query.trim();
                    posts.clear();
                    adapter.notifyDataSetChanged();
                    pb_loading_main.setVisibility(View.VISIBLE);
                    hideSoftKeyboard();
                    last_id_server = 0;
                    performSearching(filter);
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
//        final LoadingDialog loadingDialog = new LoadingDialog();
//        Bundle args = new Bundle();
//        args.putString(LoadingDialog.DIALOG_TEXT_KEY, getString(R.string.search_questions));
//        loadingDialog.setArguments(args);
//        loadingDialog.show(getFragmentManager(), "search");
//        loadingDialog.setCancelable(false);
        WebServiceFunctions.search(this, s, pref.getLong(GNLConstants.SharedPreference.ID_KEY, -1),GNLConstants.POST_LIMIT, adapter.getItemCount() - 1, last_id_server, new OnSearchCompleted() {

            @Override
            public void onSuccess(ArrayList<Posts> searchResult, long last_id) {
//                loadingDialog.dismiss();
                last_id_server = last_id_server == 0 ? last_id : last_id_server;
                adapter.addFromServer(searchResult, false);
            }

            @Override
            public void onFail(String error, int errorCode) {
            if (pb_loading_main.getVisibility() == View.VISIBLE) {
                    pb_loading_main.setVisibility(View.GONE);
                    AppSnackBar.show(rl_parent, error, Color.RED, Color.WHITE);
            } else {
                pb_loading_main.setVisibility(View.GONE);
                adapter.addFromServer(null, errorCode != 402 ? true : false);//CONNECTION ERROR
            }
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
