package zjg.qinglu.util.ui;
import zjg.qinglu.R;
import android.app.ProgressDialog;  
import android.content.Context;  
import android.widget.LinearLayout;  
import android.widget.TextView;  
public class ProgressViewHandler {  
	private ProgressDialog dlg=null;
	private Context context=null;
	private LinearLayout root=null;
	TextView alert = null;
	public ProgressViewHandler(Context context){
		dlg= new ProgressDialog(context);
		this.context=context;
	}
	
	public ProgressDialog createProgressDialog(String text){
		alert=new TextView(context);
		dlg.show();  
		dlg.setContentView(R.layout.loading);
		dlg.setCancelable(false);
		root=(LinearLayout)dlg.findViewById(R.id.progressDialog);  
		root.setGravity(android.view.Gravity.CENTER);  
		LoadingView mLoadView = new LoadingView(context);  
		mLoadView.setDrawableResId(R.drawable.loading);  
		root.addView(mLoadView); 
		root.addView(alert);
		this.setAlertText(text);
		return dlg;
	}
	
	public void setAlertText(String text){
		  
		alert.setText(text);  

	}
	
	public  void removeProgressDialog(){
		dlg.cancel();
	}
	
	public void hideProgressDialog(){
		dlg.cancel();
		dlg= new ProgressDialog(context);
	}
}  