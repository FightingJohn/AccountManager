package com.lyl.accountssoft.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author ľľ
 * ��������ת��Ϊ�ַ���
 */
public class StreamToStrUtil {

	public static String getString(InputStream in){
		
		String result = "";			//���ؽ��
		
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		int len = 0;
		byte[] buffer = new byte[1024];
		try {
			while((len=in.read(buffer, 0, 1024)) != -1){
				
				bos.write(buffer, 0, len);
				result = bos.toString("GBK");
				bos.close();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return result;
	}
}
