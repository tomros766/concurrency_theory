package lab6;

import javax.swing.text.html.Option;
import java.util.Optional;
import java.util.Random;

public class Producer implements Runnable{
    Product product;
    MyConcurrentHashMap buffer;
    private static final int MAX_PROD = 10;
    public Producer(Product p, MyConcurrentHashMap buffer) {
        this.product = p;
        this.buffer = buffer;
    }


    @Override
    public void run() {
        while (true) {
            int p = 0;
            try {
                p = product.putStart();
                Thread.sleep((int) (Math.random()*100));
                buffer.produce(p, 1);
                System.out.println("PRODUCER : Value on BUF: " + p + " has been produced.");
                product.putFinish(p);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
