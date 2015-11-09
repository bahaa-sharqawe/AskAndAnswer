package com.orchidatech.askandanswer.Fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.orchidatech.askandanswer.Entity.Post;
import com.orchidatech.askandanswer.R;
import com.orchidatech.askandanswer.View.Adapter.SearchRecViewAdapter;

import java.util.ArrayList;

/**
 * Created by Bahaa on 7/11/2015.
 */
public class MyPosts extends Fragment {
    RecyclerView rv_posts;
    SearchRecViewAdapter adapter;
    ArrayList<Post> myPosts;
    FloatingActionButton fab_add_post;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_my_posts, null, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        fab_add_post = (FloatingActionButton) getActivity().findViewById(R.id.fab_add_post);
        fab_add_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        rv_posts = (RecyclerView) getActivity().findViewById(R.id.rv_posts);
        rv_posts.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        rv_posts.setLayoutManager(llm);
        myPosts = new ArrayList<>();
        adapter = new SearchRecViewAdapter(getActivity(), myPosts);
        rv_posts.setAdapter(adapter);

    }
}
