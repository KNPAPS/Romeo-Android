package kr.go.KNPA.Romeo.Util;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.Gson;


import android.util.Log;

public class Connection {
	public final static int HTTP_OK = 200;
	
	public final static int TYPE_GET = 0;
	public final static int TYPE_POST = 1;
	public final static int TYPE_UPDATE = 2;
	public final static int TYPE_DELETE = 3;
	
	public final static int DATATYPE_JSON = 0;
	public final static int DATATYPE_TEXT = 1;
	public final static int DATATYPE_XML = 2;
	public final static int DATATYPE_JSONP = 3;
	public final static int DATATYPE_SCRIPT = 4;
	
	public final static int RETURNTYPE_JSON = 0;
	public final static int RETURNTYPE_JSONPARSED = 1;
	public final static int RETURNTYPE_JSONSTRING = 2;
	public final static int RETURNTYPE_TEXT = 3;
	public final static int RETURNTYPE_XML = 4;
	
	public final static String HOST_URL = "http://172.16.7.52/Projects/CI/index.php";
	public URL url = null;
	public String userId = null;
	public String password = null;
	public boolean async = false;
	
	public int timeout = 10000;
	
	public int type = TYPE_GET;
	public int dataType = DATATYPE_JSON;
	
	private HttpURLConnection conn = null;

	public String data = null;
	public String response = null;

