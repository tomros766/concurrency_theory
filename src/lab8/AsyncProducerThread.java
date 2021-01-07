package lab8;

import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

public class AsyncProducerThread extends Thread{
    AsyncProducer producer;
    AtomicBoolean running = new AtomicBoolean(false);
    AsyncScheduler scheduler;
    MyFuture future;
    final int delay;
    boolean isDispatched = true;

    public AsyncProducerThread(AsyncProducer producer, AsyncScheduler scheduler, int delay) {
        this.producer = producer;
        this.scheduler = scheduler;
        this.delay = delay;
    }
    @Override
    public void run() {
        running.set(true);
        while(running.get()) {
            if (isDispatched) {
                int[] products = new int[new Random().nextInt(producer.getMaxTaskSize()) + 1];
                Arrays.fill(products, (int) Thread.currentThread().getId());
                producer.setProducts(products);
                future = scheduler.enqueue(producer);
                isDispatched = false;
            } else {
                if (future != null && future.isReady()) {
                    try {
                        int[] products = future.getResource();
                        future = null;
                        isDispatched = true;
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
                try {
                    Thread.sleep(delay);

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    public void finish() {

        running.set(false);
    }
}
