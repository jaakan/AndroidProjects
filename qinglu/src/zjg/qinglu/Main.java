package zjg.qinglu;

import java.util.HashMap;

import zjg.qinglu.util.MySession;
import zjg.qinglu.util.ScreenInfo;
import zjg.qinglu.util.ui.IconAdapter;

import android.app.ActivityGroup;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.AdapterView.OnItemClickListener;

public class Main extends ActivityGroup {

	private LinearLayout container = null;// 装载sub Activity的容器
	private GridView bar;
	private HashMap<String, String> session;
	private int width=ScreenInfo.width;
	private int height=ScreenInfo.height;
	private IconAdapter imgAdapter=null;
	
	// 按钮图片;
	int[] barImageArray = {
			R.drawable.bottom_index,
			R.drawable.bottom_cart,
			//R.drawable.bottom_search,
			R.drawable.bottom_order
	};
	// 按钮文字;
	String[] barTextArray = { 
			"",
			"", 
			//"", 
			"" };

	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 隐藏标题栏
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// 设置视图
		setContentView(R.layout.main);
		container = (LinearLayout) findViewById(R.id.containerBody);
		
		bar = (GridView) this.findViewById(R.id.main_bar);
		bar.setNumColumns(barImageArray.length);// 设置每行列数
		bar.setSelector(new ColorDrawable(Color.TRANSPARENT));// 选中的时候为透明色
		bar.setGravity(Gravity.CENTER);// 位置居中
		bar.setVerticalSpacing(0);// 垂直间隔
		int _width = width/this.barImageArray.length;
		int _height=ScreenInfo.Dp2Px(this, 50);
		imgAdapter = new IconAdapter(this, barImageArray, barTextArray, _width,
				_height, R.drawable.rect2,true);
		bar.setAdapter(imgAdapter);// 设置菜单Adapter

		// 添加项目点击事件
		bar.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				SwitchActivity(arg2);
			}

		});
		SwitchActivity(0);// 默认打开第0页 ;
	}

	private void SwitchActivity(int id) {
		// TODO Auto-generated method stub
		imgAdapter.SetFocus(id);// 选中项获得高亮
		container.removeAllViews();// 必须先清除容器中所有的View
		Intent intent = null;
		switch (id) {
		case 0:
			intent = new Intent(Main.this, zjg.qinglu.order.Index.class);
			break;
		case 1:
			intent = new Intent(Main.this, zjg.qinglu.order.Cart.class);
			break;
			/*
		case 2:
			Bundle searchBundle=new Bundle();
			HashMap<String,String> reqParams=new HashMap<String,String>();
			reqParams.putAll((HashMap<String, String>) MySession.getSession("user"));
			reqParams.put("action", "init");
			searchBundle.putSerializable("reqParams",reqParams);
			intent = new Intent(Main.this, zjg.qinglu.order.Search.class);
			intent.putExtra("searchBundle",searchBundle);
			break;
			*/
		case 2:
			intent = new Intent(Main.this, zjg.qinglu.order.Record.class);
			break;
		}
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		// Activity 转为 View
		Window subActivity = getLocalActivityManager().startActivity(
				"subActivity", intent);
		// 容器添加View
		container.addView(subActivity.getDecorView(), LayoutParams.FILL_PARENT,
				LayoutParams.FILL_PARENT);

	}

	
	public void loadActivity(String tag, Class<?> _class) {
		container.removeAllViews();
		container.addView(getLocalActivityManager().startActivity(
				tag,
				new Intent(this, _class)
						.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
				.getDecorView());
	}
	
	//按键监听;
    @Override 
    public boolean onKeyDown(int keyCode, KeyEvent event) {
    	switch (keyCode) { 
   		case KeyEvent.KEYCODE_BACK:
   		//隐藏、显示底栏bottom;
   			exitDialog();
	        return false; 
	    case KeyEvent.KEYCODE_MENU:
	        return false;
    	}
    	return super.onKeyDown(keyCode, event);
   }
	//定义退出dialog方法
	public void exitDialog(){
    	final Builder builder=new AlertDialog.Builder(this);
    	builder.setIcon(R.drawable.ic_exit);
    	builder.setTitle("退出！");
    	builder.setMessage("确认退出？");
    	//响应确定事件；
    	DialogInterface.OnClickListener doExit=new DialogInterface.OnClickListener(){
		
			public void onClick(DialogInterface arg0, int arg1) {
				// TODO Auto-generated method stub
				Main.this.finish();
			}
    		
    	};
    	builder.setPositiveButton("确定",doExit );
    	builder.setNeutralButton("取消", null);
    	
    	final AlertDialog alert= builder.create();
    	alert.show();
    }

}
