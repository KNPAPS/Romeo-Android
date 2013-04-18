package kr.go.KNPA.Romeo.Register;

import java.util.ArrayList;
import java.util.HashMap;

import kr.go.KNPA.Romeo.R;
import kr.go.KNPA.Romeo.Config.Event;
import kr.go.KNPA.Romeo.Config.KEY;
import kr.go.KNPA.Romeo.Config.StatusCode;
import kr.go.KNPA.Romeo.Connection.Connection;
import kr.go.KNPA.Romeo.Connection.Data;
import kr.go.KNPA.Romeo.Connection.Payload;
import kr.go.KNPA.Romeo.Member.Department;
import kr.go.KNPA.Romeo.Member.User;
import kr.go.KNPA.Romeo.Util.CallbackEvent;
import kr.go.KNPA.Romeo.Util.ImageManager;
import kr.go.KNPA.Romeo.Util.UserInfo;
import kr.go.KNPA.Romeo.Util.WaiterView;
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
 * Application을 처음 실행했을 때, 서버에 User와 Device가 등록되어 있는지 확인하는 절차를 밟게 된다.  \n 
 * 이때, 서버에 User가 등록되어 있지 않다면, 이 Activity를 통해 User를 서버에 등록하게 된다.
 */
public class UserRegisterActivity extends Activity {

	private	HashMap <String, UserRegisterEditView> screens;		//< 여러 화면들이 있는데, 이들을 ViewGroup에 넣어 순서를 바꿔가며 보여주게 된다. 
	private	ViewGroup layout;									//< screens에 넣어진 View 객체들을 한꺼번에 담고 있는 ViewGroup
	
	/**
	 * @name 입력된 사용자 정보들
	 * @{
	 */
	public	String 		name;
	
	public	Department 	department;
	public	int 		rank;
	public	String 		role;
	//public Bitmap pic;
	public	Uri 		picURI;
	public	String 		password;	// TODO
	private String		userIdx;
	ArrayList<Department> selectedDepartments;
	/** @} */
	
	/**
	 * onCreat.\n
	 * 필요한 변수들에 공간을 할당하고, 사용자 이름을 선택받는 뷰를 보여주도록 위치시킨다.
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		screens = new HashMap<String, UserRegisterEditView>();
		layout = (ViewGroup) ((LayoutInflater)UserRegisterActivity.this.getSystemService(LAYOUT_INFLATER_SERVICE))
											 .inflate(R.layout.edit_frame, null, false);
		setFrontViewWithKey(UserRegisterEditView.KEY_NAME);
		setContentView(layout);
	}
	
	/**
	 * 뭉쳐있는 여러 뷰들 중, key에 해당하는 뷰를 보이도록 위치시킨다. 
	 * @param key 보이고자 하는 뷰를 나타내는 key값. UserRegisterEditView 에 등록되어 있다.
	 */
	public void setFrontViewWithKey(int key) {
		UserRegisterEditView view = null;
		InputMethodManager im = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
		
		switch(key) {
		case UserRegisterEditView.KEY_NAME :
			view = getView(key, "name");
			
			EditText ev = view.getEditView();
			ev.setInputType(InputType.TYPE_CLASS_TEXT);
			if(name != null) ev.setText(name);
			im.showSoftInput(ev, InputMethodManager.SHOW_FORCED);
			//ev.requestFocus();
			break;
			
		case UserRegisterEditView.KEY_DEPARTMENT :		
			view = getView(key, "department"); 
			im.hideSoftInputFromWindow(view.getDropdown(0).getWindowToken(), 0);
			// selectedDepartments??
			// View가 죽은게 아니라, 뒤에 가려지기만 한 것이므로, 정보가 저장된 채, 잘 살아 있을 것이다. 
			// TODO : 페이지가 바뀌어도 정보가 사라지지 않는 것을 확인하면, selectedDepartments 지우기
			break;
			
		case UserRegisterEditView.KEY_RANK :			
			view = getView(key, "rank");
			im.hideSoftInputFromWindow(view.getDropdown().getWindowToken(), 0);
			break;
			
		case UserRegisterEditView.KEY_ROLE :			
			view = getView(key,"role");
			
			EditText roleET = view.getEditView();
			if(role != null) roleET.setText(role);
			im.showSoftInput(roleET, InputMethodManager.SHOW_FORCED);
			//im.hideSoftInputFromWindow(view.getDropdown().getWindowToken(), 0);
			break;
			
		case UserRegisterEditView.KEY_PIC :				
			view = getView(key, "pic");	
			im.hideSoftInputFromWindow(view.getImageView().getWindowToken(), 0);
			break;
			
		case UserRegisterEditView.KEY_PASSWORD :		
			view = getView(key, "password");		
			
			EditText passEV = view.getEditView();
			//passEV.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
			passEV.setInputType(InputType.TYPE_CLASS_NUMBER);
			passEV.setTransformationMethod(PasswordTransformationMethod.getInstance());
			if(password != null) passEV.setText(password);
			im.showSoftInput(passEV, InputMethodManager.SHOW_FORCED);
			//passEV.requestFocus();
			break;
			
		case UserRegisterEditView.KEY_CONFIRM :			
			view = getView(key, "confirm");	
			
			
			department = selectedDepartments.get(selectedDepartments.size()-1);
			
			//String deps[] = department.nameFull.split(" ");
			//String dep1 = deps[0];
			//String dep23456 = "";
			String dep1 = selectedDepartments.get(0).name;
			String dep23456 = "";
			for( int i=1; i<selectedDepartments.size(); i++){
				dep23456 += (" " + selectedDepartments.get(i).name);//deps[i]);
			}
			
					
			//if(pic != null) view.setImage(pic);
			if(picURI != null) view.setImage(picURI);
			((TextView)view.findViewById(R.id.footer1)).setText(dep1+"\n"+dep23456);
			((TextView)view.findViewById(R.id.footer2)).setText(User.RANK[rank]+" "+name);
			((TextView)view.findViewById(R.id.password)).setText(password);
			im.hideSoftInputFromWindow(view.getImageView().getWindowToken(), 0);
			break;
			
		}
		//layout.bringChildToFront(view);
		layout.removeView(view);
		layout.addView(view, 0);
		layout.invalidate();
	}

