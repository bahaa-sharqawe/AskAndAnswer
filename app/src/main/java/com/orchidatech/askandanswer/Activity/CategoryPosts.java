package com.orchidatech.askandanswer.Activity;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Display;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.github.rahatarmanahmed.cpv.CircularProgressView;
import com.orchidatech.askandanswer.Constant.*;
import com.orchidatech.askandanswer.Constant.Enum;
import com.orchidatech.askandanswer.Database.DAO.CategoriesDAO;
import com.orchidatech.askandanswer.Database.DAO.PostsDAO;
import com.orchidatech.askandanswer.Database.Model.Posts;
import com.orchidatech.askandanswer.Logic.LollipopBitmapMemoryCacheParamsSupplier;
import com.orchidatech.askandanswer.R;
import com.orchidatech.askandanswer.View.Adapter.TimelineRecViewAdapter;
import com.orchidatech.askandanswer.View.Interface.OnLastListReachListener;
import com.orchidatech.askandanswer.View.Interface.OnUserPostFetched;
import com.orchidatech.askandanswer.WebService.WebServiceFunctions;

import java.util.ArrayList;

public class CategoryPosts extends AppCompatActivity {
    public static final String CATEGORY_KEY = "CATEGORY";
    public static final String USER_ID = "USER";

    RecyclerView rv_posts;
    TimelineRecViewAdapter adapter;
    ArrayList<Posts> posts;
    RelativeLayout rl_parent;
    private Toolbar toolbar;
    private long categoryId;
    private long userId;
    private long last_id_server = 0;

    RelativeLayout rl_error;
    ImageView uncolored_logo;
    TextView tv_error;
    CircularProgressView pb_loading_main;
    private ArrayList<Posts> storedPosts;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initFresco();
//        Fresco.initialize(getApplicationContext());
        setContentView(R.layout.activity_category_posts);
        setCustomActionBar();
        categoryId = getIntent().getLongExtra(CATEGORY_KEY, -1);
//        userId = getIntent().getLongExtra(USER_ID, -1);
        initializeFields();
    }

    private void initializeFields() {
        userId = getSharedPreferences(GNLConstants.SharedPreference.SHARED_PREF_NAME, MODE_PRIVATE).getLong(GNLConstants.SharedPreference.ID_KEY, -1);
        posts = new ArrayList<>();
        rl_parent = (RelativeLayout) this.findViewById(R.id.rl_parent);
        rv_posts = (RecyclerView) this.findViewById(R.id.rv_posts);
        adapter = new TimelineRecViewAdapter(this, posts, rl_parent/*, new OnPostEventListener() {
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
                        AppSnackBar.show(rl_parent, getString(R.string.post_favorite_added), getResources().getColor(R.color.colorPrimary), Color.WHITE);
                    }

                    @Override
                    public void onFail(String error) {
                        AppSnackBar.show(rl_parent, error, Color.RED, Color.WHITE);

                    }
                });

            }

            @Override
            public void onCategoryClick(long cid, long uid) {

            }
        }*/, new OnLastListReachListener() {
            @Override
            public void onReached() {
                loadNewPosts();
            }
        }, Enum.POSTS_FRAGMENTS.CATEGORY_POST.getNumericType());
        rv_posts.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        rv_posts.setLayoutManager(llm);
        rv_posts.setAdapter(adapter);
        rl_error = (RelativeLayout) this.findViewById(R.id.rl_error);
        uncolored_logo = (ImageView) this.findViewById(R.id.uncolored_logo);
        tv_error = (TextView) this.findViewById(R.id.tv_error);
        pb_loading_main = (CircularProgressView) this.findViewById(R.id.pb_loading_main);
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
        storedPosts = new ArrayList<>(PostsDAO.getAllPostsInCategory(categoryId));
        setTitle(CategoriesDAO.getCategory(categoryId).getName());
    }

    @Override
    public void setTitle(CharSequence title) {
        getSupportActionBar().setTitle(title);
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
                    if (errorCode != 402) {//ALL ERRORS EXCEPT NO_POSTS
                        if (storedPosts.size() > 0) {
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    pb_loading_main.setVisibility(View.GONE);
                                    getFromLocal();

                                }
                            }, 3000);
                        } else {
                            pb_loading_main.setVisibility(View.GONE);
                            rl_error.setVisibility(View.VISIBLE);
                            tv_error.setText(GNLConstants.getStatus(errorCode));
                            rl_error.setEnabled(true);
                        }
                    } else {
                        pb_loading_main.setVisibility(View.GONE);
                        tv_error.setText(getString(R.string.no_posts_found));
                        rl_error.setEnabled(false);
                    }
                } else /*if(adapter.getItemCount() > 0)*/ {
                    pb_loading_main.setVisibility(View.GONE);
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
    private void initFresco() {
        ActivityManager activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        ImagePipelineConfig imagePipelineConfig = ImagePipelineConfig
                .newBuilder(getApplicationContext())
                .setBitmapMemoryCacheParamsSupplier(new LollipopBitmapMemoryCacheParamsSupplier(activityManager))
                .build();

        Fresco.initialize(getApplicationContext(), imagePipelineConfig);
    }
}