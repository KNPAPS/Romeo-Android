package kr.go.KNPA.Romeo.Member;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

import kr.go.KNPA.Romeo.Util.IndexPath;

public class CellNode {
	private static final int CN_NULL = -777;
	public static final int CN_USER = 1;
	public static final int CN_DEPARTMENT = 2;
	
	public static final int NCHECK = 0;
	public static final int FCHECK = 1;
	public static final int HCHECK = 2;
	
	
	
	private int 				_type 				= 	CN_NULL;
	private String				_idx				=	null;
	//private	IndexPath 			_parentIndexPath 	= 	null;
	private	int 				_status				= 	0;
	private	boolean				_isRoot 			= 	false;
	private	boolean 			_isUnfolded 		= 	false;
	private int					_index				=	CN_NULL;
	private	CellNode 			_parent 			= 	null;
	private	ArrayList<CellNode>	_children 			= 	null;

	private static CellNode				_rootNode			= 	null;
	
	public CellNode() {
		
	}
	
	public static void init(CellNode rootNode) {
		for(int i=0; i<rootNode.children().size(); i++) {
			rootNode.children().get(i).init();
		}
	}
	
	private void init() {
		this._status = CellNode.NCHECK;
		this._isUnfolded = true;
		
		if(this.children() != null) {
			for(int i=0; i<this.children().size(); i++) {
				this.children().get(i).init();
			}
		}
	}
	
	public static CellNode rootNode() {
		if(_rootNode == null)
			_rootNode = new CellNode().isRoot(true).isUnfolded(true).parent((CellNode)null);
		return _rootNode;
	}
	
	public int type() {
		return _type;
	}
	public CellNode type(int type) {
		this._type = type;
		return this;
	}
	
	public String idx() {
		return _idx;
	}
	public CellNode idx(String idx) {
		this._idx = idx;
		return this;
	}
	
	public int status() {
		return _status;
	}
	public CellNode status(int status) {
		this._status = status;
		return this;
	}
	
	public boolean isRoot() {
		return _isRoot;
	}
	public CellNode isRoot(boolean isRoot) {
		this._isRoot = isRoot;
		return this;
	}
	
	public boolean isUnfolded() {
		return _isUnfolded;
	}
	public CellNode isUnfolded(boolean isUnfolded) {
		this._isUnfolded = isUnfolded;
		return this;
	}
	
	public int index() {
		return _index;
	}
	public CellNode index(int index) {
		this._index = index;
		return this;
	}
	
	public IndexPath indexPath() {
		IndexPath path = null;
		if(this.parent() != null)
			path = this.parent().indexPath();
		
		if(path == null) {
			path = IndexPath.indexPathWithIndex(this.index());
		} else {
			path.clone().addIndex(this.index());
		}
		
		return path;
	}
	
	public CellNode parent() {
		return _parent;
	}
	
	public CellNode parent(CellNode parent) {
		this._parent = parent;
		return this;
	}
	
	public IndexPath parentIndexPath() {
		return parent().indexPath();
	}
	
	public ArrayList<CellNode> children() {
		return _children;
	}
	
	public CellNode children(ArrayList<CellNode> children) {
		this._children = children;
		return this;
	}
	
	public CellNode append(CellNode child) {
		if(_children == null) 
			_children = new ArrayList<CellNode>();
		_children.add(child);
		return this;
	}
	
	public CellNode appendTo(CellNode parent) {
		parent.append(this);
		return parent;
	}
	
	public static class Condition {
		private int 				_type 				= 	CN_NULL;
		private String				_idx				=	null;
		private	int 				_status				= 	CN_NULL;
		private	boolean				_isRoot 			= 	false;
		private IndexPath			_indexPath			=	null;
		private	boolean 			_isUnfolded 		= 	false;
		
		private boolean _hasType 		= 	false;
		private boolean	_hasIdx			=	false;
		private boolean _hasStatus 		= 	false;
		private boolean _hasIsRoot 		= 	false;
		private boolean _hasIndexPath 	= 	false;
		private	boolean	_hasIsUnfolded	= 	false;
		
		public Condition() {}
		public Condition indexPath(IndexPath path) {
			this._indexPath = path;
			return this;
		}
		
		public Condition type(int type) {
			this._type = type;
			return this;
		}
		
