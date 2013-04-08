package kr.go.KNPA.Romeo.GCM;

import java.util.ArrayList;

import kr.go.KNPA.Romeo.Base.Message;
import kr.go.KNPA.Romeo.Chat.Chat;
import kr.go.KNPA.Romeo.Config.Event;
import kr.go.KNPA.Romeo.Config.StatusCode;
import kr.go.KNPA.Romeo.Connection.Connection;
import kr.go.KNPA.Romeo.Connection.Data;
import kr.go.KNPA.Romeo.Connection.Payload;
import kr.go.KNPA.Romeo.Document.Document;
import kr.go.KNPA.Romeo.Survey.Survey;
import kr.go.KNPA.Romeo.Util.CallbackEvent;
import android.content.Context;

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
	
	public static void sendMessage(final Context context, Message message) {
		Data reqData = new Data().add(0, Data.KEY_MESSAGE, message);
		Payload request = new Payload().setEvent(Event.Message.send()).setData(reqData);
		
		CallbackEvent<Payload,Integer,Payload> callBack = new CallbackEvent<Payload, Integer, Payload>(){
			private Message _message;
			@Override
			public void onPreExecute(Payload request){
				_message = (Message)request.getData().get(0,"message");
			}
			
			@Override
			public void onPostExecute(Payload response) {
				
				if(response.getStatusCode() == StatusCode.SUCCESS) {
				
					String messageIdx = (String)response.getData().get(0, Data.KEY_MESSAGE_HASH);
					_message.idx = messageIdx;
					// TODO :  실패한 발신자
					// TODO : 발신자 별 에러 컨트롤
					
					if(_message.mainType() == Message.MESSAGE_TYPE_CHAT) {
						((Chat)_message).afterSend(context, true);
					} else if(_message.mainType() == Message.MESSAGE_TYPE_DOCUMENT) {
						((Document)_message).afterSend(context, true);
					} else if(_message.mainType() == Message.MESSAGE_TYPE_SURVEY) {
						((Survey)_message).afterSend(context, true);
					}
				} else {
					// TODO : 실패했을때??
					if(_message.mainType() == Message.MESSAGE_TYPE_CHAT) {
						((Chat)_message).afterSend(context, false);
					} else if(_message.mainType() == Message.MESSAGE_TYPE_DOCUMENT) {
						((Document)_message).afterSend(context, false);
					} else if(_message.mainType() == Message.MESSAGE_TYPE_SURVEY) {
						((Survey)_message).afterSend(context, false);
					}
				}
				
			}
			
			@Override
			public void onError(String errorMsg, Exception e) {
				if(_message.mainType() == Message.MESSAGE_TYPE_CHAT) {
					((Chat)_message).afterSend(context, false);
				} else if(_message.mainType() == Message.MESSAGE_TYPE_DOCUMENT) {
					((Document)_message).afterSend(context, false);
				} else if(_message.mainType() == Message.MESSAGE_TYPE_SURVEY) {
					((Survey)_message).afterSend(context, false);
				}
			}
		};
		
		Connection conn = new Connection().requestPayloadJSON(request.toJSON()).callBack(callBack);
		conn.request();
	}
	
	public static void sendSurveyAnswerSheet(String json) {
		
		Data reqData = new Data().add(0, Data.KEY_USER_HASH, "").add(0, Data.KEY_MESSAGE_SURVEY_HASH, "");
		Payload request = new Payload().setEvent(Event.Message.send()).setData(reqData);
		
		CallbackEvent<Payload,Integer,Payload> callBack = new CallbackEvent<Payload, Integer, Payload>(){
			private Message _message;
			@Override
			public void onPreExecute(Payload request){
				
			}
			
			@Override
			public void onPostExecute(Payload response) {
				Survey.afterSendAnswerSheet();
			}
		};
		
		Connection conn = new Connection().requestPayloadJSON(request.toJSON()).callBack(callBack);
		conn.request();
	}
	
}
