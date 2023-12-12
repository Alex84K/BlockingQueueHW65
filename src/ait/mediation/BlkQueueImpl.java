package ait.mediation;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class BlkQueueImpl<T> implements BlkQueue<T> {
    LinkedList<T> tList = new LinkedList<>();
    Lock mutex = new ReentrantLock();
    Condition pushWaitingCondition = mutex.newCondition();
    Condition popWaitingCondition = mutex.newCondition();
//    public BlkQueueImpl(int maxSize) {
//        this.tList = tList;
//    }


    public BlkQueueImpl(int maxSize) {
        this.tList = tList;
        this.mutex = mutex;
//        this.pushWaitingCondition = pushWaitingCondition;
//        this.popWaitingCondition = popWaitingCondition;
    }

    @Override
    public void push(T message) {

        mutex.lock();
        try {
            while (message != null) {
                try {
                    pushWaitingCondition.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            popWaitingCondition.signal();
            tList.addFirst(message);
        } finally {
            mutex.unlock();
        }
    }

    @Override
    public T pop() {
        mutex.lock();
        try {
            while (tList == null) {
                try {
                    popWaitingCondition.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            T res = tList.getLast();
            pushWaitingCondition.signal();
            tList.removeLast();
            return res;
        } finally {
            mutex.unlock();
        }
    }
}
