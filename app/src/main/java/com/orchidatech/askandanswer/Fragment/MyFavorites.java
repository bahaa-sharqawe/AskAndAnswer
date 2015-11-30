package com.orchidatech.askandanswer.Fragment;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.orchidatech.askandanswer.Activity.CategoryPosts;
import com.orchidatech.askandanswer.Activity.SplashScreen;
import com.orchidatech.askandanswer.Activity.ViewPost;
import com.orchidatech.askandanswer.Constant.AppSnackBar;
import com.orchidatech.askandanswer.Constant.GNLConstants;
import com.orchidatech.askandanswer.Database.DAO.Post_FavoriteDAO;
import com.orchidatech.askandanswer.Database.Model.Post_Favorite;
import com.orchidatech.askandanswer.R;
import com.orchidatech.askandanswer.View.Adapter.MyFavoritesRecViewAdapter;
import com.orchidatech.askandanswer.View.Interface.OnLastListReachListener;
import com.orchidatech.askandanswer.View.Interface.OnPostEventListener;
import com.orchidatech.askandanswer.View.Interface.OnPostFavoriteListener;
import com.orchidatech.askandanswer.View.Interface.OnUserFavPostFetched;
import com.orchidatech.askandanswer.WebService.WebServiceFunctions;

import java.util.ArrayList;

/**
 * Created by Bahaa on 18/11/2015.
 */
public class MyFavorites extends Fragment {
    RecyclerView rv_favorites;
    MyFavoritesRecViewAdapter adapter;
    ArrayList<Post_Favorite> myFavorites;
    RelativeLayout rl_parent;
    private long last_id_server = 0;
    private long user_id;

    RelativeLayout rl_error;
    ImageView uncolored_logo;
    TextView tv_error;
    ProgressBar pb_loading_main;
    private ArrayList<Post_Favorite> userFavPosts;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_fav_posts, null, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        user_id = SplashScreen.pref.getLong(GNLConstants.SharedPreference.ID_KEY, -1);
        rl_parent = (RelativeLayout) getActivity().findViewById(R.id.rl_parent);
        rv_favorites = (RecyclerView) getActivity().findViewById(R.id.rv_favorites);
        rv_favorites.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        rv_favorites.setLayoutManager(llm);
        myFavorites = new ArrayList<>();
        adapter = new MyFavoritesRecViewAdapter(getActivity(), myFavorites, rl_parent, new OnPostEventListener() {

            @Override
            public void onClick(long pid) {
                Intent intent = new Intent(getActivity(), ViewPost.class);
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
            public void onFavoritePost(final int position, long pid, long uid) {
                //remove post from favoritea
                WebServiceFunctions.removePostFavorite(getActivity(), pid, uid, new OnPostFavoriteListener() {

                    @Override
                    public void onSuccess() {
                        AppSnackBar.show(rl_parent, getString(R.string.post_favorite_removed), getResources().getColor(R.color.colorPrimary), Color.WHITE);
                        adapter.removePost(position);
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
                intent.putExtra(CategoryPosts.CATEGORY_KEY, cid);
                intent.putExtra(CategoryPosts.USER_ID, uid);
                startActivity(intent);
            }
        }, new OnLastListReachListener() {
            @Override
            public void onReached() {
                loadNewPosts();
            }
        });
        rv_favorites.setAdapter(adapter);
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
        rl_error.setVisibility(View.GONE);
        resizeLogo();
        loadNewPosts();
        userFavPosts = new ArrayList<>(Post_FavoriteDAO.getAllUserPostFavorite(user_id));
    }

    private void loadNewPosts() {
        WebServiceFunctions.getUserFavPosts(getActivity(), user_id, GNLConstants.POST_LIMIT, adapter.getItemCount() - 1, last_id_server, new OnUserFavPostFetched() {
            @Override
            public void onSuccess(ArrayList<Post_Favorite> userFavPosts, long last_id) {
                if (pb_loading_main.getVisibility() == View.VISIBLE) {
                    pb_loading_main.setVisibility(View.GONE);
                }
                Log.i("sd222222sd", userFavPosts.size()+"");
                last_id_server = last_id_server == 0 ? last_id : last_id_server;
                adapter.addFromServer(userFavPosts, false);
            }

            @Override
            public void onFail(String error, int errorCode) {
                if (pb_loading_main.getVisibility() == View.VISIBLE) {
                    pb_loading_main.setVisibility(View.GONE);
                    if (errorCode != 403) {//ALL ERRORS EXCEPT NO_FAV_POSTS
                        if (userFavPosts.size() > 0)
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
                    adapter.addFromServer(null, errorCode != 403 ? true : false);//CONNECTION ERROR
                }/*else{
                        getFromLocal();
                    }
*/
            }
        });
    }

    private void getFromLocal() {
        adapter.addFromLocal(userFavPosts);
    }

    private void resizeLogo() {
        Display display = ((WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        Point screenSize = new Point(); // used to store screen size
        display.getSize(screenSize); // store size in screenSize
        uncolored_logo.getLayoutParams().height = (int) (screenSize.y * 0.25);
        uncolored_logo.getLayoutParams().width = (int) (screenSize.y * 0.25);
    }
}
