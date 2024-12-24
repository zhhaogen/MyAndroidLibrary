package cn.zhg.base;

import android.annotation.SuppressLint;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PermissionInfo;
import android.os.Build;

import androidx.core.app.ActivityCompat;

import java.util.ArrayList;
import java.util.List;

public class BasicActivity extends BaseActivity {
    /**
     * support 6.0<br>
     * 检查授权,如果全部允许则执行@link{#OnPermissionsAccess},否则执行@link{#OnPermissionsDenied}<br>
     */
    public void checkPermission() {
       if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            this.onPermissionsAccess();
            return;
        }
        PackageInfo pp;
        PackageManager pm = this.getPackageManager();
        try {
            pp = pm.getPackageInfo(this.getPackageName(),
                    PackageManager.GET_PERMISSIONS);
        } catch (Exception igr) {
            this.onPermissionsAccess();
            return;
        }
        String[] ps = pp.requestedPermissions;
        if (ps == null || ps.length == 0) {
            this.onPermissionsAccess();
            return;
        }
        ArrayList<String> cs = new ArrayList<String>();
        for (String p : ps) {
            if (checkNeedRequestPermission(pm, p)) {
                cs.add(p);
            }
        }
        if (cs.isEmpty()) {
            this.onPermissionsAccess();
            return;
        }
        String chc[] = new String[cs.size()];
        cs.toArray(chc);
        ActivityCompat.requestPermissions(this, chc, 2);
    }

    /**
     * 所有权限被允许
     */
    protected void onPermissionsAccess() {

    }

    /**
     * 权限被拒绝
     *
     * @param permissions 被拒绝的权限列表
     */
    protected void onPermissionsDenied(List<PermissionInfo> permissions) {
    }

    @SuppressLint("MissingSuperCall")
   public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults == null || permissions == null) {
            onPermissionsAccess();
            return;
        }
        boolean isPermission = true;
        PackageManager pm = getPackageManager();
        List<PermissionInfo> infos = new ArrayList<>();
        for (int i = 0; i < grantResults.length; i++) {
            if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                try {
                    PermissionInfo info = pm.getPermissionInfo(
                            permissions[i], PackageManager.GET_META_DATA);
                    infos.add(info);
                } catch (Exception igr) {

                }
                isPermission = false;
            }
        }
        if (isPermission) {
            onPermissionsAccess();
            return;
        }
        onPermissionsDenied(infos);
    }
	/**
     * 检查权限是否需要被授权,危险的和未允许
     * @param pm
     * @param name
     */
    private boolean checkNeedRequestPermission(PackageManager pm, String name)
    {
        try
        {
            PermissionInfo info = pm.getPermissionInfo(name,
                    PackageManager.GET_META_DATA);
            if(Build.VERSION.SDK_INT<Build.VERSION_CODES.P){
                return (info.protectionLevel == PermissionInfo.PROTECTION_DANGEROUS
                        && ActivityCompat.checkSelfPermission(this,
                        name) == PackageManager.PERMISSION_DENIED);
            }
            return (info.getProtection() == PermissionInfo.PROTECTION_DANGEROUS
                    && ActivityCompat.checkSelfPermission(this,
                    name) == PackageManager.PERMISSION_DENIED);
        } catch (Throwable igr)
        {

        }
        //未知的权限,可能是更高级别的api的权限
        return false;
    }
}
