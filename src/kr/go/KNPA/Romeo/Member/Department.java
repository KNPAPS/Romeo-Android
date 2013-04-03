package kr.go.KNPA.Romeo.Member;

import java.util.ArrayList;

/**
 * 부서
 */
public class Department {
	
	private String deptHash;
	private String deptName;
	private String deptFullName;
	private String parentHash;
	private Department parentDept;
	private boolean selected = false;
	
	//! 해당 부서 소속원
	private ArrayList<Member> members = null;

	public Department(String deptHash) { setDeptHash(deptHash); }
	
	/**
	 * @name getters and setters
	 * @{
	 */
	public String getDeptHash() { return deptHash; }
	public Department setDeptHash(String deptHash) { this.deptHash = deptHash; return this; } 
	public String getDeptName() { return deptName; }
	public Department setDeptName(String deptName) { this.deptName = deptName; return this; }
	public String getDeptFullName() { return deptFullName; } 
	public Department setDeptFullName(String deptFullName) { this.deptFullName = deptFullName; return this; }
	public String getParentHash() { return parentHash; }
	public Department setParentHash(String parentHash) { this.parentHash = parentHash; return this; }
	public Department getParentDept() { return parentDept; }
	public Department setParentDept(Department parentDept) { this.parentDept = parentDept; return this; }
	public boolean isSelected() { return selected; }
	public Department setSelected(boolean selected) { this.selected = selected; return this; }
	public ArrayList<Member> getMembers() { return members; }
	public Department setMembers(ArrayList<Member> members ) { this.members = members; return this; }
	/**@}*/
	
	/**
	 * 멤버 추가
	 * @param member 멤버
	 * @return
	 */
	public Department addMember(Member member) { this.members.add(member); return this; }
	
}
