package zjg.qinglu.util.ui;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class IconAdapter extends BaseAdapter {
	private Context mContext;
	private LinearLayout[] iconItems;
	private int selResId;

	public IconAdapter(Context c, int[] picIds, String[] iconNames, int width,
			int height, int selResId) {
		new IconAdapter(c, picIds, iconNames, width, height, selResId, false);
	}

	public IconAdapter(Context c, int[] picIds, String[] iconNames, int width,
			int height, int selResId, boolean noName) {
		mContext = c;
		this.selResId = selResId;
		iconItems = new LinearLayout[picIds.length];
		for (int i = 0; i < picIds.length; i++) {
			iconItems[i] = new LinearLayout(mContext);
			iconItems[i].setOrientation(LinearLayout.VERTICAL);
			iconItems[i].setGravity(Gravity.CENTER_HORIZONTAL);
			// iconItems[i].setLayoutParams(new GridView.LayoutParams(width,
			// height));//设置LinearLayout宽高
			iconItems[i].setLayoutParams(new GridView.LayoutParams(width,
					height));// 设置LinearLayout宽高
			
			ImageView image = new ImageView(mContext);
			image.setAdjustViewBounds(false);
			// iconItems[i].setScaleType(ImageView.ScaleType.CENTER_CROP);
			image.setImageResource(picIds[i]);
			if (noName) {
				image.setLayoutParams(new GridView.LayoutParams(
						GridView.LayoutParams.WRAP_CONTENT, height));
				iconItems[i].addView(image);
			}else{
				image.setLayoutParams(new GridView.LayoutParams(
						GridView.LayoutParams.WRAP_CONTENT, height * 3 / 4));
				
				TextView text = new TextView(mContext);
				text.setText(iconNames[i]);
				text.setLayoutParams(new GridView.LayoutParams(
						GridView.LayoutParams.WRAP_CONTENT, height / 4));
				text.setTextColor(Color.parseColor("#13077A"));
				iconItems[i].addView(image);
				iconItems[i].addView(text);
			}

		}
	}

	public int getCount() {
		return iconItems.length;
	}

	public Object getItem(int position) {
		return position;
	}

	public long getItemId(int position) {
		return position;
	}

	/**
	 * 设置选中的效果
	 */
	public void SetFocus(int index) {
		for (int i = 0; i < iconItems.length; i++) {
			if (i != index) {
				iconItems[i].setBackgroundResource(0);// 恢复未选中的样式
			}
		}
		iconItems[index].setBackgroundResource(selResId);// 设置选中的样式
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		LinearLayout iconView;
		if (convertView == null) {
			iconView = iconItems[position];
		} else {
			iconView = (LinearLayout) convertView;
		}
		return iconView;
	}
}
