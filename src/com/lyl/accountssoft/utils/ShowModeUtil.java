package com.lyl.accountssoft.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * @author 木木
 *	工具类，获得编辑模式的状态
 */
public class ShowModeUtil {
	
	public static boolean getModeResult(Context context){
		
		SharedPreferences sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
		
		if(sp.getBoolean("nightState", false)){
			return true;
		}else{
			return false;
		}
	}

}
