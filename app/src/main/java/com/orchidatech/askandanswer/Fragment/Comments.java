package com.orchidatech.askandanswer.Fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.orchidatech.askandanswer.Activity.ViewPost;
import com.orchidatech.askandanswer.Constant.GNLConstants;
import com.orchidatech.askandanswer.Database.DAO.CommentsDAO;
import com.orchidatech.askandanswer.R;
import com.orchidatech.askandanswer.View.Adapter.CommentsRecViewAdapter;
import com.orchidatech.askandanswer.View.Interface.OnCommentFetchListener;
import com.orchidatech.askandanswer.View.Interface.OnLastListReachListener;
import com.orchidatech.askandanswer.View.Interface.OnUserActionsListener;
import com.orchidatech.askandanswer.WebService.WebServiceFunctions;

import java.util.ArrayList;

/**
 * Created by Bahaa on 15/11/2015.
 */
public class Comments extends DialogFragment {
    AlertDialog dialog;
    RecyclerView mRecyclerView;
    CommentsRecViewAdapter adapter;
    long postId;
    private long last_id_server = 0;

    RelativeLayout rl_error;
    ImageView uncolored_logo;
    TextView tv_error;
    ProgressBar pb_loading_main;
    private ArrayList<com.orchidatech.askandanswer.Database.Model.Comments> postComments;
    private RelativeLayout rl_parent;
    private ArrayList<com.orchidatech.askandanswer.Database.Model.Comments> comments
            ;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        postId = getArguments().getLong(ViewPost.POST_ID);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        dialog = builder.create();
        dialog.getWindow().setBackgroundDrawable(getActivity().getResources().getDrawable(R.drawable.comments_fragment_backgnd));
        dialog.setView(getCustomView(), 0, 0, 0, 0);
        dialog.setCanceledOnTouchOutside(true);

        return dialog;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    private View getCustomView() {
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.fragment_comments, null, false);
        rl_parent = (RelativeLayout) view.findViewById(R.id.rl_parent);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.rv_comments);
        mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(llm);
        comments = new ArrayList<>();
        adapter = new CommentsRecViewAdapter(getActivity(), comments, rl_parent, new OnUserActionsListener() {
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
        mRecyclerView.setAdapter(adapter);
        rl_error = (RelativeLayout) view.findViewById(R.id.rl_error);
        uncolored_logo = (ImageView) view.findViewById(R.id.uncolored_logo);
        tv_error = (TextView) view.findViewById(R.id.tv_error);
        pb_loading_main = (ProgressBar) view.findViewById(R.id.pb_loading_main);
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
        postComments = new ArrayList<>(CommentsDAO.getAllCommentsByPost(postId));
        return view;
    }

    private void loadNewComments() {
        WebServiceFunctions.getPostComments(getActivity(), postId, GNLConstants.COMMENTS_LIMIT, adapter.getItemCount() - 1, last_id_server, new OnCommentFetchListener() {
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
                        if (postComments.size() > 0)
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
                    adapter.addFromServer(null, errorCode != 402 ? true : false);//CONNECTION ERROR
                }/*else{
                        getFromLocal();
                    }
*/
            }
        });
    }

    private void getFromLocal() {
        adapter.addFromLocal(postComments);
    }

    private void resizeLogo() {
        Display display = ((WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        Point screenSize = new Point(); // used to store screen size
        display.getSize(screenSize); // store size in screenSize
        uncolored_logo.getLayoutParams().height = (int) (screenSize.y * 0.25);
        uncolored_logo.getLayoutParams().width = (int) (screenSize.y * 0.25);
    }



}
