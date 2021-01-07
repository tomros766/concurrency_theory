package lab7;

import lab8.Task;

public class Producer implements Task {
    private int priority = 0;
    private final Buffer buffer;
    private int[] products;

    public Producer(Buffer buffer, int[] products) {
        this.buffer = buffer;
        this.products = products;
    }

    public void setProducts(int[] products) {
        if (products.length > getMaxTaskSize()) {
            throw new IllegalArgumentException("Trying to set too many products for one producer!");
        }
        this.products = products;
    }

    @Override
    public int getPriority() {
        return this.priority;
    }

    @Override
    public void increasePriority() {
        this.priority++;
    }

    @Override
    public void decreasePriority() {
        this.priority--;
    }

    @Override
    public int getMaxTaskSize() {
        return buffer.getCapacity()/2;
    }

    @Override
    public int[] execute() throws IllegalAccessException {
        if (!areConditionsFulfilled())
            throw new IllegalAccessException("Cannot execute producer method - conditions aren't fullfiled!");
        else {
            buffer.put(products);
            return products;
        }
    }

    @Override
    public boolean areConditionsFulfilled() {
        return buffer.howManyElements() + products.length <= buffer.getCapacity();
    }

}
