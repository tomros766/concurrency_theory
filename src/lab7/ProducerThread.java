package lab7;

import lab8.MyFuture;

import java.util.Arrays;
import java.util.Random;

public class ProducerThread implements Runnable{
    Producer producer;
    Scheduler scheduler;
    MyFuture future;
    boolean isDispatched = true;

    public ProducerThread(Producer producer, Scheduler scheduler) {
        this.producer = producer;
        this.scheduler = scheduler;
    }
    @Override
    public void run() {
        while(true) {
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
                        System.out.println("PRODUCER[" + Thread.currentThread().getId() +  "]: The products: " + Arrays.toString(products) + " has been succesfully produced");
                        future = null;
                        isDispatched = true;
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
//                try {
//                    Thread.sleep(100);
////                    System.out.println("PRODUCER[" + Thread.currentThread().getId() +  "]: Minding my own business....");
//
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
            }

        }
    }
}
