package kr.go.KNPA.Romeo.Settings;

import java.util.Calendar;

import kr.go.KNPA.Romeo.MainActivity;
import kr.go.KNPA.Romeo.R;
import kr.go.KNPA.Romeo.Config.Event;
import kr.go.KNPA.Romeo.Config.KEY;
import kr.go.KNPA.Romeo.Config.StatusCode;
import kr.go.KNPA.Romeo.Connection.Connection;
import kr.go.KNPA.Romeo.Connection.Data;
import kr.go.KNPA.Romeo.Connection.Payload;
import kr.go.KNPA.Romeo.Util.CallbackEvent;
import kr.go.KNPA.Romeo.Util.UserInfo;
import kr.go.KNPA.Romeo.Util.WaiterView;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class BugReportFragment extends Fragment {

	private EditText content;
	
	public BugReportFragment() {
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		View view = inflater.inflate(R.layout.bug_report_fragment, container, false);
		
		OnClickListener lbbOnClickListener = new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				MainActivity.sharedActivity().toggle();
			}
		};
		
		OnClickListener rbbOnClickListener = new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				submit();
			}
		};
		
		initNavigationBar(view, "버그 리포트", true, true, "메뉴", "전송", lbbOnClickListener, rbbOnClickListener);
		
		this.content = (EditText)view.findViewById(R.id.content);
		return view;
	}
	
	private void submit() {

		Data reqData = new Data()
		.add(0, KEY.MESSAGE.SENDER_IDX, UserInfo.getUserIdx(getActivity()))
		.add(0, KEY.MESSAGE.CREATED_TS, Calendar.getInstance().getTime())
		.add(0, KEY.MESSAGE.CONTENT, content.getText().toString());
		
		Payload request = new Payload().setEvent("BUG:REPORT").setData(reqData);

		CallbackEvent<Payload, Integer, Payload> callback = new CallbackEvent<Payload, Integer, Payload>() {
			@Override
			public void onPreExecute(Payload params) {
				WaiterView.showDialog(getActivity());
				WaiterView.setTitle("전송중입니다");
			}
			
			@Override
			public void onPostExecute(Payload result) {
				WaiterView.dismissDialog(getActivity());
				String toastMSG = "";
				if( result.getStatusCode() == StatusCode.SUCCESS ) {
					toastMSG = "성공적으로 전송했습니다";
				} else {
					toastMSG = "오류가 발생!\n다시 시도해 주세요";
				}
				
				Toast.makeText(getActivity(), toastMSG, Toast.LENGTH_SHORT).show();
			}
		};
		
		Connection conn = new Connection().requestPayload(request).async(true).callBack(callback).request();

	}
	
	protected void initNavigationBar(View parentView, String titleText, boolean lbbVisible, boolean rbbVisible, String lbbTitle, String rbbTitle, OnClickListener lbbOnClickListener, OnClickListener rbbOnClickListener) {
		
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
