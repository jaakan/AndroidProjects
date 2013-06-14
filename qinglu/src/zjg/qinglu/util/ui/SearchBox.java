package zjg.qinglu.util.ui;

import zjg.qinglu.R;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

public class SearchBox extends LinearLayout {
	public  EditText text=null;
	public  Button button=null;
	public SearchBox(Context context) {
		super(context);
		new SearchBox(context,null);
	}

	public SearchBox(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		LayoutInflater inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.searchbox, this);
		text=(EditText) this.findViewById(R.id.searchbox_text);
		button=(Button) this.findViewById(R.id.searchbox_button);
	}
	
	public void setSearchListener(View.OnClickListener listener){
		button.setOnClickListener(listener);
	}
}
