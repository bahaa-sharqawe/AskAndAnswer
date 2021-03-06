package com.orchidatech.askandanswer.Fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.orchidatech.askandanswer.R;
import com.orchidatech.askandanswer.View.Interface.OnDeleteCommentListener;
import com.orchidatech.askandanswer.View.Utils.FontManager;
import com.orchidatech.askandanswer.WebService.WebServiceFunctions;

/**
 * Created by Bahaa on 2/12/2015.
 */
public class DeleteComment extends DialogFragment {
    public static final java.lang.String COMMENT_ID = "COMMENT_ID";
    public static final String COMMENT_POS = "COMMENT_POSITION";
    AlertDialog dialog;
    TextView tv_confirm;
    TextView tv_cancel;
    TextView tv_delCommentTitle;
    TextView tv_delCommtAttention;
    long commentId;
    int commentPos;
    private OnDeleteListener listener;
    private FontManager mFontManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        commentId = getArguments().getLong(COMMENT_ID, -1);
//        commentPos = getArguments().getInt(COMMENT_POS, -1);

    }
    public DeleteComment(OnDeleteListener listener){
        this.listener = listener;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        dialog = builder.create();
        dialog.setView(getCustomView(), 0, 0, 0, 0);
        dialog.setCanceledOnTouchOutside(false);
        return dialog;
    }

    private View getCustomView() {
        mFontManager = FontManager.getInstance(getActivity().getAssets());

        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.fragment_delete_comment, null, false);
        tv_confirm = (TextView) view.findViewById(R.id.tv_confirm);
        tv_confirm.setTypeface(mFontManager.getFont(FontManager.ROBOTO_LIGHT));

        tv_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //delete comment here then
//                performDeleting();
                listener.onDelete();
                dialog.dismiss();
            }
        });
        tv_cancel = (TextView) view.findViewById(R.id.tv_cancel);
        tv_cancel.setTypeface(mFontManager.getFont(FontManager.ROBOTO_LIGHT));

        tv_delCommtAttention = (TextView) view.findViewById(R.id.tv_delCommtAttention);
        tv_delCommtAttention.setTypeface(mFontManager.getFont(FontManager.ROBOTO_LIGHT));

        tv_delCommentTitle = (TextView) view.findViewById(R.id.tv_delCommentTitle);
        tv_delCommentTitle.setTypeface(mFontManager.getFont(FontManager.ROBOTO_MEDIUM));

        tv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        return view;
    }

    public interface OnDeleteListener{
        void onDelete();
    }
}
