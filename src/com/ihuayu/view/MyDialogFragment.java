package com.ihuayu.view;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ihuayu.R;
import com.ihuayu.activity.ResultDetailFragment;
import com.ihuayu.activity.db.entity.DialogKeywords;

public class MyDialogFragment extends DialogFragment {

	public static final int ADD_TO_BOOKMARK = 0;
	public static final int ADD_SUCCESS = 1;
	public static final int DO_SEARCH_DB = 2;
	public static final int SCENARIO_DIALOG = 3;
	private static final String TAG = "iHuayu:MyDialogFragment";
	
	private static FragmentActivity parentActivity = null;
	private static LayoutInflater mInflater = null;
	private static HashMap<com.ihuayu.activity.db.entity.Dialog, List<DialogKeywords>> dialogKeyMap = null;
	private static com.ihuayu.activity.db.entity.Dialog dialogItem = null;
	private static List<DialogKeywords> keyWordList = null;
	
    @SuppressWarnings("unchecked")
	public static MyDialogFragment newInstance(Context context, int dialogType, Object param) {
    	Log.d(TAG, "[newInstance] + Begin");
    	MyDialogFragment frag = new MyDialogFragment();
    	
    	parentActivity = (FragmentActivity) context;
    	mInflater = parentActivity.getLayoutInflater();
    	
    	dialogKeyMap = (HashMap<com.ihuayu.activity.db.entity.Dialog, List<DialogKeywords>>) param;

        Iterator<com.ihuayu.activity.db.entity.Dialog> iterator = dialogKeyMap.keySet().iterator();
        while(iterator.hasNext()) {
        	dialogItem = (com.ihuayu.activity.db.entity.Dialog) iterator.next();
        }
        keyWordList = dialogKeyMap.get(dialogItem);
        Log.d(TAG, "[newInstance] keyWordList size = "+keyWordList.size());
        for (int i = 0; i< keyWordList.size() ; i++) {
        	DialogKeywords words = keyWordList.get(i);
			String enStr = words.getSrc_keyword();
			String pyStr = words.getKeyword_py();
			String cnStr = words.getDest_keyword();
			Log.d(TAG, "[newInstance] enStr = "+enStr);
			Log.d(TAG, "[newInstance] pyStr = "+pyStr);
			Log.d(TAG, "[newInstance] cnStr = "+cnStr);
        }
    	
        Bundle args = new Bundle();
        args.putInt("dialogType", dialogType);
        frag.setArguments(args);
        Log.d(TAG, "[newInstance] + End");
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
        } else if (dialogType == SCENARIO_DIALOG) {
        	View dialogView = mInflater.inflate(R.layout.scenario_fragment_dialog,null);
        	final Dialog dialog = new Dialog(parentActivity,R.style.custom_dialog);
        	dialog.setContentView(dialogView);
        	
        	TextView firstLine = (TextView)dialogView.findViewById(R.id.dialog_text_firstline);
			TextView secondLine = (TextView)dialogView.findViewById(R.id.dialog_text_secondline);
			TextView nameLine = (TextView)dialogView.findViewById(R.id.dialog_text_thridline);
			firstLine.setText(dialogItem.getSentence());
			nameLine.setText(dialogItem.getNarrator());
			secondLine.setText(dialogItem.getSentence_py());
			
			LinearLayout wordslayout = (LinearLayout)dialogView.findViewById(R.id.dialog_words_define_panel);
			for (int i = 0; i< keyWordList.size() ; i++) {
				View wordsView = mInflater.inflate(R.layout.scenario_fragment_dialog_words,null);
				TextView textEn = (TextView)wordsView.findViewById(R.id.dialog_text_word_en);
				TextView textPY = (TextView)wordsView.findViewById(R.id.dialog_text_word_py);
				TextView textCN = (TextView)wordsView.findViewById(R.id.dialog_text_word_cn);
				DialogKeywords words = keyWordList.get(i);
				if (null != words) {
					String enStr = words.getSrc_keyword();
					String pyStr = words.getKeyword_py();
					String cnStr = words.getDest_keyword();
					Log.d(TAG, "[onCreateDialog] enStr = "+enStr);
					Log.d(TAG, "[onCreateDialog] pyStr = "+pyStr);
					Log.d(TAG, "[onCreateDialog] cnStr = "+cnStr);
					textEn.setText(enStr);
					textPY.setText(pyStr);
					textCN.setText(cnStr);
				}
				wordslayout.addView(wordsView, i, new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));
			}
			
			Button btnOK = (Button)dialogView.findViewById(R.id.scenario_fragment_dialog_btn);
			btnOK.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Log.d(TAG, "[onCreateDialog] onClick OK Btn");
					dialog.dismiss();
				}
			});
			return dialog;
        }
		return null;
    }
}
