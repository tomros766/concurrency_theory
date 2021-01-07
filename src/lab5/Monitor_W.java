package lab5;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class Monitor_W {
    private final int[] products;
    private final int capacity;
    private int reading;
    private int writing;
    private int quantity;
    ReentrantLock lock = new ReentrantLock();
    Condition firstProducer = lock.newCondition();
    int firstProdCnt = 0;
    Condition firstConsumer = lock.newCondition();
    int firstConsCnt = 0;
    Condition restProducers = lock.newCondition();
    int restProdCnt = 0;
    Condition restConsumers = lock.newCondition();
    int restConsCnt = 0;

    public Monitor_W(int capacity) {
        this.capacity = capacity;
        this.products = new int[capacity];
        this.reading = 0;
        this.writing = 0;
        this.quantity = 0;
    }

    public void produce(int[] product) throws InterruptedException {
        try {
            lock.lock();

            while (lock.hasWaiters(firstProducer))  {
                restProdCnt++;
                restProducers.await();
                System.out.println("restProducers: " + restProdCnt);
                restProdCnt--;
            }
            while (quantity + product.length > capacity) {
                firstProdCnt++;
                firstProducer.await();
                System.out.println("firstProducer: " + firstProdCnt);
                firstProdCnt--;
            }
            for (int j : product) {
                writing = (writing + 1) % capacity;
                this.products[writing] = j;
                quantity++;
            }

            restProducers.signal();
            firstConsumer.signal();
        } finally {
            printQueuesState();
            lock.unlock();
        }
    }

    public int[] consume(int howMany) throws InterruptedException {
        int[] res = new int[howMany];
        try {
            lock.lock();

            while (lock.hasWaiters(firstConsumer)) {
                restConsCnt++;
                restConsumers.await();
                System.out.println("restConsumers: " + restConsCnt);
                restConsCnt--;
            }
            while (quantity < howMany) {
                firstConsCnt++;
                firstConsumer.await();
                System.out.println("firstConsumer: " + firstConsCnt);
                firstConsCnt--;
            }
            for (int i = 0; i < howMany; i++) {
                reading = (reading + 1) % capacity;
                res[i] = products[reading];
                products[i] = 0;
                quantity--;
            }
            restConsumers.signal();
            firstProducer.signal();
        } finally {
            printQueuesState();
            lock.unlock();
        }

        return res;
    }

    private void printQueuesState() throws InterruptedException {
   }
}
