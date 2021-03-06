/**
 * 
 */

package sg.gov.nhb.ihuayu.activity;

import sg.gov.nhb.ihuayu.R;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;

/**
 * @author Kesen
 */
public class InfoFragment extends Fragment {

    private static final String TAG = "iHuayu:InfoFragment";

    /**
     * Create a new instance of InfoFragment
     */
    static InfoFragment newInstance() {
        Log.d(TAG, "[newInstance] + Begin");
        InfoFragment fragment = new InfoFragment();
        Log.d(TAG, "[newInstance] + End");
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "[onCreate] + Begin");
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        Log.d(TAG, "[onCreate] + End");
    }

    /**
     * When creating, retrieve this parameter from its arguments.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        Log.d(TAG, "[onCreateView] + Begin");
        View v = inflater.inflate(R.layout.info_fragment, container, false);
        Log.d(TAG, "[onCreateView] + End");
        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        Log.d(TAG, "[onViewCreated] + Begin");
        // TODO Auto-generated method stub
        super.onViewCreated(view, savedInstanceState);
        WebView webView = (WebView) view.findViewById(R.id.fragment_info_webView);
        webView.loadUrl("file:///android_asset/about.html");
        WebSettings websetting = webView.getSettings();
        websetting.setBuiltInZoomControls(true);

        final FragmentActivity activity = this.getActivity();
        Button helpBtn = (Button) activity.findViewById(R.id.fragment_info_title_button);
        helpBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // TODO Auto-generated method stub
            	Log.d(TAG, "[onViewCreated] new HelpFragment to replace tab_info");
                FragmentManager fm = activity.getSupportFragmentManager();
                Fragment newFragment = HelpFragment.newInstance();
                FragmentTransaction ft = fm.beginTransaction();
                ft.replace(R.id.tab_content_info, newFragment, MainActivity.fragment_tag_help);
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                ft.commitAllowingStateLoss();
            }
        });
        Log.d(TAG, "[onViewCreated] + End");
    }
}
