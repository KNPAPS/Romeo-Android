package kr.go.KNPA.Romeo.Util;
/**
 * Generic callback class
 */
public class CallbackEvent<Params, Progress, Result> {
	public void beforeSend(Params params){ }
	public void error(String errorMsg, Exception e){ }
	public void onProgressUpdate(Progress progress){ }
	public void success(Result result){ }
}
