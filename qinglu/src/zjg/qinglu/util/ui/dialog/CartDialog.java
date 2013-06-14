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
		builder.setTitle("���빺��������");
		builder.setView(numText);
		// ������Ӧȷ�����������¼�;
		DialogInterface.OnClickListener onOK = new DialogInterface.OnClickListener() {
			
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				// ��ȡѡ�����е��ֻ�������
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
		builder.setPositiveButton("ȷ��", onOK);
		builder.setNegativeButton("ȡ��", null);
	}

	public void change(JSONObject sale) {
		try {
		final JSONObject _sale=sale;
		final String id = _sale.getString("id");
		builder = new AlertDialog.Builder(this.context);
		numText = new EditText(this.context);
		numText.setInputType(InputType.TYPE_CLASS_PHONE);
		numText.setHint("����0��ɾ������Ʒ");
		builder.setTitle("�޸�������");
		JSONObject old_sale=new JSONObject(cart.get(_sale.getString("id")));
		builder.setMessage("���Ĺ��ﳵ���Ѵ��ڴ���Ʒ" + old_sale.getString("number") + "��");
		builder.setView(numText);
		// ������Ӧȷ�����������¼�;
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
		builder.setPositiveButton("ȷ��", onOK);
		builder.setNegativeButton("ȡ��", null);
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
		builder.setTitle("��չ��ﳵ");
		builder.setMessage("�Ƿ���չ��ﳵ?");
		DialogInterface.OnClickListener onOK = new DialogInterface.OnClickListener() {
			
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				CartDialog.this.cart.clear();
				msg.what=CartDialog.CLEAR;
				handler.sendMessage(msg);
			}

		};
		builder.setPositiveButton("ȷ��", onOK );
		builder.setNegativeButton("ȡ��", null);
		
	}
	
	public void setTitle(String title){
		builder.setTitle(title);
	}

	public void show() {
		dialog = builder.create();
		dialog.show();
	}

}
