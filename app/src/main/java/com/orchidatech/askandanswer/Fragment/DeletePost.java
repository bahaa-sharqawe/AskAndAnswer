package com.orchidatech.askandanswer.Fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.orchidatech.askandanswer.Activity.ViewPost;
import com.orchidatech.askandanswer.R;
import com.orchidatech.askandanswer.View.Utils.FontManager;

/**
 * Created by Bahaa on 5/11/2015.
 */
public class DeletePost extends DialogFragment {
    AlertDialog dialog;
    TextView tv_confirm;
    TextView tv_cancel;
    TextView tv_delPostTitle;
    TextView tv_delPostAttention;
    long postId;
    private OnDeleteListener listener;
    private FontManager fontManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        postId = getArguments().getLong(ViewPost.POST_ID, -1);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if(activity != null && activity instanceof OnDeleteListener){
            listener = (OnDeleteListener) activity;
        }
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
        fontManager = FontManager.getInstance(getActivity().getAssets());

        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.fragment_delete_post, null, false);
        tv_delPostAttention = (TextView) view.findViewById(R.id.tv_delPostAttention);
        tv_delPostAttention.setTypeface(fontManager.getFont(FontManager.ROBOTO_LIGHT));

        tv_delPostTitle = (TextView) view.findViewById(R.id.tv_delPostTitle);
        tv_delPostTitle.setTypeface(fontManager.getFont(FontManager.ROBOTO_MEDIUM));

        tv_confirm = (TextView) view.findViewById(R.id.tv_confirm);
        tv_confirm.setTypeface(fontManager.getFont(FontManager.ROBOTO_LIGHT));

        tv_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //delete post here then
//                performDeleting();
                listener.onDelete();
                dialog.dismiss();
            }
        });
        tv_cancel = (TextView) view.findViewById(R.id.tv_cancel);
        tv_cancel.setTypeface(fontManager.getFont(FontManager.ROBOTO_LIGHT));

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
