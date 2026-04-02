package com.cj.lib_tools.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.cj.lib_tools.R;
import com.cj.lib_tools.interfaces.IDataListener;
import com.cj.lib_tools.util.LayoutUtils;
import com.zhpan.bannerview.BannerViewPager;
import com.zhpan.bannerview.BaseBannerAdapter;
import com.zhpan.bannerview.BaseViewHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description: 多个网格页组成的view，可左右滑动查看
 * @Author: CJ
 * @CreateDate: 2025/1/14 下午 5:45:12
 */
public abstract class GridBanner<T> extends BannerViewPager<List<T>> {

    public abstract int getRow();

    public abstract int getCol();

    private IDataListener<T> mIDataListener;  //pos是分页前列表的位置，比如一页8个，第2页的第3个就是11

    protected abstract void bindItemViewData(View view, T data, int pos);

    protected abstract int getItemViewLayoutId(); //每页的每个item

    public GridBanner(Context context) {
        super(context);
        init();
    }

    public GridBanner(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public GridBanner(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    protected void init(){
        setAdapter(new PageAdapter());
        setAutoPlay(false)
                .setIndicatorVisibility(View.GONE);
    }

    public void setIDataListener(IDataListener<T> IDataListener) {
        mIDataListener = IDataListener;
    }

    /**
     * 一页多少个item
     */
    public int getItemCountPerPage() {
        return getRow() * getCol();
    }

    /**
     * @param data 源列表，自动切割
     */
    public void create2(List<T> data) {
        create(splitList(data, getItemCountPerPage()));
    }

    protected void create2(int size){
        List<T> datas  = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            datas.add(null);
        }
        create2(datas);
    }

    /**
     * 传进来之后会根据pageSize裁剪成一个个子list
     *
     * @param list
     */
    public List<List<T>> splitList(List<T> list, int len) {
        if (list == null || list.size() == 0 || len < 1) {
            return new ArrayList<>();
        }

        List<List<T>> result = new ArrayList<>();

        int size = list.size();
        int count = (size + len - 1) / len;

        for (int i = 0; i < count; i++) {
            List subList = list.subList(i * len, ((i + 1) * len > size ? size : len * (i + 1)));
            result.add(subList);
        }
        return result;
    }

    protected class PageAdapter extends BaseBannerAdapter<List<T>>{

        private void initGridLayout(GridLayout gridLayout){
            ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams(getWidth(), getHeight());
            gridLayout.setLayoutParams(layoutParams);
            gridLayout.removeAllViews();
            gridLayout.setColumnCount(getCol());
            gridLayout.setRowCount(getRow());
            gridLayout.setOrientation(GridLayout.HORIZONTAL);
        }


        @Override
        protected void bindData(BaseViewHolder<List<T>> holder, List<T> datas, int position, int pageSize) {
            if (!(holder.itemView instanceof ViewGroup)) {
                return;
            }
            GridLayout gridLayout = holder.itemView.findViewById(R.id.grid_layout);

            if (gridLayout.getChildCount() == 0){
                initGridLayout(gridLayout);
            }

            if (datas.get(0) == null && mIDataListener != null){
                datas = mIDataListener.loadData(getItemCountPerPage() * position, datas.size());
            }
            if (datas != null) {
                for (int i = 0; i < datas.size(); i++) {
                    T data = datas.get(i);
                    View view;
                    if (gridLayout.getChildCount() > i){
                        view = gridLayout.getChildAt(i);
                    }else {
                        view = LayoutInflater.from(getContext()).inflate(getItemViewLayoutId(),
                                gridLayout, false); //不传父布局item的xml的根布局宽高不生效
                        LayoutUtils.gridLayoutAddView(gridLayout, view);
                    }

                    if (data != null) {
                        bindItemViewData(view, data, getItemCountPerPage() * position + i);
                    }
                }
            }
        }

        @Override
        public int getLayoutId(int viewType) {
            return R.layout.grid_banner_page;
        }
    }

    /**
     * 刷新数据
     * @param iDataListener
     */
    public void refresh(IDataListener<T> iDataListener){
        mIDataListener = iDataListener;
        create2(iDataListener.getDataCount());
    }
}
