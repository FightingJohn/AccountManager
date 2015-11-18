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
	EditText txtlogin;// 创建EditText对象
	Button btnlogin, btnclose;// 创建两个Button对象
	private PwdDAO pwdDAO;
	private AlertDialog setPasswordDialog;
	private EditText et_setPassword_first;
	private EditText et_setPassword_second;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);// 设置布局文件
		pwdDAO = new PwdDAO(Login.this);

		txtlogin = (EditText) findViewById(R.id.txtLogin);// 获取密码文本框
		btnlogin = (Button) findViewById(R.id.btnLogin);// 获取登录按钮
		btnclose = (Button) findViewById(R.id.btnClose);// 获取取消按钮

		// 首次进入软件判断是否有密码，没有的话弹出注册密码对话框
		if ((pwdDAO.getCount() == 0 || pwdDAO.find().getPassword().isEmpty())) {
			// 显示设置密码对话框
			showSetPasswordDialog();
		}

		btnlogin.setOnClickListener(new OnClickListener() {// 为登录按钮设置监听事件
			@Override
			public void onClick(View arg0) {

				Intent intent = new Intent(Login.this, MainActivity.class);// 创建Intent对象

				// 判断输入的密码是否与数据库中的密码一致
				if (pwdDAO.find().getPassword()
						.equals(txtlogin.getText().toString())) {
					startActivity(intent);// 启动主Activity
					//结束登录界面的activity
					finish();
				} else {
					// 弹出信息提示
					Toast.makeText(Login.this, "请输入正确的密码！", Toast.LENGTH_SHORT)
							.show();
				}
				txtlogin.setText("");// 清空密码文本框
			}
		});

		btnclose.setOnClickListener(new OnClickListener() {// 为取消按钮设置监听事件
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				finish();// 退出当前程序
			}
		});
	}

	/* 设置密码对话框 */
	private void showSetPasswordDialog() {

		View view = View.inflate(this, R.layout.lyl_dialog_setpassword, null);
		// 显示对话框
		setPasswordDialog = new AlertDialog.Builder(this).setView(view).show();

		// 对话框中的两个输入框
		et_setPassword_first = (EditText) view
				.findViewById(R.id.et_setPassword_first);
		et_setPassword_second = (EditText) view
				.findViewById(R.id.et_setPassword_second);

		Button bt_setpassword_ok = (Button) view
				.findViewById(R.id.bt_setpassword_ok);
		Button bt_setpassword_cancel = (Button) view
				.findViewById(R.id.bt_setpassword_cancel);

		//注册按钮点击事件
		DialogOnclickListener listener = new DialogOnclickListener();
		bt_setpassword_ok.setOnClickListener(listener);
		bt_setpassword_cancel.setOnClickListener(listener);
	}

	// 对话框中按钮的点击处理
	class DialogOnclickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.bt_setpassword_ok: {
				// 获得输入框第一次输入密码和确认密码
				String password1 = et_setPassword_first.getText().toString();
				String password2 = et_setPassword_second.getText().toString();
				if (TextUtils.isEmpty(password1)) {
					Toast.makeText(Login.this, "密码不能为空", Toast.LENGTH_SHORT).show();
				} else {
					if (password1.equals(password2)) {
						// 保存密码
						Tb_pwd tb_pwd = new Tb_pwd(password1);
						pwdDAO.add(tb_pwd);
						Toast.makeText(Login.this, "密码保存成功", Toast.LENGTH_SHORT).show();

						// 让对话框消失
						setPasswordDialog.dismiss();
					} else {
						Toast.makeText(Login.this, "确认密码不正确", Toast.LENGTH_SHORT).show();
					}
				}
			}
				break;

			case R.id.bt_setpassword_cancel: {

				// 让对话框消失
				setPasswordDialog.dismiss();
			}
				break;
			}
		}

	}
}
