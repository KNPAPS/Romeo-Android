package kr.go.KNPA.Romeo.Register;

import java.util.ArrayList;

import kr.go.KNPA.Romeo.R;
import kr.go.KNPA.Romeo.Config.Constants;
import kr.go.KNPA.Romeo.Config.EventEnum;
import kr.go.KNPA.Romeo.Config.StatusCodeEnum;
import kr.go.KNPA.Romeo.Connection.Connection;
import kr.go.KNPA.Romeo.Connection.Data;
import kr.go.KNPA.Romeo.Connection.Payload;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

/**
 * 사용자 등록 화면. 
 * setFrontViewWithKey 메소드를 통해 순차적으로 입력 화면을 바꿔가며\n
 * 사용자의 정보를 입력받는다.
 * @author 채호식
 */
public class UserRegisterActivity extends Activity {

	//! 사용자의 입력을 받을 화면들의 array.
	/**
	 * array의 index는 UserRegisterEditView 클래스에 설정된 key 값을 기준으로 한다
	 */
	ArrayList<UserRegisterEditView> screens ;

	/**
	 * edit view들의 root viewgroup
	 */	
	ViewGroup layout;
	
	private String name;
	private String userHash;
	private String deptHash;
	public int rank;
	public String role;
	public String password;	// TODO
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//context
		Context context = UserRegisterActivity.this;
		
		screens = new ArrayList<UserRegisterEditView>();
		layout = (ViewGroup) ((LayoutInflater)context
								.getSystemService(LAYOUT_INFLATER_SERVICE))
								.inflate(R.layout.edit_frame, null, false);
		
		/**
		 * 이름 입력 받는 view부터 출력한다.
		 */
		setFrontViewWithKey(UserRegisterEditView.KEY_NAME);
		
		setContentView(layout);
	}
	
	/**
	 * UserRegisterEditView에 정의된 key 값을 토대로 입력 화면을 바꿔가며 출력함 
	 * @param key
	 */
	public void setFrontViewWithKey(int key) {
		
		UserRegisterEditView view = null;
		InputMethodManager im = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
		
		/**
		 * screens에 view가 없으면 추가.
		 */
		view = screens.get(key);
		if(view == null) {
			view = new UserRegisterEditView(this, key);
			screens.add(key, view);
			layout.addView(view);
		}
		
		switch(key) {
		case UserRegisterEditView.KEY_NAME :
			
			EditText ev = view.getEditView();
			ev.setInputType(InputType.TYPE_CLASS_TEXT);
			if(name != null) ev.setText(name);
			im.showSoftInput(ev, InputMethodManager.SHOW_FORCED);
			
			break;
		case UserRegisterEditView.KEY_DEPARTMENT:
			//TODO department view 수정
			
			im.hideSoftInputFromWindow(view.getDropdown(0).getWindowToken(), 0);
			
			break;
		case UserRegisterEditView.KEY_RANK:
			
			im.hideSoftInputFromWindow(view.getDropdown().getWindowToken(), 0);
			break;
		case UserRegisterEditView.KEY_ROLE:
			
			EditText roleET = view.getEditView();
			if(role != null) roleET.setText(role);
			im.showSoftInput(roleET, InputMethodManager.SHOW_FORCED);
			//im.hideSoftInputFromWindow(view.getDropdown().getWindowToken(), 0);
			break;
		case UserRegisterEditView.KEY_PIC:
			im.hideSoftInputFromWindow(view.getImageView().getWindowToken(), 0);
			break;
		case UserRegisterEditView.KEY_PASSWORD :
			
			EditText passEV = view.getEditView();
			//passEV.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
			passEV.setInputType(InputType.TYPE_CLASS_NUMBER);
			passEV.setTransformationMethod(PasswordTransformationMethod.getInstance());
			if(password != null) passEV.setText(password);
			im.showSoftInput(passEV, InputMethodManager.SHOW_FORCED);
			//passEV.requestFocus();
			
			break;
		case UserRegisterEditView.KEY_CONFIRM:
			
			((TextView)view.findViewById(R.id.footer1)).setText(deptFullName);
			((TextView)view.findViewById(R.id.footer2)).setText(Constants.POLICE_RANK[rank]+" "+name);
			//TODO ((TextView)view.findViewById(R.id.password)).setText(password);
			im.hideSoftInputFromWindow(view.getImageView().getWindowToken(), 0);
			break;
			
		}
		//layout.bringChildToFront(view);
		layout.removeView(view);
		layout.addView(view, 0);
		layout.invalidate();
	}

	/**
	 * 유저 정보를 서버에 등록하고 SharedPref에 저장한다.
	 */
	public void goSubmit() {
		
		//서버에 등록한 후 유저 해쉬를 받아옴. 등록 실패시 null
		String userHash = registerUser();
		
		if ( userHash == null ) {
			//TODO 등록 실패 처리
		}
		
		
//			//Shared Preference
//					Context context = UserRegisterActivity.this;
//					UserInfo.setUserIdx(context, userIdx);
//					UserInfo.setName(context, name);
//					UserInfo.setDepartment(context, deps);
//					UserInfo.setDepartmentIdx(context, depIdx);
//					UserInfo.setRankIdx(context, rank);
//					UserInfo.setRank(context, User.RANK[rank]);
//					UserInfo.setPassword(context, password);
//			//		UserInfo.setPi                                                              cPath(context, path);
			
			
//			Intent intent = new Intent(UserRegisterActivity.this, NotRegisteredActivity.class);
//			startActivity(intent);
			
		Bundle r = new Bundle();
		r.putBoolean("status", true);
		Intent intent = new Intent();
		intent.putExtras(r);
		
		setResult(RESULT_OK, intent);
		finish();
		} else {
			Bundle r = new Bundle();
			r.putBoolean("status", false);
			Intent intent = new Intent();
			intent.putExtras(r);
			
			setResult(RESULT_OK, intent);
			finish();
		} 
		
	}
	
	/**
	 * 이 액티비티에 설정된 멤버 변수들을 토대로 서버에 유저 등록 후 user hash를 받아와 반환
	 * @return
	 */
	private String registerUser() {
		Payload reqpl = new Payload(EventEnum.USER_REGISTER);
		Data data = new Data();
		
		data.add(0,Data.KEY_USER_NAME,name);
		data.add(0,Data.KEY_USER_RANK,userHash);
		data.add(0,Data.KEY_USER_ROLE,rank);
		data.add(0,Data.KEY_DEPT_HASH,deptHash);
		reqpl.setData(data);
		
		Connection conn = new Connection.Builder(reqpl.toJson()).build();
		conn.request();
		Payload resp = new Payload( conn.getResponsePayload() );
		if ( resp.getStatusCode() == StatusCodeEnum.SUCCESS ) {
			return resp.getData().get(0,Data.KEY_USER_HASH).toString();
		} else {
			return null;
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(resultCode == RESULT_OK) {
//TODO			if(requestCode == UserRegisterEditView.REQUEST_PIC_PICKER) {
//				UserRegisterEditView picEditView = (UserRegisterEditView) screens.get();
//				picEditView.imagePicked(data);
//			}
		}
		//super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onBackPressed() {
		return ;
	}
}
