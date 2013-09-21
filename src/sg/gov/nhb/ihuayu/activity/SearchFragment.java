/**
 * 
 */

package sg.gov.nhb.ihuayu.activity;

import java.util.ArrayList;
import java.util.List;

import sg.gov.nhb.ihuayu.activity.db.entity.Dictionary;
import sg.gov.nhb.ihuayu.activity.db.entity.FuzzyResult;
import sg.gov.nhb.ihuayu.activity.db.entity.QueryType;
import sg.gov.nhb.ihuayu.activity.db.entity.Scenario;
import sg.gov.nhb.ihuayu.activity.rest.AudioPlayer;
import sg.gov.nhb.ihuayu.view.MyDialogFragment;

import sg.gov.nhb.ihuayu.R;

import android.content.Context;
import android.content.SharedPreferences;
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
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author Kesen
 */
public class SearchFragment extends Fragment {

    private static final String TAG = "iHuayu:SearchFragment";
    private static final int DELAY_REFRESH_LIST_VIEW = 200;
    private static final int MSG_DO_SUGGEST_SEARCH = 201;
    private static final int MSG_REFRESH_SUGGEST_LISTVIEW = 202;
    private static final int MSG_DO_FUZZY_SEARCH = 203;
    private static final int MSG_REFRESH_FUZZY_RESULT = 204;
    private static final int MSG_PLAY_AUDIO = 205;
    private static final int SHOW_DOWNLOAD_DIALOG = 206;
    private static final int HIDE_DOWNLOAD_DIALOG = 207;
    private static final int VIEW_TYPE_SUGGEST = 0;
    private static final int VIEW_TYPE_FUZZY = 1;

    private static final String PREFERENCE_SEARCH_TYPE = "mSearchType";

    // Define Thread Name
    private static final String THREAD_NAME = "SearchFragmentThread";
    // The DB Handler Thread
    private HandlerThread mHandlerThread = null;
    // The DB Operation Thread
    private static NonUiHandler mNonUiHandler = null;
    private static FragmentActivity parentActivity = null;
    private static boolean bFuzzyMode = false;

    private SearchListAdapter mAdapter = null;
    private SearchScenarioAdapter mScenarioAdapter = null;
    private ListView mListView = null;
    private ListView mScenarioListView = null;

    private QueryType mSearchKeyType = QueryType.EN;
    private EditText mEditText = null;
    private Button mBtnLanguage = null;
    private ImageView mPromptImg = null;
    private ImageView mClearImg = null;
    private LinearLayout mFuzzyHintLayout = null;
    private TextView mFuzzySuggestHint = null;
    private TextView mEmptySuggestHint = null;
    private TextView mDicDivider = null;
    private TextView mSceDivider = null;
    private List<Dictionary> mDicList = new ArrayList<Dictionary>();
    private List<Scenario> mSceList = new ArrayList<Scenario>();
    private InputMethodManager mInputMethodManager = null;

    /**
     * Create a new instance of SearchFragment
     */
    static SearchFragment newInstance() {
        Log.d(TAG, "[newInstance] + Begin");
        SearchFragment fragment = new SearchFragment();

        // Supply num input as an argument.
        // Bundle args = new Bundle();
        // args.putInt("num", num);
        // f.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "[onCreate] + Begin");
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        // mNum = getArguments() != null ? getArguments().getInt("num") : 1;
    }

