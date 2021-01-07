package lab8;

public class AsyncConsumer implements Task {
    private int priority = 0;
    private final AsyncBuffer buffer;
    private int howMany;

    public AsyncConsumer(AsyncBuffer buffer, int howMany) {
        this.buffer = buffer;
        this.howMany = howMany;
    }

    public void setHowMany(int howMany) {
        this.howMany = howMany;
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
            return buffer.get(howMany);
        }
    }

    @Override
    public boolean areConditionsFulfilled() {
        return howMany <= buffer.howManyElements();
    }
}
