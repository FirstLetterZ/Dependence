package com.zpf.tool.compat.fragment;

import android.util.SparseArray;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.zpf.tool.fragment.IViewCreator;
import com.zpf.tool.fragment.IViewManager;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

/**
 * Created by ZPF on 2018/6/14.
 */
public class CompatFragmentManager implements IViewManager<String, Fragment> {

    private final FragmentManager mFragmentManager;
    private final IViewCreator<String, Fragment> mCreator;
    private final SparseArray<HashSet<String>> mParents = new SparseArray<>();
    private final HashMap<String, Integer> mTags = new HashMap<>();
    private FragmentTransaction mTransaction;

    public CompatFragmentManager(FragmentManager fragmentManager, IViewCreator<String, Fragment> creator) {
        this.mFragmentManager = fragmentManager;
        this.mCreator = creator;
    }

    //需要调用commit方法
    @Override
    public CompatFragmentManager add(int parentId, String tagName) {
        if (tagName == null) {
            return this;
        }
        Integer cacheParentId = mTags.get(tagName);
        Fragment tagFragment = mFragmentManager.findFragmentByTag(tagName);
        if (cacheParentId != null && cacheParentId != parentId) {
            HashSet<String> cacheGroup = mParents.get(cacheParentId);
            if (cacheGroup != null) {
                cacheGroup.remove(tagName);
            }
            if (tagFragment != null) {
                getTransaction().remove(tagFragment);
            }
        }
        if (tagFragment == null) {
            tagFragment = mCreator.create(tagName);
        }
        if (tagFragment == null) {
            return this;
        }
        getTransaction().add(parentId, tagFragment, tagName);
        HashSet<String> tagGroup = mParents.get(parentId);
        if (tagGroup == null) {
            tagGroup = new HashSet<>();
            mParents.put(parentId, tagGroup);
        }
        tagGroup.add(tagName);
        mTags.put(tagName, parentId);
        return this;
    }

    @Override
    public CompatFragmentManager remove(String tagName) {
        if (tagName == null) {
            return this;
        }
        Fragment tagFragment = mFragmentManager.findFragmentByTag(tagName);
        if (tagFragment != null) {
            if (tagFragment.isAdded()) {
                getTransaction().remove(tagFragment);
                commit();
            }
            Integer cacheParentId = mTags.get(tagName);
            if (cacheParentId != null) {
                HashSet<String> cacheGroup = mParents.get(cacheParentId);
                if (cacheGroup != null) {
                    cacheGroup.remove(tagName);
                }
            }
            mTags.remove(tagName);
        }
        return this;
    }

    @Override
    public void clear(int parentId) {
        HashSet<String> cacheGroup = mParents.get(parentId);
        if (cacheGroup != null && cacheGroup.size() > 0) {
            Iterator<String> iterator = cacheGroup.iterator();
            while (iterator != null && iterator.hasNext()) {
                String tagName = iterator.next();
                if (tagName == null) {
                    continue;
                }
                Fragment tagFragment = mFragmentManager.findFragmentByTag(tagName);
                if (tagFragment != null && tagFragment.isAdded()) {
                    getTransaction().remove(tagFragment);
                }
                mTags.remove(tagName);
            }
            commit();
            mParents.remove(parentId);
        }
    }

    @Override
    public boolean commit() {
        if (mTransaction != null) {
            mTransaction.commitAllowingStateLoss();
            mTransaction = null;
            return true;
        }
        return false;
    }

    @Override
    public Fragment getView(String tagName) {
        return mFragmentManager.findFragmentByTag(tagName);
    }

    @Override
    public void show(String tagName) {
        if (tagName == null) {
            return;
        }
        Fragment tagFragment = mFragmentManager.findFragmentByTag(tagName);
        if (tagFragment == null) {
            return;
        }
        getTransaction().show(tagFragment);
        Integer cacheParentId = mTags.get(tagName);
        HashSet<String> cacheGroup = null;
        if (cacheParentId != null) {
            cacheGroup = mParents.get(cacheParentId);
        }
        //cacheGroup为null，说明不在管理范围内
        if (cacheGroup != null) {
            Iterator<String> iterator = cacheGroup.iterator();
            String name;
            Fragment fragment;
            while (iterator != null && iterator.hasNext()) {
                name = iterator.next();
                if (name == null || name.equals(tagName)) {
                    continue;
                }
                fragment = mFragmentManager.findFragmentByTag(name);
                if (fragment != null) {
                    getTransaction().hide(fragment);
                }
            }
        }
        commit();
    }

    @Override
    public void hide(String tagName) {
        if (tagName == null) {
            return;
        }
        Fragment tagFragment = mFragmentManager.findFragmentByTag(tagName);
        if (tagFragment == null) {
            return;
        }
        getTransaction().hide(tagFragment);
        commit();
    }

    private FragmentTransaction getTransaction() {
        if (mTransaction == null) {
            mTransaction = mFragmentManager.beginTransaction();
        }
        return mTransaction;
    }

}