    /**
     * When creating, retrieve this parameter from its arguments.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "[onCreateView] + Begin");
        View v = inflater.inflate(R.layout.search_fragment, container, false);
        Log.d(TAG, "[onCreateView] + End");
        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        Log.d(TAG, "[onViewCreated] + Begin");
        // TODO Auto-generated method stub
        super.onViewCreated(view, savedInstanceState);

        parentActivity = this.getActivity();
        mInputMethodManager = (InputMethodManager) parentActivity
                .getSystemService(Context.INPUT_METHOD_SERVICE);

        // Init Thread
        mHandlerThread = new HandlerThread(THREAD_NAME);
        mHandlerThread.setPriority(Thread.NORM_PRIORITY);
        mHandlerThread.start();
        mNonUiHandler = new NonUiHandler(mHandlerThread.getLooper());

        mBtnLanguage = (Button) parentActivity.findViewById(R.id.search_bar_btn);
        // Tag "EN" represent input type is English while "CN" represent
        // Chinese.
        mBtnLanguage.setBackgroundResource(R.drawable.btn_english);
        mBtnLanguage.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                String text = mEditText.getText().toString().trim();
                if (mSearchKeyType == QueryType.EN) {
                    mBtnLanguage.setBackgroundResource(R.drawable.btn_chinese);
                    mSearchKeyType = QueryType.CN;
                    if (text != null && text.length() > 0) {
                        sendSuggentSearchMsg(text, DELAY_REFRESH_LIST_VIEW);
                        mClearImg.setVisibility(View.VISIBLE);
                        mPromptImg.setVisibility(View.GONE);
                        mListView.setVisibility(View.VISIBLE);
                        bFuzzyMode = false;
                    }
                } else {
                    mBtnLanguage.setBackgroundResource(R.drawable.btn_english);
                    mSearchKeyType = QueryType.EN;
                    if (text != null && text.length() > 2) {
                        sendSuggentSearchMsg(text, DELAY_REFRESH_LIST_VIEW);
                        mPromptImg.setVisibility(View.GONE);
                        bFuzzyMode = false;
                    } else {
                        // mAdapter.clear();
                        // mAdapter.notifyDataSetChanged();
                        mListView.setVisibility(View.GONE);
                        mPromptImg.setVisibility(View.VISIBLE);
                    }
                    mClearImg.setVisibility(View.VISIBLE);
                }
            }
        });

        mPromptImg = (ImageView) parentActivity
                .findViewById(R.id.search_fragment_prompt_image);
        mClearImg = (ImageView) parentActivity
                .findViewById(R.id.search_bar_edit_clear);
        mFuzzyHintLayout = (LinearLayout) parentActivity
                .findViewById(R.id.search_fuzzy_hint_layout);
        mDicDivider = (TextView) parentActivity.findViewById(R.id.search_fuzzy_divider_dic);
        mSceDivider = (TextView) parentActivity.findViewById(R.id.search_fuzzy_divider_sce);
        mEditText = (EditText) parentActivity.findViewById(R.id.search_bar_edit_text);
        mFuzzySuggestHint = (TextView) parentActivity
                .findViewById(R.id.search_fuzzy_hint_text_suggest);
        mFuzzySuggestHint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (null != mEditText) {
                    Utils.sendMailToFeedback(parentActivity, mEditText.getText().toString());
                }
            }
        });

        mEmptySuggestHint = (TextView) parentActivity
                .findViewById(R.id.search_empty_view_text_second_line);
        mEmptySuggestHint.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (null != mEditText) {
                    Editable editable = mEditText.getText();
                    String feedbackStr = "";
                    if (editable != null) {
                        feedbackStr = editable.toString();
                    }
                    Utils.sendMailToFeedback(parentActivity, feedbackStr);
                }
            }
        });

        mAdapter = new SearchListAdapter(parentActivity);
        mListView = (ListView) parentActivity.findViewById(R.id.search_result_list);
        mListView.setScrollingCacheEnabled(false);
        mListView.setAdapter(mAdapter);
        mListView.setEmptyView(parentActivity.findViewById(R.id.search_fragment_emptyview));
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                // TODO Auto-generated method stub
                Log.d(TAG, "[onItemClick] + arg 2=" + arg2 + ",arg 3=" + arg3);
                mInputMethodManager.hideSoftInputFromWindow(mEditText.getWindowToken(), 0);

                FragmentManager fm = parentActivity.getSupportFragmentManager();
                Fragment newFragment = SearchDetailFragment.newInstance(mAdapter.getItem(arg2));
                FragmentTransaction ft = fm.beginTransaction();
                ft.replace(R.id.tab_content_search, newFragment,
                        MainActivity.fragment_tag_search_detail);
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                ft.addToBackStack(null);
                ft.commitAllowingStateLoss();
            }
        });

        mScenarioListView = (ListView) parentActivity.findViewById(R.id.search_result_scenario);
        mScenarioAdapter = new SearchScenarioAdapter(parentActivity);
        mScenarioListView.setScrollingCacheEnabled(false);
        mScenarioListView.setAdapter(mScenarioAdapter);
        mScenarioListView.setEmptyView(parentActivity.findViewById(R.id.search_fragment_emptyview));
        // mScenarioListView.setCacheColorHint(R.color.black_normal);
        mScenarioListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                // TODO Auto-generated method stub
                Log.d(TAG, "[onItemClick] + arg 2=" + arg2 + ",arg 3=" + arg3);
                FragmentManager fm = parentActivity.getSupportFragmentManager();
                Fragment newFragment = ScenarioDetailFragment.newInstance(mScenarioAdapter
                        .getItem(arg2));
                FragmentTransaction ft = fm.beginTransaction();
                ft.replace(R.id.tab_content_search, newFragment);
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                ft.addToBackStack(null);
                ft.commitAllowingStateLoss();
            }
        });

        mEditText.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO Auto-generated method stub
                Log.d(TAG, "[onTextChanged] + Begin");
                Log.d(TAG, "[onTextChanged] start=" + start + ",before=" + before + ",count="
                        + count);
                String text = s.toString().trim();
                Log.d(TAG, "[onTextChanged] text string is :" + text);
                if (text == null || text.equals("")) {
                    sendSuggentSearchMsg(null, DELAY_REFRESH_LIST_VIEW);
                    bFuzzyMode = false;
                    mClearImg.setVisibility(View.GONE);
                    mPromptImg.setVisibility(View.VISIBLE);
                    mListView.setVisibility(View.GONE);
                } else {
                    if (start - before != 0) {
                        Log.i(TAG, "[onTextChanged] set fuzzyMode = false");
                        bFuzzyMode = false;
                    }
                    if (bFuzzyMode) {
                        Log.i(TAG, "[onTextChanged] is bFuzzyMode, do sendFuzzySearchMsg");
                        sendFuzzySearchMsg(text, 0);
                    } else {
                        Log.i(TAG, "[onTextChanged] isn't bFuzzyMode, do sendSuggentSearchMsg");
                        if (mSearchKeyType == QueryType.EN) {
                            if (text.length() > 2) {
                                sendSuggentSearchMsg(text, DELAY_REFRESH_LIST_VIEW);
                                mPromptImg.setVisibility(View.GONE);
                                mListView.setVisibility(View.VISIBLE);
                            }
                            mClearImg.setVisibility(View.VISIBLE);
                        } else {
                            sendSuggentSearchMsg(text, DELAY_REFRESH_LIST_VIEW);
                            mClearImg.setVisibility(View.VISIBLE);
                            mPromptImg.setVisibility(View.GONE);
                            mListView.setVisibility(View.VISIBLE);
                        }
                        bFuzzyMode = false;
                    }
                }
                Log.d(TAG, "[onTextChanged] + End");
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // TODO Auto-generated method stub
                Log.d(TAG, "[beforeTextChanged] + Begin");
            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
                Log.d(TAG, "[afterTextChanged] + Begin");
            }
        });
        mEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                // TODO Auto-generated method stub
                Log.d(TAG, "[onEditorAction] + Begin ,actionId = " + actionId);
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_SEARCH
                        || actionId == EditorInfo.IME_ACTION_UNSPECIFIED
                        || actionId == EditorInfo.IME_ACTION_DONE
                        || actionId == EditorInfo.IME_ACTION_GO) {
                    Log.d(TAG, "[onEditorAction] -> IME_ACTION_SEARCH ");
                    String searchKey = mEditText.getText().toString();
                    Log.d(TAG, "[onEditorAction] searchKey = " + searchKey);
                    if (null != searchKey && !searchKey.trim().isEmpty()) {
                        sendFuzzySearchMsg(searchKey, 300);
                        bFuzzyMode = true;
                    }
                    handled = true;
                }
                return handled;
            }
        });

        mClearImg.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Log.d(TAG, "[onClick] mClearImg + Begin");
                mEditText.getText().clear();
            }
        });
    }

    /**
     * Upon being resumed we can retrieve the current state. This allows us to
     * update the state if it was changed at any time while paused.
     */
    @Override
    public void onResume() {
        Log.d(TAG, "[onResume] + Begin");
        super.onResume();
        SharedPreferences prefs = this.getActivity().getPreferences(Context.MODE_PRIVATE);
        String restoredText = prefs.getString(PREFERENCE_SEARCH_TYPE, null);
        Log.i(TAG, "[onResume] read preference is " + restoredText);
        if (restoredText != null) {
            if (restoredText.equalsIgnoreCase("en2sc")) {
                mSearchKeyType = QueryType.EN;
                mBtnLanguage.setBackgroundResource(R.drawable.btn_english);
            } else {
                mSearchKeyType = QueryType.CN;
                mBtnLanguage.setBackgroundResource(R.drawable.btn_chinese);
            }
        } else {
            // Set first launch query type
            Log.i(TAG, "[onResume] read preference is null, set default EN");
            mSearchKeyType = QueryType.EN;
            mBtnLanguage.setBackgroundResource(R.drawable.btn_english);
        }
        Log.d(TAG, "[onResume] + End");
    }