		public Condition idx(String idx) {
			this._idx = idx;
			return this;
		}
		
		public Condition isRoot(boolean isRoot) {
			this._isRoot = isRoot;
			return this;
		}
		
		public Condition status (int status) {
			this._status = status;
			return this;
		}
		
		public Condition isUnfolded(boolean isUnfolded) {
			this._isUnfolded = isUnfolded;
			return this;
		}
		
		public IndexPath indexPath() {
			return _indexPath;
		}
		
		public int type() {
			return _type;
		}
		
		public String idx()	{
			return _idx;
		}
		
		public int status() {
			return _status;
		}
		
		public boolean isRoot() {
			return _isRoot;
		}
		
		public boolean isUnfolded() {
			return _isUnfolded;
		}
		
		public boolean hasStatus() {
			return _hasStatus;
		}
		public boolean hasType() {
			return _hasType;
		}
		
		public boolean hasIdx() {
			return _hasIdx;
		}
		
		public boolean hasIndexPath() {
			return _hasIndexPath;
		}
		
		public boolean hasIsRoot() {
			return _hasIsRoot;
		}
		
		public boolean hasIsUnfolded() {
			return _hasIsUnfolded;
		}
		
		public void removeType() {
			_hasType = false;
			_type = CN_NULL;
		}
		
		public void removeIdx() {
			_hasIdx = false;
			_idx = null;
		}
		
		public void removeStatus() {
			_hasStatus = false;
			_status = CN_NULL;
		}
		
		public void removeIsRoot() {
			_hasIsRoot = false;
			_isRoot = false;
		}
		
		public void removeIndexPath() {
			_hasIndexPath = false;
			_indexPath = null;
		}
	}


	public static CellNode parent(CellNode node, Condition c) {
		return node.parent(c);
	}
	
	public static ArrayList<CellNode> parents(CellNode node, Condition c ) {
		return node.parents(c);
	}
	
	public static ArrayList<CellNode> children(CellNode node, Condition c) {
		return node.children(c);
	}
	
	public static ArrayList<CellNode> find(CellNode node, Condition c) {
		return node.find(c);
	}
	
	public CellNode parent(Condition c) {
		CellNode p = this.parent();
		while(p != null) {
			if(p.inCondition(c) == true)
				return p;
			p = p.parent();
		}
		
		return null;
	}
	
	public ArrayList<CellNode> parents(Condition c) {
		ArrayList<CellNode> parents = new ArrayList<CellNode>();
		
		CellNode p = this.parent();
		while(p != null) {
			if(p.inCondition(c) == true)
				parents.add(p);
			p = p.parent();
		}
		
		return parents;
	}
	
	public ArrayList<CellNode> children(Condition c) {
		ArrayList<CellNode> children = new ArrayList<CellNode>();
		
		int nChildren = this.children().size();
		for(int i=0; i<nChildren; i++) {
			CellNode child = this.children().get(i); 
			if(child.inCondition(c))
				children.add(child);
		}
		
		return children;
	}

	public ArrayList<CellNode> find(Condition c) {
		ArrayList<CellNode> result = new ArrayList<CellNode>();
		
		if(this.children() != null) {
			for(int i=0; i< this.children().size(); i++) {
				this.children().get(i).find(c, result);
			}
		}
		return result;
	}
	
	private ArrayList<CellNode> find(Condition c, ArrayList<CellNode> array) {
		
		if(this.inCondition(c) == true)
			array.add(this);
		
		if(children() != null) {
			for(int i=0; i < this.children().size(); i++) {
				this.children().get(i).find(c, array);
			}
		}
		return array;
	}
	
	public boolean inCondition(Condition c) {
		boolean inCondition = true;
		
		if(c.hasIndexPath() && this.indexPath().equals(c.indexPath()) == false)
			inCondition = false;
		
		if(c.hasIsRoot() && (this.isRoot() != c.isRoot()) )
			inCondition = false;
		
		if(c.hasIsUnfolded() && (this.isUnfolded() != c.isUnfolded()) )
			inCondition = false;
		
		if(c.hasStatus() && this.status() != c.status())
			inCondition = false;
		
		if(c.hasType() && this.type() != c.type())
				inCondition = false;
		
		return inCondition;
	}
	
