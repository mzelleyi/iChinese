
package sg.gov.nhb.ihuayu.activity;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import sg.gov.nhb.ihuayu.activity.db.entity.ScenarioDialog;
import sg.gov.nhb.ihuayu.activity.db.entity.DialogKeywords;
import sg.gov.nhb.ihuayu.activity.db.entity.Scenario;
import sg.gov.nhb.ihuayu.activity.rest.AudioPlayer;
import sg.gov.nhb.ihuayu.view.MyDialogFragment;

import sg.gov.nhb.ihuayu.R;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
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
 */
public class ScenarioDetailFragment extends Fragment {

    private static final String TAG = "iHuayu:ScenarioDetailFragment";
    private static FragmentActivity parentActivity = null;
    private static Scenario mScenario = null;

    private static final int PLAY_DIALOG_AUDIO = 401;
    private static final int SHOW_DOWNLOAD_DIALOG = 402;
    private static final int HIDE_DOWNLOAD_DIALOG = 403;

    // Define Thread Name
    private static final String THREAD_NAME = "ScenarioDetailFragmentThread";
    // The DB Handler Thread
    private HandlerThread mHandlerThread = null;
    // The DB Operation Thread
    private static NonUiHandler mNonUiHandler = null;

    /**
     * Create a new instance of ScenarioFragment
     */
    static ScenarioDetailFragment newInstance(Scenario item) {
        Log.d(TAG, "[newInstance] + Begin");
        ScenarioDetailFragment fragment = new ScenarioDetailFragment();
        mScenario = item;
        return fragment;
    }

    /**
     * When creating, retrieve this parameter from its arguments.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        Log.d(TAG, "[onCreateView] + Begin");
        View v = inflater.inflate(R.layout.scenario_detail_fragment, container, false);
        Log.d(TAG, "[onCreateView] + End");
        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        Log.d(TAG, "[onViewCreated] + Begin");
        // TODO Auto-generated method stub
        super.onViewCreated(view, savedInstanceState);
        parentActivity = this.getActivity();

        // Init Thread
        mHandlerThread = new HandlerThread(THREAD_NAME);
        mHandlerThread.setPriority(Thread.NORM_PRIORITY);
        mHandlerThread.start();
        mNonUiHandler = new NonUiHandler(mHandlerThread.getLooper());

        // Init Item Outline Info
        ImageView leftImg = (ImageView) parentActivity
                .findViewById(R.id.scenario_detail_item_icon_left);
        leftImg.setImageResource(R.drawable.icn_paper_2x);
        if (mScenario != null) {
            TextView TextView1 = (TextView) parentActivity
                    .findViewById(R.id.scenario_detail_item_text_first_line);
            TextView1.setText(mScenario.getTitle_en());
            TextView TextView2 = (TextView) parentActivity
                    .findViewById(R.id.scenario_detail_item_text_second_line);
            TextView2.setText(mScenario.getTitle_cn());
            TextView TextView3 = (TextView) parentActivity
                    .findViewById(R.id.scenario_detail_item_text_third_line);
            TextView3.setText(mScenario.getTitle_py());
        }

        // Set Back Button On Click Listener
        Button backBtn = (Button) parentActivity.findViewById(R.id.scenario_detail_backbtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // TODO Auto-generated method stub
                FragmentManager fm = parentActivity.getSupportFragmentManager();
                fm.popBackStack();
                // Fragment currentFragment =
                // fm.findFragmentByTag(MainActivity.fragment_tag_scenario);
                // if (currentFragment == null) {
                // Log.i(TAG,
                // "[onClick] backBtn : new BookmarkFragment to replace");
                // Fragment newFragment = ScenarioFragment.newInstance();
                // FragmentTransaction ft = fm.beginTransaction();
                // ft.replace(R.id.tab_content_scenario, newFragment,
                // MainActivity.fragment_tag_scenario);
                // ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                // ft.commit();
                // }
            }
        });

        // Add Detail Dialog Fragment
        FragmentManager fm = parentActivity.getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ScenarioDialogFragment list = (ScenarioDialogFragment) fm
                .findFragmentById(R.id.scenario_detail_dialog_listview);
        // Create the list fragment and add it as our sole content.
        if (list == null) {
            Log.d(TAG, "[onViewCreated] new ScenarioDialogFragment, do add");
            list = new ScenarioDialogFragment();
            ft.add(R.id.scenario_detail_dialog_listview, list);
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            // ft.addToBackStack(null);
            ft.commit();
        } else {
            list = new ScenarioDialogFragment();
            Log.d(TAG, "[onViewCreated] used ScenarioDialogFragment, do replace");
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            ft.replace(R.id.scenario_detail_dialog_listview, list);
            // ft.addToBackStack(null);
            ft.commit();
        }
        Log.d(TAG, "[onViewCreated] + End");
    }

    private final Handler mUiHandler = new Handler() {
        DialogFragment downloadDialog = null;

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SHOW_DOWNLOAD_DIALOG: {
                    Log.d(TAG, "[mUihandler handleMessage] SHOW_DOWNLOAD_DIALOG");
                    downloadDialog = MyDialogFragment.newInstance(parentActivity,
                            MyDialogFragment.DIALOG_DOWNLOAD);
                    downloadDialog.show(parentActivity.getSupportFragmentManager(),
                            "dialog_download");
                    break;
                }
                case HIDE_DOWNLOAD_DIALOG: {
                    Log.d(TAG, "[mUihandler handleMessage] HIDE_DOWNLOAD_DIALOG");
                    if (downloadDialog != null) {
                        downloadDialog.dismiss();
                    }
                    break;
                }
                default: {
                    Log.e(TAG, "[mUihandler handleMessage] Something wrong!!!");
                    break;
                }
            }
        }
    };

    /**
     * The NonUiHandler to do DB action
     * 
     * @author kesen
     */
    private final class NonUiHandler extends Handler
    {
        public NonUiHandler(Looper looper)
        {
            super(looper);
            Log.d(TAG, "[NonUihandler] Constructor");
        }

