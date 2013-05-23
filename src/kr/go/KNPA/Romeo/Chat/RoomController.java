package kr.go.KNPA.Romeo.Chat;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import kr.go.KNPA.Romeo.MainActivity;
import kr.go.KNPA.Romeo.R;
import kr.go.KNPA.Romeo.Base.BaseController;
import kr.go.KNPA.Romeo.Config.Constants;
import kr.go.KNPA.Romeo.Config.Event;
import kr.go.KNPA.Romeo.Config.KEY;
import kr.go.KNPA.Romeo.Config.StatusCode;
import kr.go.KNPA.Romeo.Connection.Connection;
import kr.go.KNPA.Romeo.Connection.Data;
import kr.go.KNPA.Romeo.Connection.Payload;
import kr.go.KNPA.Romeo.DB.DBProcManager;
import kr.go.KNPA.Romeo.Member.MemberDetailActivity;
import kr.go.KNPA.Romeo.Member.UserListActivity;
import kr.go.KNPA.Romeo.Util.Formatter;
import kr.go.KNPA.Romeo.Util.ImageManager;
import kr.go.KNPA.Romeo.Util.ImageViewActivity;
import kr.go.KNPA.Romeo.Util.UserInfo;
import kr.go.KNPA.Romeo.Util.WaiterView;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

public class RoomController extends BaseController implements RoomLayout.Listener, ChatListAdapter.Listener {

	private static final String	TAG					= RoomController.class.getSimpleName();

	private RoomLayout			mLayout;
	private RoomModel			mModel;
	private ChatListAdapter		mListAdapter;

	private Room				mRoom;
	private static Handler		mHandler;

	private static final int	PICK_FROM_CAMERA	= 0;
	private static final int	PICK_FROM_ALBUM		= 1;

	public static final int		ACTION_LEAVE_ROOM	= 1;
	public static final int		ACTION_INVITE_USERS	= 2;
	public static final int		ACTION_SET_TITLE	= 3;

	/**
	 * @name Constructor
	 * @{
	 */

	public RoomController(Room room)
	{
		mRoom = room;
	}

	/** @} */

	/**
	 * @name Life-Cycle
	 * @{
	 */

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		mModel = new RoomModel(getActivity(), mRoom);

		// 레이아웃 설정
		mLayout = new RoomLayout(getActivity(), inflater, container, savedInstanceState);
		mLayout.setListener(this);
		mLayout.setLeftNavBarBtnText(R.string.menu);
		mLayout.setRightNavBarBtnText(R.string.edit);

		// 방 이름 설정
		setNavBarTitle();

		if (mHandler == null)
		{
			mHandler = new Handler();
		}

		new Thread() {
			@Override
			public void run()
			{
				// listview에 adpater를 설정하고 초기 채팅 목록 불러오기
				mListAdapter = new ChatListAdapter(getActivity(), null, mModel.getRoom());
				mListAdapter.setListener(RoomController.this);
				mLayout.getListView().setAdapter(mListAdapter);

				final Cursor c = mListAdapter.query(ChatListAdapter.NUM_CHAT_PER_PAGE);

				mHandler.post(new Runnable() {
					public void run()
					{
						mListAdapter.changeCursor(c);
						mListAdapter.notifyDataSetChanged();
						mLayout.getListView().scrollToItem(0);
					}
				});

				super.run();
			}
		}.start();

