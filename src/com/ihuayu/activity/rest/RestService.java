/**
 * 
 */
package com.ihuayu.activity.rest;

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

import android.annotation.SuppressLint;
import android.content.ContentValues;

import com.ihuayu.activity.aes.AESUtils;
import com.ihuayu.activity.operation.OperationUtils;

/**
 * @author lixingwang
 */
public class RestService {
	
	public static final String GET_NUMBER_OF_DOWNLOADS_URL = "http://ihuayu.gistxl.com/smc/WebServices/SMCWCFService.svc/GetNumberOfDownloads";
	public static final String GET_DICTIONARY_UPDATE_URL = "http://ihuayu.gistxl.com/smc/WebServices/SMCWCFService.svc/GetDictionariesV1_1";
	public static final String GET_SCENARIO_UPDATE_URL = "http://ihuayu.gistxl.com/smc/WebServices/SMCWCFService.svc/GetScenariosV1_1";
	private static final String KEY = "NLBkey1111111111";
	
	public int getNumberIfDownloads(String lastUpdateTime) throws ClientProtocolException, IOException, InvalidKeyException, ParseException {
		HttpClient client=new DefaultHttpClient();  
		HttpPost httpPost=new HttpPost(GET_NUMBER_OF_DOWNLOADS_URL);  
		StringEntity entity = new StringEntity(getRequestJsonString(lastUpdateTime), "UTF-8");
		entity.setContentEncoding("UTF-8");  
		entity.setContentType("application/json"); 
		httpPost.setEntity(entity);  
		HttpResponse response=client.execute(httpPost); 
//		System.out.println("Wangzai 1111 " + EntityUtils.toString(response.getEntity()));
		return Integer.parseInt(EntityUtils.toString(response.getEntity()));
	}
	
	public List<ContentValues> getDictionary(String lastUpdateTime) throws ClientProtocolException, IOException, ParseException, JSONException {
		HttpClient client=new DefaultHttpClient();  
		HttpPost httpPost=new HttpPost(GET_DICTIONARY_UPDATE_URL);  
		StringEntity entity = new StringEntity(getRequestJsonString(lastUpdateTime), "UTF-8");
		entity.setContentEncoding("UTF-8");  
		entity.setContentType("application/json"); 
		httpPost.setEntity(entity);  
		HttpResponse response=client.execute(httpPost); 
		String result = EntityUtils.toString(response.getEntity()).replace("\\", "");
		String finalResult = result.substring(1, result.length()-1);
		JSONArray resultArray = new JSONArray(finalResult);
		return OperationUtils.jsonTileToSqliteContentValuesDictionary(resultArray);
	}
	
	public HashMap<ContentValues, HashMap<ContentValues, List<ContentValues>>> getScenario(String lastUpdateTime) throws ClientProtocolException, IOException, ParseException, JSONException {
		HttpClient client=new DefaultHttpClient();  
		HttpPost httpPost=new HttpPost(GET_SCENARIO_UPDATE_URL);  
		StringEntity entity = new StringEntity(getRequestJsonStringForTesting(lastUpdateTime), "UTF-8");
		entity.setContentEncoding("UTF-8");  
		entity.setContentType("application/json"); 
		httpPost.setEntity(entity);  
		HttpResponse response=client.execute(httpPost); 
		String result =  EntityUtils.toString(response.getEntity()).replace("\\", "");
		System.out.println("Wangzai 2222 " + result);
		System.out.println(response);
		String finalResult = result.substring(1, result.length()-1);
		JSONArray resultArray = new JSONArray(finalResult);
		
		HashMap<ContentValues, HashMap<ContentValues, List<ContentValues>>> scenariaoDialogKeyWords = OperationUtils.getScenarioFromJsonObject(resultArray);
		return scenariaoDialogKeyWords;
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