        @Override
        public void handleMessage(Message msg)
        {
            switch (msg.what)
            {
                case PLAY_DIALOG_AUDIO:
                    Log.d(TAG, "[NonUihandler][handleMessage] - PLAY_DIALOG_AUDIO");
                    doPlayAudio((String) msg.obj);
                    break;
                default:
                    Log.d(TAG, "[NonUihandler][handleMessage] Something wrong in handleMessage()");
                    break;
            }
        }

        private void doPlayAudio(String audio) {
            Log.d(TAG, "[NonUihandler][doPlayAudio] + Begin");
            String audioStr = null;
            if (audio != null) {
                audioStr = audio;
            }
            Log.d(TAG, "[NonUihandler][doPlayAudio] audioStr = " + audioStr);

            AudioPlayer mAudioPlayer = AudioPlayer.newInstance();
            try {
                boolean bDownloaded = mAudioPlayer.doCheckDownloaded(audioStr);
                if (bDownloaded) {
                    mAudioPlayer.doPlay(audioStr);
                } else {
                    if (!Utils.hasNetwork(parentActivity))
                        return;
                    if (mUiHandler.hasMessages(SHOW_DOWNLOAD_DIALOG)) {
                        mUiHandler.removeMessages(SHOW_DOWNLOAD_DIALOG);
                    }
                    mUiHandler.sendEmptyMessage(SHOW_DOWNLOAD_DIALOG);
                    boolean result = mAudioPlayer.doDownload(audioStr);
                    if (result) {
                        if (mUiHandler.hasMessages(HIDE_DOWNLOAD_DIALOG)) {
                            mUiHandler.removeMessages(HIDE_DOWNLOAD_DIALOG);
                        }
                        mUiHandler.sendEmptyMessage(HIDE_DOWNLOAD_DIALOG);
                        mAudioPlayer.doPlay(audioStr);
                    } else {
                        Toast.makeText(
                                parentActivity,
                                parentActivity.getResources().getString(
                                        R.string.toast_download_failed), Toast.LENGTH_SHORT).show();
                    }
                }
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            Log.d(TAG, "[NonUihandler][doPlayAudio] + End");
        }
    }

