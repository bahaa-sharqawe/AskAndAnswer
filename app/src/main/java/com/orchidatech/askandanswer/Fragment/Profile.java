package com.orchidatech.askandanswer.Fragment;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
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
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.orchidatech.askandanswer.Activity.UpdateProfile;
import com.orchidatech.askandanswer.Constant.*;
import com.orchidatech.askandanswer.Constant.Enum;
import com.orchidatech.askandanswer.Database.DAO.PostsDAO;
import com.orchidatech.askandanswer.Database.DAO.UsersDAO;
import com.orchidatech.askandanswer.Database.Model.Posts;
import com.orchidatech.askandanswer.Database.Model.Users;
import com.orchidatech.askandanswer.R;
import com.orchidatech.askandanswer.View.Adapter.TimelineRecViewAdapter;
import com.orchidatech.askandanswer.View.Interface.OnLastListReachListener;
import com.orchidatech.askandanswer.View.Interface.OnUserInfoFetched;
import com.orchidatech.askandanswer.View.Interface.OnUserPostFetched;
import com.orchidatech.askandanswer.View.Utils.FontManager;
import com.orchidatech.askandanswer.WebService.WebServiceFunctions;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

//import com.github.gorbin.asne.googleplus.GooglePlusSocialNetwork;

/**
 * Created by Bahaa on 7/11/2015.
 */
public class Profile extends Fragment {
    public static final int MATCH_PARENT = RelativeLayout.LayoutParams.MATCH_PARENT;
    public static final String USER_ID_KEY = "USER_ID";


    RecyclerView rv_posts;
    TimelineRecViewAdapter adapter;
    ArrayList<Posts> posts;
    TextView tv_person;
    RatingBar rating_person;
    CircleImageView iv_profile;
    TextView tv_asks;
    TextView tv_answers;
    FloatingActionButton fab_edit_profile;

    private long last_id_server = 0;
    private long user_id;
    Users user;
    RelativeLayout rl_parent;
    RelativeLayout rl_error;
    ImageView uncolored_logo;
    TextView tv_error;
    ProgressBar pb_loading_main;
    private ArrayList<Posts> userPosts;
    private SharedPreferences pref;
    private FontManager fontManager;
    private TextView tv_person_photo;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
       View view = inflater.inflate(R.layout.fragment_profile, null, false);
        setActionBar();
        fontManager = FontManager.getInstance(getActivity().getAssets());

        user_id = getArguments().getLong(USER_ID_KEY, -1);
        user = UsersDAO.getUser(user_id);
        pref = getActivity().getSharedPreferences(GNLConstants.SharedPreference.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        rl_parent = (RelativeLayout) view.findViewById(R.id.rl_parent);
        fab_edit_profile = (FloatingActionButton) view.findViewById(R.id.fab_edit_profile);
        fab_edit_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), UpdateProfile.class));
                getActivity().overridePendingTransition(R.anim.enter, R.anim.exit);

            }
        });
        if (pref.getLong(GNLConstants.SharedPreference.ID_KEY, -1) != user_id)
            fab_edit_profile.setVisibility(View.GONE);

        rating_person = (RatingBar) view.findViewById(R.id.rating_person);
        iv_profile = (CircleImageView) view.findViewById(R.id.iv_profile);
        tv_person_photo = (TextView) view.findViewById(R.id.tv_person_photo);
        tv_person = (TextView) view.findViewById(R.id.tv_person);
        tv_person.setTypeface(fontManager.getFont(FontManager.ROBOTO_MEDIUM));

        tv_answers = (TextView) view.findViewById(R.id.tv_answers);
        tv_answers.setTypeface(fontManager.getFont(FontManager.ROBOTO_LIGHT));

        tv_asks = (TextView) view.findViewById(R.id.tv_asks);
        tv_asks.setTypeface(fontManager.getFont(FontManager.ROBOTO_LIGHT));

        rating_person.setRating(user.getRating());
        Log.i("rating", user.getRating()+"");
        tv_person.setText(user.getFname() + " " + user.getLname());
        if(user != null && !user.getImage().equals(URL.DEFAULT_IMAGE))
            Picasso.with(getActivity()).load(Uri.parse(user.getImage())).into(iv_profile, new Callback() {
                @Override
                public void onSuccess() {
                    tv_person_photo.setVisibility(View.INVISIBLE);
                    iv_profile.setVisibility(View.VISIBLE);
                }

                @Override
                public void onError() {
                    iv_profile.setVisibility(View.INVISIBLE);
                    tv_person_photo.setVisibility(View.VISIBLE);
                    tv_person_photo.setText(user.getFname().charAt(0)+"");
                }
            });
        else{
            iv_profile.setVisibility(View.INVISIBLE);
            tv_person_photo.setVisibility(View.VISIBLE);
            tv_person_photo.setText(user.getFname().charAt(0)+"");
        }
        tv_person_photo.setTypeface(fontManager.getFont(FontManager.ROBOTO_MEDIUM));

        tv_asks.setText(getString(R.string.tv_ask_count, user.getAsks()));
        tv_answers.setText(getString(R.string.tv_answer_count, user.getAnswers()));
