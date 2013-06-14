package zjg.qinglu.util.net;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import zjg.qinglu.util.MyCookie;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
public class HttpUtil
{
	public int status=MessageWhat.NET_CONNECT_ING;
	
	private Context context=null;
	
	private String result=null;
	
	
	public HttpUtil(){
		
	}
	// ����HttpClient����
	public  HttpClient httpClient = new DefaultHttpClient();
	//APN��
	public static final String BASE_URL = "http://10.101.16.178/xcyx/android/";
	//������public static final String BASE_URL = "http://10.34.237.139/xcyx/android/";
	/**
	 * 
	 * @param url ���������URL
	 * @return ��������Ӧ�ַ���
	 * @throws Exception
	 */
	public  String getRequest(String url)throws Exception
	{
		String result=null;
		// ����HttpGet����
		HttpGet get = new HttpGet(url);
		//���sessionid��Ϣ��Cookie
		get.setHeader("Cookie", MyCookie.getCookie("sessionid"));
		// ����GET����
		HttpResponse httpResponse = httpClient.execute(get);
		
		// ����������ɹ��ط�����Ӧ
		if (httpResponse.getStatusLine().getStatusCode() == 200)
		{
			this.status=MessageWhat.NET_CONNECT_SUCCESS;
			// ��ȡ��������Ӧ�ַ���
			result = EntityUtils.toString(httpResponse.getEntity());
			return result;
		}else{
			this.status=MessageWhat.NET_CONNECT_FAIL;
		}
		return null;
	}

	/**
	 * 
	 * @param url ���������URL
	 * @param params �������
	 * @return ��������Ӧ�ַ���
	 * @throws Exception
	 */
	public  String postRequest(String url, Map<String ,String> rawParams)throws Exception
	{
		String result=null;
		// ����HttpPost����
		HttpPost post = new HttpPost(url);
		post.setHeader("Cookie", MyCookie.getCookie("sessionid"));
		// ������ݲ��������Ƚ϶�Ļ����ԶԴ��ݵĲ������з�װ
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		for(String key : rawParams.keySet())
		{
			//��װ�������
			params.add(new BasicNameValuePair(key , rawParams.get(key)));
		}
		// �����������
		post.setEntity(new UrlEncodedFormEntity(params, "UTF_8"));
		// ����POST����
		HttpResponse httpResponse = httpClient.execute(post);
		// ����������ɹ��ط�����Ӧ
		if (httpResponse.getStatusLine().getStatusCode() == 200)
		{
			this.status=MessageWhat.NET_CONNECT_SUCCESS;
			// ��ȡ��������Ӧ�ַ���
			result = EntityUtils.toString(httpResponse.getEntity());
			
		}else{
			this.status=MessageWhat.NET_CONNECT_FAIL;
		}
		return result;
	}
	
	/**
	 * 
	 * @param url ���������URL
	 * @param rawParams �������
	 * @param Handler handler ���ա�������Ϣ��Handler��
	 * @param int outTim �������ӳ�ʱʱ�䣨��λ��0.1�룩;
	 * @return ��������Ӧ�ַ���
	 * @throws Exception
	 */
	public void postRequest(String url, Map<String ,String> rawParams,Handler handler,int outTime){
		new postThread(url, rawParams).start();
		new CheckStatusThread(handler,outTime).start();
	}
	
	/**
	 * ������Handler���յ�MessageWhat.NET_CONNECT_SUCCESS��Ϣʱ���ã�
	 * @return ����void postRequest(url,rawParams,handler,ioutTime)������õ�����Ӧ���;
	 */
	public String getResult(){
		return this.result;
	}
	
	class postThread implements Runnable{
		private Thread thread=null;
		private String url=null;
		private Map<String ,String> rawParams=null;
		public postThread(String url, Map<String ,String> rawParams){
			thread=new Thread(this);
			this.url=url;
			this.rawParams=rawParams;
		}

		public void run() {
			// TODO Auto-generated method stub
			try {
				HttpUtil.this.result=HttpUtil.this.postRequest(this.url, this.rawParams);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				HttpUtil.this.status=MessageWhat.NET_CONNECT_FAIL;
			}
		}
		
		public void start(){
			thread.start();
		}
	}
	
	class  CheckStatusThread implements Runnable{
		private Handler handler=null;
		private Thread thread=null;
		private int outTime;
		public CheckStatusThread(Handler handler,int outTime){
			this.handler=handler;
			thread=new Thread(this);
			this.outTime=outTime;
		}

	
		public void run() {
			// TODO Auto-generated method stub
			int time=0;
			boolean loop=true;	
			while(loop){
				time++;  //����ʱ�䣻
				Message msg=new Message();
				//�ж���������״̬��
				
				//���ó�ʱ�¼�;
				if(time>=outTime){
					loop=false;
					msg.what=MessageWhat.NET_CONNECT_OUTTIME;
					handler.sendMessage(msg);
					break;
				}
				
				msg.what=HttpUtil.this.status;
				handler.sendMessage(msg);
				if(msg.what!=MessageWhat.NET_CONNECT_ING){
					loop=false;
					break;
				}
				try {
					thread.sleep(100);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
		public void start(){
			thread.start();
		}
	}
}
