package cn.springmvc.service;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Utility {

	public static Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
	
	public static String createJsonMsg(Object status, Object message, Object body){
		Map<String, Object> ret = new HashMap<String, Object>();
		ret.put("status", status);
		ret.put("message", message);
		ret.put("body", body);
		return gson.toJson(ret);
	}
	
	public static String createJsonMsg(Object status, Object message){
		Map<String, Object> ret = new HashMap<String, Object>();
		ret.put("status", status);
		ret.put("message", message);
		return gson.toJson(ret);
	}
	
	private static double EARTH_RADIUS = 6378.137;//地球半径
	private static double rad(double d)
	{
	   return d * Math.PI / 180.0;
	}

	public static double GetDistance(double lat1, double lng1, double lat2, double lng2)
	{
	   double radLat1 = rad(lat1);
	   double radLat2 = rad(lat2);
	   double a = radLat1 - radLat2;
	   double b = rad(lng1) - rad(lng2);

	   double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a/2),2) +
	    Math.cos(radLat1)*Math.cos(radLat2)*Math.pow(Math.sin(b/2),2)));
	   s = s * EARTH_RADIUS;
	   //s = Math.round(s * 10000) / 10000;
	   return s * 1000;
	}
	
	public static Map<String, Object> post(String url, Map<String, Object> argMap){
		  
		  HttpClient httpClient = new DefaultHttpClient();
		  HttpPost post = new HttpPost(url);
		  		  
		  String json = new Gson().toJson(argMap);
		  StringEntity se = new StringEntity(json.toString(), "UTF-8"); 		  
		  se.setContentEncoding(new BasicHeader(HTTP.CONTENT_ENCODING, "UTF-8"));
	      se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json;charset=utf-8"));
	      post.setEntity(se);
	      
	      Map<String, Object> resultMap = new HashMap<String, Object>();
	      try {
	          HttpResponse response = httpClient.execute(post);                   
	          StatusLine status = response.getStatusLine();	          	          
	          int code = status.getStatusCode();
	          resultMap.put("status", code);
	          if(code < 200 || code >=300){
	        	  resultMap.put("message", "request fail");
	          }else{
	        	  HttpEntity entity = response.getEntity();
	        	  String body = EntityUtils.toString(entity);
	        	  resultMap.put("body", body);
	        	  resultMap.put("message", "request success");
	          }                            
	                    
	      } catch (ClientProtocolException e) {
	          e.printStackTrace();
	          post.abort();
	      } catch (IOException e) {
	          e.printStackTrace();
	          post.abort();
	      } finally{
	    	  httpClient.getConnectionManager().shutdown();
	    	  
	      }
	      return resultMap;
	}
	
	public static void updateObjectField(Object item, String className,  Map<String, Object> args){
		Method[] methods = null;
		try {
			methods = Class.forName(className).getMethods();
		} catch (SecurityException | ClassNotFoundException e1) {
			e1.printStackTrace();
		}
		for(int i = 0; i < methods.length; i++){
			String methodName = methods[i].getName();
			if(!methodName.substring(0, 3).equals("set"))
				continue;
			String fieldInMethod = methodName.substring(3).toLowerCase();
			if(args.containsKey(fieldInMethod)){
				try {
					methods[i].invoke(item, args.get(fieldInMethod));
				} catch (IllegalAccessException | IllegalArgumentException
						| InvocationTargetException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	static public String createToken(){
		return UUID.randomUUID().toString() + "-" + new Date().getTime();
	}
}
