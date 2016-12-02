package com.xtech.sultano.optimizedfilesender.observer;

public interface Subject {
    //methods to register and unregister observers
    public void register(Observer obj);
    public void unregister(Observer obj);
    public void notifyObservers(Object o);
}