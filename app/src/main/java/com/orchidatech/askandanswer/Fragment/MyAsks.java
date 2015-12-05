package com.orchidatech.askandanswer.Fragment;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
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

import com.orchidatech.askandanswer.Constant.Enum;
import com.orchidatech.askandanswer.Constant.GNLConstants;
import com.orchidatech.askandanswer.Database.DAO.PostsDAO;
import com.orchidatech.askandanswer.Database.Model.Posts;
import com.orchidatech.askandanswer.R;
import com.orchidatech.askandanswer.View.Adapter.TimelineRecViewAdapter;
import com.orchidatech.askandanswer.View.Interface.OnLastListReachListener;
import com.orchidatech.askandanswer.View.Interface.OnUserPostFetched;
import com.orchidatech.askandanswer.WebService.WebServiceFunctions;

import java.util.ArrayList;

/**
 * Created by Bahaa on 24/11/2015.
 */
public class MyAsks extends Fragment {
    RecyclerView rv_favorites;
    TimelineRecViewAdapter adapter;
    ArrayList<Posts> myPosts;
    RelativeLayout rl_parent;
    private long last_id_server = 0;
    private long user_id;

    RelativeLayout rl_error;
    ImageView uncolored_logo;
    TextView tv_error;
    ProgressBar pb_loading_main;
    private ArrayList<Posts> userPosts;
    private SharedPreferences pref;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_my_asks, null, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setActionBar();
        pref = getActivity().getSharedPreferences(GNLConstants.SharedPreference.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        user_id = pref.getLong(GNLConstants.SharedPreference.ID_KEY, -1);
        rl_parent = (RelativeLayout) getActivity().findViewById(R.id.rl_parent);
        rv_favorites = (RecyclerView) getActivity().findViewById(R.id.rv_favorites);
        rv_favorites.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        rv_favorites.setLayoutManager(llm);
        myPosts = new ArrayList<>();
        adapter = new TimelineRecViewAdapter(getActivity(), myPosts, rl_parent, new OnLastListReachListener() {
            @Override
            public void onReached() {
                loadNewPosts();
            }
        }, Enum.POSTS_FRAGMENTS.MY_ASKS.getNumericType());
        rv_favorites.setAdapter(adapter);
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
        rl_error.setVisibility(View.GONE);
        resizeLogo();
        loadNewPosts();
        userPosts = new ArrayList<>(PostsDAO.getUserPosts(user_id));
    }

    private void loadNewPosts() {
        WebServiceFunctions.getUserPosts(getActivity(), user_id, GNLConstants.POST_LIMIT, adapter.getItemCount() - 1, last_id_server, new OnUserPostFetched() {
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
                        if (userPosts.size() > 0) {
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

    private void getFromLocal() {
        adapter.addFromLocal(userPosts);
    }

    private void resizeLogo() {
        Display display = ((WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        Point screenSize = new Point(); // used to store screen size
        display.getSize(screenSize); // store size in screenSize
        uncolored_logo.getLayoutParams().height = (int) (screenSize.y * 0.25);
        uncolored_logo.getLayoutParams().width = (int) (screenSize.y * 0.25);
    }

    private void setActionBar() {
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("My Asks");
        ((AppCompatActivity) getActivity()).getSupportActionBar().show();
        (getActivity().findViewById(R.id.ed_search)).setVisibility(View.GONE);
        (getActivity().findViewById(R.id.rl_num_notifications)).setVisibility(View.GONE);
    }
}
