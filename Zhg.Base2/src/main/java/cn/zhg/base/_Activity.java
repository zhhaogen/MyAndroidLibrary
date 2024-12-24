/**
 * 
 * @author zhhaogen
 * 创建于 2017年3月21日 下午9:24:42
 */
package cn.zhg.base;


import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

/**
 * 
 *
 */
public class _Activity extends AppCompatActivity
{
	protected void replaceFragment(int containerViewId, _Fragment fragment){
		FragmentTransaction tr = this.getSupportFragmentManager().beginTransaction();
		tr.replace(containerViewId, fragment); 
		tr.commitAllowingStateLoss();
	}
	protected void replaceFragment(int containerViewId, _Fragment fragment, String tag){
		FragmentTransaction tr = this.getSupportFragmentManager().beginTransaction();
		tr.replace(containerViewId, fragment, tag);
		tr.commitAllowingStateLoss();
	}
}
