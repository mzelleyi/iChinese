package com.ihuayu.activity;

import java.util.List;

import com.ihuayu.R;
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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

/**
 * @author Kesen
 *
 */
public class ScenarioFragment extends Fragment {

	private static final String TAG = "iHuayu:ScenarioFragment";
	private static FragmentActivity parentActivity = null;

    /**
     * Create a new instance of ScenarioFragment
     */
    static ScenarioFragment newInstance( ) {
    	Log.d(TAG, "[newInstance] + Begin");
    	ScenarioFragment fragment = new ScenarioFragment();
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
        View v = inflater.inflate(R.layout.scenario_fragment, container, false);
        return v;
    }

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		Log.d(TAG, "[onViewCreated] + Begin");
		// TODO Auto-generated method stub
		super.onViewCreated(view, savedInstanceState);
		
		parentActivity = this.getActivity();
		
		FragmentManager fm = parentActivity.getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		ScenarioListFragment list = (ScenarioListFragment) fm.findFragmentById(R.id.fragment_scenario_listview);
        // Create the list fragment and add it as our sole content.
        if (list == null) {
        	Log.d(TAG, "[onViewCreated] new ScenarioListFragment, do add");
        	list = new ScenarioListFragment();
			ft.add(R.id.fragment_scenario_listview, list);
			ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
			//ft.addToBackStack(null);
			ft.commit();
        } else {
        	Log.d(TAG, "[onViewCreated] used ScenarioListFragment, do replace");
        	list = new ScenarioListFragment();
			ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
			ft.replace(R.id.fragment_scenario_listview, list);
			//ft.addToBackStack(null);
			ft.commit();
        }
	}
	
	public static class ScenarioListFragment extends ListFragment implements 
		LoaderManager.LoaderCallbacks<List<Scenario>> {
		
		// This is the Adapter being used to display the list's data.
		ScenarioListAdapter mAdapter;
		
		List<Scenario> mOriginScenarioList = null;
		
		@Override
		public void onActivityCreated(Bundle savedInstanceState) {
			Log.d(TAG, "[ScenarioListFragment][onActivityCreated] + Begin");
			super.onActivityCreated(savedInstanceState);

			// Give some text to display if there is no data. In a real
			// application this would come from a resource.
			this.setEmptyText("No Scenarios");

			// We have a menu item to show in action bar.
			this.setHasOptionsMenu(false);

			// Create an empty adapter we will use to display the loaded data.
			mAdapter = new ScenarioListAdapter(this.getActivity());
			this.setListAdapter(mAdapter);

			// Start out with a progress indicator.
			this.setListShown(false);

			// Prepare the loader. Either re-connect with an existing one,
			// or start a new one.
			this.getLoaderManager().initLoader(0, null, this);
		}

		@Override
		public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
			Log.d(TAG, "[ScenarioListFragment][onCreateOptionsMenu] + Begin");
			// Since we no need action bar and menu,Choose close
			menu.close();
		}

		@Override
		public void onListItemClick(ListView l, View v, int position, long id) {
			// Insert desired behavior here.
			Log.i(TAG, "[ScenarioListFragment][onListItemClick] Item clicked: " + id);
//			FragmentManager fm = parentActivity.getSupportFragmentManager();
//			Fragment newFragment = ResultDetailFragment.newInstance(mOriginScenarioList,position);
//			FragmentTransaction ft = fm.beginTransaction();
//			ft.add(R.id.tab_content_scenario, newFragment);
//			ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
//			ft.addToBackStack(null);
//			ft.commit();
		}

		public Loader<List<Scenario>> onCreateLoader(int id, Bundle args) {
			Log.d(TAG, "[ScenarioListFragment][onCreateLoader] + Begin");
			// This is called when a new Loader needs to be created. This
			// sample only has one Loader with no arguments, so it is simple.
			return new ScenarioListLoader(this.getActivity());
		}

		public void onLoadFinished(Loader<List<Scenario>> loader, List<Scenario> data) {
			Log.d(TAG, "[ScenarioListFragment][onLoadFinished] + Begin");
			
			mOriginScenarioList = data; 
			// Set the new data in the adapter.
			mAdapter.setData(data);

			// The list should now be shown.
			if (this.isResumed()) {
				this.setListShown(true);
			} else {
				this.setListShownNoAnimation(true);
			}
		}

		public void onLoaderReset(Loader<List<Scenario>> loader) {
			Log.d(TAG, "[ScenarioListFragment][onLoaderReset] + Begin");
			// Clear the data in the adapter.
			mAdapter.setData(null);
		}
	}
	
