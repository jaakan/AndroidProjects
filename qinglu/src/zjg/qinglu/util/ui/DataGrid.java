package zjg.qinglu.util.ui;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import zjg.qinglu.R;
import zjg.qinglu.util.ScreenInfo;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;


public class DataGrid extends LinearLayout{

	private PagerCtrl pager;
	private Context context;
	private LinearLayout body;
	private Row tableTitle;
	private Row selectRow;
	private View.OnClickListener listener;
	private View.OnClickListener next;
	private View.OnClickListener pre;
	private int rowHeight;
	public DataGrid(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		this.context=context;
		rowHeight=ScreenInfo.height/15;
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.datagrid, this);
		pager=(PagerCtrl) this.findViewById(R.id.pager);
		body=(LinearLayout) this.findViewById(R.id.datagrid_body);
		tableTitle=(Row) this.findViewById(R.id.datagrid_title);
		
	}
	public void loadData(String[] filters,JSONObject header,JSONObject json,int pageSize){
		body.removeAllViews();
		try {
			int total=json.getInt("total");
			JSONArray rows=json.getJSONArray("rows");
			//设置分页器;
			this.pager.init(total,pageSize);
			//设置表格标题;
			tableTitle.loadData(filters, header);
			for(int i=0;i<tableTitle.getChildCount();i++){
				View v=tableTitle.getChildAt(i);
				if(v instanceof TextView){
					((TextView) v).setTextAppearance(context, R.style.row_text_title);
				}
			}
			for(int i=0;i<rows.length();i++){
				JSONObject obj=rows.getJSONObject(i);
				Row row=new Row(this.context,null);
				if(i%2==0){
					row.setBackgroundResource(R.drawable.row1);
				}else{
					row.setBackgroundResource(R.drawable.row2);
				}
				
				row.setLayoutParams(new LinearLayout.LayoutParams(
						LayoutParams.FILL_PARENT,
						this.rowHeight
						));
				row.setPadding(0, 2, 0, 0);
				row.loadData(filters, obj);
				row.setGravity(Gravity.CENTER);
				row.setOnClickListener(this.listener);
				body.addView(row);
			}
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void loadData(String[] filters,String[] titleNames,JSONObject json,int pageSize){
		JSONObject header = new JSONObject();
		for (int i = 0; i < filters.length; i++) {
			try {
				header.put(filters[i], titleNames[i]);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		this.loadData(filters, header, json, pageSize);
	}
	
	public PagerCtrl getPager(){
		return this.pager;
	}
	
	//定义选中Row方法：
	public void onSelectRow(View.OnClickListener listener){
		this.listener=listener;
	}
	
	public void clickNext(View.OnClickListener listener){
		this.next=listener;
		this.pager.nextBtn.setOnClickListener(this.next);
	
		
	}
	
	public void clickPre(View.OnClickListener listener){
		this.pre=listener;
		this.pager.preBtn.setOnClickListener(this.pre);
	
	}

}
