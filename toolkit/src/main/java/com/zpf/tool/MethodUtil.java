package com.zpf.tool;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.LinkedList;

public class MethodUtil {
    private static class Entry<P, V> {
        LinkedList<P> nodeList = new LinkedList<>();
        V tempValue = null;
    }

    public static <P, V> V cps(P original, INodeManager<P, V> nv) {
        Entry<P, V> entry = new Entry<>();
        entry.tempValue = nv.addNodeValue(original, entry.tempValue);
        nv.addChildNode(original, entry.nodeList);
        while (entry.nodeList.size() > 0) {
            P n = entry.nodeList.pollFirst();
            entry.tempValue = nv.addNodeValue(n, entry.tempValue);
            nv.addChildNode(n, entry.nodeList);
        }
        return entry.tempValue;
    }


    public interface INodeManager<P, V> {
        @Nullable
        V addNodeValue(@Nullable P node, @Nullable V value);

        void addChildNode(@Nullable P node, @NonNull LinkedList<P> nodeList);
    }
}
