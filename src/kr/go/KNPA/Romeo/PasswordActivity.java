package kr.go.KNPA.Romeo;

import kr.go.KNPA.Romeo.Config.Constants;
import kr.go.KNPA.Romeo.Util.Encrypter;
import kr.go.KNPA.Romeo.Util.UserInfo;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class PasswordActivity extends Activity {
	private static final int NUM_DIGIT = 4;
	private ViewGroup parent;
	
	private static int errorCount = 0;
	
	private Bundle targetModuleInfo;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Intent intent = getIntent();
		targetModuleInfo = intent.getExtras();
	  
		parent = (ViewGroup)((LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE)).inflate(R.layout.password_activity, null, false);
		
		OnClickListener rbbOnClickListener = new OnClickListener() {	@Override	public void onClick(View v) {	submit();	}	};
		initNavigationBar(parent, "비밀번호 입력", false, true, null, getString(R.string.submit), null, rbbOnClickListener);
		
		for(int i=0; i<NUM_DIGIT; i++) {
			final EditText digitET = getDigitView(i);
			
			digitET.setInputType(InputType.TYPE_CLASS_NUMBER);
		    digitET.setTransformationMethod(PasswordTransformationMethod.getInstance());
			
			TextWatcher watcher = new TextWatcher() {
				@Override	public void onTextChanged(CharSequence s, int start, int before, int count) {}
				@Override	public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
				@Override	public void afterTextChanged(Editable s) {	
					if(s.length() == 1) {	
						if(digitET != null){
							View nextFocusView = digitET.focusSearch(View.FOCUS_RIGHT);
							if(nextFocusView != null)
								nextFocusView.requestFocus();
						}
					}
				}
			};
			
			digitET.addTextChangedListener(watcher);
			digitET.setOnKeyListener(keyListener);
			digitET.setOnClickListener(new OnClickListener() {	@Override	public void onClick(View arg0) {	onClickEditText(digitET);	}	});
		}
		
		setContentView(parent);
	}
	
	OnKeyListener keyListener = new OnKeyListener() {
		
		@Override
		public boolean onKey(View v, int keyCode, KeyEvent event) {
			EditText et = (EditText)v;
			if(event.getAction() == KeyEvent.ACTION_UP && 
					keyCode == KeyEvent.KEYCODE_ENTER) {
				/*
				switch(getDigitNumber(et)){
					case 0: getDigitView(1).requestFocus();
					case 1: getDigitView(2).requestFocus();
					case 2: getDigitView(3).requestFocus();break;
					case 3:
				}
				*/
				
				if(getDigitNumber(et) == 3) {
					submit();
					return true;
				} else {
					et.focusSearch(View.FOCUS_RIGHT).requestFocus();
					return true;
				}
			} else if(event.getAction() == KeyEvent.ACTION_UP &&
					keyCode == KeyEvent.KEYCODE_DEL) {
					
				if(et.getSelectionEnd() > 0) {
					// 커서가 글자 뒤에 놓여있는 경우
					return false; // default Function(delete/Backspace)
				} else {
					// 커서가 글자 앞에 놓여있는 경우
						
					if(et != null){
						View nextFocusView = et.focusSearch(View.FOCUS_LEFT);
						if(nextFocusView != null) {
							nextFocusView.requestFocus();
							((EditText)nextFocusView).setText("");
						}
					}
					return false; // default Function(delete(Backspace))
				}
			}
			return false;
		}
	};

	private int getDigitNumber(EditText digitET) {
		for(int i=0; i<NUM_DIGIT; i++) {
			if( isDigitView(digitET, i) == true)
				return i;
		}
		return Constants.NOT_SPECIFIED;
	}
	
	private boolean isDigitView(EditText digitET, int digitNumber) {
		EditText targetET = getDigitView(digitNumber);
		return digitET.equals(targetET);
	}
	
	private EditText getDigitView(int digitNumber) {
		switch (digitNumber) {
			case 0 : return (EditText)parent.findViewById(R.id.digit0);
			case 1 : return (EditText)parent.findViewById(R.id.digit1);
			case 2 : return (EditText)parent.findViewById(R.id.digit2);
			case 3 : return (EditText)parent.findViewById(R.id.digit3);
		}
		return null;
	}
	
	private EditText previousDigitView(EditText digitET) {
		switch (getDigitNumber(digitET)) {
			case 0 : return (EditText)parent.findViewById(R.id.digit0);
			case 1 : return (EditText)parent.findViewById(R.id.digit0);
			case 2 : return (EditText)parent.findViewById(R.id.digit1);
			case 3 : return (EditText)parent.findViewById(R.id.digit2);
		}
		return null;
	}
	
	private EditText nextDigitView(EditText digitET) {
		switch (getDigitNumber(digitET)) {
			case 0 : return (EditText)parent.findViewById(R.id.digit1);
			case 1 : return (EditText)parent.findViewById(R.id.digit2);
			case 2 : return (EditText)parent.findViewById(R.id.digit3);
			case 3 : return (EditText)parent.findViewById(R.id.digit3);
		}
		return null;
	}
	
	private void onClickEditText(EditText digitET) {
		if(digitET.length() > 0)
			digitET.setSelection(0, digitET.length());
		else 
			digitET.setSelection(digitET.length());
	}
	
	private int getDigitContent(int digitNumber) {
		try {
			return Integer.parseInt(getDigitView(digitNumber).getText().toString());
		} catch(NumberFormatException e) {
			return Constants.NOT_SPECIFIED;
		}
	}
	
	private void submit() {
		String result = "";
		for(int i=0; i<NUM_DIGIT; i++) {
			int digit = getDigitContent(i);
			if(digit == Constants.NOT_SPECIFIED) {
				Toast.makeText(PasswordActivity.this, (i+1)+"번째 자리 비밀번호를 정확히 입력해 주세요.", Toast.LENGTH_SHORT).show();
				return ;
			}
			result += digit;
		}
		Encrypter enc = new Encrypter();
		String encResult = enc.encryptString(result);
		if(encResult.trim().equals( UserInfo.getPassword(PasswordActivity.this).trim() )) {
			Toast.makeText(PasswordActivity.this, "비밀번호가 일치합니다.", Toast.LENGTH_SHORT).show();

			Intent intent = new Intent(PasswordActivity.this, MainActivity.class);
			intent.putExtras(targetModuleInfo);

			startActivity(intent);
			finish();
		} else {
			errorCount++;
			Toast.makeText(PasswordActivity.this, "비밀번호를 "+ errorCount +"회 틀렸습니다.", Toast.LENGTH_SHORT).show();
			if(errorCount >= 3) {
				Toast.makeText(PasswordActivity.this, "비밀번호를 3회 이상 틀렸습니다.", Toast.LENGTH_SHORT).show();
				// TODO : 잠금 && 보고
			}
		}
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
