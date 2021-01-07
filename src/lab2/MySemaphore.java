package lab2;

public class MySemaphore {
    private boolean flag;

    public MySemaphore(boolean flag) {
        this.flag = flag;
    }

    public synchronized void semWait() throws InterruptedException {
        while (!flag) wait();
        flag = false;
    }

    public synchronized void semRise() {
        flag = true;
        notify();
    }
}
