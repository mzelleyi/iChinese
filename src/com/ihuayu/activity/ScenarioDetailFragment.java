package com.ihuayu.activity;


import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import com.ihuayu.R;
import com.ihuayu.activity.db.entity.Dialog;
import com.ihuayu.activity.db.entity.DialogKeywords;
import com.ihuayu.activity.db.entity.Scenario;

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
	private static Scenario mScenario = null;

    /**
     * Create a new instance of ScenarioFragment
     */
    static ScenarioDetailFragment newInstance(Scenario item) {
    	Log.d(TAG, "[newInstance] + Begin");
    	ScenarioDetailFragment fragment = new ScenarioDetailFragment();
    	mScenario = item;
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
		TextView1.setText(mScenario.getTitle_en());
		TextView TextView2 = (TextView) parentActivity.findViewById(R.id.scenario_detail_item_text_second_line);
		TextView2.setText(mScenario.getTitle_cn());
		TextView TextView3 = (TextView) parentActivity.findViewById(R.id.scenario_detail_item_text_third_line);
		TextView3.setText(mScenario.getTitle_py());
		
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
		LoaderManager.LoaderCallbacks<List<HashMap<Dialog, List<DialogKeywords>>>> {
		
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
	
		public Loader<List<HashMap<Dialog, List<DialogKeywords>>>> onCreateLoader(int id, Bundle args) {
			Log.d(TAG, "[ScenarioDialogFragment][onCreateLoader] + Begin");
			// This is called when a new Loader needs to be created. This
			// sample only has one Loader with no arguments, so it is simple.
			return new ScenarioDialogLoader(this.getActivity());
		}
	
		public void onLoadFinished(Loader<List<HashMap<Dialog, List<DialogKeywords>>>> loader, 
				List<HashMap<Dialog, List<DialogKeywords>>> data) {
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
	
		public void onLoaderReset(Loader<List<HashMap<Dialog, List<DialogKeywords>>>> loader) {
			Log.d(TAG, "[ScenarioDialogFragment][onLoaderReset] + Begin");
			// Clear the data in the adapter.
			mAdapter.setData(null);
		}
	}
	

	/**
	 * A custom Loader that loads all of the installed applications.
	 */
	public static class ScenarioDialogLoader extends AsyncTaskLoader<List<HashMap<Dialog, List<DialogKeywords>>>> {

		List<HashMap<Dialog, List<DialogKeywords>>> mDialogList = null;
		
	    public ScenarioDialogLoader(Context context) {
	        super(context);
	    }

	    /**
	     * This is where the bulk of our work is done.  This function is
	     * called in a background thread and should generate a new set of
	     * data to be published by the loader.
	     */
	    @Override 
	    public List<HashMap<Dialog, List<DialogKeywords>>> loadInBackground() {
	    	Log.d(TAG, "[ScenarioDialogLoader][loadInBackground] + Begin");
	    	
	    	mDialogList = MainActivity.dbManagerment.getDialogList(mScenario.getTitle_id());
	        
	    	Log.d(TAG, "[ScenarioDialogLoader][loadInBackground] + Dialog Size = "+mDialogList.size());
	    	Log.d(TAG, "[ScenarioDialogLoader][loadInBackground] + End");
	    	return mDialogList;
	    }

	    /**
	     * Called when there is new data to deliver to the client.  The
	     * super class will take care of delivering it; the implementation
	     * here just adds a little more logic.
	     */
	    @Override 
	    public void deliverResult(List<HashMap<Dialog, List<DialogKeywords>>> dialogList) {
	    	Log.d(TAG, "[ScenarioDialogLoader][deliverResult] + Begin");
	        if (this.isReset()) {
	            // An async query came in while the loader is stopped.  We
	            // don't need the result.
	            if (dialogList != null) {
	                this.onReleaseResources(dialogList);
	            }
	        }
	        Log.d(TAG, "[ScenarioDialogLoader][deliverResult] List Size="+dialogList.size());
	        List<HashMap<Dialog, List<DialogKeywords>>> oldDialogList = dialogList;
	        mDialogList = dialogList;

	        if (this.isStarted()) {
	            // If the Loader is currently started, we can immediately
	            // deliver its results.
	            super.deliverResult(dialogList);
	        }

	        // At this point we can release the resources associated with
	        // 'oldScenarioList' if needed; now that the new result is delivered we
	        // know that it is no longer in use.
	        if (dialogList != null) {
	            this.onReleaseResources(oldDialogList);
	        }
	        Log.d(TAG, "[ScenarioDialogLoader][deliverResult] + End");
	    }

	    /**
	     * Handles a request to start the Loader.
	     */
	    @Override 
	    protected void onStartLoading() {
	    	Log.d(TAG, "[ScenarioListLoader][onStartLoading] + Begin");
	        if (mDialogList != null) {
	            // If we currently have a result available, deliver it
	            // immediately.
	            this.deliverResult(mDialogList);
	        }


	        if (takeContentChanged() || mDialogList == null) {
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
	    public void onCanceled(List<HashMap<Dialog, List<DialogKeywords>>> scenarioList) {
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
	        if (mDialogList != null) {
	            this.onReleaseResources(mDialogList);
	            mDialogList = null;
	        }
	    }

	    /**
	     * Helper function to take care of releasing resources associated
	     * with an actively loaded data set.
	     */
	    protected void onReleaseResources(List<HashMap<Dialog, List<DialogKeywords>>> scenario) {
	        // For a simple List<> there is nothing to do.  For something
	        // like a Cursor, we would close it here.
	    }
	}
	
	public static class ScenarioDialogAdapter extends ArrayAdapter<HashMap<Dialog, List<DialogKeywords>>> {
	    private final LayoutInflater mInflater;

	    public ScenarioDialogAdapter(Context context) {
	        //super(context, android.R.layout.simple_list_item_2);
	    	super(context, R.layout.scenario_detail_listitem_female);
	        mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	    }

	    public void setData(List<HashMap<Dialog, List<DialogKeywords>>> data) {
	    	Log.d(TAG, "[ScenarioDialogAdapter][setData] + Begin");
	        this.clear();
	        
	        if (data != null) {
	        	Log.d(TAG, "[ScenarioDialogAdapter][setData] Size"+data.size());
	            for (HashMap<Dialog, List<DialogKeywords>> dialog : data) {
	                this.add(dialog);
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
	        
	        String dialogGender = null;
	        //String dialogAudio = null;
	        Dialog dialogItem = null;
			
	        final HashMap<Dialog, List<DialogKeywords>> mapItem = this.getItem(position);
	        
	        Iterator<Dialog> iterator = mapItem.keySet().iterator();
	        while(iterator.hasNext()) {
	        	dialogItem = (Dialog) iterator.next();
	        	dialogGender = dialogItem.getGender();
	        	//dialogAudio = dialogItem.getAudio();
	        }

	        if (convertView == null) {
	        	if (dialogGender.equalsIgnoreCase("f")) {
	        		Log.d(TAG, "[ScenarioDialogAdapter][getView] new female item");
	        		view = mInflater.inflate(R.layout.scenario_detail_listitem_female, parent, false);
	        	} else {
	        		Log.d(TAG, "[ScenarioDialogAdapter][getView] new male item");
	        		view = mInflater.inflate(R.layout.scenario_detail_listitem_male, parent, false);
	        	}
	        } else {
	            view = convertView;
	        }


			if (dialogItem != null) {
				ImageView sexIcon = (ImageView)view.findViewById(R.id.scenario_dialog_img_icon);
				if (dialogGender.equalsIgnoreCase("f")) {
					sexIcon.setImageResource(R.drawable.icn_female);
				} else {
					sexIcon.setImageResource(R.drawable.icn_male);
				}
				
				TextView firstLine = (TextView)view.findViewById(R.id.scenario_dialog_text_first_line);
				TextView secondLine = (TextView)view.findViewById(R.id.scenario_dialog_text_second_line);
				TextView nameLine = (TextView)view.findViewById(R.id.scenario_dialog_textview_name);
				nameLine.setText(dialogItem.getNarrator());
				firstLine.setText(dialogItem.getSentence());
				secondLine.setText(dialogItem.getSentence_py());
				
				ImageView speakIcon = (ImageView)view.findViewById(R.id.scenario_dialog_img_speaker);
				speakIcon.setImageResource(R.drawable.btn_speaker_s);
				
				speakIcon.setOnClickListener(new View.OnClickListener()	{
					@Override
					public void onClick(View v)
					{
						// TODO Auto-generated method stub
						//Log.d(TAG, "[ScenarioDialogAdapter][onClick] URL = "+dialogAudio);
					}
				});
			}
	        return view;
	    }
	}
}



