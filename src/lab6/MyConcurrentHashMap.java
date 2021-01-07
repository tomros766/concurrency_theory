package lab6;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MyConcurrentHashMap {
    private final Map<Integer, Integer> buffer;
    private final int capacity;
    int tickets = 0;

    public MyConcurrentHashMap(int capacity) {
        this.capacity = capacity;
        this.buffer = new ConcurrentHashMap<>(capacity);
        for (int i = 0; i < capacity; i++) {
            this.buffer.put(i, 0);
        }

    }

    public void produce(int key, int value) {
        tickets++;
        System.out.println("Number of tickets: " + tickets);
        this.buffer.replace(key, value);
        tickets--;
    }

    public int consume(int key) {
        tickets++;
        System.out.println("Number of tickets: " + tickets);
        int result = this.buffer.get(key);
        this.buffer.replace(key, 0);
        tickets--;
        return result;
    }

    public int getCapacity() {
        return this.capacity;
    }


}