		return mLayout.getView();
	}

	@Override
	public void onResume()
	{
		super.onResume();
		RoomListController.setCurrentRoom(this);
		new Thread() {
			public void run()
			{
				mModel.notifyLastReadTS(System.currentTimeMillis() / 1000);
				mModel.pullLastReadTS();
			}
		}.start();
	}

	@Override
	public void onPause()
	{
		super.onPause();
		RoomListController.setCurrentRoom(null);
	}

	@Override
	public void onDestroy()
	{
		mHandler = null;
		super.onDestroy();
	}

	/**
	 * 채팅 수신 시, 채팅방 화면이 ForeGround에 있다면(ChatFragment.getCurrentRoom() != null)
	 * \n 다른 쓰레드에서 GCM에 의해 이 메소드가 호출됨\n 이 메소드가 호출될 때는 이미 Local DB에 새 채팅이 저장된
	 * 후임.\n 그러므로 lastReadTS를 업데이트 하고 채팅 목록에 1개를 추가하여 불러온다
	 * 
	 * @param chat
	 *            도착한 채팅
	 */
	public void onReceiveChat(Chat chat)
	{
		mModel.notifyLastReadTS(System.currentTimeMillis() / 1000);

		final Cursor c = mListAdapter.query(mListAdapter.getCount() + 1);

		mHandler.post(new Runnable() {
			@Override
			public void run()
			{
				mListAdapter.changeCursor(c);
				mListAdapter.notifyDataSetChanged();
				mLayout.getListView().scrollToItem(0);
			}
		});
	}

	/**
	 * 다른 사람이 채팅 목록을 읽었을 때 GCMMessageManager에 의해 다른 쓰레드에서 호출된다.
	 * 
	 * @param userIdx
	 * @param lastReadTS
	 */
	public void onUpdateLastTS(String userIdx, long lastReadTS)
	{
		mModel.updateLastReadTS(userIdx, lastReadTS);

		final Cursor c = mListAdapter.query(mListAdapter.getCount());

		mHandler.post(new Runnable() {
			@Override
			public void run()
			{
				mListAdapter.changeCursor(c);
				mListAdapter.notifyDataSetChanged();
			}
		});
	}

	public void onChatterJoin(String inviterIdx, ArrayList<String> newbies)
	{
		mModel.addChatters(inviterIdx, newbies);

		final Cursor c = mListAdapter.query(mListAdapter.getCount() + 1);

		mHandler.post(new Runnable() {
			@Override
			public void run()
			{
				setNavBarTitle();
				mListAdapter.changeCursor(c);
				mListAdapter.notifyDataSetChanged();
				mLayout.getListView().scrollToItem(0);
			}
		});
	}

	public void onChatterLeave(String userIdx)
	{
		mModel.removeChatter(userIdx);

		final Cursor c = mListAdapter.query(mListAdapter.getCount() + 1);

		mHandler.post(new Runnable() {
			@Override
			public void run()
			{
				setNavBarTitle();
				mListAdapter.changeCursor(c);
				mListAdapter.notifyDataSetChanged();
				mLayout.getListView().scrollToItem(0);
			}
		});
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		if (resultCode != Activity.RESULT_OK)
		{
			return;
		}
		String filePath = null;
		switch (requestCode)
		{
		case PICK_FROM_ALBUM:
			mImageCaptureUri = data.getData();
			Cursor c = getActivity().getContentResolver().query(mImageCaptureUri, null, null, null, null);
			c.moveToNext();
			filePath = c.getString(c.getColumnIndex(MediaStore.MediaColumns.DATA));
			c.close();
			sendChat(Chat.CONTENT_TYPE_PICTURE, filePath);
			break;
		case PICK_FROM_CAMERA:
			filePath = mImageCaptureUri.getPath();
			sendChat(Chat.CONTENT_TYPE_PICTURE, filePath);
			break;

		case RoomSettingActivity.REQUEST_CODE:
			Bundle b = data.getExtras();

			int action = b.getInt(RoomSettingActivity.KEY_ACTION);

			switch (action)
			{

			case ACTION_LEAVE_ROOM:
				WaiterView.showDialog(getActivity());
				new Thread() {
					public void run()
					{

						mModel.deleteRoom();

						mHandler.post(new Runnable() {
							public void run()
							{
								MainActivity.sharedActivity().popContent();
								WaiterView.dismissDialog(getActivity());
							};
						});
					}
				}.start();

				break;
			case ACTION_INVITE_USERS:

				final ArrayList<String> newChatters = b.getStringArrayList(RoomSettingActivity.KEY_IDXS);

				if (newChatters.size() == 0)
				{
					return;
				}

				new Thread() {
					public void run()
					{
						String userIdx = UserInfo.getUserIdx(getActivity());
						// 추가
						if (mModel.addChatters(userIdx, newChatters) == true)
						{
							// 성공 시 refresh, 타이틀의 사람 숫자 변경
							final Cursor c = mListAdapter.query(mListAdapter.getCount() + 1);
							mHandler.post(new Runnable() {
								public void run()
								{
									mListAdapter.changeCursor(c);
									mListAdapter.notifyDataSetChanged();
									setNavBarTitle();
									mLayout.getListView().scrollToItem(0);
								}
							});
						}
						else
						{
							mHandler.post(new Runnable() {
								public void run()
								{
									Toast.makeText(getActivity(), "서버와 통신에 실패하였습니다. 다시 시도해주세요.", Toast.LENGTH_LONG).show();
								}
							});
						}

					}
				}.start();

				break;
			}

		default:
			return;
		}
	}

	private void startCreateRoomThread()
	{
		Thread newRoomThread = new Thread() {
			@Override
			public void run()
			{
				super.run();
				mHandler.post(new Runnable() {
					@Override
					public void run()
					{
						WaiterView.showDialog(getActivity());
					}
				});

				// 방 생성
				if (mModel.createRoom() == true)
				{
					mHandler.post(new Runnable() {
						@Override
						public void run()
						{
							setNavBarTitle();
							WaiterView.dismissDialog(getActivity());
						}
					});
				}
				else
				{
					mHandler.post(new Runnable() {
						@Override
						public void run()
						{
							WaiterView.dismissDialog(getActivity());
							Toast.makeText(getActivity(), "방 생성 실패", Toast.LENGTH_SHORT).show();
						}
					});
				}
			}
		};

		newRoomThread.start();

		try
		{
			newRoomThread.join();
		}
		catch (InterruptedException e)
		{
			Log.e(TAG, "쓰레드 interrupt로 인한 방 생성 실패 " + e.getMessage());
			e.printStackTrace();
		}
	}

	private Uri	mImageCaptureUri;

	@Override
	public void onTakePhotoFromCamera()
	{
		/*
		 * 참고 해볼곳 http://2009.hfoss.org/Tutorial:Camera_and_Gallery_Demo
		 * http://stackoverflow
		 * .com/questions/1050297/how-to-get-the-url-of-the-captured-image
		 * http://www.damonkohler.com/2009/02/android-recipes.html
		 * http://www.firstclown.us/tag/android/
		 */

		// 찍은 사진을 저장할 경로를 설정함
		Date now = new Date();
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.KOREA);

		// 카메라 촬영을 할 수 있는 액티비티를 실행할 수 있도록 인텐트 객체를 생성한다.
		Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
		// 외장 메모리에서 공유를 목적으로 하는 사진을 저장할 수 있는 폴더 경로를
		// File 객체로 얻는다.
		File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);

		String fileName = "DAON_" + format.format(now) + ".jpg";

		// 폴더 경로에 해당하는 폴더가 존재하지 않으면 폴더를 생성한다.
		if (!path.exists())
			path.mkdirs();

		File file = new File(path, fileName + ".jpg");
		// 파일 경로가 저장된 File 객체의 URI 를 얻는다.
		mImageCaptureUri = Uri.fromFile(file);
		// 인텐트에 URI 정보를 저장한다.
		// 카메라 액티비티는 이 URI 에 입력된 경로에 촬영한 이미지를 저장한다.
		intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, mImageCaptureUri);

		// 인텐트 정보에 맞는 액티비티를 실행한다.
		startActivityForResult(intent, PICK_FROM_CAMERA);
	}

	@Override
	public void onTakePhotoFromAlbum()
	{
		// 앨범 호출
		Intent intent = new Intent(Intent.ACTION_PICK);
		intent.setType(android.provider.MediaStore.Images.Media.CONTENT_TYPE);
		startActivityForResult(intent, PICK_FROM_ALBUM);
	}

	@Override
	public void onLeftNavBarBtnClick()
	{
		MainActivity.sharedActivity().toggle();
	}

	@Override
	public void onRightNavBarBtnClick()
	{
		Intent intent = new Intent(getActivity(), RoomSettingActivity.class);
		startActivityForResult(intent, RoomSettingActivity.REQUEST_CODE);
	}

	private void sendChat(int contentType, final String content)
	{
		if (mModel.getRoom().getStatus() == Room.STATUS_VIRTUAL)
		{
			startCreateRoomThread();
		}

		String senderIdx = UserInfo.getUserIdx(getActivity());
		ArrayList<String> receivers = mModel.getRoom().getChattersIdx();

		String chatIdx = Chat.makeChatIdx(getActivity());

		String chatContent = "";

		switch (contentType)
		{
		case Chat.CONTENT_TYPE_TEXT:
			chatContent = content;
			break;
		case Chat.CONTENT_TYPE_PICTURE:
			chatContent = "";
			break;
		default:
			break;
		}

		final Chat chat = new Chat(chatIdx, mModel.getRoom().getType(), chatContent, senderIdx, receivers, false, System.currentTimeMillis() / 1000, true, System.currentTimeMillis() / 1000, mModel
				.getRoom().getCode(), Chat.CONTENT_TYPE_TEXT);

		new Thread() {
			public void run()
			{
				// local db에 저장
				DBProcManager.sharedManager(getActivity()).chat().saveChatOnSend(chat.idx, chat.roomCode, chat.senderIdx, chat.content, chat.contentType, chat.TS, Chat.STATE_SENDING);

				// 채팅 리스목록에 보내고 있는 채팅 추가
				final Cursor c = mListAdapter.query(mListAdapter.getCount() + 1);
				mHandler.post(new Runnable() {
					public void run()
					{
						mListAdapter.changeCursor(c);
						mListAdapter.notifyDataSetChanged();
						mLayout.getListView().scrollToItem(0);
					}
				});

				// 사진 전송일 경우 사진부터 업로드
				if (chat.contentType == Chat.CONTENT_TYPE_PICTURE)
				{
					ImageManager im = new ImageManager();

					// 업로드 실패시 해당 채팅의 상태를 fail로 변경하고 서버로 보내지 말고 종료
					if (im.upload(ImageManager.CHAT_SIZE_ORIGINAL, chat.idx, content, false) == false)
					{
						DBProcManager.sharedManager(getActivity()).chat().updateChatState(chat.idx, Chat.STATE_FAIL);

						// 채팅 상태가 변경되었으므로 채팅 리스트 커서 새로고침
						final Cursor cursor = mListAdapter.query(mListAdapter.getCount());
						mHandler.post(new Runnable() {
							public void run()
							{
								mListAdapter.changeCursor(cursor);
								mListAdapter.notifyDataSetChanged();
							}
						});
						// 쓰레드 종료
						return;
					}
				}

				// 서버로 전송
				Data reqData = new Data().add(0, KEY._MESSAGE, chat);
				Payload request = new Payload().setEvent(Event.Message.send()).setData(reqData);
				Connection conn = new Connection().requestPayload(request).async(false);
				conn.request();

				// 응답 받아와서 성공여부를 핸들러에 알림
				Payload response = conn.getResponsePayload();

				// 응답 여부에 따라 채팅의 상태를 success 또는 fail로 변경
				int chatStatus = response.getStatusCode() == StatusCode.SUCCESS ? Chat.STATE_SUCCESS : Chat.STATE_FAIL;

				DBProcManager.sharedManager(getActivity()).chat().updateChatState(chat.idx, chatStatus);

				// 채팅 상태가 변경되었으므로 채팅 리스트 커서 새로고침
				final Cursor cursor = mListAdapter.query(mListAdapter.getCount());
				mHandler.post(new Runnable() {
					public void run()
					{
						mListAdapter.changeCursor(cursor);
						mListAdapter.notifyDataSetChanged();
					}
				});

				// 자신이 보낸 채팅은 바로 읽었다고 다른 사람들에게 notify
				mModel.notifyLastReadTS(System.currentTimeMillis() / 1000);
			}
		}.start();
	}

	@Override
	public void onSendText(String text)
	{
		sendChat(Chat.CONTENT_TYPE_TEXT, text);
	}

	public Room getRoom()
	{
		return this.mModel.getRoom();
	}

	public void setNavBarTitle()
	{
		String roomName = mModel.getRoomName();

		String ellipsedName = Formatter.makeEllipsis(roomName, Constants.CHAT_ROOM_TITLE_MAX_LEN);

		int n = mModel.getRoom().chatters.size() + 1;

		if (n == 2)
		{
			mLayout.setNavBarTitleTV(ellipsedName);
		}
		else
		{
			mLayout.setNavBarTitleTV(ellipsedName + " (" + String.valueOf(n) + "명)");
		}

		mLayout.getView().requestLayout();
	}

	@Override
	public void onChatDelete(final String chatIdx)
	{
		new Thread() {
			public void run()
			{

				DBProcManager.sharedManager(getActivity()).chat().deleteChat(chatIdx);

				final Cursor cursor = mListAdapter.query(mListAdapter.getCount() - 1);

				mHandler.post(new Runnable() {
					@Override
					public void run()
					{
						mListAdapter.changeCursor(cursor);
						mListAdapter.notifyDataSetChanged();
					}
				});
			};
		}.start();

	}

	@SuppressWarnings("deprecation")
	@Override
	public void onChatTextCopy(String text)
	{
		if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.HONEYCOMB)
		{
			android.text.ClipboardManager clipboard = (android.text.ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
			clipboard.setText(text);
		}
		else
		{
			android.content.ClipboardManager clipboard = (android.content.ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);

			android.content.ClipData clip = android.content.ClipData.newPlainText("text", text);
			clipboard.setPrimaryClip(clip);
		}
	}

	@Override
	public void onProfileImageClick(String senderIdx)
	{
		Bundle b = new Bundle();
		b.putString(MemberDetailActivity.KEY_IDX, senderIdx);
		b.putInt(MemberDetailActivity.KEY_IDX_TYPE, MemberDetailActivity.IDX_TYPE_USER);
		Intent intent = new Intent(getActivity(), MemberDetailActivity.class);
		intent.putExtras(b);
		getActivity().startActivity(intent);
	}

	@Override
	public void onChatImageClick(String imageIdx)
	{
		Bundle b = new Bundle();
		b.putInt("imageType", ImageManager.CHAT_SIZE_ORIGINAL);
		b.putString("imageHash", imageIdx);
		Intent intent = new Intent(getActivity(), ImageViewActivity.class);
		intent.putExtras(b);
		getActivity().startActivity(intent);
	}

	@Override
	public void onGoToUncheckersList(ArrayList<String> uncheckers)
	{
		Intent intent = new Intent(MainActivity.sharedActivity(), UserListActivity.class);
		intent.putExtra(UserListActivity.KEY_USERS_IDX, uncheckers);
		intent.putExtra(UserListActivity.KEY_TITLE, "미확인자 명단");
		getActivity().startActivity(intent);
	}

	@Override
	public void onFailedChatReSend(String chatIdx)
	{

	}

	@Override
	public void onScrollToTop()
	{
		if (mModel.getRoom().getStatus() != Room.STATUS_CREATED)
		{
			return;
		}

		final int nItems = mListAdapter.getCount();
		final int nTotal = mListAdapter.numTotalChat;

		synchronized (mLayout.getListView().isLoading)
		{

			if (nItems < nTotal && mLayout.getListView().isLoading == false)
			{
				new Thread() {
					@Override
					public void run()
					{
						super.run();

						mLayout.getListView().isLoading = true;

						final Cursor c = mListAdapter.query(nItems + ChatListAdapter.NUM_CHAT_PER_PAGE);

						mHandler.post(new Runnable() {
							@Override
							public void run()
							{
								mListAdapter.changeCursor(c);
								mListAdapter.notifyDataSetChanged();
								mLayout.getListView().scrollToItem(nItems);
								mLayout.getListView().isLoading = false;
							}
						});
					}
				}.start();
			}

		}
	}
}
