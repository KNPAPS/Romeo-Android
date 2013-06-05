package kr.go.KNPA.Romeo;

import kr.go.KNPA.Romeo.Config.Event;
import kr.go.KNPA.Romeo.Config.KEY;
import kr.go.KNPA.Romeo.Config.StatusCode;
import kr.go.KNPA.Romeo.Connection.Connection;
import kr.go.KNPA.Romeo.Connection.Data;
import kr.go.KNPA.Romeo.Connection.Payload;
import kr.go.KNPA.Romeo.GCM.GCMRegisterManager;
import kr.go.KNPA.Romeo.Member.Department;
import kr.go.KNPA.Romeo.Member.MemberManager;
import kr.go.KNPA.Romeo.Member.User;
import kr.go.KNPA.Romeo.Register.NotRegisteredActivity;
import kr.go.KNPA.Romeo.Register.StatusChecker;
import kr.go.KNPA.Romeo.Register.UserRegisterActivity;
import kr.go.KNPA.Romeo.Util.CacheManager;
import kr.go.KNPA.Romeo.Util.RomeoDialog;
import kr.go.KNPA.Romeo.Util.UserInfo;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

public class IntroActivity extends Activity {// extends BaseActivity{
	private final int				REQUEST_REGISTER_USER	= 0;
	private static StatusChecker	checker;

	private Bundle					targetModuleInfo		= null;

	public IntroActivity()
	{
		// super(R.string.changing_fragments);

		super();
	}

	private boolean	userRegistered	= false;
	private boolean	userEnabled		= false;
	private boolean	devRegistered	= false;
	private boolean	devEnabled		= false;