//        getUserInfo(user_id);
        rv_posts = (RecyclerView) view.findViewById(R.id.rv_posts);
        rv_posts.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        rv_posts.setLayoutManager(llm);
        posts = new ArrayList<>();
        adapter = new TimelineRecViewAdapter(getActivity(), posts, rl_parent, new OnLastListReachListener() {
            @Override
            public void onReached() {
                loadNewPosts();
            }
        }, Enum.POSTS_FRAGMENTS.PROFILE.getNumericType());
        rv_posts.setAdapter(adapter);
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
        userPosts = new ArrayList<>(PostsDAO.getUserPosts(user_id));
        rl_error.setVisibility(View.GONE);
        if(user_id == pref.getLong(GNLConstants.SharedPreference.ID_KEY, -1) || user.getIsPublicProfile() == 0) {
            loadNewPosts();
        }else{
            pb_loading_main.setVisibility(View.GONE);
        }
        resizeLogo();



        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    private void setActionBar() {
        ((AppCompatActivity) getActivity()).getSupportActionBar().show();
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Profile");
        ( getActivity().findViewById(R.id.ed_search)).setVisibility(View.GONE);
        (getActivity(). findViewById(R.id.rl_num_notifications)).setVisibility(View.GONE);

    }

    private void getUserInfo(final long user_id) {
        WebServiceFunctions.getUserInfo(getActivity(), user_id, new OnUserInfoFetched() {

            @Override
            public void onDataFetched(Users user, int no_answer, int no_ask) {
                tv_person.setText(user.getFname() + " " + user.getLname());
                Picasso.with(getActivity()).load(Uri.parse(user.getImage())).into(iv_profile, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError() {
                        iv_profile.setImageResource(R.drawable.ic_user);
                    }
                });
                tv_asks.setText(getActivity().getString(R.string.tv_ask_count, no_ask));
                tv_answers.setText(getActivity().getString(R.string.tv_answer_count, no_answer));
            }

            @Override
            public void onFail(String error) {
                Users user = null;
                if ((user = UsersDAO.getUser(user_id)) == null)
                    AppSnackBar.show(rl_parent, getString(R.string.BR_GNL_005), Color.RED, Color.WHITE);
                else {
                    tv_person.setText(user.getFname() + " " + user.getLname());
                    Picasso.with(getActivity()).load(Uri.parse(user.getImage())).into(iv_profile);
                }
            }
        });
    }

    private void loadNewPosts() {
        WebServiceFunctions.getUserPosts(getActivity(), user_id, GNLConstants.POST_LIMIT, adapter.getItemCount()-1, last_id_server, new OnUserPostFetched() {
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
                        }else {
                            pb_loading_main.setVisibility(View.GONE);
                            rl_error.setVisibility(View.VISIBLE);
                            tv_error.setText(GNLConstants.getStatus(errorCode));
                            rl_error.setEnabled(true);
                        }
                    } else {
                        pb_loading_main.setVisibility(View.GONE);
                        tv_error.setText(getString(R.string.no_posts_found));
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

}
