package com.cj.lib_tools.interfaces;

import java.util.List;

/**
 * @Description:
 * @Author: CJ
 * @CreateDate: 2025/3/17 下午 4:15:46
 */
public interface IDataListener<T>{

    int getDataCount();

    List<T> loadData(int pos, int size);
}