	@Override
	protected void onNewIntent(Intent intent)
	{
		targetModuleInfo = new Bundle();
		Bundle b = intent.getExtras();
		if (b != null)
			targetModuleInfo.putAll(b);
	}

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.intro);

		new Thread(new Runnable() {

			@Override
			public void run()
			{
				Intent intent = getIntent();
				targetModuleInfo = new Bundle();

				Bundle b = intent.getExtras();
				if (b != null)
					targetModuleInfo.putAll(b);

				checkRegistered();

				loadBaseDataFromServer_Temporary();
			}

		}).start();
	}

	private void loadBaseDataFromServer_Temporary()
	{
		// TODO 적당한 캐시 정책을 세우고 구현할 때 까지 임시로 사용. 앱 시작 시에 모든 유저에 대한 정보를 캐싱한다.
		Data reqData = new Data();
		reqData.add(0, KEY.DEPT.IDX, "");
		reqData.add(0, KEY.DEPT.FETCH_RECURSIVE, 1);
		reqData.add(0, KEY.USER.IDX, UserInfo.getUserIdx(this));

		Payload request = new Payload().setData(reqData).setEvent(Event.USER_GET_MEMBERS);
		Payload response = new Connection().requestPayload(request).async(false).request().getResponsePayload();

		if (response.getStatusCode() == StatusCode.SUCCESS)
		{
			Data d = response.getData();
			for (int i = 0; i < d.size(); i++)
			{

				String deptIdx = d.get(i).get(KEY.DEPT.IDX).toString();
				Department dept = MemberManager.sharedManager().getDeptartment(deptIdx);
				User u = new User(d.get(i).get(KEY.USER.IDX).toString(), d.get(i).get(KEY.USER.NAME).toString(), Integer.parseInt(d.get(i).get(KEY.USER.RANK).toString()), d.get(i).get(KEY.USER.ROLE)
						.toString(), dept);

				CacheManager.addUserToMemCache(u);
			}

		}
	}

	private void checkRegistered()
	{
		/**
		 * 애플리케이션을 구동하기 위해 요구되는 상태를 체크하는 객체
		 */
		checker = new StatusChecker(this);

		if (!checker.isConnectedToNetwork())
		{
			// TODO 인터넷이 안될 때 띄울 화면 처리.
			// 만약 처음에 킬 땐 안 됐다가 나중에 연결하는 걸 대비해서
			// 다음 사이트에 있는 내용을 참고하여 구현해야함
			// http://shstarkr.tistory.com/158
			finish();
		}

		checkUserRegistered();

		if (userRegistered)
		{
			checkDeviceRegistered();

			if (devRegistered)
			{
				goNextActivity();
			}
			else
			{
				GCMRegisterManager.registerGCM(IntroActivity.this);
				devRegistered = true;
				goNextActivity();
			}

		}
		else
		{
			startUserRegisterActivity();
			// 기기 등록은 onResultActivity에서
		}

	}

	private void checkUserRegistered()
	{
		/**
		 * 유저 상태 체크 USER_REGISTERED_ENABLED 상태가 아니라면 해당 상태마다 필요한 Activity를 호출.
		 * USER_REGISTERED_ENABLED일 경우 기기 인증 진행
		 */
		switch (checker.getUserStatus())
		{

		case StatusChecker.USER_NOT_REGISTERED: // 등록이 되어있지 않을 때
			userRegistered = false;
			userEnabled = false;
			break;
		case StatusChecker.USER_REGISTERED_NOT_ENABLED: // 아직 유저 활성화가 안됨
			userRegistered = true;
			userEnabled = false;
			break;

		case StatusChecker.USER_REGISTERED_ENABLED:
			userRegistered = true;
			userEnabled = true;
			break;
		}

	}

	private void checkDeviceRegistered()
	{
		/**
		 * 기기 상태 체크 DEVICE_REGISTERED_ENABLED 상태가 아니라면 해당 상태마다 필요한 Activity를 호출.
		 * DEVICE_REGISTERED_ENABLED일 경우 기기 인증 진행
		 */
		switch (checker.getDeviceStatus())
		{
		case StatusChecker.DEVICE_NOT_REGISTERED:
			devRegistered = false;
			devEnabled = false;
			break;

		case StatusChecker.DEVICE_REGISTERED_NOT_ENABLED:
			devRegistered = true;
			devEnabled = false;
			break;

		case StatusChecker.DEVICE_REGISTERED_ENABLED:
			devRegistered = true;
			devEnabled = true;
			break;
		}

	}

	private void startUserRegisterActivity()
	{
		runOnUiThread(new Runnable() {

			@Override
			public void run()
			{
				Intent intent = new Intent(IntroActivity.this, UserRegisterActivity.class);
				// if(targetModuleInfo != null)
				// intent.putExtras(targetModuleInfo);
				startActivityForResult(intent, REQUEST_REGISTER_USER);
				overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

			}
		});

	}

	private void startNotRegisteredActivity()
	{
		runOnUiThread(new Runnable() {

			@Override
			public void run()
			{
				Intent intent = new Intent(IntroActivity.this, NotRegisteredActivity.class);
				startActivity(intent);
				overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
				finish();
			}
		});

	}

	private void goNextActivity()
	{
		// userRegistered == true , devRegistered == true;
		if (userEnabled && devEnabled)
			runApplication();
		else
			startNotRegisteredActivity();
	}

	private void runApplication()
	{
		runOnUiThread(new Runnable() {

			@Override
			public void run()
			{
				/**
				 * 모든 검증을 정상적으로 통과. 메인 액티비티 시작
				 */

				Intent intent = null;

				// intent = new Intent(IntroActivity.this, MainActivity.class);
				// intent.putExtras(targetModuleInfo);
				//
				// startActivity(intent);

				// GO TO PASSWORD ACTIVITY
				intent = new Intent(IntroActivity.this, PasswordActivity.class);
				intent.putExtras(targetModuleInfo);

				startActivity(intent);

				finish();
				overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		if (resultCode == RESULT_OK)
		{
			if (requestCode == REQUEST_REGISTER_USER)
			{
				// UserRegisterActivity를 통해 User는 성공적으로 동록되었다고 본다.
				// (UserRegisterActivity가 유저의 등록을 책임진다.)

				// ///////////////////////
				// userRegistered O, userEnabled ?(X)
				// //////////////////////
				checkDeviceRegistered();

				if (devRegistered)
				{
					goNextActivity();
				}
				else
				{
					GCMRegisterManager.registerGCM(IntroActivity.this);
					goNextActivity();
				}

				// TODO : 유저 등록, 디바이스 등록을 할만큼 처음 사용하는 것이라면,
				// 메시지가 온적도 없을 것이고, 메시지가 와서 그것을 알리는 푸시 노티피케이션을 누르고
				// 앱 && 액티비티가 실행되는 일도 없을 것이다.
				// 따라서 이 곳에 해당 모듈이 실행되도록 하는 로직을 구성할 필요는 없다.
				// TODO : 분실 신고, 분실 중 메시지 도착, 단말기 복구 ?? =>> 쌓여있던 메시지들은??,,,,
			}

		}
		else
		{
			new RomeoDialog.Builder(IntroActivity.this).setTitle("유저 등록에 실패했습니다.")// context.getString(kr.go.KNPA.Romeo.R.string.)
					.setMessage("다시 시도해주시기 바랍니다. 문제가 반복되어 발생하는 경우 재설치해주시기 바랍니다.").setPositiveButton(IntroActivity.this.getString(kr.go.KNPA.Romeo.R.string.ok), new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which)
						{
							dialog.dismiss();
							android.os.Process.killProcess(android.os.Process.myPid());
						}
					}).show();
		}
	}

	/*
	 * public void removeIntroView(ViewGroup v) { View view =
	 * (View)v.findViewById(R.id.intro); if(view == null) view =
	 * (View)v.findViewWithTag("intro"); if(view != null) v.removeView(view);
	 * 
	 * }
	 */
	@Override
	public void onBackPressed()
	{
		// super.onBackPressed();
		// h.removeCallbacks(logo);
		return;
	}

}
