package kr.go.KNPA.Romeo.Chat;

import java.util.ArrayList;

import kr.go.KNPA.Romeo.R;
import kr.go.KNPA.Romeo.Base.FragmentLayout;
import kr.go.KNPA.Romeo.Util.RomeoDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;

public class RoomListFragmentLayout extends FragmentLayout {

	public interface Listener extends BaseListener {
		void onEnterRoom(String roomCode);

		void onGoToSetRoomAliasActivity(String roomCode);

		void onDeleteRoom(String roomCode);
	}

	private RoomListView	mListView;
	private Listener		mListener;

	public RoomListFragmentLayout(Context context, LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState, int layoutResourceId)
	{
		super(context, inflater, container, savedInstanceState, layoutResourceId);

		initListView();
	}

	private void initListView()
	{
		mListView = (RoomListView) mRoot.findViewById(R.id.roomListView);
		mListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, final int position, long l_position)
			{
				if (mListener != null)
				{
					String roomCode = view.getTag().toString();
					mListener.onEnterRoom(roomCode);
				}
			}
		});

		mListView.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, final View view, int position, long id)
			{
				if (mListener != null)
				{
					final String roomCode = view.getTag().toString();

					final RomeoDialog.Builder chooseDlg = new RomeoDialog.Builder(mContext);
					chooseDlg.setTitle("옵션");

					ArrayList<String> array = new ArrayList<String>();
					array.add("채팅방 이름 설정");
					array.add("나가기");

					ArrayAdapter<String> arrayAdt = new ArrayAdapter<String>(mContext, R.layout.dialog_menu_cell2, array);

					chooseDlg.setAdapter(arrayAdt, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(final DialogInterface menus, int which)
						{
							switch (which)
							{
							case 0:// 채팅방 이름 설정
								mListener.onGoToSetRoomAliasActivity(roomCode);
								break;
							case 1:// 나가기
								new RomeoDialog.Builder(getActivity()).setIcon(getActivity().getResources().getDrawable(kr.go.KNPA.Romeo.R.drawable.icon_dialog)).setTitle("다On")// context.getString(kr.go.KNPA.Romeo.R.string.)
										.setMessage("방에서 나가면 채팅 내역이 모두 삭제됩니다. 방에서 나가시겠습니까? ").setPositiveButton(kr.go.KNPA.Romeo.R.string.ok, new DialogInterface.OnClickListener() {
											@Override
											public void onClick(DialogInterface dialog, int which)
											{
												dialog.dismiss();
												menus.dismiss();
												mListener.onDeleteRoom(roomCode);
											}
										}).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
											@Override
											public void onClick(DialogInterface dialog, int whichButton)
											{
												dialog.dismiss();
												menus.dismiss();
											}
										}).show();
								break;
							}
						}
					});

					chooseDlg.setCancelable(true);
					chooseDlg.show();
				}
				return false;
			}
		});
	}

	public void setBackground(Drawable d)
	{
		if (Build.VERSION.SDK_INT < 16)
		{
			getListView().setBackgroundDrawable(d);
		}
		else
		{
			getListView().setBackground(d);
		}
	}

	public RoomListView getListView()
	{
		return this.mListView;
	}

	public void setListener(Listener l)
	{
		mListener = l;
		setBaseListener(l);
	}
}
