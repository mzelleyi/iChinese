package com.ihuayu.activity;


import com.ihuayu.R;
import com.ihuayu.activity.ScenarioFragment.ScenarioEntry;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * @author Kesen
 *
 */
public class ScenarioDetailFragment extends Fragment implements 
	LoaderManager.LoaderCallbacks<ScenarioEntry> {

	private static final String TAG = "iHuayu:ScenarioDetailFragment";
	private static FragmentActivity parentActivity = null;
	private static ScenarioEntry mScenarioEntry = null;

    /**
     * Create a new instance of ScenarioFragment
     */
    static ScenarioDetailFragment newInstance(ScenarioEntry item) {
    	Log.d(TAG, "[newInstance] + Begin");
    	ScenarioDetailFragment fragment = new ScenarioDetailFragment();
    	mScenarioEntry = item;
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
        View v = inflater.inflate(R.layout.scenario_detail_fragment, container, false);
        return v;
    }

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		Log.d(TAG, "[onViewCreated] + Begin");
		// TODO Auto-generated method stub
		super.onViewCreated(view, savedInstanceState);
		
		parentActivity = this.getActivity();
		Button backBtn = (Button)parentActivity.findViewById(R.id.scenario_detail_backbtn);
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
        
        this.getLoaderManager().initLoader(0, null, this);
	}
	
	@Override
	public Loader<ScenarioEntry> onCreateLoader(int id, Bundle args) {
		Log.d(TAG, "[onCreateLoader] + Begin");
		// This is called when a new Loader needs to be created. This
		// sample only has one Loader with no arguments, so it is simple.
		return new ScenarioDetailLoader(this.getActivity());
	}
	
	@Override
	public void onLoadFinished(Loader<ScenarioEntry> loader, ScenarioEntry data) {
		Log.d(TAG, "[onLoadFinished] + Begin");
		// Set the new data in the adapter.
		//mAdapter.setData(data);
		
		ImageView leftImg = (ImageView)parentActivity.findViewById(R.id.scenario_detail_item_icon_left);
		leftImg.setImageResource(R.drawable.icn_paper_2x);
		
		TextView TextView1 = (TextView) parentActivity.findViewById(R.id.scenario_detail_item_text_first_line);
		TextView1.setText(data.getLabel());
		TextView TextView2 = (TextView) parentActivity.findViewById(R.id.scenario_detail_item_text_second_line);
		TextView2.setText(data.getLabel());
		TextView TextView3 = (TextView) parentActivity.findViewById(R.id.scenario_detail_item_text_third_line);
		TextView3.setText(data.getLabel());
		
		// The list should now be shown.
//		if (this.isResumed()) {
//			this.setListShown(true);
//		} else {
//			this.setListShownNoAnimation(true);
//		}
	}
	
	@Override
	public void onLoaderReset(Loader<ScenarioEntry> loader) {
		Log.d(TAG, "[onLoaderReset] + Begin");
		// Clear the data in the adapter.
		//mAdapter.setData(null);
	}
	

	/**
	 * A custom Loader that loads all of the installed applications.
	 */
	public static class ScenarioDetailLoader extends AsyncTaskLoader<ScenarioEntry> {

	    //ScenarioEntry mScenarioEntry;
	    public ScenarioDetailLoader(Context context) {
	        super(context);
	    }

	    /**
	     * This is where the bulk of our work is done.  This function is
	     * called in a background thread and should generate a new set of
	     * data to be published by the loader.
	     */
	    @Override 
	    public ScenarioEntry loadInBackground() {
	    	Log.d(TAG, "[ScenarioDetailLoader][loadInBackground] + Begin");
	        
	        Log.d(TAG, "[ScenarioDetailLoader][loadInBackground] + End");
	        // Done!
	        return mScenarioEntry;
	    }

	    /**
	     * Called when there is new data to deliver to the client.  The
	     * super class will take care of delivering it; the implementation
	     * here just adds a little more logic.
	     */
	    @Override 
	    public void deliverResult(ScenarioEntry mScenarioEntry) {
	    	Log.d(TAG, "[ScenarioDetailLoader][deliverResult] + Begin");
	        if (this.isReset()) {
	            // An async query came in while the loader is stopped.  We
	            // don't need the result.
	            if (mScenarioEntry != null) {
	                this.onReleaseResources(mScenarioEntry);
	            }
	        }

	        if (this.isStarted()) {
	            // If the Loader is currently started, we can immediately
	            // deliver its results.
	            super.deliverResult(mScenarioEntry);
	        }

	        // At this point we can release the resources associated with
	        // 'oldScenarioList' if needed; now that the new result is delivered we
	        // know that it is no longer in use.
	        if (mScenarioEntry != null) {
	            this.onReleaseResources(mScenarioEntry);
	        }
	        Log.d(TAG, "[ScenarioDetailLoader][deliverResult] + End");
	    }

	    /**
	     * Handles a request to start the Loader.
	     */
	    @Override 
	    protected void onStartLoading() {
	    	Log.d(TAG, "[ScenarioDetailLoader][onStartLoading] + Begin");
	        if (mScenarioEntry != null) {
	            // If we currently have a result available, deliver it
	            // immediately.
	            this.deliverResult(mScenarioEntry);
	        }

	        if (takeContentChanged() || mScenarioEntry == null) {
	            // If the data has changed since the last time it was loaded
	            // or is not currently available, start a load.
	            this.forceLoad();
	        }
	        Log.d(TAG, "[ScenarioDetailLoader][onStartLoading] + End");
	    }

	    /**
	     * Handles a request to stop the Loader.
	     */
	    @Override 
	    protected void onStopLoading() {
	    	Log.d(TAG, "[ScenarioDetailLoader][onStopLoading] + Begin");
	        // Attempt to cancel the current load task if possible.
	        this.cancelLoad();
	    }

	    /**
	     * Handles a request to cancel a load.
	     */
	    @Override 
	    public void onCanceled(ScenarioEntry scenario) {
	    	Log.d(TAG, "[ScenarioDetailLoader][onCanceled] + Begin");
	        super.onCanceled(scenario);

	        // At this point we can release the resources associated with 'scenarioList' if needed.
	        this.onReleaseResources(scenario);
	    }

	    /**
	     * Handles a request to completely reset the Loader.
	     */
	    @Override 
	    protected void onReset() {
	    	Log.d(TAG, "[ScenarioDetailLoader][onReset] + Begin");
	        super.onReset();

	        // Ensure the loader is stopped
	        this.onStopLoading();

	        // At this point we can release the resources associated with 'scenarioList' if needed.
	        if (mScenarioEntry != null) {
	            this.onReleaseResources(mScenarioEntry);
	            mScenarioEntry = null;
	        }
	    }

	    /**
	     * Helper function to take care of releasing resources associated
	     * with an actively loaded data set.
	     */
	    protected void onReleaseResources(ScenarioEntry scenario) {
	        // For a simple List<> there is nothing to do.  For something
	        // like a Cursor, we would close it here.
	    }
	}
}



