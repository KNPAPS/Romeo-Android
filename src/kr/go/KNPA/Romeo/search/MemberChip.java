package kr.go.KNPA.Romeo.search;

import kr.go.KNPA.Romeo.Member.User;
import android.graphics.drawable.Drawable;
import android.text.style.DynamicDrawableSpan;
import android.text.style.ImageSpan;

class MemberChip extends ImageSpan {
	private final CharSequence	mDisplay;

	private final CharSequence	mIdx;

	public MemberChip(Drawable drawable, User user)
	{
		super(drawable, DynamicDrawableSpan.ALIGN_BOTTOM);
		mDisplay = user.name;
		mIdx = user.idx;
	}

	/**
	 * Get the text displayed in the chip.
	 */
	public CharSequence getDisplay()
	{
		return mDisplay;
	}

	/**
	 * Get the text value this chip represents.
	 */
	public CharSequence getIdx()
	{
		return mIdx;
	}

}