package kr.go.KNPA.Romeo.GCM;

import kr.go.KNPA.Romeo.Base.*;
import kr.go.KNPA.Romeo.Member.User;
import kr.go.KNPA.Romeo.Member.Department;
import kr.go.KNPA.Romeo.Member.MemberManager;
import kr.go.KNPA.Romeo.Util.*;

public class GCMMessageSender {

	/* -------------
	 * 		Payload
	 * -------------
	 * event		: String	: "event0 : subEvent : subSubEvnet.."
	 * sender		: long		:  발신자의 DB상의 idx값
	 * receivers	: long[]	:  수신자들의 DB상의 idx값의 모임
	 * roomCode		: String	: "senderIdx : departedTS"
	 * message		: Object
	 */	
	
	public GCMMessageSender() {
		// TODO Auto-generated constructor stub
	}
	
	public String sendMessageWithPayload(Payload p) {
		Connection conn = new Connection.Builder()
										.dataType(Connection.DATATYPE_JSON)
										.type(Connection.TYPE_POST)
										.url(Connection.HOST_URL + "/Message/sendMessageWithGCM")
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

}
