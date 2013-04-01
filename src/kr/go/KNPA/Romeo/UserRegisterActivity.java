package kr.go.KNPA.Romeo;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;

import kr.go.KNPA.Romeo.Member.MemberManager;
import kr.go.KNPA.Romeo.Member.User;
import kr.go.KNPA.Romeo.Util.ImageManager;
import kr.go.KNPA.Romeo.Util.UserInfo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore.Images;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class UserRegisterActivity extends Activity {

	HashMap <String, UserRegisterEditView> screens;
	ViewGroup layout;
	
	public String name;
	public String[] departments;
	public int rank;
	public String role;
	//public Bitmap pic;
	public Uri picURI;
	public String password;	// TODO
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Context context = UserRegisterActivity.this;
		
		screens = new HashMap<String, UserRegisterEditView>();
		
		layout = (ViewGroup) ((LayoutInflater)context.getSystemService(LAYOUT_INFLATER_SERVICE)).inflate(R.layout.edit_frame, null, false);
		
		setFrontViewWithKey(UserRegisterEditView.KEY_NAME);
		
		setContentView(layout);

	}
	
	public void setFrontViewWithKey(int key) {
		String sKey = null;

		Context context = UserRegisterActivity.this;

		LayoutInflater inflater = (LayoutInflater)context.getSystemService(LAYOUT_INFLATER_SERVICE);
		UserRegisterEditView view = null;
		InputMethodManager im = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
		
		switch(key) {
		case UserRegisterEditView.KEY_NAME :
			sKey = "name";
			view = screens.get(sKey);
			
			if(view == null) {
				view = new UserRegisterEditView(this, UserRegisterEditView.KEY_NAME);
				screens.put("name", view);
				layout.addView(view);
			}
			
			EditText ev = view.getEditView();
			ev.setInputType(InputType.TYPE_CLASS_TEXT);
			if(name != null) ev.setText(name);
			im.showSoftInput(ev, InputMethodManager.SHOW_FORCED);
			//ev.requestFocus();
			
			break;
		case UserRegisterEditView.KEY_DEPARTMENT :		
			sKey = "department"; 
			view = screens.get(sKey);
			
			if(view == null) {
				view = new UserRegisterEditView(this, UserRegisterEditView.KEY_DEPARTMENT);
				screens.put("department", view);
				layout.addView(view);
			}
			
			im.hideSoftInputFromWindow(view.getDropdown(0).getWindowToken(), 0);
			
			break;
		case UserRegisterEditView.KEY_RANK :			
			sKey = "rank";	
			view = screens.get(sKey);
			if(view == null) {
				view = new UserRegisterEditView(this, UserRegisterEditView.KEY_RANK);
				screens.put("rank", view);
				layout.addView(view);
			}
			im.hideSoftInputFromWindow(view.getDropdown().getWindowToken(), 0);
			break;
		case UserRegisterEditView.KEY_ROLE :			
			sKey = "role";
			view = screens.get(sKey);
			if(view == null) {
				view = new UserRegisterEditView(this, UserRegisterEditView.KEY_ROLE);
				screens.put("role", view);
				layout.addView(view);
			}
			EditText roleET = view.getEditView();
			if(role != null) roleET.setText(role);
			im.showSoftInput(roleET, InputMethodManager.SHOW_FORCED);
			//im.hideSoftInputFromWindow(view.getDropdown().getWindowToken(), 0);
			break;
		case UserRegisterEditView.KEY_PIC :				
			sKey = "pic";	
			view = screens.get(sKey);
			if(view == null) {
				view = new UserRegisterEditView(this, UserRegisterEditView.KEY_PIC);
				screens.put("pic", view);
				layout.addView(view);
			}
			im.hideSoftInputFromWindow(view.getImageView().getWindowToken(), 0);
			break;
		case UserRegisterEditView.KEY_PASSWORD :		
			sKey = "password";		
			view = screens.get(sKey);
			if(view == null) {
				view = new UserRegisterEditView(this, UserRegisterEditView.KEY_PASSWORD);
				screens.put("password", view);
				layout.addView(view);
			}
			
			EditText passEV = view.getEditView();
			//passEV.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
			passEV.setInputType(InputType.TYPE_CLASS_NUMBER);
			passEV.setTransformationMethod(PasswordTransformationMethod.getInstance());
			if(password != null) passEV.setText(password);
			im.showSoftInput(passEV, InputMethodManager.SHOW_FORCED);
			//passEV.requestFocus();
			
			break;
		case UserRegisterEditView.KEY_CONFIRM :			
			sKey = "confirm";	
			view = screens.get(sKey);
			if(view == null) {
				view = new UserRegisterEditView(this, UserRegisterEditView.KEY_CONFIRM);
				screens.put("confirm", view);
				layout.addView(view);
			}
			String subDepartment = "";
			for(int i=1; i< UserRegisterEditView.DROPDOWN_MAX_LENGTH; i++) {
				if(departments !=null && departments[i] != null)
					subDepartment = subDepartment + departments[i] + " ";
			}
			//if(pic != null) view.setImage(pic);
			if(picURI != null) view.setImage(picURI);
			((TextView)view.findViewById(R.id.footer1)).setText(departments[0]+"\n"+subDepartment);
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

	public void goSubmit() {
		// TODO : go Resgister
		Bundle b = new Bundle();
		b.putString("name", name);
		b.putStringArray("departments", departments);
		b.putInt("rank", rank);
		b.putString("role", role);
		if(picURI != null)	b.putString("picURI", picURI.toString());
		
		Bundle result = MemberManager.sharedManager().registerUser(UserRegisterActivity.this, b);
		int status = result.getInt("status");
		if(status == 1) {
			
			long userIdx = result.getLong("userIdx");
			long depIdx = result.getLong("departmentIdx");
			
			String path = "USERPIC_"+userIdx+".jpg";
//			ImageManager.saveBitmapFromURIToPath(UserRegisterActivity.this, picURI, path);
			
			String deps = "";
			
			for(int i=0; i<departments.length; i++) {
				if(departments[i] == null) continue;
				deps += departments[i];
				deps += " ";
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
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(resultCode == RESULT_OK) {
			if(requestCode == UserRegisterEditView.REQUEST_PIC_PICKER) {
				UserRegisterEditView picEditView = (UserRegisterEditView) screens.get("pic");
				picEditView.imagePicked(data);
			}
		}
		//super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onBackPressed() {
		return ;
	}
}
