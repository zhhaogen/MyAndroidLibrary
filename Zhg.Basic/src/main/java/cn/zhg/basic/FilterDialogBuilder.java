/**
 * 
 * @author zhhaogen
 * 创建于 2020年2月28日 下午8:22:29
 */
package cn.zhg.basic;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface; 
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.*;
import android.widget.AdapterView.*;
import cn.zhg.basic.filter.FilterModel;
import cn.zhg.basic.filter.MultiValueFilterModel;
import cn.zhg.basic.filter.RangeValueFilterModel;
import cn.zhg.basic.filter.ValueFilterModel;
import cn.zhg.basic.inter.EConsumer;
import cn.zhg.basic.sorter.SorterModel;
import cn.zhg.basic.util.ReflectUtil;

/**
 * 
 *
 */
public class FilterDialogBuilder<T>
{

	private List<FilterModel<T>> filters; 
	private List<FilterModel<T>> orignalFilters;
	private Context context;
	private EConsumer<List<FilterModel<T>>> okListener;
	private  Runnable cancelListener;
	private LayoutInflater layoutInflater;

	/**
	 * @param context
	 */
	public FilterDialogBuilder(Context context)
	{
		this.context = context;
		layoutInflater = LayoutInflater.from(context);
	}

	/**
	 *设置初始值
	 */
	public FilterDialogBuilder<T> setFilters(List<FilterModel<T>> filters)
	{
		this.filters=filters;
		if(filters==null){
			this.orignalFilters=null;
		}else{
			this.orignalFilters=new ArrayList<>();
			for(FilterModel<T> filter:filters){
				orignalFilters.add(filter.clone());
			} 
		} 
		return this;
	}

	/**
	 * 确认回调监听器
	 */
	public FilterDialogBuilder<T> setOkListener(EConsumer<List<FilterModel<T>>> listener)
	{
		okListener=listener;
		return this;
	}
	/**
	 * 取消回调监听器
	 */
	public FilterDialogBuilder<T> setCancelListener(Runnable listener)
	{
		cancelListener=listener;
		return this;
	}

	/**
	 * 创建对话框
	 */
	@SuppressLint("InflateParams")
	public AlertDialog create()
	{
		View view = layoutInflater.inflate(R.layout.basic_layout_viewgroup,
				null, false);
		final LinearLayout viewGroup = (LinearLayout) view
				.findViewById(R.id.viewgroup_layout);
		addViews(viewGroup, this.filters);
		final AlertDialog alert = new AlertDialog.Builder(context).setView(view)
				.setTitle(R.string.basic_action_filter).setCancelable(false)
				.setNegativeButton(R.string.basic_action_reset, null)
				.setNeutralButton(android.R.string.cancel, null)
				.setPositiveButton(android.R.string.ok, null).create();

		alert.setOnShowListener(new DialogInterface.OnShowListener()
		{
			public void onShow(DialogInterface dialog)
			{
				// setNegativeButton会导致点击完后关闭,使用getButton,
				alert.getButton(DialogInterface.BUTTON_NEGATIVE)
						.setOnClickListener(new View.OnClickListener()
						{
							public void onClick(View v)
							{
								// 重置为初始值
								viewGroup.removeAllViews();
								if (filters != null)
								{
									filters.clear();
									for(FilterModel<T> filter:orignalFilters){
										filters.add(filter.clone());
									} 
								}
								addViews(viewGroup, filters);
							}
						});
				alert.getButton(DialogInterface.BUTTON_NEUTRAL)
						.setOnClickListener(new View.OnClickListener()
						{
							public void onClick(View v)
							{  
								if (cancelListener != null)
								{
									cancelListener.run();
								}
								alert.dismiss();
							}
						});
				alert.getButton(DialogInterface.BUTTON_POSITIVE)
						.setOnClickListener(new View.OnClickListener()
						{
							public void onClick(View v)
							{ 
								if (okListener != null)
								{
									okListener.accept(filters);
								}
								alert.dismiss();
							}
						});
			}
		});
 
		return alert;
	}

	/**
	 * 显示对话框 
	 */
	public void show()
	{ 
		create().show();
	}

