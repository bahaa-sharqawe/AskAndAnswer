package com.orchidatech.askandanswer.Fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.orchidatech.askandanswer.R;
import com.orchidatech.askandanswer.View.Utils.FontManager;

/**
 * Created by Bahaa on 12/12/2015.
 */
public class CommentOptionsDialog extends DialogFragment {

    private AlertDialog dialog;
    private TextView tv_header;
    private TextView tv_delete;
    private OnCommentOptionsListener listener;
    private FontManager fontManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fontManager = FontManager.getInstance(getActivity().getAssets());
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        dialog = builder.create();
//        dialog.getWindow().setBackgroundDrawable(getActivity().getResources().getDrawable(R.drawable.comments_fragment_backgnd));
        dialog.setView(getCustomView(), 0, 0, 0, 0);
        return dialog;
    }
    public CommentOptionsDialog(OnCommentOptionsListener listener){
        this.listener = listener;
    }
    private View getCustomView() {
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.comment_options, null, false);
        tv_delete = (TextView) view.findViewById(R.id.tv_delete);
        tv_delete.setTypeface(fontManager.getFont(FontManager.ROBOTO_LIGHT));

        tv_header = (TextView) view.findViewById(R.id.tv_header);
        tv_header.setTypeface(fontManager.getFont(FontManager.ROBOTO_MEDIUM));

        tv_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onDelete();
                dialog.dismiss();
            }
        });
        return view;
    }

    public interface OnCommentOptionsListener{
        void onDelete();
    }
    }
