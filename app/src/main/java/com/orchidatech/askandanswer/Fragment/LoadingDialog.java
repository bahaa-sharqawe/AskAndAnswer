package com.orchidatech.askandanswer.Fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.orchidatech.askandanswer.Activity.ViewPost;
import com.orchidatech.askandanswer.R;
import com.orchidatech.askandanswer.View.Adapter.CommentsRecViewAdapter;

/**
 * Created by Bahaa on 21/11/2015.
 */
public class LoadingDialog extends DialogFragment {
    public static final String DIALOG_TEXT_KEY = "DIALOG_TXT";
    AlertDialog dialog;
    String dialogTxt;
    ProgressBar pb_loading;
    TextView tv_loading;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dialogTxt = getArguments().getString(DIALOG_TEXT_KEY, getString(R.string.loading));
    }
 private LoadingDialog(int i ){

}
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        dialog = builder.create();
        dialog.getWindow().setBackgroundDrawable(getActivity().getResources().getDrawable(R.drawable.loading_fragment_backgnd));
        dialog.setView(getCustomView(), 0, 0, 0, 0);
        dialog.setCanceledOnTouchOutside(true);
        return dialog;
    }
    private View getCustomView() {
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.loading_dialog, null, false);
        pb_loading = (ProgressBar) view.findViewById(R.id.pb_loading);
        pb_loading.getIndeterminateDrawable().setColorFilter(Color.parseColor("#249885"), android.graphics.PorterDuff.Mode.MULTIPLY);
        tv_loading = (TextView) view.findViewById(R.id.tv_loading);
        tv_loading.setText(dialogTxt);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}
