package lab7;

import java.util.Arrays;
import java.util.Comparator;

public class Main {


    public static void main(String[] args) {
        Scheduler scheduler = new Scheduler();
        Buffer buffer = new Buffer(40);
        Thread[] producers = new Thread[100];
        Thread[] consumers = new Thread[100];
        Thread schedulerThread = new Thread(scheduler);

        schedulerThread.start();

        for (int i = 1; i < producers.length; i++) {
            producers[i] = new Thread(new ProducerThread(new Producer(buffer, null), scheduler));
            producers[i].start();
        }

        for (int i = 0; i < consumers.length; i++) {
            consumers[i] = new Thread(new ConsumerThread(new Consumer(buffer, 0), scheduler));
            consumers[i].start();
        }


    }
}
