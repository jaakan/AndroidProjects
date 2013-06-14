package zjg.qinglu.util;

import java.util.HashMap;

public class MyCookie {
	private static HashMap<String, String> map = new HashMap<String, String>();
	public static void setCookie(String key,String value){
		if (map.containsKey(key)) {
			map.remove(key);
		}
		String sessionCookie="JSESSIONID="+value+    //java ÈÝÆ÷
				";PHPSESSID="+value;                 //phpÈÝÆ÷;
		map.put(key, sessionCookie);
	}
	
	public static String getCookie(String key){
		if (!map.containsKey(key)) {
			return null;
		} else {
			return map.get(key);
		}
	}
}
