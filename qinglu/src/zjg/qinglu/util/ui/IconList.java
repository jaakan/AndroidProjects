package zjg.qinglu.util.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class IconList {
	private Activity activity=null;
	int ViewGroupId;
	private ArrayList<ViewGroup> icons=null;
	public IconList(Activity activity,int ViewGroupId){
		icons=new ArrayList<ViewGroup>();
		this.activity=activity;
		this.ViewGroupId=ViewGroupId;
	}
	
	public void addIcon(String name,int imageId){
		ViewGroup icon=(ViewGroup) this.activity.findViewById(this.ViewGroupId);
		ImageView image=(ImageView) this.activity.findViewById(imageId);
	}

}