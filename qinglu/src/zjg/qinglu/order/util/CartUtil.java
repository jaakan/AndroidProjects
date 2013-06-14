package zjg.qinglu.order.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import zjg.qinglu.util.MySharedPreferences;
import zjg.qinglu.util.SQLiteFactory;

public class CartUtil {
	private Context context=null;
	private SQLiteDatabase db=null;
	public CartUtil(Context context){
		this.context=context;
		db=SQLiteFactory.getDB();
		String sql="CREATE TABLE IF NOT  EXISTS tb_cart(" +
				"id varchar(255)," +
				"name varchar(255)," +
				"type varchar(255)," +
				"number varchar(255)," +
				"price varchar(255)" +
				")";
		db.execSQL(sql);
	}
	public  void add(JSONObject sale,int number) throws JSONException{
		String id=sale.getString("id");
		HashMap<String,String> result=this.get(id);
		if(result!=null){
			this.update(id,number);
		}else{
			String name=sale.getString("name");
			String type=sale.getString("type_name");
			String price=sale.getString("price");
			String sql="INSERT INTO tb_cart (id,name,type,number,price) VALUES ('"+
			id+"','"+
			name+"','"+
			type+"','"+
			Integer.toString(number)+"','"+
			price+"')";
			db.execSQL(sql);
		}
		
	}
	
	public void update(String id,int number){
		if(number>0){
			String sql="UPDATE tb_cart SET number='"+Integer.toString(number)+"' WHERE id='"+id+"'";
			db.execSQL(sql);
		}else{
			this.remove(id);
		}
	}
	
	
	public void remove(String id){
		String sql="DELETE FROM tb_cart WHERE id='"+id+"'";
		db.execSQL(sql);
	}
	
	public HashMap<String,String> get(String id){
		String sql="SELECT * FROM tb_cart WHERE id='"+id+"'";
		HashMap<String,String> result =new HashMap<String,String>();
		Cursor cursor=this.db.rawQuery(sql, null);
		if(cursor.moveToNext()){
			result.put("id", cursor.getString(0));
			result.put("name", cursor.getString(1));
			result.put("type", cursor.getString(2));
			result.put("number", cursor.getString(3));
			result.put("price", cursor.getString(4));
			return result;
		}
		return null;
	}
	
	public void clear(){
		String sql="DELETE FROM tb_cart";
		db.execSQL(sql);
	}
	
	public List<HashMap<String,String>> list(){
		String sql="SELECT * FROM tb_cart";
		List<HashMap<String,String>> list =new ArrayList<HashMap<String,String>>();
		Cursor cursor=this.db.rawQuery(sql, null);
		while(cursor.moveToNext()){
			HashMap<String,String> result=new HashMap<String,String>();
			result.put("id", cursor.getString(0));
			result.put("name", cursor.getString(1));
			result.put("type", cursor.getString(2));
			result.put("number", cursor.getString(3));
			result.put("price", cursor.getString(4));
			list.add(result);
		}
		return list;
	}
	
}
