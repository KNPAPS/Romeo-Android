package kr.go.KNPA.Romeo.Menu;

import kr.go.KNPA.Romeo.R;
import kr.go.KNPA.Romeo.R.layout;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class MenuFragment extends Fragment {
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		return inflater.inflate(R.layout.menu_fragment, container, false);
	}
}
