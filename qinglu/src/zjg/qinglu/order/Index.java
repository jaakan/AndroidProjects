package zjg.qinglu.order;

import java.util.HashMap;
import org.json.JSONException;
import org.json.JSONObject;

import zjg.qinglu.R;
import zjg.qinglu.util.AppInfo;
import zjg.qinglu.util.MySession;
import zjg.qinglu.util.net.HttpUtil;
import zjg.qinglu.util.net.MessageWhat;
import zjg.qinglu.util.ui.DataGrid;
import zjg.qinglu.util.ui.PagerCtrl;
import zjg.qinglu.util.ui.ProgressViewHandler;
import zjg.qinglu.util.ui.Row;
import zjg.qinglu.util.ui.SearchBox;
import zjg.qinglu.util.ui.dialog.CartDialog;
import zjg.qinglu.util.ui.dialog.DialogUtil;

import android.app.Activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

public class Index extends Activity {

	private ProgressViewHandler pvh = null;
	private SearchBox searchBox = null;
	private DataGrid datagrid;
	private int pageSize = 15;
	private String ACTION="getAll";
	private String keyWord=null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 初始化静态界面;
		initUI();
	}

	/**** 初始化静态界面; ****/
	private void initUI() {
		this.setContentView(R.layout.order_index);
		
		searchBox = (SearchBox) this.findViewById(R.id.searchBox);
		// 单击搜索按钮；
		searchBox.setSearchListener(new OnClickListener() {
		
			public void onClick(View arg0) {
				// click search;
				String keyWord=Index.this.searchBox.text.getText().toString();
				if(!"".equals(keyWord)){
					Index.this.keyWord=keyWord;
					Index.this.ACTION="search";
				}else{
					Index.this.ACTION="getAll";
				}
				load(1,Index.this.pageSize);
			}
		});
		// 设置DataGrid
		this.datagrid = (DataGrid) this.findViewById(R.id.order_index_datagrid);
		// 需先注册onSelectRow事件再载入数据;
		this.datagrid.onSelectRow(new View.OnClickListener() {
		
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Log.d("Index#initUI#ClickRow:", ((Row) v).getData().toString());
				// 选中后隐藏
				final Row currentRow=(Row) v;
				//初始购物车
				CartDialog cart=new CartDialog(Index.this);
				String saleName="";
				try {
					saleName = currentRow.getData().getString("name");
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				cart.setHandler(new Handler(){
					@Override
					public void handleMessage(Message msg){
						switch(msg.what){
						case CartDialog.ADD :
							currentRow.setVisibility(View.GONE);
							break;
						}
					}
				});
				cart.add(currentRow.getData(),"请输入需订购的"+saleName+"的数量");
				cart.show();
				
			}
		});
		// 加载第一页数据;
		this.load(1, pageSize);
		// 获取分页器并设置方法;
		final PagerCtrl pager = this.datagrid.getPager();
		// 单击Next
		this.datagrid.clickNext(new View.OnClickListener() {

			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				int p = pager.getPage();
				Log.d("Index#clickNext:",
						new Integer(pager.getPage()).toString());
				if(p!=pager.getPages()){
					pager.setPage(p+1);
					load(pager.getPage(), pageSize);
				}
				
			}
		});
		// 单击pre
		this.datagrid.clickPre(new View.OnClickListener() {

		
			public void onClick(View v) {
				// TODO Auto-generated method stub
				int p = pager.getPage();
				Log.d("Index#clickNext:",
						new Integer(pager.getPage()).toString());
				if(p!=1){
					pager.setPage(p-1);
					load(pager.getPage(), pageSize);
				}
				
			}
		});

	}

	// 加载网络商品数据;
	private void loadSaleData(int page, int size) {
		pvh = new ProgressViewHandler(this);
		pvh.createProgressDialog("加载数据");
		String url = AppInfo.BASE_URL + "sale.do";
		HashMap<String, String> map = (HashMap<String, String>) MySession
				.getSession("user");
		map.put("action", "onSale");
		map.put("page", new Integer(page).toString());
		map.put("rows", new Integer(size).toString());
		final HttpUtil httpUtil = new HttpUtil();
		httpUtil.postRequest(url, map, new LoadSaleDataHandler(httpUtil), 10000);
	}
	//加载搜索结果;
	private void loadSearchData(int page, int size) {
		pvh = new ProgressViewHandler(this);
		pvh.createProgressDialog("加载数据");
		String url = AppInfo.BASE_URL + "sale.do";
		HashMap<String, String> map = (HashMap<String, String>) MySession
				.getSession("user");
		map.put("action", "search");
		map.put("keyWord", keyWord);
		map.put("page", new Integer(page).toString());
		map.put("rows", new Integer(size).toString());
		final HttpUtil httpUtil = new HttpUtil();
		httpUtil.postRequest(url, map, new LoadSaleDataHandler(httpUtil), 10000);
	}
	
	//加载网络数据;
	private void load(int page, int size){
		if(ACTION.equals("getAll")){
			this.loadSaleData(page, size);
		}else if(ACTION.equals("search")){
			this.loadSearchData(page, size);
		}
	}

	class LoadSaleDataHandler extends Handler {
		private HttpUtil httpUtil;

		public LoadSaleDataHandler(HttpUtil httpUtil) {
			this.httpUtil = httpUtil;
		}

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MessageWhat.NET_CONNECT_ING:
				// donothing
				break;
			case MessageWhat.NET_CONNECT_FAIL:
				pvh.removeProgressDialog();
				DialogUtil.showDialog(Index.this, "连接失败，请稍后再试", false);
				break;
			case MessageWhat.NET_CONNECT_OUTTIME:
				pvh.removeProgressDialog();
				DialogUtil.showDialog(Index.this, "连接超时，请稍后再试", false);
				break;
			case MessageWhat.NET_CONNECT_SUCCESS:
				pvh.removeProgressDialog();
				// 连接成功，返回将数据加载至DataGrid;
				String result = httpUtil.getResult();
				Log.d("Index##loadSaleData:", result);
				if (result != null) {
					JSONObject json;
					try {
						json = new JSONObject(result);
						putDataToDataGrid(json);
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						DialogUtil.showDialog(Index.this, "服务器响应错误", false);
					}

				} else {
					DialogUtil.showDialog(Index.this, "服务器响应错误", false);
				}

				break;
			}
		}

		// 将sale数据加入DataGrid
		private void putDataToDataGrid(JSONObject json) {
			// TODO Auto-generated method stub
			String[] filters = { "name", "price", "type_name" };
			String[] hTexts = { "商品名称", "单价", "分类" };
			JSONObject header = new JSONObject();
			for (int i = 0; i < filters.length; i++) {
				try {
					header.put(filters[i], hTexts[i]);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			Index.this.datagrid.loadData(filters, header, json, pageSize);
		}

	}
}
