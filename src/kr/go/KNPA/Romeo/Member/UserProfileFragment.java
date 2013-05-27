package kr.go.KNPA.Romeo.Member;

import kr.go.KNPA.Romeo.MainActivity;
import kr.go.KNPA.Romeo.R;
import kr.go.KNPA.Romeo.Config.Constants;
import kr.go.KNPA.Romeo.Connection.Payload;
import kr.go.KNPA.Romeo.Util.CallbackEvent;
import kr.go.KNPA.Romeo.Util.ImageManager;
import kr.go.KNPA.Romeo.Util.UserInfo;
import kr.go.KNPA.Romeo.Util.WaiterView;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class UserProfileFragment extends Fragment {

	private CallbackEvent<Payload, Integer, Payload>	picCallback	= null;
	private Uri											picUri		= null;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		picCallback = new CallbackEvent<Payload, Integer, Payload>() {
			public void onError(String errorMsg, Exception e)
			{
				WaiterView.dismissDialog(getActivity());
				AlertDialog ad = new AlertDialog.Builder(getActivity()).setIcon(R.drawable.icon_dialog).setTitle("사진을 전송할 수 없습니다.").setMessage(errorMsg + " 잠시후에 다시 시도해 주세요.")
						.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which)
							{
								dialog.dismiss();
							}
						}).show();
			};

			public void onProgressUpdate(Integer progress)
			{
				WaiterView.setProgress(progress);
			};

			public void onPostExecute(Payload result)
			{
				ImageView userPicIV = (ImageView) getView().findViewById(R.id.userPic);
				if (picUri != null)
					userPicIV.setImageURI(picUri);
				WaiterView.dismissDialog(getActivity());
			};
		};
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.user_profile_fragment, container, false);

		Button menuBT = (Button) view.findViewById(R.id.menu);
		menuBT.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0)
			{
				MainActivity.sharedActivity().toggle();

			}
		});
		ImageView userPicIV = (ImageView) view.findViewById(R.id.userPic);
		ImageManager im = new ImageManager();
		im.loadToImageView(ImageManager.PROFILE_SIZE_MEDIUM, UserInfo.getUserIdx(getActivity()), userPicIV);
		// userPicIV.setImageResource(R.drawable.user_pic_default);

		userPicIV.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view)
			{
				Intent intent = new Intent(Intent.ACTION_PICK);
				intent.setType(android.provider.MediaStore.Images.Media.CONTENT_TYPE);
				startActivityForResult(intent, Constants.REQUEST_PIC_PICKER);
				// http://stackoverflow.com/questions/6147884/onactivityresult-not-being-called-in-fragment
			}
		});

		TextView nameTV = (TextView) view.findViewById(R.id.name);
		nameTV.setText(UserInfo.getName(getActivity()));

		TextView rankTV = (TextView) view.findViewById(R.id.rank);
		rankTV.setText(User.RANK[UserInfo.getRank(getActivity())]);

		TextView departmentTV = (TextView) view.findViewById(R.id.department);
		departmentTV.setText(UserInfo.getDepartment(getActivity()));

		return view;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		if (requestCode == Constants.REQUEST_PIC_PICKER)
		{

			if (resultCode == Activity.RESULT_CANCELED || data.getData() == null)
			{
				return;
			}

			picUri = data.getData();

			if (picUri != null)
			{
				WaiterView.showDialog(getActivity());
				ImageManager im = new ImageManager().callBack(picCallback);
				im.upload(ImageManager.PROFILE_SIZE_ORIGINAL, UserInfo.getUserIdx(getActivity()), picUri);
			}
		}
		else
		{
			super.onActivityResult(requestCode, resultCode, data);
		}
	}
}
