package zjg.qinglu.util.net;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;


public class NetInfoUtil {
	public final static int NETTYPE_NONE=-1;
	public final static int NETTYPE_GPRS=0;
	public final static int NETTYPE_WIFI=1;
	
	
	public final static int getNetType(Context context){
		int info=-1;
		ConnectivityManager mConnectivityManager = (ConnectivityManager)context
		.getSystemService(Context.CONNECTIVITY_SERVICE); 
		NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo(); 
		if (mNetworkInfo != null && mNetworkInfo.isAvailable()) { 
			info=mNetworkInfo.getType(); 
		} 
	
		return info; 
	}
	
	
}
