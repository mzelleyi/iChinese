package com.ihuayu.activity;

import java.io.File;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.ihuayu.R;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.IntentCompat;
import android.support.v4.content.Loader;
import android.support.v4.content.pm.ActivityInfoCompat;
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
		
		FragmentActivity activity = this.getActivity();
		FragmentManager fm = activity.getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();

        // Create the list fragment and add it as our sole content.
        if (fm.findFragmentById(R.id.fragment_scenario_listview) == null) {
        	ScenarioListFragment list = new ScenarioListFragment();
        	ft.add(R.id.fragment_scenario_listview, list).commit();
        }
	}
	
	public static class ScenarioListFragment extends ListFragment implements 
		LoaderManager.LoaderCallbacks<List<ScenarioEntry>> {
		//OnQueryTextListenerCompat mOnQueryTextListenerCompat;
		// This is the Adapter being used to display the list's data.
		ScenarioListAdapter mAdapter;
		// If non-null, this is the current filter the user has provided.
		String mCurFilter;
		
		@Override
		public void onActivityCreated(Bundle savedInstanceState) {
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
			// Place an action bar item for searching.
			menu.close();
		}

		@Override
		public void onListItemClick(ListView l, View v, int position, long id) {
			// Insert desired behavior here.
			Log.i(TAG, "[onListItemClick] Item clicked: " + id);
		}

		public Loader<List<ScenarioEntry>> onCreateLoader(int id, Bundle args) {
			// This is called when a new Loader needs to be created. This
			// sample only has one Loader with no arguments, so it is simple.
			return new ScenarioListLoader(this.getActivity());
		}

		public void onLoadFinished(Loader<List<ScenarioEntry>> loader,
				List<ScenarioEntry> data) {
			// Set the new data in the adapter.
			mAdapter.setData(data);

			// The list should now be shown.
			if (isResumed()) {
				this.setListShown(true);
			} else {
				this.setListShownNoAnimation(true);
			}
		}

		public void onLoaderReset(Loader<List<ScenarioEntry>> loader) {
			// Clear the data in the adapter.
			mAdapter.setData(null);
		}
	}
	
	/**
	 * This class holds the per-item data in our Loader.
	 */
	public static class ScenarioEntry {
	    public ScenarioEntry(ScenarioListLoader loader, ApplicationInfo info) {
	        mLoader = loader;
	        mInfo = info;
	        mApkFile = new File(info.sourceDir);
	    }

	    public ApplicationInfo getApplicationInfo() {
	        return mInfo;
	    }

	    public String getLabel() {
	        return mLabel;
	    }

	    public Drawable getIcon() {
	        if (mIcon == null) {
	            if (mApkFile.exists()) {
	                mIcon = mInfo.loadIcon(mLoader.mPm);
	                return mIcon;
	            } else {
	                mMounted = false;
	            }
	        } else if (!mMounted) {
	            // If the Scenario wasn't mounted but is now mounted, reload
	            // its icon.
	            if (mApkFile.exists()) {
	                mMounted = true;
	                mIcon = mInfo.loadIcon(mLoader.mPm);
	                return mIcon;
	            }
	        } else {
	            return mIcon;
	        }

	        return mLoader.getContext().getResources().getDrawable(
	                android.R.drawable.sym_def_app_icon);
	    }

	    @Override 
	    public String toString() {
	        return mLabel;
	    }

	    void loadLabel(Context context) {
	        if (mLabel == null || !mMounted) {
	            if (!mApkFile.exists()) {
	                mMounted = false;
	                mLabel = mInfo.packageName;
	            } else {
	                mMounted = true;
	                CharSequence label = mInfo.loadLabel(context.getPackageManager());
	                mLabel = label != null ? label.toString() : mInfo.packageName;
	            }
	        }
	    }

	    private final ScenarioListLoader mLoader;
	    private final ApplicationInfo mInfo;
	    private final File mApkFile;
	    private String mLabel;
	    private Drawable mIcon;
	    private boolean mMounted;
	}

	/**
	 * A custom Loader that loads all of the installed applications.
	 */
	public static class ScenarioListLoader extends AsyncTaskLoader<List<ScenarioEntry>> {
	    final InterestingConfigChanges mLastConfig = new InterestingConfigChanges();
	    final PackageManager mPm;

	    List<ScenarioEntry> mApps;
	    PackageIntentReceiver mPackageObserver;

	    public ScenarioListLoader(Context context) {
	        super(context);

	        // Retrieve the package manager for later use; note we don't
	        // use 'context' directly but instead the save global application
	        // context returned by getContext().
	        mPm = getContext().getPackageManager();
	    }

	    /**
	     * This is where the bulk of our work is done.  This function is
	     * called in a background thread and should generate a new set of
	     * data to be published by the loader.
	     */
	    @Override public List<ScenarioEntry> loadInBackground() {
	        // Retrieve all known applications.
	        List<ApplicationInfo> apps = mPm.getInstalledApplications(
	                PackageManager.GET_UNINSTALLED_PACKAGES |
	                PackageManager.GET_DISABLED_COMPONENTS);
	        if (apps == null) {
	            apps = new ArrayList<ApplicationInfo>();
	        }

	        final Context context = getContext();

	        // Create corresponding array of entries and load their labels.
	        List<ScenarioEntry> entries = new ArrayList<ScenarioEntry>(apps.size());
	        for (int i=0; i<apps.size(); i++) {
	        	ScenarioEntry entry = new ScenarioEntry(this, apps.get(i));
	            entry.loadLabel(context);
	            entries.add(entry);
	        }

	        // Sort the list.
	        Collections.sort(entries, ALPHA_COMPARATOR);

	        // Done!
	        return entries;
	    }

	    /**
	     * Called when there is new data to deliver to the client.  The
	     * super class will take care of delivering it; the implementation
	     * here just adds a little more logic.
	     */
	    @Override public void deliverResult(List<ScenarioEntry> apps) {
	        if (isReset()) {
	            // An async query came in while the loader is stopped.  We
	            // don't need the result.
	            if (apps != null) {
	                onReleaseResources(apps);
	            }
	        }
	        List<ScenarioEntry> oldApps = apps;
	        mApps = apps;

	        if (isStarted()) {
	            // If the Loader is currently started, we can immediately
	            // deliver its results.
	            super.deliverResult(apps);
	        }

	        // At this point we can release the resources associated with
	        // 'oldApps' if needed; now that the new result is delivered we
	        // know that it is no longer in use.
	        if (oldApps != null) {
	            onReleaseResources(oldApps);
	        }
	    }

	    /**
	     * Handles a request to start the Loader.
	     */
	    @Override protected void onStartLoading() {
	        if (mApps != null) {
	            // If we currently have a result available, deliver it
	            // immediately.
	            deliverResult(mApps);
	        }

	        // Start watching for changes in the app data.
	        if (mPackageObserver == null) {
	            mPackageObserver = new PackageIntentReceiver(this);
	        }

	        // Has something interesting in the configuration changed since we
	        // last built the app list?
	        boolean configChange = mLastConfig.applyNewConfig(getContext().getResources());

	        if (takeContentChanged() || mApps == null || configChange) {
	            // If the data has changed since the last time it was loaded
	            // or is not currently available, start a load.
	            forceLoad();
	        }
	    }

	    /**
	     * Handles a request to stop the Loader.
	     */
	    @Override protected void onStopLoading() {
	        // Attempt to cancel the current load task if possible.
	        cancelLoad();
	    }

	    /**
	     * Handles a request to cancel a load.
	     */
	    @Override public void onCanceled(List<ScenarioEntry> apps) {
	        super.onCanceled(apps);

	        // At this point we can release the resources associated with 'apps'
	        // if needed.
	        onReleaseResources(apps);
	    }

	    /**
	     * Handles a request to completely reset the Loader.
	     */
	    @Override protected void onReset() {
	        super.onReset();

	        // Ensure the loader is stopped
	        onStopLoading();

	        // At this point we can release the resources associated with 'apps'
	        // if needed.
	        if (mApps != null) {
	            onReleaseResources(mApps);
	            mApps = null;
	        }

	        // Stop monitoring for changes.
	        if (mPackageObserver != null) {
	            getContext().unregisterReceiver(mPackageObserver);
	            mPackageObserver = null;
	        }
	    }

	    /**
	     * Helper function to take care of releasing resources associated
	     * with an actively loaded data set.
	     */
	    protected void onReleaseResources(List<ScenarioEntry> apps) {
	        // For a simple List<> there is nothing to do.  For something
	        // like a Cursor, we would close it here.
	    }
	}

	public static class ScenarioListAdapter extends ArrayAdapter<ScenarioEntry> {
	    private final LayoutInflater mInflater;

	    public ScenarioListAdapter(Context context) {
	        super(context, android.R.layout.simple_list_item_2);
	        mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	    }

	    public void setData(List<ScenarioEntry> data) {
	        clear();
	        if (data != null) {
	            for (ScenarioEntry scenarioEntry : data) {
	                add(scenarioEntry);
	            }
	        }
	    }

	    /**
	 * Populate new items in the list.
	 */
	    @Override public View getView(int position, View convertView, ViewGroup parent) {
	        View view;

	        if (convertView == null) {
	            view = mInflater.inflate(R.layout.list_item_icon_text, parent, false);
	        } else {
	            view = convertView;
	        }

	        ScenarioEntry item = getItem(position);
	        ((ImageView)view.findViewById(R.id.icon)).setImageDrawable(item.getIcon());
	        ((TextView)view.findViewById(R.id.text)).setText(item.getLabel());

	        return view;
	    }
	}

	/**
	 * Helper class to look for interesting changes to the installed apps
	 * so that the loader can be updated.
	 */
	public static class PackageIntentReceiver extends BroadcastReceiver {
	    final ScenarioListLoader mLoader;

	    public PackageIntentReceiver(ScenarioListLoader loader) {
	        mLoader = loader;
	        IntentFilter filter = new IntentFilter(Intent.ACTION_PACKAGE_ADDED);
	        filter.addAction(Intent.ACTION_PACKAGE_REMOVED);
	        filter.addAction(Intent.ACTION_PACKAGE_CHANGED);
	        filter.addDataScheme("package");
	        mLoader.getContext().registerReceiver(this, filter);
	        // Register for events related to sdcard installation.
	        IntentFilter sdFilter = new IntentFilter();
	        sdFilter.addAction(IntentCompat.ACTION_EXTERNAL_APPLICATIONS_AVAILABLE);
	        sdFilter.addAction(IntentCompat.ACTION_EXTERNAL_APPLICATIONS_UNAVAILABLE);
	        mLoader.getContext().registerReceiver(this, sdFilter);
	    }

	    @Override public void onReceive(Context context, Intent intent) {
	        // Tell the loader about the change.
	        mLoader.onContentChanged();
	    }
	}

	/**
	 * Perform alphabetical comparison of application entry objects.
	 */
	public static final Comparator<ScenarioEntry> ALPHA_COMPARATOR = new Comparator<ScenarioEntry>() {
	    private final Collator sCollator = Collator.getInstance();
	    public int compare(ScenarioEntry object1, ScenarioEntry object2) {
	        return sCollator.compare(object1.getLabel(), object2.getLabel());
	    }
	};

	/**
	 * Helper for determining if the configuration has changed in an interesting
	 * way so we need to rebuild the app list.
	 */
	public static class InterestingConfigChanges {
	    final Configuration mLastConfiguration = new Configuration();
	    int mLastDensity;

	    boolean applyNewConfig(Resources res) {
	        int configChanges = mLastConfiguration.updateFrom(res.getConfiguration());
	        boolean densityChanged = mLastDensity != res.getDisplayMetrics().densityDpi;
	        if (densityChanged || (configChanges&(ActivityInfo.CONFIG_LOCALE
	                |ActivityInfoCompat.CONFIG_UI_MODE|ActivityInfo.CONFIG_SCREEN_LAYOUT)) != 0) {
	            mLastDensity = res.getDisplayMetrics().densityDpi;
	            return true;
	        }
	        return false;
	    }
	}
}



