package kr.go.KNPA.Romeo.Chat;

import java.util.ArrayList;

import kr.go.KNPA.Romeo.Config.Constants;
import kr.go.KNPA.Romeo.Member.MemberManager;
import kr.go.KNPA.Romeo.Member.User;

/**
 * 채팅방에 대한 Entity Class
 */
public class Room {

	private String				mCode;
	private String				mTitle;
	private String				mAlias;
	private int					mType;
	private int					mStatus;
	private boolean				isAlarmOn;
	private boolean				isHost			= false;

	public ArrayList<Chatter>	chatters;

	public static final int		STATUS_VIRTUAL	= 1;
	public static final int		STATUS_CREATED	= 2;
	public static final int		STATUS_INVITED	= 3;

	public static final int		TYPE_MEETING	= Chat.TYPE_MEETING;
	public static final int		TYPE_COMMAND	= Chat.TYPE_COMMAND;

	public Room(String roomCode)
	{
		mTitle = "";
		mAlias = "";
		mCode = roomCode;
		mType = Constants.NOT_SPECIFIED;
		mStatus = STATUS_CREATED;
		chatters = new ArrayList<Chatter>();
	}

	public Room()
	{
		mTitle = "";
		mAlias = "";
		mCode = "";
		mType = Constants.NOT_SPECIFIED;
		mStatus = STATUS_VIRTUAL;

		chatters = new ArrayList<Chatter>();
	}

	public ArrayList<String> getChattersIdx()
	{
		ArrayList<String> chattersIdx = new ArrayList<String>(chatters.size());

		for (int i = 0; i < chatters.size(); i++)
		{
			chattersIdx.add(chatters.get(i).idx);
		}

		return chattersIdx;
	}

	public Chatter getChatter(String chatterIdx)
	{
		for (int i = 0; i < chatters.size(); i++)
		{
			Chatter c = chatters.get(i);
			if (c.idx.equalsIgnoreCase(chatterIdx))
			{
				return c;
			}
		}
		return null;
	}

	public void setLastReadTS(String chatterIdx, Long TS)
	{
		for (int i = 0; i < chatters.size(); i++)
		{
			Chatter c = chatters.get(i);
			if (c.idx.equals(chatterIdx))
			{
				c.lastReadTS = TS;
				chatters.set(i, c);
			}
		}
	}

	public String getCode()
	{
		return mCode;
	}

	public void setCode(String code)
	{
		this.mCode = code;
	}

	public String getTitle()
	{
		return mTitle;
	}

	public void setTitle(String title)
	{
		this.mTitle = title;
	}

	public String getAlias()
	{
		return mAlias;
	}

	public void setAlias(String alias)
	{
		this.mAlias = alias;
	}

	public int getType()
	{
		return mType;
	}

	public void setType(int type)
	{
		this.mType = type;
	}

	public int getStatus()
	{
		return mStatus;
	}

	public void setStatus(int status)
	{
		this.mStatus = status;
	}

	public void addChattersIdx(ArrayList<String> chattersIdx)
	{
		ArrayList<User> newbies = MemberManager.sharedManager().getUsers(chattersIdx);
		for (int i = 0; i < newbies.size(); i++)
		{
			User u = newbies.get(i);
			Chatter c = new Chatter(u);
			chatters.add(c);
		}
	}

	public void addChatters(ArrayList<User> users)
	{
		for (int i = 0; i < users.size(); i++)
		{
			User u = users.get(i);
			Chatter c = new Chatter(u);
			chatters.add(c);
		}
	}

	
	public boolean isAlarmOn()
	{
		return isAlarmOn;
	}

	public void setAlarm(boolean isAlarmOn)
	{
		this.isAlarmOn = isAlarmOn;
	}

	public boolean isHost()
	{
		return isHost;
	}

	public void setHost(boolean isHost)
	{
		this.isHost = isHost;
	}
}
