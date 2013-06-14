package zjg.qinglu.util;

import java.util.HashMap;

public class MySession {
	private static HashMap<String, Object> map = new HashMap<String, Object>();

	public static void setSession(String key, Object value) {
		if (map.containsKey(key)) {
			map.remove(key);
		}
		map.put(key, value);
	}

	public static Object getSession(String key) {
		if (!map.containsKey(key)) {
			return null;
		} else {
			return map.get(key);
		}
	}
}
