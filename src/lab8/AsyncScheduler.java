package lab8;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class AsyncScheduler extends Thread {
    AtomicBoolean running = new AtomicBoolean(false);
    private final Map<Task, MyFuture> futures = new ConcurrentHashMap<>();
    private final Lock lock = new ReentrantLock();
    private final AsyncTaskQueue queue = new AsyncTaskQueue(lock.newCondition());

    public MyFuture enqueue(Task t) {
        try {
            lock.lock();
            if (!futures.containsKey(t)) {
                MyFuture future = new MyFuture();
                futures.put(t, future);
                queue.addTask(t);
                return future;
            } else {
                throw new IllegalArgumentException("The task already has a future object in queue!");
            }
        } finally {
            lock.unlock();
        }
    }

    private void dispatch() {
        try {
            lock.lock();
            Task task = queue.poll();
            if (!futures.containsKey(task))
                throw new IllegalArgumentException("The future object for dispatched task not found!");
            if (task.areConditionsFulfilled()) {
                int[] products = task.execute();
                futures.get(task).setReady(true);
                futures.get(task).setResource(products);
                futures.remove(task);
            } else {
                task.increasePriority();
                queue.addTask(task);
            }
        } catch (IllegalAccessException | InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }

    }

    @Override
    public void run() {
        running.set(true);
        while (running.get()) {
            dispatch();
        }
    }

    public void finish() {
        running.set(false);
    }
}
