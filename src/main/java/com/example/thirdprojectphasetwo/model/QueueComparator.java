package com.example.thirdprojectphasetwo.model;

import java.util.Comparator;

public class QueueComparator implements Comparator<InnerNode> {
    @Override
    public int compare(InnerNode o1, InnerNode o2) {
        return o2.getCount () - o1.getCount ();
    }
}
