package com.zpf.tool.fragment;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;

import java.util.HashMap;

/**
 * @author Created by ZPF on 2021/2/5.
 */
public class FragmentViewManager implements IViewManager<Fragment> {

    private FragmentManager mFragmentManager;
    private FragmentTransaction mTransaction;

    public FragmentViewManager(FragmentManager fragmentManager) {
        this.mFragmentManager = fragmentManager;
    }

    @Override
    public FragmentViewManager add(int parentId, String tagName, Fragment child) {
        Fragment tagFragment = mFragmentManager.findFragmentByTag(tagName);
        if (tagFragment == null) { //添加
            getTransaction().add(parentId, child, tagName);
        } else if (child != tagFragment) {//替换
            getTransaction().remove(tagFragment).add(parentId, child, tagName);
        } else {//更新
            //
        }
        return this;
    }

    @Override
    public FragmentViewManager add(int parentId, String tagName, Class<Fragment> childClass) {
        Fragment tagFragment = mFragmentManager.findFragmentByTag(tagName);
        if (tagFragment == null) { //添加
                try {
                    tagFragment = childClass.newInstance();
                } catch (Exception e) {
                    //
                }
            if (tagFragment != null) {
                getTransaction().add(parentId, tagFragment, tagName);
            }
        } else if (childClass != tagFragment.getClass()) {//替换
                try {
                    tagFragment = childClass.newInstance();
                } catch (Exception e) {
                    //
                }
            if (tagFragment != null) {
                getTransaction().remove(tagFragment).add(parentId, tagFragment, tagName);
            }
        } else {//更新
            //
        }
        return this;
    }

    @Override
    public FragmentViewManager remove(String tagName) {
        if (tagName != null) {
            Fragment tagFragment = mFragmentManager.findFragmentByTag(tagName);
            if (tagFragment != null && tagFragment.isAdded()) {
                getTransaction().remove(tagFragment);
            }
        }
        return this;
    }

    @Override
    public FragmentViewManager remove(Fragment child) {
        if (child != null && child.isAdded()) {
            if (child.isAdded()) {
                getTransaction().remove(child);
            }
        }
        return this;
    }

    @Override
    public void clear(int parentId) {

    }

    @Override
    public void show(String tagName) {

    }

    @Override
    public void show(Fragment child) {

    }

    @Override
    public void hide(String tagName) {
        Fragment tagFragment = mFragmentManager.findFragmentByTag(tagName);
        hide(tagFragment);
    }

    @Override
    public void hide(Fragment child) {
        if (child != null && child.isAdded() && !child.isHidden()) {
            getTransaction().hide(child);
        }
    }

    @Override
    public void commit() {
        if (mTransaction != null) {
            mTransaction.commitAllowingStateLoss();
            mTransaction = null;
        }
    }

    @Override
    public Fragment get(String tagName) {
        return null;
    }

    private FragmentTransaction getTransaction() {
        if (mTransaction == null) {
            mTransaction = mFragmentManager.beginTransaction();
        }
        return mTransaction;
    }

    private void updateCache(FragmentInfo cacheInfo, int parentId, String tagName, Fragment child) {
        if (cacheInfo == null) {
            cacheInfo = new FragmentInfo(parentId, child, tagName);
        } else {
            cacheInfo.parentId = parentId;
            cacheInfo.tagName = tagName;
            cacheInfo.obj = child;
        }
        fragmentCache.put(tagName, cacheInfo);
    }

    class FragmentInfo {
        int parentId;
        String tagName;
        Fragment obj;

        public FragmentInfo(int parentId, Fragment obj, String tagName) {
            this.parentId = parentId;
            this.tagName = tagName;
            this.obj = obj;
        }
    }
}
