package lab8;

import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

public class AsyncConsumerThread extends Thread{
    AsyncConsumer consumer;
    AtomicBoolean running = new AtomicBoolean(false);
    AsyncScheduler scheduler;
    MyFuture future;
    boolean isDispatched = true;
    public final int delay;

    public AsyncConsumerThread(AsyncConsumer consumer, AsyncScheduler scheduler, int delay) {
        this.consumer = consumer;
        this.scheduler = scheduler;
        this.delay = delay;
    }

    @Override
    public void run() {
        running.set(true);
        while (running.get()) {
            if (isDispatched) {
                int howMany = new Random().nextInt(consumer.getMaxTaskSize()) + 1;
                consumer.setHowMany(howMany);
                future = scheduler.enqueue(consumer);
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
            }
            try {
                Thread.sleep(delay);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void finish() {
        running.set(false);
    }
}
