/**
 * 
 * @author zhhaogen
 * 创建于 2020年2月27日 下午11:10:42
 */
package cn.zhg.basic.adapter;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;
import cn.zhg.basic.R;
import cn.zhg.basic.sorter.SorterModel;

/**
 * 排序对话框的列表适配器
 */
public class SorterModelAdapter<T>  extends BasicAdapter<SorterModel<T>>
{ 
	public SorterModelAdapter(Context context, List<SorterModel<T>> sorters)
	{
		super(context,sorters); 
	} 
	public View onCreateView(ViewGroup parent)
	{
		return inflater.inflate(R.layout.basic_item_sort, parent,
				false);
	}

	@Override
	public  BasicAdapter.BasicViewHolder<SorterModel<T>> onCreateViewHolder(
			View itemView)
	{
		return new SorterModelViewHolder(itemView);
	} 
	private class SorterModelViewHolder extends BasicAdapter.BasicViewHolder<SorterModel<T>>{
		private CompoundButton reverseCheck;
		private TextView nameText;
		private View moveBtn;
		/**
		 * @param itemView
		 */
		public SorterModelViewHolder(View itemView)
		{
			super(itemView); 
			reverseCheck=this.getViewById(R.id.reverse_sw);
			nameText=this.getViewById(R.id.name_tv);
			moveBtn=this.getViewById(R.id.move_btn);
		}

		/**
		 * @param item
		 */
		public void update(final SorterModel<T>  item, final int position)
		{
			reverseCheck.setChecked(item.isReverse);
			nameText.setText(item.name);
			reverseCheck.setOnClickListener(new View.OnClickListener()
			{ 
				public void onClick(View v)
				{
					item.isReverse=!item.isReverse;
					notifyDataSetChanged();
				}
			});
		}
		
	}
}
