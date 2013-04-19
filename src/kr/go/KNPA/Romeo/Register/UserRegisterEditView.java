package kr.go.KNPA.Romeo.Register;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import kr.go.KNPA.Romeo.R;
import kr.go.KNPA.Romeo.Member.Department;
import kr.go.KNPA.Romeo.Member.MemberManager;
import kr.go.KNPA.Romeo.Member.User;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

/**
 * UserRegisterActivity의 View를 담당한다.
 */
public class UserRegisterEditView extends LinearLayout {
	
	/**
	 * @name Defined KEYs
	 * @{
	 */
	public static final int KEY_NAME 			= 0;
	public static final int KEY_DEPARTMENT 		= 1;
	public static final int KEY_RANK			= 2;
	public static final int KEY_ROLE			= 3;
	public static final int KEY_PIC				= 4;
	public static final int KEY_PASSWORD		= 5;
	public static final int KEY_CONFIRM			= 6;
	/** @} */
	
	public static final int REQUEST_PIC_PICKER = 300;
	private static final int DROPDOWN_MAX_LENGTH = 6;
	
	private static final String MAKE_DUMMY_SET	= "MAKE_DUMMY_SET";
	
	private View navBar;
	private View view;
	private int res;
	private int key;
	
	private UserRegisterActivity context;
	
	private ArrayList<Department> dummySet;
	private Department dummy;
	
