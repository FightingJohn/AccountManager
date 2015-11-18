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
	
	//服务器地址,不同的ip地址测试程序时要修改ip
	private final String SERVER_PATH = "http://192.168.253.7:8080/server/version.json";
	private String description;		//新版本描述
	private String downloadPath;	//新版本的下载路径
	
	GridView gvInfo;// 创建GridView对象
	// 定义字符串数组，存储系统功能
	String[] titles = new String[] { "新增支出", "新增收入", "我的支出", "我的收入", "数据管理",
			"系统设置", "收支便签", "退出" };
	// 定义int数组，存储功能对应的图标
	int[] images = new int[] { R.drawable.addoutaccount,
			R.drawable.addinaccount, R.drawable.outaccountinfo,
			R.drawable.inaccountinfo, R.drawable.showinfo, R.drawable.sysset,
			R.drawable.accountflag, R.drawable.exit };

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		gvInfo = (GridView) findViewById(R.id.gvInfo);// 获取布局文件中的gvInfo组件
		
		pictureAdapter adapter = new pictureAdapter(titles, images, this);// 创建pictureAdapter对象
		gvInfo.setAdapter(adapter);// 为GridView设置数据源
		gvInfo.setOnItemClickListener(new OnItemClickListener() {// 为GridView设置项单击事件
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				Intent intent = null;// 创建Intent对象
				switch (arg2) {
				case 0:
					intent = new Intent(MainActivity.this, AddOutaccount.class);// 使用AddOutaccount窗口初始化Intent
					startActivity(intent);// 打开AddOutaccount
					break;
				case 1:
					intent = new Intent(MainActivity.this, AddInaccount.class);// 使用AddInaccount窗口初始化Intent
					startActivity(intent);// 打开AddInaccount
					break;
				case 2:
					intent = new Intent(MainActivity.this, Outaccountinfo.class);// 使用Outaccountinfo窗口初始化Intent
					startActivity(intent);// 打开Outaccountinfo
					break;
				case 3:
					intent = new Intent(MainActivity.this, Inaccountinfo.class);// 使用Inaccountinfo窗口初始化Intent
					startActivity(intent);// 打开Inaccountinfo
					break;
				case 4:
					intent = new Intent(MainActivity.this, Showinfo.class);// 使用Showinfo窗口初始化Intent
					startActivity(intent);// 打开Showinfo
					break;
				case 5:
					intent = new Intent(MainActivity.this, Sysset.class);// 使用Sysset窗口初始化Intent
					startActivity(intent);// 打开Sysset
					break;
				case 6:
					intent = new Intent(MainActivity.this, Accountflag.class);// 使用Accountflag窗口初始化Intent
					startActivity(intent);// 打开Accountflag
					break;
				case 7:
					finish();// 关闭当前Activity
				}
			}
		});
		
		SharedPreferences sp = getSharedPreferences("config", MODE_PRIVATE);
		//当软件的自动更新功能打开时，自动检测更新
		if(sp.getBoolean("autoUpdate", false)){
			updateVersion();
		}
	}
	
	
	//设置菜单
	public boolean onCreateOptionsMenu(Menu menu) {
		
		//在菜单中加入条目——检测更新 id 设为100
		menu.add(Menu.NONE, 100, 0, "检测更新");
		
		getMenuInflater().inflate(R.menu.main, menu);
		
		return true;
	}
	
	//菜单处理函数
	public boolean onOptionsItemSelected(MenuItem item) {
		
		if(item.getItemId() == 100){
//			Toast.makeText(this, "检测更新", Toast.LENGTH_SHORT).show();
			//执行更新操作
			updateVersion();
		}
		
		return super.onOptionsItemSelected(item);
	}
	
	//常量，用来表示请求服务器是否有新版本时返回的不同情况，然后做出不同的响应
	private final int MSG_NEWVERSION = 1;		//新版本
	private final int MSG_SAMEVERSION = 2;		//无新版本
	private final int MSG_NETWORK_ERROR = 3;	//网络连接错误
	private final int MSG_URL_ERROR = 4;		//url错误
	private final int MSG_IO_ERROR = 5;			//IO错误
	private final int MSG_JSON_ERROR = 6;		//解析json错误
	
	/*处理子线程发出的信息*/
	Handler handler = new Handler(){
		
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
				case MSG_NEWVERSION:{
					//如果有新版，对话框显示更新版本内容，询问是否更新
					new AlertDialog.Builder(MainActivity.this)
					.setTitle("更新提示")
					.setMessage(description)
					.setPositiveButton("现在更新", new MyOnClikListener())
					.setNegativeButton("以后再说", null)
					.show();
				}
				break;
				
				case MSG_SAMEVERSION:{
					Toast.makeText(MainActivity.this, "已经是最新版本", 1).show();
				}
				break;
				
				case MSG_NETWORK_ERROR:{
					Toast.makeText(MainActivity.this, "服务器连接错误", 0).show();
				}
				break;
				
				case MSG_URL_ERROR:{
					Toast.makeText(MainActivity.this, "URL错误", 0).show();
				}
				break;
				
				case MSG_IO_ERROR:{
					Toast.makeText(MainActivity.this, "IO错误", 0).show();
				}
				break;
				
				case MSG_JSON_ERROR:{
					Toast.makeText(MainActivity.this, "JSON解析错误", 0).show();
				}
				break;
			}
		};
	};

	/*从服务器检测软件是否有新版本*/
	private void updateVersion() {
		
		//由于向服务器发送请求存在阻塞状态，所以要放在子线程中
		new Thread(new Runnable() {
			

			public void run() {
				
				Message msg = handler.obtainMessage();
				try {
					//使用java网络连接
					URL url = new URL(SERVER_PATH);
					HttpURLConnection connection = (HttpURLConnection) url.openConnection();
					
					//使用get请求方式
					connection.setRequestMethod("GET");
					//设置请求的响应的时间
					connection.setReadTimeout(5000);
					connection.setConnectTimeout(5000);
					
					//连接，阻塞状态
					connection.connect();
					//连接成功，解析服务器的json文件，获得最新软件版本信息
					if(connection.getResponseCode() == 200){
						InputStream inputStream = connection.getInputStream();
						//使用工具类，把输入流转化为gbk编码的字符串
						String json = StreamToStrUtil.getString(inputStream);
						
						//解析服务器发来的json文件
						JSONObject jsonObject = new JSONObject(json);
						
						String newVersion = jsonObject.getString("newVersion");		//版本号
						downloadPath = jsonObject.getString("downloadPath");
						description = jsonObject.getString("description");
						
						//获得软件目前的版本
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
	
	/*获得软件目前的版本名称*/
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
	
	/*调用系统的intent安装应用*/
	private void installApk(File file){ 
		
		System.out.println("安装前");
		Intent intent = new Intent();
		intent.setAction(Intent.ACTION_VIEW);
		System.out.println("调用intent后");
		intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
		System.out.println("启动intent");
		startActivity(intent);
		System.out.println("intent启动完成后");
	}
	
	/*处理对话框的“现在更新”按钮的点击事件*/
	class MyOnClikListener implements OnClickListener{
		
		@Override
		public void onClick(DialogInterface dialog, int which) {
			
			DownloadApkUtil.download(downloadPath);
			
			//从sdcard中获得最新版本安装
			File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/accountMs.apk");
			
			//提示是否安装
			installApk(file);
		}
		
	}
}



class pictureAdapter extends BaseAdapter// 创建基于BaseAdapter的子类
{
	private LayoutInflater inflater;// 创建LayoutInflater对象
	private List<Picture> pictures;// 创建List泛型集合

	// 为类创建构造函数
	public pictureAdapter(String[] titles, int[] images, Context context) {
		super();
		pictures = new ArrayList<Picture>();// 初始化泛型集合对象
		inflater = LayoutInflater.from(context);// 初始化LayoutInflater对象
		for (int i = 0; i < images.length; i++)// 遍历图像数组
		{
			Picture picture = new Picture(titles[i], images[i]);// 使用标题和图像生成Picture对象
			pictures.add(picture);// 将Picture对象添加到泛型集合中
		}
	}

	@Override
	public int getCount() {// 获取泛型集合的长度
		if (null != pictures) {// 如果泛型集合不为空
			return pictures.size();// 返回泛型长度
		} else {
			return 0;// 返回0
		}
	}

	@Override
	public Object getItem(int arg0) {
		return pictures.get(arg0);// 获取泛型集合指定索引处的项
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;// 返回泛型集合的索引
	}

	@Override
	public View getView(int arg0, View arg1, ViewGroup arg2) {
		ViewHolder viewHolder;// 创建ViewHolder对象
		if (arg1 == null)// 判断图像标识是否为空
		{
			arg1 = inflater.inflate(R.layout.gvitem, null);// 设置图像标识
			viewHolder = new ViewHolder();// 初始化ViewHolder对象
			viewHolder.title = (TextView) arg1.findViewById(R.id.ItemTitle);// 设置图像标题
			viewHolder.image = (ImageView) arg1.findViewById(R.id.ItemImage);// 设置图像的二进制值
			arg1.setTag(viewHolder);// 设置提示
		} else {
			viewHolder = (ViewHolder) arg1.getTag();// 设置提示
		}
		viewHolder.title.setText(pictures.get(arg0).getTitle());// 设置图像标题
		viewHolder.image.setImageResource(pictures.get(arg0).getImageId());// 设置图像的二进制值
		return arg1;// 返回图像标识
	}
}

/*此类的主要目的是为了回收资源，可以防止GridView图标过多时造成的内存溢出*/
class ViewHolder// 创建ViewHolder类
{
	public TextView title;// 创建TextView对象
	public ImageView image;// 创建ImageView对象
}

class Picture// 创建Picture类
{
	private String title;// 定义字符串，表示图像标题
	private int imageId;// 定义int变量，表示图像的二进制值

	public Picture()// 默认构造函数
	{
		super();
	}

	public Picture(String title, int imageId)// 定义有参构造函数
	{
		super();
		this.title = title;// 为图像标题赋值
		this.imageId = imageId;// 为图像的二进制值赋值
	}

	public String getTitle() {// 定义图像标题的可读属性
		return title;
	}

	public void setTitle(String title) {// 定义图像标题的可写属性
		this.title = title;
	}

	public int getImageId() {// 定义图像二进制值的可读属性
		return imageId;
	}

	public void setimageId(int imageId) {// 定义图像二进制值的可写属性
		this.imageId = imageId;
	}
}