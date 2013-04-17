package kr.go.KNPA.Romeo.Settings;

import kr.go.KNPA.Romeo.R;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

public class SettingsFragment extends Fragment {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		
		LinearLayout view = (LinearLayout) inflater.inflate(R.layout.settings_fragment, container, false);
		
		// 알림 //
		SettingsCellMaker.makeSectionHeader(inflater, container, "알림");
		SettingsCellMaker.makeCell(inflater, container, SettingsCellMaker.ONE_LINE, SettingsCellMaker.CONTROL_CHECKBOX);
		SettingsCellMaker.makeCell(inflater, container, SettingsCellMaker.ONE_LINE, SettingsCellMaker.CONTROL_NONE);
		SettingsCellMaker.makeCell(inflater, container, SettingsCellMaker.ONE_LINE, SettingsCellMaker.CONTROL_NONE);
		
		// 데이터 //
		SettingsCellMaker.makeSectionHeader(inflater, container, "데이터");
		SettingsCellMaker.makeCell(inflater, container, SettingsCellMaker.ONE_LINE, SettingsCellMaker.CONTROL_NONE);
		
		// 정보 //
		SettingsCellMaker.makeSectionHeader(inflater, container, "정보");
		SettingsCellMaker.makeCell(inflater, container, SettingsCellMaker.ONE_LINE, SettingsCellMaker.CONTROL_ARROW);
		//view.addView(child)
		
		return view;
	}
}
