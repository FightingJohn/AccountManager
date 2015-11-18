package com.xiaoke.accountsoft.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.lyl.accountsoft.view.SystemItemView;

public class Sysset extends Activity implements OnClickListener{

	private SystemItemView siv_settings_item1;	//ģʽ�趨����
	private SystemItemView siv_settings_item2;	//�Զ������趨����
	private SharedPreferences sp;				//���ڱ�������ҹ��ģʽ��״̬
	private Editor edit;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		setContentView(R.layout.lyl_system_settings);
		sp = getSharedPreferences("config", MODE_PRIVATE);
		
		siv_settings_item1 = (SystemItemView) findViewById(R.id.siv_settings_item1);
		siv_settings_item2 = (SystemItemView) findViewById(R.id.siv_settings_item2);
		TextView tv_settings_item3 = (TextView) findViewById(R.id.tv_settings_item3);
		
		edit = sp.edit();
		edit.commit();			//��һ�δ���config�ļ�ʱ��Ҫ
		//��ʼ��ģʽ�趨����״̬
		if(sp.getBoolean("nightState", false)){
			siv_settings_item1.setState(true);
		}else{
			siv_settings_item1.setState(false);
		}
		
		//��ʼ���Զ������趨����״̬
		if(sp.getBoolean("autoUpdate", false)){
			siv_settings_item2.setState(true);
		}else{
			siv_settings_item2.setState(false);
		}
		//��ӵ���¼�
		siv_settings_item1.setOnClickListener(this);
		siv_settings_item2.setOnClickListener(this);
		
		tv_settings_item3.setOnClickListener(this);
		
	}
	

	/*����������*/
	public void onClick(View v) {
		switch (v.getId()) {
			//����1������ҹ��ģʽ
			case R.id.siv_settings_item1:{
				
				if(siv_settings_item1.cbIsChecked()){
					siv_settings_item1.setState(false);
					edit.putBoolean("nightState", false);
					edit.commit();
					
				}else{
					siv_settings_item1.setState(true);
					edit.putBoolean("nightState", true);
					edit.commit();
				}
			}
			break;
			
			//����2�������Զ�����
			case R.id.siv_settings_item2:{
				
				if(siv_settings_item2.cbIsChecked()){
					siv_settings_item2.setState(false);
					edit.putBoolean("autoUpdate", false);
					edit.commit();
					
				}else{
					siv_settings_item2.setState(true);
					edit.putBoolean("autoUpdate", true);
					edit.commit();
				}
			}
			break;
			
			//����3���޸�����
			case R.id.tv_settings_item3:{
				Intent intent = new Intent(this, ChagePwdActivity.class);
				startActivity(intent);
			}
			break;

		default:
			break;
		}
		
	}
}
