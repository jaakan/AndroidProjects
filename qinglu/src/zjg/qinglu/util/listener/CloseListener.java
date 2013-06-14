/**
 * 
 */
package zjg.qinglu.util.listener;

import android.app.Activity;
import android.view.View;
import android.view.View.OnClickListener;

// ����һ��������ǰActivity��
public class CloseListener implements OnClickListener
{
	private Activity activity;
	public CloseListener(Activity activity)
	{
		this.activity = activity;
	}

	public void onClick(View source)
	{
		// ������ǰActivity
		activity.finish();
	}
}
