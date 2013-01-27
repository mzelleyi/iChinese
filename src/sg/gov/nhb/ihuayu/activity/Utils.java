package sg.gov.nhb.ihuayu.activity;

import sg.gov.nhb.ihuayu.R;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Log;

/**
 * 
 * @author Kesen
 *
 */
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
	
	public static SpannableString getSpanableText(String orginText, String hightlightText) {
		Log.d(TAG, "[getSpanableText] + Begin");
		if (TextUtils.isEmpty(orginText)) {
			Log.w(TAG, "[getSpanableText] orginText is empty");
			return null;
		}  
		SpannableString spannableString = new SpannableString(orginText);
		
		if (hightlightText.equals("")) {
			Log.w(TAG, "[getSpanableText] hightlightText is empty");
			return spannableString;
		} 
		
		int startPos = orginText.toLowerCase().indexOf(hightlightText.toLowerCase());
		if (startPos == -1) {
			Log.w(TAG, "[getSpanableText] startPos is -1");
			return spannableString;  
		} 
		
		int endPos = startPos + hightlightText.length();
		spannableString.setSpan(new ForegroundColorSpan(Color.RED),
	    		startPos, endPos, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
	    Log.d(TAG, "[getSpanableText] + End");
		return spannableString;  
	} 
}