    /**
     * Any time we are paused we need to save away the current state, so it will
     * be restored correctly when we are resumed.
     */
    @Override
    public void onPause() {
        Log.d(TAG, "[onPause] + Begin");
        super.onPause();
        SharedPreferences.Editor editor = this.getActivity().getPreferences(Context.MODE_PRIVATE)
                .edit();
        editor.putString(PREFERENCE_SEARCH_TYPE, mSearchKeyType.getName());
        editor.apply();
        Log.i(TAG, "[onPause] write preference is " + mSearchKeyType.getName());
        Log.d(TAG, "[onPause] + End");
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        Log.d(TAG, "[onSaveInstanceState] + Begin");
        // TODO Auto-generated method stub
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        Log.d(TAG, "[onViewStateRestored] + Begin");
        // TODO Auto-generated method stub
        super.onViewStateRestored(savedInstanceState);
    }

    private void sendSuggentSearchMsg(String key, long delayTime) {
        if (mNonUiHandler.hasMessages(MSG_DO_SUGGEST_SEARCH)) {
            mNonUiHandler.removeMessages(MSG_DO_SUGGEST_SEARCH);
        }
        Message msg = mNonUiHandler.obtainMessage(MSG_DO_SUGGEST_SEARCH);
        msg.obj = key;
        if (delayTime > 0) {
            mNonUiHandler.sendMessageDelayed(msg, delayTime);
        } else {
            mNonUiHandler.sendMessage(msg);
        }
    }

