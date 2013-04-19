package kr.go.KNPA.Romeo.Member;

import kr.go.KNPA.Romeo.R;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.LinearLayout;
import android.widget.Spinner;

public class ConditionalSearch extends LinearLayout implements OnItemSelectedListener{

	public LinearLayout conditionsLL;
	private LinearLayout currentConditionLL;
	private Spinner cLocationSP;
	private Spinner cRankSP;
	private Spinner cDepartmentSP;
	private Spinner cRoleSP;
	
	public ConditionalSearch(Context context) {
		this(context, null);
	}

	public ConditionalSearch(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context, attrs);
	}

	public ConditionalSearch(Context context, AttributeSet attrs, int defStyle) {
		this(context, attrs);
	}

	public void init(Context context, AttributeSet attrs) {
		LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.member_conditional_search, this, false);
		
		conditionsLL = (LinearLayout)view.findViewById(R.id.conditions);
		loadCurrentConditionViewGroup();
		
		this.addView(view);
	}

	private void loadCurrentConditionViewGroup() {
		int count = conditionsLL.getChildCount();
		currentConditionLL = (LinearLayout)conditionsLL.getChildAt(count-1);
		cLocationSP = (Spinner)currentConditionLL.findViewById(R.id.conditionLocation);
		cRankSP = (Spinner)currentConditionLL.findViewById(R.id.conditionRank);
		cRoleSP = (Spinner)currentConditionLL.findViewById(R.id.conditionRole);
		cDepartmentSP = (Spinner)currentConditionLL.findViewById(R.id.conditionDepartment);
		
		cLocationSP.setOnItemSelectedListener(this);
		cRankSP.setOnItemSelectedListener(this);
		cRoleSP.setOnItemSelectedListener(this);
		cDepartmentSP.setOnItemSelectedListener(this);
	}
	
	private void unloadCurrentConditionViewGroup() {
		
		cLocationSP.setOnItemSelectedListener(null);
		cRankSP.setOnItemSelectedListener(null);
		cRoleSP.setOnItemSelectedListener(null);
		cDepartmentSP.setOnItemSelectedListener(null);
		
		currentConditionLL = null;
		cLocationSP = null;
		cRankSP = null;
		cRoleSP = null;
		cDepartmentSP = null;
		
		
	}
	
	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position, long rowid) {
		//parent	The AdapterView where the selection happened
		//view		The view within the AdapterView that was clicked
		//position	The position of the view in the adapter
		//id		The row id of the item that is selected
		//  Callback method to be invoked when an item in this view has been selected. 
		// This callback is invoked only when the newly selected position is different
		// from the previously selected position or if there was no selected item.

		switch(parent.getId()) {
			case R.id.conditionLocation : break;
			case R.id.conditionDepartment : break;
			case R.id.conditionRank : break;
			case R.id.conditionRole : break;
		}

	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
		//parent	The AdapterView that now contains no selected item.
		// Callback method to be invoked when the selection disappears from this view. 
		//The selection can disappear for instance when touch is activated or when the adapter becomes empty.
	}
}