	/**
	 * setFrontViewWithKey(int)를 돕는 함수
	 * @param key	setFrontViewWithKey(int)에 입력된 int형 인자와 동일한 인자
	 * @param sKey	내부적으로 저장될 키 값
	 * @return 생성된 view
	 */
	private UserRegisterEditView getView(int key, String sKey) {
		UserRegisterEditView view = screens.get(sKey);
		if(view == null) {
			view = new UserRegisterEditView(this, key);
			screens.put(sKey, view);
			layout.addView(view);
		}
		return view;
	}
	
	/**
	 * 확인 스크린 이후, 제출 버튼을 누르면 실제 등록을 위한 절차를 진행하게 된다.\n
	 * 제출 버튼을 눌렀을 때 동작할 메서드이다.
	 */
	public void goSubmit() {

		
		// TODO : register 성공할 때 까지 반복
		boolean registered = registerUser();
		
		if(registered == true) {
			if(picURI != null) {
				WaiterView.showDialog(UserRegisterActivity.this);
				ImageManager im = new ImageManager().callBack(userPicCallback);
				im.upload(ImageManager.PROFILE_SIZE_ORIGINAL, userIdx, picURI.getPath());
			} 
			saveAndFinish();
			
		} else {
			cancelAndFinish();
		} 
	}
	private CallbackEvent<Payload, Integer, Payload> userPicCallback = new CallbackEvent<Payload, Integer, Payload>() {
		@Override
		public void onError(String errorMsg, Exception e) {
			super.onError(errorMsg, e);
			WaiterView.dismissDialog(UserRegisterActivity.this);
		}
		
		@Override
		public void onProgressUpdate(Integer progress) {
			if(progress != null)
				WaiterView.setProgress(progress);
		};
		
		@Override
		public void onPostExecute(Payload result) {
			super.onPostExecute(result);
			WaiterView.dismissDialog(UserRegisterActivity.this);
		}
	};
	
	private void saveAndFinish() {
		//Shared Preference
		Context context = UserRegisterActivity.this;
		UserInfo.setUserIdx(context, userIdx);
		UserInfo.setName(context, name);
		UserInfo.setDepartment(context, department.nameFull);
		UserInfo.setDepartmentIdx(context, department.idx);
		UserInfo.setRankIdx(context, rank);
		UserInfo.setRank(context, rank);
		UserInfo.setPassword(context, password);
		
		Intent intent = new Intent();
		Bundle resultBundle = new Bundle();
		resultBundle.putBoolean("status", true);
		intent.putExtras(resultBundle);
		setResult(RESULT_OK, intent);
		finish();
	}
	
	private void cancelAndFinish() {
		Intent intent = new Intent();
		Bundle resultBundle = new Bundle();
		resultBundle.putBoolean("status", false);
		intent.putExtras(resultBundle);
		setResult(RESULT_CANCELED, intent);
		finish();
	}
	
	/**
	 * 이 액티비티에 설정된 멤버 변수들을 토대로 서버에 유저 등록 후 user hash를 받아와 반환
	 * @return
	 */
	public boolean registerUser() {
		Data reqData = new Data().add(0, KEY.USER.NAME, name)
								 .add(0, KEY.USER.ROLE, role)
								 .add(0, KEY.USER.RANK, rank)
								 .add(0, KEY.DEPT.IDX, department.idx);
		// TODO : 사진 업로드
		
		Payload request = new Payload().setEvent(Event.User.register()).setData(reqData);
		Connection conn = new Connection().requestPayload(request).async(false).request();
		Payload response = conn.getResponsePayload();
		
		if(response.getStatusCode() == StatusCode.SUCCESS) {
			userIdx = (String)response.getData().get(0, KEY.USER.IDX);
			return true;
		} else {
			return false;
		}
		
		// TODO 사진 업로드하는 코드를 삽입한다.
		//if(picURI != null)
			//ImageManager.bitmapFromURI(context, picURI);
		// 사진 업로드는 비동기로 이루어지며,
		// sharedPreference에 사진 업로드 여부를 체크하는 변수를 할당한다. ( 추후 사진 변경시 등등 활용 할 수 있기 때문이다.)
		// 앱이 켜질때 혹은 조직도 등 특정 조건 하에서 사진 전송 여부를 확인하여 되어있지 않았다면 수시로 업로드 할 수 있는 기회를 제공하도록 한다.
	}
	
	/**
	 * 사진선택은 갤러리에서 이루어진다.\n
	 * 이 갤러리는 안드로이드에서 제공하는 별도의 Activity로 이루어져 있다.\n
	 * 이 액티비티가 종료되며 결과값을 반납할 때 호출되는 메서드이다. 
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(resultCode == RESULT_OK) {
			if(requestCode == UserRegisterEditView.REQUEST_PIC_PICKER) {
				UserRegisterEditView picEditView = (UserRegisterEditView) screens.get("pic");
				picEditView.imagePicked(data);
			}
		}
		// TODO : 
		//super.onActivityResult(requestCode, resultCode, data);
	}

	/**
	 * 반드시 유저 등록이 이루어져야 하므로, 백버튼을 통해 UserRegisterActivity가 종료되거나 이를 벗어나는 일이 없도록 방지한다.
	 */
	@Override
	public void onBackPressed() {
		return ;
	}
}
