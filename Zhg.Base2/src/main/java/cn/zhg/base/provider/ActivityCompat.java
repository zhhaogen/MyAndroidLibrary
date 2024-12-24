package cn.zhg.base.provider;

import android.app.Activity;
import android.content.Context;

public class ActivityCompat extends androidx.core.app.ActivityCompat
{
	public static int checkSelfPermission( Context context,  String permission) {
		  return androidx.core.app.ActivityCompat.checkSelfPermission(context, permission);
	  }

	public static void requestPermissions(Activity activity,
			String[] permissions, int requestCode)
	{
		androidx.core.app.ActivityCompat.requestPermissions(activity, permissions,requestCode);
	}
}
