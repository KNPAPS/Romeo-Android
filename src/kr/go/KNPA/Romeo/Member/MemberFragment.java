/**
 * 
 */
package kr.go.KNPA.Romeo.Member;

import kr.go.KNPA.Romeo.MainActivity;
import kr.go.KNPA.Romeo.R;
import kr.go.KNPA.Romeo.Util.DBManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author pong0923
 *
 */
public class MemberFragment extends Fragment {

	public static final int NOT_SPECIFIED = -777;
	public static final int TYPE_MEMBERLIST = 0;
	public static final int TYPE_FAVORITE = 1;
	
	private DBManager dbManager;
	public int type = NOT_SPECIFIED;
	
	public MemberFragment() {
		this(TYPE_MEMBERLIST);
	}
	
	public MemberFragment(int type) {
		this.type = type;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		dbManager = new DBManager(getActivity());
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = setup(inflater, container, savedInstanceState);
		
		return view;
	}
	
	private View setup(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = null;
		String titleText = null;
		String lbbText = null, rbbText = null;
		boolean lbbIsVisible = false;
		boolean rbbIsVisible = false;
		
		switch(this.type) {
		case TYPE_FAVORITE :
			view = inflater.inflate(R.layout.member_fragment, container, false);
			titleText = getString(R.string.memberFavoriteTitle);
			lbbText = getString(R.string.menu);
			rbbText = getString(R.string.add);
			lbbIsVisible = true;
			rbbIsVisible = true;
			break;
		default : 
		case TYPE_MEMBERLIST :
			view = inflater.inflate(R.layout.member_fragment, container, false);
			titleText = getString(R.string.memberListTitle);
			lbbText = getString(R.string.menu);
			rbbText = getString(R.string.dummy);
			lbbIsVisible = true;
			rbbIsVisible = false;
			break;
		}

		if(view != null) {
			MemberListView mlv = (MemberListView)view.findViewById(R.id.memberListView);
			try {
				mlv.setType(this.type);
			} catch (RuntimeException e) {
				mlv.setBackgroundResource(R.drawable.empty_set_background);
				Toast.makeText(getActivity(), "통신 오류가 발생했습니다.", Toast.LENGTH_SHORT);
				// TODO : getMembers 통신 오류 등이다. 다시 로드 할 수 있도록 조치를 취해야 하는데..
			}
		}
		Button lbb = (Button)view.findViewById(R.id.left_bar_button);
		Button rbb = (Button)view.findViewById(R.id.right_bar_button);
		
		lbb.setVisibility((lbbIsVisible?View.VISIBLE:View.INVISIBLE));
		rbb.setVisibility((rbbIsVisible?View.VISIBLE:View.INVISIBLE));
		
		if(lbb.getVisibility() == View.VISIBLE) { lbb.setText(lbbText);	}
		if(rbb.getVisibility() == View.VISIBLE) { rbb.setText(rbbText);	}
		
		TextView titleView = (TextView)view.findViewById(R.id.title);
		titleView.setText(titleText);
		
		if(lbb.getVisibility() == View.VISIBLE) {
			lbb.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					MainActivity.sharedActivity().toggle();
				}
			});
		}
		
		if(rbb.getVisibility() == View.VISIBLE) {
			rbb.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					
				}
			});
		}
		
		return view;
	}

}
