package com.ihuayu.activity;


import java.util.ArrayList;
import java.util.List;

import com.ihuayu.R;
import com.ihuayu.activity.ScenarioFragment.ScenarioEntry;

import android.content.Context;
import android.os.Bundle;
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

/**
 * @author Kesen
 *
 */
public class ScenarioDetailFragment extends Fragment {

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
		
		//Init Item Outline Info
		ImageView leftImg = (ImageView)parentActivity.findViewById(R.id.scenario_detail_item_icon_left);
		leftImg.setImageResource(R.drawable.icn_paper_2x);
		TextView TextView1 = (TextView) parentActivity.findViewById(R.id.scenario_detail_item_text_first_line);
		TextView1.setText(mScenarioEntry.getLabel());
		TextView TextView2 = (TextView) parentActivity.findViewById(R.id.scenario_detail_item_text_second_line);
		TextView2.setText(mScenarioEntry.getLabel());
		TextView TextView3 = (TextView) parentActivity.findViewById(R.id.scenario_detail_item_text_third_line);
		TextView3.setText(mScenarioEntry.getLabel());
		
		//Set Back Button On Click Listener
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
		
		//Add Detail Dialog Fragment
		FragmentManager fm = parentActivity.getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		ScenarioDialogFragment list = (ScenarioDialogFragment) fm.findFragmentById(R.id.scenario_detail_dialog_listview);
        // Create the list fragment and add it as our sole content.
        if (list == null) {
        	Log.d(TAG, "[onViewCreated] new ScenarioDialogFragment, do add");
        	list = new ScenarioDialogFragment();
			ft.add(R.id.scenario_detail_dialog_listview, list);
			ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
			//ft.addToBackStack(null);
			ft.commit();
        } else {
        	Log.d(TAG, "[onViewCreated] used ScenarioDialogFragment, do replace");
        	list = new ScenarioDialogFragment();
			ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
			ft.replace(R.id.scenario_detail_dialog_listview, list);
			//ft.addToBackStack(null);
			ft.commit();
        }
	}
	
	public static class ScenarioDialogFragment extends ListFragment implements 
		LoaderManager.LoaderCallbacks<List<ScenarioEntry>> {
		
		// This is the Adapter being used to display the list's data.
		ScenarioDialogAdapter mAdapter;
		
		@Override
		public void onActivityCreated(Bundle savedInstanceState) {
			Log.d(TAG, "[ScenarioDialogFragment][onActivityCreated] + Begin");
			super.onActivityCreated(savedInstanceState);
	
			// Give some text to display if there is no data. In a real
			// application this would come from a resource.
			this.setEmptyText("No Scenarios");
	
			// We have a menu item to show in action bar.
			this.setHasOptionsMenu(false);
	
			// Create an empty adapter we will use to display the loaded data.
			mAdapter = new ScenarioDialogAdapter(this.getActivity());
			this.setListAdapter(mAdapter);
	
			// Start out with a progress indicator.
			this.setListShown(false);
	
			// Prepare the loader. Either re-connect with an existing one,
			// or start a new one.
			this.getLoaderManager().initLoader(0, null, this);
		}
	
		@Override
		public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
			Log.d(TAG, "[ScenarioDialogFragment][onCreateOptionsMenu] + Begin");
			// Since we no need action bar and menu,Choose close
			menu.close();
		}
	
		@Override
		public void onListItemClick(ListView l, View v, int position, long id) {
			// Insert desired behavior here.
			Log.i(TAG, "[ScenarioDialogFragment][onListItemClick] Item clicked: " + id);
			
		}
	
		public Loader<List<ScenarioEntry>> onCreateLoader(int id, Bundle args) {
			Log.d(TAG, "[ScenarioDialogFragment][onCreateLoader] + Begin");
			// This is called when a new Loader needs to be created. This
			// sample only has one Loader with no arguments, so it is simple.
			return new ScenarioDialogLoader(this.getActivity());
		}
	
		public void onLoadFinished(Loader<List<ScenarioEntry>> loader, List<ScenarioEntry> data) {
			Log.d(TAG, "[ScenarioDialogFragment][onLoadFinished] + Begin");
			// Set the new data in the adapter.
			mAdapter.setData(data);
	
			// The list should now be shown.
			if (this.isResumed()) {
				this.setListShown(true);
			} else {
				this.setListShownNoAnimation(true);
			}
		}
	
		public void onLoaderReset(Loader<List<ScenarioEntry>> loader) {
			Log.d(TAG, "[ScenarioDialogFragment][onLoaderReset] + Begin");
			// Clear the data in the adapter.
			mAdapter.setData(null);
		}
	}
	

	/**
	 * A custom Loader that loads all of the installed applications.
	 */
	public static class ScenarioDialogLoader extends AsyncTaskLoader<List<ScenarioEntry>> {

		List<ScenarioEntry> mScenarioList;
	    //ScenarioEntry mScenarioEntry;
	    public ScenarioDialogLoader(Context context) {
	        super(context);
	    }

	    /**
	     * This is where the bulk of our work is done.  This function is
	     * called in a background thread and should generate a new set of
	     * data to be published by the loader.
	     */
	    @Override 
	    public List<ScenarioEntry> loadInBackground() {
	    	Log.d(TAG, "[ScenarioDialogLoader][loadInBackground] + Begin");
	    	//TODO:Dummy Data
	    	List<ScenarioEntry> entries = new ArrayList<ScenarioEntry>(3);
	    	for (int i=0; i<3; i++) {
	            entries.add(mScenarioEntry);
	        }
	        Log.d(TAG, "[ScenarioDialogLoader][loadInBackground] + End");
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
	    	Log.d(TAG, "[ScenarioDialogLoader][deliverResult] + Begin");
	        if (this.isReset()) {
	            // An async query came in while the loader is stopped.  We
	            // don't need the result.
	            if (scenarioList != null) {
	                this.onReleaseResources(scenarioList);
	            }
	        }
	        Log.d(TAG, "[ScenarioDialogLoader][deliverResult] List Size="+scenarioList.size());
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
	        Log.d(TAG, "[ScenarioDialogLoader][deliverResult] + End");
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
	    	Log.d(TAG, "[ScenarioDialogLoader][onCanceled] + Begin");
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
	
	public static class ScenarioDialogAdapter extends ArrayAdapter<ScenarioEntry> {
	    private final LayoutInflater mInflater;

	    public ScenarioDialogAdapter(Context context) {
	        //super(context, android.R.layout.simple_list_item_2);
	    	super(context, R.layout.scenario_detail_listitem_female);
	        mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	    }

	    public void setData(List<ScenarioEntry> data) {
	    	Log.d(TAG, "[ScenarioDialogAdapter][setData] + Begin");
	    	Log.d(TAG, "[ScenarioDialogAdapter][setData] Size"+data.size());
	        this.clear();
	        if (data != null) {
	            for (ScenarioEntry scenarioEntry : data) {
	                this.add(scenarioEntry);
	            }
	        }
	        Log.d(TAG, "[ScenarioDialogAdapter][setData] + End");
	    }
	    
	    /**
		 * Populate new items in the list.
		 */
	    @Override 
	    public View getView(int position, View convertView, ViewGroup parent) {
	    	Log.d(TAG, "[ScenarioDetailAdapter][getView] + pos="+position);
	        View view;
			
	        if (convertView == null) {
	        	if (position%2 == 0) {
	        		Log.d(TAG, "[ScenarioDetailAdapter][getView] new female item");
	        		view = mInflater.inflate(R.layout.scenario_detail_listitem_female, parent, false);
	        	} else {
	        		Log.d(TAG, "[ScenarioDetailAdapter][getView] new male item");
	        		view = mInflater.inflate(R.layout.scenario_detail_listitem_male, parent, false);
	        	}
	        } else {
	            view = convertView;
	        }

	        final ScenarioEntry item = this.getItem(position);
			if (item != null) {
				ImageView sexIcon = (ImageView)view.findViewById(R.id.scenario_dialog_img_icon);
				if (position%2 == 0) {
					sexIcon.setImageResource(R.drawable.icn_female);
				} else {
					sexIcon.setImageResource(R.drawable.icn_male);
				}
				
				TextView firstLine = (TextView)view.findViewById(R.id.scenario_dialog_text_first_line);
				TextView secondLine = (TextView)view.findViewById(R.id.scenario_dialog_text_second_line);
				TextView nameLine = (TextView)view.findViewById(R.id.scenario_dialog_textview_name);
				nameLine.setText(item.getLabel());
				firstLine.setText(item.getLabel());
				secondLine.setText(item.getLabel());
				
				ImageView speakIcon = (ImageView)view.findViewById(R.id.scenario_dialog_img_speaker);
				speakIcon.setImageResource(R.drawable.btn_speaker_s);
				
				speakIcon.setOnClickListener(new View.OnClickListener()	{
					@Override
					public void onClick(View v)
					{
						// TODO Auto-generated method stub
						
					}
				});
			}
	        return view;
	    }
	}
}



