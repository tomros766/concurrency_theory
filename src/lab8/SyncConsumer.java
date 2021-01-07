package lab8;

import lab5.Monitor_Z;
import lab6.MyConcurrentHashMap;
import lab6.Product;

import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

public class SyncConsumer extends Thread{
    AtomicBoolean running = new AtomicBoolean(false);
    SyncMonitor monitor;
    int capacity;

    public SyncConsumer(SyncMonitor monitor, int capacity) {
        this.monitor = monitor;
        this.capacity = capacity;
    }

    @Override
    public void run() {
        running.set(true);
        while(running.get()) {
            try {
                int cnt = new Random().nextInt(capacity/2);
                int[] values = monitor.consume(cnt);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void finish() {
        running.set(false);
    }
}
