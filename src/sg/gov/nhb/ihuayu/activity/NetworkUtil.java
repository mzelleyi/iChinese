/**
 * 
 */
package sg.gov.nhb.ihuayu.activity;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * @author lixingwang
 *
 */
public class NetworkUtil {
	
	/**
	 * Judge whether the current mobile network is available
	 * @param context
	 * @return
	 */
	public static boolean isNetworkAvailable(Context context)  
	{  
	    ConnectivityManager mConnMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);  
	    NetworkInfo mWifi = mConnMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);  
	    NetworkInfo mMobile = mConnMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);  
	    boolean flag = false;  
	    if((mWifi != null)  && ((mWifi.isAvailable())))  
	    {  
	        if((mWifi.isConnected()) || (mMobile.isConnected()))  
	        {  
	            flag = true;  
	        }  
	    }  
	    return flag;  
	}  
}
