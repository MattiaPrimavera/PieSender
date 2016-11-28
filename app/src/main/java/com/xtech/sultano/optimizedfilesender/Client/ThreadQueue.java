package com.xtech.sultano.optimizedfilesender.Client;

import java.util.LinkedList;

public class ThreadQueue {
    private final int MAX_ACTIVE_THREADS = 5;
    private int activeThreads;
    private LinkedList<Thread> list;

    // Queue constructor
    public ThreadQueue()
    {
        this.activeThreads = 0;
        // Create a new LinkedList.
        list = new LinkedList<Thread>();
    }

    public boolean isEmpty(){
        return (list.size() == 0);
    }

    public synchronized void enqueue(Thread item){
        // Append the item to the end of our linked list.
        list.add(item);
        if(this.activeThreads <= MAX_ACTIVE_THREADS){
            this.dequeue();
        }

    }

    public synchronized void increaseActiveThreads(){
        this.activeThreads++;
    }

    public synchronized void decreaseActiveThreads(){
        this.activeThreads--;
    }

    public synchronized Thread dequeue(){
        if(!this.isEmpty()){
            this.increaseActiveThreads();
            Thread item = list.get(0);
            item.start();
            list.remove(0);
            return item;

        }
        return null;
    }

    public Thread peek(){
        if(!isEmpty())
            return list.get(1);
        return null;
    }
}