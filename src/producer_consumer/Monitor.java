package producer_consumer;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class Monitor {
    private final int[] products;
    private final int capacity;
    private int reading;
    private int writing;
    private int quantity;
    ReentrantLock lock = new ReentrantLock();
    Condition producerCondition = lock.newCondition();
    Condition consumerCondition = lock.newCondition();

    public Monitor(int capacity) {
        this.capacity = capacity;
        this.products = new int[capacity];
        this.reading = 0;
        this.writing = 0;
        this.quantity = 0;
    }

    public void produce(int product) throws InterruptedException {
            try {
                lock.lock();
                while (quantity >= capacity) producerCondition.await();
                this.products[writing] = product;
                writing = (writing + 1) % capacity;
                quantity++;
                consumerCondition.signal();
            } finally {
                lock.unlock();
            }
    }

    public int consume() throws InterruptedException {
        int res;
            try {
                lock.lock();
                while (quantity == 0) consumerCondition.await();
                producerCondition.signal();
                reading = (reading + 1) % capacity;
                res = products[reading];
                products[reading] = 0;
                quantity--;
            } finally {
                lock.unlock();
            }

        return res;

    }

}
