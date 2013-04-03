package kr.go.KNPA.Romeo.Member;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

import kr.go.KNPA.Romeo.Config.Constants;
import kr.go.KNPA.Romeo.Util.IndexPath;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class CellNode {//extends ArrayList<CellNode> {

	private static final long serialVersionUID = 498188955518204141L;
	public static final int CELLNODE_NULL = -777;
	public static final int CELLNODE_USER = 1;
	public static final int CELLNODE_DEPARTMENT = 2;
	public boolean isRoot = false;
	
	public int type = CELLNODE_NULL;
	private boolean _unfolded;
	private int _index = -1;
	private IndexPath _parentIndexPath = null;
	public int status = 0;
	public CellNode() {
		
	}
	
	public CellNode(String json) {
		JSONObject jo = null;
		try {
			jo = new JSONObject(json);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		if( jo== null) return;
		int type = Constants.NOT_SPECIFIED;
		try {
			type = jo.getInt("type");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		boolean isUnfolded = false;
		try {
			isUnfolded = (jo.getInt("isUnfolded") == 1 ? true: false);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		int index = Constants.NOT_SPECIFIED;
		try {
			index = jo.getInt("index");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		String _ip = null;
		try {
			_ip = jo.getString("parentIndexPath");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		IndexPath parentIndexPath = IndexPath.indexPathWithString(_ip);
		
		int status = 0;
		try {
			status = jo.getInt("status");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		this._index = index;
		this._parentIndexPath = parentIndexPath;
		this._unfolded = isUnfolded;
		this.type = type;
		this.status = status;
	}
	
	public static class Builder {
		private int _type = CELLNODE_NULL;
		private boolean _unfolded;
		private int _index = -1;
		private IndexPath _parentIndexPath = null;
		private int _status = 0;
		public Builder type(int type) {
			this._type = type;
			return this;
		}
		public Builder unfolded(boolean unfolded) {
			this._unfolded = unfolded;
			return this;
		}
		public Builder index(int index) {
			this._index = index;
			return this;
		}
		public Builder status(int status) {
			this._status = status;
			return this;
		}
		public Builder parentIndexPath(IndexPath parentIndexPath) {
			this._parentIndexPath = parentIndexPath;
			return this;
		}
		public CellNode build() {
			CellNode node = new CellNode();
			node.type = this._type;
			node._unfolded = this._unfolded;
			node._index = this._index;
			node._parentIndexPath = this._parentIndexPath;
			node.status = this._status;
			return node;
			
		}
	}

	
	public static CellNode nodeAtIndexPath(CellNode rootNode, IndexPath path) {
		String json = MemberListAdapter.nodeManager.command("nodeAtIndexPath(\""+path.toString()+"\")");
		Log.d("CellNode:nodeAtIndexPath",json);
		return new CellNode(json);
		/*
		CellNode cn = rootNode;
		IndexPath.Iterator itr = new IndexPath.Iterator(path);
		
		while(itr.hasNextIndex()) {
			cn = cn.get(itr.nextIndex());
		}
		return cn;
		*/
	}

	/*
	public int countIncludeFolded() {
		int result = 0;
		for(int i=0; i< this.size(); i++) {
			result += this.get(i).countIncludeFolded();
		}
		return (result+1);
	}
	*/
	public int count() {

		String json = MemberListAdapter.nodeManager.command("count("+this.toJSON()+")");
		Log.d("CellNode:count",json);
		return Integer.parseInt(json);
		/*
		int result = 0;
		
		if(isUnfolded() == true) {		  	// UnFolded
			for(int i=0; i< this.size(); i++) {
				result += this.get(i).count();
			}
		} else if(isUnfolded() == false) { 	// Folded
		//	result += 1;
		}
		
		
		if(isRoot != true) return result+1;
		return result;
		*/
	}
	
	public boolean isUnfolded() {
		return _unfolded;
	}
	
	public void setUnfolded(boolean unfolded) {
		int uf = (unfolded == true?1:0);
		String json = MemberListAdapter.nodeManager.command("setIsUnfolded("+this.toJSON()+","+uf+")");
		Log.d("CellNode:setUnfolded",json);
		_unfolded = unfolded;
	}
	
	public IndexPath getIndexPath() {
		int idx = _index;
		
		if(_parentIndexPath == null) {
			return IndexPath.indexPathWithIndex(_index);
		} else {
			return _parentIndexPath.indexPathByAddingIndex(idx);
		}
	}
	
	public void setParentIndexPath(IndexPath path) {
		_parentIndexPath = path;
	}
	public IndexPath getParentIndexPath() {
		return _parentIndexPath;
	}
	public void setIndex(int index) {
		String json = MemberListAdapter.nodeManager.command("setIndex("+this.toJSON()+","+index+")");
		Log.d("CellNode:setIndex",json);
		_index = index;
	}
	
	public int size() {
		String json = MemberListAdapter.nodeManager.command("size("+this.toJSON()+")");
		Log.d("CellNode:size",json);
		return Integer.parseInt(json);
	}
	
	public void add(CellNode node) {
		String json = MemberListAdapter.nodeManager.command("add("+this.toJSON()+","+node.toJSON()+")");
		Log.d("command","add("+this.toJSON()+","+node.toJSON()+")");
		Log.d("CellNode:add",json);
	}
	
	public CellNode get(int i) {
		String json = MemberListAdapter.nodeManager.command("get("+this.toJSON()+","+i+")");
		Log.d("CellNode:get",json);
		return new CellNode(json);
	}
	
	public String toJSON() {
		StringBuilder sb = new StringBuilder();
		String q = "\"";
		String c = ":";
		sb.append("{");
		int unfolded = (_unfolded==true?1:0);
		int root = (isRoot==true?1:0);
		sb.append(q).append("type").append(q).append(c).append(type).append(",");
		sb.append(q).append("isUnfolded").append(q).append(c).append(unfolded).append(",");
		if(_parentIndexPath != null)
			sb.append(q).append("parentIndexPath").append(q).append(c).append(q).append(_parentIndexPath).append(q).append(",");
		sb.append(q).append("indexPath").append(q).append(c).append(q).append(getIndexPath().toString()).append(q).append(",");
		sb.append(q).append("index").append(q).append(c).append(_index).append(",");
		sb.append(q).append("isRoot").append(q).append(c).append(root);
		
		sb.append("}");
		
		return sb.toString();
	}
	
	public void check() {
		String json = MemberListAdapter.nodeManager.command("check("+this.toJSON()+")");
		Log.d("CellNode", "CHECK");
	}
	
	
	public String collect() {
		String json = MemberListAdapter.nodeManager.command("collect()");
		Log.d("CellNode", "COLLECT");
		return json;
	}
	
	public long[] collectInLongArray() {
		String json = MemberListAdapter.nodeManager.command("collect()");
		Log.d("CellNode", "COLLECT");
		String[] _indexes = json.split("[^0-9]");
		long[] indexes = new long[_indexes.length];
		for(int i=0; i<indexes.length; i++) {
			indexes[i] = Long.parseLong(_indexes[i]);
		}
		return indexes;
	}
	
	public ArrayList<MemberManager> collectInUserArrayList() {
		String json = MemberListAdapter.nodeManager.command("collect()");
		Log.d("CellNode", "COLLECT");
		String[] _indexes = json.split("[^0-9]");
		ArrayList<MemberManager> users = new ArrayList<MemberManager>();
		for(int i=0; i<_indexes.length; i++) {
			users.add(MemberManager.userWithIdx(Long.parseLong(_indexes[i])));
		}
		return users;
	}
	
	
	
	
	
	
	
	public static class NodeManager {
		public WebView w;
		private Context context;
		public jsPlugin commander;
		public MemberListAdapter adapter;
		
		public NodeManager(Context context, MemberListAdapter adapter) {
			this.context = context;
			this.adapter = adapter;
			w = new WebView(context);
			w.getSettings().setJavaScriptEnabled(true);
			commander = new jsPlugin();
	        w.addJavascriptInterface(commander, "jsPlugin");
	        w.setWebViewClient(new jsWebViewClient());
	        w.loadUrl("file:///android_asset/www/nodeManager.html");
		}
		
	    public String command(String command) {
	    	return commander.getJSValue(w, command);
	    }
		
	    public String collect() {
			String json = command("collect()");
			Log.d("CellNode", "COLLECT");
			return json;
		}
		
		public long[] collectInLongArray() {
			String json = command("collect()");
			Log.d("CellNode", "COLLECT");
			String[] _indexes = json.split(" ");
			if(_indexes.length <2 &&  (_indexes[0].trim().length() == 0|| _indexes[0].trim().equals("")) ) return null;
			long[] indexes = new long[_indexes.length];
			for(int i=0; i<_indexes.length; i++) {
				IndexPath ip = IndexPath.indexPathWithString(_indexes[i]);
				MemberManager user = (MemberManager)adapter.objectForRowAtIndexPath(ip);
				indexes[i] = user.idx;
			}
					
			return indexes;
		}
		
		public ArrayList<MemberManager> collectInUserArrayList() {
			String json = command("collect()");
			Log.d("CellNode", "COLLECT");
			String[] _indexes = json.split("[^0-9]");
			ArrayList<MemberManager> users = new ArrayList<MemberManager>();
			for(int i=0; i<_indexes.length; i++) {
				users.add(MemberManager.userWithIdx(Long.parseLong(_indexes[i])));
			}
			return users;
		}
	    
		private class jsPlugin {
	    	private CountDownLatch latch = null;
	    	private String returnValue;
	    	public String getJSValue(WebView webView, String expression) {
	    		latch = new CountDownLatch(1);
	    		String code = "javascript:window."+getInterfaceName()+".setValue((function() {try{return "+expression +
	    				"+\"\";}catch(js_eval_err){return js_eval_err+'';}})());";
	    		webView.loadUrl(code);
	    		try {   
					                // Set a 1 second timeout in case there's an error
						//latch.await(1, TimeUnit.SECONDS);
	    				latch.await();
						return returnValue;
					} catch (InterruptedException e) {
						Log.e("jsPlugin", "Interrupted", e);
					}
					return null;
	    	}
	    	
	    	public void setValue(String value)
	    	{
	    		returnValue = value;
	    		try { latch.countDown(); } catch (Exception e) {} 
	    	}
	    	
	    	public String getInterfaceName(){
	    		return "jsPlugin";
	    	}
	    	
	    }
		
	    private class jsWebViewClient extends WebViewClient {
	    	@Override
	    	public void onPageFinished(WebView view, String url) {
	    		adapter.nodeManagerReady = true;
	    		adapter.refresh();
	    		super.onPageFinished(view, url);
	    	}
	    	/*
	    	@Override
	    	public boolean shouldOverrideUrlLoading(WebView view, String url) {
	    		// TODO Auto-generated method stub
	    		// WebViewClient 클래스를 상속받아서 만들었는데, 
	    		//WebViewClient 는 WebView 에서 로딩한 웹페이지의 링크를 클릭했을 때, 
	    		//해당 URL을 로딩하는 데 필요합니다.
	    		view.loadUrl(url);
	    		return super.shouldOverrideUrlLoading(view, url);
	    	}
	    	*/
	    }
	  
	}

}