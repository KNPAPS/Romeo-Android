/**
 * 
 */
package kr.go.KNPA.Romeo.Chat;

import kr.go.KNPA.Romeo.R;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * @author pong0923
 *
 */
public class MeetingFragment extends Fragment {

	/**
	 * 
	 */
	public MeetingFragment() {
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {

				super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.meeting_fragment, container, false);

		
		return view;
	}

}