package lab6;

import java.util.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;



public class Product {
    private final LinkedList<Integer> emptyEls;
    private final LinkedList<Integer> fullEls;
    STATE[] states;


    private final ReentrantLock lock = new ReentrantLock();
    private final Condition waitProds = lock.newCondition();
    private final Condition waitCons = lock.newCondition();

    public Product(int capacity) {
        this.emptyEls = new LinkedList<>();
        this.fullEls = new LinkedList<>();
        states = new STATE[capacity];
        for(int i = 0; i < capacity; i++) {
            emptyEls.add(i);
            states[i] = STATE.EMPTY;
        }
    }

    public int putStart() throws InterruptedException {
        lock.lock();
        while (emptyEls.isEmpty())
            waitProds.await();
        int p = emptyEls.pop();
        states[p] = STATE.BUSY;
        waitCons.signal();
        lock.unlock();
        return p;
    }


    public void putFinish(int i) {
        try {
            lock.lock();
            fullEls.add(i);
            states[i] = STATE.FILLED;
            waitCons.signal();
        } finally {
            lock.unlock();
        }

    }

    public int getStart() throws InterruptedException {
            lock.lock();
            while (fullEls.isEmpty())
                waitCons.await();
            int p = fullEls.pop();
            states[p] = STATE.EMPTY;
            return p;
    }

    public void getFinish(int i) {
        try {
            lock.lock();
            states[i] = STATE.EMPTY;
            emptyEls.add(i);
            waitProds.signal();
        } finally {
            lock.unlock();
        }
    }
}
