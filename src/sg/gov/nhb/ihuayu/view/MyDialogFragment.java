package sg.gov.nhb.ihuayu.view;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import sg.gov.nhb.ihuayu.activity.MainActivity;
import sg.gov.nhb.ihuayu.activity.BookmarkDetailFragment;
import sg.gov.nhb.ihuayu.activity.SearchDetailFragment;
import sg.gov.nhb.ihuayu.activity.db.entity.DialogKeywords;
import sg.gov.nhb.ihuayu.activity.db.entity.ScenarioDialog;

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
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import sg.gov.nhb.ihuayu.R;

public class MyDialogFragment extends DialogFragment {

	public static final int											ADD_TO_BOOKMARK			= 0;
	public static final int											ADD_RESULT				= 1;
	public static final int											REMOVE_FROM_BOOKMARK	= 2;
	public static final int											REMOVE_RESULT			= 3;

	public static final int											DO_SEARCH_DB			= 4;
	public static final int											SCENARIO_DIALOG			= 5;
	public static final int											DIALOG_DOWNLOAD			= 6;

	public static final int											CHECKING_UPDATE			= 7;
	public static final int											UPDATE_COUNT			= 8;

	public static final int											NO_INTERNET_CONNETION	= 10;

	private static final String										TAG						= "iHuayu:MyDialogFragment";

	private static FragmentActivity									parentActivity			= null;
	private static LayoutInflater									mInflater				= null;
	private static HashMap<ScenarioDialog, List<DialogKeywords>>	dialogKeyMap			= null;
	private static ScenarioDialog									dialogItem				= null;
	private static List<DialogKeywords>								keyWordList				= null;

    public static MyDialogFragment newInstance(Context context, int dialogType) {
    	return newInstance(context, dialogType, false, null, 0, false);
    }
    
    public static MyDialogFragment newInstance(Context context, int dialogType, Object param) {
    	return newInstance(context, dialogType, false, param, 0, false);
    }
    
    public static MyDialogFragment newInstance(Context context, int dialogType, boolean succuss, Object param) {
    	return newInstance(context, dialogType, succuss, param, 0, false);
    }
    
    public static MyDialogFragment newInstance(Context context, int dialogType, boolean succuss, boolean isSearchDetail) {
    	return newInstance(context, dialogType, succuss, null, 0, isSearchDetail);
    }
    
    public static MyDialogFragment newInstance(Context context, int dialogType, int numberOfUpdates) {
    	return newInstance(context, dialogType, false, null, numberOfUpdates, false);
    }
    
