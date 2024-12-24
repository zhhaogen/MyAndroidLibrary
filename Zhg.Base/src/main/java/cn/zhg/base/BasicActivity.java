/**
 * @author zhhaogen
 * 创建于 2017年2月21日 上午10:24:33
 */
package cn.zhg.base;

import android.annotation.TargetApi;
import android.content.pm.*;
import android.os.Build;
import android.support.annotation.RequiresApi;

import java.util.*;

/**
 *
 */
public class BasicActivity extends BaseActivity
{
    protected static final int REQUEST_CODE_CHECK_PERMISSIONS = 2;

    /**
     * 请求授权,检查权限<br>
     * support 6.0
     */
    public void checkPermission()
    {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M)
        {
            this.onPermissionsAccess();
            return;
        }
        PackageInfo pp;
        PackageManager pm = this.getPackageManager();
        try
        {
            pp = pm.getPackageInfo(this.getPackageName(),
                    PackageManager.GET_PERMISSIONS);
        } catch (Exception igr)
        {
            this.onPermissionsAccess();
            return;
        }
        String[] ps = pp.requestedPermissions;
        if (ps == null || ps.length == 0)
        {
            this.onPermissionsAccess();
            return;
        }
        ArrayList<String> cs = new ArrayList<String>();
        for (String p : ps)
        {
            if (checkNeedRequestPermission(pm, p))
            {
                cs.add(p);
            }
        }
        if (cs.isEmpty())
        {
            this.onPermissionsAccess();
            return;
        }
        String chc[] = new String[cs.size()];
        cs.toArray(chc);
        checkPermissions(chc);
    }

    /**
     * 请求授权,检查权限
     */
    @TargetApi(Build.VERSION_CODES.M)
    protected void checkPermissions(String... permissions)
    {
        requestPermissions(permissions, REQUEST_CODE_CHECK_PERMISSIONS);
    }

    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (REQUEST_CODE_CHECK_PERMISSIONS != requestCode)
        {
            return;
        }
        if (grantResults == null || permissions == null)
        {
            onPermissionsAccess();
            return;
        }
        boolean isPermission = true;
        PackageManager pm = getPackageManager();
        List<PermissionInfo> infos = new ArrayList<>();
        for (int i = 0; i < grantResults.length; i++)
        {
            if (grantResults[i] == PackageManager.PERMISSION_DENIED)
            {
                try
                {
                    PermissionInfo info = pm.getPermissionInfo(
                            permissions[i], PackageManager.GET_META_DATA);
                    infos.add(info);
                } catch (Exception igr)
                {

                }
                isPermission = false;
            }
        }
        if (isPermission)
        {
            onPermissionsAccess();
            return;
        }
        onPermissionsDenied(infos);
    }

    /**
     * 所有权限被允许
     */
    protected void onPermissionsAccess()
    {

    }

    /**
     * 权限被拒绝
     *
     * @param permissions 被拒绝的权限列表
     */
    protected void onPermissionsDenied(List<PermissionInfo> permissions)
    {
    }

    /**
     * 检查权限是否需要被授权,危险的和未允许
     *
     * @param pm
     * @param name
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    private boolean checkNeedRequestPermission(PackageManager pm, String name)
    {
        try
        {
            PermissionInfo info = pm.getPermissionInfo(name,
                    PackageManager.GET_META_DATA);
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P)
            {
                return (info.protectionLevel == PermissionInfo.PROTECTION_DANGEROUS
                        && checkSelfPermission(name) == PackageManager.PERMISSION_DENIED);
            }
            return (info.getProtection() == PermissionInfo.PROTECTION_DANGEROUS
                    && checkSelfPermission(name) == PackageManager.PERMISSION_DENIED);
        } catch (Throwable igr)
        {

        }
        //未知的权限,可能是更高级别的api的权限
        return false;
    }
}
