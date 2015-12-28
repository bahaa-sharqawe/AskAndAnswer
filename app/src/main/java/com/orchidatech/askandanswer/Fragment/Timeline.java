package com.orchidatech.askandanswer.Fragment;

import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
import android.support.v4.widget.SwipeRefreshLayout;
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

import com.github.rahatarmanahmed.cpv.CircularProgressView;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.orchidatech.askandanswer.Activity.AddEditPost;
import com.orchidatech.askandanswer.Activity.SplashScreen;
import com.orchidatech.askandanswer.Activity.ViewPost;
import com.orchidatech.askandanswer.Constant.*;
import com.orchidatech.askandanswer.Constant.Enum;
import com.orchidatech.askandanswer.Database.DAO.NotificationsDAO;
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

import me.zhanghai.android.materialprogressbar.MaterialProgressBar;

/**
 * Created by Bahaa on 7/11/2015.
 */
public class Timeline extends Fragment {
    public static final String CUSTOM_INTENT = "com.orchida.askandanswer.notification_received";
    private final IntentFilter intentFilter = new IntentFilter(CUSTOM_INTENT);


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
    TextView tv_no_notification;
    private long last_id_server = 0;
    private long user_id;

    RelativeLayout rl_error;
    ImageView uncolored_logo;
    TextView tv_error;
    CircularProgressView pb_loading_main;
    private SharedPreferences pref;
    private List<Posts> allStoredPosts;

    TextView tv_notifications_count;
    private BroadcastReceiver notifications_listener = new NotificationRec();
    SwipeRefreshLayout swipeRefreshLayout;
    private int numOfPostFetchedSwiping = 0;

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
        tv_no_notification = (TextView) view.findViewById(R.id.tv_no_notification);
//        rv_posts.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        rv_posts.setLayoutManager(llm);

        LinearLayoutManager llm2 = new LinearLayoutManager(getActivity());
        llm2.setOrientation(LinearLayoutManager.VERTICAL);
        rv_notifications.setLayoutManager(llm2);

        allPosts = new ArrayList<>();
        allNotifications = new ArrayList<>(NotificationsDAO.getAllNotifications());
        Log.i("xvxv", allNotifications.size() + "");
        adapter = new TimelineRecViewAdapter(getActivity(), allPosts, coordinator_layout, new OnLastListReachListener() {
            @Override
            public void onReached() {
                loadNewPosts();
            }
        }, Enum.POSTS_FRAGMENTS.TIMELINE.getNumericType());
        if (allNotifications.size() > 0)
            tv_no_notification.setVisibility(View.INVISIBLE);
        else
            tv_no_notification.setVisibility(View.VISIBLE);
        notificationsAdapter = new NotificationsAdapter(getActivity(), allNotifications);
        rv_posts.setAdapter(adapter);
        rv_notifications.setAdapter(notificationsAdapter);

        rl_error = (RelativeLayout) view.findViewById(R.id.rl_error);
        uncolored_logo = (ImageView) view.findViewById(R.id.uncolored_logo);
        tv_error = (TextView) view.findViewById(R.id.tv_error);
        pb_loading_main = (CircularProgressView) view.findViewById(R.id.pb_loading_main);
//        pb_loading_main.getIndeterminateDrawable().setColorFilter(Color.parseColor("#2dbda6"), android.graphics.PorterDuff.Mode.MULTIPLY);
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
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setColorSchemeResources(
                R.color.refresh_progress_1,
                R.color.refresh_progress_2,
                R.color.refresh_progress_3);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshItems();
            }
        });
        resizeLogo();
        allStoredPosts = PostsDAO.getPostsInUserCategories(user_id);

        loadNewPosts();
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        tv_notifications_count = (TextView) getActivity().findViewById(R.id.tv_notifications_count);
      getNotificationsCount();
        rl_num_notifications = (RelativeLayout) getActivity().findViewById(R.id.rl_num_notifications);
        rl_num_notifications.setVisibility(View.VISIBLE);
        rl_num_notifications.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mDrawerLayout.isDrawerOpen(GravityCompat.END))
                    mDrawerLayout.openDrawer(GravityCompat.END);
                else
                    mDrawerLayout.closeDrawer(GravityCompat.END);

