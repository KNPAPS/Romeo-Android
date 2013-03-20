package kr.go.KNPA.Romeo.Util;

public class IndexPath {

	public final static int MAX_LENGTH = 6;
	private int[] p = new int[MAX_LENGTH]; 
	private final static int INDEXPATH_NULL = -777;
	public final static String INDEXPATH_TOKEN = ":";
	public final static int INDEXPATH_LONG_TOKEN = 100;
	private IndexPath initPath() {
		for(int i=0; i<MAX_LENGTH; i++) {
			p[i] = INDEXPATH_NULL;
		}
		return this;
	}
	
	public IndexPath() {
		initPath();
	}
	
	public IndexPath clone() {
		int[] indexes = this.getIndexes(null);
		IndexPath ip = indexPathWithIndexesAndLength(indexes, indexes.length);
		return ip;
	}
	
	public static IndexPath indexPathWithIndex(int index) {
		IndexPath path = new IndexPath();
		path.p[0] = index;
		return path;
	}
	
	public static IndexPath indexPathWithIndexesAndLength(int[] indexes, int length) {
		IndexPath path = new IndexPath();
		
		int _len = Math.min(indexes.length, length);
		
		for(int i=0; i<_len; i++) {
			path.p[i] = indexes[i];
		}
		return path;
	}
	
	public String toString() {
		return indexPathToString();
	}
	
	public String indexPathToString() {
		StringBuffer sb = new StringBuffer();
		int _len = this.length();
		for(int i=0; i<_len; i++) {
			sb.append(this.p[i]);
			if(i != _len-1) sb.append(INDEXPATH_TOKEN);
		}
		
		return sb.toString();
	}
	
	public long indexPathToLong() {
		int _len = this.length();
		long result = 0;
		for (int i=0; i<_len; i++) {
			result = result * INDEXPATH_LONG_TOKEN + this.p[i];
		}
		return result;
	}
	
	public static IndexPath indexPathWithString(String iString) {
		
		
		IndexPath path = null; 
		if( iString == null || iString.trim().length() < 1) {
			path = IndexPath.indexPathWithIndexesAndLength(new int[0], 0);
		} else {
			String[] _paths = iString.split("[^0-9]");
			int[] _intPath = new int[_paths.length];
			for(int i=0; i< _paths.length; i++) {
				if(_paths[i].length() ==0 ) continue;
				_intPath[i] = Integer.parseInt(_paths[i]);
			}
			path = IndexPath.indexPathWithIndexesAndLength(_intPath, _intPath.length);
		}
		return path;
	}
	
	public int[] getIndexes(int[] indexes) {
		int _len = this.length();//Math.min(p.length(), MAX_LENGTH);
		int[] _indexes = new int[_len];
		
		for(int i=0; i<_len; i++) {
			_indexes[i] = p[i];
		}
		return _indexes;
	}
	
	public IndexPath indexPathByAddingIndex(int index) {
		IndexPath ip = this.clone();
		int _len = ip.length();
		ip.p[_len] = index;
		
		return ip;
	}
	
	public IndexPath addIndex(int index) {
		int _len = this.length();
		this.p[_len] = index;
		
		return this;
	}
	
	public IndexPath removeLastIndex() {
		int _len = Math.max(this.length(), 1);
		this.p[_len-1] = INDEXPATH_NULL;
		
		return this;
	}
	
	public IndexPath indexPathByRemovingLastIndex() {
		IndexPath ip = this.clone();
		int _len = Math.max(ip.length(), 1);
		ip.p[_len-1] = INDEXPATH_NULL;
		
		return ip;
	}
	
	public int lengthWithIndexes(int[] indexes) {
		int length = -1;
		
		for(int i=0; i<MAX_LENGTH; i++) {
			if(indexes[i] != INDEXPATH_NULL) {
				length = i+1;
			}
		}
		return length;
	}
	
	public int length() {
		int length = -1;
		
		for(int i=0; i<MAX_LENGTH; i++) {
			if(this.p[i] != INDEXPATH_NULL) {
				length = i+1;
			}
		}
		
		if(this.p[0] == INDEXPATH_NULL) {
			length = 0;
		}
		return length;
	}
	
	public int section() {
		int section = INDEXPATH_NULL;
		if(this.p[1] != INDEXPATH_NULL) {
			section = this.p[0];
		}
		
		return section;
	}
	
	public int row() {
		int row = INDEXPATH_NULL;
		if(this.p[1]!=INDEXPATH_NULL){
			row = this.p[1];
		} else {
			row = this.p[0];
		}
		return row;
	}
	
	public static class Iterator {
		private int location = -1;
		private IndexPath path = null;
		
		public Iterator(IndexPath path) {
			this.path = path;
		}
		
		public boolean hasNextIndex() {
			boolean result = true;
			int[] paths = path.getIndexes(null);
			
			if(nextLocation() >= paths.length || nextLocation() >= MAX_LENGTH) {
				result = false;
			}
			
			return result;
		}
		
		private int nextLocation() {
			return (location+1);
		}
		
		private int previousLocation() {
			return (location-1);
		}
		
		private int firstLocation() {
			return 0;
		}
		
		private int lastLocation() {
			return (path.length()-1);
		}
		
		public void next() {
			int nextLocation = nextLocation();
			location = nextLocation;
		}
		
		public void previous() {
			int previousLocation = previousLocation();
			location = previousLocation;
		}
		
		public void first() {
			location = firstLocation();
		}
		
		public void last() {
			location = lastLocation();
		}
		
		public int index() {
			int[] paths = path.getIndexes(null);
			return paths[location];
		}
		
		public int nextIndex() {
			next();
			return index(); 
		}
		
		public int previousIndex() {
			previous();
			return index();
		}
		
		public int firstIndex() {
			first();
			return index();
		}
		
		public int lastIndex() {
			last();
			return index();
		}
		
		public int goIndex() {
			int index = index();
			next();
			return index;
		}
		
		public int[] getIndexesUntilNow() {
			int[] paths = path.getIndexes(null);
			int[] result = new int[(location+1)];
			for(int i=0; i<paths.length; i++){
				result[i] = paths[i];
			}
			return result;
		}
	}
}
