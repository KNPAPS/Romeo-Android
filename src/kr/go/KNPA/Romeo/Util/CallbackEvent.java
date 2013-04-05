package kr.go.KNPA.Romeo.Util;
/**
 * Generic callback class
 */
public class CallbackEvent<Params, Progress, Result> {
	public void onPreExecute(Params params){ }
	public void onError(String errorMsg, Exception e){ }
	public void onProgressUpdate(Progress progress){ }
	public void onPostExecute(Result result){ } 
}
