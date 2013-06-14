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
		// 初始清除和提交按钮并注册清除和提交事件;
		this.clearBtn = (Button) this.findViewById(R.id.cart_clear);
		this.submitBtn = (Button) this.findViewById(R.id.cart_submit);
		// 清空购物车
		this.clearBtn.setOnClickListener(new OnClearCartListener());
		// 提交
		this.submitBtn.setOnClickListener(new OnSubmitListener());
		// 初始化DataGrid组件，加载购物车数据；
		dataGrid = (DataGrid) this.findViewById(R.id.order_cart_datagrid);
		// 需先注册onSelectRow事件再载入数据;
		dataGrid.onSelectRow(new OnSelectRowListener());
		// 隐藏分页器；
		dataGrid.getPager().setVisibility(View.GONE);

	}

	/**
	 * 定义初始化载入购物车数据方法
	 * 
	 * @throws JSONException
	 **/
	private void loadCartData() throws JSONException {
		// 将cart数据转换成JSONObject格式字符串；
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
		String[] hTexts = { "商品名称", "单价", "分类", "数量" };
		this.dataGrid.loadData(filters, hTexts, json, 1000);

	}

	// 服务器响应提交;
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
			DialogUtil.showDialog(this, "上传失败", false);
		} else {

			if (errorList != null && errorList.length() > 0) {
				String error = "发生错误：";
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
				String fail = "部分商品提交失败，请重新提交";
				DialogUtil.showDialog(this, fail, false);
				// 清除已经提交成功的货物;
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

	/*--------------------------以下是监听器类-------------------------*/
	/** 定义清空事件监听器 ***/
	class OnClearCartListener implements View.OnClickListener {

		public void onClick(View v) {
			// 清空购物车；清空后的事件再cart的setHandler中定义；

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
				DialogUtil.showDialog(Cart.this, "购物车中无数据", false);
			}

		}

	}

	/** 定义提交事件监听器 ***/
	class OnSubmitListener implements View.OnClickListener {

		public void onClick(View v) {

			OnClickListener listener = new OnClickListener() {
				public void onClick(DialogInterface arg0, int arg1) {
					// TODO Auto-generated method stub
					// TODO Auto-generated method stub
					Cart.this.pvh = new ProgressViewHandler(Cart.this);
					pvh.createProgressDialog("正在提交");
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
								DialogUtil.showDialog(Cart.this, "提交失败", false);
								break;
							case MessageWhat.NET_CONNECT_OUTTIME:
								pvh.removeProgressDialog();
								DialogUtil.showDialog(Cart.this, "连接超时", false);
								break;
							}
						}
					}, 10000);
				}
			};
			if (cartUtil.list().size() > 0) {
				DialogUtil.showDialog(Cart.this, "确认提交？", listener);
			} else {
				DialogUtil.showDialog(Cart.this, "购物车中无数据,请添加", false);
			}

		}

	}

	/** 定义单击行的事件监听器 **/
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
				cart.change(data, "修改订购" + data.getString("name") + "的数量");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			cart.show();
		}

	}

}
