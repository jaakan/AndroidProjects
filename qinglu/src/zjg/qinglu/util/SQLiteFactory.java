package zjg.qinglu.util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public class SQLiteFactory {
	private  static  SQLiteDatabase db=null;
	
	public static synchronized SQLiteDatabase getDB(){
		return db;
	}
	
	public static void createDB(Context context,String fileName){
		String path=context.getFilesDir().toString();
		String filePath=path+"/"+fileName;
		db=SQLiteDatabase.openOrCreateDatabase(filePath, null);
	}
	
	public static void close(){
		db.close();
	}
}
