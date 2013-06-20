package kr.go.KNPA.Romeo;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;

import kr.go.KNPA.Romeo.Config.Event;
import kr.go.KNPA.Romeo.Config.KEY;
import kr.go.KNPA.Romeo.Connection.Connection;
import kr.go.KNPA.Romeo.Connection.Data;
import kr.go.KNPA.Romeo.Connection.Payload;
import kr.go.KNPA.Romeo.Util.UserInfo;
import android.app.Application;

public class DaonApplication extends Application {
	
	private UncaughtExceptionHandler mUncaughtExceptionHandler;
	
	@Override
	public void onCreate()
	{
		mUncaughtExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandlerApplication());
		super.onCreate();
	}

	/**
	 * 메시지로 변환
	 * @param th
	 * @return
	 */
	private String getStackTrace(Throwable th)
	{
		final Writer result = new StringWriter();
		final PrintWriter printWriter = new PrintWriter(result);

		Throwable cause = th;
		while (cause != null)
		{
			cause.printStackTrace(printWriter);
			cause = cause.getCause();
		}
		final String stacktraceAsString = result.toString();
		printWriter.close();

		return stacktraceAsString;
	}

	class UncaughtExceptionHandlerApplication implements Thread.UncaughtExceptionHandler {

		@Override
		public void uncaughtException(Thread thread, Throwable ex)
		{
			String log = getStackTrace(ex);
			
			Data reqData = new Data()
					.add(0, KEY.USER.IDX, UserInfo.getUserIdx(getApplicationContext()))
					.add(0, KEY.MESSAGE.CREATED_TS, System.currentTimeMillis() / 1000)
					.add(0, KEY.MESSAGE.CONTENT, log);

			Payload request = new Payload().setEvent(Event.USER_BUG_REPORT).setData(reqData);

			new Connection().requestPayload(request).async(false).request();

			// 예외처리를 하지 않고 DefaultUncaughtException으로 넘긴다.
			mUncaughtExceptionHandler.uncaughtException(thread, ex);
		}

	}
}
