package kr.go.KNPA.Romeo;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class NotRegisteredActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LayoutInflater inflater = (LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.not_registered_activity, null);
		
		initNavigationBar(view, "", false, false, "", "", null, null);
		TextView header = (TextView)view.findViewById(R.id.header);
		TextView footer = (TextView)view.findViewById(R.id.footer);
		
		header.setText("사용자 등록이 되지 않았거나,\n등록 요청 중에 있습니다.\n\n사용자 등록이 완료된 후\n다시 실행해 주세요.");
		footer.setText("");
		
		setContentView(view);
		
		IntroActivity.sharedActivity().removeIntroView((ViewGroup) view);
	}
	
	@Override
	protected void onPause() {
		moveTaskToBack(true);
		android.os.Process.killProcess(android.os.Process.myPid());
	}
	
	private void initNavigationBar(View parentView, String titleText, boolean lbbVisible, boolean rbbVisible, String lbbTitle, String rbbTitle, OnClickListener lbbOnClickListener, OnClickListener rbbOnClickListener) {
			
		Button lbb = (Button)parentView.findViewById(R.id.left_bar_button);
		Button rbb = (Button)parentView.findViewById(R.id.right_bar_button);
		
		lbb.setVisibility((lbbVisible?View.VISIBLE:View.INVISIBLE));
		rbb.setVisibility((rbbVisible?View.VISIBLE:View.INVISIBLE));
		
		if(lbb.getVisibility() == View.VISIBLE) { lbb.setText(lbbTitle);	}
		
		if(rbb.getVisibility() == View.VISIBLE) { rbb.setText(rbbTitle);	}
		
		TextView titleView = (TextView)parentView.findViewById(R.id.title);
		titleView.setText(titleText);
		
		if(lbb.getVisibility() == View.VISIBLE) lbb.setOnClickListener(lbbOnClickListener);
		if(rbb.getVisibility() == View.VISIBLE) rbb.setOnClickListener(rbbOnClickListener);
	}
}
