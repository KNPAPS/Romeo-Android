package kr.go.KNPA.Romeo.Util;

import java.lang.reflect.Field;

import kr.go.KNPA.Romeo.R;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.widget.ImageView;

public class RoundSpinnerView extends ImageView {
	public RoundSpinnerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setAnimation(context, attrs);
    }

    public RoundSpinnerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setAnimation(context, attrs);
    }

    public RoundSpinnerView(Context context) {
        super(context);
    }

    private void setAnimation(Context context, AttributeSet attrs) {
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.RoundSpinnerView);
        int fps = a.getInt(R.styleable.RoundSpinnerView_fps, 12);  
        int duration = a.getInt(R.styleable.RoundSpinnerView_duration, 1000);
        String drawableSource = a.getString(R.styleable.RoundSpinnerView_src);
        
        if(drawableSource == null) 
        	drawableSource = "ring_shape";
        
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
        Animation a = AnimationUtils.loadAnimation(getContext(), R.anim.round_spinner_anim);
        a.setDuration(duration);
        a.setInterpolator(new Interpolator() {

            @Override
            public float getInterpolation(float input) {
                return (float)Math.floor(input*fps)/fps;
            }
        });
        startAnimation(a);
    }
}
