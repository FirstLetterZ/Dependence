package com.zpf.frame;

import android.os.Bundle;
import androidx.annotation.Nullable;

/**
 * Created by ZPF on 2019/6/12.
 */
public interface IContainerHelper {

    IViewContainer createFragmentContainer(@Nullable Bundle args);

    Class<? extends IViewProcessor> getErrorProcessorClass(Class<?> targetViewClass);

    Class<? extends IViewProcessor> getLaunchProcessorClass(Class<?> targetViewClass);

    Class<? extends IViewContainer> getDefContainerClassByType(int type);
}