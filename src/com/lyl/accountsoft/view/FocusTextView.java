package com.lyl.accountsoft.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewDebug.ExportedProperty;
import android.widget.TextView;
/**
 * @author 木木
 *	主要实验textView的走马灯效果
 */
public class FocusTextView extends TextView {

	public FocusTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	public FocusTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public FocusTextView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	
	//主要实现的方法
	@Override
	@ExportedProperty(category = "focus")
	public boolean isFocused() {
		// TODO Auto-generated method stub
		return true;
	}

}
