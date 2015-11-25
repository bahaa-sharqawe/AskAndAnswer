package com.orchidatech.askandanswer.Fragment;

import android.app.Fragment;
import android.content.Intent;
import android.graphics.Color;
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

import com.orchidatech.askandanswer.Activity.CategoryPosts;
import com.orchidatech.askandanswer.Activity.ViewPost;
import com.orchidatech.askandanswer.Constant.AppSnackBar;
import com.orchidatech.askandanswer.Database.Model.Posts;
import com.orchidatech.askandanswer.R;
import com.orchidatech.askandanswer.View.Adapter.SearchRecViewAdapter;
import com.orchidatech.askandanswer.View.Interface.OnPostEventListener;
import com.orchidatech.askandanswer.View.Interface.OnPostFavoriteListener;
import com.orchidatech.askandanswer.View.Interface.OnSearchCompleted;
import com.orchidatech.askandanswer.WebService.WebServiceFunctions;
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
        adapter = new SearchRecViewAdapter(getActivity(), posts, ll_parent, new OnPostEventListener() {
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
            public void onFavoritePost(int position, long pid, long uid) {
                WebServiceFunctions.addPostFavorite(getActivity(), pid, uid, new OnPostFavoriteListener() {

                    @Override
                    public void onSuccess() {
                        AppSnackBar.show(ll_parent, getString(R.string.post_favorite_added), getResources().getColor(R.color.colorPrimary), Color.WHITE);
                    }

                    @Override
                    public void onFail(String error) {
                        AppSnackBar.show(ll_parent, error, Color.RED, Color.WHITE);

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
        });
        rv_posts.setAdapter(adapter);

    }

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
                    posts.clear();
                    performSearching(ed_search.getText().toString());
                return true;
            }
        }

        return false;
    }

    private void performSearching(String s) {
        final LoadingDialog loadingDialog = new LoadingDialog();
        Bundle args = new Bundle();
        args.putString(LoadingDialog.DIALOG_TEXT_KEY, getString(R.string.search_questions));
        loadingDialog.setArguments(args);
        loadingDialog.show(getFragmentManager(), "search");
        WebServiceFunctions.search(getActivity(), s, new OnSearchCompleted(){

            @Override
            public void onSuccess(ArrayList<Posts> searchResult) {
                if(loadingDialog.isVisible())
                    loadingDialog.dismiss();
                posts.addAll(searchResult);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onFail(String error) {
                if(loadingDialog.isVisible())
                    loadingDialog.dismiss();
                AppSnackBar.show(ll_parent, error, Color.RED, Color.WHITE);

            }
        });
    }
}
