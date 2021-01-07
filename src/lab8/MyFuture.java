package lab8;

import java.util.concurrent.Future;
import java.util.concurrent.locks.ReentrantLock;

public class MyFuture {
    private boolean ready;
    private int[] resource;

    public MyFuture() {
        this.ready = false;
    }

    public boolean isReady() {
        return ready;
    }

    public void setReady(boolean ready) {
        this.ready = ready;
    }

    public int[] getResource() throws IllegalAccessException {
        if (!ready) {
            throw new IllegalAccessException("Resource is not ready yet!");
        } else {
            return resource;
        }
    }

    public void setResource(int[] products) {
        this.resource = products;
    }
}
