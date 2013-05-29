package kr.go.KNPA.Romeo.search;

import kr.go.KNPA.Romeo.R;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ImageSpan;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.MultiAutoCompleteTextView;
import android.widget.TextView;

public class MemberSearchTextView extends MultiAutoCompleteTextView implements OnItemClickListener {

	private final String	TAG	= MemberSearchTextView.class.getSimpleName();

	// Constructor
	public MemberSearchTextView(Context context)
	{
		super(context);
		init(context);
	}

	// Constructor
	public MemberSearchTextView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		init(context);
	}

	// Constructor
	public MemberSearchTextView(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
		init(context);
	}

	// set listeners for item click and text change
	public void init(Context context)
	{
		setOnItemClickListener(this);
		setTokenizer(new SpaceTokenizer());
		setThreshold(1);

	}

	@Override
	public void onItemClick(AdapterView parent, View view, int position, long id)
	{
		if (getText().toString().contains(" "))
		{
			makeBubbles();
		}
	}

	private void makeBubbles()
	{
		SpannableStringBuilder ssb = new SpannableStringBuilder(getText());
		// split string wich comma
		String chips[] = getText().toString().trim().split(" ");
		int x = 0;
		// loop will generate ImageSpan for every country name separated by
		// comma
		for (String c : chips)
		{
			// inflate chips_edittext layout
			LayoutInflater lf = (LayoutInflater) getContext().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
			TextView textView = (TextView) lf.inflate(R.layout.tv_invitee_bubble, null);

			textView.setText(c); // set text

			// capture bitmapt of genreated textview
			int spec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
			textView.measure(spec, spec);
			textView.layout(0, 0, textView.getMeasuredWidth(), textView.getMeasuredHeight());
			Bitmap b = Bitmap.createBitmap(textView.getWidth(), textView.getHeight(), Bitmap.Config.ARGB_8888);
			Canvas canvas = new Canvas(b);
			canvas.translate(-textView.getScrollX(), -textView.getScrollY());
			textView.draw(canvas);
			textView.setDrawingCacheEnabled(true);
			Bitmap cacheBmp = textView.getDrawingCache();
			Bitmap viewBmp = cacheBmp.copy(Bitmap.Config.ARGB_8888, true);
			textView.destroyDrawingCache(); // destory drawable
			// create bitmap drawable for imagespan
			// create and set imagespan
			ssb.setSpan(new ImageSpan(getContext(), viewBmp), x, x + c.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

			// TODO convert name to userIdx ssb.replace(x, x + c.length(),
			// ":" + c + ":");

			x = x + c.length();
		}
		// set chips span
		setText(ssb);
		// move cursor to last
		setSelection(getText().length());
	}

	public class SpaceTokenizer implements Tokenizer {

		public int findTokenStart(CharSequence text, int cursor)
		{
			int i = cursor;

			while (i > 0 && text.charAt(i - 1) != ' ')
			{
				i--;
			}
			while (i < cursor && text.charAt(i) == ' ')
			{
				i++;
			}

			return i;
		}

		public int findTokenEnd(CharSequence text, int cursor)
		{
			int i = cursor;
			int len = text.length();

			while (i < len)
			{
				if (text.charAt(i) == ' ')
				{
					return i;
				}
				else
				{
					i++;
				}
			}

			return len;
		}

		public CharSequence terminateToken(CharSequence text)
		{
			int i = text.length();

			while (i > 0 && text.charAt(i - 1) == ' ')
			{
				i--;
			}

			if (i > 0 && text.charAt(i - 1) == ' ')
			{
				return text;
			}
			else
			{
				if (text instanceof Spanned)
				{
					SpannableString sp = new SpannableString(text + " ");
					TextUtils.copySpansFrom((Spanned) text, 0, text.length(), Object.class, sp, 0);
					return sp;
				}
				else
				{
					return text + " ";
				}
			}
		}
	}
}
