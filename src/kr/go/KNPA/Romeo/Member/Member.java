package kr.go.KNPA.Romeo.Member;

import kr.go.KNPA.Romeo.Config.Constants;

/**
 * 다른 사람 개개인의 정보를 담고 있는 객체
 * @author 최영우
 * @since 2013.4.2
 */
public class Member {
	
	private String memberHash;
	private Department dept = null;
	private String deptHash = null;
	private String deptName = null;
	private String deptFullName = null;
	private String memberName = null;
	private String pic = null;
	private String memberRole = null;
	private int rankIdx = Constants.NOT_SPECIFIED;
	private boolean isSelected = false;
	
	public Member(String hash){ setMemberHash(hash); }

	/**
	 * @name getters and setters
	 * @{
	 */
	public Department getDept() { return dept; }
	public Member setDept(Department dept) { this.dept = dept; return this; }
	public Member setMemberRole(String memberRole) { this.memberRole = memberRole; return this; }
	public String getMemberRole() { return memberRole; }
	public String getPic() { return pic; }
	public Member setPic(String pic) { this.pic = pic; return this;}
	public boolean isSelected() { return isSelected; }
	public Member setSelected(boolean isSelected) { this.isSelected = isSelected; return this;}
	public int getRankIdx() { return rankIdx; }
	public Member setRankIdx(int rankIdx) { this.rankIdx = rankIdx; return this;}
	public String getMemberName() { return memberName; }
	public Member setMemberName(String memberName) { this.memberName = memberName; return this;}
	public String getDeptFullName() { return deptFullName; }
	public Member setDeptFullName(String deptFullName) { this.deptFullName = deptFullName; return this;}
	public String getDeptName() { return deptName; }
	public Member setDeptName(String deptName) { this.deptName = deptName; return this;}
	public String getDeptHash() { return deptHash; }
	public Member setDeptHash(String deptHash) { this.deptHash = deptHash; return this;}
	public String getMemberHash() { return memberHash; }
	public Member setMemberHash(String memberHash) { this.memberHash = memberHash; return this;}
	/** @} */
}
