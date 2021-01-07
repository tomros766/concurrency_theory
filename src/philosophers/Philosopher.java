package philosophers;

import lab2.MySemaphore;

import static java.lang.Thread.sleep;

public class Philosopher {
    MySemaphore left;
    MySemaphore right;
    int id;

    public Philosopher(int id, MySemaphore left, MySemaphore right) {
        this.id = id;
        this.left = left;
        this.right = right;
    }

    public void eat() throws InterruptedException {
//        if (id == 0) {
//            MySemaphore buff = left;
//            left = right;
//            right = buff;
//        }
        left.semWait();
        right.semWait();

        System.out.println("Philosopher " + id + " is eating.......");

        left.semRise();
        right.semRise();
    }

    public void think() {
        System.out.println("Philosopher " + id + " is thinking.......");
    }
}
