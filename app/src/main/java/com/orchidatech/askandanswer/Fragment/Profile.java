package com.orchidatech.askandanswer.Fragment;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
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
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.orchidatech.askandanswer.Activity.CategoryPosts;
import com.orchidatech.askandanswer.Activity.SplashScreen;
import com.orchidatech.askandanswer.Activity.UpdateProfile;
import com.orchidatech.askandanswer.Activity.ViewPost;
import com.orchidatech.askandanswer.Constant.AppSnackBar;
import com.orchidatech.askandanswer.Constant.GNLConstants;
import com.orchidatech.askandanswer.Database.DAO.PostsDAO;
import com.orchidatech.askandanswer.Database.DAO.UsersDAO;
import com.orchidatech.askandanswer.Database.Model.Posts;
import com.orchidatech.askandanswer.Database.Model.Users;
import com.orchidatech.askandanswer.R;
import com.orchidatech.askandanswer.View.Adapter.ProfileRecViewAdapter;
import com.orchidatech.askandanswer.View.Interface.OnLastListReachListener;
import com.orchidatech.askandanswer.View.Interface.OnPostEventListener;
import com.orchidatech.askandanswer.View.Interface.OnPostFavoriteListener;
import com.orchidatech.askandanswer.View.Interface.OnUserInfoFetched;
import com.orchidatech.askandanswer.View.Interface.OnUserPostFetched;
import com.orchidatech.askandanswer.WebService.WebServiceFunctions;
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
    ProfileRecViewAdapter adapter;
    ArrayList<Posts> posts;
    TextView tv_person;
    RatingBar rating_person;
    CircleImageView iv_profile;
    TextView tv_asks;
    TextView tv_answers;
    FloatingActionButton fab_edit_profile;

    private long last_id_server = 0;
    private long user_id;
    RelativeLayout rl_parent;
    RelativeLayout rl_error;
    ImageView uncolored_logo;
    TextView tv_error;
    ProgressBar pb_loading_main;
    private ArrayList<Posts> userPosts;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        user_id = getArguments().getLong(USER_ID_KEY, -1);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, null, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setActionBar();
        rl_parent = (RelativeLayout) getActivity().findViewById(R.id.rl_parent);
        fab_edit_profile = (FloatingActionButton) getActivity().findViewById(R.id.fab_edit_profile);
        fab_edit_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), UpdateProfile.class));
            }
        });
        if (SplashScreen.pref.getLong(GNLConstants.SharedPreference.ID_KEY, -1) != user_id)
            fab_edit_profile.setVisibility(View.GONE);

        rating_person = (RatingBar) getActivity().findViewById(R.id.rating_person);
        iv_profile = (CircleImageView) getActivity().findViewById(R.id.iv_profile);
        tv_person = (TextView) getActivity().findViewById(R.id.tv_person);
        tv_answers = (TextView) getActivity().findViewById(R.id.tv_answers);
        tv_asks = (TextView) getActivity().findViewById(R.id.tv_asks);
        getUserInfo(user_id);
        rv_posts = (RecyclerView) getActivity().findViewById(R.id.rv_posts);
        rv_posts.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        rv_posts.setLayoutManager(llm);
        posts = new ArrayList<>();
        adapter = new ProfileRecViewAdapter(getActivity(), posts, rl_parent, new OnPostEventListener() {

            @Override
            public void onClick(long pid) {

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
                comments.show(getFragmentManager(), "comments");
//                Toast.makeText(getActivity(), pid+"", Toast.LENGTH_LONG).show();
//                Bundle args = new Bundle();
//                args.putLong(ViewPost.POST_ID, pid);
//                Comments comments = new Comments();
//                comments.setArguments(args);
//                getFragmentManager().beginTransaction().replace(R.id.comment_fragment_container, comments).commit();
//                getFragmentManager().executePendingTransactions();
            }

            @Override
            public void onFavoritePost(final int position, long pid, long uid) {
                //add post to favorites
                WebServiceFunctions.addPostFavorite(getActivity(), pid, uid, new OnPostFavoriteListener() {

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
                Intent intent = new Intent(getActivity(), CategoryPosts.class);
                Bundle args = new Bundle();
                args.putLong(CategoryPosts.CATEGORY_KEY, cid);
                startActivity(intent);

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
        resizeLogo();
        loadNewPosts();

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
                Picasso.with(getActivity()).load(Uri.parse(user.getImage())).into(iv_profile);
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
                    pb_loading_main.setVisibility(View.GONE);
                    if (errorCode != 402) {//ALL ERRORS EXCEPT NO_POSTS
                        if (userPosts.size() > 0)
                            getFromLocal();
                        else {
                            rl_error.setVisibility(View.VISIBLE);
                            tv_error.setText(GNLConstants.getStatus(errorCode));
                            rl_error.setEnabled(true);
                        }
                    } else {
                        tv_error.setText(getActivity().getString(R.string.no_posts_found));
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
