/**
 * 
 */
package kr.go.KNPA.Romeo.Chat;

import kr.go.KNPA.Romeo.R;
import kr.go.KNPA.Romeo.Util.DBManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * @author pong0923
 *
 */
public class CommandFragment extends Fragment {

	/**
	 * 
	 */
	private DBManager dbManager;
	
	public CommandFragment() {
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		
		dbManager = new DBManager(getActivity());
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		return inflater.inflate(R.layout.command_fragment, container, false);
	}

}