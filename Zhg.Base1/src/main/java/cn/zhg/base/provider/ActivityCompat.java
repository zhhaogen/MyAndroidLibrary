package cn.zhg.base.provider;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Process; 

public class ActivityCompat
{
	  public static int checkSelfPermission( Context context,  String permission) {
		  return context.checkPermission(permission, android.os.Process.myPid(), Process.myUid());
	  }

	public static void requestPermissions(Activity activity,
			String[] permissions, int requestCode)
	{
		if (Build.VERSION.SDK_INT >= 23)
		{
			activity.requestPermissions(permissions, requestCode);
		}
	}
}
