/**
 * 
 * @author zhhaogen
 * 创建于 2018年4月22日 下午1:47:30
 */
package cn.zhg.basic.util;

import android.content.Context;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

import dalvik.system.DexFile;
import dalvik.system.PathClassLoader;

/**
 * 
 *
 */
public final class ReflectUtil
{
	private ReflectUtil(){
		
	}
	public static List<String> scanAll(Context context)
	{
		List<String> list = new ArrayList<>();
		try
		{  
			String path = context.getPackageCodePath();
//			Logger.d("path="+path);
			DexFile dex = new DexFile(path);
			scan(dex,list); 
		} catch (Throwable igr)
		{
//			igr.printStackTrace();
		}
		return list;
	}
	/**
	 * @param dex
	 * @param list
	 */
	private static void scan(DexFile dex, List<String> list)
	{
		Enumeration<String> entries = dex.entries();
		while (entries.hasMoreElements())
		{
			String entry = entries.nextElement();
//			Logger.d(entry);
		 list.add(entry);
//			Class<?> entryClass = dex.loadClass(entry, classLoader);
		}
	}
	public static List<String> scanAll( )
	{
		 List<String> list=new ArrayList<>();
		 try{
				PathClassLoader classLoader = (PathClassLoader) Thread.currentThread().getContextClassLoader();
				Field dexField = PathClassLoader.class.getDeclaredField("mDexs");
				boolean access=dexField.isAccessible();
				if(!access)
				{
					dexField.setAccessible(access);
				}
				DexFile[] dexs = (DexFile[]) dexField.get(classLoader);
				for (DexFile dex : dexs) {
					scan(dex,list);
				}
		 }catch(Throwable igr)
		 {
			 igr.printStackTrace();
		 }
	
		return list;
	}
	/**
	 * 获取所有方法,包括父类
	 * @param clazz 不能为null
	 * @param superClazz 直到指定父类为止,最值应为java.lang.Object
	 * @return
	 */
	public static List<Method> getAllMethods(Class clazz, Class superClazz)
	{
		List<Method> mths = new ArrayList<>();
		while (true)
		{
			for (Method m : clazz.getDeclaredMethods())
			{
				mths.add(m);
			}
			clazz = clazz.getSuperclass();
			if (!superClazz.isAssignableFrom(clazz))
			{
				break;
			}
		}
		return mths;
	}
	 
}
