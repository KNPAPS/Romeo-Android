package kr.go.KNPA.Romeo.Chat;

import kr.go.KNPA.Romeo.Member.User;

public class Chatter extends User {
	public Long	lastReadTS	= 0L;
	public Long	enteredTS	= 0L;

	public Chatter()
	{

	}

	public Chatter(User user)
	{
		this.idx = user.idx;
		this.name = user.name;
		this.department = user.department;
		this.rank = user.rank;
		this.role = user.role;
	}
}