    public static void doPositiveClick() {
        // TODO Auto-generated method stub
        Log.d(TAG, "[doPositiveClick] + Begin");
    }

    public class ScenarioDialogFragment extends ListFragment implements
            LoaderManager.LoaderCallbacks<List<HashMap<ScenarioDialog, List<DialogKeywords>>>> {

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
            HashMap<ScenarioDialog, List<DialogKeywords>> mapItem = mAdapter.getItem(position);

            DialogFragment newFragment = MyDialogFragment.newInstance(parentActivity,
                    MyDialogFragment.SCENARIO_DIALOG, mapItem);
            newFragment.show(parentActivity.getSupportFragmentManager(), "scenario_dialog");
        }

        public Loader<List<HashMap<ScenarioDialog, List<DialogKeywords>>>> onCreateLoader(int id,
                Bundle args) {
            Log.d(TAG, "[ScenarioDialogFragment][onCreateLoader] + Begin");
            // This is called when a new Loader needs to be created. This
            // sample only has one Loader with no arguments, so it is simple.
            return new ScenarioDialogLoader(this.getActivity());
        }

        public void onLoadFinished(
                Loader<List<HashMap<ScenarioDialog, List<DialogKeywords>>>> loader,
                List<HashMap<ScenarioDialog, List<DialogKeywords>>> data) {
            Log.d(TAG, "[ScenarioDialogFragment][onLoadFinished] + Begin");
            // Set the new data in the adapter.
            mAdapter.setData(data);

            // Cancel default divider
            ListView mListView = this.getListView();
            mListView.setDivider(null);
            mListView.setScrollingCacheEnabled(false);

            // The list should now be shown.
            if (this.isResumed()) {
                this.setListShown(true);
            } else {
                this.setListShownNoAnimation(true);
            }
        }

        public void onLoaderReset(Loader<List<HashMap<ScenarioDialog, List<DialogKeywords>>>> loader) {
            Log.d(TAG, "[ScenarioDialogFragment][onLoaderReset] + Begin");
            // Clear the data in the adapter.
            mAdapter.setData(null);
        }
    }

