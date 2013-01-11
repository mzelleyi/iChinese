package com.ihuayu.view;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;

import com.ihuayu.R;
import com.ihuayu.activity.ResultDetailFragment;

public class MyDialogFragment extends DialogFragment {

	public static final int ADD_TO_BOOKMARK = 0;
	public static final int ADD_SUCCESS = 1;
	public static final int DO_SEARCH_DB = 2;
	
	private static FragmentActivity parentActivity = null;
	
    public static MyDialogFragment newInstance(Context context, int dialogType) {
    	MyDialogFragment frag = new MyDialogFragment();
    	
    	parentActivity = (FragmentActivity) context;
        Bundle args = new Bundle();
        args.putInt("dialogType", dialogType);
        frag.setArguments(args);
        return frag;
    }
    
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        int dialogType = getArguments().getInt("dialogType");
        if (dialogType == ADD_TO_BOOKMARK) {
        	return new AlertDialog.Builder(parentActivity)
            .setIcon(android.R.drawable.ic_dialog_alert)
            .setTitle(R.string.dialog_msg_add_to_bookmark)
            .setPositiveButton(android.R.string.ok,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    	ResultDetailFragment.doPositiveClick();
                    }
                }
            )
            .setNegativeButton(android.R.string.cancel,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    	ResultDetailFragment.doNegativeClick();
                    }
                }
            )
            .create();
        } else if (dialogType == ADD_SUCCESS) {
        	return new AlertDialog.Builder(parentActivity)
            .setIcon(android.R.drawable.ic_dialog_alert)
            .setTitle(R.string.dialog_msg_add_success)
            .setPositiveButton(android.R.string.ok,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    	ResultDetailFragment.doPositiveClick();
                    }
                }
            )
            .create();
        } else if (dialogType == DO_SEARCH_DB) {
        	ProgressDialog dialog = new ProgressDialog(parentActivity);
            dialog.setMessage("Please wait while searching...");
            dialog.setIndeterminate(true);
            dialog.setCancelable(true);
            return dialog;
        }
		return null;
    }
}
