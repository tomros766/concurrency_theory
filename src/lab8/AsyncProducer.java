package lab8;

public class AsyncProducer implements Task {
    private int priority = 0;
    private final AsyncBuffer buffer;
    private int[] products;

    public AsyncProducer(AsyncBuffer buffer, int[] products) {
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
    public int[] execute() throws IllegalAccessException, InterruptedException {
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
