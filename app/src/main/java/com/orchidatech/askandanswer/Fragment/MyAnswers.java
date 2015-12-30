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
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.rahatarmanahmed.cpv.CircularProgressView;
import com.orchidatech.askandanswer.Constant.Enum;
import com.orchidatech.askandanswer.Constant.GNLConstants;
import com.orchidatech.askandanswer.Database.DAO.CommentsDAO;
import com.orchidatech.askandanswer.Database.DAO.PostsDAO;
import com.orchidatech.askandanswer.Database.Model.Posts;
import com.orchidatech.askandanswer.R;
import com.orchidatech.askandanswer.View.Adapter.CommentsRecViewAdapter;
import com.orchidatech.askandanswer.View.Adapter.TimelineRecViewAdapter;
import com.orchidatech.askandanswer.View.Interface.OnCommentFetchListener;
import com.orchidatech.askandanswer.View.Interface.OnCommentOptionListener;
import com.orchidatech.askandanswer.View.Interface.OnLastListReachListener;
import com.orchidatech.askandanswer.WebService.WebServiceFunctions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Bahaa on 18/11/2015.
 */
public class MyAnswers extends Fragment {
    RecyclerView rv_answers;
    TimelineRecViewAdapter adapter;
    List<com.orchidatech.askandanswer.Database.Model.Comments> myAnswers;
    private long last_id_server = 0;
    private long user_id;
    RelativeLayout rl_parent;

    RelativeLayout rl_error;
    ImageView uncolored_logo;
    TextView tv_error;
    CircularProgressView pb_loading_main;
    private ArrayList<com.orchidatech.askandanswer.Database.Model.Posts> allFetchedCommentedPosts;
    private SharedPreferences pref;
    ArrayList<Posts> allStoredCommentedPost;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setActionBar();
        View view  =  inflater.inflate(R.layout.fragment_my_answers, null, false);

        pref = getActivity().getSharedPreferences(GNLConstants.SharedPreference.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        user_id = pref.getLong(GNLConstants.SharedPreference.ID_KEY, -1);
        rl_parent = (RelativeLayout) view.findViewById(R.id.rl_parent);
        rv_answers = (RecyclerView) view.findViewById(R.id.rv_answers);

        rv_answers.setHasFixedSize(true);
        final LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        rv_answers.setLayoutManager(llm);
        allFetchedCommentedPosts = new ArrayList<>();
        adapter = new TimelineRecViewAdapter(getActivity(), allFetchedCommentedPosts, rl_parent, new OnLastListReachListener() {
            @Override
            public void onReached() {
                loadNewComments();
            }
        }, Enum.POSTS_FRAGMENTS.MY_ANSWERS_POSTS.getNumericType());
        rv_answers.setAdapter(adapter);

        rl_error = (RelativeLayout) view.findViewById(R.id.rl_error);
        uncolored_logo = (ImageView) view.findViewById(R.id.uncolored_logo);
        tv_error = (TextView) view.findViewById(R.id.tv_error);
        pb_loading_main = (CircularProgressView) view.findViewById(R.id.pb_loading_main);
//        pb_loading_main.getIndeterminateDrawable().setColorFilter(Color.parseColor("#249885"), android.graphics.PorterDuff.Mode.MULTIPLY);
        rl_error.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setVisibility(View.GONE);
                pb_loading_main.setVisibility(View.VISIBLE);
                loadNewComments();
            }
        });
        rl_error.setVisibility(View.GONE);
        pb_loading_main.setVisibility(View.VISIBLE);
        resizeLogo();

        myAnswers = new ArrayList<>(CommentsDAO.getAllComments(user_id));
        loadNewComments();

        return view;
    }


    private void loadNewComments() {
        WebServiceFunctions.getUserComments(getActivity(), user_id, GNLConstants.COMMENTS_LIMIT, adapter.getItemCount() - 1, last_id_server, new OnCommentFetchListener() {
            @Override
            public void onSuccess(ArrayList<com.orchidatech.askandanswer.Database.Model.Comments> comments, long last_id) {
                if (pb_loading_main.getVisibility() == View.VISIBLE) {
                    pb_loading_main.setVisibility(View.GONE);
                }
                last_id_server = last_id_server == 0 ? last_id : last_id_server;
                Log.i("list size",allFetchedCommentedPosts.size()+"" );
                ArrayList<Posts> newCommentedPost = new ArrayList<Posts>();
//                Collections.reverse(comments);
                for(int i = 0; i < comments.size(); i++){
                    Posts post = PostsDAO.getPost(comments.get(i).getPostID());
                    boolean isExist = false;
                    for(int x = 0; x < allFetchedCommentedPosts.size(); x++){
                        if(allFetchedCommentedPosts.get(x).getServerID() == comments.get(i).getPostID()){
                            isExist = true;
                            break;
                        }
                    }
                    if(!isExist)
                        allFetchedCommentedPosts.add(post);

                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onFail(String error, int errorCode) {
                if (pb_loading_main.getVisibility() == View.VISIBLE) {
                    if (errorCode != 402) {//ALL ERRORS EXCEPT NO_COMMENTS
                        if (myAnswers.size() > 0) {
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
                            if(isAdded())
                                tv_error.setText(GNLConstants.getStatus(errorCode));
                            rl_error.setEnabled(true);
                        }
                    } else {
                        pb_loading_main.setVisibility(View.GONE);
                        tv_error.setText(getActivity().getString(R.string.no_comments_found));
                        rl_error.setEnabled(true);
                        rl_error.setVisibility(View.VISIBLE);
                    }
                } else {
                    pb_loading_main.setVisibility(View.GONE);
                    adapter.addFromServer(null, errorCode != 402 ? true : false);
                }
            }
        });
    }

    private void getFromLocal() {
        allStoredCommentedPost  = new ArrayList<>();
        for(int i = 0; i < myAnswers.size(); i++){
            Posts post = PostsDAO.getPost(myAnswers.get(i).getPostID());
            boolean isExist = false;
            for(int x = 0; x < allStoredCommentedPost.size(); x++){
                if(allStoredCommentedPost.get(x).getServerID() == post.getServerID()){
                    isExist = true;
                    break;
                }
            }
            if(!isExist)
                allStoredCommentedPost.add(post);
        }
        adapter.addFromLocal(allStoredCommentedPost);
    }

    private void resizeLogo() {
        Display display = ((WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        Point screenSize = new Point(); // used to store screen size
        display.getSize(screenSize); // store size in screenSize
        uncolored_logo.getLayoutParams().height = (int) (screenSize.y * 0.25);
        uncolored_logo.getLayoutParams().width = (int) (screenSize.y * 0.25);
    }

    private void setActionBar() {
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("My Answers");
        ((AppCompatActivity) getActivity()).getSupportActionBar().show();
        (getActivity().findViewById(R.id.ed_search)).setVisibility(View.GONE);
        (getActivity().findViewById(R.id.rl_num_notifications)).setVisibility(View.GONE);
    }

//    @Override
//    public boolean onContextItemSelected(MenuItem item) {
//        long id = item.getItemId();
//        if (id == R.id.delete_comment) {
//            final int position = adapter.getPosition();
//            DeleteComment deletePost = new DeleteComment(new DeleteComment.OnDeleteListener() {
//
//                @Override
//                public void onDelete() {
//                    adapter.performDeleting(position);
//                }
//            });
//            deletePost.show(getActivity().getFragmentManager(), getActivity().getString(R.string.delete_comment));
//        }
//        return super.onContextItemSelected(item);
//    }
}
