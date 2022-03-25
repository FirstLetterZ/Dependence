package com.zpf.views.type;

import java.util.List;

/**
 * @author Created by ZPF on 2021/11/24.
 */
public interface IDecorative<T> {
    void addDecoration(T child, int hierarchy);

    List<T> queryDecoration(int hierarchy);
}