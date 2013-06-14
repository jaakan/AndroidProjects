package zjg.qinglu.util.ui;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class LoadImagesThread implements Runnable{
	private final Handler handler;
	private Thread thread=null;
	private String urlStr=null;
	private int point;
	private final Bitmap[] imageDatas;
	
	public LoadImagesThread(final Handler handler,String url,int point,final Bitmap[] imageDatas){
		this.handler=handler;
		this.thread=new Thread(this);
		this.urlStr=url;
		this.point=point;
		this.imageDatas=imageDatas;
	}


	public  void run() {
		// TODO Auto-generated method stub
		Message msg=new Message();
		String _url = null;
		try {
			_url = urlStr;
			Log.d("LoadImagesThread>>imageURL:", _url);

			URL url = new URL(_url);
			HttpURLConnection con = (HttpURLConnection) url
					.openConnection();
			con.setReadTimeout(10*1000);
			if(con.getResponseCode()==200){
				InputStream in=con.getInputStream();
				imageDatas[this.point]=BitmapFactory.decodeStream(in);
				msg.what=point;
				in.close();
			}else{
				msg.what=-1;
			}

		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			msg.what=-1;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			msg.what=-1;
		}
		this.handler.sendMessage(msg);
	}
	
	public void start(){
		thread.start();
	}
	
}