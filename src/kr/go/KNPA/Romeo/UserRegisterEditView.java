package kr.go.KNPA.Romeo;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import kr.go.KNPA.Romeo.GCM.GCMMessageSender;
import kr.go.KNPA.Romeo.Member.Department;
import kr.go.KNPA.Romeo.Member.User;
import kr.go.KNPA.Romeo.Util.Encrypter;
import kr.go.KNPA.Romeo.Util.ImageManager;
import kr.go.KNPA.Romeo.Util.IndexPath.Iterator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.Config;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore.Images;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Toast;

public class UserRegisterEditView extends LinearLayout {
	
	public static final int KEY_NAME 			= 0;
	public static final int KEY_DEPARTMENT 		= 1;
	public static final int KEY_RANK			= 2;
	public static final int KEY_ROLE			= 3;
	public static final int KEY_PIC				= 4;
	public static final int KEY_PASSWORD		= 5;
	public static final int KEY_CONFIRM			= 6;
	
	public static final int DROPDOWN_MAX_LENGTH = 6;
//	private static final int DROPDOWN_VIEW_LAYOUT = R.layout.edit;
//	private static final int DROPDOWN_ITEM_LAYOUT = R.layout.edit_dropdown_layout;
	
	public static final int REQUEST_PIC_PICKER = 300;
	
	public View navBar;
	public View view;
	public int res;
	public int key;
	
	private UserRegisterActivity context;
	
	private final String[] emptySet = {getContext().getString(R.string.none)};
	
	public UserRegisterEditView(Context context) {
		this(context, null);
	}
	
	public UserRegisterEditView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public UserRegisterEditView(UserRegisterActivity activity, int key) {
		this(activity);
		this.key = key;
		this.context = activity;
		
		switch(key) {
			case KEY_NAME : 		this.res = R.layout.edit_text;		break;
			case KEY_DEPARTMENT : 	this.res = R.layout.edit_dropdown6;	break;
			case KEY_RANK : 		this.res = R.layout.edit_dropdown;	break;
			case KEY_ROLE : 		this.res = R.layout.edit_text; break;//edit_dropdown;	break;
			case KEY_PASSWORD : 	this.res = R.layout.edit_text;		break;
			case KEY_PIC : 			this.res = R.layout.edit_pic;		break;
			case KEY_CONFIRM : 		this.res = R.layout.edit_confirm;	break;
		}
		
		this.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		this.setOrientation(LinearLayout.VERTICAL);
		this.setBackgroundResource(R.color.lighter);
		LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		
		 
		View navBar = inflater.inflate(R.layout.navigation_bar, this, false);
		initNavigationBar(navBar, key);
		this.addView(navBar);
		
		view = inflater.inflate(this.res, this, false);
		this.addView(view);
		
		Button clearEdit = getClearEditButton();
		if(clearEdit != null)  clearEdit.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				clearEdit();
			}
		});
		
		OnItemSelectedListener listener = new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
				int index = Integer.parseInt((String)parent.getTag());
				Spinner _dd = (Spinner)parent;
				
				String[] selected = new String[index+1];
				for(int i=0; i<selected.length; i++) {
					Spinner dd = getDropdown(i);
					String str = (String)dd.getSelectedItem();
					selected[i] = str;
					
				}
				
				// 선택된 스피너의 다음 스피너.
				String[] deps = getDepartment(selected);
				DepartmentDropdownAdapter adapter = new DepartmentDropdownAdapter(getContext(), deps);
				Spinner dd = getDropdown((index+1));
				dd.setAdapter(adapter);
				
