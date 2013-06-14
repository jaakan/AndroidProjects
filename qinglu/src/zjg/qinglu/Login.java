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
	//���屣�����ݵ�JSON��
	private JSONObject saveDataJSON=new JSONObject();
	//����������ѡ��ť��
	private CheckBox radio1;
	// ��������������ı���
	EditText etName, etPass;
	// ���������������ť
	Button bnLogin, bnCancel;
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		// ���ر�����
        requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.login);
		
		// ��ȡ�����������༭��������ѡ��ť��
		etName = (EditText) findViewById(R.id.userEditText);
		etPass = (EditText) findViewById(R.id.pwdEditText);
		
		//�����û����ı��༭�򵯳����ּ���:
		etName.setInputType(EditorInfo.TYPE_CLASS_PHONE);
		radio1=(CheckBox)this.findViewById(R.id.saveUsername);
		//��ȡֻ�ܱ���Ӧ�ó����д��SharedPreferences;
		String userData=MySharedPreferences.read("userData", "user", this);
		
		//�������SharedPreferences�����û��������������������䣻
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
        int width = metric.widthPixels;     // ��Ļ��ȣ����أ�
        int height = metric.heightPixels;   // ��Ļ�߶ȣ����أ�
		etName.setWidth(width-20);
		*/
		
		// ��ȡ�����е�������ť
		bnLogin = (Button) findViewById(R.id.bnLogin);
		bnCancel = (Button) findViewById(R.id.bnCancel);
		// ΪbnCancal��ť�ĵ����¼����¼�������
		bnCancel.setOnClickListener(new CloseListener(this));
		bnLogin.setOnClickListener(new OnClickListener()
		{
		
			public void onClick(View v)
			{
				// ִ������У��
				if (validate())
				{
					// �����¼�ɹ�
					if (loginPro())
					{
						// ����Main Activity
						
						Intent intent = new Intent(Login.this,zjg.qinglu.Main.class);
						startActivity(intent);
						Log.d("Start Main.class:","Start Main.class");
						// ������Activity
						finish();
					}
					else
					{
						DialogUtil.showDialog(Login.this
							, "�û����ƻ�������������������룡", false);
					}
				}
			}
		});
	}

	private boolean loginPro()
	{
		// ��ȡ�û�������û���������
		String username = etName.getText().toString();
		String passwd = etPass.getText().toString();
		JSONObject jsonObj;
		try
		{
			jsonObj = query(username, passwd);
			Log.d("���ص�¼���:", jsonObj.toString());
			// ���userId ����0
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
				
				//��½�ɹ����ж��Ƿ񱣴��û���Ϣ�����ǣ����û���Ϣд��SharedPreferences��
				if(radio1.isChecked()){
					saveDataJSON.put("username",username);
					saveDataJSON.put("password",passwd);
				}else{
					saveDataJSON.put("username","");
					saveDataJSON.put("password","");
					
				}
				//д��sharedPreferences
				MySharedPreferences.write("userData", "user", saveDataJSON.toString(), this);
				return true;
			}
		}
		catch (Exception e)
		{
			DialogUtil.showDialog(this, "��������Ӧ�쳣�����Ժ����ԣ�", false);
			e.printStackTrace();
		}
		return false;
	}

	// ���û�������û������������У��
	private boolean validate()
	{
		String username = etName.getText().toString().trim();
		if (username.equals(""))
		{
			DialogUtil.showDialog(this, "�û��˻��Ǳ����", false);
			return false;
		}
		String pwd = etPass.getText().toString().trim();
		if (pwd.equals(""))
		{
			DialogUtil.showDialog(this, "�û������Ǳ����", false);
			return false;
		}
		return true;
	}

	// ���巢������ķ���
	private JSONObject query(String username, String password) throws Exception
	{
		// ʹ��Map��װ�������
		Map<String, String> map = new HashMap<String, String>();
		map.put("username", username);
		map.put("passwd", password);
		// ���巢�������URL
		String url = AppInfo.BASE_URL + "login.do";
		// ��������
		return new JSONObject(new HttpUtil().postRequest(url, map));
	}

}