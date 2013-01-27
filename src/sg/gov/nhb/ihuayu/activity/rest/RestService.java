/**
 * 
 */
package sg.gov.nhb.ihuayu.activity.rest;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;

import sg.gov.nhb.ihuayu.activity.aes.AESUtils;
import sg.gov.nhb.ihuayu.activity.operation.OperationUtils;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.util.Log;


/**
 * @author lixingwang
 */
public class RestService {
	
	public static final String GET_NUMBER_OF_DOWNLOADS_URL = "http://ihuayu.gistxl.com/smc/WebServices/SMCWCFService.svc/GetNumberOfDownloads";
	public static final String GET_DICTIONARY_UPDATE_URL = "http://ihuayu.gistxl.com/smc/WebServices/SMCWCFService.svc/GetDictionariesV1_1";
	public static final String GET_SCENARIO_UPDATE_URL = "http://ihuayu.gistxl.com/smc/WebServices/SMCWCFService.svc/GetScenariosV1_1";
	private static final String KEY = "NLBkey1111111111";
	private static final String TAG = "iHuayu:DatabaseHelper";
	private static final String AUDIO_DOWNLOAD_URL = "http://ihuayu.gistxl.com/smc/";
	
	public int getNumberIfDownloads(String lastUpdateTime) throws ClientProtocolException, IOException, InvalidKeyException, ParseException {
		long start = System.currentTimeMillis();
		HttpClient client=new DefaultHttpClient();  
		HttpPost httpPost=new HttpPost(GET_NUMBER_OF_DOWNLOADS_URL);  
		StringEntity entity = new StringEntity(getRequestJsonString(lastUpdateTime), "UTF-8");
		entity.setContentEncoding("UTF-8");  
		entity.setContentType("application/json"); 
		httpPost.setEntity(entity);  
		HttpResponse response=client.execute(httpPost); 
//		System.out.println("Wangzai 1111 " + EntityUtils.toString(response.getEntity()));
		long end = System.currentTimeMillis();
		Log.i(TAG, "Get Numbers Time speed: " + (end -start)/1000);
		return Integer.parseInt(EntityUtils.toString(response.getEntity()));
	}
	
	public List<ContentValues> getDictionary(String lastUpdateTime) throws ClientProtocolException, IOException, ParseException, JSONException {
		long start = System.currentTimeMillis();
		HttpClient client=new DefaultHttpClient();  
		HttpPost httpPost=new HttpPost(GET_DICTIONARY_UPDATE_URL);  
		StringEntity entity = new StringEntity(getRequestJsonString(lastUpdateTime), "UTF-8");
		entity.setContentEncoding("UTF-8");  
		entity.setContentType("application/json"); 
		httpPost.setEntity(entity);  
		HttpResponse response=client.execute(httpPost); 
		String result = EntityUtils.toString(response.getEntity()).replace("\\", "");
		String finalResult = result.substring(1, result.length()-1);
		if(finalResult ==null || finalResult.length() <= 0) return null;
		Log.i(TAG, "GetDictionary Result: " + finalResult);
		JSONArray resultArray = new JSONArray(finalResult);
		long end = System.currentTimeMillis();
		Log.i(TAG, "Get Directoray time speed: " + (end -start)/1000);
		return OperationUtils.jsonTileToSqliteContentValuesDictionary(resultArray);
	}
	
	public HashMap<ContentValues, HashMap<ContentValues, List<ContentValues>>> getScenario(String lastUpdateTime) throws ClientProtocolException, IOException, ParseException, JSONException {
		long start = System.currentTimeMillis();
		HttpClient client=new DefaultHttpClient();  
		HttpPost httpPost=new HttpPost(GET_SCENARIO_UPDATE_URL);  
		StringEntity entity = new StringEntity(getRequestJsonString(lastUpdateTime), "UTF-8");
		entity.setContentEncoding("UTF-8");  
		entity.setContentType("application/json"); 
		httpPost.setEntity(entity);  
		HttpResponse response=client.execute(httpPost); 
		String result =  EntityUtils.toString(response.getEntity()).replace("\\", "");
//		Log.d(TAG,"Wangzai 2222 " + result);
//		Log.d(TAG,"respone:"+response);
		String finalResult = result.substring(1, result.length()-1);
		if(finalResult ==null || finalResult.length() <= 0) return null;
		JSONArray resultArray = new JSONArray(finalResult);
		Log.i(TAG, "GetDictionary Result: " + finalResult);
		long end = System.currentTimeMillis();
		Log.i(TAG, "Scenariao time speed: " + (end -start)/1000);
		HashMap<ContentValues, HashMap<ContentValues, List<ContentValues>>> scenariaoDialogKeyWords = OperationUtils.getScenarioFromJsonObject(resultArray);
		return scenariaoDialogKeyWords;
	}
	
	public int getAudioDownload(String audioURL) throws ClientProtocolException, IOException, InvalidKeyException, ParseException {
		HttpClient client=new DefaultHttpClient();  
		HttpPost httpPost=new HttpPost(AUDIO_DOWNLOAD_URL + audioURL);  
//		StringEntity entity = new StringEntity(getRequestJsonString(lastUpdateTime), "UTF-8");
//		entity.setContentEncoding("UTF-8");  
//		entity.setContentType("application/json"); 
//		httpPost.setEntity(entity);  
		HttpResponse response=client.execute(httpPost); 
//		System.out.println("Wangzai 1111 " + EntityUtils.toString(response.getEntity()));
		return Integer.parseInt(EntityUtils.toString(response.getEntity()));
	}
	
	
	public String getRequestJsonString(String lastUpdateTime) throws ParseException {
		return "{\"rLastDownloadDate\":\""+lastUpdateTime+"\",\"rAuthToken\":\""+AESUtils.encription(KEY, getStandardTimeStr())+"\"}";
	}
	
	
	public String getRequestJsonStringForTesting(String lastUpdateTime) throws ParseException {
		return "{\"rLastDownloadDate\":\"2010-04-01T01S01S01\",\"rAuthToken\":\""+AESUtils.encription(KEY, "2010-04-01T01S01S01")+"\"}";
	}
	
	@SuppressLint("SimpleDateFormat")
	private String getStandardTimeStr() throws ParseException {
		DateFormat  format2 = new SimpleDateFormat("yyyyMMddHHmmss");
		Date now = new Date(); 
		return format2.format(now);
	}

}