	/**
	 * @name Constructors
	 * @{
	 */
	public UserRegisterEditView(Context context) 						{	this(context, null);	}
	public UserRegisterEditView(Context context, AttributeSet attrs)	{	super(context, attrs);	}
	public UserRegisterEditView(UserRegisterActivity activity, int key) {
		this(activity);
		this.key = key;
		this.context = activity;
		
		dummy = new Department();
		dummy.idx = null;
		dummy.name = context.getString(R.string.none);
		
		dummySet = new ArrayList<Department>(1);
		dummySet.add(dummy);
		
		//주어진 key에 따라 inflate할 XML파일을 정한다.
		switch(key) {
			case KEY_NAME : 		this.res = R.layout.edit_text;		break;
			case KEY_DEPARTMENT : 	this.res = R.layout.edit_dropdown6;	break;
			case KEY_RANK : 		this.res = R.layout.edit_dropdown;	break;
			case KEY_ROLE : 		this.res = R.layout.edit_text; break;//edit_dropdown;	break;
			case KEY_PASSWORD : 	this.res = R.layout.edit_text;		break;
			case KEY_PIC : 			this.res = R.layout.edit_pic;		break;
			case KEY_CONFIRM : 		this.res = R.layout.edit_confirm;	break;
		}
		
		// Layout차원에서 여러가지 설정을 한다.
		this.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		this.setOrientation(LinearLayout.VERTICAL);
		this.setBackgroundResource(R.color.lighter);
		
		// inflater를 구하고, 정해진 XML파일을 inflate 한다/
		LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		// NavigationBar 부터 Inflate하고, key를 통해 초기화 하여 현재 Layout (this)에 붙인다.
		View navBar = inflater.inflate(R.layout.navigation_bar, this, false);
		initNavigationBar(navBar, key);
		this.addView(navBar);	
		// 본격적인 View를 inflate 하고 현재 Layout (this)에 붙인다.
		view = inflater.inflate(this.res, this, false);
		this.addView(view);
		
		
		// 각종 Listener를 더한다.
		
		// Clear Edit이 존재한다면, Click Listener를 더해준다.
		Button clearEdit = getClearEditButton();
		if(clearEdit != null)  clearEdit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {	clearEdit(); }
		});
		
		// key 값에 따라 본격적인 초기화를 진행한다.
		switch(key) {
		case KEY_NAME:
			setHeaderTitle("본인 이름을 입력해 주세요.");
			setFooterTitle("띄어쓰기나 오타 없이 입력해주셔야\n정상적으로 등록이 이루어질 수 있습니다.");
			break;
			
		case KEY_DEPARTMENT:
		{
			// 초기 목록 정보 할당
			Spinner dd1 = getDropdown(0);
			ArrayList<Department> level1Set = getDepartment(null);
			DepartmentDropdownAdapter adapter1 = new DepartmentDropdownAdapter(context, level1Set);
			// TODO _adapter.setDropDownViewResource(DROPDOWN_VIEW_LAYOUT);
			dd1.setAdapter(adapter1);
			dd1.setOnItemSelectedListener(deptListener);
			dd1.setPrompt(context.getString(R.string.department)+" 선택");
			
/*			// 2~6번의 DropDown에도 Listener 할당
			for(int i=1; i<DROPDOWN_MAX_LENGTH; i++) {
				Spinner dd = getDropdown(i);
				DepartmentDropdownAdapter adapter = new DepartmentDropdownAdapter(context, dummySet);
				// TODO adapter.setDropDownViewResource(DROPDOWN_VIEW_LAYOUT);
				dd.setAdapter(adapter);
				dd.setPrompt(context.getString(R.string.department)+" 선택");
				if(i != DROPDOWN_MAX_LENGTH-1 )
					dd.setOnItemSelectedListener(deptListener);
			}
*/		}
			setHeaderTitle("본인 소속 부서를 선택해주세요.");
			setFooterTitle("");
			break;
			
		case KEY_RANK:
		{
			Spinner dd = getDropdown();
			ArrayList<String> ranks = new ArrayList<String>(User.RANK.length+1); // +1 : "선택해주세요"
			
			for(int i=0; i<User.RANK.length+1; i++) {
				if(i==0) {
					ranks.add( context.getString(R.string.letSelect) );
				} else {
					ranks.add( User.RANK[i-1] );
				}
			}
			
			ArrayAdapter<String> adapter = new ArrayAdapter<String> (context,  android.R.layout.simple_spinner_item, ranks ); //TODO : template
			// TODO adapter.setDropDownViewResource(DROPDOWN_VIEW_LAYOUT);
			dd.setAdapter(adapter);
			dd.setPrompt(context.getString(R.string.rank)+" 선택");
		}	
			setHeaderTitle("본인 계급을 선택해주세요.");
			setFooterTitle("");
			break;
			
		case KEY_ROLE:
			// TODO
			setHeaderTitle("본인 직책을 선택해 주세요.");
			setFooterTitle("");
			EditText roleET= getEditView();
			roleET.setHint("국장, 과장, 계장, 반장, 주임 ...");
			break;
			
		case KEY_PIC:
			setHeaderTitle("본인 프로필 사진을 추가해 주세요.");
			setFooterTitle("지금 등록하지 않아도\n프로필 사진 편집이 가능합니다.");
			
			Button imageBT = getImageButton();
			imageBT.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(Intent.ACTION_PICK);
					intent.setType(android.provider.MediaStore.Images.Media.CONTENT_TYPE);
					context.startActivityForResult(intent, REQUEST_PIC_PICKER);
				}
			});
			break;
			
		case KEY_PASSWORD:
			setHeaderTitle("어플레케이션 접속 시 사용할 비밀번호\n4자리를 입력해 주세요.");
			setFooterTitle("어플리케이션 실행 시 비밀번호가\n필요하니 잊지 않도록 주의해주세요.");
			break;
			
		case KEY_CONFIRM:
			setHeaderTitle("");
			((TextView)view.findViewById(R.id.footer3)).setText("정보가 올바르게 입력되었는지 확인 후\n완료 버튼을 눌러주시면\n등록이 정상적으로 진행됩니다.");
			break;
		
		}
		
		setSubmitButtonVisible(false);
	}
	/** @} */
	
	/**
	 * @name Dropdown Listner 관련
	 * @{
	 */
	/**
	 * DEPARTMENT 에서 사용하는 이벤트 리스너이다. \n
	 * 6개의 Dropdown의 이벤트 핸들링을 처리한다. 
	 */
	private OnItemSelectedListener deptListener = new OnItemSelectedListener() {
		@Override
		public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
			
			// 몇 번째 DropDown인지
			int ddIdx = Integer.parseInt((String)parent.getTag());
			
			// 현재 선택한 Department
			Department dep = ((Department)parent.getSelectedItem());
			
			
				// 선택된 정보를 가지고 서버에 하위 부서들을 요청한다.
				ArrayList<Department> deps = null;
				
				if( !dep.equals(dummy) && dep.idx != null ) {
					deps =getDepartment( dep.idx );
				} else {
					// 선택된 Department가 dummy일 경우
					//  "해당 없음" 아이템이 선택된 경우(자신이 실제로는 빈 집합을 받은 경우) 아래 스피너에 빈 어댑터를 할당한다.
					deps = dummySet;
				}
				
				String depName = dep.name;
				((TextView)view).setText(depName);
				
				// 선택된 스피너의 다음 스피너를 위한 새 어댑터를 생성한다.
				DepartmentDropdownAdapter adapter = new DepartmentDropdownAdapter(getContext(), deps);
				

				if(ddIdx != DROPDOWN_MAX_LENGTH-1) {
					// 다음 스피너에 조금 전에 생성한 어댑터를 할당한다.
					Spinner ddNext = getDropdown((ddIdx+1));
					ddNext.setAdapter(adapter);
					ddNext.setPrompt(context.getString(R.string.department)+" 선택");
					//  스피너에 어댑터를 할당하게 되면 스피너 내의 리스트가 업데이트 되면서, 자동으로 첫 번째 아이템이 선택되게 되므로,
					//  어댑터를 할당하는 것만으로도 이벤트 전파 효과가 난다.
					if(ddNext.getOnItemSelectedListener() == null)
						ddNext.setOnItemSelectedListener(deptListener);
				
				}
			
				
		}

		@Override
		public void onNothingSelected(AdapterView<?> parent) {
			int ddIdx = Integer.parseInt((String)parent.getTag());
			getDropdown(ddIdx).setAdapter(new DepartmentDropdownAdapter(getContext(), dummySet));
			// 자동으로 전파된다.
		}
		
	};
	
	
	public ArrayList<Department> getDepartment(String depIdx) {
		ArrayList<Department> departments = null;
		
		departments = MemberManager.sharedManager().getChildDepts(depIdx);
		
		Department dummy = new Department();
		
		if(departments.size() == 0) {			
			dummy.idx = null;
			dummy.name = context.getString(R.string.none);			
		} else {
			
			Comparator<Department> _comp = new Comparator<Department>() {
				@Override
				public int compare(Department lhs, Department rhs) {
					 if( lhs.sequence > rhs.sequence ) return 1;
					 if( lhs.sequence < rhs.sequence ) return -1;	 
					return 0;
				}
			};
			
			Collections.sort(departments, _comp);
			
			dummy.idx = null;
			dummy.name = context.getString(R.string.letSelect);
		}
		
		departments.add(0, dummy);
		
		return departments;
	}
	
	/** @} */
	
	/**
	 * @name subView Getters && Setters
	 * XML로부터 생선된 view에서 subView들을 쉽게 찾아내도록 돕는 메서드들이다.
	 * @{
	 */
	public	EditText 	getEditView() 					{	return (EditText)view.findViewById(R.id.edit);		}
	private	String 		getEditTitle() 					{	return getEditView().getText().toString();			}
	private	void 		setEditTitle(String title) 		{	getEditView().setText(title);						}
	private	Button 		getClearEditButton() 			{	return (Button)view.findViewById(R.id.clearEdit);	}
	private	void 		clearEdit() 					{	getEditView().setText("");							}
	private	void 		setHeaderTitle(String title) 	{	this.getHeaderView().setText(title);				}
	private	void 		setFooterTitle(String title) 	{	this.getFooterView().setText(title);				}
	private	TextView 	getHeaderView() 				{	return (TextView)view.findViewById(R.id.header);	}
	private	TextView 	getFooterView() 				{	return (TextView)view.findViewById(R.id.footer);	}
	private	Button 		getSubmitButton() 				{	return (Button)view.findViewById(R.id.submit);		}
	private	void 		setSubmitButtonVisible(Boolean isVisible) {
		int v = (isVisible? View.VISIBLE : View.INVISIBLE);
		Button b = getSubmitButton();
		if(b != null) b.setVisibility(v);
	}

	public Spinner getDropdown() {	return (Spinner)view.findViewById(R.id.dropdown);	}
	
	public Spinner getDropdown(int i) {
		Spinner dd = null;
		switch(i) {
			case 0 : dd = (Spinner)view.findViewById(R.id.dropdown0); dd.setTag("0"); return dd;
			case 1 : dd = (Spinner)view.findViewById(R.id.dropdown1); dd.setTag("1"); return dd;
			case 2 : dd = (Spinner)view.findViewById(R.id.dropdown2); dd.setTag("2"); return dd;
			case 3 : dd = (Spinner)view.findViewById(R.id.dropdown3); dd.setTag("3"); return dd;
			case 4 : dd = (Spinner)view.findViewById(R.id.dropdown4); dd.setTag("4"); return dd;
			case 5 : dd = (Spinner)view.findViewById(R.id.dropdown5); dd.setTag("5"); return dd;
			default : return null;
		}
	}
	/** @} */
	
	
	/**
	 * @name subView (Image)
	 * 이미지와 관련된 SubView들에 대한 메소드이다.
	 * @{
	 */
	private	Button 		getImageButton() 		{	return (Button)view.findViewById(R.id.editPic);		}
	public ImageView 	getImageView() 			{	return (ImageView)view.findViewById(R.id.image);	}
	private	void 		setImage(int resId) 	{	getImageView().setImageResource(resId);				}
	private	void 		setImage(Drawable d) 	{	getImageView().setImageDrawable(d);					}
	private	void 		setImage(Bitmap b) 		{	getImageView().setImageBitmap(b);					}
	private	void 		setImage(Matrix m) 		{	getImageView().setImageMatrix(m);					}
	public void 		setImage(Uri uri) 		{
		//Bitmap b = ImageManager.bitmapFromURI(context, uri);
		//if(b!= null) setImage(b);
		//getImageView().setImageURI(uri);
	}
	private	Drawable 	getDrawable() 			{	return getImageView().getDrawable();				}
	private	Bitmap 		getBitmap() {
		Drawable d = getDrawable();
		return Bitmap.createBitmap(d.getIntrinsicWidth(), d.getIntrinsicHeight(), Config.ARGB_8888);
	}
	
	public void 		imagePicked(Intent data) {
		Uri imgURI = data.getData();
		setImage(imgURI);
		context.picURI = imgURI;
	}
	/** @} */
	
	
	
	private void initNavigationBar(View navBar, int key) {
		String titleText	= null;
		String lbbTitle		= context.getString(R.string.previous);
		String rbbTitle		= context.getString(R.string.next);
		boolean lbbVisible	= true;
		boolean rbbVisible	= true;
		
		OnClickListener lbbOnClickListener = null;
		OnClickListener rbbOnClickListener = null;
		
		switch(key) {
		case KEY_NAME :
			titleText = "이름 입력";
			lbbVisible = false;
			
			rbbOnClickListener = new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					String s = getEditTitle().trim();
					if(s.length() < 1 || s.equals("")) {
						Toast.makeText(context, "이름을 선택해 주세요.", Toast.LENGTH_SHORT).show();
					} else {
						context.name = getEditTitle().trim();
						context.setFrontViewWithKey(KEY_DEPARTMENT);
					}
				}
			};
			
			break;
			
		case KEY_DEPARTMENT :
			titleText = "소속 부서 선택";
			lbbOnClickListener = new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					context.setFrontViewWithKey(KEY_NAME);
				}
			};
			
			rbbOnClickListener = new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					boolean allRight = true;
					
					Department[] selectedDeps = new Department[DROPDOWN_MAX_LENGTH];
					int depth = 0;
					
					for(int i=0; i<DROPDOWN_MAX_LENGTH; i++) {
						Department selectedDep = (Department)getDropdown(i).getSelectedItem();
						if(  (selectedDep.idx == null && !selectedDep.name.equals(context.getString(R.string.none))) 
								|| selectedDep.name.equals(context.getString(R.string.letSelect))) {
							allRight = false;
							selectedDeps[i] = null;
						} else {
							if(selectedDep.name.equals(context.getString(R.string.none))) {
								selectedDeps[i] = null;
							} else {
								selectedDeps[i] = selectedDep;
								depth++;
							}
						}
					}
					
					if(!allRight) {
						Toast.makeText(context, "소속 부서를 올바르게 선택해 주세요.", Toast.LENGTH_SHORT).show();
					} else {
						
						context.selectedDepartments = new ArrayList<Department>(depth);
						for(int i=0; i<depth; i++) {
							context.selectedDepartments.add(selectedDeps[i]);
						}
						context.setFrontViewWithKey(KEY_RANK);
					}
				}
			};
			
			break;
			
		case KEY_RANK :
			titleText = "계급 선택";
			
			lbbOnClickListener = new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					context.setFrontViewWithKey(KEY_DEPARTMENT);
				}
			};
			
			rbbOnClickListener = new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					boolean selected = true;
					if(getDropdown().getSelectedItem().equals(context.getString(R.string.letSelect))) selected = false;
					if(!selected) {
						Toast.makeText(context, "계급을 선택해 주세요.", Toast.LENGTH_SHORT).show();
					} else {
						context.rank = (getDropdown().getSelectedItemPosition())-1;
						context.setFrontViewWithKey(KEY_ROLE);
					}
				}
			};
			
			break;
			
			
		case KEY_ROLE :
			titleText = "직책 선택";
			
			lbbOnClickListener = new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					context.setFrontViewWithKey(KEY_RANK);
				}
			};
			
			rbbOnClickListener = new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO
