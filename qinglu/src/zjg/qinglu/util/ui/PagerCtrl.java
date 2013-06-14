package zjg.qinglu.util.ui;

import zjg.qinglu.R;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class PagerCtrl extends LinearLayout{
	//·ÖÒ³Æ÷Ïà¹Ø£»
	private int page=1;
	private int pageSize=8;
	private int total=0;
	private int pages=1;
	private TextView pagerText;
	public ImageView preBtn;
	public ImageView nextBtn;
	
	public PagerCtrl(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		LayoutInflater inflater=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.pager, this);
		preBtn=(ImageView) this.findViewById(R.id.pre_btn);
		nextBtn=(ImageView) this.findViewById(R.id.next_btn);
		this.pagerText= (TextView) this.findViewById(R.id.pager_text);
	}
	
	public void init(int total,int size){
		this.setTotal(total);
		this.setPageSize(size);
		this.pagerText.setText(this.getPage()+"/"+this.getPages());
		Log.d("PagerCtrl#init:", new Integer(this.page).toString());
	}
	
	
	public void setTotal(int total){
		this.total=total;
		Log.d("PagerCtrl#setTotal:", new Integer(this.page).toString());
	}
	public int getTotal(){
		return this.total;
	}
	
	public void setPage(int page){
		if(page>this.getPages()){
			page=this.getPages();
		}
		if(page<1){
			page=1;
		}
		this.page=page;
		this.pagerText.setText(this.getPage()+"/"+this.getPages());
	}
	
	public int getPage(){
		if(page>this.getPages()){
			page=this.getPages();
		}
		if(page<1){
			page=1;
		}
		return page;
	}
	
	public void setPageSize(int size){
		if(size<1){
			size=1;
		}
		
		this.pages=(int)(this.getTotal()/this.getPageSize());
		if(this.getTotal()%this.getPageSize()!=0){
			this.pages+=1;
		}
		this.pageSize=size;
	}
	
	public int getPageSize(){
		return this.pageSize;
	}
	
	public int getPages(){
		this.pages=(int)(this.getTotal()/this.getPageSize());
		if(this.getTotal()%this.getPageSize()!=0){
			this.pages+=1;
		}
		return this.pages;
	}
	
}
