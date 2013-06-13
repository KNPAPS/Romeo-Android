package kr.go.KNPA.Romeo.Util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;

import kr.go.KNPA.Romeo.R;
import android.content.Context;

/**
 * string, timestamp formatting
 */
public class Formatter {

	public Formatter()
	{
	}

	/**
	 * timestamp를 지정된 포맷으로 바꿈
	 * 
	 * @param timestamp
	 *            timestamp
	 * @param formatString
	 *            포맷으로 사용할 예시 문자열 (ex. 2011.3.12)
	 * @return formatted date string
	 */
	public static String timeStampToStringWithFormat(long secTS, String formatString)
	{
		String result = null;
		Date date = new Date(secTS * 1000);
		try
		{
			SimpleDateFormat format = new SimpleDateFormat(formatString, Locale.KOREA);
			result = format.format(date);
		}
		catch (RuntimeException e)
		{
			result = null;
		}
		return result;
	}

	/**
	 * string name="formatString_regular" 에 정의된 포맷으로 타임스탬프를 변환\n 현재는 yyyy.MM.dd
	 * HH:mm:ss 형식임
	 * 
	 * @param timestamp
	 * @param context
	 *            애플리케이션 context. R을 참조할 수 있어야 함
	 * @return formatted date string
	 */
	public static String timeStampToStringInRegularFormat(long secTS, Context context)
	{
		return timeStampToStringWithFormat(secTS, context.getString(R.string.formatString_regular));
	}

	/**
	 * timestamp를 직관적인 형식의 문자열로 변환.
	 * 
	 * @param timestamp
	 * @return 오늘이면 HH시 mm분, 어제면 '어제', x일 전, 일주일 전, mm월 dd일\n hh시 mm분
	 */
	public static String timeStampToRecentString(long secTS)
	{
		// StringBuilder sb = new StringBuilder();
		String result = null;
		long now = System.currentTimeMillis();
		long dayInMils = 24 * 60 * 60 * 1000;
		int dayDif = (int) Math.floor((now - secTS * 1000) + 1.0 / dayInMils);

		String formatString = null;

		switch (dayDif)
		{
		case 0: // today
			formatString = "HH'시' mm'분'";
			break;
		case 1:
			result = "어제";
			break;
		case 2:
		case 3:
		case 4:
		case 5:
		case 6:
			result = dayDif + "일 전";
			break;
		case 7:
			result = "일주일 전";
			break;
		default:
			formatString = "MM'월' dd'일'\nHH'시' mm'분'";
			break;
		}

		if (!(dayDif > 0 && dayDif <= 7))
		{
			Date date = new Date(secTS * 1000);
			SimpleDateFormat format = new SimpleDateFormat(formatString, Locale.KOREA);
			result = format.format(date);
		}

		return result;// sb.toString();
	}

	public static String encodeURIComponent(String s)
	{
		String result = null;

		try
		{
			result = URLEncoder.encode(s, "UTF-8").replaceAll("\\+", "%20").replaceAll("\\%21", "!").replaceAll("\\%27", "'").replaceAll("\\%28", "(").replaceAll("\\%29", ")")
					.replaceAll("\\%7E", "~");
		}
		// This exception should never occur.
		catch (UnsupportedEncodingException e)
		{
			result = s;
		}

		return result;
	}

	public static String join(Collection<?> s, String delimiter)
	{
		StringBuilder builder = new StringBuilder();
		Iterator<?> iter = s.iterator();
		while (iter.hasNext())
		{
			builder.append(iter.next());
			if (!iter.hasNext())
			{
				break;
			}
			builder.append(delimiter);
		}
		return builder.toString();
	}

	public static String makeEllipsis(String str, int maxLen)
	{
		if (str.length() > maxLen)
		{
			return str.substring(0, maxLen) + "...";
		}
		else
		{
			return str;
		}
	}
}