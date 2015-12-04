package com.orchidatech.askandanswer.Fragment;

import android.app.Fragment;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.orchidatech.askandanswer.Activity.SplashScreen;
import com.orchidatech.askandanswer.Constant.*;
import com.orchidatech.askandanswer.Constant.Enum;
import com.orchidatech.askandanswer.Database.DAO.CommentsDAO;
import com.orchidatech.askandanswer.R;
import com.orchidatech.askandanswer.View.Adapter.CommentsRecViewAdapter;
import com.orchidatech.askandanswer.View.Interface.OnCommentFetchListener;
import com.orchidatech.askandanswer.View.Interface.OnCommentOptionListener;
import com.orchidatech.askandanswer.View.Interface.OnLastListReachListener;
import com.orchidatech.askandanswer.WebService.WebServiceFunctions;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Bahaa on 18/11/2015.
 */
public class MyAnswers extends Fragment {
    RecyclerView rv_answers;
    CommentsRecViewAdapter adapter;
    List<com.orchidatech.askandanswer.Database.Model.Comments> myAnswers;
    private long last_id_server = 0;
    private long user_id;
    RelativeLayout rl_parent;

    RelativeLayout rl_error;
    ImageView uncolored_logo;
    TextView tv_error;
    ProgressBar pb_loading_main;
    private ArrayList<com.orchidatech.askandanswer.Database.Model.Comments> userComments;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_my_answers, null, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setActionBar();
        user_id = SplashScreen.pref.getLong(GNLConstants.SharedPreference.ID_KEY, -1);
        rl_parent = (RelativeLayout) getActivity().findViewById(R.id.rl_parent);
        rv_answers = (RecyclerView) getActivity().findViewById(R.id.rv_answers);
//        registerForContextMenu(rv_answers);

        rv_answers.setHasFixedSize(true);
        final LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        rv_answers.setLayoutManager(llm);
        myAnswers = new ArrayList<>();
        adapter = new CommentsRecViewAdapter(getActivity(), myAnswers, rl_parent/*, new OnUserActionsListener() {
            @Override
            public void onLike(long commentId, int positon) {

            }

            @Override
            public void onDislike(long commentId, int positon) {
            }


        }*/, new OnLastListReachListener() {
            @Override
            public void onReached() {
                loadNewComments();
            }
        }, new OnCommentOptionListener() {
            @Override
            public void onEditComment(long commentId) {

            }

            @Override
            public void onDeleteComment(long commentId) {

            }
        }, Enum.COMMENTS_FRAGMENTS.MY_ANSSWERS.getNumericType());
        rv_answers.setAdapter(adapter);

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
                loadNewComments();
            }
        });
        rl_error.setVisibility(View.GONE);
        pb_loading_main.setVisibility(View.VISIBLE);
        resizeLogo();
        loadNewComments();
        userComments = new ArrayList<>(CommentsDAO.getAllComments(user_id));

    }

    private void loadNewComments() {
        WebServiceFunctions.getUserComments(getActivity(), user_id, GNLConstants.COMMENTS_LIMIT, adapter.getItemCount() - 1, last_id_server, new OnCommentFetchListener() {
            @Override
            public void onSuccess(ArrayList<com.orchidatech.askandanswer.Database.Model.Comments> comments, long last_id) {
                if (pb_loading_main.getVisibility() == View.VISIBLE) {
                    pb_loading_main.setVisibility(View.GONE);
                }
                last_id_server = last_id_server == 0 ? last_id : last_id_server;
                adapter.addFromServer(comments, false);
            }

            @Override
            public void onFail(String error, int errorCode) {
                if (pb_loading_main.getVisibility() == View.VISIBLE) {
                    Log.i("gfddv", "from adffdff");

                    pb_loading_main.setVisibility(View.GONE);
                    if (errorCode != 402) {//ALL ERRORS EXCEPT NO_COMMENTS
                        if (userComments.size() > 0)
                            getFromLocal();
                        else {
                            rl_error.setVisibility(View.VISIBLE);
                            tv_error.setText(GNLConstants.getStatus(errorCode));
                            rl_error.setEnabled(true);
                        }
                    } else {
                        tv_error.setText(getActivity().getString(R.string.no_comments_found));
                        rl_error.setEnabled(true);
                        rl_error.setVisibility(View.VISIBLE);
                    }
                } else /*if(adapter.getItemCount() > 0)*/ {
                    Log.i("gfddv", "from erver");
                    adapter.addFromServer(null, errorCode != 402 ? true : false);

                }/*else{
                        getFromLocal();
                    }
*/
            }
        });
    }

    private void getFromLocal() {
        Log.i("fdcxzcx", "dsdsdlocal");
        adapter.addFromLocal(userComments);
    }

    private void resizeLogo() {
        Display display = ((WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        Point screenSize = new Point(); // used to store screen size
        display.getSize(screenSize); // store size in screenSize
        uncolored_logo.getLayoutParams().height = (int) (screenSize.y * 0.25);
        uncolored_logo.getLayoutParams().width = (int) (screenSize.y * 0.25);
    }
    private void setActionBar() {
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("My Answers");
        ((AppCompatActivity)getActivity()).getSupportActionBar().show();
        ( getActivity().findViewById(R.id.ed_search)).setVisibility(View.GONE);
        (getActivity(). findViewById(R.id.rl_num_notifications)).setVisibility(View.GONE);
    }
//
//    @Override
//    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
//        MenuInflater inflater = getActivity().getMenuInflater();
//        menu.setHeaderTitle("Comment Options:");
//        inflater.inflate(R.menu.comment_menu, menu);
//        super.onCreateContextMenu(menu, v, menuInfo);
//    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        long id = item.getItemId();
        if(id == R.id.delete_comment){
            final int position = adapter.getPosition();
            Log.i("dgdgfhffdhfpost", position+"");
            DeleteComment deletePost = new DeleteComment(new DeleteComment.OnDeleteListener(){

                @Override
                public void onDelete() {

                    adapter.performDeleting(position);
                }
            });
            deletePost.show(getActivity().getFragmentManager(), getActivity().getString(R.string.delete_comment));
        }
        return super.onContextItemSelected(item);
    }


}
