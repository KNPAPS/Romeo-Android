package kr.go.KNPA.Romeo.Settings;

import java.util.ArrayList;
import java.util.HashMap;

import kr.go.KNPA.Romeo.MainActivity;
import kr.go.KNPA.Romeo.R;
import kr.go.KNPA.Romeo.Config.VibrationPattern;
import kr.go.KNPA.Romeo.Util.UserInfo;
import kr.go.KNPA.Romeo.Util.WaiterView;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class SettingsFragment extends Fragment {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		
		LinearLayout view = (LinearLayout) inflater.inflate(R.layout.settings_fragment, container, false);
		initNavigationBar(view, getString(R.string.settingsTitle), true, false, getString(R.string.menu), null,
				new OnClickListener() {	@Override	public void onClick(View v) {	MainActivity.sharedActivity().toggle();	}	}
				, null);
		
		// 알림 //
		final RelativeLayout hNoti 		= SettingsCellMaker.makeSectionHeader(inflater, container, "알림");
		view.addView(hNoti);
		final RelativeLayout cWillNoti 	= SettingsCellMaker.makeCell(inflater, container, SettingsCellMaker.ONE_LINE, SettingsCellMaker.CONTROL_CHECKBOX);
		view.addView(cWillNoti);
		view.addView(SettingsCellMaker.makeListCellDivider(getActivity(), view));
		final RelativeLayout cNotiBell 	= SettingsCellMaker.makeCell(inflater, container, SettingsCellMaker.TWO_LINE, SettingsCellMaker.CONTROL_NONE);
		view.addView(cNotiBell);
		view.addView(SettingsCellMaker.makeListCellDivider(getActivity(), view));
		final RelativeLayout cNotiVib 	= SettingsCellMaker.makeCell(inflater, container, SettingsCellMaker.TWO_LINE, SettingsCellMaker.CONTROL_NONE);
		view.addView(cNotiVib);
		
		
		// 데이터 //
		final RelativeLayout hData 		= SettingsCellMaker.makeSectionHeader(inflater, container, "데이터");
		view.addView(hData);
		final RelativeLayout cClearData	= SettingsCellMaker.makeCell(inflater, container, SettingsCellMaker.ONE_LINE, SettingsCellMaker.CONTROL_NONE);
		view.addView(cClearData);
		
		
		// 정보 //
		final RelativeLayout hInfo 		= SettingsCellMaker.makeSectionHeader(inflater, container, "정보");
		view.addView(hInfo);
		final RelativeLayout cDevInfo 	= SettingsCellMaker.makeCell(inflater, container, SettingsCellMaker.ONE_LINE, SettingsCellMaker.CONTROL_ARROW);
		view.addView(cDevInfo);
		
		SettingsCellMaker.setTitle(cWillNoti, "알림");
		SettingsCellMaker.setOnCheckedChangeListener(
				SettingsCellMaker.getCheckBox(cWillNoti),
				new OnCheckedChangeListener() {
					
					@Override
					public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
						cNotiBell.setEnabled(isChecked);
						cNotiVib.setEnabled(isChecked);
						
					}
				});
		
		SettingsCellMaker.setTitle(cNotiBell, "알림 소리 선택");
		SettingsCellMaker.setContent(cNotiBell, "");	// TODO : 설정값 세팅
		SettingsCellMaker.setOnClickListner(cNotiBell, 
				new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						final RingtoneManager rm = new RingtoneManager(getActivity());
						rm.setType(RingtoneManager.TYPE_NOTIFICATION);
						Cursor cRingtone = rm.getCursor();
						
						CursorAdapter rtAdapter = new CursorAdapter(getActivity(), cRingtone, false) {
							
							@Override
							public View newView(Context context, Cursor c, ViewGroup parent) {	
								return 	((LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
												.inflate(android.R.layout.simple_list_item_1, parent);	
							}
							
							@Override
							public void bindView(View convertView, Context context, Cursor c) {
									TextView titleTV = (TextView)convertView.findViewById(android.R.id.text1);
									titleTV.setText(c.getString(RingtoneManager.TITLE_COLUMN_INDEX));
							}
						};
						

						DialogInterface.OnClickListener rtClicked = new DialogInterface.OnClickListener() {
							
							@Override
							public void onClick(DialogInterface dialog, int which) {
								Uri rtUri = rm.getRingtoneUri(which);
								// TODO : 저장
								Cursor cRington = rm.getCursor();
								cRington.moveToPosition(which);
								String title = cRington.getString(RingtoneManager.TITLE_COLUMN_INDEX);
								SettingsCellMaker.setContent(cNotiBell, title);
								dialog.dismiss();
							}
						};
						
						AlertDialog adRington = new AlertDialog.Builder(getActivity())
															   .setIcon(R.drawable.icon)
															   .setTitle("알림 소리 선택")
															   .setAdapter(rtAdapter, rtClicked)
															   .show();
					}
				});
		
		SettingsCellMaker.setTitle(cNotiVib, "진동 패턴 선택");
		String vibPatternKey = UserInfo.getVibrationPattern(getActivity());
		SettingsCellMaker.setContent(cNotiVib, VibrationPattern.getTitle(vibPatternKey));
		SettingsCellMaker.setOnClickListner(cNotiVib, 
				new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						
						ArrayList<HashMap<String, Object>> vibs = new ArrayList<HashMap<String, Object>> ();
						
						final ArrayAdapter<HashMap<String, Object>> vbAdapter = 
								new ArrayAdapter<HashMap<String, Object>>(
										getActivity(), 
										android.R.layout.simple_list_item_1, 
										android.R.id.text1, 
										vibs);
						

						DialogInterface.OnClickListener vbClicked = new DialogInterface.OnClickListener() {
							
							@Override
							public void onClick(DialogInterface dialog, int which) {
								HashMap<String, Object> vibObj = vbAdapter.getItem(which);
								
								// TODO : 저장
								String title = (String)vibObj.get("");	// TODO : 키값
								SettingsCellMaker.setContent(cNotiVib, title);
								dialog.dismiss();
							}
						};
						
						AlertDialog adRington = new AlertDialog.Builder(getActivity())
															   .setIcon(R.drawable.icon)
															   .setTitle("알림 소리 선택")
															   .setAdapter(vbAdapter, vbClicked)
															   .show();
					}
				});
		
		SettingsCellMaker.setTitle(cClearData, "캐시 데이터 삭제");
		SettingsCellMaker.setOnClickListner(	cClearData, 
				new OnClickListener() {
					
					@Override
					public void onClick(View view) {
						WaiterView.showDialog(getActivity());
						Toast.makeText(getActivity(), "캐시 데이터가 초기화 되었습니다.", Toast.LENGTH_SHORT).show();
						// TODO : 캐시 데이터 초기화
						WaiterView.dismissDialog(getActivity());
					}
				});
		
		SettingsCellMaker.setTitle(cDevInfo, "개발 정보");
		SettingsCellMaker.setOnClickListner(	cDevInfo, 
				new OnClickListener() {
					
					@Override
					public void onClick(View view) {
						// TODO : 개발정보 준비
						Toast.makeText(getActivity(), "준비중입니다.", Toast.LENGTH_SHORT).show();
					}
				});

		return view;
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
