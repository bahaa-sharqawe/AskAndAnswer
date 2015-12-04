package com.orchidatech.askandanswer.Activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.orchidatech.askandanswer.Constant.AppSnackBar;
import com.orchidatech.askandanswer.Database.DAO.PostsDAO;
import com.orchidatech.askandanswer.Database.Model.Posts;
import com.orchidatech.askandanswer.Fragment.DeletePost;
import com.orchidatech.askandanswer.Fragment.LoadingDialog;
import com.orchidatech.askandanswer.R;
import com.orchidatech.askandanswer.View.Interface.OnPostDeletedListener;
import com.orchidatech.askandanswer.WebService.WebServiceFunctions;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

public class ViewPost extends AppCompatActivity implements DeletePost.OnDeleteListener{
    public static final String POST_ID = "POST_ID";
    long postId;
    Posts post;
    ImageView iv_post;
    TextView tv_post;
    LinearLayout ll_parent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_post);
        setCustomActionBar();
        postId = getIntent().getLongExtra(POST_ID, -1);
//        if(getIntent().getByteArrayExtra("picture") != null){
//            byte[] b = getIntent().getByteArrayExtra("picture");
//            Bitmap bmp = BitmapFactory.decodeByteArray(b, 0, b.length);
//            iv_post.setImageBitmap(bmp);
//        }
        post = PostsDAO.getPost(postId);
        if(post != null) {
            iv_post = (ImageView) this.findViewById(R.id.iv_post);
            tv_post = (TextView) this.findViewById(R.id.tv_post);
            tv_post.setText(post.getText());
            if(!TextUtils.isEmpty(post.getImage()))
                   Picasso.with(this).load(Uri.parse(post.getImage())).into(iv_post, new Callback() {
                       @Override
                       public void onSuccess() {

                       }

                       @Override
                       public void onError() {
                           iv_post.setVisibility(View.INVISIBLE);
                       }
                   });
            else
                iv_post.setVisibility(View.INVISIBLE);
        }
        ll_parent = (LinearLayout) this.findViewById(R.id.ll_parent);
    }

    private void setCustomActionBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.android));
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.view_post_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.delete_post) {
            deletePost();
            return true;
        } else if (id == R.id.edit_post) {
            edit_post();
            return true;
        }
        return false;
    }

    private void deletePost() {
        DeletePost deletePost = new DeletePost();
        Bundle args = new Bundle();
        args.putLong(POST_ID, postId);
        deletePost.setArguments(args);
        deletePost.show(getFragmentManager(), getString(R.string.delete_post));
    }
    private void edit_post() {
        Intent intent = new Intent(this, AddEditPost.class);
        intent.putExtra(POST_ID, postId);
        startActivity(intent);
    }
    private void performDeleting() {
        final LoadingDialog loadingDialog = new LoadingDialog();
        Bundle args = new Bundle();
        args.putString(LoadingDialog.DIALOG_TEXT_KEY, getString(R.string.delteing));
        loadingDialog.setArguments(args);
        loadingDialog.setCancelable(false);
        loadingDialog.show(getFragmentManager(), "deleting");
        WebServiceFunctions.deletePost(this, postId, new OnPostDeletedListener() {

            @Override
            public void onDeleted() {
                    loadingDialog.dismiss();
                AppSnackBar.show(ll_parent, getResources().getString(R.string.deleted), getResources().getColor(R.color.colorPrimary), Color.WHITE);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        finish();
                    }
                }, 3000);
            }

            @Override
            public void onFail(String error) {
                    loadingDialog.dismiss();
                AppSnackBar.show(ll_parent, error, Color.RED, Color.WHITE);
            }
        });
    }

    @Override
    public void onDelete() {
        performDeleting();
    }

}