	/**
	 * 添加子元素
	 * 
	 * @param viewGroup
	 * @param filters
	 */
	private void addViews(LinearLayout viewGroup, List<FilterModel<T>> filters)
	{
		if (filters == null)
		{
			return;
		}
		for (int position = 0, size = filters
				.size(); position < size; position++)
		{
			final FilterModel<T> item = filters.get(position);
	
			View convertView = layoutInflater
					.inflate(R.layout.basic_item_filter, viewGroup, false);
			viewGroup.addView(convertView);
	
			if (item instanceof ValueFilterModel)
			{
				initValueFilterModel((ValueFilterModel) item, convertView);
			} else if (item instanceof RangeValueFilterModel)
			{
				initRangeValueFilterModel((RangeValueFilterModel) item,
						convertView);
			} else if (item instanceof MultiValueFilterModel)
			{
				initMultiValueFilterModel((MultiValueFilterModel) item,
						convertView);
			} else
			{
				initFilterModel(item, convertView);
			}
		}
	}

	/**
	 * @param item
	 * @param convertView
	 */
	private void initValueFilterModel(final ValueFilterModel<?> item,
			View convertView)
	{
		// init views
		final TextView nameText = (TextView) convertView
				.findViewById(R.id.name_tv);
		final TextView valueText = (TextView) convertView
				.findViewById(R.id.value_tv);
		final CompoundButton enableSwitch = (CompoundButton) convertView
				.findViewById(R.id.enable_sw);
		final Spinner methodSp = (Spinner) convertView
				.findViewById(R.id.method_sp);
	
		//初始化值
		nameText.setText(item.name);
		valueText.setText(item.value);
		methodSp.setAdapter(new ArrayAdapter<String>(context,
				android.R.layout.simple_spinner_dropdown_item,
				item.supportMethods));
		int idx = item.supportMethods.indexOf(item.method);
		if (idx != -1)
		{
			methodSp.setSelection(idx);
		}
		
		// 组件情况
		enableSwitch.setChecked(item.enable);
		valueText.setEnabled(item.enable);
		methodSp.setEnabled(item.enable);
	
		//监听器
		enableSwitch.setOnClickListener(new View.OnClickListener()
		{
			public void onClick(View v)
			{
				item.enable = !item.enable;
				item.value=valueText.getText().toString();
				valueText.setEnabled(item.enable);
				methodSp.setEnabled(item.enable);
			}
		});
		methodSp.setOnItemSelectedListener(new OnItemSelectedListener()
		{
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id)
			{
				item.method = (String) parent.getSelectedItem();
			} 
			public void onNothingSelected(AdapterView<?> parent)
			{
			}
		}); 
		valueText.addTextChangedListener(new TextWatcher()
		{
			public void beforeTextChanged(CharSequence s, int start,
					int count, int after)
			{
			} 
			public void onTextChanged(CharSequence s, int start, int before,
					int count)
			{
			} 
			public void afterTextChanged(Editable s)
			{
				item.value = s.toString();
			}
		});
	}

	/**
	 * @param item
	 * @param convertView
	 */
	private void initRangeValueFilterModel(
			final RangeValueFilterModel<?> item, View convertView)
	{
		// init views
		final TextView nameText = (TextView) convertView
				.findViewById(R.id.name_tv);
		final TextView valueText = (TextView) convertView
				.findViewById(R.id.value_tv);
		final CompoundButton enableSwitch = (CompoundButton) convertView
				.findViewById(R.id.enable_sw);
		final Spinner methodSp = (Spinner) convertView
				.findViewById(R.id.method_sp);
	
		// 初始化值
		nameText.setText(item.name);
		valueText.setText(item.value);
		methodSp.setAdapter(new ArrayAdapter<String>(context,
				android.R.layout.simple_spinner_dropdown_item,
				item.supportValues));
		int idx = item.supportValues.indexOf(item.value);
		if (idx != -1)
		{
			methodSp.setSelection(idx);
		}
	
		// 组件情况
		enableSwitch.setChecked(item.enable);
		valueText.setVisibility(View.GONE);
		methodSp.setEnabled(item.enable);
	
		// 监听器
		enableSwitch.setOnClickListener(new View.OnClickListener()
		{
			public void onClick(View v)
			{
				item.enable = !item.enable; 
				methodSp.setEnabled(item.enable);
			}
		});
		methodSp.setOnItemSelectedListener(new OnItemSelectedListener()
		{
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id)
			{
				item.value = (String) parent.getSelectedItem();
			}
	
			public void onNothingSelected(AdapterView<?> parent)
			{
			}
		});
	}

	/**
		 * @param item
		 * @param convertView
		 */
		private void initMultiValueFilterModel(
				final MultiValueFilterModel<?> item, View convertView)
		{
			// init views
			final TextView nameText = (TextView) convertView
					.findViewById(R.id.name_tv);
			final TextView valueText = (EditText) convertView
					.findViewById(R.id.value_tv);
			final CompoundButton enableSwitch = (CompoundButton) convertView
					.findViewById(R.id.enable_sw);
			final Spinner methodSp = (Spinner) convertView
					.findViewById(R.id.method_sp);
	
			// 初始化值
			nameText.setText(item.name); 
			valueText.setText(join(",", item.values)); 
	
			// 组件情况
			enableSwitch.setChecked(item.enable);
			methodSp.setVisibility(View.GONE); 
			valueText.setEnabled(item.enable);
			valueText.setInputType(EditorInfo.TYPE_NULL);
	//		valueText.setFocusableInTouchMode(false);
			// 监听器
			enableSwitch.setOnClickListener(new View.OnClickListener()
			{
				public void onClick(View v)
				{
					item.enable = !item.enable;
					valueText.setEnabled(item.enable);
				}
			}); 
			valueText.setOnClickListener(new View.OnClickListener()
			{ 
				public void onClick(View v)
				{
					showChoiceDialog(item,valueText);
				}
			});
		}

	/**
	 * @param item
	 * @param convertView
	 */
	private void initFilterModel(final FilterModel<?> item,
			View convertView)
	{
		// init views
		final TextView nameText = (TextView) convertView
				.findViewById(R.id.name_tv);
		final TextView valueText = (TextView) convertView
				.findViewById(R.id.value_tv);
		final CompoundButton enableSwitch = (CompoundButton) convertView
				.findViewById(R.id.enable_sw);
		final Spinner methodSp = (Spinner) convertView
				.findViewById(R.id.method_sp);
	
		// 初始化值
		nameText.setText(item.name); 
	
		// 组件情况
		enableSwitch.setChecked(item.enable);
		valueText.setVisibility(View.GONE);
		methodSp.setVisibility(View.GONE);
		
		// 监听器
		enableSwitch.setOnClickListener(new View.OnClickListener()
		{
			public void onClick(View v)
			{
				item.enable = !item.enable;
				valueText.setEnabled(item.enable);
				methodSp.setEnabled(item.enable);
			}
		});
	}

	/**
	 * @param item
	 * @param valueText 
	 */
	private void showChoiceDialog(final MultiValueFilterModel<?> item, final TextView valueText)
	{
		final String[] items = item.supportValues.toArray(new String[0]);
		final	boolean[] checkedItems = new boolean[items.length];
		for(int i=0,size=item.values.size();i<size;i++){
			String itemValue = item.values.get(i);
			int idx=item.supportValues.indexOf(itemValue);
			if(idx!=-1){
				checkedItems[idx]=true;
			}
		}
		DialogInterface.OnMultiChoiceClickListener listener = new DialogInterface.OnMultiChoiceClickListener()
		{ 
			public void onClick(DialogInterface dialog, int which, boolean isChecked)
			{
				checkedItems[which]=isChecked; 
			}
		}; 
		AlertDialog alert = new AlertDialog.Builder(context)
				.setCancelable(false)
				.setMultiChoiceItems(items, checkedItems, listener)
				.setNeutralButton(android.R.string.cancel, null)
				.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener()
				{ 
					public void onClick(DialogInterface dialog, int which)
					{
						item.values.clear();
						for(int i=0;i<checkedItems.length;i++){
							if(checkedItems[i]){
								item.values.add(items[i]);
							} 
						}
						valueText.setText(join(",", item.values));
					}
				}).create(); 
		alert.show();
	} 
	/**
	 * 拼接字符串,jdk6+
	 * @param delimiter
	 * @param elements
	 * @return
	 */
	private static CharSequence join(String delimiter, List<String> elements)
	{ 
		 if(elements==null){
			 return null;
		 }
		 StringBuilder sb=new StringBuilder(); 
		 for(int i=0,size=elements.size();i<size;i++){
			 if(i!=0){
				 sb.append(delimiter);
			 }
			 sb.append(elements.get(i));
		 }
		return sb.toString();
	} 
}
