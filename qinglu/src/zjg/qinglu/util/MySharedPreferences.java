package zjg.qinglu.util;

import android.content.Context;
import android.content.SharedPreferences;

public class MySharedPreferences {
	public static String read(String spName,String key,Context context){
		return MySharedPreferences.getSharedPreferences(spName, context).getString(key, null);
	}
	public static void write(String spName,String key,String value,Context context){
		MySharedPreferences.getEditor(spName, context).putString(key, value).commit();
	}
	
	public static void remove(String spName,String key,Context context){
		MySharedPreferences.getEditor(spName, context).remove(key).commit();
	}
	
	public static SharedPreferences getSharedPreferences(String name,Context context){
		 return context.getSharedPreferences(
				 name,context.MODE_PRIVATE);
	}
	
	public static SharedPreferences.Editor getEditor(String name,Context context){
		return MySharedPreferences.getSharedPreferences(name, context).edit();
	}
	
	public static void clear(String name,Context context){
		MySharedPreferences.getEditor(name, context).clear().commit();
	}
}
