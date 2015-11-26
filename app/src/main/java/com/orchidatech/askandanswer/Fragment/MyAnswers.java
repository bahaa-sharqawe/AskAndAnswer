package com.orchidatech.askandanswer.Fragment;

import android.app.Fragment;
import android.content.Context;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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

import com.orchidatech.askandanswer.Activity.SplashScreen;
import com.orchidatech.askandanswer.Constant.GNLConstants;
import com.orchidatech.askandanswer.Database.DAO.CommentsDAO;
import com.orchidatech.askandanswer.R;
import com.orchidatech.askandanswer.View.Adapter.MyAnswersRecViewAdapter;
import com.orchidatech.askandanswer.View.Interface.OnCommentFetchListener;
import com.orchidatech.askandanswer.View.Interface.OnLastListReachListener;
import com.orchidatech.askandanswer.View.Interface.OnUserActionsListener;
import com.orchidatech.askandanswer.WebService.WebServiceFunctions;

import java.util.ArrayList;

/**
 * Created by Bahaa on 18/11/2015.
 */
public class MyAnswers extends Fragment {
    RecyclerView rv_answers;
    MyAnswersRecViewAdapter adapter;
    ArrayList<com.orchidatech.askandanswer.Database.Model.Comments> myAnswers;
    LinearLayout ll_parent;
    private long last_id_server = 0;
    private long user_id;

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
        user_id = SplashScreen.pref.getLong(GNLConstants.SharedPreference.ID_KEY, -1);
        ll_parent = (LinearLayout) getActivity().findViewById(R.id.ll_parent);
        rv_answers = (RecyclerView) getActivity().findViewById(R.id.rv_answers);
        rv_answers.setHasFixedSize(true);
        final LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        rv_answers.setLayoutManager(llm);
        myAnswers = new ArrayList<>();
        adapter = new MyAnswersRecViewAdapter(getActivity(), myAnswers, ll_parent, new OnUserActionsListener() {
            @Override
            public void onLike(long commentId) {

            }

            @Override
            public void onDislike(long commentId) {
            }


        }, new OnLastListReachListener() {
            @Override
            public void onReached() {
                loadNewComments();
            }
        });
        rv_answers.setAdapter(adapter);
        rl_error = (RelativeLayout) getActivity().findViewById(R.id.rl_error);
        uncolored_logo = (ImageView) getActivity().findViewById(R.id.uncolored_logo);
        tv_error = (TextView) getActivity().findViewById(R.id.tv_error);
        pb_loading_main = (ProgressBar) getActivity().findViewById(R.id.pb_loading_main);
        rl_error.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setVisibility(View.GONE);
                pb_loading_main.setVisibility(View.VISIBLE);
                loadNewComments();
            }
        });
        rl_error.setVisibility(View.GONE);
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
                    pb_loading_main.setVisibility(View.GONE);
                    if (errorCode != 402) {//ALL ERRORS EXCEPT NO_POSTS
                        if (userComments.size() > 0)
                            getFromLocal();
                        else {
                            rl_error.setVisibility(View.VISIBLE);
                            tv_error.setText(GNLConstants.getStatus(errorCode));
                            rl_error.setEnabled(true);
                        }
                    } else {
                        tv_error.setText(getActivity().getString(R.string.no_comments_found));
                        rl_error.setEnabled(false);
                    }
                } else /*if(adapter.getItemCount() > 0)*/ {
                    adapter.addFromServer(null, errorCode != 402 ? true : false);
                }/*else{
                        getFromLocal();
                    }
*/
            }
        });
    }

    private void getFromLocal() {
        adapter.addFromLocal(userComments);
    }

    private void resizeLogo() {
        Display display = ((WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        Point screenSize = new Point(); // used to store screen size
        display.getSize(screenSize); // store size in screenSize
        uncolored_logo.getLayoutParams().height = (int) (screenSize.y * 0.25);
        uncolored_logo.getLayoutParams().width = (int) (screenSize.y * 0.25);
    }

}
