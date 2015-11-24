package com.orchidatech.askandanswer.Activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.LinearLayout;

import com.orchidatech.askandanswer.Database.DAO.PostsDAO;
import com.orchidatech.askandanswer.Database.Model.Posts;
import com.orchidatech.askandanswer.R;
import com.orchidatech.askandanswer.View.Adapter.CategoryPostRecViewAdapter;
import com.orchidatech.askandanswer.View.Interface.OnPostEventListener;

import java.util.ArrayList;

public class CategoryPosts extends AppCompatActivity {
    public static final String CATEGORY_KEY = "CATEGORY";
    public static final String USER_ID = "USER";

    RecyclerView rv_posts;
    CategoryPostRecViewAdapter adapter;
    ArrayList<Posts> posts;
    LinearLayout ll_parent;
    private Toolbar toolbar;
    private long categoryId;
    private long userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_posts);
        setCustomActionBar();
        categoryId = getIntent().getLongExtra(CATEGORY_KEY, -1);
        userId = getIntent().getLongExtra(USER_ID, -1);
        initializeFields();
    }

    private void initializeFields() {
        posts = new ArrayList<>(PostsDAO.getAllPosts(userId, categoryId));
        ll_parent = (LinearLayout) this.findViewById(R.id.ll_parent);
        rv_posts = (RecyclerView) this.findViewById(R.id.rv_posts);
        adapter = new CategoryPostRecViewAdapter(this, posts, 20, ll_parent,  new OnPostEventListener() {

            @Override
            public void onClick(long pid) {

            }

            @Override
            public void onSharePost(long pid) {

            }

            @Override
            public void onCommentPost(long pid) {

            }

            @Override
            public void onFavoritePost(int position, long pid, long uid) {

            }

            @Override
            public void onCategoryClick(long cid, long uid) {

            }
        });
        rv_posts.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        rv_posts.setLayoutManager(llm);
        rv_posts.setAdapter(adapter);

    }

    private void setCustomActionBar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        toolbar.setNavigationIcon(R.drawable.ic_back);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }
}

