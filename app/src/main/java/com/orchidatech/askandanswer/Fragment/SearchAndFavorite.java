package com.orchidatech.askandanswer.Fragment;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;

import com.orchidatech.askandanswer.Activity.CategoryPosts;
import com.orchidatech.askandanswer.Activity.SplashScreen;
import com.orchidatech.askandanswer.Activity.ViewPost;
import com.orchidatech.askandanswer.Constant.AppSnackBar;
import com.orchidatech.askandanswer.Constant.GNLConstants;
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
    private SharedPreferences pref;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        posts = new ArrayList<>();
        pref = getActivity().getSharedPreferences(GNLConstants.SharedPreference.SHARED_PREF_NAME, Context.MODE_PRIVATE);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_search_favourite, null, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setActionBar();
//        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        ll_parent = (LinearLayout) getActivity().findViewById(R.id.ll_parent);
        ll_parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSoftKeyboard();
            }
        });
        ed_search = (MaterialEditText) getActivity().findViewById(R.id.ed_search);
        rv_posts = (RecyclerView) getActivity().findViewById(R.id.rv_posts);
        rv_posts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSoftKeyboard();
            }
        });
        rv_posts.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        rv_posts.setLayoutManager(llm);
        adapter = new SearchRecViewAdapter(getActivity(), posts, ll_parent);
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
                if (ed_search.getText().toString().length() > 0) {
                    hideSoftKeyboard();
                    performSearching(ed_search.getText().toString());
                }
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
        loadingDialog.setCancelable(false);
        WebServiceFunctions.search(getActivity(), s, pref.getLong(GNLConstants.SharedPreference.ID_KEY, -1), new OnSearchCompleted() {

            @Override
            public void onSuccess(ArrayList<Posts> searchResult) {
                loadingDialog.dismiss();
                posts.addAll(searchResult);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onFail(String error) {
                loadingDialog.dismiss();
                adapter.notifyDataSetChanged();
                AppSnackBar.show(ll_parent, error, Color.RED, Color.WHITE);

            }
        });
    }

    private void setActionBar() {
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Search");
        ((AppCompatActivity) getActivity()).getSupportActionBar().show();
        (getActivity().findViewById(R.id.ed_search)).setVisibility(View.GONE);
        (getActivity().findViewById(R.id.rl_num_notifications)).setVisibility(View.GONE);
    }

    private void hideSoftKeyboard() {
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}
