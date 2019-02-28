package com.zpf.tool.fragment;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;

import java.lang.ref.WeakReference;
import java.util.HashMap;

/**
 * Created by ZPF on 2018/6/14.
 */
public class AppFragmentManager implements FragmentManagerInterface {

    private FragmentManager mFragmentManager;
    private FragmentCreator mCreator;
    private int mContainerViewId;
    private String[] mTagArray;
    private int mCurrentIndex = 0;
    private HashMap<String, WeakReference<Fragment>> fragmentCache = new HashMap<>();

    public AppFragmentManager(FragmentManager mFragmentManager, int mContainerViewId,
                              String[] tagArray, FragmentCreator mCreator) {
        this.mFragmentManager = mFragmentManager;
        this.mCreator = mCreator;
        this.mTagArray = tagArray;
        this.mContainerViewId = mContainerViewId;
    }

    @Override
    public void showFragment(int index) {
        if (mTagArray == null || mTagArray.length - 1 < index) {
            return;
        }
        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        for (int i = 0; i < mTagArray.length; i++) {
            Fragment fragment = getFragment(i);
            if (i == index) {
                if (fragment == null) {
                    fragment = mCreator.create(index);
                    if (fragment != null) {
                        transaction.add(mContainerViewId, fragment, mTagArray[i]);
                        fragmentCache.put(mTagArray[i], new WeakReference<>(fragment));
                    }
                } else {
                    transaction.show(fragment);
                }
            } else if (fragment != null) {
                transaction.hide(fragment);
            }
        }
        mCurrentIndex = index;
        transaction.commitAllowingStateLoss();
    }

    @Override
    public int getCurrentIndex() {
        return mCurrentIndex;
    }

    @Override
    public Fragment getFragment(int index) {
        Fragment fragment = null;
        if (mTagArray != null && index < mTagArray.length) {
            WeakReference<Fragment> weakReference = fragmentCache.get(mTagArray[index]);
            if (weakReference != null) {
                fragment = weakReference.get();
            }
            if (fragment == null) {
                fragment = mFragmentManager.findFragmentByTag(mTagArray[index]);
            }
        }
        return fragment;
    }

    public interface FragmentCreator {
        Fragment create(int index);
    }

}
