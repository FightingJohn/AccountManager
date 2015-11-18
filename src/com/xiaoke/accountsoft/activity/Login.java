package com.xiaoke.accountsoft.activity;

import com.xiaoke.accountsoft.dao.PwdDAO;
import com.xiaoke.accountsoft.model.Tb_pwd;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Login extends Activity {
	EditText txtlogin;// ����EditText����
	Button btnlogin, btnclose;// ��������Button����
	private PwdDAO pwdDAO;
	private AlertDialog setPasswordDialog;
	private EditText et_setPassword_first;
	private EditText et_setPassword_second;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);// ���ò����ļ�
		pwdDAO = new PwdDAO(Login.this);

		txtlogin = (EditText) findViewById(R.id.txtLogin);// ��ȡ�����ı���
		btnlogin = (Button) findViewById(R.id.btnLogin);// ��ȡ��¼��ť
		btnclose = (Button) findViewById(R.id.btnClose);// ��ȡȡ����ť

		// �״ν�������ж��Ƿ������룬û�еĻ�����ע������Ի���
		if ((pwdDAO.getCount() == 0 || pwdDAO.find().getPassword().isEmpty())) {
			// ��ʾ��������Ի���
			showSetPasswordDialog();
		}

		btnlogin.setOnClickListener(new OnClickListener() {// Ϊ��¼��ť���ü����¼�
			@Override
			public void onClick(View arg0) {

				Intent intent = new Intent(Login.this, MainActivity.class);// ����Intent����

				// �ж�����������Ƿ������ݿ��е�����һ��
				if (pwdDAO.find().getPassword()
						.equals(txtlogin.getText().toString())) {
					startActivity(intent);// ������Activity
					//������¼�����activity
					finish();
				} else {
					// ������Ϣ��ʾ
					Toast.makeText(Login.this, "��������ȷ�����룡", Toast.LENGTH_SHORT)
							.show();
				}
				txtlogin.setText("");// ��������ı���
			}
		});

		btnclose.setOnClickListener(new OnClickListener() {// Ϊȡ����ť���ü����¼�
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				finish();// �˳���ǰ����
			}
		});
	}

	/* ��������Ի��� */
	private void showSetPasswordDialog() {

		View view = View.inflate(this, R.layout.lyl_dialog_setpassword, null);
		// ��ʾ�Ի���
		setPasswordDialog = new AlertDialog.Builder(this).setView(view).show();

		// �Ի����е����������
		et_setPassword_first = (EditText) view
				.findViewById(R.id.et_setPassword_first);
		et_setPassword_second = (EditText) view
				.findViewById(R.id.et_setPassword_second);

		Button bt_setpassword_ok = (Button) view
				.findViewById(R.id.bt_setpassword_ok);
		Button bt_setpassword_cancel = (Button) view
				.findViewById(R.id.bt_setpassword_cancel);

		//ע�ᰴť����¼�
		DialogOnclickListener listener = new DialogOnclickListener();
		bt_setpassword_ok.setOnClickListener(listener);
		bt_setpassword_cancel.setOnClickListener(listener);
	}

	// �Ի����а�ť�ĵ������
	class DialogOnclickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.bt_setpassword_ok: {
				// ���������һ�����������ȷ������
				String password1 = et_setPassword_first.getText().toString();
				String password2 = et_setPassword_second.getText().toString();
				if (TextUtils.isEmpty(password1)) {
					Toast.makeText(Login.this, "���벻��Ϊ��", Toast.LENGTH_SHORT).show();
				} else {
					if (password1.equals(password2)) {
						// ��������
						Tb_pwd tb_pwd = new Tb_pwd(password1);
						pwdDAO.add(tb_pwd);
						Toast.makeText(Login.this, "���뱣��ɹ�", Toast.LENGTH_SHORT).show();

						// �öԻ�����ʧ
						setPasswordDialog.dismiss();
					} else {
						Toast.makeText(Login.this, "ȷ�����벻��ȷ", Toast.LENGTH_SHORT).show();
					}
				}
			}
				break;

			case R.id.bt_setpassword_cancel: {

				// �öԻ�����ʧ
				setPasswordDialog.dismiss();
			}
				break;
			}
		}

	}
}