    /**
     * A custom Loader that loads all of the installed applications.
     */
    public static class ScenarioDialogLoader extends
            AsyncTaskLoader<List<HashMap<ScenarioDialog, List<DialogKeywords>>>> {

        List<HashMap<ScenarioDialog, List<DialogKeywords>>> mDialogList = null;

        public ScenarioDialogLoader(Context context) {
            super(context);
        }

        /**
         * This is where the bulk of our work is done. This function is called
         * in a background thread and should generate a new set of data to be
         * published by the loader.
         */
        @Override
        public List<HashMap<ScenarioDialog, List<DialogKeywords>>> loadInBackground() {
            Log.d(TAG, "[ScenarioDialogLoader][loadInBackground] + Begin");

            mDialogList = MainActivity.dbManagerment.getDialogList(mScenario.getTitle_id());

            Log.d(TAG,
                    "[ScenarioDialogLoader][loadInBackground] + Dialog Size = "
                            + mDialogList.size());
            Log.d(TAG, "[ScenarioDialogLoader][loadInBackground] + End");
            return mDialogList;
        }

        /**
         * Called when there is new data to deliver to the client. The super
         * class will take care of delivering it; the implementation here just
         * adds a little more logic.
         */
        @Override
        public void deliverResult(List<HashMap<ScenarioDialog, List<DialogKeywords>>> dialogList) {
            Log.d(TAG, "[ScenarioDialogLoader][deliverResult] + Begin");
            if (this.isReset()) {
                // An async query came in while the loader is stopped. We
                // don't need the result.
                if (dialogList != null) {
                    this.onReleaseResources(dialogList);
                }
            }

            if (dialogList != null) {
                Log.d(TAG, "[ScenarioDialogLoader][deliverResult] List Size=" + dialogList.size());
                List<HashMap<ScenarioDialog, List<DialogKeywords>>> oldDialogList = dialogList;
                mDialogList = dialogList;

                if (this.isStarted()) {
                    // If the Loader is currently started, we can immediately
                    // deliver its results.
                    super.deliverResult(dialogList);
                }

                // At this point we can release the resources associated with
                // 'oldDialogList' if needed; now that the new result is
                // delivered we
                // know that it is no longer in use.
                if (dialogList != null) {
                    this.onReleaseResources(oldDialogList);
                }
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
        public void onCanceled(List<HashMap<ScenarioDialog, List<DialogKeywords>>> scenarioList) {
            Log.d(TAG, "[ScenarioDialogLoader][onCanceled] + Begin");
            super.onCanceled(scenarioList);

            // At this point we can release the resources associated with
            // 'scenarioList' if needed.
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

            // At this point we can release the resources associated with
            // 'scenarioList' if needed.
            if (mDialogList != null) {
                this.onReleaseResources(mDialogList);
                mDialogList = null;
            }
        }

        /**
         * Helper function to take care of releasing resources associated with
         * an actively loaded data set.
         */
        protected void onReleaseResources(
                List<HashMap<ScenarioDialog, List<DialogKeywords>>> scenario) {
            // For a simple List<> there is nothing to do. For something
            // like a Cursor, we would close it here.
        }
    }

    public class ScenarioDialogAdapter extends
            ArrayAdapter<HashMap<ScenarioDialog, List<DialogKeywords>>> {
        private ScenarioDialog dialogItem = null;
        private List<DialogKeywords> keyWordList = null;
        private final LayoutInflater mInflater;

        public ScenarioDialogAdapter(Context context) {
            // super(context, android.R.layout.simple_list_item_2);
            super(context, R.layout.scenario_detail_listitem_female);
            mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        public void setData(List<HashMap<ScenarioDialog, List<DialogKeywords>>> data) {
            Log.d(TAG, "[ScenarioDialogAdapter][setData] + Begin");
            this.clear();

            if (data != null) {
                Log.d(TAG, "[ScenarioDialogAdapter][setData] Size" + data.size());
                for (HashMap<ScenarioDialog, List<DialogKeywords>> dialog : data) {
                    this.add(dialog);
                }
            }
            Log.d(TAG, "[ScenarioDialogAdapter][setData] + End");
        }

        // @Override
        // public int getItemViewType(int position) {
        // // TODO Auto-generated method stub
        // final HashMap<Dialog, List<DialogKeywords>> mapItem =
        // this.getItem(position);
        // Iterator<Dialog> iterator = mapItem.keySet().iterator();
        // while(iterator.hasNext()) {
        // dialogItem = (Dialog) iterator.next();
        // }
        // if (dialogItem.getGender().equalsIgnoreCase("f")) {
        // Log.d(TAG, "[getItemViewType], return VIEW_TYPE_FEMALE");
        // return VIEW_TYPE_FEMALE;
        // } else {
        // Log.d(TAG, "[getItemViewType], return VIEW_TYPE_MALE");
        // return VIEW_TYPE_MALE;
        // }
        // //return super.getItemViewType(position);
        // }

        @Override
        public int getViewTypeCount() {
            Log.d(TAG, "[getViewTypeCount], return 2");
            // TODO Auto-generated method stub
            return 2;
            // return super.getViewTypeCount();
        }

        /**
         * Populate new items in the list.
         */
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Log.d(TAG, "[ScenarioDetailAdapter][getView] + pos=" + position);
            final int pos = position;

            final HashMap<ScenarioDialog, List<DialogKeywords>> mapItem = this.getItem(position);
            Iterator<ScenarioDialog> iterator = mapItem.keySet().iterator();
            while (iterator.hasNext()) {
                dialogItem = (ScenarioDialog) iterator.next();
            }
            keyWordList = mapItem.get(dialogItem);

            // String dialogGender = null;
            // String dialogAudio = null;
            SpannableString spanStr = null;

            if (convertView == null) {
                if (dialogItem.getGender().equalsIgnoreCase("f")) {
                    Log.d(TAG, "[ScenarioDialogAdapter][getView] new female item");
                    convertView = mInflater.inflate(R.layout.scenario_detail_listitem_female,
                            parent, false);
                } else {
                    Log.d(TAG, "[ScenarioDialogAdapter][getView] new male item");
                    convertView = mInflater.inflate(R.layout.scenario_detail_listitem_male, parent,
                            false);
                }
            }

            if (convertView != null) {
                if (dialogItem != null) {
                    TextView firstLine = (TextView) convertView
                            .findViewById(R.id.scenario_dialog_text_first_line);
                    TextView secondLine = (TextView) convertView
                            .findViewById(R.id.scenario_dialog_text_second_line);
                    TextView nameLine = (TextView) convertView
                            .findViewById(R.id.scenario_dialog_textview_name);
                    nameLine.setText(dialogItem.getNarrator());
                    secondLine.setText(dialogItem.getSentence_py());

                    String sectenceStr = dialogItem.getSentence();
                    spanStr = new SpannableString(sectenceStr);
                    Log.d(TAG, "[getView] Info (1)Name = " + dialogItem.getNarrator());
                    Log.d(TAG, "[getView] Info (2)secondLine = " + dialogItem.getSentence_py());
                    Log.d(TAG, "[getView] Info (3)firstLine = " + dialogItem.getSentence());
                    for (int i = 0; i < keyWordList.size(); i++) {
                        DialogKeywords keyItem = keyWordList.get(i);
                        String keyStr = keyItem.getDest_keyword();
                        Log.d(TAG, "[getView] keyStr = " + keyStr);
                        int firstIndex = sectenceStr.indexOf(keyStr);
                        int lastIndex = firstIndex + keyStr.length();
                        Log.d(TAG, "[getView] firstIndex = " + firstIndex + ",lastIndex = "
                                + lastIndex);
                        spanStr.setSpan(new ForegroundColorSpan(Color.RED), firstIndex, lastIndex,
                                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }
                    firstLine.setText(spanStr);

                    ImageView speakIcon = (ImageView) convertView
                            .findViewById(R.id.scenario_dialog_img_speaker);
                    speakIcon.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v)
                        {
                            // TODO Auto-generated method stub
                            final HashMap<ScenarioDialog, List<DialogKeywords>> mapItem = getItem(pos);
                            Iterator<ScenarioDialog> iterator = mapItem.keySet().iterator();
                            while (iterator.hasNext()) {
                                dialogItem = (ScenarioDialog) iterator.next();
                            }
                            String strAudio = dialogItem.getAudio();
                            Log.d(TAG, "[onClick] dialogAudio = " + strAudio);
                            if (mNonUiHandler != null) {
                                if (mNonUiHandler.hasMessages(PLAY_DIALOG_AUDIO)) {
                                    mNonUiHandler.removeMessages(PLAY_DIALOG_AUDIO);
                                }
                                Message msg = Message.obtain(mNonUiHandler, PLAY_DIALOG_AUDIO,
                                        strAudio);
                                mNonUiHandler.sendMessage(msg);
                            }
                        }
                    });
                }
            }
            return convertView;
        }
    }
}
