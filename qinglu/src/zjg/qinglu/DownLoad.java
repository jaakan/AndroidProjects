package zjg.qinglu;

import java.io.File;
import java.net.URI;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import zjg.qinglu.R;
import zjg.qinglu.util.net.DownUtil;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * Description:
 * <br/>site: <a href="http://www.crazyit.org">crazyit.org</a> 
 * <br/>Copyright (C), 2001-2012, Yeeku.H.Lee
 * <br/>This program is protected by copyright laws.
 * <br/>Program Name:
 * <br/>Date:
 * @author  Yeeku.H.Lee kongyeeku@163.com
 * @version  1.0
 */
public class DownLoad extends Activity
{
	String url=null;
	TextView target;
	Button downBn;
	ProgressBar bar;
	DownUtil downUtil;
	private HashMap<String,String>upload;
	
	private int mDownStatus;
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.download);
		//��ȡurl��
		upload = (HashMap<String,String>) this.getIntent().getBundleExtra("version").getSerializable("versionid");
		url=upload.get("url");
		Log.d("2:URL:",url);
		// ��ȡ��������е���������ؼ�
		target = (TextView) findViewById(R.id.download_target);
		downBn = (Button) findViewById(R.id.download_down);
		bar = (ProgressBar) findViewById(R.id.download_bar);
		
		// ����һ��Handler����
		final Handler handler = new Handler()
		{
			@Override
			public void handleMessage(Message msg)
			{
				if (msg.what == 0x123)
				{
					Log.d("mDownStatus",new Integer(mDownStatus).toString());
					bar.setProgress(mDownStatus);
				}
			}
		};
		downBn.setOnClickListener(new OnClickListener()
		{
	
			public void onClick(View v)
			{
				// ��ʼ��DownUtil����
				downUtil = new DownUtil(url,
					target.getText().toString(), 1);
				try
				{
					// ��ʼ����
					downUtil.download();
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
				// ����ÿ����Ȼ�ȡһ��ϵͳ����ɽ���
				final Timer timer = new Timer();
				timer.schedule(new TimerTask()
				{
					@Override
					public void run()
					{
						// ��ȡ�����������ɱ���
						double completeRate = downUtil.getCompleteRate();
						mDownStatus = (int) (completeRate * 100);
						// ������Ϣ֪ͨ������½�����
						handler.sendEmptyMessage(0x123);
						// ������ȫ��ȡ���������
						if (mDownStatus >= 100)
						{
							//ֹͣ��ʱ��;
							timer.cancel();
							//�Զ���APK�ļ�;
							Intent intent = new Intent(Intent.ACTION_VIEW);  
							intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
							Uri uri = Uri.fromFile(new File(target.getText().toString()));
						    intent.setDataAndType(uri, "application/vnd.android.package-archive");           
							startActivity(intent);
						}
					}
				}, 0, 100);
			}
		});
		
	}
}