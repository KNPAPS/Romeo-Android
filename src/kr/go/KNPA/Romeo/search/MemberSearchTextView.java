package kr.go.KNPA.Romeo.search;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

import kr.go.KNPA.Romeo.R;
import kr.go.KNPA.Romeo.Config.Constants;
import kr.go.KNPA.Romeo.Member.User;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.QwertyKeyListener;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.MultiAutoCompleteTextView;
import android.widget.TextView;

public class MemberSearchTextView extends MultiAutoCompleteTextView implements OnItemClickListener {

	private Tokenizer	mTokenizer;

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
		setDropDownWidth(Constants.DEVICE_WIDTH);
		setDropDownAnchor(R.id.fl_typeahead);
		setOnKeyListener(new OnKeyListener() {

			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event)
			{
				if (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_DEL)
				{
					int selStart = getSelectionStart();
					MemberChip[] repl = getSpannable().getSpans(selStart - 1, selStart, MemberChip.class);
					if (repl.length > 0)
					{
						((MemberSearchTextViewAdapter) getAdapter()).removeExcludeIdxs(repl[0].getIdx().toString());
					}
				}

				return false;
			}
		});
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id)
	{
		submitItemAtPosition(position);
	}

	private void submitItemAtPosition(int position)
	{
		User user = (User) getAdapter().getItem(position);

		if (user == null)
		{
			return;
		}

		clearComposingText();

		int end = getSelectionEnd();
		int start = mTokenizer.findTokenStart(getText(), end);

		Editable editable = getText();
		QwertyKeyListener.markAsReplaced(editable, start, end, "");
		CharSequence chip = createChip(user, false);

		if (chip != null && start >= 0 && end >= 0)
		{
			// TODO chip 사이에 새 칩을 넣을 때tokenizer가
			// start를 end와 같은 위치로 잡음. 그래서 일단 start를 강제로 조절해놓음
			start = end - chip.length();
			editable.replace(start, end, chip);
			((MemberSearchTextViewAdapter) getAdapter()).addExcludeIdxs(user.idx);
		}

		sanitizeBetween();
	}

	private CharSequence createChip(User user, boolean pressed)
	{
		// Always leave a blank space at the end of a chip.
		String displayText = user.name + " ";

		if (TextUtils.isEmpty(displayText))
		{
			return null;
		}

		SpannableString chipText = null;

		int end = getSelectionEnd();
		int start = mTokenizer.findTokenStart(getText(), end);
		int textLength = displayText.length();

		chipText = new SpannableString(displayText);

		MemberChip chip = constructChipSpan(user, start, pressed);
		chipText.setSpan(chip, 0, textLength, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

		return chipText;
	}

	private MemberChip constructChipSpan(User user, int offset, boolean pressed)
	{
		LayoutInflater lf = (LayoutInflater) getContext().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
		View clipView = lf.inflate(R.layout.member_clip, null);

		((TextView) clipView.findViewById(R.id.member_clip)).setText(user.name);

		int spec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
		clipView.measure(spec, spec);
		clipView.layout(0, 0, clipView.getMeasuredWidth(), clipView.getMeasuredHeight());
		Bitmap b = Bitmap.createBitmap(clipView.getWidth(), clipView.getHeight(), Bitmap.Config.ARGB_8888);

		Canvas canvas = new Canvas(b);
		canvas.translate(-clipView.getScrollX(), -clipView.getScrollY());
		clipView.draw(canvas);
		clipView.setDrawingCacheEnabled(true);
		Bitmap cacheBmp = clipView.getDrawingCache();

		Bitmap viewBmp = cacheBmp.copy(Bitmap.Config.ARGB_8888, true);
		clipView.destroyDrawingCache();

		Drawable result = new BitmapDrawable(getResources(), viewBmp);
		result.setBounds(0, 0, viewBmp.getWidth(), viewBmp.getHeight());
		MemberChip chip = new MemberChip(result, user);
		return chip;
	}

	private void sanitizeBetween()
	{
		// Find the last chip.
		MemberChip[] recips = getSortedRecipients();
		if (recips != null && recips.length > 0)
		{
			MemberChip last = recips[recips.length - 1];
			MemberChip beforeLast = null;
			if (recips.length > 1)
			{
				beforeLast = recips[recips.length - 2];
			}
			int startLooking = 0;
			int end = getSpannable().getSpanStart(last);
			if (beforeLast != null)
			{
				startLooking = getSpannable().getSpanEnd(beforeLast);
				Editable text = getText();
				if (startLooking == -1 || startLooking > text.length() - 1)
				{
					// There is nothing after this chip.
					return;
				}
				if (text.charAt(startLooking) == ' ')
				{
					startLooking++;
				}
			}
			if (startLooking >= 0 && end >= 0 && startLooking < end)
			{
				getText().delete(startLooking, end);
			}
		}
	}

	MemberChip[] getSortedRecipients()
	{
		MemberChip[] members = getSpannable().getSpans(0, getText().length(), MemberChip.class);
		ArrayList<MemberChip> memberLists = new ArrayList<MemberChip>(Arrays.asList(members));
		final Spannable spannable = getSpannable();
		Collections.sort(memberLists, new Comparator<MemberChip>() {

			@Override
			public int compare(MemberChip first, MemberChip second)
			{
				int firstStart = spannable.getSpanStart(first);
				int secondStart = spannable.getSpanStart(second);
				if (firstStart < secondStart)
				{
					return -1;
				}
				else if (firstStart > secondStart)
				{
					return 1;
				}
				else
				{
					return 0;
				}
			}
		});
		return memberLists.toArray(new MemberChip[memberLists.size()]);
	}

	public ArrayList<String> getMembersIdx()
	{
		MemberChip[] members = getSpannable().getSpans(0, getText().length(), MemberChip.class);
		ArrayList<String> membersIdx = new ArrayList<String>(members.length);
		for (int i = 0; i < members.length; i++)
		{
			membersIdx.add(members[i].getIdx().toString());
		}
		return membersIdx;
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

	@Override
	public void setTokenizer(Tokenizer t)
	{
		mTokenizer = t;
		super.setTokenizer(t);
	}

	private Spannable getSpannable()
	{
		return getText();
	}

	public void appendMemberClip(User user)
	{
		CharSequence chip = createChip(user, false);
		Editable editable = getText();

		if (chip != null)
		{
			editable.append(chip);
			((MemberSearchTextViewAdapter) getAdapter()).addExcludeIdxs(user.idx);
		}

		setSelection(editable.length());
	}
}
