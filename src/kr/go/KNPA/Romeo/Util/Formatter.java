package kr.go.KNPA.Romeo.Util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.content.Context;
import android.text.GetChars;

import kr.go.KNPA.Romeo.R;



public class Formatter {

	public Formatter() {
		// TODO Auto-generated constructor stub
	}

	public static String timeStampToStringWithFormat(long timestamp, String formatString) {
		String result= null;
		Date date = new Date(timestamp);
		try {
			SimpleDateFormat format = new SimpleDateFormat(formatString, Locale.KOREA);
			result = format.format(date);
		} catch (RuntimeException e) {
			result = null;
		}
		return result;
	}
	
	public static String timeStampToStringInRegularFormat(long timestamp, Context context) {
		return timeStampToStringWithFormat(timestamp, context.getString(R.string.formatString_regular));
	}
	
	public static String timeStampToRecentString(long timestamp) {
		//StringBuilder sb = new StringBuilder();
		String result= null;
		long now = System.currentTimeMillis();
		long dayInMils = 24*60*60*1000;
		int dayDif = (int)Math.floor((now - timestamp)+1.0/dayInMils);
	
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
			Date date = new Date(timestamp);
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