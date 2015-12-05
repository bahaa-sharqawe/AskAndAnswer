package com.orchidatech.askandanswer.Fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.orchidatech.askandanswer.Constant.GNLConstants;
import com.orchidatech.askandanswer.R;
import com.orchidatech.askandanswer.View.Interface.OnSendMessageListener;
import com.orchidatech.askandanswer.View.Interface.OnUserActionsListener;
import com.orchidatech.askandanswer.View.Utils.Validator;
import com.orchidatech.askandanswer.WebService.WebServiceFunctions;

/**
 * Created by Bahaa on 5/11/2015.
 */
public class ContactUs extends DialogFragment {
    AlertDialog dialog;
    TextView tv_cancel;
    TextView tv_send;
    EditText ed_message;
    LinearLayout ll_parent;
    private long user_id;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        dialog = builder.create();
        dialog.setView(getCustomView(), 0, 0, 0, 0);
        dialog.setCanceledOnTouchOutside(false);
        return dialog;
    }

    private View getCustomView() {
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.fragment_contactus, null, false);
        ll_parent = (LinearLayout) view.findViewById(R.id.ll_parent);
        ll_parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSoftKeyboard();
            }
        });
        user_id = getActivity().getSharedPreferences(GNLConstants.SharedPreference.SHARED_PREF_NAME, Context.MODE_PRIVATE)
                .getLong(GNLConstants.SharedPreference.ID_KEY, -1);
        ed_message = (EditText) view.findViewById(R.id.ed_message);
        tv_send = (TextView) view.findViewById(R.id.tv_send);
        tv_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //send message to server then
                hideSoftKeyboard();
                String message = ed_message.getText().toString().trim();
                if(!TextUtils.isEmpty(message)){
                    message = Validator.getInstance().getSafeText(message);
                    if(!TextUtils.isEmpty(message)){
                        final LoadingDialog loadingDialog = new LoadingDialog();
                        Bundle args = new Bundle();
                        args.putString(LoadingDialog.DIALOG_TEXT_KEY, getString(R.string.sending));
                        loadingDialog.setArguments(args);
                        loadingDialog.setCancelable(false);
                        loadingDialog.show(getFragmentManager(), "sending");
                        WebServiceFunctions.sendMessage(getActivity(), user_id, message, new OnSendMessageListener(){

                            @Override
                            public void onSuccess() {
                                ed_message.setText("");
                                Toast.makeText(getActivity(), getString(R.string.message_sent), Toast.LENGTH_LONG).show();
                                loadingDialog.dismiss();
                                dialog.dismiss();

                            }

                            @Override
                            public void onFail(String error) {
                                loadingDialog.dismiss();
                                Toast.makeText(getActivity(), error, Toast.LENGTH_LONG).show();
                            }
                        });
                    }else{
                        ed_message.setError(getString(R.string.BR_GNL_007));
                    }
                }
            }
        });
        tv_cancel = (TextView) view.findViewById(R.id.tv_cancel);
        tv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSoftKeyboard();
                dialog.dismiss();
            }
        });
        return view;
    }
    private void hideSoftKeyboard() {
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

}
