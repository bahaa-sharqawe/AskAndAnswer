package com.orchidatech.askandanswer.Fragment;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.ListViewAutoScrollHelper;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.orchidatech.askandanswer.Activity.AddEditPost;
import com.orchidatech.askandanswer.Activity.SplashScreen;
import com.orchidatech.askandanswer.Activity.ViewPost;
import com.orchidatech.askandanswer.Constant.*;
import com.orchidatech.askandanswer.Constant.Enum;
import com.orchidatech.askandanswer.Database.DAO.PostsDAO;
import com.orchidatech.askandanswer.Database.Model.Notifications;
import com.orchidatech.askandanswer.Database.Model.Posts;
import com.orchidatech.askandanswer.R;
import com.orchidatech.askandanswer.View.Adapter.NotificationsAdapter;
import com.orchidatech.askandanswer.View.Adapter.TimelineRecViewAdapter;
import com.orchidatech.askandanswer.View.Interface.OnLastListReachListener;
import com.orchidatech.askandanswer.View.Interface.OnPostEventListener;
import com.orchidatech.askandanswer.View.Interface.OnPostFavoriteListener;
import com.orchidatech.askandanswer.View.Interface.OnUserPostFetched;
import com.orchidatech.askandanswer.WebService.WebServiceFunctions;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Bahaa on 7/11/2015.
 */
public class Timeline extends Fragment {
    RecyclerView rv_posts;
    TimelineRecViewAdapter adapter;
    NotificationsAdapter notificationsAdapter;
    List<Posts> allPosts;
    List<Notifications> allNotifications;
    FloatingActionButton fab_add_post;
    RelativeLayout rl_num_notifications;
    CoordinatorLayout coordinator_layout;
    DrawerLayout mDrawerLayout;
    RecyclerView rv_notifications;
    private long last_id_server = 0;
    private long user_id;

    RelativeLayout rl_error;
    ImageView uncolored_logo;
    TextView tv_error;
    ProgressBar pb_loading_main;
    private SharedPreferences pref;
    private List<Posts> allStoredPosts;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_timeline, null, false);
        pref = getActivity().getSharedPreferences(GNLConstants.SharedPreference.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        user_id = pref.getLong(GNLConstants.SharedPreference.ID_KEY, -1);
        setActionBar();
//        mDrawerLayout = (DrawerLayout) getActivity().findViewById(R.id.notif_drawer);
//        rv_notifications = (RecyclerView) getActivity().findViewById(R.id.rv_notifications);

        fab_add_post = (FloatingActionButton) view.findViewById(R.id.fab_add_post);
        fab_add_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), AddEditPost.class));
            }
        });
        coordinator_layout = (CoordinatorLayout) view.findViewById(R.id.coordinator_layout);
        rv_posts = (RecyclerView) view.findViewById(R.id.rv_posts);
        rv_notifications = (RecyclerView) view.findViewById(R.id.rv_notifications);
//        rv_posts.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        rv_posts.setLayoutManager(llm);

        LinearLayoutManager llm2 = new LinearLayoutManager(getActivity());
        llm2.setOrientation(LinearLayoutManager.VERTICAL);
        rv_notifications.setLayoutManager(llm2);

        allPosts = new ArrayList<>();
        allNotifications = new ArrayList<>();
        adapter = new TimelineRecViewAdapter(getActivity(), allPosts, coordinator_layout, new OnLastListReachListener() {
            @Override
            public void onReached() {
                loadNewPosts();
            }
        }, Enum.POSTS_FRAGMENTS.TIMELINE.getNumericType());

        notificationsAdapter = new NotificationsAdapter(getActivity(), allNotifications);
        rv_posts.setAdapter(adapter);
        rv_notifications.setAdapter(notificationsAdapter);

        rl_error = (RelativeLayout) view.findViewById(R.id.rl_error);
        uncolored_logo = (ImageView) view.findViewById(R.id.uncolored_logo);
        tv_error = (TextView) view.findViewById(R.id.tv_error);
        pb_loading_main = (ProgressBar) view.findViewById(R.id.pb_loading_main);
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
        mDrawerLayout = (DrawerLayout) view.findViewById(R.id.notif_drawer);
        resizeLogo();
        allStoredPosts = PostsDAO.getPostsInUserCategories(user_id);
        loadNewPosts();
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        rl_num_notifications = (RelativeLayout) getActivity().findViewById(R.id.rl_num_notifications);
        rl_num_notifications.setVisibility(View.VISIBLE);
        rl_num_notifications.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!mDrawerLayout.isDrawerOpen(GravityCompat.END))
                    mDrawerLayout.openDrawer(GravityCompat.END);
                else
                    mDrawerLayout.closeDrawer(GravityCompat.END);

//                mDrawerLayout.openDrawer(rv_notifications);
            }
        });

    }

    private void setActionBar() {
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Questions");
        ((AppCompatActivity)getActivity()).getSupportActionBar().show();
        ( getActivity().findViewById(R.id.ed_search)).setVisibility(View.GONE);
        (getActivity(). findViewById(R.id.rl_num_notifications)).setVisibility(View.VISIBLE);
    }

    private void loadNewPosts() {
        WebServiceFunctions.geTimeLine(getActivity(), user_id, GNLConstants.POST_LIMIT, adapter.getItemCount() - 1, last_id_server, new OnUserPostFetched() {
            @Override
            public void onSuccess(ArrayList<Posts> latestPosts, long last_id) {
                if (pb_loading_main.getVisibility() == View.VISIBLE) {
                    pb_loading_main.setVisibility(View.GONE);
                }
                last_id_server = last_id_server == 0 ? last_id : last_id_server;

                adapter.addFromServer(latestPosts, false);
            }

            @Override
            public void onFail(final String error, int errorCode) {
                if (pb_loading_main.getVisibility() == View.VISIBLE) {
                    if (errorCode != 402) {//ALL ERRORS EXCEPT NO_POSTS
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                pb_loading_main.setVisibility(View.GONE);
                                getFromLocal(error);
                            }
                        }, 3000);
//                            pb_loading_main.setVisibility(View.GONE);
//                            getFromLocal(error);

                    } else {
                        pb_loading_main.setVisibility(View.GONE);
                        tv_error.setText(getActivity().getString(R.string.no_posts_found));
                        rl_error.setEnabled(true);
                        rl_error.setVisibility(View.VISIBLE);
                    }
                } else {
                    pb_loading_main.setVisibility(View.GONE);

                    adapter.addFromServer(null, errorCode != 402 ? true : false);//CONNECTION ERROR
                }
            }
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

    @Override
    public void onPause() {
        super.onPause();
        Log.i("mmmmmmmmmmm", "pause");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.i("mmmmmmmmmmm", "stop");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.i("mmmmmmmmmmm", "detach");

    }
}
