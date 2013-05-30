package kr.go.KNPA.Romeo.Chat;

import java.util.ArrayList;

import kr.go.KNPA.Romeo.R;
import kr.go.KNPA.Romeo.Base.FragmentLayout;
import kr.go.KNPA.Romeo.Util.RomeoDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;

/**
 * 채팅방 화면
 */
public class RoomLayout extends FragmentLayout {

	public static final String	CHAT_LIST_VIEW_HEADER	= "chatListViewHeader";

	public interface Listener extends BaseListener {
		/**
		 * 카메라로 찍은 사진을 전송할 때
		 */
		void onTakePhotoFromCamera();

		/**
		 * 앨범에 있는 사진을 전송할 때
		 */
		void onTakePhotoFromAlbum();

		/**
		 * 내용이 text로 된 채팅을 전송할 때
		 * 
		 * @param text
		 *            채팅 내용
		 */
		void onSendText(String text);

		void onScrollToTop();
	}

	private Button			mAppendixBtn, mSubmitBtn;
	private EditText		mInputET;
	private ChatListView	mListView;
	protected Listener		mListener;

	public RoomLayout(Context context, LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		super(context, inflater, container, savedInstanceState, R.layout.chat_room_fragment);
		initInputBar();
		initListView();
	}

	/**
	 * chat_input_bar.xml에 있는 요소들에 대한 layout 설정
	 */
	private void initInputBar()
	{
		mAppendixBtn = (Button) mRoot.findViewById(R.id.appendixBtn);
		mSubmitBtn = (Button) mRoot.findViewById(R.id.submitBtn);
		mInputET = (EditText) mRoot.findViewById(R.id.inputET);

		// 사진 보내기 클릭 리스너
		mAppendixBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v)
			{
				RomeoDialog.Builder chooseDlg = new RomeoDialog.Builder(getActivity());
				chooseDlg.setTitle("사진 보내기");

				ArrayList<String> array = new ArrayList<String>();
				array.add("사진 촬영");
				array.add("앨범에서 선택");

				ArrayAdapter<String> arrayAdt = new ArrayAdapter<String>(getActivity(), R.layout.dialog_menu_cell2, array);

				chooseDlg.setAdapter(arrayAdt, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which)
					{
						if (mListener != null)
						{
							switch (which)
							{
							case 0:// 사진 촬영
								mListener.onTakePhotoFromCamera();
								break;
							case 1:// 앨범에서선택
								mListener.onTakePhotoFromAlbum();
								break;
							}
						}
					}
				});

				chooseDlg.setCancelable(true);
				chooseDlg.show();
			}
		});

		// mInputET에 텍스트가 입력되지 않으면 전송 버튼을 비활성화
		mInputET.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count)
			{
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after)
			{
			}

			@Override
			public void afterTextChanged(Editable s)
			{
				if (s.length() > 0)
				{
					mSubmitBtn.setEnabled(true);
				}
				else
				{
					mSubmitBtn.setEnabled(false);
				}
			}
		});

		/**
		 * 채팅 전송 클릭리스너
		 */
		mSubmitBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v)
			{
				if (mListener != null)
				{
					String text = mInputET.getText().toString();
					mListener.onSendText(text);
					mInputET.setText("");
				}
			}
		});
	}

	/**
	 * ListView 설정
	 */
	private void initListView()
	{
		mListView = (ChatListView) mRoot.findViewById(R.id.chatListView);

		mListView.setOnScrollListener(new OnScrollListener() {
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount)
			{
				if (firstVisibleItem == 0)
				{
					if (mListener != null)
					{
						mListener.onScrollToTop();
					}
				}
			}

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState)
			{

			}
		});
	}

	/**
	 * 채팅 목록을 표현하는 listview를 리턴한다
	 * 
	 * @return listview
	 */
	public ChatListView getListView()
	{
		return this.mListView;
	}

	public void setListener(Listener l)
	{
		mListener = l;
		setBaseListener(l);
	}
}
