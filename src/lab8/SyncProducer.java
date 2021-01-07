package lab8;

import lab5.Monitor_Z;
import lab6.Product;

import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

public class SyncProducer extends Thread{
    AtomicBoolean running = new AtomicBoolean(false);
    SyncMonitor monitor;
    int capacity;

    public SyncProducer(SyncMonitor monitor, int capacity) {
        this.monitor = monitor;
        this.capacity = capacity;
    }

    @Override
    public void run() {
        running.set(true);
        while(running.get()) {
            try {
                int cnt = new Random().nextInt(capacity/2);
                int[] values = new int[cnt];
                for (int i = 0; i < cnt; i++)
                    values[i] = new Random().nextInt();
                monitor.produce(values);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void finish() {
        running.set(false);
    }
}
