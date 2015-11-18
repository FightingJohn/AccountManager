package com.xiaoke.accountsoft.activity;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.lyl.accountssoft.utils.DownloadApkUtil;
import com.lyl.accountssoft.utils.StreamToStrUtil;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	
	//��������ַ,��ͬ��ip��ַ���Գ���ʱҪ�޸�ip
	private final String SERVER_PATH = "http://192.168.253.7:8080/server/version.json";
	private String description;		//�°汾����
	private String downloadPath;	//�°汾������·��
	
	GridView gvInfo;// ����GridView����
	// �����ַ������飬�洢ϵͳ����
	String[] titles = new String[] { "����֧��", "��������", "�ҵ�֧��", "�ҵ�����", "���ݹ���",
			"ϵͳ����", "��֧��ǩ", "�˳�" };
	// ����int���飬�洢���ܶ�Ӧ��ͼ��
	int[] images = new int[] { R.drawable.addoutaccount,
			R.drawable.addinaccount, R.drawable.outaccountinfo,
			R.drawable.inaccountinfo, R.drawable.showinfo, R.drawable.sysset,
			R.drawable.accountflag, R.drawable.exit };

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		gvInfo = (GridView) findViewById(R.id.gvInfo);// ��ȡ�����ļ��е�gvInfo���
		
		pictureAdapter adapter = new pictureAdapter(titles, images, this);// ����pictureAdapter����
		gvInfo.setAdapter(adapter);// ΪGridView��������Դ
		gvInfo.setOnItemClickListener(new OnItemClickListener() {// ΪGridView��������¼�
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				Intent intent = null;// ����Intent����
				switch (arg2) {
				case 0:
					intent = new Intent(MainActivity.this, AddOutaccount.class);// ʹ��AddOutaccount���ڳ�ʼ��Intent
					startActivity(intent);// ��AddOutaccount
					break;
				case 1:
					intent = new Intent(MainActivity.this, AddInaccount.class);// ʹ��AddInaccount���ڳ�ʼ��Intent
					startActivity(intent);// ��AddInaccount
					break;
				case 2:
					intent = new Intent(MainActivity.this, Outaccountinfo.class);// ʹ��Outaccountinfo���ڳ�ʼ��Intent
					startActivity(intent);// ��Outaccountinfo
					break;
				case 3:
					intent = new Intent(MainActivity.this, Inaccountinfo.class);// ʹ��Inaccountinfo���ڳ�ʼ��Intent
					startActivity(intent);// ��Inaccountinfo
					break;
				case 4:
					intent = new Intent(MainActivity.this, Showinfo.class);// ʹ��Showinfo���ڳ�ʼ��Intent
					startActivity(intent);// ��Showinfo
					break;
				case 5:
					intent = new Intent(MainActivity.this, Sysset.class);// ʹ��Sysset���ڳ�ʼ��Intent
					startActivity(intent);// ��Sysset
					break;
				case 6:
					intent = new Intent(MainActivity.this, Accountflag.class);// ʹ��Accountflag���ڳ�ʼ��Intent
					startActivity(intent);// ��Accountflag
					break;
				case 7:
					finish();// �رյ�ǰActivity
				}
			}
		});
		
		SharedPreferences sp = getSharedPreferences("config", MODE_PRIVATE);
		//��������Զ����¹��ܴ�ʱ���Զ�������
		if(sp.getBoolean("autoUpdate", false)){
			updateVersion();
		}
	}
	
	
	//���ò˵�
	public boolean onCreateOptionsMenu(Menu menu) {
		
		//�ڲ˵��м�����Ŀ���������� id ��Ϊ100
		menu.add(Menu.NONE, 100, 0, "������");
		
		getMenuInflater().inflate(R.menu.main, menu);
		
		return true;
	}
	
	//�˵�������
	public boolean onOptionsItemSelected(MenuItem item) {
		
		if(item.getItemId() == 100){
//			Toast.makeText(this, "������", Toast.LENGTH_SHORT).show();
			//ִ�и��²���
			updateVersion();
		}
		
		return super.onOptionsItemSelected(item);
	}
	
	//������������ʾ����������Ƿ����°汾ʱ���صĲ�ͬ�����Ȼ��������ͬ����Ӧ
	private final int MSG_NEWVERSION = 1;		//�°汾
	private final int MSG_SAMEVERSION = 2;		//���°汾
	private final int MSG_NETWORK_ERROR = 3;	//�������Ӵ���
	private final int MSG_URL_ERROR = 4;		//url����
	private final int MSG_IO_ERROR = 5;			//IO����
	private final int MSG_JSON_ERROR = 6;		//����json����
	
	/*�������̷߳�������Ϣ*/
	Handler handler = new Handler(){
		
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
				case MSG_NEWVERSION:{
					//������°棬�Ի�����ʾ���°汾���ݣ�ѯ���Ƿ����
					new AlertDialog.Builder(MainActivity.this)
					.setTitle("������ʾ")
					.setMessage(description)
					.setPositiveButton("���ڸ���", new MyOnClikListener())
					.setNegativeButton("�Ժ���˵", null)
					.show();
				}
				break;
				
				case MSG_SAMEVERSION:{
					Toast.makeText(MainActivity.this, "�Ѿ������°汾", 1).show();
				}
				break;
				
				case MSG_NETWORK_ERROR:{
					Toast.makeText(MainActivity.this, "���������Ӵ���", 0).show();
				}
				break;
				
				case MSG_URL_ERROR:{
					Toast.makeText(MainActivity.this, "URL����", 0).show();
				}
				break;
				
				case MSG_IO_ERROR:{
					Toast.makeText(MainActivity.this, "IO����", 0).show();
				}
				break;
				
				case MSG_JSON_ERROR:{
					Toast.makeText(MainActivity.this, "JSON��������", 0).show();
				}
				break;
			}
		};
	};

	/*�ӷ������������Ƿ����°汾*/
	private void updateVersion() {
		
		//��������������������������״̬������Ҫ�������߳���
		new Thread(new Runnable() {
			

			public void run() {
				
				Message msg = handler.obtainMessage();
				try {
					//ʹ��java��������
					URL url = new URL(SERVER_PATH);
					HttpURLConnection connection = (HttpURLConnection) url.openConnection();
					
					//ʹ��get����ʽ
					connection.setRequestMethod("GET");
					//�����������Ӧ��ʱ��
					connection.setReadTimeout(5000);
					connection.setConnectTimeout(5000);
					
					//���ӣ�����״̬
					connection.connect();
					//���ӳɹ���������������json�ļ��������������汾��Ϣ
					if(connection.getResponseCode() == 200){
						InputStream inputStream = connection.getInputStream();
						//ʹ�ù����࣬��������ת��Ϊgbk������ַ���
						String json = StreamToStrUtil.getString(inputStream);
						
						//����������������json�ļ�
						JSONObject jsonObject = new JSONObject(json);
						
						String newVersion = jsonObject.getString("newVersion");		//�汾��
						downloadPath = jsonObject.getString("downloadPath");
						description = jsonObject.getString("description");
						
						//������Ŀǰ�İ汾
						String nowVersion = getVerisonName();
						if(nowVersion.equals(newVersion)){
							msg.what = MSG_SAMEVERSION;
						}else{
							msg.what = MSG_NEWVERSION;
						}
					}else{
						msg.what = MSG_NETWORK_ERROR;
					}
					
				} catch (MalformedURLException e) {
					msg.what = MSG_URL_ERROR;
					e.printStackTrace();
				} catch (IOException e) {
					msg.what = MSG_IO_ERROR;
					e.printStackTrace();
				} catch (JSONException e) {
					msg.what = MSG_JSON_ERROR;
					e.printStackTrace();
				}finally{
					handler.sendMessage(msg);
				}
			}

		}).start();
		
	}
	
	/*������Ŀǰ�İ汾����*/
	private String getVerisonName() {
		
		String name = "";
		try {
			PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), 0);
			name = info.versionName;
		} catch (NameNotFoundException e) {
			
			e.printStackTrace();
		}
		return name;
	}
	
	/*����ϵͳ��intent��װӦ��*/
	private void installApk(File file){ 
		
		System.out.println("��װǰ");
		Intent intent = new Intent();
		intent.setAction(Intent.ACTION_VIEW);
		System.out.println("����intent��");
		intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
		System.out.println("����intent");
		startActivity(intent);
		System.out.println("intent������ɺ�");
	}
	
	/*����Ի���ġ����ڸ��¡���ť�ĵ���¼�*/
	class MyOnClikListener implements OnClickListener{
		
		@Override
		public void onClick(DialogInterface dialog, int which) {
			
			DownloadApkUtil.download(downloadPath);
			
			//��sdcard�л�����°汾��װ
			File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/accountMs.apk");
			
			//��ʾ�Ƿ�װ
			installApk(file);
		}
		
	}
}



