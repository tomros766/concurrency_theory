package lab8;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class SyncMonitor {
    private final int[] products;
    private final int capacity;
    private int reading;
    private int writing;
    private int quantity;
    private int howManyConsumed;
    private final int delay;
    ReentrantLock lock = new ReentrantLock();
    Condition firstProducer = lock.newCondition();
    Condition firstConsumer = lock.newCondition();
    Condition restProducers = lock.newCondition();
    Condition restConsumers = lock.newCondition();

    boolean firstProducerEmpty = true;
    boolean firstConsumerEmpty = true;

    public SyncMonitor(int capacity, int delay) {
        this.capacity = capacity;
        this.products = new int[capacity];
        this.reading = 0;
        this.writing = 0;
        this.quantity = 0;
        this.delay = delay;
    }

    public int getHowManyConsumed() {
        return howManyConsumed;
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
            Thread.sleep(delay);

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
            howManyConsumed+=howMany;
            firstConsumerEmpty = true;
            Thread.sleep(delay);

            restConsumers.signal();
            firstProducer.signal();
        } finally {
            lock.unlock();
        }

        return res;
    }
}
