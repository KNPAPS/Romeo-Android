package kr.go.KNPA.Romeo.Settings;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import kr.go.KNPA.Romeo.MainActivity;
import kr.go.KNPA.Romeo.R;
import kr.go.KNPA.Romeo.Config.ConnectionConfig;
import kr.go.KNPA.Romeo.Config.VibrationPattern;
import kr.go.KNPA.Romeo.Util.RomeoDialog;
import kr.go.KNPA.Romeo.Util.UserInfo;
import kr.go.KNPA.Romeo.Util.WaiterView;
import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Vibrator;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
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
		final RelativeLayout cPreViewMessage 	= SettingsCellMaker.makeCell(inflater, container, SettingsCellMaker.ONE_LINE, SettingsCellMaker.CONTROL_CHECKBOX);
		view.addView(cPreViewMessage);
		view.addView(SettingsCellMaker.makeListCellDivider(getActivity(), view));
		final RelativeLayout cWillNoti 	= SettingsCellMaker.makeCell(inflater, container, SettingsCellMaker.ONE_LINE, SettingsCellMaker.CONTROL_CHECKBOX);
		view.addView(cWillNoti);
		view.addView(SettingsCellMaker.makeListCellDivider(getActivity(), view));
		final RelativeLayout cNotiBell 	= SettingsCellMaker.makeCell(inflater, container, SettingsCellMaker.TWO_LINE, SettingsCellMaker.CONTROL_NONE);
		view.addView(cNotiBell);
		view.addView(SettingsCellMaker.makeListCellDivider(getActivity(), view));
		final RelativeLayout cNotiVib 	= SettingsCellMaker.makeCell(inflater, container, SettingsCellMaker.TWO_LINE, SettingsCellMaker.CONTROL_NONE);
		view.addView(cNotiVib);
		
		
		/*// 데이터 //
		final RelativeLayout hData 		= SettingsCellMaker.makeSectionHeader(inflater, container, "데이터");
		view.addView(hData);
		final RelativeLayout cClearData	= SettingsCellMaker.makeCell(inflater, container, SettingsCellMaker.ONE_LINE, SettingsCellMaker.CONTROL_NONE);
		view.addView(cClearData);
		*/
		
		// 정보 //
		final RelativeLayout hInfo 		= SettingsCellMaker.makeSectionHeader(inflater, container, "정보");
		view.addView(hInfo);
		final RelativeLayout cDevInfo 	= SettingsCellMaker.makeCell(inflater, container, SettingsCellMaker.ONE_LINE, SettingsCellMaker.CONTROL_ARROW);
		view.addView(cDevInfo);
		
		boolean isToastEnabled = UserInfo.getToastEnabled(getActivity());
		((CheckBox)SettingsCellMaker.getCheckBox(cPreViewMessage)).setChecked(isToastEnabled);
		SettingsCellMaker.setTitle(cPreViewMessage, "메시지 미리보기");
		SettingsCellMaker.setOnCheckedChangeListener(
				SettingsCellMaker.getCheckBox(cPreViewMessage),
				new OnCheckedChangeListener() {
					
					@Override
					public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
						UserInfo.setToastEnabled(getActivity(), isChecked);
					}
				});
		
		
		
		SettingsCellMaker.setTitle(cWillNoti, "알림");
		SettingsCellMaker.setOnCheckedChangeListener(
				SettingsCellMaker.getCheckBox(cWillNoti),
				new OnCheckedChangeListener() {
					
					@Override
					public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
						UserInfo.setAlarmEnabled(getActivity(), isChecked);
						SettingsCellMaker.setEnabled(cNotiBell, isChecked);
						SettingsCellMaker.setEnabled(cNotiVib, isChecked);
					}
				});
		
		boolean isAlarmEnabled = UserInfo.getAlarmEnabled(getActivity());
		((CheckBox)SettingsCellMaker.getCheckBox(cWillNoti)).setChecked(isAlarmEnabled);
		SettingsCellMaker.setTitle(cNotiBell, "알림 소리 선택");
		
		final RingtoneManager rm = new RingtoneManager(getActivity());
		rm.setType(RingtoneManager.TYPE_NOTIFICATION);
		
		Uri ringtoneUri = UserInfo.getRingtone(getActivity());
		int rtPos = rm.getRingtonePosition(ringtoneUri);
		
		String ringtoneTitle = "";
		if(rtPos >= 0) {
			Cursor cRingtone = rm.getCursor();
			cRingtone.moveToPosition(rtPos);
			ringtoneTitle = cRingtone.getString(RingtoneManager.TITLE_COLUMN_INDEX);
		}
		
		SettingsCellMaker.setContent(cNotiBell, ringtoneTitle);
		SettingsCellMaker.setOnClickListner(cNotiBell, 
				new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						
						Cursor cRingtone = rm.getCursor();
						
						ArrayList<String> rtTitleArray = new ArrayList<String>();
						cRingtone.moveToFirst();
						while( !cRingtone.isAfterLast() ) {
							rtTitleArray.add(cRingtone.getString(RingtoneManager.TITLE_COLUMN_INDEX));
							cRingtone.moveToNext();
						}
						
						
						ArrayAdapter<String> rtAdapter = new ArrayAdapter<String>(getActivity(),R.layout.dialog_menu_cell2, R.id.title, rtTitleArray);
						
//						CursorAdapter rtAdapter = new CursorAdapter(getActivity(), cRingtone, false) {
//							
//							@Override
//							public View newView(Context context, Cursor c, ViewGroup parent) {	
//								return 	((LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
//												.inflate(android.R.layout.simple_list_item_1, parent);	
//							}
//							
//							@Override
//							public void bindView(View convertView, Context context, Cursor c) {
//									TextView titleTV = (TextView)convertView.findViewById(android.R.id.text1);
//									titleTV.setText(c.getString(RingtoneManager.TITLE_COLUMN_INDEX));
//							}
//						};
						

						DialogInterface.OnClickListener rtClicked = new DialogInterface.OnClickListener() {
							
							@Override
							public void onClick(DialogInterface dialog, int which) {
								Uri rtUri = rm.getRingtoneUri(which);
								
								UserInfo.setRingtone(getActivity(), rtUri);
								
								Cursor cRington = rm.getCursor();
								cRington.moveToPosition(which);
								String title = cRington.getString(RingtoneManager.TITLE_COLUMN_INDEX);
								SettingsCellMaker.setContent(cNotiBell, title);
								
								Ringtone ringtone = RingtoneManager.getRingtone(getActivity(), rtUri);
								ringtone.play();
								
								dialog.dismiss();
							}
						};
						
						RomeoDialog adRington = new RomeoDialog.Builder(getActivity())
															   .setIcon(R.drawable.icon_dialog)
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
						
						ArrayList<HashMap<String, String>> vibs = VibrationPattern.getDictionary();
						
						final ArrayAdapter<HashMap<String, String>> vbAdapter = 
								new ArrayAdapter<HashMap<String, String>>(
										getActivity(), 
										R.layout.dialog_menu_cell2, 
										R.id.title, 
										vibs) {
							@Override
							
							public View getView(int position, View convertView,	ViewGroup parent) {
								super.getView(position, convertView, parent);
								HashMap<String, String> vibObj = getItem(position);
								convertView = ((LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE))
													.inflate(R.layout.dialog_menu_cell2, parent, false);
								TextView tv = (TextView)convertView.findViewById(R.id.title); 
								(tv).setText(vibObj.get(VibrationPattern.DICTIONARY_TITLE));
								return convertView; 
							}}; 
						

						DialogInterface.OnClickListener vbClicked = new DialogInterface.OnClickListener() {
							
							@Override
							public void onClick(DialogInterface dialog, int which) {
								HashMap<String, String> vibObj = vbAdapter.getItem(which);
								
								String patternKey = vibObj.get( VibrationPattern.DICTIONARY_KEY);
								String title = vibObj.get(VibrationPattern.DICTIONARY_TITLE);
								
								UserInfo.setVibrationPattern(getActivity(), patternKey);
								SettingsCellMaker.setContent(cNotiVib, title);
								
								Vibrator vibrator = (Vibrator)getActivity().getSystemService(Context.VIBRATOR_SERVICE);
								vibrator.vibrate(
										VibrationPattern.getPattern(
												patternKey
										), -1
									);
								
								
								dialog.dismiss();
							}
						};
						
						RomeoDialog adVibration = new RomeoDialog.Builder(getActivity())
															   .setIcon(R.drawable.icon_dialog)
															   .setTitle("진동 패턴 선택")
															   .setAdapter(vbAdapter, vbClicked)
															   .show();
					}
				});
		
		/*SettingsCellMaker.setTitle(cClearData, "캐시 데이터 삭제");
		SettingsCellMaker.setOnClickListner(	cClearData, 
				new OnClickListener() {
					
					@Override
					public void onClick(View view) {
						WaiterView.showDialog(getActivity());
						Toast.makeText(getActivity(), "캐시 데이터가 초기화 되었습니다.", Toast.LENGTH_SHORT).show();
						// TODO : 캐시 데이터 초기화
						WaiterView.dismissDialog(getActivity());
					}
				});*/
		
		SettingsCellMaker.setTitle(cDevInfo, "업데이트");
		SettingsCellMaker.setOnClickListner(	cDevInfo, 
				new OnClickListener() {
					
					@Override
					public void onClick(View view) {
						
						checkVersionAndDownload_Temporary();
			
						
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

	private void checkVersionAndDownload_Temporary()
	{
		
		PackageInfo pInfo = null;
		try
		{
			pInfo = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0);
		}
		catch (NameNotFoundException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		final String version = pInfo.versionName;
		
		WaiterView.showDialog(getActivity());
		new Thread() {
			@SuppressLint("InlinedApi")
			public void run() 
			{
				try {
					URL url = new URL(ConnectionConfig.SERVER_HOST+"juliette/version.txt");
					
					BufferedReader in = new BufferedReader(
					            new InputStreamReader(
					            url.openStream()));
	
					String lastestVersion = "";
					String tmp;
					while ((tmp = in.readLine()) != null)
					    lastestVersion += tmp;
	
					in.close();
					if (lastestVersion.equals(version))
					{
						getActivity().runOnUiThread(new Runnable(){
							public void run() 
							{
								Toast.makeText(getActivity(), "이미 최신버전입니다.", Toast.LENGTH_SHORT).show();
								WaiterView.dismissDialog(getActivity());
							}
						});
					}
					else
					{
						getActivity().runOnUiThread(new Runnable(){
							public void run() 
							{
								WaiterView.dismissDialog(getActivity());
								Toast.makeText(getActivity(), "다운로드를 시작합니다.\n완료가 되면 재설치해주세요.", Toast.LENGTH_SHORT).show();
							}
						});
						
						Uri uri = Uri.parse("http://vo.to/daon");

						DownloadManager.Request request = new DownloadManager.Request(uri);
						List<String> pathSegments = uri.getPathSegments();
						request.setTitle("다온");
						request.setDescription("다온");
						if (Build.VERSION.SDK_INT >= 11)							
						request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
						request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, pathSegments.get(pathSegments.size()-1));
						Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).mkdirs();
						((DownloadManager)getActivity().getSystemService(Context.DOWNLOAD_SERVICE)).enqueue(request);
						 
					}
				}
				catch (Exception e)
				{
					getActivity().runOnUiThread(new Runnable(){
						public void run() 
						{
							WaiterView.dismissDialog(getActivity());
							Toast.makeText(getActivity(), "에러가 발생했습니다.\n다시 시도해주세요.", Toast.LENGTH_SHORT).show();
						}
					});
				}
			}
		}.start();
		
	}
}
