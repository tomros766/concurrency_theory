package lab5;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class Monitor_Z {
    private final int[] products;
    private final int capacity;
    private int reading;
    private int writing;
    private int quantity;
    ReentrantLock lock = new ReentrantLock();
    Condition firstProducer = lock.newCondition();
    Condition firstConsumer = lock.newCondition();
    Condition restProducers = lock.newCondition();
    Condition restConsumers = lock.newCondition();

    boolean firstProducerEmpty = true;
    boolean firstConsumerEmpty = true;

    public Monitor_Z(int capacity) {
        this.capacity = capacity;
        this.products = new int[capacity];
        this.reading = 0;
        this.writing = 0;
        this.quantity = 0;
    }

    public void produce(int[] product) throws InterruptedException {
        try {
            lock.lock();

            while (!firstProducerEmpty)  {
                restProducers.await();
            }
            while (quantity + product.length > capacity) {
                firstProducerEmpty = false;
                firstProducer.await();
            }
            for (int j : product) {
                writing = (writing + 1) % capacity;
                this.products[writing] = j;
                quantity++;
            }
            firstProducerEmpty = true;
            System.out.println("PRODUCER " + Thread.currentThread().getId() + " has finished producing");

            restProducers.signal();
            firstConsumer.signal();
        } finally {
            lock.unlock();
        }
    }

    public int[] consume(int howMany) throws InterruptedException {
        int[] res = new int[howMany];
        try {
            lock.lock();
            while (!firstConsumerEmpty) restConsumers.await();
            while (quantity < howMany) {
                firstConsumerEmpty = false;
                firstConsumer.await();
            }
            for (int i = 0; i < howMany; i++) {
                reading = (reading + 1) % capacity;
                res[i] = products[reading];
                products[i] = 0;
                quantity--;
            }
            firstConsumerEmpty = true;

            System.out.println("\tCONSUMER " + Thread.currentThread().getId() + " has finished consuming");

            restConsumers.signal();
            firstProducer.signal();
        } finally {
            lock.unlock();
        }

        return res;
    }
}
