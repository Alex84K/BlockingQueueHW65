package ait.mediation;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class BlkQueueImpl<T> implements BlkQueue<T> {

    private T t;
    List<T> tList = new LinkedList<>();
    Lock mutex = new ReentrantLock();
    Condition pushWaitingCondition = mutex.newCondition();
    Condition popWaitingCondition = mutex.newCondition();
    public BlkQueueImpl(int maxSize) {
        this.t = t;
        this.tList = tList;
    }

    @Override
    public void push(T message) {
        mutex.lock();
        try {
            while (this.t != null) {
                try {
                    pushWaitingCondition.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            this.t = t;
            popWaitingCondition.signal();
            tList.add(t);
        } finally {
            mutex.unlock();
        }
    }

    @Override
    public T pop() {
        mutex.lock();
        try {
            while (this.t == null) {
                try {
                    popWaitingCondition.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            T res = t;
            tList.set(0, null);
            pushWaitingCondition.signal();
            return res;
        } finally {
            mutex.unlock();
        }
    }
}
