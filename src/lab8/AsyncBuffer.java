package lab8;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AsyncBuffer {
    private final List<Integer> buffer;
    private final int capacity;
    private int howManyConsumed = 0;
    private final int delay;

    public AsyncBuffer(int capacity, int delay) {
        this.buffer = new ArrayList<>(capacity);
        this.capacity = capacity;
        this.delay = delay;
    }

    public void put(int[] p) throws IllegalAccessException, InterruptedException {
        if (buffer.size() + p.length > capacity)
            throw new IllegalAccessException("There is no more room for another product!");
        if (p.length > capacity / 2)
            throw new IllegalArgumentException("The thread is trying to produce too many products!");

        Thread.sleep(delay);
        Arrays.stream(p).forEach(buffer::add);
    }

    public int[] get(int howMany) throws IllegalAccessException, InterruptedException {
        if (this.buffer.size() < howMany)
            throw new IllegalAccessException("There is not enough products to consume!");
        if (howMany > capacity/2)
            throw new IllegalArgumentException("The thread is trying to consume too many products!");

        int[] result = new int[howMany];
        for (int i = 0; i < howMany; i++) {
            result[i] = buffer.remove(0);
        }
        Thread.sleep(delay);
        howManyConsumed += howMany;
        return result;
    }

    public int getCapacity() {
        return capacity;
    }

    public int howManyElements() {
        return buffer.size();
    }

    public int getHowManyConsumed() {
        return howManyConsumed;
    }
}
