package lab7;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Buffer {
    private final List<Integer> buffer;
    private final int capacity;

    public Buffer(int capacity) {
        this.buffer = new ArrayList<>(capacity);
        this.capacity = capacity;
    }

    public void put(int[] p) throws IllegalAccessException {
        if (buffer.size() + p.length > capacity)
            throw new IllegalAccessException("There is no more room for another product!");
        if (p.length > capacity / 2)
            throw new IllegalArgumentException("The thread is trying to produce too many products!");

        Arrays.stream(p).forEach(buffer::add);
    }

    public int[] get(int howMany) throws IllegalAccessException {
        if (this.buffer.size() < howMany)
            throw new IllegalAccessException("There is not enough products to consume!");
        if (howMany > capacity/2)
            throw new IllegalArgumentException("The thread is trying to consume too many products!");

        int[] result = new int[howMany];
        for (int i = 0; i < howMany; i++) {
            result[i] = buffer.remove(0);
        }
        return result;
    }

    public int getCapacity() {
        return capacity;
    }

    public int howManyElements() {
        return buffer.size();
    }
}
