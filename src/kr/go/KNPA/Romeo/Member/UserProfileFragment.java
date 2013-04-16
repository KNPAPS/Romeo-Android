package kr.go.KNPA.Romeo.Member;

import kr.go.KNPA.Romeo.MainActivity;
import kr.go.KNPA.Romeo.R;
import kr.go.KNPA.Romeo.Util.ImageManager;
import kr.go.KNPA.Romeo.Util.UserInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class UserProfileFragment extends Fragment {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.user_profile_fragment, container, false);
		
		Button menuBT = (Button)view.findViewById(R.id.menu);
		menuBT.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				MainActivity.sharedActivity().toggle();
				
			}
		});
		
		ImageView userPicIV = (ImageView)view.findViewById(R.id.userPic);
		ImageManager im = new ImageManager();
		im.loadToImageView(ImageManager.PROFILE_SIZE_MEDIUM, UserInfo.getUserIdx(getActivity()), userPicIV);
		//userPicIV.setImageResource(R.drawable.user_pic_default);
		
		TextView nameTV = (TextView)view.findViewById(R.id.name);
		nameTV.setText(UserInfo.getName(getActivity()));
		
		TextView rankTV = (TextView)view.findViewById(R.id.rank);
		rankTV.setText(User.RANK[UserInfo.getRank(getActivity())]);
		
		TextView departmentTV = (TextView)view.findViewById(R.id.department);
		departmentTV.setText(UserInfo.getDepartment(getActivity()));
		
		return view;
	}
}