//	/**
//	 * This class holds the per-item data in our Loader.
//	 */
//	public static class ScenarioEntry {
//	    public ScenarioEntry(ScenarioListLoader loader, ApplicationInfo info) {
//	        mLoader = loader;
//	        mInfo = info;
//	        mApkFile = new File(info.sourceDir);
//	    }
//
//	    public ApplicationInfo getApplicationInfo() {
//	        return mInfo;
//	    }
//
//	    public String getLabel() {
//	        return mLabel;
//	    }
//
//	    public Drawable getIcon() {
//	        if (mIcon == null) {
//	            if (mApkFile.exists()) {
//	                mIcon = mInfo.loadIcon(mLoader.mPm);
//	                return mIcon;
//	            } else {
//	                mMounted = false;
//	            }
//	        } else if (!mMounted) {
//	            // If the Scenario wasn't mounted but is now mounted, reload
//	            // its icon.
//	            if (mApkFile.exists()) {
//	                mMounted = true;
//	                mIcon = mInfo.loadIcon(mLoader.mPm);
//	                return mIcon;
//	            }
//	        } else {
//	            return mIcon;
//	        }
//
//	        return mLoader.getContext().getResources().getDrawable(
//	                android.R.drawable.sym_def_app_icon);
//	    }
//
//	    @Override 
//	    public String toString() {
//	        return mLabel;
//	    }
//
//	    void loadLabel(Context context) {
//	        if (mLabel == null || !mMounted) {
//	            if (!mApkFile.exists()) {
//	                mMounted = false;
//	                mLabel = mInfo.packageName;
//	            } else {
//	                mMounted = true;
//	                CharSequence label = mInfo.loadLabel(context.getPackageManager());
//	                mLabel = label != null ? label.toString() : mInfo.packageName;
//	            }
//	        }
//	    }
//
//	    private final ScenarioListLoader mLoader;
//	    private final ApplicationInfo mInfo;
//	    private final File mApkFile;
//	    private String mLabel;
//	    private Drawable mIcon;
//	    private boolean mMounted;
//	}

	/**
	 * A custom Loader that loads all of the installed applications.
	 */
	public static class ScenarioListLoader extends AsyncTaskLoader<List<Scenario>> {
	    //final InterestingConfigChanges mLastConfig = new InterestingConfigChanges();
	    //final PackageManager mPm;

	    List<Scenario> mScenarioList;
	    //PackageIntentReceiver mPackageObserver;

	    public ScenarioListLoader(Context context) {
	        super(context);

	        // Retrieve the package manager for later use; note we don't
	        // use 'context' directly but instead the save global application
	        // context returned by getContext().
	        // mPm = getContext().getPackageManager();
	    }

	    /**
	     * This is where the bulk of our work is done.  This function is
	     * called in a background thread and should generate a new set of
	     * data to be published by the loader.
	     */
	    @Override 
	    public List<Scenario> loadInBackground() {
	    	Log.d(TAG, "[ScenarioListLoader][loadInBackground] + Begin");
	        // Retrieve all known applications.
	    	
	    	mScenarioList = MainActivity.dbManagerment.getAllScenarios();
	    	
//	        List<ApplicationInfo> apps = mPm.getInstalledApplications(
//	                PackageManager.GET_UNINSTALLED_PACKAGES |
//	                PackageManager.GET_DISABLED_COMPONENTS);
//	        if (apps == null) {
//	            apps = new ArrayList<ApplicationInfo>();
//	        }

//	        final Context context = getContext();

	        // Create corresponding array of entries and load their labels.
//	        List<Scenario> entries = new ArrayList<Scenario>(mScenarioList.size());
//	        for (int i=0; i<mScenarioList.size(); i++) {
//	        	Scenario entry = mScenarioList.get(i);
//	            //entry.loadLabel(context);
//	            entries.add(entry);
//	        }

	        // Sort the list.
//	        Collections.sort(entries, ALPHA_COMPARATOR);
	        
	        Log.d(TAG, "[ScenarioListLoader][loadInBackground] List Size ="+mScenarioList.size());
	        Log.d(TAG, "[ScenarioListLoader][loadInBackground] + End");
	        // Done!
	        return mScenarioList;
	    }

	    /**
	     * Called when there is new data to deliver to the client.  The
	     * super class will take care of delivering it; the implementation
	     * here just adds a little more logic.
	     */
	    @Override 
	    public void deliverResult(List<Scenario> scenarioList) {
	    	Log.d(TAG, "[ScenarioListLoader][deliverResult] + Begin");
	        if (this.isReset()) {
	            // An async query came in while the loader is stopped.  We
	            // don't need the result.
	            if (scenarioList != null) {
	                this.onReleaseResources(scenarioList);
	            }
	        }
	        List<Scenario> oldScenarioList = scenarioList;
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
	        Log.d(TAG, "[ScenarioListLoader][deliverResult] + End");
	    }

	    /**
	     * Handles a request to start the Loader.
	     */
	    @Override 
	    protected void onStartLoading() {
	    	Log.d(TAG, "[ScenarioListLoader][onStartLoading] + Begin");
	        if (mScenarioList != null) {
	            // If we currently have a result available, deliver it immediately.
	            this.deliverResult(mScenarioList);
	        }

	        // Has something interesting in the configuration changed since we
	        // last built the list?
	        // boolean configChange = mLastConfig.applyNewConfig(getContext().getResources());

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
	    public void onCanceled(List<Scenario> scenarioList) {
	    	Log.d(TAG, "[ScenarioListLoader][onCanceled] + Begin");
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
	    protected void onReleaseResources(List<Scenario> scenarioList) {
	        // For a simple List<> there is nothing to do.  For something
	        // like a Cursor, we would close it here.
	    }
	}

	public static class ScenarioListAdapter extends ArrayAdapter<Scenario> {
	    private final LayoutInflater mInflater;

	    public ScenarioListAdapter(Context context) {
	        //super(context, android.R.layout.simple_list_item_2);
	        //super(context, R.layout.scenario_list_item);
	    	super(context, R.layout.scenario_list_item);
	        mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	    }

	    public void setData(List<Scenario> data) {
	    	Log.d(TAG, "[ScenarioListAdapter][setData] + Begin");
	        this.clear();
	        if (data != null) {
	        	Log.d(TAG, "[ScenarioListAdapter][setData] Size"+data.size());
	            for (Scenario scenarioEntry : data) {
	                this.add(scenarioEntry);
	            }
	        }
	    }
	    
		class ViewHolder
		{
			public ImageView	listImageLeft	= null;
			public TextView		listTextView1	= null;
			public TextView		listTextView2	= null;
			public TextView		listTextView3	= null;
			public ImageView	listImageRight	= null;
		}

	    /**
		 * Populate new items in the list.
		 */
	    @Override 
	    public View getView(int position, View convertView, ViewGroup parent) {
	    	Log.d(TAG, "[ScenarioListAdapter][getView] + pos="+position);
	        //View view;
			ViewHolder holder;
			if (convertView == null) {
				Log.d(TAG, "convertView == null,Then inflate and new holder");
				convertView = mInflater.inflate(R.layout.scenario_list_item, parent, false);
				holder = new ViewHolder();
				holder.listImageLeft = (ImageView) convertView.findViewById(R.id.scenario_listitem_icon_left);
				holder.listTextView1 = (TextView) convertView.findViewById(R.id.scenario_listitem_text_first_line);
				holder.listTextView2 = (TextView) convertView.findViewById(R.id.scenario_listitem_text_second_line);
				holder.listTextView3 = (TextView) convertView.findViewById(R.id.scenario_listitem_text_third_line);
				holder.listImageRight = (ImageView) convertView.findViewById(R.id.scenario_listitem_icon_right);
				convertView.setTag(holder);
			} else {
				Log.d(TAG, "convertView != null,Then get Holder");
				holder = (ViewHolder) convertView.getTag();
			}

	        final Scenario item = this.getItem(position);
			if (holder != null && item != null) {
				holder.listImageLeft.setImageResource(R.drawable.icn_paper_2x);
				holder.listTextView1.setText(item.getTitle_en());
				holder.listTextView2.setText(item.getTitle_cn());
				holder.listTextView3.setText(item.getTitle_py());
				holder.listImageRight.setImageResource(R.drawable.icn_accessoryview_2x);
				holder.listImageRight.setOnClickListener(new View.OnClickListener()	{
					@Override
					public void onClick(View v)
					{
						// TODO Auto-generated method stub
						FragmentManager fm = parentActivity.getSupportFragmentManager();
						Fragment newFragment = ScenarioDetailFragment.newInstance(item);
						FragmentTransaction ft = fm.beginTransaction();
						ft.add(R.id.tab_content_scenario, newFragment);
						ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
						ft.addToBackStack(null);
						ft.commit();
					}
				});
			}
	        return convertView;
	    }
	}

//	/**
//	 * Helper class to look for interesting changes to the installed apps
//	 * so that the loader can be updated.
//	 */
//	public static class PackageIntentReceiver extends BroadcastReceiver {
//	    final ScenarioListLoader mLoader;
//
//	    public PackageIntentReceiver(ScenarioListLoader loader) {
//	        mLoader = loader;
//	        IntentFilter filter = new IntentFilter(Intent.ACTION_PACKAGE_ADDED);
//	        filter.addAction(Intent.ACTION_PACKAGE_REMOVED);
//	        filter.addAction(Intent.ACTION_PACKAGE_CHANGED);
//	        filter.addDataScheme("package");
//	        mLoader.getContext().registerReceiver(this, filter);
//	        // Register for events related to sdcard installation.
//	        IntentFilter sdFilter = new IntentFilter();
//	        sdFilter.addAction(IntentCompat.ACTION_EXTERNAL_APPLICATIONS_AVAILABLE);
//	        sdFilter.addAction(IntentCompat.ACTION_EXTERNAL_APPLICATIONS_UNAVAILABLE);
//	        mLoader.getContext().registerReceiver(this, sdFilter);
//	    }
//
//	    @Override 
//	    public void onReceive(Context context, Intent intent) {
//	        // Tell the loader about the change.
//	        mLoader.onContentChanged();
//	    }
//	}

//	/**
//	 * Perform alphabetical comparison of application entry objects.
//	 */
//	public static final Comparator<Scenario> ALPHA_COMPARATOR = new Comparator<Scenario>() {
//	    private final Collator sCollator = Collator.getInstance();
//	    public int compare(Scenario object1, Scenario object2) {
//	        return sCollator.compare(object1.getLabel(), object2.getLabel());
//	    }
//	};

//	/**
//	 * Helper for determining if the configuration has changed in an interesting
//	 * way so we need to rebuild the list.
//	 */
//	public static class InterestingConfigChanges {
//	    final Configuration mLastConfiguration = new Configuration();
//	    int mLastDensity;
//
//	    boolean applyNewConfig(Resources res) {
//	        int configChanges = mLastConfiguration.updateFrom(res.getConfiguration());
//	        boolean densityChanged = mLastDensity != res.getDisplayMetrics().densityDpi;
//	        if (densityChanged || (configChanges&(ActivityInfo.CONFIG_LOCALE
//	                |ActivityInfoCompat.CONFIG_UI_MODE|ActivityInfo.CONFIG_SCREEN_LAYOUT)) != 0) {
//	            mLastDensity = res.getDisplayMetrics().densityDpi;
//	            return true;
//	        }
//	        return false;
//	    }
//	}
	
}