/*				// 선택된 스피너의 다음 스피너 그 이상.
				for(int i=(index+2); i<DROPDOWN_MAX_LENGTH; i++) {
					ArrayAdapter<String> __adapter = new ArrayAdapter<String>(getContext(), DROPDOWN_ITEM_LAYOUT, emptySet);
					Spinner __dd = getDropdown((index+1));
					__dd.setAdapter(__adapter);
				}
*/				
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				int index = Integer.parseInt((String)parent.getTag());
				
				for(int i=index; i<DROPDOWN_MAX_LENGTH; i++) {
					Spinner dd = getDropdown(i);
					DepartmentDropdownAdapter adapter = new DepartmentDropdownAdapter(getContext(),  emptySet);
					// adapter.setDropDownViewResource(DROPDOWN_VIEW_LAYOUT); TODO
					dd.setAdapter(adapter);
				}
				
			}
			
		};
		
		switch(key) {
		case KEY_NAME:
			setHeaderTitle("본인 이름을 입력해 주세요.");
			setFooterTitle("띄어쓰기나 오타 없이 입력해주셔야\n정상적으로 등록이 이루어질 수 있습니다.");
			break;
			
		case KEY_DEPARTMENT:
		{
			Spinner _dd = getDropdown(0);
			String[] level1Set = getDepartment(null);
			DepartmentDropdownAdapter _adapter = new DepartmentDropdownAdapter(context, level1Set);
			// TODO _adapter.setDropDownViewResource(DROPDOWN_VIEW_LAYOUT);
			_dd.setAdapter(_adapter);
			_dd.setOnItemSelectedListener(listener);
			_dd.setPrompt(context.getString(R.string.department)+" 선택");
			for(int i=1; i<DROPDOWN_MAX_LENGTH; i++) {
				Spinner dd = getDropdown(i);
				DepartmentDropdownAdapter adapter = new DepartmentDropdownAdapter(context, emptySet);
				// TODO adapter.setDropDownViewResource(DROPDOWN_VIEW_LAYOUT);
				dd.setAdapter(adapter);
				dd.setPrompt(context.getString(R.string.department)+" 선택");
				if(i != DROPDOWN_MAX_LENGTH-1 )
					dd.setOnItemSelectedListener(listener);
			}
		}
			setHeaderTitle("본인 소속 부서를 선택해주세요.");
			setFooterTitle("");
			break;
			
		case KEY_RANK:
		{
			Spinner dd = getDropdown();
			String[] ranks = new String[User.RANK.length+1];
			for(int i=0; i<ranks.length; i++) {
				if(i==0) {
					ranks[i] = context.getString(R.string.letSelect);
				} else {
					ranks[i] = User.RANK[i-1];
				}
			}
			DepartmentDropdownAdapter adapter = new DepartmentDropdownAdapter(context, ranks);
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
			//Spinner dd = getDropdown();
			//dd.setPrompt(context.getString(R.string.role)+" 선택");
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
	
	public void setHeaderTitle(String title) {
		this.getHeaderView().setText(title);
	}
	
	public void setFooterTitle(String title) {
		this.getFooterView().setText(title);
	}
	
	public TextView getHeaderView() {
		return (TextView)view.findViewById(R.id.header);
	}
	
	public TextView getFooterView() {
		return (TextView)view.findViewById(R.id.footer);
	}
	
	public EditText getEditView() {
		return (EditText)view.findViewById(R.id.edit);
	}
	
	public String getEditTitle() {
		return getEditView().getText().toString();
	}
	
	public void setEditTitle(String title) {
		getEditView().setText(title);
	}
	
	public Button getClearEditButton() {
		return (Button)view.findViewById(R.id.clearEdit);
	}
	
	public void clearEdit() {
		getEditView().setText("");
	}
	
	public Button getSubmitButton() {
		return (Button)view.findViewById(R.id.submit);
	}
	
	public void setSubmitButtonVisible(Boolean isVisible) {
		int v = (isVisible? View.VISIBLE : View.INVISIBLE);
		Button b = getSubmitButton();
		if(b != null) b.setVisibility(v);
	}
	
	public Button getImageButton() {
		return (Button)view.findViewById(R.id.editPic);
	}
	
	public void imagePicked(Intent data) {
		
		
		Uri imgURI = data.getData();
		
		setImage(imgURI);
		context.picURI = imgURI;
	}
	
	public ImageView getImageView() {
		return (ImageView)view.findViewById(R.id.image);
		
	}
	
	public void setImage(int resId) {
		getImageView().setImageResource(resId);
	}
	
	public void setImage(Drawable d) {
		getImageView().setImageDrawable(d);
	}
	
	public void setImage(Bitmap b) {
		getImageView().setImageBitmap(b);
	}
	
	public void setImage(Matrix m) {
		getImageView().setImageMatrix(m);
	}
	
	public void setImage(Uri uri) {
		Bitmap b = ImageManager.bitmapFromURI(context, uri);
		if(b!= null) setImage(b);
		//getImageView().setImageURI(uri);
	}
	
	public Drawable getDrawable() {
		return getImageView().getDrawable();
	}
	
	public Bitmap getBitmap() {
		Drawable d = getDrawable();
		return Bitmap.createBitmap(d.getIntrinsicWidth(), d.getIntrinsicHeight(), Config.ARGB_8888);
	}
	
	public Spinner getDropdown() {
		return (Spinner)view.findViewById(R.id.dropdown);
	}
	
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
	
	public String[] getDepartment(String[] deps) {
		if(deps != null && ( deps[(deps.length-1)].equals(context.getString(R.string.none)) || deps[(deps.length-1)].equals(context.getString(R.string.letSelect)) ) ) return emptySet;
		StringBuilder j = new StringBuilder("{\"departments\" : [");
		for(int i=0; deps!=null && i<deps.length; i++) {
			j.append("\"").append(deps[i]).append("\"");
			if(i!= (deps.length-1)) j.append(",");
		}
		j.append("]").append("}");
		
		ArrayList<Department> departments = GCMMessageSender.getSubDepartment(j.toString());
		java.util.Iterator<Department> itr = ((ArrayList<Department>)departments.clone()).iterator();
		while(itr.hasNext()) {
			Department d = itr.next();
			if(d.title.trim().equals(""))
				departments.remove(d);
		}
		
		String[] result = null;
		
		if(departments.size() == 0) {
			result = new String[1];
			result[0] = context.getString(R.string.none);
			
		} else {
			
			Comparator<Department> _comp = new Comparator<Department>() {

				@Override
				public int compare(Department lhs, Department rhs) {
					long lSeq = lhs.sequence;
					long rSeq = rhs.sequence;
					 if( lSeq > rSeq ) return 1;
					 if( lSeq < rSeq ) return -1;	 
					return 0;
				}
			};
			
			Collections.sort(departments, _comp);
			
			result = new String[departments.size()+1];
			for(int i=0; i<departments.size()+1;i++) {
				if(i == 0 ) {
					result[i] = context.getString(R.string.letSelect);
				} else {
					result[i] = departments.get(i-1).title;
				}
			}
		}
		return result;
	}
	
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
					boolean selected = true;
					
					for(int i=0; i<DROPDOWN_MAX_LENGTH; i++) {
						if(getDropdown(i).getSelectedItem().equals(context.getString(R.string.letSelect)))
								selected = false;
					}
					if(!selected) {
						Toast.makeText(context, "소속 부서를 올바르게 선택해 주세요.", Toast.LENGTH_SHORT).show();
					} else {
										
						String[] _deps = new String[DROPDOWN_MAX_LENGTH];
						int cnt = 0;
						for(int i=0; i<DROPDOWN_MAX_LENGTH; i++) {
							Spinner dd = getDropdown(i);
							String str = (String)dd.getSelectedItem();
							 
							if(str.equals(emptySet[0])) {
								_deps[i] = null;
							} else {
								_deps[i] = str;
								cnt++;
							}
						}
						
						//String[] deps = new String[cnt];
						//for(int i=0; i<cnt; i++) {
						//	deps[i] = _deps[i];
						//}
						context.departments = _deps;
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
	
	
	private class DepartmentDropdownAdapter extends ArrayAdapter<String> {

		public DepartmentDropdownAdapter(Context context,
				int textViewResourceId, String[] objects) {
			super(context, textViewResourceId, objects);
		}
		
		public DepartmentDropdownAdapter(Context context, String[] strings) {
			this(context, android.R.layout.simple_spinner_item, strings);
		}
		
		@Override
		public View getDropDownView(int position, View convertView,
				ViewGroup parent) {
			if(convertView == null) {
				LayoutInflater inflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = inflater.inflate(R.layout.edit_dropdown_item_layout, parent, false);
			}
			
			String dep = getItem(position);
			TextView depTV = (TextView)convertView.findViewById(R.id.title);
			depTV.setText(dep);
			
			return convertView;
		}
	}
}
