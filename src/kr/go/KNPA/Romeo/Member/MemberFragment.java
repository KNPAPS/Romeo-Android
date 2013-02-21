/**
 * 
 */
package kr.go.KNPA.Romeo.Member;

import kr.go.KNPA.Romeo.MainActivity;
import kr.go.KNPA.Romeo.R;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

/**
 * @author pong0923
 *
 */
public class MemberFragment extends Fragment {

	/**
	 * 
	 */
	public MemberFragment() {
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {

				super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.member_fragment, container, false);
		
		Button lbb = (Button)view.findViewById(R.id.left_bar_button);
		Button rbb = (Button)view.findViewById(R.id.right_bar_button);
		TextView titleView = (TextView)view.findViewById(R.id.title);
		
		lbb.setText(R.string.menu);
		rbb.setVisibility(View.INVISIBLE);
		titleView.setText(R.string.memberTitle);
		
		lbb.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				MainActivity.sharedActivity().toggle();
			}
		});
		
		return view;
	}

}
