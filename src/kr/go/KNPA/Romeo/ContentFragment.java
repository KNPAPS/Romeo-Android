package kr.go.KNPA.Romeo;


import java.io.IOException;
import java.util.HashMap;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import kr.go.KNPA.Romeo.Util.Connection;
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
