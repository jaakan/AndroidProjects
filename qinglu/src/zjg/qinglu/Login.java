package zjg.qinglu;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import zjg.qinglu.util.AppInfo;
import zjg.qinglu.util.MyCookie;
import zjg.qinglu.util.MySession;
import zjg.qinglu.util.MySharedPreferences;
import zjg.qinglu.util.listener.CloseListener;
import zjg.qinglu.util.net.HttpUtil;
import zjg.qinglu.util.ui.dialog.DialogUtil;

public class Login extends Activity
{
	//定义保存数据的JSON；
	private JSONObject saveDataJSON=new JSONObject();
	//创建两个单选按钮；
	private CheckBox radio1;
	// 定义界面中两个文本框
	EditText etName, etPass;
	// 定义界面中两个按钮
	Button bnLogin, bnCancel;
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		// 隐藏标题栏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.login);
		
		// 获取界面中两个编辑框及两个复选按钮；
		etName = (EditText) findViewById(R.id.userEditText);
		etPass = (EditText) findViewById(R.id.pwdEditText);
		
		//设置用户名文本编辑框弹出数字键盘:
		etName.setInputType(EditorInfo.TYPE_CLASS_PHONE);
		radio1=(CheckBox)this.findViewById(R.id.saveUsername);
		//获取只能被本应用程序读写的SharedPreferences;
		String userData=MySharedPreferences.read("userData", "user", this);
		
		//如果存在SharedPreferences，将用户名输入框和密码输入框填充；
		if(userData!=null){
			try {
				this.saveDataJSON=new JSONObject(userData);
				etName.setText(saveDataJSON.getString("username"));
				etPass.setText(saveDataJSON.getString("password"));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
		
		/*
		DisplayMetrics metric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metric);
        int width = metric.widthPixels;     // 屏幕宽度（像素）
        int height = metric.heightPixels;   // 屏幕高度（像素）
		etName.setWidth(width-20);
		*/
		
		// 获取界面中的两个按钮
		bnLogin = (Button) findViewById(R.id.bnLogin);
		bnCancel = (Button) findViewById(R.id.bnCancel);
		// 为bnCancal按钮的单击事件绑定事件监听器
		bnCancel.setOnClickListener(new CloseListener(this));
		bnLogin.setOnClickListener(new OnClickListener()
		{
		
			public void onClick(View v)
			{
				// 执行输入校验
				if (validate())
				{
					// 如果登录成功
					if (loginPro())
					{
						// 启动Main Activity
						
						Intent intent = new Intent(Login.this,zjg.qinglu.Main.class);
						startActivity(intent);
						Log.d("Start Main.class:","Start Main.class");
						// 结束该Activity
						finish();
					}
					else
					{
						DialogUtil.showDialog(Login.this
							, "用户名称或者密码错误，请重新输入！", false);
					}
				}
			}
		});
	}

	private boolean loginPro()
	{
		// 获取用户输入的用户名、密码
		String username = etName.getText().toString();
		String passwd = etPass.getText().toString();
		JSONObject jsonObj;
		try
		{
			jsonObj = query(username, passwd);
			Log.d("返回登录结果:", jsonObj.toString());
			// 如果userId 大于0
			if (jsonObj.getString("access").equals("success"))
			{
				/* user:{"level":"","name":"","dept":""} */
				JSONObject _user=jsonObj.getJSONObject("user");
				HashMap<String,String> user=new HashMap<String,String>();
				user.put("level",_user.getString("level"));
				user.put("name",_user.getString("name"));
				user.put("dept",_user.getString("dept"));
				String sessionid=jsonObj.getString("sessionid");
				MySession.setSession("user",user);
				MySession.setSession("sessionid", sessionid);
				MyCookie.setCookie("sessionid", sessionid);
				
				//登陆成功，判断是否保存用户信息，若是，将用户信息写入SharedPreferences；
				if(radio1.isChecked()){
					saveDataJSON.put("username",username);
					saveDataJSON.put("password",passwd);
				}else{
					saveDataJSON.put("username","");
					saveDataJSON.put("password","");
					
				}
				//写入sharedPreferences
				MySharedPreferences.write("userData", "user", saveDataJSON.toString(), this);
				return true;
			}
		}
		catch (Exception e)
		{
			DialogUtil.showDialog(this, "服务器响应异常，请稍后再试！", false);
			e.printStackTrace();
		}
		return false;
	}

	// 对用户输入的用户名、密码进行校验
	private boolean validate()
	{
		String username = etName.getText().toString().trim();
		if (username.equals(""))
		{
			DialogUtil.showDialog(this, "用户账户是必填项！", false);
			return false;
		}
		String pwd = etPass.getText().toString().trim();
		if (pwd.equals(""))
		{
			DialogUtil.showDialog(this, "用户口令是必填项！", false);
			return false;
		}
		return true;
	}

	// 定义发送请求的方法
	private JSONObject query(String username, String password) throws Exception
	{
		// 使用Map封装请求参数
		Map<String, String> map = new HashMap<String, String>();
		map.put("username", username);
		map.put("passwd", password);
		// 定义发送请求的URL
		String url = AppInfo.BASE_URL + "login.do";
		// 发送请求
		return new JSONObject(new HttpUtil().postRequest(url, map));
	}

}