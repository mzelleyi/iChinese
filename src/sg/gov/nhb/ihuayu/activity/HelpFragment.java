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
public class HelpFragment extends Fragment {

    private static final String TAG = "iHuayu:HelpFragment";

    /**
     * Create a new instance of HelpFragment
     */
    static HelpFragment newInstance() {
        Log.d(TAG, "[newInstance] + Begin");
        HelpFragment fragment = new HelpFragment();
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
        View v = inflater.inflate(R.layout.help_fragment, container, false);
        Log.d(TAG, "[onCreateView] + End");
        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        Log.d(TAG, "[onViewCreated] + Begin");
        // TODO Auto-generated method stub
        super.onViewCreated(view, savedInstanceState);
        WebView webView = (WebView) view.findViewById(R.id.fragment_help_webView);
        webView.loadUrl("file:///android_asset/help.html");
        WebSettings websetting = webView.getSettings();
        websetting.setBuiltInZoomControls(true);

        final FragmentActivity activity = this.getActivity();
        Button backBtn = (Button) activity.findViewById(R.id.fragment_help_title_button);
        backBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // TODO Auto-generated method stub
                FragmentManager fm = activity.getSupportFragmentManager();
                // fm.popBackStack();
                Fragment currentFragment = fm.findFragmentByTag(MainActivity.fragment_tag_info);
                if (currentFragment == null) {
                	Log.i(TAG, "[onViewCreated] new InfoFragment then replace tab_info");
                    Fragment newFragment = InfoFragment.newInstance();
                    FragmentTransaction ft = fm.beginTransaction();
                    ft.replace(R.id.tab_content_info, newFragment, MainActivity.fragment_tag_info);
                    ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                    ft.commit();
                }
            }
        });
        Log.d(TAG, "[onViewCreated] + End");
    }
}