	public int request() {//throws IOException {
		try {
			conn = (HttpURLConnection)url.openConnection();
		} catch (IOException e) {
			RuntimeException re = new RuntimeException(e);
			throw re;
		}

		conn.setUseCaches(false);

		String methodType = null;
		switch(type) {
		case TYPE_GET 	: 	methodType = "GET"; break;
		case TYPE_POST 	:	methodType = "POST"; break;
		case TYPE_UPDATE	:	methodType = "UPDATE"; break;
		case TYPE_DELETE :	methodType = "DELETE"; break;
		}
		try {
			conn.setRequestMethod(methodType);
		} catch (ProtocolException e) {
			RuntimeException re = new RuntimeException(e);
			throw re;
		}
		
		conn.setConnectTimeout(timeout);
		conn.setReadTimeout(timeout);
		conn.setAllowUserInteraction(true);
		
		
		String mimeType = null;
		switch(type) {
		case DATATYPE_JSON 	: 	mimeType = "application/json"; break;
		case DATATYPE_TEXT 	:	mimeType = "text/plain"; break;
		case DATATYPE_XML	:	mimeType = "application/xml"; break;
		case DATATYPE_JSONP :	mimeType = "application/javascript"; break;
		case DATATYPE_SCRIPT :	mimeType = "application/javascript"; break;
		}
		
		mimeType = "application/x-www-form-urlencoded;charset=UTF-8";
		conn.setRequestProperty("Cache-Control", "no-cache");
		conn.setRequestProperty("Content-type", mimeType);
		conn.setRequestProperty("Accept", mimeType);
		
		
		
		conn.setDoInput(true);
		

		
		if(data != null) {
			
			StringBuffer sb = new StringBuffer();
			
			Gson gson = new Gson();
			HashMap<String, Object> hmap = gson.fromJson(data, HashMap.class);
			Set keyset = hmap.keySet();
			Object[] hKeys = keyset.toArray();
			
			for(int i=0; i<hKeys.length; i++) {
				String key = (String)hKeys[i];
				Object value = hmap.get(key);
				
				sb.append(key).append("=").append(value.toString());//gson.toJson(value));
				if(i != hKeys.length-1) {
					sb.append("&");
				}
			}

			conn.setDoOutput(true);

			OutputStream _os;
			try {
				_os = conn.getOutputStream();
			} catch (IOException e) {
				RuntimeException re = new RuntimeException(e);
				throw re;
			}
			OutputStreamWriter _osw;
			try {
				_osw = new OutputStreamWriter(_os,"UTF-8");
			} catch (UnsupportedEncodingException e) {
				RuntimeException re = new RuntimeException(e);
				throw re;
			}
			PrintWriter ps = new PrintWriter(_osw);
			ps.write(sb.toString());
			ps.flush();		
//			os = conn.getOutputStream();
//			os.write(data.getBytes());
//			os.flush();
			ps.close();
		}

		
			
		int responseCode;
		try {
			responseCode = conn.getResponseCode();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			RuntimeException re = new RuntimeException(e);
			throw re;
		}

		StringBuffer resp = new StringBuffer();
		if(responseCode == HttpURLConnection.HTTP_OK) {
			String line;
			
			InputStream _is;
			try {
				_is = conn.getInputStream();
			} catch (IOException e) {
				RuntimeException re = new RuntimeException(e);
				throw re;
			}
			InputStreamReader _isr;
			try {
				_isr = new InputStreamReader(_is, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				RuntimeException re = new RuntimeException(e);
				throw re;
			}
			BufferedReader br = new BufferedReader(_isr);
			
			try {
				while ((line = br.readLine() ) != null) {
					//System.out.println(line);
					resp.append(line);
				}
			} catch (IOException e) {
				RuntimeException re = new RuntimeException(e);
				throw re;
			}
			
			try {
				br.close();
			} catch (IOException e) {
				RuntimeException re = new RuntimeException(e);
				throw re;
			}
			
//			is = conn.getInputStream();
//			baos = new ByteArrayOutputStream();
//			byte[] byteBuffer = new byte[1024];
//			byte[] byteData = null;
//			int nLength = 0;
//			while((nLength = is.read(byteBuffer, 0, byteBuffer.length)) != -1) {
//				baos.write(byteBuffer, 0, nLength);
//			}
//			byteData = baos.toByteArray();
//			response = new String(byteData);
			//is.close();
			response = resp.toString();
			//Log.v("CONNECTION","Got JSON DATA : " + response);

		}
		
		
		conn.disconnect();
		
		return responseCode;
	}
	
	public String getResponse() {
		
		return response;
	}
	
	public JSONObject getJSON() {
		JSONObject json = null;
		try {
			json = new JSONObject(response);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return json;
	}
	/*
	public HashMap parseToHashMap() {
		
		long startTime = System.currentTimeMillis();
		
		Gson gson = new Gson();
		HashMap hmap = gson.fromJson(response, HashMap.class);
		
		long endTime = System.currentTimeMillis();
		Log.v("PARSE_TIME_DIFFERENCE", endTime-startTime+"ms, startTime:"+startTime+" endTime:"+endTime);
		
		return hmap;
	}*/
	public Connection() {}
	
	public static class Builder {
		private String url = null;
		private String userId = null;
		private String password = null;
		private boolean async = false;
		private int timeout = 10000;
		private int type = 0;
		private HashMap<String,?> data = null;
		private String jsonData = null;
		private int dataType = 0;
		
		public Builder url(String _url) {
			this.url = _url;
			return this;
		}
		public Builder userId(String _userId) {
			this.userId = _userId;
			return this;
		}
		public Builder password(String _password) {
			this.password = _password;
			return this;
		}

		public Builder async(boolean _async) {
			this.async = _async;
			return this;
		}

		public Builder type(int _type) {
			this.type = _type;
			return this;
		}
		public Builder data(HashMap<String,?> _data) {
			this.data = _data;
			this.jsonData = null;
			return this;
		}
		public Builder data(String jsonData) {
			this.jsonData = jsonData;
			this.data = null;
			return this;
		}
		public Builder dataType(int _dataType) {
			this.dataType = _dataType;
			return this;
		}
		public Builder timeout(int _timeout) {
			this.timeout = _timeout;
			return this;
		}
		public Connection build() {
			Connection connection = new Connection();
			Gson gson = new Gson();
			
			connection.userId = this.userId;
			connection.password = this.password;
			connection.async = this.async;
			connection.type = this.type;
			if(jsonData != null) {
				connection.data = jsonData;
			} else if(data != null) {
				connection.data = gson.toJson(this.data);
			}
			connection.dataType = this.dataType;
			connection.timeout = this.timeout;
			
			if(type == TYPE_GET && connection.data != null) {
				StringBuffer sb = new StringBuffer();
				
				
				//HashMap<String, Object> hmap = gson.fromJson(data, HashMap.class);
				HashMap<String, ?> hmap = data;
				Set keyset = hmap.keySet();
				String[] hKeys = (String[])keyset.toArray();
				
				for(int i=0; i<hKeys.length; i++) {
					String key = hKeys[i];
					
					Object value = hmap.get(key);
					
					sb.append(key).append("=").append(gson.toJson(value));
					if(i != hKeys.length-1) {
						sb.append("&");
					}
				}
				
				try {	connection.url = new URL(this.url + "?" + sb.toString());	} 
				catch (MalformedURLException e) {	e.printStackTrace();	}

				connection.data = null;
			} else {
				try {	connection.url = new URL(this.url);	} 
				catch (MalformedURLException e) {	e.printStackTrace();	}
			}
			
			return connection;
		}
	}
	
}
