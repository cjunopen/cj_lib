package com.cj.lib_tools.util;

import android.view.View;
import android.widget.GridLayout;

import timber.log.Timber;

/**
 * @Description:
 * @Author: CJ
 * @CreateDate: 2025/3/13 下午 5:46:25
 */
public class LayoutUtils {

    public static void gridLayoutAddView(GridLayout layout, View view){
        gridLayoutAddView(layout, view, -1 ,-1);
    }

    /**
     * 均匀分布
     */
    public static void gridLayoutAddView(GridLayout layout, View view, int leftMargin, int topMargin){
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                int row = layout.getRowCount();
                int col = layout.getColumnCount();

                int parentW = ViewUtils.getViewWidth(layout);
                int parentH = ViewUtils.getViewHeight(layout);
                if (parentW == 0 || parentH == 0) {
                    Timber.i("gridLayoutAddViews parentW: %d, parentH:%d", parentW, parentH);
                    return;
                }

                int myLeftMargin = leftMargin, myTopMargin = topMargin;
                GridLayout.LayoutParams layoutParams = view.getLayoutParams() != null ?
                        (GridLayout.LayoutParams) view.getLayoutParams() : new GridLayout.LayoutParams();
                if (myLeftMargin == -1) {
                    int w = ViewUtils.getViewWidth(view);
                    myLeftMargin = ((parentW - layout.getPaddingLeft() - layout.getPaddingRight())
                            - w * col) / (col + 1);
                }
                if (topMargin == -1) {
                    int h = ViewUtils.getViewHeight(view);
                    myTopMargin = ((parentH - layout.getPaddingTop() - layout.getPaddingBottom())
                            - h * row) / (row + 1);
                }

                layoutParams.leftMargin = myLeftMargin;
                layoutParams.topMargin = myTopMargin;
                layout.addView(view,layoutParams);
                // FIXME: 2025/3/13 addViewInLayout的区别？
//                try {
//                    ReflectUtils.reflect(layout).method("addViewInLayout", view, -1, layoutParams, true).get();
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//                layout.requestLayout();
            }
        };
        layout.post(runnable);
    }
}
