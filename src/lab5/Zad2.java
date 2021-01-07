package lab5;

import java.util.Arrays;
import java.util.Random;

public class Zad2 {
    final static int capacity = 30;

    public static void main(String[] args) {
        Monitor_Z monitor = new Monitor_Z(capacity);
        Consumer[] consumers = new Consumer[2];
        Producer[] producers = new Producer[10];

        for (int i = 0; i < producers.length; i++) {
            producers[i] = new Producer(monitor);
            producers[i].start();
        }
        for (int i = 0; i < consumers.length; i++) {
            consumers[i] = new Consumer(monitor);
            consumers[i].start();
        }
    }

    private static class Producer extends Thread {
        Monitor_Z monitor;

        public Producer(Monitor_Z monitor) {
            this.monitor = monitor;
        }

        @Override
        public void run() {
            int x = 0;
            while(true) {
                try {
                    int cnt = new Random().nextInt(2);
                    int[] values = new int[cnt];
                    for (int i = 0; i < cnt; i++)
                        values[i] = new Random().nextInt();
                    monitor.produce(values);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static class Consumer extends Thread {
        Monitor_Z monitor;

        public Consumer(Monitor_Z monitor) {
            this.monitor = monitor;
        }

        @Override
        public void run() {
            while(true) {
                try {
                    int cnt = new Random().nextInt(2);
                    int[] values = monitor.consume(cnt + 3);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
