package kr.go.KNPA.Romeo.GCM;

import java.util.ArrayList;

import kr.go.KNPA.Romeo.Base.Message;
import kr.go.KNPA.Romeo.Config.Event;
import kr.go.KNPA.Romeo.Config.KEY;
import kr.go.KNPA.Romeo.Config.StatusCode;
import kr.go.KNPA.Romeo.Connection.Connection;
import kr.go.KNPA.Romeo.Connection.Data;
import kr.go.KNPA.Romeo.Connection.Payload;
import kr.go.KNPA.Romeo.Document.Document;
import kr.go.KNPA.Romeo.Survey.Survey;
import kr.go.KNPA.Romeo.Survey.Survey.AnswerSheet;
import kr.go.KNPA.Romeo.Util.CallbackEvent;
import kr.go.KNPA.Romeo.Util.UserInfo;
import kr.go.KNPA.Romeo.Util.WaiterView;
import android.content.Context;

public class GCMMessageSender {

	/*
	 * ------------- Payload ------------- event : String :
	 * "event0 : subEvent : subSubEvnet.." sender : long : 발신자의 DB상의 idx값
	 * receivers : long[] : 수신자들의 DB상의 idx값의 모임 X roomCode : String :
	 * "senderIdx : departedTS" message : Object
	 */

	public static final String	MESSAGE_RECEIVED	= "MESSAGE:RECEIVED";
	public static final String	MESSAGE_DEPARTED	= "MESSAGE:DEPARTED";
	public static final String	TAG					= "GCMMessageSender";

	public GCMMessageSender()
	{
	}

	public static boolean setMessageChecked(int type, String messageIdx, String userIdx)
	{
		Payload request = new Payload().setData(new Data().add(0, KEY.MESSAGE.TYPE, type).add(0, KEY.MESSAGE.IDX, messageIdx).add(0, KEY.USER.IDX, userIdx));
		Connection conn = new Connection().requestPayload(request).request();
		Payload response = conn.getResponsePayload();

		if (response.getStatusCode() == StatusCode.SUCCESS)
		{
			String result = (String) response.getData().get(0, KEY._MESSAGE);
			return true;
		}
		else
		{
			return false;
		}
	}

	public static ArrayList<String> getUncheckers(int type, String idx)
	{
		Payload request = new Payload().setEvent(Event.MESSAGE_GET_UNCHECKERS).setData(new Data().add(0, KEY.MESSAGE.TYPE, type).add(0, KEY.MESSAGE.IDX, idx));
		Connection conn = new Connection().async(false).requestPayload(request).request();
		Payload response = conn.getResponsePayload();

		ArrayList<String> uncheckers = new ArrayList<String>();
		if (response.getStatusCode() == StatusCode.SUCCESS)
		{
			Data respData = response.getData();
			int nUncheckers = respData.size();
			for (int i = 0; i < nUncheckers; i++)
			{
				if (respData.get(i, KEY.USER.IDX) != null)
				{
					uncheckers.add((String) respData.get(i, KEY.USER.IDX).toString());
				}
			}

		}

		return uncheckers;
	}

	public static void sendMessage(final Context context, Message message)
	{
		Data reqData = new Data().add(0, KEY._MESSAGE, message);
		Payload request = new Payload().setEvent(Event.Message.send()).setData(reqData);

		CallbackEvent<Payload, Integer, Payload> callBack = new CallbackEvent<Payload, Integer, Payload>() {
			private Message	_message;

			@Override
			public void onPreExecute(Payload request)
			{
				_message = (Message) request.getData().get(0, KEY._MESSAGE);
			}

			@Override
			public void onPostExecute(Payload response)
			{

				if (response.getStatusCode() == StatusCode.SUCCESS)
				{

					String messageIdx = (String) response.getData().get(0, KEY.MESSAGE.IDX);
					_message.idx = messageIdx;
					// TODO : 실패한 발신자
					// TODO : 발신자 별 에러 컨트롤

					if (_message.mainType() == Message.MESSAGE_TYPE_DOCUMENT)
					{
						((Document) _message).afterSend(context, true);
					}
					else if (_message.mainType() == Message.MESSAGE_TYPE_SURVEY)
					{
						((Survey) _message).afterSend(context, true);
					}
				}
				else
				{
					// TODO : 실패했을때??
					if (_message.mainType() == Message.MESSAGE_TYPE_DOCUMENT)
					{
						((Document) _message).afterSend(context, false);
					}
					else if (_message.mainType() == Message.MESSAGE_TYPE_SURVEY)
					{
						((Survey) _message).afterSend(context, false);
					}
				}

				// for survey TODO
				WaiterView.dismissDialog(context);

			}

			@Override
			public void onError(String errorMsg, Exception e)
			{
				if (_message.mainType() == Message.MESSAGE_TYPE_DOCUMENT)
				{
					((Document) _message).afterSend(context, false);
				}
				else if (_message.mainType() == Message.MESSAGE_TYPE_SURVEY)
				{
					((Survey) _message).afterSend(context, false);
				}

				// for survey TODO
				WaiterView.dismissDialog(context);

			}
		};

		Connection conn = new Connection().requestPayload(request).callBack(callBack);
		conn.request();
	}

	public static void sendSurveyAnswerSheet(final Context context, final Survey survey, final AnswerSheet answerSheet)
	{

		Data reqData = new Data().add(0, KEY.USER.IDX, UserInfo.getUserIdx(context)).add(0, KEY.SURVEY.IDX, survey.idx).add(0, KEY.SURVEY.ANSWER_SHEET, answerSheet);
		Payload request = new Payload().setEvent(Event.Message.Survey.answerSurvey()).setData(reqData);

		CallbackEvent<Payload, Integer, Payload> callBack = new CallbackEvent<Payload, Integer, Payload>() {
			@Override
			public void onPostExecute(Payload response)
			{
				if (response.getStatusCode() == StatusCode.SUCCESS)
				{
					survey.afterSendAnswerSheet(context, answerSheet, true);
				}
				else
				{
					survey.afterSendAnswerSheet(context, answerSheet, false);
					// TODO : 정상적으로 처리되지 않았을 때 대응
				}
			}
		};

		Connection conn = new Connection().requestPayload(request).callBack(callBack);
		conn.request();
	}

}
