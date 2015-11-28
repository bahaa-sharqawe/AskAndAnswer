package com.orchidatech.askandanswer.Activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Display;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.orchidatech.askandanswer.Constant.AppSnackBar;
import com.orchidatech.askandanswer.Constant.GNLConstants;
import com.orchidatech.askandanswer.Database.DAO.Post_FavoriteDAO;
import com.orchidatech.askandanswer.Database.DAO.PostsDAO;
import com.orchidatech.askandanswer.Database.Model.Post_Favorite;
import com.orchidatech.askandanswer.Database.Model.Posts;
import com.orchidatech.askandanswer.Fragment.Comments;
import com.orchidatech.askandanswer.R;
import com.orchidatech.askandanswer.View.Adapter.CategoryPostRecViewAdapter;
import com.orchidatech.askandanswer.View.Interface.OnLastListReachListener;
import com.orchidatech.askandanswer.View.Interface.OnPostEventListener;
import com.orchidatech.askandanswer.View.Interface.OnPostFavoriteListener;
import com.orchidatech.askandanswer.View.Interface.OnUserFavPostFetched;
import com.orchidatech.askandanswer.View.Interface.OnUserPostFetched;
import com.orchidatech.askandanswer.WebService.WebServiceFunctions;

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
    private long last_id_server = 0;

    RelativeLayout rl_error;
    ImageView uncolored_logo;
    TextView tv_error;
    ProgressBar pb_loading_main;
    private ArrayList<Posts> storedPosts;


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
        adapter = new CategoryPostRecViewAdapter(this, posts, ll_parent, new OnPostEventListener() {
            @Override
            public void onClick(long pid) {
                Intent intent = new Intent(CategoryPosts.this, ViewPost.class);
                intent.putExtra(ViewPost.POST_ID, pid);
                startActivity(intent);
            }

            @Override
            public void onSharePost(long pid) {

            }

            @Override
            public void onCommentPost(long pid) {
                Bundle args = new Bundle();
                args.putLong(ViewPost.POST_ID, pid);
                Comments comments = new Comments();
                comments.setArguments(args);
                comments.show(getFragmentManager(), "Comments");

            }

            @Override
            public void onFavoritePost(int position, long pid, long uid) {
                //add post to favorites
                WebServiceFunctions.addPostFavorite(CategoryPosts.this, pid, uid, new OnPostFavoriteListener() {

                    @Override
                    public void onSuccess() {
                        AppSnackBar.show(ll_parent, getString(R.string.post_favorite_added), getResources().getColor(R.color.colorPrimary), Color.WHITE);
                    }

                    @Override
                    public void onFail(String error) {
                        AppSnackBar.show(ll_parent, error, Color.RED, Color.WHITE);

                    }
                });

            }

            @Override
            public void onCategoryClick(long cid, long uid) {

            }
        }, new OnLastListReachListener() {
            @Override
            public void onReached() {
                loadNewPosts();
            }
        });
        rv_posts.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        rv_posts.setLayoutManager(llm);
        rv_posts.setAdapter(adapter);
        rl_error = (RelativeLayout) this.findViewById(R.id.rl_error);
        uncolored_logo = (ImageView) this.findViewById(R.id.uncolored_logo);
        tv_error = (TextView) this.findViewById(R.id.tv_error);
        pb_loading_main = (ProgressBar) this.findViewById(R.id.pb_loading_main);
        rl_error.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setVisibility(View.GONE);
                pb_loading_main.setVisibility(View.VISIBLE);
                loadNewPosts();
            }
        });
        rl_error.setVisibility(View.GONE);
        resizeLogo();
        loadNewPosts();
        storedPosts = new ArrayList<>(PostsDAO.getAllPosts(userId, categoryId));

    }

    private void loadNewPosts() {
        WebServiceFunctions.getCategoryPosts(this, userId, categoryId, GNLConstants.POST_LIMIT, adapter.getItemCount() - 1, last_id_server, new OnUserPostFetched() {
            @Override
            public void onSuccess(ArrayList<Posts> userPosts, long last_id) {
                if (pb_loading_main.getVisibility() == View.VISIBLE) {
                    pb_loading_main.setVisibility(View.GONE);
                }
                last_id_server = last_id_server == 0 ? last_id : last_id_server;
                adapter.addFromServer(userPosts, false);
            }

            @Override
            public void onFail(String error, int errorCode) {
                if (pb_loading_main.getVisibility() == View.VISIBLE) {
                    pb_loading_main.setVisibility(View.GONE);
                    if (errorCode != 402) {//ALL ERRORS EXCEPT NO_POSTS
                        if (posts.size() > 0)
                            getFromLocal();
                        else {
                            rl_error.setVisibility(View.VISIBLE);
                            tv_error.setText(GNLConstants.getStatus(errorCode));
                            rl_error.setEnabled(true);
                        }
                    } else {
                        tv_error.setText(getString(R.string.no_posts_found));
                        rl_error.setEnabled(false);
                    }
                } else /*if(adapter.getItemCount() > 0)*/ {
                    adapter.addFromServer(null, errorCode != 402 ? true : false);//CONNECTION ERROR
                }/*else{
                        getFromLocal();
                    }
*/
            }
        });
    }

    private void getFromLocal() {
        adapter.addFromLocal(storedPosts);
    }


    private void resizeLogo() {
        Display display = ((WindowManager) this.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        Point screenSize = new Point(); // used to store screen size
        display.getSize(screenSize); // store size in screenSize
        uncolored_logo.getLayoutParams().height = (int) (screenSize.y * 0.25);
        uncolored_logo.getLayoutParams().width = (int) (screenSize.y * 0.25);
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