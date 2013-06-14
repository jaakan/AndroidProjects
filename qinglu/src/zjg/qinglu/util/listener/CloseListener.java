/**
 * 
 */
package zjg.qinglu.util.listener;

import android.app.Activity;
import android.view.View;
import android.view.View.OnClickListener;

// 定义一个结束当前Activity的
public class CloseListener implements OnClickListener
{
	private Activity activity;
	public CloseListener(Activity activity)
	{
		this.activity = activity;
	}

	public void onClick(View source)
	{
		// 结束当前Activity
		activity.finish();
	}
}
