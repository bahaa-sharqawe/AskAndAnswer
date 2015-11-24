package com.orchidatech.askandanswer.Fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.orchidatech.askandanswer.Database.Model.Posts;
import com.orchidatech.askandanswer.R;
import com.orchidatech.askandanswer.View.Adapter.SearchRecViewAdapter;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.ArrayList;

/**
 * Created by Bahaa on 7/11/2015.
 */
public class SearchAndFavorite extends Fragment {
    RecyclerView rv_posts;
    SearchRecViewAdapter adapter;
    ArrayList<Posts> posts;
    MaterialEditText ed_search;
    LinearLayout ll_parent;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_search_favourite, null, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
//        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        ll_parent = (LinearLayout) getActivity().findViewById(R.id.ll_parent);
        ed_search = (MaterialEditText) getActivity().findViewById(R.id.ed_search);
        rv_posts = (RecyclerView) getActivity().findViewById(R.id.rv_posts);
        rv_posts.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        rv_posts.setLayoutManager(llm);
        posts = new ArrayList<>();
        adapter = new SearchRecViewAdapter(getActivity(), posts, 20, ll_parent);
        rv_posts.setAdapter(adapter);

    }
//    private void setCustomActionBar() {
//        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
//        toolbar.setTitle("Search And Favorite");
//        toolbar.setTitleTextColor(Color.parseColor("#fff"));
//        toolbar.setNavigationIcon(R.drawable.ic_search);
//
//        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
//        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(true);
//
//    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.search_and_questions_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.search) {//search icon
            if (ed_search.getVisibility() == View.GONE) {
                ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("");
                ed_search.setVisibility(View.VISIBLE);
                return true;
            } else {
                //perform searching
                return true;
            }
        }

        return false;
    }
}
