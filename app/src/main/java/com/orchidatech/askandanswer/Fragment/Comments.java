package com.orchidatech.askandanswer.Fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.orchidatech.askandanswer.Activity.SplashScreen;
import com.orchidatech.askandanswer.Activity.ViewPost;
import com.orchidatech.askandanswer.Constant.*;
import com.orchidatech.askandanswer.Constant.Enum;
import com.orchidatech.askandanswer.Database.DAO.CommentsDAO;
import com.orchidatech.askandanswer.R;
import com.orchidatech.askandanswer.View.Adapter.CommentsRecViewAdapter;
import com.orchidatech.askandanswer.View.Interface.OnCommentAddListener;
import com.orchidatech.askandanswer.View.Interface.OnCommentFetchListener;
import com.orchidatech.askandanswer.View.Interface.OnCommentOptionListener;
import com.orchidatech.askandanswer.View.Interface.OnDeleteCommentListener;
import com.orchidatech.askandanswer.View.Interface.OnLastListReachListener;
import com.orchidatech.askandanswer.WebService.WebServiceFunctions;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Bahaa on 15/11/2015.
 */
public class Comments extends DialogFragment {
    private static final int RESULT_LOAD_IMAGE = 1;
    AlertDialog dialog;
    RecyclerView mRecyclerView;
    CommentsRecViewAdapter adapter;
    long postId;
    public static long last_id_server = 0;

