package kr.go.KNPA.Romeo.Util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import kr.go.KNPA.Romeo.R;
import android.content.Context;

/**
 * string, timestamp formatting
 */
public class Formatter {

	public Formatter() {
	}

	/**
	 * timestamp를 지정된 포맷으로 바꿈
	 * @param timestamp timestamp
	 * @param formatString 포맷으로 사용할 예시 문자열 (ex. 2011.3.12)
	 * @return formatted date string
	 */
	public static String timeStampToStringWithFormat(long secTS, String formatString) {
		String result= null;
		Date date = new Date(secTS*1000);
		try {
			SimpleDateFormat format = new SimpleDateFormat(formatString, Locale.KOREA);
			result = format.format(date);
		} catch (RuntimeException e) {
			result = null;
		}
		return result;
	}
	
	/**
	 * string name="formatString_regular" 에 정의된 포맷으로 타임스탬프를 변환\n
	 * 현재는 yyyy.MM.dd HH:mm:ss 형식임
	 * @param timestamp
	 * @param context 애플리케이션 context. R을 참조할 수 있어야 함
	 * @return formatted date string
	 */
	public static String timeStampToStringInRegularFormat(long secTS, Context context) {
		return timeStampToStringWithFormat(secTS, context.getString(R.string.formatString_regular));
	}
	
	/**
	 * timestamp를 직관적인 형식의 문자열로 변환.
	 * @param timestamp
	 * @return 오늘이면 HH시 mm분, 어제면 '어제', x일 전, 일주일 전, mm월 dd일\n hh시 mm분 
	 */
	public static String timeStampToRecentString(long secTS) {
		//StringBuilder sb = new StringBuilder();
		String result= null;
		long now = System.currentTimeMillis();
		long dayInMils = 24*60*60*1000;
		int dayDif = (int)Math.floor((now - secTS*1000)+1.0/dayInMils);
	
		String formatString = null;
		
		switch(dayDif) {
		case 0 : // today
			formatString = "HH'시' mm'분'"; break;
		case 1 :
			result = "어제"; break;
		case 2 :
		case 3 :
		case 4 :
		case 5 :
		case 6 :
			result = dayDif + "일 전"; break;
		case 7 :
			result = "일주일 전"; break;
		default :
			formatString = "MM'월' dd'일'\nHH'시' mm'분'";
			break;
		}
		
		if(!(dayDif > 0 && dayDif <= 7)) {
			Date date = new Date(secTS*1000);
			SimpleDateFormat format = new SimpleDateFormat(formatString, Locale.KOREA);
			result = format.format(date);
		}
		
		return result;//sb.toString();
	}
}
/*




Romeo.Model.TimeStamp = Backbone.Model.extend({
},
{ // CLASS METHDOS, PROPERTIES
	getTime : function() {
		return Date.now();
	},
	toRecentString : function(timestamp) {
		var now = this.toTimeStamp(timestamp);//this.getTime();
		var dayInMils = 24*60*60*1000;
		var dayDif = Math.floor((now - timestamp) / dayInMils);
		
		var string = "";
		
		switch(dayDif) {
			case 0 : // today
				string = this.toString("hh시 mm분", timestamp); break;
			case 1 :
				string = "어제"; break;
			case 2 :
			case 3 :
			case 4 :
			case 5 :
			case 6 :
				string = dayDif + "일 전"; break;
			case 7 :
				string = "일주일 전"; break;
			default :
				string = this.toString("MM월 DD일\nhh시 mm분", timestamp); break;
		}
		
		return string;
	},
	toTimeStamp : function(datetime) {
		if((typeof datetime) == "number") return datetime;
		var result = datetime;
		if(_.without(datetime.split(/[0-9]+/),"").length > 0) {
			// not just Timestamp
			var dts = _.without(datetime.split(/[^0-9]+/),"")
			var date = new Date();
			date.setFullYear(dts[0], dts[1], dts[2]);
			date.setHours(dts[3], dts[4], dts[5]);
			result = date.getTime();
		} else {// else : "14072340235" , 923075823457 
			result = datetime-0;
			if((typeof result) != "number") result = datetime;
		}
		return result;
	},
	toString : function(format, timestamp) {
		if(!format) return "[Object Romeo.Model.TimeStamp] : "+this.get("timestamp");
		var date = timestamp?timestamp:this.getTime();
		if(!(date instanceof Date)) date = new Date(timestamp);
		// UTC Hours : 표준시간
		// Hours : GMT+0900 적용된 Local Time
		// format : Y, M, D, h, m, s

		var Y = date.getYear()+1900;
		var M = date.getMonth()+1;
		var D = date.getDate();
		var h = date.getHours();
		var m = date.getMinutes();
		var s = date.getSeconds();
		
		
		var re= /[^YMDhms]/;// /[:-\s\/]/;
		
		var frags = _.without(format.split(re), "");
		var seperator = _.without(format.split(/[YYMDhms]/),"");
		
		var array = Array();
		for(var i=0; i<frags.length; i++) {
			switch(frags[i]) {
				case 	 	 "Y" : 
				case 		"YY" : array.push((Y.length>2?(Y.substring(Y.length-2)):Y));	break;
				case	"YYYY" : array.push(Y); 										break;
				case 		 "M" : array.push(M); 										break;
				case 		"MM" : array.push((M.length<2?("0"+M):M)); 	break;
				case 		 "D" : array.push(D);											break;
				case 		"DD" : array.push((D.length<2?("0"+D):D));	break;
				case 		 "h" : array.push(h);											break;
				case 		"hh" : array.push((h.length<2?("0"+h):h));	break;
				case 		 "m" : array.push(m);											break;
				case 		"mm" : array.push((m.length<2?("0"+m):m));	break;
				case 		 "s" : array.push(s);											break;
				case 		"ss" : array.push((s.length<2?("0"+s):s));	break;
				default : break;
			}
			array.push(seperator[i]);
		}
		
		return array.join("");
		
	}
});





*/