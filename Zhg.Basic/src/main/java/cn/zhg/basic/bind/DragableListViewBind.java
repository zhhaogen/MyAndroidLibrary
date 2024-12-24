package cn.zhg.basic.bind;

import android.content.Context;
import android.graphics.*;
import android.util.Log;
import android.view.*;
import android.widget.*;

import androidx.annotation.*;

/**
 * 将普通listview转变可拖动排序listview
 */
public class DragableListViewBind implements View.OnTouchListener {
    private static final String TAG = DragableListViewBind.class.getSimpleName();
    private final ListView listView;
    private final ViewGroup listViewLayout;
    private final Context context;
    /**
     * 绘制的阴影图
     */
    private ImageView shadowView;
    /**
     * 阴影y坐标与鼠标的偏移
     */
    private int offsetY;
    /**
     * 是否启用
     */
    private boolean enable;
    /**
     * 最大y坐标点
     */
    private int maxY;
    /**
     * 拖动区域id
     */
//    @IdRes
    private int dragViewId;
    private OnDragListener onDragListener;
    private int startPosition;
    /**
     *
     * @param listView
     * @param listViewLayout
     */
    public DragableListViewBind(ListView listView, ViewGroup listViewLayout) {
        this.listView = listView;
        this.listViewLayout = listViewLayout;
        this.context = listView.getContext();
        listView.setOnTouchListener(this);
    }

    /**
     * 启用拖动
     */
    public DragableListViewBind enable() {
        enable = true;
        return this;
    }

    /**
     * 停用拖动
     */
    public DragableListViewBind disable() {
        enable = false;
        if (shadowView != null) {
            listViewLayout.removeView(shadowView);
            shadowView = null;
        }
        return this;
    }
    /**
     * 拖动区域id，只有点击该区域才能进行拖动
     */
    public DragableListViewBind setDragViewId(int dragViewId) {
        this.dragViewId = dragViewId;
        return this;
    }

    public void setOnDragListener(OnDragListener onDragListener) {
        this.onDragListener = onDragListener;
    }

    public boolean onTouch(View view, MotionEvent e) {
        if (!enable) {
            return false;
        }
        int action = e.getAction();
        if (action == MotionEvent.ACTION_DOWN) {
            if (shadowView == null) {
                //开始绘制
                int x = (int) e.getX();
                int y = (int) e.getY();
                int currentPostion = listView.pointToPosition(x, y);
//                Log.d(TAG, "开始拖动 :" + "x=" + x + ",y=" + y + ",postion=" + currentPostion);
                if (currentPostion == AdapterView.INVALID_POSITION) {
                    return false;
                }
                View itemView = listView.getChildAt(currentPostion - listView.getFirstVisiblePosition());
//                Log.d(TAG, "测量大小 :"+"listView="+listView.getWidth()+"*"+listView.getHeight()+",itemView=" + itemView.getWidth()+"*"+itemView.getHeight());
//                Log.d(TAG, "测量位置 :"+"listView="+listView.getX()+","+listView.getY()+",itemView=" + itemView.getX()+","+itemView.getY());
                //判断是否在id中
                if(dragViewId!=0){
                   View dragView=itemView.findViewById(dragViewId);
                    if(dragView==null){
                        return false;
                    }
                    Rect outRect=new Rect();
                    dragView.getHitRect(outRect);
//                    Log.d(TAG, "测量位置2 :"+"dragView="+dragView.getX()+","+dragView.getY() +",outRect="+outRect.toShortString());
                    outRect.top= (int) (outRect.top+itemView.getY());
                    outRect.bottom= (int) (outRect.bottom+itemView.getY());
//                    Log.d(TAG, "测量位置3 :" +",outRect="+outRect.toShortString());
                    if(!outRect.contains(x,y)){
                        return false;
                    }
                }
                //
                startPosition=currentPostion;
                float itemY = itemView.getY();
                maxY = listView.getHeight() - itemView.getHeight();
                //绘制阴影
                itemView.setDrawingCacheEnabled(true);// 开启cache.
                itemView.buildDrawingCache();
                Bitmap bm = Bitmap.createBitmap(itemView.getDrawingCache());// 根据cache创建一个新的bitmap对象.
                itemView.setDrawingCacheEnabled(false);// 一定关闭cache，否则复用会出现错乱
                shadowView = new ImageView(context);
                shadowView.setImageBitmap(bm);
                //需要设置
                shadowView.setBackgroundColor(Color.argb(100, 0, 0, 0));
                ViewGroup.MarginLayoutParams layoutParams ;
                layoutParams= new RelativeLayout.LayoutParams(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
                layoutParams.topMargin = (int) itemY;
                listViewLayout.addView(shadowView, layoutParams);
                //计算开始的偏差值
                offsetY = (int) (e.getY() - itemY);
            }
            return false;
        }
        if (action == MotionEvent.ACTION_MOVE) {
            if (shadowView == null) {
                return false;
            }
            int moveY = (int) (e.getY() - offsetY);
            //超出位置
            if (moveY < 0) {
                //让其向上滚动
                listView.scrollListBy(-20);
                return false;
            }
            if (moveY > maxY) {
                //让其向下滚动
                listView.scrollListBy(20);
                return false;
            }
            //开始移动
            ViewGroup.MarginLayoutParams windowParams = (ViewGroup.MarginLayoutParams) shadowView.getLayoutParams();
            windowParams.topMargin = moveY;
            listViewLayout.updateViewLayout(shadowView, windowParams);
            return true;
        }
        if (action == MotionEvent.ACTION_UP) {
            if (shadowView == null) {
                return false;
            }
            //停止绘制
            listViewLayout.removeView(shadowView);
            shadowView = null;
            int x = (int) e.getX();
            int y = (int) e.getY();
            int currentPostion = listView.pointToPosition(x, y);
//            Log.d(TAG, "停止拖动 :" + "x=" + x + ",y=" + y + ",postion=" + currentPostion);
            if (currentPostion == AdapterView.INVALID_POSITION) {
                return false;
            }
            if(onDragListener!=null){
                onDragListener.onDragEnd(startPosition,currentPostion);
            }
            return false;
        }
        return false;
    }
    public static interface OnDragListener{
        void onDragEnd(int startPosition,int endPosition);
    }
}
