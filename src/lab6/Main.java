package lab6;


public class Main {
    final static int capacity = 50;
    final static MyConcurrentHashMap hashMap = new MyConcurrentHashMap(capacity);

    public static void main(String[] args) {
        Product monitor = new Product(capacity);
        Thread[] consumers = new Thread[10];
        Thread[] producers = new Thread[10];

        for (int i = 0; i < producers.length; i++) {
            producers[i] = new Thread(new Producer(monitor, hashMap));
            producers[i].start();
        }
        for (int i = 0; i < consumers.length; i++) {
            consumers[i] = new Thread(new Consumer(monitor, hashMap));
            consumers[i].start();
        }
    }
}
