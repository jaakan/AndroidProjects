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
	// 创建HttpClient对象
	public  HttpClient httpClient = new DefaultHttpClient();
	//APN：
	public static final String BASE_URL = "http://10.101.16.178/xcyx/android/";
	//内网：public static final String BASE_URL = "http://10.34.237.139/xcyx/android/";
	/**
	 * 
	 * @param url 发送请求的URL
	 * @return 服务器响应字符串
	 * @throws Exception
	 */
	public  String getRequest(String url)throws Exception
	{
		String result=null;
		// 创建HttpGet对象。
		HttpGet get = new HttpGet(url);
		//添加sessionid信息至Cookie
		get.setHeader("Cookie", MyCookie.getCookie("sessionid"));
		// 发送GET请求
		HttpResponse httpResponse = httpClient.execute(get);
		
		// 如果服务器成功地返回响应
		if (httpResponse.getStatusLine().getStatusCode() == 200)
		{
			this.status=MessageWhat.NET_CONNECT_SUCCESS;
			// 获取服务器响应字符串
			result = EntityUtils.toString(httpResponse.getEntity());
			return result;
		}else{
			this.status=MessageWhat.NET_CONNECT_FAIL;
		}
		return null;
	}

	/**
	 * 
	 * @param url 发送请求的URL
	 * @param params 请求参数
	 * @return 服务器响应字符串
	 * @throws Exception
	 */
	public  String postRequest(String url, Map<String ,String> rawParams)throws Exception
	{
		String result=null;
		// 创建HttpPost对象。
		HttpPost post = new HttpPost(url);
		post.setHeader("Cookie", MyCookie.getCookie("sessionid"));
		// 如果传递参数个数比较多的话可以对传递的参数进行封装
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		for(String key : rawParams.keySet())
		{
			//封装请求参数
			params.add(new BasicNameValuePair(key , rawParams.get(key)));
		}
		// 设置请求参数
		post.setEntity(new UrlEncodedFormEntity(params, "UTF_8"));
		// 发送POST请求
		HttpResponse httpResponse = httpClient.execute(post);
		// 如果服务器成功地返回响应
		if (httpResponse.getStatusLine().getStatusCode() == 200)
		{
			this.status=MessageWhat.NET_CONNECT_SUCCESS;
			// 获取服务器响应字符串
			result = EntityUtils.toString(httpResponse.getEntity());
			
		}else{
			this.status=MessageWhat.NET_CONNECT_FAIL;
		}
		return result;
	}
	
	/**
	 * 
	 * @param url 发送请求的URL
	 * @param rawParams 请求参数
	 * @param Handler handler 接收、处理消息的Handler；
	 * @param int outTim 设置连接超时时间（单位：0.1秒）;
	 * @return 服务器响应字符串
	 * @throws Exception
	 */
	public void postRequest(String url, Map<String ,String> rawParams,Handler handler,int outTime){
		new postThread(url, rawParams).start();
		new CheckStatusThread(handler,outTime).start();
	}
	
	/**
	 * 在宿主Handler接收到MessageWhat.NET_CONNECT_SUCCESS消息时调用，
	 * @return 调用void postRequest(url,rawParams,handler,ioutTime)方法后得到的响应结果;
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
				time++;  //设置时间；
				Message msg=new Message();
				//判断网络连接状态；
				
				//设置超时事件;
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
