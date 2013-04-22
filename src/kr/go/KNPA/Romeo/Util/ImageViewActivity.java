package kr.go.KNPA.Romeo.Util;
import kr.go.KNPA.Romeo.R;
import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class ImageViewActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.activity_image_view, null);
		setContentView(view);
		
		ImageView imageView = (ImageView) view.findViewById(R.id.fullImageView);
		int imageType = getIntent().getExtras().getInt("imageType");
		String imageHash = getIntent().getExtras().getString("imageHash");
		
		ImageManager im = new ImageManager();
		im.loadToImageView(imageType, imageHash, imageView, true);
		Button lbb = (Button)view.findViewById(R.id.left_bar_button);
		Button rbb = (Button)view.findViewById(R.id.right_bar_button);
		
		rbb.setVisibility(View.INVISIBLE);
		lbb.setText("취소");
		
		TextView titleView = (TextView)view.findViewById(R.id.title);
		titleView.setText("");
		
		lbb.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}
}
