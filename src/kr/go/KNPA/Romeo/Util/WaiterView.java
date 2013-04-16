package kr.go.KNPA.Romeo.Util;

import java.lang.reflect.Field;

import kr.go.KNPA.Romeo.R;
import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class WaiterView extends ImageView {
	
	private View savedView = null;
	private int savedViewVisibility = -777;
	private static Dialog _waiterDialog;
	
	public WaiterView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setAnimation(context, attrs);
    }

    public WaiterView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setAnimation(context, attrs);
    }

    public WaiterView(Context context) {
        super(context);
    }

    public static void showDialog(Context context) {
    	if(_waiterDialog == null) {
    		_waiterDialog = new Dialog(context, R.style.Theme_Dialog);
    		LayoutInflater inflter = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    		_waiterDialog.setContentView(inflter.inflate(R.layout.waiter_dialog, null));
    	}
		_waiterDialog.show();
    }
    
    public static void dismissDialog(Context context) {
    	if(_waiterDialog == null)
    		return;
    	_waiterDialog.dismiss();
    }
    
    public void setProgress(int percent) {
		TextView progressTV = (TextView)this.findViewById(R.id.progress);
		if(progressTV.getVisibility() != View.VISIBLE) {
			progressTV.setVisibility(View.VISIBLE);
		}
		progressTV.setText(percent+" %");
	}

    private void setAnimation(Context context, AttributeSet attrs) {
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.WaiterView);
        int fps = a.getInt(R.styleable.WaiterView_fps, 50);  
        int duration = a.getInt(R.styleable.WaiterView_duration, 1000);
        String drawableSource = a.getString(R.styleable.WaiterView_src);
        
        if(drawableSource == null) 
        	drawableSource = "waiter_shape";
        
        //setImageResource(getResId(drawableSource, context, Drawable.class));
        setImageDrawable(getAndroidDrawable(drawableSource, context));
        a.recycle();

        setAnimation(fps, duration);
    }

    public static int getResId(String variableName, Context context, Class<?> c) {
    	// http://stackoverflow.com/questions/4427608/android-getting-resource-id-from-string
        try {
            Field idField = c.getDeclaredField(variableName);
            return idField.getInt(idField);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        } 
    }
    
    static public Drawable getAndroidDrawable(String pDrawableName, Context context){
        int resourceId = context.getResources().getIdentifier(pDrawableName, "drawable", context.getPackageName());//"android");
    //Resources.getSystem().getIdent..
        if(resourceId==0){
            return null;
        } else {
            return context.getResources().getDrawable(resourceId);
        }
    }
    
    public void setAnimation(final int fps, final int duration) {
        Animation a = AnimationUtils.loadAnimation(getContext(), R.anim.waiter_anim);
        a.setDuration(duration);
        a.setInterpolator(new Interpolator() {

            @Override
            public float getInterpolation(float input) {
                return (float)Math.floor(input*fps)/fps;
            }
        });
        startAnimation(a);
    }
    
    @Override
    public void setVisibility(int visibility) {
    	if(visibility == View.VISIBLE) {
    		this.setAlpha(234);
    	} else {
    		this.setAlpha(0);
    	}
    	super.setVisibility(visibility);
    }
    
    public void substituteView(View view) {
    	_substituteView(view, (int)(view.getHeight()*1.0));
    }
    
    public void substituteView(View view, int widthInDP) {
    	Resources r = getResources();
    	int px = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, widthInDP, r.getDisplayMetrics());
    	
    	_substituteView(view, px);
    }
    
    private void _substituteView(View view, int widthInPixel) {
    	ViewGroup parentView = (ViewGroup)view.getParent();
    	int index = parentView.indexOfChild(view);
    	
    	savedViewVisibility = view.getVisibility();
    	savedView = view;

    	LayoutParams lp = this.getLayoutParams();
    	if(lp == null) {

        	if(parentView instanceof LinearLayout) {
        		int sideMargin = (int) ((view.getWidth() - widthInPixel)*0.5);
        		LinearLayout.LayoutParams _lp = new LinearLayout.LayoutParams(widthInPixel, widthInPixel);
        		_lp.setMargins(sideMargin, _lp.topMargin, sideMargin, _lp.bottomMargin);
        		lp = _lp;
        	} else if(parentView instanceof RelativeLayout) {
        		/*
        		//RelativeLayout.LayoutParams _lp = new RelativeLayout.LayoutParams(widthInPixel, widthInPixel);
        		RelativeLayout.LayoutParams _lp = (RelativeLayout.LayoutParams)view.getLayoutParams();
        		int sideMargin = (int) ((view.getWidth() - widthInPixel)*0.5);
        		_lp.setMargins(sideMargin, _lp.topMargin, sideMargin, _lp.bottomMargin);
        		lp = _lp;
        		*/
        		RelativeLayout.LayoutParams _lp = new RelativeLayout.LayoutParams(widthInPixel, widthInPixel);
        		RelativeLayout.LayoutParams __lp = (RelativeLayout.LayoutParams)view.getLayoutParams();
        		
        		int sideMargin = (int) ((view.getWidth() - widthInPixel)*0.5);
        		//_lp.alignWithParent = __lp.alignWithParent;
        		//_lp.layoutAnimationParameters = __lp.layoutAnimationParameters;
        		//_lp.setMargins(__lp.leftMargin + sideMargin, __lp.topMargin, __lp.rightMargin + sideMargin, __lp.bottomMargin);
        		lp = __lp;//_lp;
        	} else if(parentView instanceof FrameLayout) {
        		FrameLayout.LayoutParams _lp = new FrameLayout.LayoutParams(widthInPixel, widthInPixel);
        		lp = _lp;
        	} else {
        		lp = new LayoutParams(widthInPixel, widthInPixel);
        	}
    		
    	} else {
    		
	    	lp.height = widthInPixel;
	    	lp.width = widthInPixel;
	    	
	    	if(parentView instanceof LinearLayout) {
        		int sideMargin = (int) ((view.getWidth() - widthInPixel)*0.5);
        		LinearLayout.LayoutParams _lp = (LinearLayout.LayoutParams)lp;
        		_lp.setMargins(sideMargin, _lp.topMargin, sideMargin, _lp.bottomMargin);
        	} else if(parentView instanceof RelativeLayout) {
        	} else if(parentView instanceof FrameLayout) {
        	} else {
        	}
	    	
    	}
    	this.setLayoutParams(lp);
    	this.setVisibility(VISIBLE);
    	this.setAlpha(234);
    	if(this.getAnimation() == null)
    		this.setAnimation(getContext(), null);
    	parentView.addView(this, index);
    	view.setVisibility(GONE);

    }
    
    public void restoreView() {
    	
    	if ( savedView == null ) {
    		return;
    	}
    	
    	ViewGroup parentView = (ViewGroup)savedView.getParent();
    	
    	parentView.removeView(this);
    	this.setAlpha(0);
    	
    	savedView.setVisibility(savedViewVisibility);
    	savedView.invalidate();
    	savedView = null;
    }
}
