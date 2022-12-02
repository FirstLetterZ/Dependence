package com.zpf.tool.fragment;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;

/**
 * @author Created by ZPF on 2021/2/5.
 */
public class TestFragmentManager {

    private final FragmentManager mFragmentManager;
    private volatile FragmentTransaction mTransaction;
    protected IFragmentFactory<? extends Fragment> defFactory;
    private final HashMap<String, Integer> mTags = new HashMap<>();
    private final HashMap<String, PendingNode<Fragment>> waitCommitMap = new HashMap<>();

    public TestFragmentManager(@NonNull FragmentManager fragmentManager) {
        this(fragmentManager, null);
    }

    public TestFragmentManager(@NonNull FragmentManager fragmentManager, @Nullable IFragmentFactory<? extends Fragment> factory) {
        this.mFragmentManager = fragmentManager;
        this.defFactory = factory;
    }

    public void setDefaultFactory(@Nullable IFragmentFactory<? extends Fragment> factory) {
        defFactory = factory;
    }

    @NonNull
    public TestFragmentManager add(@NonNull String[] tagArray, @Nullable IFragmentFactory<? extends Fragment> factory) {
        for (String tag : tagArray) {
            add(tag, factory);
        }
        return this;
    }

    @NonNull
    public TestFragmentManager add(@NonNull String tag, @Nullable IFragmentFactory<? extends Fragment> factory) {
        Fragment tagFragment = findInAdded(tag);
        if (tagFragment != null) {
            return this;
        }
        tagFragment = findInPending(tag);
        if (tagFragment != null) {
            return this;
        }
        PendingNode<Fragment> node = createPendingNode(tag, factory);
        if (node != null) {
            tagFragment = node.getValue();
        }
        if (tagFragment == null) {
            return this;
        }
        waitCommitMap.put(tag, node);
        getTransaction().add(node.parent, tagFragment, tag);
        mTags.put(tag, node.parent);
        return this;
    }

    public boolean remove(@NonNull String tag) {
        mTags.remove(tag);
        Fragment tagFragment = findInAdded(tag);
        if (tagFragment != null) {
            if (!tagFragment.isRemoving()) {
                getTransaction().remove(tagFragment);
                return commit();
            }
            return false;
        }
        tagFragment = findInPending(tag);
        if (tagFragment != null) {
            if (waitCommitMap.remove(tag) != null) {
                getTransaction().remove(tagFragment);
                return commit();
            }
            return false;
        }
        return false;
    }

    public boolean show(@NonNull String tag) {
        return show(tag, null);
    }

    public boolean show(@NonNull String tag, @Nullable IFragmentFactory<? extends Fragment> factory) {
        Fragment tagFragment = findInAdded(tag);
        int parentId = 0;
        if (tagFragment != null) {
            if (tagFragment.isInLayout()) {
                View view = tagFragment.getView();
                if (view != null) {
                    view.setVisibility(View.VISIBLE);
                    return true;
                }
                return false;
            }
            parentId = tagFragment.getId();
        } else {
            PendingNode<Fragment> node = waitCommitMap.get(tag);
            if (node != null) {
                parentId = node.parent;
                tagFragment = node.getValue();
            }
        }
        if (tagFragment == null) {
            PendingNode<Fragment> node = createPendingNode(tag, factory);
            if (node != null) {
                tagFragment = node.getValue();
            }
            if (tagFragment == null) {
                return false;
            }
            waitCommitMap.put(tag, node);
            getTransaction().add(node.parent, tagFragment, tag);
            parentId = node.parent;
        }
        mTags.put(tag, parentId);
        if (tagFragment.isHidden()) {
            getTransaction().show(tagFragment);
        }
        Fragment fragment;
        for (Map.Entry<String, Integer> entry : mTags.entrySet()) {
            if (!Objects.equals(entry.getValue(), parentId)) {
                continue;
            }
            fragment = findByTag(tag);
            if (fragment != null && !fragment.isHidden()) {
                getTransaction().hide(fragment);
            }
        }
        return commit();
    }

    public boolean hide(@NonNull String tag) {
        Fragment fragment = findByTag(tag);
        if (fragment != null && !fragment.isHidden()) {
            getTransaction().hide(fragment);
            return commit();
        }
        return false;
    }

    public void clear(int parentId) {
        Fragment fragment;
        String tag;
        for (Map.Entry<String, Integer> entry : mTags.entrySet()) {
            if (!Objects.equals(entry.getValue(), parentId)) {
                continue;
            }
            tag = entry.getKey();
            mTags.remove(tag);
            fragment = findByTag(tag);
            if (fragment != null && fragment.isAdded() && !fragment.isRemoving()) {
                getTransaction().remove(fragment);
            }
        }
        commit();
    }

    public boolean commit() {
        if (mTransaction != null) {
            mTransaction.commitAllowingStateLoss();
            mTransaction = null;

            return true;
        }
        return false;
    }

    @Nullable
    public Fragment findByTag(@NonNull String tag) {
        Fragment fragment = findInAdded(tag);
        if (fragment == null) {
            fragment = findInPending(tag);
        }
        return fragment;
    }

    @Nullable
    public Fragment findInAdded(@NonNull String tag) {
        return mFragmentManager.findFragmentByTag(tag);
    }

    @Nullable
    public Fragment findInPending(@NonNull String tag) {
        PendingNode<Fragment> node = waitCommitMap.get(tag);
        if (node == null) {
            return null;
        }
        return node.getValue();
    }

    @Nullable
    private PendingNode<Fragment> createPendingNode(@NonNull String tag, @Nullable IFragmentFactory<? extends Fragment> factory) {
        int parentId = 0;
        Fragment tagFragment = null;
        if (factory != null) {
            parentId = factory.getParentId(tag);
            tagFragment = factory.create(tag);
        }
        if (tagFragment == null) {
            factory = defFactory;
            if (factory != null) {
                parentId = factory.getParentId(tag);
                tagFragment = factory.create(tag);
            }
        }
        if (tagFragment == null) {
            return null;
        }
        return new PendingNode<>(tagFragment, parentId, tag);
    }

    private FragmentTransaction getTransaction() {
        if (mTransaction == null) {
            mTransaction = mFragmentManager.beginTransaction();
        }
        return mTransaction;
    }

    public void clearCommitCache() {
        if (waitCommitMap.size() > 0) {
            for (Iterator<PendingNode<Fragment>> it = waitCommitMap.values().iterator(); it.hasNext(); ) {
                PendingNode<Fragment> item = it.next();
                Fragment f = item.getValue();
                if (f == null || f.isInLayout() || f.isAdded()) {
                    it.remove();
                }
            }
        }
    }
}