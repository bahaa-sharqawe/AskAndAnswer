package com.orchidatech.askandanswer.Fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.orchidatech.askandanswer.Database.Model.Posts;
import com.orchidatech.askandanswer.R;
import com.orchidatech.askandanswer.View.Adapter.MyAnswersRecViewAdapter;
import com.orchidatech.askandanswer.View.Interface.OnUserActionsListener;

import java.util.ArrayList;

/**
 * Created by Bahaa on 18/11/2015.
 */
public class MyAnswers extends Fragment {
    RecyclerView rv_answers;
    MyAnswersRecViewAdapter adapter;
    ArrayList<Posts> myAnswers;
    LinearLayout ll_parent;
    private int tempNum = 10;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_my_answers, null, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ll_parent = (LinearLayout) getActivity().findViewById(R.id.ll_parent);
        rv_answers = (RecyclerView) getActivity().findViewById(R.id.rv_answers);
        rv_answers.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        rv_answers.setLayoutManager(llm);
        myAnswers = new ArrayList<>();
        adapter = new MyAnswersRecViewAdapter(getActivity(), myAnswers, tempNum, ll_parent, new OnUserActionsListener() {
            @Override
            public void onLike() {

            }

            @Override
            public void onDislike() {

            }

            @Override
            public void onComment() {

            }
        });
        rv_answers.setAdapter(adapter);

    }
}
