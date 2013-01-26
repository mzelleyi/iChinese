package sg.gov.nhb.ihuayu.activity;

import sg.gov.nhb.ihuayu.R;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class Utils {
	
	private static final String	TAG	= "iHuayu:Utils";
	
	public static void sendMailToFeedback(Context context, String editText) {
		Log.d(TAG, "[sendMailToFeedback] + Begin");
		String strSubject = null;
		String strBody = null;
		String strBodyText = null;
		String strTitle = null;
		if (MainActivity.mRes != null) {
			Log.d(TAG, "[sendMailToFeedback] do get string");
			strSubject = MainActivity.mRes.getString(R.string.send_mail_subject_text);
			strBody = MainActivity.mRes.getString(R.string.send_mail_body_contact_text);
			strBodyText = "\"" + editText + "\" " + strBody; 
			strTitle = MainActivity.mRes.getString(R.string.send_mail_title_text);
		}
		
		Intent emailIntent = new Intent(Intent.ACTION_SEND); 
		emailIntent.setType("text/plain"); 
		emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"info@mandarin.org.sg"}); 
		emailIntent.putExtra(Intent.EXTRA_SUBJECT, strSubject); 
		emailIntent.putExtra(Intent.EXTRA_TEXT, strBodyText); 
		
		if (context != null) {
			Log.d(TAG, "[sendMailToFeedback] do startActivity");
			context.startActivity(Intent.createChooser(emailIntent, strTitle));
		}
		Log.d(TAG, "[sendMailToFeedback] + End");
	}
}
