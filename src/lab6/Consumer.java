package lab6;

import java.util.Optional;
import java.util.Random;

public class Consumer implements Runnable{
    Product product;
    MyConcurrentHashMap buffer;
    private static final int MAX_PROD = 50;
    public Consumer(Product p, MyConcurrentHashMap buffer) {
        this.product = p;
        this.buffer = buffer;
    }


    @Override
    public void run() {
        while (true) {
            int i = 0;
            try {
                i = product.getStart();
                Thread.sleep((int) (Math.random()*100));
                int res = buffer.consume(i);
                System.out.println("\tCONSUMER : Value from BUF: " + i + " has been consumed.");
                product.getFinish(res);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
