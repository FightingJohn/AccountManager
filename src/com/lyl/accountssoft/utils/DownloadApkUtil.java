package com.lyl.accountssoft.utils;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.http.Header;

import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

/**
 * @author 木木
 * 从服务器下载最新的apk到sdcard中
 */
public class DownloadApkUtil {
	
	
	public static void download(String downloadPath){
		
		//新键客户端
		AsyncHttpClient client =  new AsyncHttpClient();
		//
		client.get(downloadPath, new MyAsyncHttpResponseHandler());
	}
	
	
}

/*异步请求处理*/
class MyAsyncHttpResponseHandler extends AsyncHttpResponseHandler{

	//请求成功
	public void onSuccess(int statusCode, Header[] headers,
			byte[] responseBody) {
		//把服务器返回的结果字节数组responseBody保存到sdcard中
		
		String sdcardPath = Environment.getExternalStorageDirectory().getAbsolutePath();//sdcard绝对路径
		try {
			//保存的目的地
			FileOutputStream fos = new FileOutputStream(sdcardPath+"/accountMs.apk");
			fos.write(responseBody);
			
			fos.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	//请求失败
	public void onFailure(int statusCode, Header[] headers,
			byte[] responseBody, Throwable error) {
		Log.i("MyAsyncHttpResponseHandler", "下载内容失败");
	}
	
}
