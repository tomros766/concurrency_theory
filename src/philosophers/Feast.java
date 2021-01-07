package philosophers;

import lab2.MySemaphore;

import static java.lang.Thread.sleep;

public class Feast {

    public static void main(String[] args) {
        MySemaphore[] forks = new MySemaphore[5];
        Philosopher[] philosophers = new Philosopher[5];

        for (int i = 0; i < 5; i++) {
            forks[i] = new MySemaphore(true);
            forks[(i+1)%5] = new MySemaphore(true);
            philosophers[i] = new Philosopher(i, forks[i], forks[(i+1)%5]);
        }

        for (int i = 0; i < 5; i++) {
            int finalI = i;

            Thread thread = new Thread(() ->
            { while(true)
                try {
                    sleep(500);
                    philosophers[finalI].eat();
                    philosophers[finalI].think();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
            thread.start();
        }


    }








}
