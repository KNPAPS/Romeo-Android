package kr.go.KNPA.Romeo.Settings;

import kr.go.KNPA.Romeo.R;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;


public class SettingsCellMaker {
	
	private static final int TYPE_ONELINE 					= 0;
	private static final int TYPE_ONELINE_CONTROL 			= 1;
	private static final int TYPE_TWOLINE 			= 10;
	private static final int TYPE_TWOLINE_CONTROL 	= 11;
	
	public static final int ONE_LINE = 1;
	public static final int TWO_LINE = 2;
	
	public static final int CONTROL_NONE		= 0;
	public static final int CONTROL_ARROW		= 1;
	public static final int CONTROL_BUTTON		= 2;
	public static final int CONTROL_CHECKBOX	= 3;
	
	private static RelativeLayout makeCell(LayoutInflater inflater, ViewGroup parent, int type) {
		switch(type) {
			case TYPE_ONELINE 			:	
				return (RelativeLayout)inflater.inflate(R.layout.settings_cell, 				parent, false);
			
			case TYPE_ONELINE_CONTROL 	:	
				return (RelativeLayout)inflater.inflate(R.layout.settings_cell_control, 		parent, false);
			
			case TYPE_TWOLINE 			:	
				return (RelativeLayout)inflater.inflate(R.layout.settings_cell_twoline, 		parent, false);
			
			case TYPE_TWOLINE_CONTROL 	:
			default 					:	
				return (RelativeLayout)inflater.inflate(R.layout.settings_cell_twoline_control,	parent, false);
		}
	}
	
	public static RelativeLayout makeCell(LayoutInflater inflater, ViewGroup parent, int nLines, int control) {
		//  1 <= nLines <= 2
		nLines = Math.min(nLines, 1);
		nLines = Math.max(nLines, 2);
		
		int type = (nLines-1)*10 + Math.max(control, 1);
		
		RelativeLayout cell = makeCell(inflater, parent, type);
		
		switch(control) {
		case CONTROL_NONE :  break;
		case CONTROL_ARROW : 
			getArrow(cell).setVisibility(View.VISIBLE);		break;
		case CONTROL_BUTTON :
			getButton(cell).setVisibility(View.VISIBLE);		break;
		case CONTROL_CHECKBOX :
			getCheckBox(cell).setVisibility(View.VISIBLE);	break;
		}
		
		return cell;
	}
	
	public static RelativeLayout setTitle(RelativeLayout cell, String title) {
		TextView titleTV = (TextView)cell.findViewById(R.id.title);
		if(titleTV != null)
			titleTV.setText(title);
		return cell;
	}
	
	public static RelativeLayout setContent(RelativeLayout cell, String content) {
		TextView contentTV = (TextView)cell.findViewById(R.id.content);
		if(contentTV != null)
			contentTV.setText(content);
		return cell;
	}
	
	public static Button setOnClickListener(Button button, OnClickListener listner) {
		//Button button = getButton(cell);
		if(button != null)
			button.setOnClickListener(listner);
		return button;
	}
	
	public static RelativeLayout setOnClickListner(RelativeLayout cell, OnClickListener listner) {
		cell.setOnClickListener(listner);
		return cell;
	}
	
	public static Button getButton(RelativeLayout cell) {
		return (Button)cell.findViewById(R.id.button);
	}
	
	public static ImageView getArrow(RelativeLayout cell) {
		return (ImageView)cell.findViewById(R.id.arrow);
	}
	
	public static CheckBox getCheckBox(RelativeLayout cell) {
		//checkBox
		return (CheckBox)cell.findViewById(R.id.checkBox);
	}
	
	public static RelativeLayout makeSectionHeader(LayoutInflater inflater, ViewGroup parent, String title) {
		RelativeLayout sectionHeader = (RelativeLayout)inflater.inflate(R.layout.section_header, parent, false);
		((TextView)sectionHeader.findViewById(R.id.title)).setText(title);
		return sectionHeader;
	}
}
