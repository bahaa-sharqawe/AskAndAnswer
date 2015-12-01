package com.orchidatech.askandanswer.Fragment;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.orchidatech.askandanswer.Activity.AddEditPost;
import com.orchidatech.askandanswer.Activity.SplashScreen;
import com.orchidatech.askandanswer.Activity.ViewPost;
import com.orchidatech.askandanswer.Constant.AppSnackBar;
import com.orchidatech.askandanswer.Constant.GNLConstants;
import com.orchidatech.askandanswer.Database.DAO.PostsDAO;
import com.orchidatech.askandanswer.Database.Model.Posts;
import com.orchidatech.askandanswer.R;
import com.orchidatech.askandanswer.View.Adapter.TimelineRecViewAdapter;
import com.orchidatech.askandanswer.View.Interface.OnLastListReachListener;
import com.orchidatech.askandanswer.View.Interface.OnPostEventListener;
import com.orchidatech.askandanswer.View.Interface.OnPostFavoriteListener;
import com.orchidatech.askandanswer.View.Interface.OnUserPostFetched;
import com.orchidatech.askandanswer.WebService.WebServiceFunctions;

import java.util.ArrayList;

/**
 * Created by Bahaa on 7/11/2015.
 */
public class Timeline extends Fragment {
    RecyclerView rv_posts;
    TimelineRecViewAdapter adapter;
    ArrayList<Posts> allPosts;
    FloatingActionButton fab_add_post;
    RelativeLayout rl_num_notifications;
    RelativeLayout rl_parent;
    DrawerLayout mDrawerLayout;
    RecyclerView rv_notifications;
    private long last_id_server = 0;
    private long user_id;

    RelativeLayout rl_error;
    ImageView uncolored_logo;
    TextView tv_error;
    ProgressBar pb_loading_main;
    private ArrayList<Posts> allStoredPosts;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_timeline, null, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        user_id = SplashScreen.pref.getLong(GNLConstants.SharedPreference.ID_KEY, -1);
//        mDrawerLayout = (DrawerLayout) getActivity().findViewById(R.id.notif_drawer);
//        rv_notifications = (RecyclerView) getActivity().findViewById(R.id.rv_notifications);
        rl_num_notifications = (RelativeLayout) getActivity().findViewById(R.id.rl_num_notifications);
        rl_num_notifications.setVisibility(View.VISIBLE);
        rl_num_notifications.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                mDrawerLayout.openDrawer(GravityCompat.END);
//                mDrawerLayout.openDrawer(rv_notifications);
            }
        });
//        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(true);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Questions");
        fab_add_post = (FloatingActionButton) getActivity().findViewById(R.id.fab_add_post);
        fab_add_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), AddEditPost.class));
            }
        });
        rl_parent = (RelativeLayout) getActivity().findViewById(R.id.rl_parent);
        rv_posts = (RecyclerView) getActivity().findViewById(R.id.rv_posts);
//        rv_posts.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        rv_posts.setLayoutManager(llm);
        allPosts = new ArrayList<>();
        adapter = new TimelineRecViewAdapter(getActivity(), allPosts, rl_parent, new OnPostEventListener() {

            @Override
            public void onClick(long pid) {
                Bundle args = new Bundle();
                args.putLong(ViewPost.POST_ID, pid);
                Comments comments = new Comments();
                comments.setArguments(args);
                comments.show(getFragmentManager(), "Comments");
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
                WebServiceFunctions.addPostFavorite(getActivity(), pid, uid, new OnPostFavoriteListener(){

                    @Override
                    public void onSuccess() {
                        AppSnackBar.show(rl_parent, getString(R.string.post_favorite_added),getResources().getColor(R.color.colorPrimary), Color.WHITE);
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onFail(String error) {
                        AppSnackBar.show(rl_parent, error, Color.RED, Color.WHITE);

                    }
                });
            }

            @Override
            public void onCategoryClick(long cid, long uid) {
//            Intent intent = new Intent(getActivity(), CategoryPosts.class);
//                intent.putExtra(CategoryPosts.CATEGORY_KEY, cid);
//                intent.putExtra(CategoryPosts.USER_ID, uid);
//                startActivity(intent);
            }
        }, new OnLastListReachListener() {
            @Override
            public void onReached() {
                loadNewPosts();
            }
        });
        rv_posts.setAdapter(adapter);
        rl_error = (RelativeLayout) getActivity().findViewById(R.id.rl_error);
        uncolored_logo = (ImageView) getActivity().findViewById(R.id.uncolored_logo);
        tv_error = (TextView) getActivity().findViewById(R.id.tv_error);
        pb_loading_main = (ProgressBar) getActivity().findViewById(R.id.pb_loading_main);
        pb_loading_main.getIndeterminateDrawable().setColorFilter(Color.parseColor("#249885"), android.graphics.PorterDuff.Mode.MULTIPLY);
        rl_error.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setVisibility(View.GONE);
                pb_loading_main.setVisibility(View.VISIBLE);
                loadNewPosts();
            }
        });
//        userPosts = new ArrayList<>(PostsDAO.getUserPosts(user_id));
        rl_error.setVisibility(View.GONE);
        resizeLogo();
        allStoredPosts = PostsDAO.getPostsInUserCategories(user_id);
        loadNewPosts();
    }

    private void loadNewPosts() {
        WebServiceFunctions.geTimeLine(getActivity(), user_id, GNLConstants.POST_LIMIT, adapter.getItemCount()-1, last_id_server, new OnUserPostFetched() {
            @Override
            public void onSuccess(ArrayList<Posts> latestPosts, long last_id) {
                if(pb_loading_main.getVisibility() == View.VISIBLE){
                    pb_loading_main.setVisibility(View.GONE);
                }
                last_id_server = last_id_server == 0?last_id:last_id_server;
                adapter.addFromServer(latestPosts, false);
            }

            @Override
            public void onFail(String error, int errorCode) {
                if(pb_loading_main.getVisibility() == View.VISIBLE){
                    pb_loading_main.setVisibility(View.GONE);
                    if(errorCode != 402){//ALL ERRORS EXCEPT NO_POSTS
                        getFromLocal(error);
                    }else{
                        tv_error.setText(getActivity().getString(R.string.no_posts_found));
                        rl_error.setEnabled(false);
                        rl_error.setVisibility(View.VISIBLE);
                    }
                } else /*if(adapter.getItemCount() > 0)*/{
                        adapter.addFromServer(null, errorCode != 402?true:false);//CONNECTION ERROR
                    }/*else{
                        getFromLocal();
                    }
*/            }
        });
    }

    private void getFromLocal(String error) {
        if(allStoredPosts == null || allStoredPosts.size() == 0) {
            rl_error.setVisibility(View.VISIBLE);
            tv_error.setText(error);
            rl_error.setEnabled(true);
        }
        adapter.addFromLocal(allStoredPosts);
    }
    private void resizeLogo() {
        Display display = ((WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        Point screenSize = new Point(); // used to store screen size
        display.getSize(screenSize); // store size in screenSize
        uncolored_logo.getLayoutParams().height = (int) (screenSize.y * 0.25);
        uncolored_logo.getLayoutParams().width = (int) (screenSize.y * 0.25);
    }
}
