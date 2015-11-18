package com.lyl.accountsoft.view;
import com.xiaoke.accountsoft.activity.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;
/**
 * @author 木木
 * 自定义控件
 */
public class SystemItemView extends RelativeLayout {

	private TextView tv_systemItem_title;
	private TextView tv_systemItem_state;
	private CheckBox cb_systemItem_state;
	private String onState;
	private String offState;

	public SystemItemView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public SystemItemView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
		//获得自定义命名空间下属性的值
		String title = attrs.getAttributeValue("http://schemas.android.com/apk/res/com.xiaoke.accountsoft.activity", "title");
		onState = attrs.getAttributeValue("http://schemas.android.com/apk/res/com.xiaoke.accountsoft.activity", "onState");
		offState = attrs.getAttributeValue("http://schemas.android.com/apk/res/com.xiaoke.accountsoft.activity", "offState");
		
		tv_systemItem_title.setText(title);
	}

	public SystemItemView(Context context) {
		super(context);
		init();
	}

	//初始化
	private void init() {
		//这里必须使用this，可以在调用的布局中显示
		View view = View.inflate(getContext(), R.layout.lyl_system_item, this);
		
		tv_systemItem_title = (TextView) view.findViewById(R.id.tv_systemItem_title);
		tv_systemItem_state = (TextView) view.findViewById(R.id.tv_systemItem_state);
		cb_systemItem_state = (CheckBox) findViewById(R.id.cb_systemItem_state);
	}
	
	//返回CheckBox的状态信息
	public boolean cbIsChecked(){
		
		return cb_systemItem_state.isChecked();
	}
	
	//设置状态栏(tv_systemItem_state)的信息
	public void setState(boolean flag){
		cb_systemItem_state.setChecked(flag);
		if(flag){
			tv_systemItem_state.setText(onState);
		}else{
			tv_systemItem_state.setText(offState);
		}
	}
	

}
