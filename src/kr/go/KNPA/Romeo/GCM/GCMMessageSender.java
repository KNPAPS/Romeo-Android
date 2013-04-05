package kr.go.KNPA.Romeo.GCM;

import java.util.ArrayList;

import kr.go.KNPA.Romeo.Base.Message;
import kr.go.KNPA.Romeo.Config.StatusCode;
import kr.go.KNPA.Romeo.Connection.Connection;
import kr.go.KNPA.Romeo.Connection.Data;
import kr.go.KNPA.Romeo.Connection.Payload;
import kr.go.KNPA.Romeo.Member.Department;
import kr.go.KNPA.Romeo.Util.CollectionFactory;
import kr.go.KNPA.Romeo.Util.UserInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

public class GCMMessageSender {

	/* -------------
	 * 		Payload
	 * -------------
	 * event		: String	: "event0 : subEvent : subSubEvnet.."
	 * sender		: long		:  발신자의 DB상의 idx값
	 * receivers	: long[]	:  수신자들의 DB상의 idx값의 모임
	 * X roomCode		: String	: "senderIdx : departedTS"
	 * message		: Object
	 */	
	
	

	public static final String MESSAGE_RECEIVED = "MESSAGE:RECEIVED";
	public static final String MESSAGE_DEPARTED = "MESSAGE:DEPARTED";
	public static final String TAG = "GCMMessageSender";
	
	public GCMMessageSender() {
	}
	
	public String sendMessageWithPayload(Payload p) {
		Connection conn = new Connection.Builder()
										.dataType(Connection.DATATYPE_JSON)
										.type(Connection.TYPE_POST)
										.url(Connection.HOST_URL + "/message/sendMessageWithGCM")
										.data(CollectionFactory.hashMapWithKeysAndValues("payload", p))
										.build();
		String result = null;
		int requestCode = conn.request();
		if(requestCode == Connection.HTTP_OK) {
			result = conn.getResponse();
		} else {
			return null;
		}
		
		
		return result;
	}
	
	public static boolean setMessageChecked(int type, String messageIdx, String userIdx) {
		Payload request = new Payload().setData(
				new Data()
					.add(0, Data.KEY_MESSAGE_TYPE, type)
					.add(0, Data.KEY_MESSAGE_HASH, messageIdx)
					.add(0, Data.KEY_USER_HASH, userIdx)
				);
		Connection conn = new Connection().requestPayloadJSON(request.toJSON()).request();
		Payload response = conn.getResponsePayload();
		
		if ( response.getStatusCode() == StatusCode.SUCCESS ){
			String result = (String)response.getData().get(0, Data.KEY_MESSAGE);
			return true;
		} else {
			return false;
		}
	}

	public static ArrayList<String> getUncheckers(int type, String idx) {
		Payload request = new Payload().setData(new Data().add(0, Data.KEY_MESSAGE_TYPE, type).add(0, Data.KEY_MESSAGE_HASH, idx));
		Connection conn = new Connection().requestPayloadJSON(request.toJSON()).request();
		Payload response = conn.getResponsePayload();
		
		ArrayList<String> uncheckers = new ArrayList<String>();
		if ( response.getStatusCode() == StatusCode.SUCCESS ){
			Data respData = response.getData();
			int nUncheckers = respData.size();
			for(int i=0; i<nUncheckers; i++) {
				uncheckers.add( (String)respData.get(i, Data.KEY_USER_HASH) );
			}
			
		}
		
		return uncheckers;
	}
	
	public static long sendMessage(Message message) {
		Data data = new Data();
		data.add(0, Data.KEY_SENDER_HASH, message.sender.idx);
		data.add(0, Data.KEY_RECEIVER_HASH, message.receivers);
		if(message.mainType() == Message.MESSAGE_TYPE_CHAT) {
			data.add(0, Data.KEY_ROOM_HASH, message.appendix.getRoomCode());
		}
		
		data.add(0, Data.Key_, value)
		Payload request = new Payload().setData(data);
		Connection conn = new Connection().requestPayloadJSON(request.toJSON()).request();
		
		Payload payload = new Payload.Builder()
									.message(message)
									.sender(message.sender)
									.receivers(message.receivers)
									.event(MESSAGE_DEPARTED)
									.build();
		return sendJSON(payload.toJSON());
	}
	
	public static long sendJSON(String json) {
		// Payload
		//HashMap<String, Object> data = null;
		Connection conn = new Connection.Builder()
										.url(Connection.HOST_URL + "/message/sendMessageWithGCM")
										.type(Connection.TYPE_POST)
										.dataType(Connection.DATATYPE_JSON)
										.data(json)
										.build();
		int responseCode = conn.request();
		String response = null;
		if(responseCode == Connection.HTTP_OK) {
			response = conn.getResponse();
		}
		// TODO with response
		// TODO 
		return getMessageIndexFromJSONResponse(response);
	}
	
	public static long getMessageIndexFromJSONResponse(String json) {
		JSONObject response;
		try {
			response = new JSONObject(json);
			return response.getLong("messageIdx");
		} catch (JSONException e) {
			Log.w(TAG, "Cannot get MessageIdx From Responsed JSON Message");
			return Message.NOT_SPECIFIED;
		}
		
	}
	
	public static boolean sendSurveyAnswerSheet(String json) {
		
		Connection conn = new Connection.Builder()
				.url(Connection.HOST_URL + "/survey/answerSurvey")
				.type(Connection.TYPE_POST)
				.dataType(Connection.DATATYPE_JSON)
				.data(json)
				.build();
		String result = null;
		int requestCode = conn.request();
		if(requestCode == Connection.HTTP_OK) {
			result = conn.getResponse();
		} else {
			return false;
		}
		
		if(result == null) return false;
		
		JSONObject jo=null;
		try {
			 jo = new JSONObject(result);
		} catch (JSONException e) {
			return false;
		}
		
		if(jo == null) return false;
		
		boolean status = false;
		try {
			status = (jo.getInt("status") == 1?true:false);
		} catch (JSONException e) {
			return false;
		}
		
		return status;
	}
	
	public static ArrayList<Department> getSubDepartment(String json) {
		Connection conn = new Connection.Builder()
										.url(Connection.HOST_URL + "/member/getSubDepartment")
										.type(Connection.TYPE_GET)
										.dataType(Connection.DATATYPE_JSON)
										.data(json)
										.build();
		
		String result = null;
		int requestCode = conn.request();
		if(requestCode == Connection.HTTP_OK) {
			result = conn.getResponse();
		} else {
			return null;
		}
		
		JSONObject jo = null;
		
		try {
			jo = new JSONObject(result);
		} catch (JSONException e) {
			return null;
		}
		
		JSONArray ja = null;
		try {
			ja  = jo.getJSONArray("departments");
		} catch (JSONException e) {
			return null;
		}
		
		if(ja == null) return null;
		
		ArrayList<Department> departments = new ArrayList<Department>(ja.length());
		
		for(int i=0; i < ja.length(); i++) {
			try {
				JSONObject _dep = ja.getJSONObject(i);
				Department dep = new Department();
				dep.title = _dep.getString("title");
				dep.sequence = _dep.getLong("sequence");
				departments.add(dep);
			} catch (JSONException e) {
			}
		}
		
		return departments;
	}
}
