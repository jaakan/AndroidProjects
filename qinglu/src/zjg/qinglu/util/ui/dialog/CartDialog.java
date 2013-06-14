package zjg.qinglu.util.ui.dialog;

import org.json.JSONException;
import org.json.JSONObject;

import zjg.qinglu.order.util.CartUtil;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.text.InputType;
import android.util.Log;
import android.widget.EditText;

public class CartDialog {
	private Context context = null;
	private AlertDialog.Builder builder = null;
	private AlertDialog dialog = null;
	private EditText numText = null;
	private CartUtil cart = null;
	private Handler handler = null;
	public final static int CHANGE = 1;
	public final static int ADD = 2;
	public final static int DELETE = 3;
	public final static int CLEAR=4;
	private Message msg = null;

	public CartDialog(Context context) {
		this.context = context;
		msg = new Message();
		this.cart = new CartUtil(this.context);
	}

	public void setHandler(Handler handler) {
		this.handler = handler;
	}

	public void add(JSONObject sale) {
		final JSONObject _sale=sale;
		builder = new AlertDialog.Builder(this.context);
		numText = new EditText(this.context);
		numText.setInputType(InputType.TYPE_CLASS_PHONE);
		builder.setTitle("输入购买数量：");
		builder.setView(numText);
		// 创建响应确定购买数量事件;
		DialogInterface.OnClickListener onOK = new DialogInterface.OnClickListener() {
			
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				// 获取选中行中的手机数量；
				String numStr = numText.getText().toString();
				try {
					CartDialog.this.cart.add(_sale, new Integer(numStr));
				} catch (NumberFormatException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				msg.what=CartDialog.ADD;
				handler.sendMessage(msg);
			}
		};
		builder.setPositiveButton("确定", onOK);
		builder.setNegativeButton("取消", null);
	}

	public void change(JSONObject sale) {
		try {
		final JSONObject _sale=sale;
		final String id = _sale.getString("id");
		builder = new AlertDialog.Builder(this.context);
		numText = new EditText(this.context);
		numText.setInputType(InputType.TYPE_CLASS_PHONE);
		numText.setHint("输入0则删除该物品");
		builder.setTitle("修改数量：");
		JSONObject old_sale=new JSONObject(cart.get(_sale.getString("id")));
		builder.setMessage("您的购物车中已存在此商品" + old_sale.getString("number") + "件");
		builder.setView(numText);
		// 创建响应确定购买数量事件;
		DialogInterface.OnClickListener onOK = new DialogInterface.OnClickListener() {
			
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				String numStr = numText.getText().toString();
				try {
					CartDialog.this.cart.update(_sale.getString("id"), new Integer(numStr));
				} catch (NumberFormatException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				msg.what=CartDialog.CHANGE;
				handler.sendMessage(msg);
			}

		};
		builder.setPositiveButton("确定", onOK);
		builder.setNegativeButton("取消", null);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void change(JSONObject sale,String title) {
		this.change(sale);
		this.setTitle(title);
	}
	
	public void add(JSONObject sale,String title){
		this.add(sale);
		this.setTitle(title);
	}
	
	public void clear(){
		builder = new AlertDialog.Builder(this.context);
		builder.setTitle("清空购物车");
		builder.setMessage("是否清空购物车?");
		DialogInterface.OnClickListener onOK = new DialogInterface.OnClickListener() {
			
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				CartDialog.this.cart.clear();
				msg.what=CartDialog.CLEAR;
				handler.sendMessage(msg);
			}

		};
		builder.setPositiveButton("确定", onOK );
		builder.setNegativeButton("取消", null);
		
	}
	
	public void setTitle(String title){
		builder.setTitle(title);
	}

	public void show() {
		dialog = builder.create();
		dialog.show();
	}

}