//                mDrawerLayout.openDrawer(rv_notifications);
            }
        });

    }

    private void getNotificationsCount() {
        ArrayList<Notifications> allNotDoneNoti = new ArrayList<>(NotificationsDAO.getAllNotDoneNotifications());
        if (allNotDoneNoti.size() == 0)
            tv_notifications_count.setVisibility(View.INVISIBLE);
        else
            tv_notifications_count.setText(allNotDoneNoti.size() + "");
    }

    private void setActionBar() {
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Questions");
        ((AppCompatActivity) getActivity()).getSupportActionBar().show();
        (getActivity().findViewById(R.id.ed_search)).setVisibility(View.GONE);
        (getActivity().findViewById(R.id.rl_num_notifications)).setVisibility(View.VISIBLE);
    }

    private void refreshItems() {
        if(adapter.getItemCount()==1){ swipeRefreshLayout.setRefreshing(false);return;}
        WebServiceFunctions.getNewestPosts(getActivity(), user_id, adapter.getNewestPostId(), new OnUserPostFetched() {
            @Override
            public void onSuccess(ArrayList<Posts> latestPosts, long last_id) {
                adapter.addFrontOfList(latestPosts);
                swipeRefreshLayout.setRefreshing(false);
                numOfPostFetchedSwiping +=latestPosts.size();
            }

            @Override
            public void onFail(final String error, int errorCode) {
                swipeRefreshLayout.setRefreshing(false);
            }

        });
    }
    private void loadNewPosts() {
//        if (pb_loading_main.getVisibility() == View.VISIBLE)
//            swipeRefreshLayout.setEnabled(false);
                WebServiceFunctions.geTimeLine(getActivity(), user_id, GNLConstants.POST_LIMIT, adapter.getItemCount() - numOfPostFetchedSwiping - 1, last_id_server, new OnUserPostFetched() {
                    @Override
                    public void onSuccess(ArrayList<Posts> latestPosts, long last_id) {
                        if (pb_loading_main.getVisibility() == View.VISIBLE) {
                            pb_loading_main.setVisibility(View.GONE);
                        }
                        last_id_server = last_id_server == 0 ? last_id : last_id_server;

                        adapter.addFromServer(latestPosts, false);
                        swipeRefreshLayout.setEnabled(true);

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
                                        swipeRefreshLayout.setEnabled(true);
                                    }
                                }, 3000);
//                            pb_loading_main.setVisibility(View.GONE);
//                            getFromLocal(error);

                            } else {
                                pb_loading_main.setVisibility(View.GONE);
                                tv_error.setText(getActivity().getString(R.string.no_posts_found));
                                rl_error.setEnabled(true);
                                rl_error.setVisibility(View.VISIBLE);
                                swipeRefreshLayout.setEnabled(false);
                            }
                        } else {
                            pb_loading_main.setVisibility(View.GONE);
                            adapter.addFromServer(null, errorCode != 402 ? true : false);//CONNECTION ERROR
                            swipeRefreshLayout.setEnabled(true);

                        }
                    }
                });
    }

            private void getFromLocal(String error) {
                if (allStoredPosts == null || allStoredPosts.size() == 0) {
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
            public void onResume() {
                super.onResume();
                getActivity().registerReceiver(notifications_listener, intentFilter);
            }

            @Override
            public void onPause() {
                super.onPause();
                getActivity().unregisterReceiver(notifications_listener);

            }

            private class NotificationRec extends BroadcastReceiver {

                @Override
                public void onReceive(Context context, Intent intent) {
                    getNotificationsCount();
                    allNotifications.add(0, NotificationsDAO.getAllNotifications().get(0));
                    notificationsAdapter.notifyDataSetChanged();
        }

    }
}
