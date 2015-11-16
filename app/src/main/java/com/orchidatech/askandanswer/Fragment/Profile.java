package com.orchidatech.askandanswer.Fragment;

import android.app.Fragment;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;

//import com.github.gorbin.asne.googleplus.GooglePlusSocialNetwork;
import com.orchidatech.askandanswer.Activity.EditProfile;
import com.orchidatech.askandanswer.Activity.MainScreen;
import com.orchidatech.askandanswer.Activity.ViewPost;
import com.orchidatech.askandanswer.Entity.Post;
import com.orchidatech.askandanswer.R;
import com.orchidatech.askandanswer.View.Adapter.ProfileRecViewAdapter;
import com.orchidatech.askandanswer.View.Adapter.SearchRecViewAdapter;
import com.orchidatech.askandanswer.View.Interface.OnCategoryClickListener;
import com.orchidatech.askandanswer.View.Interface.OnPostEventListener;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Bahaa on 7/11/2015.
 */
public class Profile extends Fragment {
    RecyclerView rv_posts;
    ProfileRecViewAdapter adapter;
    ArrayList<Post> posts;
    RatingBar rating_person;
    CircleImageView iv_profile;
    FloatingActionButton fab_edit_profile;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, null, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        fab_edit_profile = (FloatingActionButton) getActivity().findViewById(R.id.fab_edit_profile);
        fab_edit_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), EditProfile.class));
            }
        });
        rating_person = (RatingBar) getActivity().findViewById(R.id.rating_person);
        iv_profile = (CircleImageView) getActivity().findViewById(R.id.iv_profile);
        iv_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), EditProfile.class));
            }
        });
        rv_posts = (RecyclerView) getActivity().findViewById(R.id.rv_posts);
        rv_posts.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        rv_posts.setLayoutManager(llm);
        posts = new ArrayList<>();
        adapter = new ProfileRecViewAdapter(getActivity(), posts, 10, new OnPostEventListener() {
            @Override
            public void onEditPost() {
                startActivity(new Intent(getActivity(), ViewPost.class));
            }

            @Override
            public void onSharePost() {

            }

            @Override
            public void onCommentPost() {
                new Comments().show(getFragmentManager(), "comments");
            }

            @Override
            public void onFavoritePost() {
            }
        }, new OnCategoryClickListener() {
            @Override
            public void onClick(long userId, long categoryId) {
                ((MainScreen)getActivity()).startEvent(2);
            }
        });
        rv_posts.setAdapter(adapter);
    }
}
