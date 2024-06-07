package com.zpf.tool.animation;

import android.util.SparseArray;

import java.util.HashMap;
import java.util.HashSet;

public class StagedViewAnimator extends StagedAnimator {
    private final HashMap<String, ViewAnimAttribute> attrMap = new HashMap<>();
    private final SparseArray<HashSet<String>> stageAnimKeys = new SparseArray<>();

    protected void onUpdate(int stage, float percent, boolean stageChanged) {
        if (stageChanged) {
            HashSet<String> last = stageAnimKeys.get(stage - 1);
            if (last != null) {
                for (String key : last) {
                    if (key != null) {
                        ViewAnimAttribute attribute = attrMap.get(key);
                        if (attribute != null) {
                            attribute.run(1f);
                        }
                    }
                }
            }
        }
        HashSet<String> current = stageAnimKeys.get(stage);
        if (current != null) {
            for (String key : current) {
                if (key != null) {
                    ViewAnimAttribute attribute = attrMap.get(key);
                    if (attribute != null) {
                        if (stageChanged) {
                            attribute.run(0f);
                        }
                        attribute.run(percent);
                    }
                }
            }
        }
        super.onUpdate(stage, percent, stageChanged);
    }

    @Override
    protected void onComplete(boolean canceled) {
        if (!canceled) {
            HashSet<String> current = stageAnimKeys.get(size() - 1);
            if (current != null) {
                for (String key : current) {
                    if (key != null) {
                        ViewAnimAttribute attribute = attrMap.get(key);
                        if (attribute != null) {
                            attribute.run(1f);
                        }
                    }
                }
            }
        }
        super.onComplete(canceled);
    }
    public void addAttribute(int stage, ViewAnimAttribute attr) {
        HashSet<String> set = stageAnimKeys.get(stage);
        if (set == null) {
            set = new HashSet<>();
            stageAnimKeys.put(stage, set);
        }
    }

    public boolean removeAttribute(ViewAnimAttribute attr) {
        if (attr == null) {
            return false;
        }
        String key = attr.getClass().getName() + attr.hashCode();
        return attrMap.remove(key) != null;
    }

    public void clearAttribute(int stage) {
        HashSet<String> set = stageAnimKeys.get(stage);
        stageAnimKeys.remove(stage);
        if (set != null) {
            for (String key : set) {
                if (key != null) {
                    attrMap.remove(key);
                }
            }
        }
    }

    public void clearAllAttribute() {
        attrMap.clear();
        stageAnimKeys.clear();
    }
}
