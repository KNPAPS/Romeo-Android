//package kr.go.KNPA.Romeo.GCM;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//
//import org.json.JSONArray;
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import android.content.Context;
//import android.util.Log;
//
//import kr.go.KNPA.Romeo.Base.*;
//import kr.go.KNPA.Romeo.Connection.Connection;
//import kr.go.KNPA.Romeo.Connection.Payload;
//import kr.go.KNPA.Romeo.Member.User;
//import kr.go.KNPA.Romeo.Member.Department;
//import kr.go.KNPA.Romeo.Member.MemberManager;
//import kr.go.KNPA.Romeo.Util.*;
//
//public class GCMMessageSender {
//
//	/* -------------
//	 * 		Payload
//	 * -------------
//	 * event		: String	: "event0 : subEvent : subSubEvnet.."
//	 * sender		: long		:  발신자의 DB상의 idx값
//	 * receivers	: long[]	:  수신자들의 DB상의 idx값의 모임
//	 * X roomCode		: String	: "senderIdx : departedTS"
//	 * message		: Object
//	 */	
//	
//	
//
//	public static final String MESSAGE_RECEIVED = "MESSAGE:RECEIVED";
//	public static final String MESSAGE_DEPARTED = "MESSAGE:DEPARTED";
//	public static final String TAG = "GCMMessageSender";
//	
//	public GCMMessageSender() {
//	}
//	
//	
//	public static boolean setMessageChecked(Context context, int type, long idx) {
//		String json = "{\"type\":"+type+",\"idx\":"+idx+",\"user\":"+UserInfo.getUserIdx(context)+"}";
//		Connection conn = new Connection.Builder()
//										.dataType(Connection.DATATYPE_JSON)
//										.type(Connection.TYPE_POST)
//										.url(Connection.HOST_URL + "/message/setMessageChecked")
//										.data(json)
//										.build();
//		String result a= null;
//		int requestCode = conn.request();
//		if(requestCode == Connection.HTTP_OK) {
//			result = conn.getResponse();
//		} else {
//			return false;
//		}
//		
//		if(result == null) return false;
//		
//		JSONObject jo=null;
//		try {
//			 jo = new JSONObject(result);
//		} catch (JSONException e) {
//			return false;
//		}
//		
//		if(jo == null) return false;
//		
//		boolean status = false;
//		try {
//			status = (jo.getInt("status") == 1?true:false);
//		} catch (JSONException e) {
//			return false;
//		}
//		
//		return status;
//	}
//
//	public static String requestUncheckers(int type, long idx) {
//		String json = "{type:"+type+",idx:"+idx+"}";
//		Connection conn = new Connection.Builder()
//										.url(Connection.HOST_URL + "/message/getUncheckers")
//										.type(Connection.TYPE_GET)
//										.dataType(Connection.DATATYPE_JSON)
//										.data(json)
//										.build();
//		int responseCode = conn.request();
//		String response = null;
//		if(responseCode == Connection.HTTP_OK) {
//			response = conn.getResponse();
//		}
//		
//		if(response == null) return null;
//		
//		JSONArray _uncheckers= null;
//		try {
//			JSONObject jo = new JSONObject(response);
//			_uncheckers = jo.getJSONArray("uncheckers");
//		} catch (JSONException e) {
//			return null;
//		}
//		
//		
//		// TODO : if TSs need?
//		/*
//		JSONArray _TSs= null;
//		try {
//			JSONObject jo = new JSONObject(response);
//			_TSs = jo.getJSONArray("TSs");
//		} catch (JSONException e) {
//			return null;
//		}
//		*/
//		if(_uncheckers == null) return null;
//		return _uncheckers.toString();
//	}
//	
//	public static long sendMessage(Message message) {
////TODO		Payload payload = new Payload.Builder()
////									.message(message)
////									.sender(message.sender)
////									.receivers(message.receivers)
////									.event(MESSAGE_DEPARTED)
////									.build();
////		return sendJSON(payload.toJSON());
//		return 3;
//	}
//	
//	public static long sendJSON(String json) {
//		// Payload
//		//HashMap<String, Object> data = null;
//		Connection conn = new Connection.Builder()
//										.url(Connection.HOST_URL + "/message/sendMessageWithGCM")
//										.type(Connection.TYPE_POST)
//										.dataType(Connection.DATATYPE_JSON)
//										.data(json)
//										.build();
//		int responseCode = conn.request();
//		String response = null;
//		if(responseCode == Connection.HTTP_OK) {
//			response = conn.getResponse();
//		}
//		// TODO with response
//		// TODO 
//		return getMessageIndexFromJSONResponse(response);
//	}
//	
//	public static long getMessageIndexFromJSONResponse(String json) {
//		JSONObject response;
//		try {
//			response = new JSONObject(json);
//			return response.getLong("messageIdx");
//		} catch (JSONException e) {
//			Log.w(TAG, "Cannot get MessageIdx From Responsed JSON Message");
//			return Message.NOT_SPECIFIED;
//		}
//		
//	}
//	
//	public static boolean sendSurveyAnswerSheet(String json) {
//		
//		Connection conn = new Connection.Builder()
//				.url(Connection.HOST_URL + "/survey/answerSurvey")
//				.type(Connection.TYPE_POST)
//				.dataType(Connection.DATATYPE_JSON)
//				.data(json)
//				.build();
//		String result = null;
//		int requestCode = conn.request();
//		if(requestCode == Connection.HTTP_OK) {
//			result = conn.getResponse();
//		} else {
//			return false;
//		}
//		
//		if(result == null) return false;
//		
//		JSONObject jo=null;
//		try {
//			 jo = new JSONObject(result);
//		} catch (JSONException e) {
//			return false;
//		}
//		
//		if(jo == null) return false;
//		
//		boolean status = false;
//		try {
//			status = (jo.getInt("status") == 1?true:false);
//		} catch (JSONException e) {
//			return false;
//		}
//		
//		return status;
//	}
//	
//	public static ArrayList<Department> getSubDepartment(String json) {
//		Connection conn = new Connection.Builder()
//										.url(Connection.HOST_URL + "/member/getSubDepartment")
//										.type(Connection.TYPE_GET)
//										.dataType(Connection.DATATYPE_JSON)
//										.data(json)
//										.build();
//		
//		String result = null;
//		int requestCode = conn.request();
//		if(requestCode == Connection.HTTP_OK) {
//			result = conn.getResponse();
//		} else {
//			return null;
//		}
//		
//		JSONObject jo = null;
//		
//		try {
//			jo = new JSONObject(result);
//		} catch (JSONException e) {
//			return null;
//		}
//		
//		JSONArray ja = null;
//		try {
//			ja  = jo.getJSONArray("departments");
//		} catch (JSONException e) {
//			return null;
//		}
//		
//		if(ja == null) return null;
//		
//		ArrayList<Department> departments = new ArrayList<Department>(ja.length());
//		
//		for(int i=0; i < ja.length(); i++) {
//			try {
//				JSONObject _dep = ja.getJSONObject(i);
//				Department dep = new Department();
//				dep.title = _dep.getString("title");
//				dep.sequence = _dep.getLong("sequence");
//				departments.add(dep);
//			} catch (JSONException e) {
//			}
//		}
//		
//		return departments;
//	}
//}
