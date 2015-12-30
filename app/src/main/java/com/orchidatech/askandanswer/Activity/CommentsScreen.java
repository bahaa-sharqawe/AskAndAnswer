package com.orchidatech.askandanswer.Activity;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.Display;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.github.rahatarmanahmed.cpv.CircularProgressView;
import com.orchidatech.askandanswer.Constant.*;
import com.orchidatech.askandanswer.Constant.Enum;
import com.orchidatech.askandanswer.Database.DAO.CommentsDAO;
import com.orchidatech.askandanswer.Database.DAO.User_ActionsDAO;
import com.orchidatech.askandanswer.Database.Model.Comments;
import com.orchidatech.askandanswer.Database.Model.User_Actions;
import com.orchidatech.askandanswer.Fragment.DeleteComment;
import com.orchidatech.askandanswer.Logic.LollipopBitmapMemoryCacheParamsSupplier;
import com.orchidatech.askandanswer.R;
import com.orchidatech.askandanswer.View.Adapter.CommentsRecViewAdapter;
import com.orchidatech.askandanswer.View.Adapter.TimelineRecViewAdapter;
import com.orchidatech.askandanswer.View.Interface.OnCommentAddListener;
import com.orchidatech.askandanswer.View.Interface.OnCommentFetchListener;
import com.orchidatech.askandanswer.View.Interface.OnCommentOptionListener;
import com.orchidatech.askandanswer.View.Interface.OnLastListReachListener;
import com.orchidatech.askandanswer.WebService.WebServiceFunctions;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommentsScreen extends Activity {
    private static final int RESULT_LOAD_IMAGE = 1;
    RecyclerView mRecyclerView;
    CommentsRecViewAdapter adapter;
    long postId;
    public static long last_id_server = 0;

    RelativeLayout rl_error;
    ImageView uncolored_logo;
    TextView tv_error;
    CircularProgressView pb_loading_main;
    private ArrayList<Comments> postComments;
    private LinearLayout rl_parent;
    private List<Comments> comments;


    EditText ed_add_comment;
    ImageView iv_add_comment;
    RelativeLayout rl_comment_photo_preview;
    ImageView iv_delete;
    ImageView iv_camera;
    ImageView iv_comment;
    private String picturePath = "";
    private String image_str = "";
    private long user_id;
    private int numOfFetchedFromServer = 0;
    private SharedPreferences pref;
    private Map<Comments, Integer> data;
    private RelativeLayout rl_send_comment;
    private CircularProgressView pb_add_comment;
    SwipeRefreshLayout swipeRefreshLayout;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        Fresco.initialize(getApplicationContext());
        initFresco();
        setContentView(R.layout.activity_comments_screen);
//        setDragEdge(SwipeBackLayout.DragEdge.BOTTOM);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        postId = getIntent().getLongExtra(ViewPost.POST_ID, -1);
        pref = getSharedPreferences(GNLConstants.SharedPreference.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        user_id = pref.getLong(GNLConstants.SharedPreference.ID_KEY, -1);
        last_id_server = 0;
        rl_parent = (LinearLayout)findViewById(R.id.rl_parent);
        rl_parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSoftKeyboard();
            }
        });
        mRecyclerView = (RecyclerView) findViewById(R.id.rv_comments);
        mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(llm);
        comments = new ArrayList<>();
        data = new HashMap<>();
        adapter = new CommentsRecViewAdapter(this, comments, rl_parent, new OnLastListReachListener() {
            @Override
            public void onReached() {
                loadNewComments();
            }
        }, new OnCommentOptionListener() {

            @Override
            public void onEditComment(long commentId) {
//                editComment(commentId);
            }

            @Override
            public void onDeleteComment(long commentId) {
//                deleteComment(commentId);
            }
        }/*, Enum.COMMENTS_FRAGMENTS.COMMENTS.getNumericType()*/);
        mRecyclerView.setAdapter(adapter);
        rl_error = (RelativeLayout) findViewById(R.id.rl_error);
        uncolored_logo = (ImageView)findViewById(R.id.uncolored_logo);
        tv_error = (TextView) findViewById(R.id.tv_error);
        pb_loading_main = (CircularProgressView) findViewById(R.id.pb_loading_main);

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
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setColorSchemeResources(
                R.color.refresh_progress_1,
                R.color.refresh_progress_2,
                R.color.refresh_progress_3);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshItems();
            }
        });
        loadNewComments();
        postComments = new ArrayList<>(CommentsDAO.getAllCommentsByPost(postId));
        iv_camera = (ImageView) findViewById(R.id.iv_camera);
        iv_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickPhoto();
            }
        });
        rl_comment_photo_preview = (RelativeLayout) findViewById(R.id.rl_comment_photo_preview);
        rl_comment_photo_preview.setVisibility(View.GONE);
        iv_comment = (ImageView) findViewById(R.id.iv_comment);
        iv_delete = (ImageView) findViewById(R.id.iv_delete);
        iv_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                picturePath = "";
                image_str = "";
                rl_comment_photo_preview.setVisibility(View.GONE);
            }
        });
        ed_add_comment = (EditText) findViewById(R.id.ed_add_comment);
        iv_add_comment = (ImageView) findViewById(R.id.iv_add_comment);
        pb_add_comment = (CircularProgressView) findViewById(R.id.pb_add_comment);
        rl_send_comment = (RelativeLayout)findViewById(R.id.rl_send_comment);
        iv_add_comment.setVisibility(View.VISIBLE);
        pb_add_comment.setVisibility(View.INVISIBLE);
        iv_add_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String comment = ed_add_comment.getText().toString().trim();
                if (!TextUtils.isEmpty(comment) || !TextUtils.isEmpty(picturePath)) {
                    iv_add_comment.setVisibility(View.INVISIBLE);
                    pb_add_comment.setVisibility(View.VISIBLE);
                    iv_add_comment.setEnabled(false);
                    iv_camera.setEnabled(false);
                    iv_delete.setEnabled(false);
                    ed_add_comment.setEnabled(false);
                    WebServiceFunctions.addComment(CommentsScreen.this, comment, picturePath, postId, pref.getLong(GNLConstants.SharedPreference.ID_KEY, -1), new OnCommentAddListener() {

                        @Override
                        public void onAdded(com.orchidatech.askandanswer.Database.Model.Comments comment) {
                            iv_add_comment.setVisibility(View.VISIBLE);
                            pb_add_comment.setVisibility(View.INVISIBLE);
                            picturePath = "";
                            image_str = "";
                            rl_comment_photo_preview.setVisibility(View.GONE);
                            pb_loading_main.setVisibility(View.GONE);
                            rl_error.setVisibility(View.GONE);
                            ed_add_comment.setText("");
                            adapter.addComment(comment);
                            iv_add_comment.setEnabled(true);
                            iv_camera.setEnabled(true);
                            ed_add_comment.setEnabled(true);

                        }

                        @Override
                        public void onFail(String error) {
                            iv_add_comment.setVisibility(View.VISIBLE);
                            pb_add_comment.setVisibility(View.INVISIBLE);
                            Toast.makeText(getApplicationContext(), error, Toast.LENGTH_LONG).show();
//                            AppSnackBar.show(rl_parent, error, Color.RED, Color.WHITE);
                            iv_add_comment.setEnabled(true);
                            iv_camera.setEnabled(true);
                            iv_delete.setEnabled(true);
                            ed_add_comment.setEnabled(true);
                        }
                    });
                }
            }
        });

    }

    private void refreshItems() {
//        Log.i("llasdsd", adapter.getNewestCommentId()+"");
        if(adapter.getItemCount()==1){ swipeRefreshLayout.setRefreshing(false);return;}
        WebServiceFunctions.getNewestComments(this, user_id, postId, adapter.getNewestCommentId(), new OnCommentFetchListener() {
            @Override
            public void onSuccess(ArrayList<com.orchidatech.askandanswer.Database.Model.Comments> comments, long last_id) {
                rl_error.setVisibility(View.GONE);
                adapter.addToLastList(comments);
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onFail(String error, int errorCode) {
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    private void pickPhoto() {
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, RESULT_LOAD_IMAGE);
    }

    private void loadNewComments() {
        swipeRefreshLayout.setEnabled(false);
        WebServiceFunctions.getPostComments(this, user_id, postId, GNLConstants.COMMENTS_LIMIT, numOfFetchedFromServer, last_id_server, new OnCommentFetchListener() {
            @Override
            public void onSuccess(ArrayList<com.orchidatech.askandanswer.Database.Model.Comments> comments, long last_id) {
                swipeRefreshLayout.setEnabled(true);
                if (pb_loading_main.getVisibility() == View.VISIBLE) {
                    pb_loading_main.setVisibility(View.GONE);
                }
                last_id_server = last_id_server == 0 ? last_id : last_id_server;
                Log.i("llasdsd", comments.get(0).getServerID() + "");
                numOfFetchedFromServer += comments.size();
                Log.i("fdfcdfdf", last_id_server + "");
//                for (com.orchidatech.askandanswer.Database.Model.Comments comment : comments) {
//                    User_Actions user_actions = User_ActionsDAO.getUserAction(user_id, comment.getServerID());
//                    if (user_actions == null || user_actions.actionType == com.orchidatech.askandanswer.Constant.Enum.USER_ACTIONS.NO_ACTIONS.getNumericType()) {
//                        data.put(comment, Enum.USER_ACTIONS.NO_ACTIONS.getNumericType());
//                    } else if (user_actions.actionType == Enum.USER_ACTIONS.LIKE.getNumericType()) {
//                        data.put(comment, Enum.USER_ACTIONS.LIKE.getNumericType());
//                    } else {
//                        data.put(comment, Enum.USER_ACTIONS.DISLIKE.getNumericType());
//                    }
//                }
                adapter.addFromServer(comments, false);
            }

            @Override
            public void onFail(String error, int errorCode) {
                swipeRefreshLayout.setEnabled(true);
                if (pb_loading_main.getVisibility() == View.VISIBLE) {
                    pb_loading_main.setVisibility(View.GONE);
                    if (errorCode != 404) {//ALL ERRORS EXCEPT NO_COMMENTS
                        if (postComments.size() > 0)
                            getFromLocal();
                        else {
                            rl_error.setVisibility(View.VISIBLE);
                            tv_error.setText(GNLConstants.getStatus(errorCode));
                            rl_error.setEnabled(true);
                        }
                    } else {
                        rl_error.setVisibility(View.VISIBLE);
//                        if (!isDestroyed())
                        tv_error.setText(getString(R.string.no_comments_found));
                        rl_error.setEnabled(true);
                    }
                } else {
                    adapter.addFromServer(null, errorCode != 404 ? true : false);//CONNECTION ERROR
                }
            }
        });
    }

    private void getFromLocal() {
        swipeRefreshLayout.setEnabled(false);
//        for(com.orchidatech.askandanswer.Database.Model.Comments comment : postComments){
//            User_Actions user_actions = User_ActionsDAO.getUserAction(user_id, comment.getServerID());
//            if(user_actions == null || user_actions.actionType == Enum.USER_ACTIONS.NO_ACTIONS.getNumericType()){
//                data.put(comment, Enum.USER_ACTIONS.NO_ACTIONS.getNumericType());
//            }else if(user_actions.actionType == Enum.USER_ACTIONS.LIKE.getNumericType()){
//                data.put(comment, Enum.USER_ACTIONS.LIKE.getNumericType());
//            }else{
//                data.put(comment, Enum.USER_ACTIONS.DISLIKE.getNumericType());
//            }
//        }
        adapter.addFromLocal(postComments);
    }

    private void resizeLogo() {
        Display display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        Point screenSize = new Point(); // used to store screen size
        display.getSize(screenSize); // store size in screenSize
        uncolored_logo.getLayoutParams().height = (int) (screenSize.y * 0.25);
        uncolored_logo.getLayoutParams().width = (int) (screenSize.y * 0.25);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && data != null) {

            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            picturePath = cursor.getString(columnIndex);
            cursor.close();
            final Bitmap bitmap = ShrinkBitmap(picturePath, 300, 300);

            if (bitmap == null) {
                Toast.makeText(getApplicationContext(), getString(R.string.choose_valid_image), Toast.LENGTH_LONG).show();
                return;
            }
            iv_comment.setImageBitmap(bitmap);
            rl_comment_photo_preview.setVisibility(View.VISIBLE);

        /*
         * Convert the image to a string
         * */
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 50, stream); //compress to which format you want.
            byte[] byte_arr = stream.toByteArray();
            image_str = Base64.encodeToString(byte_arr, Base64.DEFAULT);

//            byte[] recovered_byte_arr = Base64.decode(image_str, Base64.DEFAULT);
//           Bitmap bm = BitmapFactory.decodeByteArray(recovered_byte_arr, 0, recovered_byte_arr.length);

        }
    }

    Bitmap ShrinkBitmap(String file, int width, int height) {

        BitmapFactory.Options bmpFactoryOptions = new BitmapFactory.Options();
        bmpFactoryOptions.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeFile(file, bmpFactoryOptions);

        int heightRatio = (int) Math.ceil(bmpFactoryOptions.outHeight / (float) height);
        int widthRatio = (int) Math.ceil(bmpFactoryOptions.outWidth / (float) width);

        if (heightRatio > 1 || widthRatio > 1) {
            if (heightRatio > widthRatio) {
                bmpFactoryOptions.inSampleSize = heightRatio;
            } else {
                bmpFactoryOptions.inSampleSize = widthRatio;
            }
        }

        bmpFactoryOptions.inJustDecodeBounds = false;
        bitmap = BitmapFactory.decodeFile(file, bmpFactoryOptions);
        return bitmap;
    }

    private void hideSoftKeyboard() {
        View view = getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
        final InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

    }
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        long id = item.getItemId();
        final int position = adapter.getPosition();
        if(id == R.id.delete_comment){
            DeleteComment deletePost = new DeleteComment(new DeleteComment.OnDeleteListener(){

                @Override
                public void onDelete() {
                    adapter.performDeleting(position);
                }
            });
            deletePost.show(getFragmentManager(), getString(R.string.delete_comment));
        }
        return super.onContextItemSelected(item);
    }
    private void initFresco() {
        ActivityManager activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        ImagePipelineConfig imagePipelineConfig = ImagePipelineConfig
                .newBuilder(getApplicationContext())
                .setBitmapMemoryCacheParamsSupplier(new LollipopBitmapMemoryCacheParamsSupplier(activityManager))
                .build();

        Fresco.initialize(getApplicationContext(), imagePipelineConfig);
    }
}
