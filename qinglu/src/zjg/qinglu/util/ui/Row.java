package zjg.qinglu.util.ui;

import org.json.JSONException;
import org.json.JSONObject;

import zjg.qinglu.R;
import zjg.qinglu.util.ScreenInfo;
import android.content.Context;
import android.content.res.XmlResourceParser;
import android.util.AttributeSet;
import android.util.Xml;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class Row extends LinearLayout{

	private Context context;
	private JSONObject data;
	public Row(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		this.context=context;
		LayoutInflater inflater=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.row, this);
	}
	
	public void loadData(String[] titles,JSONObject json){
		this.data=json;
		this.removeAllViews();
		int len=titles.length;
		for(String key:titles){
			try {
				String text=json.getString(key);
				TextView textV=new TextView(context);
				//…Ë÷√text—˘ Ω;
				textV.setTextAppearance(context, R.style.row_text_body);
				textV.setGravity(Gravity.CENTER);
				textV.setWidth(ScreenInfo.width/len);
				textV.setText(text);
				this.addView(textV);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
	}
	
	public JSONObject getData(){
		return this.data;
	}
	

}
