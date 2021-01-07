package lab2;

import producer_consumer.Monitor;

import java.util.Arrays;
import java.util.Random;

public class Main {
    final static int capacity = 10;

    public static void main(String[] args) {
        Monitor monitor = new Monitor(capacity);
        Consumer[] consumers = new Consumer[3];
        Producer[] producers = new Producer[3];

        for (int i = 0; i < producers.length; i++) {
            producers[i] = new Producer(monitor);
            producers[i].start();
        }
        for (int i = 0; i < consumers.length; i++) {
            consumers[i] = new Consumer(monitor);
            consumers[i].start();
        }

//        Arrays.stream(threads).forEach((t) -> {
//            try {
//                t.join();
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        });

//        System.out.println(myInt.getVal());
    }

    private static class Producer extends Thread {
        Monitor monitor;

        public Producer(Monitor monitor) {
            this.monitor = monitor;
        }

        @Override
        public void run() {
            int x = 0;
            while(true) {
                try {
                    for (int i = 0; i < new Random().nextInt(capacity); i++) {
                        monitor.produce(x);
                        System.out.println("Value: " + x + " has been produced");
                        x = (x + 1) % capacity;
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static class Consumer extends Thread {
        Monitor monitor;

        public Consumer(Monitor monitor) {
            this.monitor = monitor;
        }

        @Override
        public void run() {
            while(true) {
                try {
                    for (int i = 0; i < new Random().nextInt(capacity); i++) {
                        int val = monitor.consume();
                        System.out.println("\tValue: " + val + " has been consumed");
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