    private void sendFuzzySearchMsg(String key, long delayTime) {
        if (mUiHandler.hasMessages(MSG_DO_FUZZY_SEARCH)) {
            mUiHandler.removeMessages(MSG_DO_FUZZY_SEARCH);
        }
        mUiHandler.sendEmptyMessage(MSG_DO_FUZZY_SEARCH);

        if (mNonUiHandler.hasMessages(MSG_DO_FUZZY_SEARCH)) {
            mNonUiHandler.removeMessages(MSG_DO_FUZZY_SEARCH);
        }
        Message msg = mNonUiHandler.obtainMessage(MSG_DO_FUZZY_SEARCH);
        msg.obj = key;
        if (delayTime > 0) {
            mNonUiHandler.sendMessageDelayed(msg, delayTime);
        } else {
            mNonUiHandler.sendMessage(msg);
        }
    }

    private Handler mUiHandler = new Handler() {
        private DialogFragment searchDialog = null;
        private DialogFragment downloadDialog = null;

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SearchFragment.MSG_REFRESH_SUGGEST_LISTVIEW: {
                    Log.d(TAG, "[mUihandler][MSG_REFRESH_SUGGEST_LISTVIEW]");
                    @SuppressWarnings("unchecked")
                    List<Dictionary> dicList = (List<Dictionary>) msg.obj;

                    mDicList.clear();
                    for (Dictionary object : dicList) {
                        mDicList.add(object);
                    }

                    mFuzzyHintLayout.setVisibility(View.GONE);
                    mDicDivider.setVisibility(View.GONE);
                    mSceDivider.setVisibility(View.GONE);
                    mScenarioListView.setVisibility(View.GONE);

                    // bFuzzyMode = false;
                    mAdapter.setData(mDicList);
                    mAdapter.notifyDataSetChanged();
                    break;
                }
                case SearchFragment.MSG_DO_FUZZY_SEARCH: {
                    Log.d(TAG, "[mUihandler][MSG_DO_FUZZY_SEARCH]");
                    mInputMethodManager.hideSoftInputFromWindow(mEditText.getWindowToken(), 0);
                    searchDialog = MyDialogFragment.newInstance(parentActivity,
                            MyDialogFragment.DO_SEARCH_DB);
                    searchDialog.show(parentActivity.getSupportFragmentManager(),
                            "dialog_search_db");
                    break;
                }
                case SearchFragment.MSG_REFRESH_FUZZY_RESULT: {
                    Log.d(TAG, "[mUihandler][MSG_REFRESH_FUZZY_RESULT]");
                    searchDialog.dismissAllowingStateLoss();

                    FuzzyResult fuzzyResult = (FuzzyResult) msg.obj;
                    List<Dictionary> dicList = fuzzyResult.getDictionaryList();
                    List<Scenario> sceList = fuzzyResult.getScenarioList();
                    int dicListSize = dicList.size();
                    int sceListSize = sceList.size();
                    Log.i(TAG, "[mUihandler] FuzzyResult Dictionary size = "
                            + dicListSize);
                    Log.i(TAG, "[mUihandler] FuzzyResult Scenario size = "
                            + sceListSize);

                    // if (sceListSize > 0 || dicListSize > 0) {
                    mPromptImg.setVisibility(View.GONE);
                    mListView.setVisibility(View.VISIBLE);
                    // } else {
                    // mPromptImg.setVisibility(View.VISIBLE);
                    // }

                    // Handle dictionary search result
                    mDicList.clear();
                    if (dicListSize > 0) {
                        for (Dictionary object : dicList) {
                            mDicList.add(object);
                        }
                    }
                    Log.d(TAG, "[mUihandler] mDicList size = " + mDicList.size());
                    if (mDicList.size() > 0) {
                        mDicDivider.setVisibility(View.VISIBLE);
                        if (!fuzzyResult.isExactResult()) {
                            mFuzzyHintLayout.setVisibility(View.VISIBLE);
                        } else {
                            mFuzzyHintLayout.setVisibility(View.GONE);
                        }
                    } else {
                        mDicDivider.setVisibility(View.GONE);
                        mFuzzyHintLayout.setVisibility(View.GONE);
                    }
                    // bFuzzyMode = true;
                    mAdapter.setData(mDicList);
                    mAdapter.notifyDataSetChanged();

                    // Handle scenario search result
                    mSceList.clear();
                    if (sceListSize > 0) {
                        for (Scenario object : sceList) {
                            mSceList.add(object);
                        }
                    }
                    Log.d(TAG, "[mUihandler] mSceList size = " + mSceList.size());
                    if (mSceList.size() > 0) {
                        mSceDivider.setVisibility(View.VISIBLE);
                        mScenarioListView.setVisibility(View.VISIBLE);
                        mScenarioAdapter.setData(mSceList);
                        mScenarioAdapter.notifyDataSetChanged();
                    } else {
                        mSceDivider.setVisibility(View.GONE);
                        if (mDicList.size() > 0) {
                            mScenarioListView.setVisibility(View.GONE);
                        } else {
                            mScenarioListView.setVisibility(View.VISIBLE);
                            mScenarioAdapter.setData(mSceList);
                            mScenarioAdapter.notifyDataSetChanged();
                        }
                    }
                    break;
                }
                case SHOW_DOWNLOAD_DIALOG: {
                    Log.d(TAG, "[mUihandler handleMessage] SHOW_DOWNLOAD_DIALOG");
                    downloadDialog = MyDialogFragment.newInstance(parentActivity,
                            MyDialogFragment.DIALOG_DOWNLOAD);
                    downloadDialog.show(
                            parentActivity.getSupportFragmentManager(),
                            "dialog_download");
                    break;
                }
                case HIDE_DOWNLOAD_DIALOG: {
                    Log.d(TAG, "[mUihandler handleMessage] HIDE_DOWNLOAD_DIALOG");
                    if (downloadDialog != null) {
                        downloadDialog.dismissAllowingStateLoss();
                    }
                    break;
                }
            }
        }
    };

    /**
     * The NonUihandler to do DB action
     * 
     * @author kesen
     */
    private final class NonUiHandler extends Handler {

        public NonUiHandler(Looper looper) {
            super(looper);
            Log.d(TAG, "[NonUihandler] Constructor");
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SearchFragment.MSG_DO_SUGGEST_SEARCH: {
                    Log.d(TAG, "[NonUihandler] handle msg [MSG_DO_SUGGEST_SEARCH]");
                    String suggestSearchStr = (String) msg.obj;
                    doSuggestSearch(suggestSearchStr);
                    break;
                }
                case SearchFragment.MSG_DO_FUZZY_SEARCH: {
                    Log.d(TAG, "[NonUihandler] handle msg [MSG_DO_FUZZY_SEARCH]");
                    String fuzzySearchStr = (String) msg.obj;
                    doFuzzySearch(fuzzySearchStr);
                    break;
                }
                case SearchFragment.MSG_PLAY_AUDIO: {
                    Log.d(TAG, "[NonUihandler] handle msg [MSG_PLAY_AUDIO]");
                    String audioStr = (String) msg.obj;
                    doPlayAudio(audioStr);
                    break;
                }
                default:
                    Log.w(TAG, "[NonUihandler][handleMessage] Something wrong in handleMessage()");
                    break;
            }
        }

        private void doSuggestSearch(String searchStr) {
            Log.d(TAG, "[NonUihandler][doSuggestSearch] + Begin");
            Log.d(TAG, "[NonUihandler][doSuggestSearch] keyStr = " + searchStr);
            if (MainActivity.dbManagerment != null) {
                List<Dictionary> dicList = MainActivity.dbManagerment.searchDictionary(mSearchKeyType,
                        searchStr);
                Log.d(TAG, "[NonUihandler][doSuggestSearch] dicList.size = " + dicList.size());
                if (mUiHandler != null) {
                    if (mUiHandler.hasMessages(MSG_REFRESH_SUGGEST_LISTVIEW)) {
                        mUiHandler.removeMessages(MSG_REFRESH_SUGGEST_LISTVIEW);
                    }
                    Message msg1 = mUiHandler.obtainMessage(MSG_REFRESH_SUGGEST_LISTVIEW);
                    msg1.obj = dicList;
                    mUiHandler.sendMessage(msg1);
                }
            }
            Log.d(TAG, "[NonUihandler][doSuggestSearch] + End");
        }

        private void doFuzzySearch(String searchStr) {
            Log.d(TAG, "[NonUihandler][doFuzzySearch] + Begin");
            Log.d(TAG, "[NonUihandler][doFuzzySearch] searchStr = " + searchStr);
            if (MainActivity.dbManagerment != null) {
                FuzzyResult fuzzyResult = MainActivity.dbManagerment.fuzzySearchDictionary(
                        mSearchKeyType,
                        searchStr);
                Log.d(TAG,
                        "[NonUihandler][doFuzzySearch] result is extact result = "
                                + fuzzyResult.isExactResult());
                if (mUiHandler != null) {
                    if (mUiHandler.hasMessages(MSG_REFRESH_FUZZY_RESULT)) {
                        mUiHandler.removeMessages(MSG_REFRESH_FUZZY_RESULT);
                    }
                    Message msg2 = mUiHandler.obtainMessage(MSG_REFRESH_FUZZY_RESULT);
                    msg2.obj = fuzzyResult;
                    mUiHandler.sendMessage(msg2);
                }
            }
            Log.d(TAG, "[NonUihandler][doFuzzySearch] + End");
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
                                        R.string.toast_download_failed),
                                Toast.LENGTH_SHORT).show();
                    }
                }
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            Log.d(TAG, "[NonUihandler][doPlayAudio] + End");
        }
    }

    public static class SearchListAdapter extends ArrayAdapter<Dictionary> {
        private LayoutInflater mInflater = null;

        public SearchListAdapter(Context context) {
            super(context, android.R.layout.simple_list_item_2);
            mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        public void setData(List<Dictionary> data) {
            this.clear();
            if (data != null && data.size() > 0) {
                // Dummy First Item For Divider
                for (Dictionary str : data) {
                    this.add(str);
                }
            }
        }

        @Override
        public int getItemViewType(int position) {
            // TODO Auto-generated method stub
            if (bFuzzyMode) {
                return VIEW_TYPE_FUZZY;
            } else {
                return VIEW_TYPE_SUGGEST;
            }
            // return super.getItemViewType(position);
        }

        @Override
        public int getViewTypeCount() {
            // TODO Auto-generated method stub
            return 2;
            // return super.getViewTypeCount();
        }

        /**
         * Populate new items in the list.
         */
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view;
            Log.d(TAG, "[getView] pos = " + position);
            if (convertView == null) {
                if (getItemViewType(position) == VIEW_TYPE_FUZZY) {
                    view = mInflater.inflate(R.layout.bookmark_list_item, parent, false);
                    view.setBackgroundColor(parentActivity.getResources().getColor(
                            R.color.green_dark));
                } else {
                    view = mInflater.inflate(R.layout.search_suggest_item, parent, false);
                }
            } else {
                view = convertView;
            }

            final Dictionary item = getItem(position);
            final int pos = position;
            if (null != view) {
                if (getItemViewType(position) == VIEW_TYPE_SUGGEST) {
                    ((TextView) view.findViewById(R.id.search_result_listitem_text))
                            .setText(item.getKeyword());
                } else {
                    ((TextView) view.findViewById(R.id.bookmark_listitem_text_first_line))
                            .setText(item
                                    .getKeyword());
                    ((TextView) view.findViewById(R.id.bookmark_listitem_text_second_line))
                            .setText(item
                                    .getDestiontion());
                    ((TextView) view.findViewById(R.id.bookmark_listitem_text_third_line))
                            .setText(item
                                    .getChineser_tone_py());
                    ((TextView) view.findViewById(R.id.bookmark_listitem_text_type)).setText(item
                            .getDic_catagory());
                    ((ImageView) view.findViewById(R.id.bookmark_listitem_icon_speaker))
                            .setOnClickListener(new View.OnClickListener() {

                                @Override
                                public void onClick(View v) {
                                    // TODO Auto-generated method stub
                                    final Dictionary item = getItem(pos);
                                    String strAudio = item.getChinese_audio();
                                    Log.d(TAG, "[onClick] Chinese_audio = " + strAudio);
                                    if (mNonUiHandler != null) {
                                        if (mNonUiHandler.hasMessages(MSG_PLAY_AUDIO)) {
                                            mNonUiHandler.removeMessages(MSG_PLAY_AUDIO);
                                        }
                                        Message msg = Message.obtain(mNonUiHandler, MSG_PLAY_AUDIO,
                                                strAudio);
                                        mNonUiHandler.sendMessage(msg);
                                    }
                                }
                            });
                }
            }
            return view;
        }
    }

    public static class SearchScenarioAdapter extends ArrayAdapter<Scenario> {
        private LayoutInflater mInflater = null;

        public SearchScenarioAdapter(Context context) {
            super(context, android.R.layout.simple_list_item_2);
            mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        public void setData(List<Scenario> data) {
            this.clear();
            if (data != null && data.size() > 0) {
                // Dummy First Item For Divider
                for (Scenario str : data) {
                    this.add(str);
                }
            }
        }

        /**
         * Populate new items in the list.
         */
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view;
            Log.d(TAG, "[getView] pos = " + position);
            if (convertView == null) {
                view = mInflater.inflate(R.layout.scenario_list_item, parent, false);
            } else {
                view = convertView;
            }

            final Scenario item = this.getItem(position);
            if (view != null && item != null) {
                // ImageView listImageLeft = (ImageView)
                // convertView.findViewById(R.id.scenario_listitem_icon_left);
                TextView listTextView1 = (TextView) view
                        .findViewById(R.id.scenario_listitem_text_first_line);
                TextView listTextView2 = (TextView) view
                        .findViewById(R.id.scenario_listitem_text_second_line);
                TextView listTextView3 = (TextView) view
                        .findViewById(R.id.scenario_listitem_text_third_line);
                ImageView listImageRight = (ImageView) view
                        .findViewById(R.id.scenario_listitem_icon_right);
                listTextView1.setText(item.getTitle_en());
                listTextView2.setText(item.getTitle_cn());
                listTextView3.setText(item.getTitle_py());
                listImageRight.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub
                        FragmentManager fm = parentActivity.getSupportFragmentManager();
                        Fragment newFragment = ScenarioDetailFragment.newInstance(item);
                        FragmentTransaction ft = fm.beginTransaction();
                        ft.replace(R.id.tab_content_search, newFragment);
                        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                        ft.addToBackStack(null);
                        ft.commitAllowingStateLoss();
                    }
                });
            }
            return view;
        }
    }
}
