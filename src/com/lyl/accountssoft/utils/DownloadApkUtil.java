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
 * @author ľľ
 * �ӷ������������µ�apk��sdcard��
 */
public class DownloadApkUtil {
	
	
	public static void download(String downloadPath){
		
		//�¼��ͻ���
		AsyncHttpClient client =  new AsyncHttpClient();
		//
		client.get(downloadPath, new MyAsyncHttpResponseHandler());
	}
	
	
}

/*�첽������*/
class MyAsyncHttpResponseHandler extends AsyncHttpResponseHandler{

	//����ɹ�
	public void onSuccess(int statusCode, Header[] headers,
			byte[] responseBody) {
		//�ѷ��������صĽ���ֽ�����responseBody���浽sdcard��
		
		String sdcardPath = Environment.getExternalStorageDirectory().getAbsolutePath();//sdcard����·��
		try {
			//�����Ŀ�ĵ�
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

	//����ʧ��
	public void onFailure(int statusCode, Header[] headers,
			byte[] responseBody, Throwable error) {
		Log.i("MyAsyncHttpResponseHandler", "��������ʧ��");
	}
	
}
