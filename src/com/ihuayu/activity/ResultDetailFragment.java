package com.ihuayu.activity;


import java.util.ArrayList;
import java.util.List;

import com.ihuayu.R;
import com.ihuayu.activity.ScenarioFragment.ScenarioEntry;
import com.ihuayu.view.MyDialogFragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author Kesen
 *
 */
public class ResultDetailFragment extends Fragment {

	private static final String TAG = "iHuayu:ResultDetailFragment";
	private static FragmentActivity parentActivity = null;
	private static List<ScenarioEntry> mResultList = null;
	private static int mCurrentPos = 0;
	private static int mDialogType = -1;
	private static boolean mBeFavorited = false;

    /**
     * Create a new instance of ResultDetailFragment
     */
    static ResultDetailFragment newInstance(List<ScenarioEntry> list, int position) {
    	Log.d(TAG, "[newInstance] + Begin");
    	ResultDetailFragment fragment = new ResultDetailFragment();
    	mResultList = list;
    	mCurrentPos = position;
        return fragment;
    }
    
	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.d(TAG, "[onCreate] + Begin");
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}
	
    /**
     * When creating, retrieve this parameter from its arguments.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
    	Log.d(TAG, "[onCreateView] + Begin");
        View v = inflater.inflate(R.layout.result_detail_fragment, container, false);
        return v;
    }

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		Log.d(TAG, "[onViewCreated] + Begin");
		// TODO Auto-generated method stub
		super.onViewCreated(view, savedInstanceState);
		parentActivity = this.getActivity();
		
		Button btnPrev = (Button)parentActivity.findViewById(R.id.result_detail_footbar_btn_prev);
		btnPrev.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mCurrentPos--;
				updateDataFragment();
			}
		});
		
		Button btnNext = (Button)parentActivity.findViewById(R.id.result_detail_footbar_btn_next);
		btnNext.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mCurrentPos++;
				updateDataFragment();
			}
		});
		
		final ImageView favoriteImg = (ImageView)parentActivity.findViewById(R.id.result_detail_des_favorite_img);
		favoriteImg.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (!mBeFavorited) {
					mDialogType = MyDialogFragment.ADD_TO_BOOKMARK;
					showDialog(mDialogType);
				} else {
					favoriteImg.setImageResource(R.drawable.btn_mark_off_2x);
					Toast.makeText(parentActivity, "Cancel", Toast.LENGTH_SHORT).show();
					mDialogType = -1;
					mBeFavorited = false;
				}
			}
		});
		
		//Set Back Button On Click Listener
		Button backBtn = (Button)parentActivity.findViewById(R.id.result_detail_title_bar_backbtn);
		backBtn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				// TODO Auto-generated method stub
				FragmentManager fm = parentActivity.getSupportFragmentManager();
				fm.popBackStack();
				
				Fragment currentFragment = fm.findFragmentById(R.id.tab_content_scenario);
				FragmentTransaction ft = fm.beginTransaction();
				ft.remove(currentFragment);
				ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
				ft.commit();
			}
		});
		
		this.updateDataFragment();
	}
	
    public static void showDialog(int dialogType) {
    	if (dialogType == MyDialogFragment.ADD_TO_BOOKMARK) {
    		DialogFragment newFragment = MyDialogFragment.newInstance(parentActivity, MyDialogFragment.ADD_TO_BOOKMARK);
            newFragment.show(parentActivity.getSupportFragmentManager(), "dialog_add");
    	} else {
    		DialogFragment newFragment = MyDialogFragment.newInstance(parentActivity, MyDialogFragment.ADD_SUCCESS);
            newFragment.show(parentActivity.getSupportFragmentManager(), "dialog_success");
    	}
    }
    
    public static void doPositiveClick() {
        // Do stuff here.
        Log.i(TAG, "Positive click!");
        if (mDialogType == MyDialogFragment.ADD_TO_BOOKMARK) {
        	ImageView favoriteImg = (ImageView)parentActivity.findViewById(R.id.result_detail_des_favorite_img);
        	favoriteImg.setImageResource(R.drawable.btn_mark_on_2x);
        	mDialogType = MyDialogFragment.ADD_SUCCESS;
        	showDialog(MyDialogFragment.ADD_SUCCESS);
        } else if (mDialogType == MyDialogFragment.ADD_SUCCESS) {
        	mBeFavorited = true;
        	mDialogType = -1;
        } else {
        	mDialogType = -1;
        	mBeFavorited = false;
        }
    }
    
    public static void doNegativeClick() {
        // Do stuff here.
        Log.i(TAG, "Negative click!");
        mDialogType = -1;
        mBeFavorited = false;
    }
	
	public void updateDataFragment() {
		//Add Detail Dialog Fragment
		FragmentManager fm = parentActivity.getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		ResultDemoFragment list = (ResultDemoFragment) fm.findFragmentById(R.id.result_detail_demo_listview);
        // Create the list fragment and add it as our sole content.
        if (list == null) {
        	Log.d(TAG, "[onViewCreated] new ResultDemoFragment, do add");
        	list = new ResultDemoFragment();
			ft.add(R.id.result_detail_demo_listview, list);
			ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
			//ft.addToBackStack(null);
			ft.commit();
        } else {
        	Log.d(TAG, "[onViewCreated] used ResultDemoFragment, do replace");
        	list = new ResultDemoFragment();
			ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
			ft.replace(R.id.result_detail_demo_listview, list);
			//ft.addToBackStack(null);
			ft.commit();
        }
	}
	
	public static class ResultDemoFragment extends ListFragment implements 
		LoaderManager.LoaderCallbacks<List<ScenarioEntry>> {
		
		// This is the Adapter being used to display the list's data.
		ResultDemoAdapter mAdapter;
		
		@Override
		public void onActivityCreated(Bundle savedInstanceState) {
			Log.d(TAG, "[ResultDemoFragment][onActivityCreated] + Begin");
			super.onActivityCreated(savedInstanceState);
	
			// Give some text to display if there is no data. In a real
			// application this would come from a resource.
			this.setEmptyText("No Scenarios");
	
			// We have a menu item to show in action bar.
			this.setHasOptionsMenu(false);
	
			// Create an empty adapter we will use to display the loaded data.
			mAdapter = new ResultDemoAdapter(this.getActivity());
			this.setListAdapter(mAdapter);
	
			// Start out with a progress indicator.
			this.setListShown(false);
	
			// Prepare the loader. Either re-connect with an existing one,
			// or start a new one.
			this.getLoaderManager().initLoader(0, null, this);
		}
	
		@Override
		public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
			Log.d(TAG, "[ResultDemoFragment][onCreateOptionsMenu] + Begin");
			// Since we no need action bar and menu,Choose close
			menu.close();
		}
	
		@Override
		public void onListItemClick(ListView l, View v, int position, long id) {
			// Insert desired behavior here.
			Log.i(TAG, "[ResultDemoAdapter][onListItemClick] Item clicked: " + id);
			
		}
	
		public Loader<List<ScenarioEntry>> onCreateLoader(int id, Bundle args) {
			Log.d(TAG, "[ResultDemoFragment][onCreateLoader] + Begin");
			// This is called when a new Loader needs to be created. This
			// sample only has one Loader with no arguments, so it is simple.
			return new ResultDemoLoader(this.getActivity());
		}
	
		public void onLoadFinished(Loader<List<ScenarioEntry>> loader, List<ScenarioEntry> data) {
			Log.d(TAG, "[ResultDemoFragment][onLoadFinished] + Begin");
			// Set the new data in the adapter.
			mAdapter.setData(data);
			
			ScenarioEntry currentEntry = mResultList.get(mCurrentPos);
			TextView TextView1 = (TextView) parentActivity.findViewById(R.id.result_detail_des_first_line);
			TextView1.setText(currentEntry.getLabel());
			TextView TextView2 = (TextView) parentActivity.findViewById(R.id.result_detail_des_second_line_text);
			TextView2.setText(currentEntry.getLabel());
			TextView TextView3 = (TextView) parentActivity.findViewById(R.id.result_detail_des_third_line);
			TextView3.setText(currentEntry.getLabel());
			
			TextView TextViewSourceLabel = (TextView) parentActivity.findViewById(R.id.result_detail_des_source_info);
			TextViewSourceLabel.setText(currentEntry.getLabel());
			TextView TextView1SourceInfo = (TextView) parentActivity.findViewById(R.id.result_detail_des_source_text);
			TextView1SourceInfo.setText(currentEntry.getLabel());
	
			// The list should now be shown.
			if (this.isResumed()) {
				this.setListShown(true);
			} else {
				this.setListShownNoAnimation(true);
			}
		}
	
		public void onLoaderReset(Loader<List<ScenarioEntry>> loader) {
			Log.d(TAG, "[ResultDemoFragment][onLoaderReset] + Begin");
			// Clear the data in the adapter.
			mAdapter.setData(null);
		}
	}
	

	/**
	 * A custom Loader that loads all of the installed applications.
	 */
	public static class ResultDemoLoader extends AsyncTaskLoader<List<ScenarioEntry>> {

		List<ScenarioEntry> mScenarioList;
	    //ScenarioEntry mScenarioEntry;
	    public ResultDemoLoader(Context context) {
	        super(context);
	    }

	    /**
	     * This is where the bulk of our work is done.  This function is
	     * called in a background thread and should generate a new set of
	     * data to be published by the loader.
	     */
	    @Override 
	    public List<ScenarioEntry> loadInBackground() {
	    	Log.d(TAG, "[ResultDemoLoader][loadInBackground] + Begin");
	    	//TODO:Dummy Data
	    	List<ScenarioEntry> entries = new ArrayList<ScenarioEntry>(3);
	    	for (int i=0; i<3; i++) {
	            entries.add(mResultList.get(mCurrentPos));
	        }
	        Log.d(TAG, "[ResultDemoLoader][loadInBackground] + End");
	        // Done!
	        return entries;
	    }

	    /**
	     * Called when there is new data to deliver to the client.  The
	     * super class will take care of delivering it; the implementation
	     * here just adds a little more logic.
	     */
	    @Override 
	    public void deliverResult(List<ScenarioEntry> scenarioList) {
	    	Log.d(TAG, "[ResultDemoLoader][deliverResult] + Begin");
	        if (this.isReset()) {
	            // An async query came in while the loader is stopped.  We
	            // don't need the result.
	            if (scenarioList != null) {
	                this.onReleaseResources(scenarioList);
	            }
	        }
	        Log.d(TAG, "[ResultDemoLoader][deliverResult] List Size="+scenarioList.size());
	        List<ScenarioEntry> oldScenarioList = scenarioList;
	        mScenarioList = scenarioList;

	        if (this.isStarted()) {
	            // If the Loader is currently started, we can immediately
	            // deliver its results.
	            super.deliverResult(scenarioList);
	        }

	        // At this point we can release the resources associated with
	        // 'oldScenarioList' if needed; now that the new result is delivered we
	        // know that it is no longer in use.
	        if (oldScenarioList != null) {
	            this.onReleaseResources(oldScenarioList);
	        }
	        Log.d(TAG, "[ResultDemoLoader][deliverResult] + End");
	    }

	    /**
	     * Handles a request to start the Loader.
	     */
	    @Override 
	    protected void onStartLoading() {
	    	Log.d(TAG, "[ScenarioListLoader][onStartLoading] + Begin");
	        if (mScenarioList != null) {
	            // If we currently have a result available, deliver it
	            // immediately.
	            this.deliverResult(mScenarioList);
	        }


	        if (takeContentChanged() || mScenarioList == null) {
	            // If the data has changed since the last time it was loaded
	            // or is not currently available, start a load.
	            this.forceLoad();
	        }
	        Log.d(TAG, "[ScenarioListLoader][onStartLoading] + End");
	    }

	    /**
	     * Handles a request to stop the Loader.
	     */
	    @Override 
	    protected void onStopLoading() {
	    	Log.d(TAG, "[ScenarioListLoader][onStopLoading] + Begin");
	        // Attempt to cancel the current load task if possible.
	        this.cancelLoad();
	    }

	    /**
	     * Handles a request to cancel a load.
	     */
	    @Override 
	    public void onCanceled(List<ScenarioEntry> scenarioList) {
	    	Log.d(TAG, "[ResultDemoLoader][onCanceled] + Begin");
	        super.onCanceled(scenarioList);

	        // At this point we can release the resources associated with 'scenarioList' if needed.
	        this.onReleaseResources(scenarioList);
	    }

	    /**
	     * Handles a request to completely reset the Loader.
	     */
	    @Override 
	    protected void onReset() {
	    	Log.d(TAG, "[ScenarioListLoader][onReset] + Begin");
	        super.onReset();

	        // Ensure the loader is stopped
	        this.onStopLoading();

	        // At this point we can release the resources associated with 'scenarioList' if needed.
	        if (mScenarioList != null) {
	            this.onReleaseResources(mScenarioList);
	            mScenarioList = null;
	        }
	    }

	    /**
	     * Helper function to take care of releasing resources associated
	     * with an actively loaded data set.
	     */
	    protected void onReleaseResources(List<ScenarioEntry> scenario) {
	        // For a simple List<> there is nothing to do.  For something
	        // like a Cursor, we would close it here.
	    }
	}
	
	public static class ResultDemoAdapter extends ArrayAdapter<ScenarioEntry> {
	    private final LayoutInflater mInflater;

	    public ResultDemoAdapter(Context context) {
	        //super(context, android.R.layout.simple_list_item_2);
	    	super(context, R.layout.result_detail_list_item);
	        mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	    }

	    public void setData(List<ScenarioEntry> data) {
	    	Log.d(TAG, "[ResultDemoAdapter][setData] + Begin");
	        this.clear();
	        if (data != null) {
	        	Log.d(TAG, "[ResultDemoAdapter][setData] Size"+data.size());
	            for (ScenarioEntry scenarioEntry : data) {
	                this.add(scenarioEntry);
	            }
	        }
	        Log.d(TAG, "[ResultDemoAdapter][setData] + End");
	    }
	    
	    /**
		 * Populate new items in the list.
		 */
	    @Override 
	    public View getView(int position, View convertView, ViewGroup parent) {
	    	Log.d(TAG, "[ResultDemoAdapter][getView] + pos="+position);
	        View view;
			
	        if (convertView == null) {
        		Log.d(TAG, "convertView == null, new result_detail_list_item");
        		view = mInflater.inflate(R.layout.result_detail_list_item, parent, false);
	        } else {
	            view = convertView;
	        }

	        final ScenarioEntry item = this.getItem(position);
			if (item != null) {
				
				TextView TextLine = (TextView)view.findViewById(R.id.result_detail_listitem_text);
				TextLine.setText(item.getLabel());
				
				ImageView speakIcon = (ImageView)view.findViewById(R.id.result_detail_listitem_icon);
				speakIcon.setImageResource(R.drawable.btn_speaker_s);
				speakIcon.setOnClickListener(new View.OnClickListener()	{
					@Override
					public void onClick(View v)
					{
						// TODO Auto-generated method stub
						Toast.makeText(parentActivity, "Not Ready", Toast.LENGTH_SHORT).show();
					}
				});
			}
	        return view;
	    }
	}
}



