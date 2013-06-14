package zjg.qinglu.order;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import zjg.qinglu.R;
import zjg.qinglu.order.util.CartUtil;
import zjg.qinglu.util.AppInfo;
import zjg.qinglu.util.net.HttpUtil;
import zjg.qinglu.util.net.MessageWhat;
import zjg.qinglu.util.ui.DataGrid;
import zjg.qinglu.util.ui.ProgressViewHandler;
import zjg.qinglu.util.ui.Row;
import zjg.qinglu.util.ui.dialog.CartDialog;
import zjg.qinglu.util.ui.dialog.DialogUtil;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class Cart extends Activity {
	private Button clearBtn;
	private Button submitBtn;
	private DataGrid dataGrid;
	private CartDialog cart;
	private CartUtil cartUtil;
	private ProgressViewHandler pvh = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.order_cart);
		cartUtil = new CartUtil(this);
		initUI();
		try {
			loadCartData();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void initUI() {
		cart = new CartDialog(this);
		// ��ʼ������ύ��ť��ע��������ύ�¼�;
		this.clearBtn = (Button) this.findViewById(R.id.cart_clear);
		this.submitBtn = (Button) this.findViewById(R.id.cart_submit);
		// ��չ��ﳵ
		this.clearBtn.setOnClickListener(new OnClearCartListener());
		// �ύ
		this.submitBtn.setOnClickListener(new OnSubmitListener());
		// ��ʼ��DataGrid��������ع��ﳵ���ݣ�
		dataGrid = (DataGrid) this.findViewById(R.id.order_cart_datagrid);
		// ����ע��onSelectRow�¼�����������;
		dataGrid.onSelectRow(new OnSelectRowListener());
		// ���ط�ҳ����
		dataGrid.getPager().setVisibility(View.GONE);

	}

	/**
	 * �����ʼ�����빺�ﳵ���ݷ���
	 * 
	 * @throws JSONException
	 **/
	private void loadCartData() throws JSONException {
		// ��cart����ת����JSONObject��ʽ�ַ�����
		JSONObject json = new JSONObject();
		JSONArray rows = new JSONArray();
		List<HashMap<String, String>> list = cartUtil.list();
		int _total = 0;
		for (HashMap<String, String> map : list) {
			// Log.d("Cart#loadCartData:", "hasNext");
			_total++;
			JSONObject sale = new JSONObject(map);
			sale.put("type_name", map.get("type"));
			rows.put(sale);
		}
		String total = Integer.toString(_total);
		json.put("total", total);
		json.put("rows", rows);
		String[] filters = { "name", "price", "type_name", "number" };
		String[] hTexts = { "��Ʒ����", "����", "����", "����" };
		this.dataGrid.loadData(filters, hTexts, json, 1000);

	}

	// ��������Ӧ�ύ;
	public void submitResult(String result) {
		JSONObject _result = null;
		JSONArray errorList = new JSONArray();
		JSONArray failList = new JSONArray();
		JSONArray successList = new JSONArray();

		if (result != null) {
			try {
				_result = new JSONObject(result);
				errorList = _result.getJSONArray("errorList");
				failList = _result.getJSONArray("failList");
				successList = _result.getJSONArray("successList");

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (successList == null) {
			DialogUtil.showDialog(this, "�ϴ�ʧ��", false);
		} else {

			if (errorList != null && errorList.length() > 0) {
				String error = "��������";
				for (int i = 0; i < errorList.length(); i++) {
					try {
						error += (i + 1) + "." + errorList.getString(i);
						DialogUtil.showDialog(this, error, false);
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}

			if (failList != null && failList.length() > 0) {
				String fail = "������Ʒ�ύʧ�ܣ��������ύ";
				DialogUtil.showDialog(this, fail, false);
				// ����Ѿ��ύ�ɹ��Ļ���;
				for (int i = 0; i < successList.length(); i++) {
					try {
						String id = successList.getString(i);
						cartUtil.remove(id);
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			} else {
				cartUtil.clear();
			}
			try {
				this.loadCartData();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/*--------------------------�����Ǽ�������-------------------------*/
	/** ��������¼������� ***/
	class OnClearCartListener implements View.OnClickListener {

		public void onClick(View v) {
			// ��չ��ﳵ����պ���¼���cart��setHandler�ж��壻

			if (cartUtil.list().size() > 0) {
				CartDialog cart = new CartDialog(Cart.this);
				cart.setHandler(new Handler() {
					@Override
					public void handleMessage(Message msg) {
						switch (msg.what) {
						case CartDialog.CLEAR:
							try {
								Cart.this.loadCartData();
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}

					}
				});
				cart.clear();
				cart.show();
			} else {
				DialogUtil.showDialog(Cart.this, "���ﳵ��������", false);
			}

		}

	}

	/** �����ύ�¼������� ***/
	class OnSubmitListener implements View.OnClickListener {

		public void onClick(View v) {

			OnClickListener listener = new OnClickListener() {
				public void onClick(DialogInterface arg0, int arg1) {
					// TODO Auto-generated method stub
					// TODO Auto-generated method stub
					Cart.this.pvh = new ProgressViewHandler(Cart.this);
					pvh.createProgressDialog("�����ύ");
					String url = AppInfo.BASE_URL + "order.do";
					HashMap<String, String> req = new HashMap<String, String>();
					JSONArray orderData = new JSONArray();
					List<HashMap<String, String>> list = cartUtil.list();
					for (HashMap<String, String> m : list) {
						JSONObject obj = new JSONObject();
						try {
							obj.put("id", m.get("id"));
							obj.put("number", m.get("number"));
							orderData.put(obj);
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					req.put("action", "submitOrder");
					req.put("orderData", orderData.toString());
					final HttpUtil http = new HttpUtil();
					http.postRequest(url, req, new Handler() {
						public void handleMessage(Message msg) {
							switch (msg.what) {
							case MessageWhat.NET_CONNECT_SUCCESS:
								pvh.removeProgressDialog();
								String result = http.getResult();
								Cart.this.submitResult(result);
								// Log.d("Cart#submit#result:", result);
								break;
							case MessageWhat.NET_CONNECT_FAIL:
								pvh.removeProgressDialog();
								DialogUtil.showDialog(Cart.this, "�ύʧ��", false);
								break;
							case MessageWhat.NET_CONNECT_OUTTIME:
								pvh.removeProgressDialog();
								DialogUtil.showDialog(Cart.this, "���ӳ�ʱ", false);
								break;
							}
						}
					}, 10000);
				}
			};
			if (cartUtil.list().size() > 0) {
				DialogUtil.showDialog(Cart.this, "ȷ���ύ��", listener);
			} else {
				DialogUtil.showDialog(Cart.this, "���ﳵ��������,�����", false);
			}

		}

	}

	/** ���嵥���е��¼������� **/
	class OnSelectRowListener implements View.OnClickListener {

		public void onClick(View v) {
			// TODO Auto-generated method stub
			Row row = (Row) v;
			JSONObject data = row.getData();
			CartDialog cart = new CartDialog(Cart.this);
			cart.setHandler(new Handler() {
				@Override
				public void handleMessage(Message msg) {
					switch (msg.what) {
					case CartDialog.CHANGE:
						try {
							Cart.this.loadCartData();
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						break;
					}
				}
			});
			try {
				cart.change(data, "�޸Ķ���" + data.getString("name") + "������");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			cart.show();
		}

	}

}