class pictureAdapter extends BaseAdapter// ��������BaseAdapter������
{
	private LayoutInflater inflater;// ����LayoutInflater����
	private List<Picture> pictures;// ����List���ͼ���

	// Ϊ�ഴ�����캯��
	public pictureAdapter(String[] titles, int[] images, Context context) {
		super();
		pictures = new ArrayList<Picture>();// ��ʼ�����ͼ��϶���
		inflater = LayoutInflater.from(context);// ��ʼ��LayoutInflater����
		for (int i = 0; i < images.length; i++)// ����ͼ������
		{
			Picture picture = new Picture(titles[i], images[i]);// ʹ�ñ����ͼ������Picture����
			pictures.add(picture);// ��Picture������ӵ����ͼ�����
		}
	}

	@Override
	public int getCount() {// ��ȡ���ͼ��ϵĳ���
		if (null != pictures) {// ������ͼ��ϲ�Ϊ��
			return pictures.size();// ���ط��ͳ���
		} else {
			return 0;// ����0
		}
	}

	@Override
	public Object getItem(int arg0) {
		return pictures.get(arg0);// ��ȡ���ͼ���ָ������������
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;// ���ط��ͼ��ϵ�����
	}

	@Override
	public View getView(int arg0, View arg1, ViewGroup arg2) {
		ViewHolder viewHolder;// ����ViewHolder����
		if (arg1 == null)// �ж�ͼ���ʶ�Ƿ�Ϊ��
		{
			arg1 = inflater.inflate(R.layout.gvitem, null);// ����ͼ���ʶ
			viewHolder = new ViewHolder();// ��ʼ��ViewHolder����
			viewHolder.title = (TextView) arg1.findViewById(R.id.ItemTitle);// ����ͼ�����
			viewHolder.image = (ImageView) arg1.findViewById(R.id.ItemImage);// ����ͼ��Ķ�����ֵ
			arg1.setTag(viewHolder);// ������ʾ
		} else {
			viewHolder = (ViewHolder) arg1.getTag();// ������ʾ
		}
		viewHolder.title.setText(pictures.get(arg0).getTitle());// ����ͼ�����
		viewHolder.image.setImageResource(pictures.get(arg0).getImageId());// ����ͼ��Ķ�����ֵ
		return arg1;// ����ͼ���ʶ
	}
}

/*�������ҪĿ����Ϊ�˻�����Դ�����Է�ֹGridViewͼ�����ʱ��ɵ��ڴ����*/
class ViewHolder// ����ViewHolder��
{
	public TextView title;// ����TextView����
	public ImageView image;// ����ImageView����
}

class Picture// ����Picture��
{
	private String title;// �����ַ�������ʾͼ�����
	private int imageId;// ����int��������ʾͼ��Ķ�����ֵ

	public Picture()// Ĭ�Ϲ��캯��
	{
		super();
	}

	public Picture(String title, int imageId)// �����вι��캯��
	{
		super();
		this.title = title;// Ϊͼ����⸳ֵ
		this.imageId = imageId;// Ϊͼ��Ķ�����ֵ��ֵ
	}

	public String getTitle() {// ����ͼ�����Ŀɶ�����
		return title;
	}

	public void setTitle(String title) {// ����ͼ�����Ŀ�д����
		this.title = title;
	}

	public int getImageId() {// ����ͼ�������ֵ�Ŀɶ�����
		return imageId;
	}

	public void setimageId(int imageId) {// ����ͼ�������ֵ�Ŀ�д����
		this.imageId = imageId;
	}
}