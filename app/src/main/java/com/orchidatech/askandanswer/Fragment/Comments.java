package com.orchidatech.askandanswer.Fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Base64;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.orchidatech.askandanswer.Activity.SplashScreen;
import com.orchidatech.askandanswer.Activity.ViewPost;
import com.orchidatech.askandanswer.Constant.AppSnackBar;
import com.orchidatech.askandanswer.Constant.GNLConstants;
import com.orchidatech.askandanswer.Database.DAO.CommentsDAO;
import com.orchidatech.askandanswer.R;
import com.orchidatech.askandanswer.View.Adapter.CommentsRecViewAdapter;
import com.orchidatech.askandanswer.View.Interface.OnCommentAddListener;
import com.orchidatech.askandanswer.View.Interface.OnCommentFetchListener;
import com.orchidatech.askandanswer.View.Interface.OnLastListReachListener;
import com.orchidatech.askandanswer.View.Interface.OnUserActionsListener;
import com.orchidatech.askandanswer.WebService.WebServiceFunctions;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

/**
 * Created by Bahaa on 15/11/2015.
 */
public class Comments extends DialogFragment {
    private static final int RESULT_LOAD_IMAGE = 1;

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
    private ArrayList<com.orchidatech.askandanswer.Database.Model.Comments> comments;


    EditText ed_add_comment;
    ImageView iv_add_comment;
    RelativeLayout rl_comment_photo_preview;
    ImageView iv_delete;
    ImageView iv_camera;
    ImageView iv_comment;
    private String picturePath;
    private String image_str;
    private long user_id;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        postId = getArguments().getLong(ViewPost.POST_ID);
        user_id = SplashScreen.pref.getLong(GNLConstants.SharedPreference.ID_KEY, -1);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
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
                picturePath = null;
                image_str = null;
                rl_comment_photo_preview.setVisibility(View.GONE);
            }
        });
        ed_add_comment = (EditText) view.findViewById(R.id.ed_add_comment);
        iv_add_comment = (ImageView) view.findViewById(R.id.iv_add_comment);
        iv_add_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String comment = ed_add_comment.getText().toString().trim();
                if(!TextUtils.isEmpty(comment)){
                    iv_add_comment.setEnabled(false);
                    iv_camera.setEnabled(false);
                    WebServiceFunctions.addComment(getActivity(), comment, picturePath, postId, SplashScreen.pref.getLong(GNLConstants.SharedPreference.ID_KEY, -1), new OnCommentAddListener(){

                        @Override
                        public void onAdded(com.orchidatech.askandanswer.Database.Model.Comments comment) {
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

    private void pickPhoto() {
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, RESULT_LOAD_IMAGE);
    }

    private void loadNewComments() {
        WebServiceFunctions.getPostComments(getActivity(), user_id, postId, GNLConstants.COMMENTS_LIMIT, adapter.getItemCount() - 1, last_id_server, new OnCommentFetchListener() {
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

}