	public static CellNode nodeAtIndexPath(CellNode rootNode, IndexPath path) {

		CellNode cn = rootNode;
		IndexPath.Iterator itr = new IndexPath.Iterator(path);
		
		while(itr.hasNextIndex()) {
		}
		return cn;

	}


	public int countIncludeFolded() {
		int result = 0;
		for(int i=0; i< this.children().size(); i++) {
			result += this.children().get(i).countIncludeFolded();
		}
		return (result+1);
	}

	public int count() {
		int result = 0;
		
		if(isUnfolded() == true) {		  	// UnFolded
			for(int i=0; i< this.children().size(); i++) {
				result += this.children().get(i).count();
			}
		} else if(isUnfolded() == false) { 	// Folded
		//	result += 1;
		}
		
		
		if(isRoot() != true) return result+1;
		return result;
		
	}
	

	public void check() {
		if( status() == CellNode.FCHECK) {
			detParentStatusChecked();
			for(int i=0; i< this.children().size(); i++) {
				CellNode child = this.children().get(i);
				child.status(NCHECK);
			}
		} else {
			detParentStatusUnchecked();
			// if(this.children().size >0)
			for(int i=0; i< this.children().size(); i++) {
				CellNode child = this.children().get(i);
				child.status(FCHECK);
			}
		}
	}
	
	private void detParentStatusChecked() {
		boolean flag = false;
		ArrayList<CellNode> sibs = this.parent().children();
		
		for(int i=0; i<sibs.size(); i++) {
			CellNode sib = sibs.get(i);
			if(sib.indexPath().equals(this.indexPath())) continue;
			if(sib.status() != NCHECK) flag = true;
		}
		
		if(flag == true) {
			if(this.isRoot() == true) return;
			Condition c = new Condition().isRoot(false);
			ArrayList<CellNode> _ps = this.parents(c);
			for(int i=0; i<_ps.size(); i++) {
				_ps.get(i).status(HCHECK);
			}
			this.status(NCHECK);
		} else {
			if(this.isRoot() == true) return;
			this.status(NCHECK);
			this.parent().detParentStatusChecked();
		}
	}
	
	private void detParentStatusUnchecked() {
		boolean flag = false;
		ArrayList<CellNode> sibs = this.parent().children();
		
		for(int i=0; i<sibs.size(); i++) {
			CellNode sib = sibs.get(i);
			if(sib.indexPath().equals(this.indexPath())) continue;
			if(sib.status() != FCHECK) flag = true;
		}
		
		if(flag == true) {
			if(this.isRoot() == true) return;
			Condition c = new Condition().isRoot(false);
			ArrayList<CellNode> _ps = this.parents(c);
			for(int i=0; i<_ps.size(); i++) {
				_ps.get(i).status(HCHECK);
			}
			this.status(FCHECK);
		} else {
			if(this.isRoot() == true) return;
			this.parent().detParentStatusUnchecked();
			this.status(FCHECK);
		}
	}
	
	public static ArrayList<String> collect(CellNode rootNode) { 
		HashSet<String> _result = new HashSet<String>();
		
		Condition uc = new Condition().status(FCHECK).type(CN_USER);
		ArrayList<CellNode> selectedUsers = rootNode.find(uc);
		
		for(int i=0; i<selectedUsers.size(); i++) {
			_result.add(selectedUsers.get(i).idx());
		}
		
		Condition dc = new Condition().status(FCHECK).type(CN_DEPARTMENT);
		ArrayList<CellNode> _selectedDeps = rootNode.find(dc);
		ArrayList<CellNode> selectedDeps = new ArrayList<CellNode>();
		
		for(int i=0; i<_selectedDeps.size(); i++) {
			if(_selectedDeps.get(i).children() != null && _selectedDeps.get(i).children().size() >0)
				selectedDeps.add(_selectedDeps.get(i));
		}
	
		
		for( int i=0; i<selectedDeps.size(); i++) {
			ArrayList<User> _us = MemberManager.sharedManager().getDeptMembers(selectedDeps.get(i).idx(), true);
			for(int j=0; j<_us.size(); j++) {
				_result.add(_us.get(j).idx);
			}
		}
		
		ArrayList<String> result = new ArrayList<String>();
		Iterator<String> itr = _result.iterator();
		while(itr.hasNext()) {
			result.add(itr.next());
		}
			
		return result;
	}
	
}