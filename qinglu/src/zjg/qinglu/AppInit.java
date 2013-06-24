package zjg.qinglu;

import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import zjg.qinglu.util.AppInfo;
import zjg.qinglu.util.MySession;
import zjg.qinglu.util.SQLiteFactory;
import zjg.qinglu.util.ScreenInfo;
import zjg.qinglu.util.net.HttpUtil;
import zjg.qinglu.util.net.MessageWhat;
import zjg.qinglu.util.net.NetInfoUtil;
import zjg.qinglu.util.ui.ProgressViewHandler;
import zjg.qinglu.util.ui.dialog.DialogUtil;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

public class AppInit extends Activity {
	private final String reqURL = AppInfo.BASE_URL + "jsp/version.jsp";
	private String downloadURL = null;
	private HashMap<String, String> version = new HashMap<String, String>();
	private ProgressViewHandler pvh = null;
	private float newestVersion;
	private String note=null;

	@Override
	public void onCreate(Bundle savedInstanceState) {

		/**** ��̬���ݳ�ʼ�� ****/
		super.onCreate(savedInstanceState);
		// ���ر�����
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.setContentView(R.layout.appinit);
		// ��ȡ��Ļ��Ϣ��������ScreenInfo���У�
		WindowManager manager = getWindowManager();
		ScreenInfo.width = manager.getDefaultDisplay().getWidth();
		ScreenInfo.height = manager.getDefaultDisplay().getHeight();
		//����������Sqlite
		SQLiteFactory.createDB(this, "qinglu.db3");
		// ����ProgressViewHandler��
		pvh = new ProgressViewHandler(this);
		pvh.createProgressDialog("");

		// �ж�����״̬����Ϊ���������Ӳ��ܼ������������Ѵ����ӣ�
		int netInfo = NetInfoUtil.getNetType(this);
		loadData();
		/*
		if (netInfo == NetInfoUtil.NETTYPE_WIFI&&netInfo == NetInfoUtil.NETTYPE_GPRS) {
			// ���������ȡ�汾��Ϣ��
			loadData();
		} else {
			DialogUtil.showDialog(AppInit.this, "�����磬���GPRS������APN��",
					new OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							Intent intent = new Intent(
									android.provider.Settings.ACTION_SETTINGS);
							AppInit.this.startActivityForResult(intent, 0);
							AppInit.this.finish();
						}

					});
		}
		*/

	}
	
	/****** �������粢��ȡ�汾��Ϣ ******/
	private void loadData(){
		final HttpUtil http = new HttpUtil();
		HashMap<String, String> rawParams = new HashMap<String, String>();
		rawParams.put("action", "getVersion");
		http.postRequest(reqURL, rawParams, new Handler() {

			int n = 0;
			String dot = ".";

			@Override
			public void handleMessage(Message msg) {
				/**** ���������ʼ�� ****/
				switch (msg.what) {
				case MessageWhat.NET_CONNECT_ING: // ��������
					// do nothing;
					n++;
					dot += ".";
					if (n % 4 == 0) {
						dot = ".";
					}
					AppInit.this.pvh.setAlertText("���ڼ���" + dot);
					break;
				case MessageWhat.NET_CONNECT_SUCCESS: // ���ӳɹ�
					String result = http.getResult();
					Log.d("result:", result);
					AppInit.this.pvh.removeProgressDialog();
					if (result != null) {
						try {
							JSONObject json = new JSONObject(result);
							//��ȡ���°汾�ţ�
							AppInit.this.newestVersion=Float.parseFloat(json.getString("version"));
							//��ȡ���°汾�ĸ�����Ϣ��
							AppInit.this.note=json.getString("note");
							//��ȡ���°汾�����ص�ַ;
							AppInit.this.downloadURL=json.getString("downloadURL");
							//ִ�а汾�жϲ����ݽ����ת��
							AppInit.this.checkVersion();
							
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							DialogUtil.showDialog(AppInit.this, "������������ֵ��JSON",
									true);
						}
					}else{
						Log.d("goto:", "Login");
						// ����Login Activity
						Intent intent = new Intent(AppInit.this, Login.class);
						startActivity(intent);
						finish();
					}
					break;
				case MessageWhat.NET_CONNECT_FAIL: // ����ʧ��
					AppInit.this.pvh.removeProgressDialog();
					
					DialogUtil.showDialog(AppInit.this, "�����쳣,����APN����",
							new OnClickListener() {

								public void onClick(DialogInterface dialog,
										int which) {
									// TODO Auto-generated method stub
									Intent intent = new Intent(
											android.provider.Settings.ACTION_APN_SETTINGS);
									AppInit.this.startActivityForResult(intent,
											0);
									AppInit.this.finish();

								}

							});
					break;
				case MessageWhat.NET_CONNECT_OUTTIME:// ���ӳ�ʱ;
					AppInit.this.pvh.removeProgressDialog();
					
					DialogUtil.showDialog(AppInit.this, "���ӳ�ʱ,����APN����,�������ź�",
							new OnClickListener() {

								public void onClick(DialogInterface dialog,
										int which) {
									// TODO Auto-generated method stub
									Intent intent = new Intent(
											android.provider.Settings.ACTION_APN_SETTINGS);
									AppInit.this.startActivityForResult(intent,
											0);
									AppInit.this.finish();

								}

							});
					break;
				}
			}
		}, 300);
	}

	/****** ���汾��Ϣ���ݰ汾��Ϣѡ����ת ******/
	private void checkVersion() {
		//������汾��
		if(AppInfo.version<this.newestVersion){  //�и��£�
			//��ʾ���²������°汾;
			String _note = "�����°汾��\n" + this.note;
			DialogUtil.showDialog(this, _note, new OnClickListener() {
				
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					Log.d("goto:", "MultiThreadDown");
					// ��ȡ���°汾�ĵ�ַ���洢�ڻỰversion�У�
					Log.d("downloadURL:", AppInit.this.downloadURL);
					AppInit.this.version.put("url", AppInit.this.downloadURL);
					// ����MultiThreadDown Activity
					Intent intent = new Intent(AppInit.this,
							DownLoad.class);
					MySession.setSession("version", version);
					startActivity(intent);
					// ������Activity
					AppInit.this.finish();
				}
			});
		}else{   //�޸���
			Intent intent = new Intent(this,
					Login.class);
			startActivity(intent);
			this.finish();
		}
	}

}
