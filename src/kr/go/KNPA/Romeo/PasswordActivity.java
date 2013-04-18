package kr.go.KNPA.Romeo;

import kr.go.KNPA.Romeo.Config.Constants;
import kr.go.KNPA.Romeo.Util.Encrypter;
import kr.go.KNPA.Romeo.Util.UserInfo;
import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

public class PasswordActivity extends Activity {
	private static final int NUM_DIGIT = 4;
	private ViewGroup parent;
	
	private static int errorCount = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		parent = (ViewGroup)((LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE)).inflate(R.layout.password_activity, null, false);
		
		for(int i=0; i<NUM_DIGIT; i++) {
			final EditText digitET = getDigitView(i);
			
			TextWatcher watcher = new TextWatcher() {
				@Override	public void onTextChanged(CharSequence s, int start, int before, int count) {}
				@Override	public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
				@Override	public void afterTextChanged(Editable s) {	if(s.length() == 1)	digitET.focusSearch(View.FOCUS_RIGHT).requestFocus();	}
			};
		}
		
		setContentView(parent);
	}
	
	OnKeyListener keyListener = new OnKeyListener() {
		
		@Override
		public boolean onKey(View v, int keyCode, KeyEvent event) {
			EditText et = (EditText)v;
			if(keyCode == KeyEvent.KEYCODE_ENTER) {
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
	
	private EditText nextDigitView(EditText digitET) {
		switch (getDigitNumber(digitET)) {
			case 0 : return (EditText)parent.findViewById(R.id.digit0);
			case 1 : return (EditText)parent.findViewById(R.id.digit1);
			case 2 : return (EditText)parent.findViewById(R.id.digit2);
			case 3 : return (EditText)parent.findViewById(R.id.digit3);
		}
		return null;
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
		if(encResult == UserInfo.getPassword(PasswordActivity.this)) {
			Toast.makeText(PasswordActivity.this, "비밀번호가 일치합니다.", Toast.LENGTH_SHORT).show();
			// TODO : go Next Activity
		} else {
			errorCount++;
			Toast.makeText(PasswordActivity.this, "비밀번호를 "+ errorCount +"회 틀렸습니다.", Toast.LENGTH_SHORT).show();
			if(errorCount >= 3) {
				Toast.makeText(PasswordActivity.this, "비밀번호를 3회 이상 틀렸습니다.", Toast.LENGTH_SHORT).show();
				// TODO : 잠금 && 보고
			}
		}
	}
}
