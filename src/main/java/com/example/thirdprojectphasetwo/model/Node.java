package com.example.thirdprojectphasetwo.model;

import lombok.Data;

import java.util.PriorityQueue;

@Data

public class Node implements Cloneable{
    String key;
    int count;
    double prob;
    PriorityQueue<InnerNode> queue;

    public Node() {
        queue = new PriorityQueue<> (new QueueComparator ());
    }

    @Override
    public Node clone() {
        Node node;
        try {
            node = (Node) super.clone ();
        } catch (CloneNotSupportedException e) {
            node = new Node ();
            node.setKey (this.getKey ());
            node.setCount (this.getCount ());
        }

        node.setQueue (new PriorityQueue<> (this.getQueue ()));
        return node;
    }
}
