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

		/**** 静态内容初始化 ****/
		super.onCreate(savedInstanceState);
		// 隐藏标题栏
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.setContentView(R.layout.appinit);
		// 获取屏幕信息并保存在ScreenInfo类中；
		WindowManager manager = getWindowManager();
		ScreenInfo.width = manager.getDefaultDisplay().getWidth();
		ScreenInfo.height = manager.getDefaultDisplay().getHeight();
		//创建本程序Sqlite
		SQLiteFactory.createDB(this, "qinglu.db3");
		// 设置ProgressViewHandler；
		pvh = new ProgressViewHandler(this);
		pvh.createProgressDialog("");

		// 判断网络状态，若为有网络连接才能继续，否则提醒打开连接；
		int netInfo = NetInfoUtil.getNetType(this);
		loadData();
		/*
		if (netInfo == NetInfoUtil.NETTYPE_WIFI&&netInfo == NetInfoUtil.NETTYPE_GPRS) {
			// 连接网络获取版本信息；
			loadData();
		} else {
			DialogUtil.showDialog(AppInit.this, "无网络，请打开GPRS并设置APN！",
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
	
	/****** 连接网络并获取版本信息 ******/
	private void loadData(){
		final HttpUtil http = new HttpUtil();
		HashMap<String, String> rawParams = new HashMap<String, String>();
		rawParams.put("action", "getVersion");
		http.postRequest(reqURL, rawParams, new Handler() {

			int n = 0;
			String dot = ".";

			@Override
			public void handleMessage(Message msg) {
				/**** 处理网络初始化 ****/
				switch (msg.what) {
				case MessageWhat.NET_CONNECT_ING: // 正在连接
					// do nothing;
					n++;
					dot += ".";
					if (n % 4 == 0) {
						dot = ".";
					}
					AppInit.this.pvh.setAlertText("正在加载" + dot);
					break;
				case MessageWhat.NET_CONNECT_SUCCESS: // 连接成功
					String result = http.getResult();
					Log.d("result:", result);
					AppInit.this.pvh.removeProgressDialog();
					if (result != null) {
						try {
							JSONObject json = new JSONObject(result);
							//获取最新版本号；
							AppInit.this.newestVersion=Float.parseFloat(json.getString("version"));
							//获取最新版本的更新信息；
							AppInit.this.note=json.getString("note");
							//获取最新版本的下载地址;
							AppInit.this.downloadURL=json.getString("downloadURL");
							//执行版本判断并根据结果跳转；
							AppInit.this.checkVersion();
							
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							DialogUtil.showDialog(AppInit.this, "服务器传回数值非JSON",
									true);
						}
					}else{
						Log.d("goto:", "Login");
						// 启动Login Activity
						Intent intent = new Intent(AppInit.this, Login.class);
						startActivity(intent);
						finish();
					}
					break;
				case MessageWhat.NET_CONNECT_FAIL: // 连接失败
					AppInit.this.pvh.removeProgressDialog();
					
					DialogUtil.showDialog(AppInit.this, "网络异常,请检查APN设置",
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
				case MessageWhat.NET_CONNECT_OUTTIME:// 连接超时;
					AppInit.this.pvh.removeProgressDialog();
					
					DialogUtil.showDialog(AppInit.this, "连接超时,请检查APN设置,或网络信号",
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

	/****** 检查版本信息根据版本信息选择跳转 ******/
	private void checkVersion() {
		//检查程序版本；
		if(AppInfo.version<this.newestVersion){  //有更新；
			//提示更新并下载新版本;
			String _note = "发现新版本！\n" + this.note;
			DialogUtil.showDialog(this, _note, new OnClickListener() {
				
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					Log.d("goto:", "MultiThreadDown");
					// 获取最新版本的地址并存储在会话version中；
					Log.d("downloadURL:", AppInit.this.downloadURL);
					AppInit.this.version.put("url", AppInit.this.downloadURL);
					// 启动MultiThreadDown Activity
					Intent intent = new Intent(AppInit.this,
							DownLoad.class);
					MySession.setSession("version", version);
					startActivity(intent);
					// 结束该Activity
					AppInit.this.finish();
				}
			});
		}else{   //无更新
			Intent intent = new Intent(this,
					Login.class);
			startActivity(intent);
			this.finish();
		}
	}

}