	/**
	 * 
	 * @param context
	 * @param dialogType
	 * @param succuss
	 *            : The flag to indicate add or remove action result
	 * @param param
	 *            : For Show Dialog Detail Page
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static MyDialogFragment newInstance(Context context, int dialogType, boolean succuss, Object param,
			int numberOfUpdates, boolean isSearchDetail)
	{
		Log.d(TAG, "[newInstance] + Begin");
		MyDialogFragment frag = new MyDialogFragment();

		parentActivity = (FragmentActivity) context;
		mInflater = parentActivity.getLayoutInflater();

		if (null != param)
		{
			dialogKeyMap = (HashMap<ScenarioDialog, List<DialogKeywords>>) param;
			Iterator<ScenarioDialog> iterator = dialogKeyMap.keySet().iterator();
			while (iterator.hasNext())
			{
				dialogItem = (ScenarioDialog) iterator.next();
			}
			keyWordList = dialogKeyMap.get(dialogItem);
			Log.d(TAG, "[newInstance] keyWordList size = " + keyWordList.size());
			for (int i = 0; i < keyWordList.size(); i++)
			{
				DialogKeywords words = keyWordList.get(i);
				String enStr = words.getSrc_keyword();
				String pyStr = words.getKeyword_py();
				String cnStr = words.getDest_keyword();
				Log.d(TAG, "[newInstance] enStr = " + enStr);
				Log.d(TAG, "[newInstance] pyStr = " + pyStr);
				Log.d(TAG, "[newInstance] cnStr = " + cnStr);
			}
		}

		Bundle args = new Bundle();
		args.putInt("dialogType", dialogType);
		args.putBoolean("actionResult", succuss);
		args.putInt("numberOfUpdates", numberOfUpdates);
		args.putBoolean("isSearchDetail", isSearchDetail);
		frag.setArguments(args);
		Log.d(TAG, "[newInstance] + End");
		return frag;
	}

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
	int dialogType = getArguments().getInt("dialogType");
	boolean actionResult = getArguments().getBoolean("actionResult", false);
	final boolean isSearchDetail = getArguments().getBoolean("isSearchDetail", false);
	
	if (dialogType == NO_INTERNET_CONNETION) {
		return new AlertDialog.Builder(parentActivity).setIcon(android.R.drawable.ic_dialog_alert)
			.setTitle(R.string.dialog_msg_no_internet_connection)
			.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
			    public void onClick(DialogInterface dialog, int whichButton) {
			    	
			    }
			}).create();
	} else if (dialogType == ADD_TO_BOOKMARK) {
	    return new AlertDialog.Builder(parentActivity).setIcon(android.R.drawable.ic_dialog_alert)
		    .setTitle(R.string.dialog_msg_add_to_bookmark)
		    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				if (isSearchDetail) {
					SearchDetailFragment.doPositiveClick(ADD_TO_BOOKMARK);
				} else {
					BookmarkDetailFragment.doPositiveClick(ADD_TO_BOOKMARK);
				}
			}
		    }).setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				if (isSearchDetail) {
					SearchDetailFragment.doNegativeClick();
				} else {
					BookmarkDetailFragment.doNegativeClick();
				}
			}
		    }).create();
	} else if (dialogType == ADD_RESULT) {
	    if (actionResult) {
		return new AlertDialog.Builder(parentActivity).setIcon(android.R.drawable.ic_dialog_alert)
			.setTitle(R.string.dialog_msg_add_success)
			.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
			    public void onClick(DialogInterface dialog, int whichButton) {
			    	if (isSearchDetail) {
			    		SearchDetailFragment.doPositiveClick(ADD_RESULT);
			    	} else {
			    		BookmarkDetailFragment.doPositiveClick(ADD_RESULT);
			    	}
			    }
			}).create();
	    } else {
		return new AlertDialog.Builder(parentActivity).setIcon(android.R.drawable.ic_dialog_alert)
			.setTitle(R.string.dialog_msg_add_failed)
			.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
			    public void onClick(DialogInterface dialog, int whichButton) {
			    	if (isSearchDetail) {
						SearchDetailFragment.doPositiveClick(ADD_RESULT);
					} else {
						BookmarkDetailFragment.doPositiveClick(ADD_RESULT);
					}
			    }
			}).create();
	    }
	} else if (dialogType == REMOVE_FROM_BOOKMARK) {
	    return new AlertDialog.Builder(parentActivity).setIcon(android.R.drawable.ic_dialog_alert)
		    .setTitle(R.string.dialog_msg_remove_from_bookmark)
		    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				if (isSearchDetail) {
					SearchDetailFragment.doPositiveClick(REMOVE_FROM_BOOKMARK);
				} else {
					BookmarkDetailFragment.doPositiveClick(REMOVE_FROM_BOOKMARK);
				}
			}
		    }).setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				if (isSearchDetail) {
					SearchDetailFragment.doNegativeClick();
				} else {
					BookmarkDetailFragment.doNegativeClick();
				}
			}
		    }).create();
	}else if (dialogType == REMOVE_RESULT) {
	    if (actionResult) {
		return new AlertDialog.Builder(parentActivity).setIcon(android.R.drawable.ic_dialog_alert)
			.setTitle(R.string.dialog_msg_remove_success)
			.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
			    public void onClick(DialogInterface dialog, int whichButton) {
			    	if (isSearchDetail) {
						SearchDetailFragment.doPositiveClick(REMOVE_RESULT);
					} else {
						BookmarkDetailFragment.doPositiveClick(REMOVE_RESULT);
					}
			    }
			}).create();
	    } else {
		return new AlertDialog.Builder(parentActivity).setIcon(android.R.drawable.ic_dialog_alert)
			.setTitle(R.string.dialog_msg_remove_failed)
			.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
			    public void onClick(DialogInterface dialog, int whichButton) {
			    	if (isSearchDetail) {
						SearchDetailFragment.doPositiveClick(REMOVE_RESULT);
					} else {
						BookmarkDetailFragment.doPositiveClick(REMOVE_RESULT);
					}
			    }
			}).create();
	    }
	} else if (dialogType == DO_SEARCH_DB) {
	    ProgressDialog dialog = new ProgressDialog(parentActivity);
	    dialog.setMessage(parentActivity.getResources().getString(R.string.dialog_msg_search_hint));
	    dialog.setIndeterminate(true);
	    dialog.setCancelable(true);
	    return dialog;
	} else if (dialogType == DIALOG_DOWNLOAD) {
	    ProgressDialog dialog = new ProgressDialog(parentActivity);
	    dialog.setMessage(parentActivity.getResources().getString(R.string.dialog_msg_download_hint));
	    dialog.setIndeterminate(true);
	    dialog.setCancelable(true);
	    return dialog;
	} else if (dialogType == CHECKING_UPDATE) {
	    ProgressDialog dialog = new ProgressDialog(parentActivity);
	    dialog.setMessage(parentActivity.getResources().getString(R.string.dialog_msg_checking_update_hint));
	    dialog.setIndeterminate(true);
	    dialog.setCancelable(true);
	    return dialog;
	} else if (dialogType == UPDATE_COUNT) {
		int numberOfUpdates = getArguments().getInt("numberOfUpdates");
		return new AlertDialog.Builder(parentActivity).setIcon(android.R.drawable.ic_dialog_alert)
				.setTitle(numberOfUpdates + " " + parentActivity.getResources().getString(R.string.dialog_msg_update_count_remind))
				.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton)
					{
						MainActivity.updateDateToDB();
					}
				})
				.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener()	{
					public void onClick(DialogInterface dialog, int whichButton)
					{
						MainActivity.updateCancelTime();
					}
				}).create();
	} else if (dialogType == SCENARIO_DIALOG) {
	    View dialogView = mInflater.inflate(R.layout.scenario_fragment_dialog, null);
	    final Dialog dialog = new Dialog(parentActivity, R.style.custom_dialog);
	    dialog.setContentView(dialogView);
	    
	    TextView firstLine = (TextView) dialogView.findViewById(R.id.dialog_text_firstline);
	    TextView secondLine = (TextView) dialogView.findViewById(R.id.dialog_text_secondline);
	    TextView thridLine = (TextView) dialogView.findViewById(R.id.dialog_text_thridline);
	    firstLine.setText(dialogItem.getSentence());
	    secondLine.setText(dialogItem.getSentence_py());
	    thridLine.setText(dialogItem.getSentence_en());

	    final LinearLayout wordslayout_0 = (LinearLayout) dialogView.findViewById(R.id.dialog_words_define_panel_0);
	    final LinearLayout wordslayout_1 = (LinearLayout) dialogView.findViewById(R.id.dialog_words_define_panel_1);
	    for (int i = 0; i < keyWordList.size(); i++) {
			View wordsView = mInflater.inflate(R.layout.scenario_fragment_dialog_words, null);
			TextView textEn = (TextView) wordsView.findViewById(R.id.dialog_text_word_en);
			TextView textPY = (TextView) wordsView.findViewById(R.id.dialog_text_word_py);
			TextView textCN = (TextView) wordsView.findViewById(R.id.dialog_text_word_cn);
			DialogKeywords words = keyWordList.get(i);
			if (null != words) {
			    String enStr = words.getSrc_keyword();
			    String pyStr = words.getKeyword_py();
			    String cnStr = words.getDest_keyword();
			    Log.d(TAG, "[onCreateDialog] enStr = " + enStr);
			    Log.d(TAG, "[onCreateDialog] pyStr = " + pyStr);
			    Log.d(TAG, "[onCreateDialog] cnStr = " + cnStr);
			    textEn.setText(enStr);
			    textPY.setText(pyStr);
			    textCN.setText(cnStr);
			}
			if (i < 3) {
			    wordslayout_0.addView(wordsView);
			    wordslayout_0.getViewTreeObserver().addOnGlobalLayoutListener(
	    		new ViewTreeObserver.OnGlobalLayoutListener()
	    		{
	    			@Override
	    			public void onGlobalLayout()
	    			{
	    				// TODO Auto-generated method stub
	    				int maxHeight =  wordslayout_0.getHeight();
	    				Log.d(TAG, "[onCreateDialog] first line height = " + maxHeight);
	    				int count = wordslayout_0.getChildCount();
	    			    for (int i = 0; i < count; i++) {
	    			    	View view = wordslayout_0.getChildAt(i);
	    			    	view.setMinimumHeight(maxHeight);
	    			    	//view.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,maxHeight));
	    			    }
	    			    
	    			    ViewTreeObserver obs = wordslayout_0.getViewTreeObserver();
	    		        obs.removeGlobalOnLayoutListener(this);
	    			}
	    		});
			} else if (i < 6) {
			    wordslayout_1.addView(wordsView);
			    wordslayout_1.getViewTreeObserver().addOnGlobalLayoutListener(
	    		new ViewTreeObserver.OnGlobalLayoutListener()
	    		{
	    			@Override
	    			public void onGlobalLayout()
	    			{
	    				// TODO Auto-generated method stub
	    				int maxHeight =  wordslayout_1.getHeight();
	    				Log.d(TAG, "[onCreateDialog] second line height = " + maxHeight);
	    				int count = wordslayout_1.getChildCount();
	    			    for (int i = 0; i < count; i++) {
	    			    	View view = wordslayout_1.getChildAt(i);
	    			    	view.setMinimumHeight(maxHeight);
	    			    	//setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,maxHeight));
	    			    }
	    			    
	    			    ViewTreeObserver obs = wordslayout_1.getViewTreeObserver();
	    		        obs.removeGlobalOnLayoutListener(this);
	    			}
	    		});
			}
	    }
	    
	    Button btnOK = (Button) dialogView.findViewById(R.id.scenario_fragment_dialog_btn);
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
