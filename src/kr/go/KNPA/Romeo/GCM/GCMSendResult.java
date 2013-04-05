package kr.go.KNPA.Romeo.GCM;

import java.util.ArrayList;
import java.util.HashMap;

import kr.go.KNPA.Romeo.Config.Constants;

/**
 * 우리 서버에서 GCM 서버로부터 응답받은 GCM send result json 객체의 java native version\n 
 */
public class GCMSendResult {
	
	public int multicastId=Constants.NOT_SPECIFIED;
	public int nSuccess=Constants.NOT_SPECIFIED;
	public int nFailure=Constants.NOT_SPECIFIED;
	public int canonicalIds=Constants.NOT_SPECIFIED;
	public ArrayList<HashMap<String,String>> eachResult=null;

}
