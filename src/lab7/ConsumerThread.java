package lab7;

import lab8.MyFuture;

import java.util.Arrays;
import java.util.Random;

public class ConsumerThread implements Runnable{
    Consumer consumer;
    Scheduler scheduler;
    MyFuture future;
    boolean isDispatched = true;

    public ConsumerThread(Consumer consumer, Scheduler scheduler) {
        this.consumer = consumer;
        this.scheduler = scheduler;
    }

    @Override
    public void run() {
        while (true) {
            if (isDispatched) {
                int howMany = new Random().nextInt(consumer.getMaxTaskSize()) + 1;
                consumer.setHowMany(howMany);
                future = scheduler.enqueue(consumer);
                isDispatched = false;
            } else {
                if (future != null && future.isReady()) {
                    try {
                        int[] products = future.getResource();
                        System.out.println("CONSUMER[" + Thread.currentThread().getId() + "]: The products: " + Arrays.toString(products) + " has been succesfully consumed");
                        future = null;
                        isDispatched = true;
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
//            try {
//                Thread.sleep(100);
////                System.out.println("CONSUMER[" + Thread.currentThread().getId() + "] Minding my own business....");
//
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
        }
    }
}
