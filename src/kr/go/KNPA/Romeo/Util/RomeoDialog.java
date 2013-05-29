package kr.go.KNPA.Romeo.Util;

import kr.go.KNPA.Romeo.R;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class RomeoDialog extends Dialog {

	protected View view; 
	
	protected RomeoDialog(Context context) {
		this(context, R.style.RomeoDialogTheme);
	}
	
	protected RomeoDialog(Context context, int theme) {
		super(context, theme);
		//super(context, android.R.style.Theme_Translucent_NoTitleBar);
		this.view = ((LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.romeo_dialog, null);
		this.setContentView(this.view);
	}
	
	
	static public class Builder {
		private Context context;
		private RomeoDialog dialog;
		
		public Builder(Context context) {
			this.context = context;
			this.dialog = new RomeoDialog(context);
		}
		
		public Builder(Context context, boolean doBackgroundDim) {
			this.context = context;
			if(doBackgroundDim) {
				this.dialog = new RomeoDialog(context, R.style.RomeoDialogTheme);
			} else{ 
				this.dialog = new RomeoDialog(context, R.style.RomeoDialogTheme_NoDim);
			}
			
		}
		
		public Context		getContext() 			{					return context;	}
		public RomeoDialog	create() 				{					return dialog;	}
		public RomeoDialog	show() 					{	dialog.show();	return dialog;	}
		
		private	ImageView	getIconView() 		{	return ((ImageView)dialog.view.findViewById(R.id.icon));	}
		private	TextView	getTitleView()		{	return ((TextView)dialog.view.findViewById(R.id.title));	}
		private	TextView	getMessageView()	{	return ((TextView)dialog.view.findViewById(R.id.message));	}
		private	ListView	getListView()		{	return ((ListView)dialog.view.findViewById(R.id.list));		}
		
		public Builder	setTitle(int titleId) 				{	getTitleView().setText(titleId);		return this;	}
		public Builder	setTitle(CharSequence title)		{	getTitleView().setText(title);			return this;	}
		public Builder	setIcon(int iconId) 				{	getIconView().setImageResource(iconId);	return this;	}
		public Builder	setIcon(Drawable icon) 				{	getIconView().setImageDrawable(icon);	return this;	}
		
		public Builder	setMesssage(int messageId) 	{	
			getMessageView().setText(messageId);
			getMessageView().setVisibility(View.VISIBLE);
			getListView().setVisibility(View.GONE);
			return this;	
		}
		public Builder	setMessage(CharSequence message) {	
			getMessageView().setText(message);
			getMessageView().setVisibility(View.VISIBLE);
			getListView().setVisibility(View.GONE);
			return this;	
		}
		
		public Builder setAdapter(ListAdapter adapter, final DialogInterface.OnClickListener listener) {
			ListView lv = getListView();
			lv.setAdapter(adapter);
			OnItemClickListener itemClickListener = new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> listView, View cell,
						int position, long l_position) {
					listener.onClick(dialog, position);
					
				}
			};
			lv.setOnItemClickListener(itemClickListener);
			
			if(getMessageView().getVisibility() != View.VISIBLE)
				getListView().setVisibility(View.VISIBLE);
			return this;
		}
		
		public Builder setPositiveButton (int textId, final DialogInterface.OnClickListener listener) {
			return setPositiveButton(context.getResources().getString(textId), listener);
		}
		
		public Builder setPositiveButton (CharSequence text, final DialogInterface.OnClickListener listener ) {
			Button positiveBT = (Button)dialog.view.findViewById(R.id.positive);
			positiveBT.setText(text);
			positiveBT.setVisibility(View.VISIBLE);
			positiveBT.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					listener.onClick(dialog, BUTTON_POSITIVE);
				}
			});
			dialog.view.findViewById(R.id.buttonWrapper).setVisibility(View.VISIBLE);
			return this;
		}
		
		public Builder setNegativeButton(int textId, final DialogInterface.OnClickListener listener) {
			return setNegativeButton(context.getResources().getString(textId), listener);
		}
		
		public Builder setNegativeButton(CharSequence text, final DialogInterface.OnClickListener listener) {
			Button negativeBT = (Button)dialog.view.findViewById(R.id.negative);
			negativeBT.setText(text);
			negativeBT.setVisibility(View.VISIBLE);
			negativeBT.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					listener.onClick(dialog, BUTTON_NEGATIVE);
				}
			});
			dialog.view.findViewById(R.id.buttonWrapper).setVisibility(View.VISIBLE);
			return this;
		}
					
		public Builder setCancelable(boolean cancelable) {
			Button cancelBT = (Button)dialog.view.findViewById(R.id.cancel);
			cancelBT.setText("취소");
			cancelBT.setVisibility(View.VISIBLE);
			cancelBT.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					dialog.dismiss();
				}
			});
			dialog.view.findViewById(R.id.buttonWrapper).setVisibility(View.VISIBLE);
			return this;
		}
		
		
	}
}
