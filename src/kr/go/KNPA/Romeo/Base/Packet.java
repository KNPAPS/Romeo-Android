package kr.go.KNPA.Romeo.Base;

import java.util.ArrayList;

import kr.go.KNPA.Romeo.Member.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.Gson;

import android.os.Bundle;

public class Packet {
	public static int NOT_SPECIFIED = -777;
	
	public long departingTS;
	public int deviceStatus;
	public Payload payload;
	
	public Packet() {
	}
	
	public Packet(Bundle b) {

		// TODO ambiguous Type
		int deviceStatus = Integer.parseInt(b.getString("deviceStatus"));
		long departingTS = Long.parseLong(b.getString("departingTS"));
		//String collapse_key = b.getString("collapse_key");
		//long from = b.getLong("from");
		String __payload = b.getString("payload");

		Gson gson = new Gson();
		
		JSONObject _payload;
		try {
			_payload = new JSONObject(__payload);
		} catch (JSONException e) {
			_payload = null;
		}
		
		String event				= null;
		User sender					= null;
		ArrayList<User>	receivers	= null;
		
		JSONObject _message			= null;
		
		long idx					= NOT_SPECIFIED;
		int type					= NOT_SPECIFIED;
		String title				= null;
		String content				= null;
		Appendix appendix			= null;
		
		JSONArray _receivers		= null;
		int _sender					= NOT_SPECIFIED;
		JSONObject _appendix		= null;

		if(_payload != null){
			try {
				 event = _payload.getString("event");
			} catch (JSONException e) {
				event = null;
			}
			
			
			try {
				_sender = _payload.getInt("sender");
			} catch (JSONException e) {
				_sender = NOT_SPECIFIED;
			}
			if(_sender != NOT_SPECIFIED) {
				sender = User.getUserWithIdx(_sender);
			}
			
			
			try {
				_receivers = _payload.getJSONArray("receivers");
			} catch (JSONException e) {
				_receivers = null;
			}
			
			if(_receivers != null) {
				ArrayList<User> users = new ArrayList<User>();
				
				for(int i= 0; i<_receivers.length(); i++) {
					try {  users.add(User.getUserWithIdx(_receivers.getInt(i)));} catch (JSONException e) {}
				}
				if(users.size() > 0) receivers = users;
			}

			try {
				_message = _payload.getJSONObject("message");
			} catch (JSONException e) {
				_message = null;
			}
			
			if(_message != null) {
				
				try {
					idx = _message.getLong("idx");
				} catch (JSONException e) {
					idx = NOT_SPECIFIED;
				}
				
				try {
					type = _message.getInt("type");
				} catch (JSONException e) {
					type = NOT_SPECIFIED;
				}
				try {
					title = _message.getString("title");
				} catch (JSONException e) {
					title = null;
				}
				
				try {
					content = _message.getString("content");
				} catch (JSONException e) {
					content = null;
				}
				
				try {
					_appendix = _message.getJSONObject("appendix");
				} catch (JSONException e) {
					_appendix = null;
				}
				
				if(_appendix != null) {
					appendix = new Appendix(_appendix.toString());///gson.fromJson(_appendix.toString(), Appendix.class);
				} else {
					appendix = new Appendix();
				}
				
			}
		}
		
		Message message = null;
		if(_message != null) {
			// 메시지의 종류에 따라 분화?
			message = new Message();
			message.appendix = appendix;
			message.content = content;
			message.title = title;
			message.type = type;
			message.idx = idx;

		}
		
		
		Payload payload = null;
		if(_payload != null) {
			payload = new Payload.Builder().message(message)
										   .event(event)
										   .sender(sender)
										   .receivers(receivers)
										   .build();
		}
		
		this.departingTS = departingTS;
		this.deviceStatus = deviceStatus;
		this.payload = payload;

	}
	
	public Packet(JSONObject json) {
		
		long departingTS;
		try {
			departingTS = json.getLong("departingTS");
		} catch (JSONException e) {
			departingTS = 0;
		}
		
		int deviceStatus;
		try {
			deviceStatus = json.getInt("deviceStatus");
		} catch (JSONException e) {
			deviceStatus = NOT_SPECIFIED;
		}
		
		JSONObject _payload;
		try {
			_payload = json.getJSONObject("payload");
		} catch (JSONException e) {
			_payload = null;
		}
		
		String event				= null;
		User sender					= null;
		ArrayList<User>	receivers	= null;
		
		JSONObject _message			= null;
		long idx					= NOT_SPECIFIED;
		int type					= NOT_SPECIFIED;
		String title				= null;
		String content				= null;
		Appendix appendix			= null;

		if(_payload != null){
			try {
				 event = _payload.getString("event");
			} catch (JSONException e) {
				event = null;
			}
			
			int _sender;
			try {
				_sender = _payload.getInt("sender");
			} catch (JSONException e) {
				_sender = NOT_SPECIFIED;
			}
			if(_sender != NOT_SPECIFIED) {
				// TODO : get User instance from _sender(idx)
			}
			
			JSONArray _receivers		= null;
			try {
				_receivers = _payload.getJSONArray("receivers");
			} catch (JSONException e) {
				_receivers = null;
			}
			
			if(_receivers != null) {
				// TODO : parsing _receivers;
			}

			
			try {
				_message = _payload.getJSONObject("message");
			} catch (JSONException e) {
				_message = null;
			}
			
			if(_message != null) {
				
				try {
					idx = _message.getLong("idx");
				} catch (JSONException e) {
					idx = NOT_SPECIFIED;
				}
				
				try {
					type = _message.getInt("type");
				} catch (JSONException e) {
					type = NOT_SPECIFIED;
				}
				try {
					title = _message.getString("title");
				} catch (JSONException e) {
					title = null;
				}
				
				try {
					content = _message.getString("content");
				} catch (JSONException e) {
					content = null;
				}
				
				JSONObject _appendix		= null;
				try {
					_appendix = _message.getJSONObject("appendix");
				} catch (JSONException e) {
					_appendix = null;
				}
				
				if(_appendix != null) {
					// TODO : make Appendix appendix;
				}
				
			}
		}
		
		Message message = null;
		if(_message != null) {
			message = new Message();
			message.appendix = appendix;
			message.content = content;
			message.title = title;
			message.type = type;
			message.idx = idx;
		}
		
		Payload payload = null;
		if(_payload != null) {
			payload = new Payload.Builder().message(message)
										   .event(event)
										   .sender(sender)
										   .receivers(receivers)
										   .build();
		}
		
		this.departingTS = departingTS;
		this.deviceStatus = deviceStatus;
		this.payload = payload;
	}

}