    RelativeLayout rl_error;
    ImageView uncolored_logo;
    TextView tv_error;
    ProgressBar pb_loading_main;
    private ArrayList<com.orchidatech.askandanswer.Database.Model.Comments> postComments;
    private LinearLayout rl_parent;
    private List<com.orchidatech.askandanswer.Database.Model.Comments> comments;


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


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        postId = getArguments().getLong(ViewPost.POST_ID);
        pref = getActivity().getSharedPreferences(GNLConstants.SharedPreference.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        user_id = pref.getLong(GNLConstants.SharedPreference.ID_KEY, -1);
        last_id_server = 0;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.MyCustomTheme);
        dialog = builder.create();
        dialog.getWindow().setBackgroundDrawable(getActivity().getResources().getDrawable(R.drawable.comments_fragment_backgnd));
        dialog.setView(getCustomView(), 0, 0, 0, 0);
        return dialog;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    private View getCustomView() {
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.fragment_comments, null, false);
        rl_parent = (LinearLayout) view.findViewById(R.id.rl_parent);
        rl_parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSoftKeyboard();
            }
        });
        mRecyclerView = (RecyclerView) view.findViewById(R.id.rv_comments);
        mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(llm);
        comments = new ArrayList<>();
        adapter = new CommentsRecViewAdapter(getActivity(), comments, rl_parent, new OnLastListReachListener() {
            @Override
            public void onReached() {
                loadNewComments();
            }
        }, new OnCommentOptionListener() {

            @Override
            public void onEditComment(long commentId) {
                editComment(commentId);
            }

            @Override
            public void onDeleteComment(long commentId) {
                deleteComment(commentId);
            }
        }, Enum.COMMENTS_FRAGMENTS.COMMENTS.getNumericType());
        mRecyclerView.setAdapter(adapter);
        rl_error = (RelativeLayout) view.findViewById(R.id.rl_error);
        uncolored_logo = (ImageView) view.findViewById(R.id.uncolored_logo);
        tv_error = (TextView) view.findViewById(R.id.tv_error);
        pb_loading_main = (ProgressBar) view.findViewById(R.id.pb_loading_main);
        pb_loading_main.getIndeterminateDrawable().setColorFilter(Color.parseColor("#249885"), android.graphics.PorterDuff.Mode.MULTIPLY);

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
        iv_camera = (ImageView) view.findViewById(R.id.iv_camera);
        iv_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickPhoto();
            }
        });
        rl_comment_photo_preview = (RelativeLayout) view.findViewById(R.id.rl_comment_photo_preview);
        rl_comment_photo_preview.setVisibility(View.GONE);
        iv_comment = (ImageView) view.findViewById(R.id.iv_comment);
        iv_delete = (ImageView) view.findViewById(R.id.iv_delete);
        iv_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                picturePath = "";
                image_str = "";
                rl_comment_photo_preview.setVisibility(View.GONE);
            }
        });
        ed_add_comment = (EditText) view.findViewById(R.id.ed_add_comment);
        iv_add_comment = (ImageView) view.findViewById(R.id.iv_add_comment);
        iv_add_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String comment = ed_add_comment.getText().toString().trim();
                if (!TextUtils.isEmpty(comment)) {
                    iv_add_comment.setEnabled(false);
                    iv_camera.setEnabled(false);
                    WebServiceFunctions.addComment(getActivity(), comment, picturePath, postId, pref.getLong(GNLConstants.SharedPreference.ID_KEY, -1), new OnCommentAddListener() {

                        @Override
                        public void onAdded(com.orchidatech.askandanswer.Database.Model.Comments comment) {
                            picturePath = "";
                            image_str = "";
                            rl_comment_photo_preview.setVisibility(View.GONE);
                            rl_error.setVisibility(View.GONE);
                            ed_add_comment.setText("");
                            adapter.addComment(comment);
                            iv_add_comment.setEnabled(true);
                            iv_camera.setEnabled(true);
                        }

                        @Override
                        public void onFail(String error) {
                            AppSnackBar.show(rl_parent, error, Color.RED, Color.WHITE);
                            iv_add_comment.setEnabled(true);
                            iv_camera.setEnabled(true);
                        }
                    });
                }
            }
        });
        return view;
    }

    private void deleteComment(long commentId) {
//        DeleteComment deletePost = new DeleteComment();
//        Bundle args = new Bundle();
//        args.putLong(DeleteComment.COMMENT_ID, commentId);
//        deletePost.setArguments(args);
//        deletePost.show(getFragmentManager(), getString(R.string.delete_comment));

    }

    private void editComment(long commentId) {
//        com.orchidatech.askandanswer.Database.Model.Comments comment = CommentsDAO.getComment(commentId);
//        ed_add_comment.setText(comment.getText());
//        if (!TextUtils.isEmpty(comment.getImage())) {
//            rl_comment_photo_preview.setVisibility(View.VISIBLE);
//            iv_comment.setVisibility(View.VISIBLE);
//            iv_comment.setImageDrawable(commentDrawable);
//        }

    }

    private void pickPhoto() {
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, RESULT_LOAD_IMAGE);
    }

    private void loadNewComments() {
        WebServiceFunctions.getPostComments(getActivity(), user_id, postId, GNLConstants.COMMENTS_LIMIT, numOfFetchedFromServer, last_id_server, new OnCommentFetchListener() {
            @Override
            public void onSuccess(ArrayList<com.orchidatech.askandanswer.Database.Model.Comments> comments, long last_id) {
                if (pb_loading_main.getVisibility() == View.VISIBLE) {
                    pb_loading_main.setVisibility(View.GONE);
                }
                last_id_server = last_id_server == 0 ? last_id : last_id_server;

                numOfFetchedFromServer += comments.size();
                Log.i("fdfcdfdf", last_id_server+"");
                adapter.addFromServer(comments, false);
            }

            @Override
            public void onFail(String error, int errorCode) {
                if (pb_loading_main.getVisibility() == View.VISIBLE) {
                    pb_loading_main.setVisibility(View.GONE);
                    if (errorCode != 404) {//ALL ERRORS EXCEPT NO_POSTS
                        if (postComments.size() > 0)
                            getFromLocal();
                        else {
                            rl_error.setVisibility(View.VISIBLE);
                            tv_error.setText(GNLConstants.getStatus(errorCode));
                            rl_error.setEnabled(true);
                        }
                    } else {
                        rl_error.setVisibility(View.VISIBLE);
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
        adapter.addFromLocal(postComments);
    }

    private void resizeLogo() {
        Display display = ((WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        Point screenSize = new Point(); // used to store screen size
        display.getSize(screenSize); // store size in screenSize
        uncolored_logo.getLayoutParams().height = (int) (screenSize.y * 0.25);
        uncolored_logo.getLayoutParams().width = (int) (screenSize.y * 0.25);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == getActivity().RESULT_OK && data != null) {

            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = getActivity().getContentResolver().query(selectedImage, filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            picturePath = cursor.getString(columnIndex);
            cursor.close();
            final Bitmap bitmap = ShrinkBitmap(picturePath, 300, 300);

            if (bitmap == null) {
                Toast.makeText(getActivity().getApplicationContext(), getString(R.string.choose_valid_image), Toast.LENGTH_LONG).show();
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
//        View view = getActivity().getCurrentFocus();
//        if (view != null) {
//            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
//            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
//        }
//        final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
//        imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);

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
            deletePost.show(getActivity().getFragmentManager(), getActivity().getString(R.string.delete_comment));
        }
        return super.onContextItemSelected(item);
    }

}