//					String s = getEditTitle().trim();
//					if(s.length() < 1 || s.equals("")) {
//						Toast.makeText(context, "직책을 입력해 주세요.", Toast.LENGTH_SHORT).show();
//					} else {
						context.role = getEditTitle().trim();
						context.setFrontViewWithKey(KEY_PIC);
//					}
				}
			};
			
			break;
			
		case KEY_PIC :
			titleText = "사진 추가";
			
			lbbOnClickListener = new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					context.setFrontViewWithKey(KEY_ROLE);
				}
			};
			
			rbbOnClickListener = new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					//context.pic = getBitmap();
					context.setFrontViewWithKey(KEY_PASSWORD);
				}
			};
			
			break;
			
			
		case KEY_PASSWORD :
			titleText = "비밀번호 입력";
			
			lbbOnClickListener = new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					
					context.setFrontViewWithKey(KEY_PIC);
					
				}
			};
			
			rbbOnClickListener = new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					String s = getEditTitle().trim();
					if(s.length() < 1 || s.equals("")) {
						Toast.makeText(context, "비밀번호를 입력해 주세요.", Toast.LENGTH_SHORT).show();
					} else if(s.length() != 4) {
						Toast.makeText(context, "비밀번호 형식이 올바르지 않습니다.\n숫자 4자리를 입력해 주세요.", Toast.LENGTH_SHORT).show();
					} else {
						context.password = s;		// TODO :  Encrypt??
						context.setFrontViewWithKey(KEY_CONFIRM);
					}
				}
			};
			
			break;
			
			
		case KEY_CONFIRM :
			titleText = "제출";
			rbbTitle = context.getString(R.string.submit);
			
			lbbOnClickListener = new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					context.setFrontViewWithKey(KEY_PASSWORD);
				}
			};
			
			rbbOnClickListener = new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					context.goSubmit();
					
				}
			};
			
			break;
			
		}
		
		initNavigationBar(navBar, titleText, lbbVisible, rbbVisible, lbbTitle, rbbTitle, lbbOnClickListener, rbbOnClickListener);
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
	
	private void initNavigationBar(View parentView, int titleTextId, boolean lbbVisible, boolean rbbVisible, int lbbTitleId, int rbbTitleId, OnClickListener lbbOnClickListener, OnClickListener rbbOnClickListener) {
		initNavigationBar(parentView, context.getString(titleTextId), lbbVisible, rbbVisible, context.getString(lbbTitleId), context.getString(rbbTitleId), lbbOnClickListener, rbbOnClickListener);
	}
	
	
	private class DepartmentDropdownAdapter extends ArrayAdapter<Department> {

		public DepartmentDropdownAdapter(Context context, int textViewResourceId, ArrayList<Department> deps) {
			super(context, textViewResourceId, deps);
		}
		
		public DepartmentDropdownAdapter(Context context, ArrayList<Department> deps) {
			//스피너 자체
			this(context, android.R.layout.simple_spinner_item, deps);
		}
		
		@Override
		public View getDropDownView(int position, View convertView, ViewGroup parent) {
			if(convertView == null) {
				// 새로 뜨는 리스트 아이템
				LayoutInflater inflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = inflater.inflate(R.layout.edit_dropdown_item_layout, parent, false);
			}
			
			// 스피너 클릭했을 때 뜨는 리스트 뷰
 			Department dep = getItem(position);
			TextView depTV = (TextView)convertView.findViewById(R.id.title);
			depTV.setText(dep.name);
			
			return convertView;
		}
	}
}
