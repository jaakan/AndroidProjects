package zjg.qinglu.order;

import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import zjg.qinglu.R;
import zjg.qinglu.order.Index.LoadSaleDataHandler;
import zjg.qinglu.util.AppInfo;
import zjg.qinglu.util.MySession;
import zjg.qinglu.util.net.HttpUtil;
import zjg.qinglu.util.net.MessageWhat;
import zjg.qinglu.util.ui.DataGrid;
import zjg.qinglu.util.ui.PagerCtrl;
import zjg.qinglu.util.ui.ProgressViewHandler;
import zjg.qinglu.util.ui.dialog.DialogUtil;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;

public class Record extends Activity {
	private DataGrid dataGrid = null;
	private int pageSize = 15;
	private ProgressViewHandler pvh = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.setContentView(R.layout.order_record);
		initUI();
	}

	private void initUI() {
		// ��ʼ��������DataGrid���;
		dataGrid = (DataGrid) this.findViewById(R.id.order_record_datagrid);
		dataGrid.onSelectRow(new OnSelectRowListener());
		// ���ص�һҳ����;
		loadSaleData(1, pageSize);
		// ��ȡ��ҳ�������÷���;
		final PagerCtrl pager = this.dataGrid.getPager();
		// ����Next
		this.dataGrid.clickNext(new View.OnClickListener() {

		
			public void onClick(View v) {
				// TODO Auto-generated method stub
				int p = pager.getPage();
				if (p!= pager.getPages()) {
					pager.setPage(p+1);
					loadSaleData(pager.getPage(), pageSize);
				}

			}
		});
		// ����pre
		this.dataGrid.clickPre(new View.OnClickListener() {

			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				int p = pager.getPage();
				Log.d("Index#clickNext:",
						new Integer(pager.getPage()).toString());
				if (p!= 1) {
					pager.setPage(p-1);
					loadSaleData(pager.getPage(), pageSize);
				}

			}
		});
	}

	// �����������ݵ���񷽷�;
	private void loadSaleData(int index, int pageSize) {
		// ����������Ʒ����;

		pvh = new ProgressViewHandler(this);
		pvh.createProgressDialog("��������");
		String url = AppInfo.BASE_URL + "order.do";
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("action", "getRecord");
		map.put("page", new Integer(index).toString());
		map.put("rows", new Integer(pageSize).toString());
		final HttpUtil httpUtil = new HttpUtil();
		httpUtil.postRequest(url, map, new LoadSaleDataHandler(httpUtil), 10000);

	}

	/************************** ������ ********************************/
	/****** ѡ��DataGrid��ĳ�е��¼������� ***************/
	class OnSelectRowListener implements View.OnClickListener {

		
		public void onClick(View arg0) {
			// TODO Auto-generated method stub

		}
	}

	/************************* �Զ���Handler�� *************************/
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
				DialogUtil.showDialog(Record.this, "����ʧ�ܣ����Ժ�����", false);
				break;
			case MessageWhat.NET_CONNECT_OUTTIME:
				pvh.removeProgressDialog();
				DialogUtil.showDialog(Record.this, "���ӳ�ʱ�����Ժ�����", false);
				break;
			case MessageWhat.NET_CONNECT_SUCCESS:
				pvh.removeProgressDialog();
				// ���ӳɹ������ؽ����ݼ�����DataGrid;
				String result = httpUtil.getResult();
				//Log.d("Index##loadSaleData:", result);
				if (result != null) {
					JSONObject json;
					try {
						json = new JSONObject(result);
						putDataToDataGrid(json);
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						DialogUtil.showDialog(Record.this, "��������Ӧ����", false);
					}

				} else {
					DialogUtil.showDialog(Record.this, "��������Ӧ����", false);
				}

				break;
			}
		}

		// ��sale���ݼ���DataGrid
		private void putDataToDataGrid(JSONObject json) {
			// TODO Auto-generated method stub
			String[] filters = { "sale_name", "time", "status", "sale_number" };
			String[] hTexts = { "��Ʒ����", "�µ�ʱ��", "״̬", "��Ʒ����" };
			JSONObject header = new JSONObject();
			for (int i = 0; i < filters.length; i++) {
				try {
					header.put(filters[i], hTexts[i]);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			Record.this.dataGrid.loadData(filters, header, json, pageSize);
		}
	}

}
