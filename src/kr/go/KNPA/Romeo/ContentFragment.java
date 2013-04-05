package kr.go.KNPA.Romeo;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
public class ContentFragment extends Fragment {
	String title;
	
	public ContentFragment() {
		super();
	}
	
	public ContentFragment(String title) {
		this();
		this.title = title;
	}
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		return inflater.inflate(R.layout.content_fragment, container, false);
	}
}
