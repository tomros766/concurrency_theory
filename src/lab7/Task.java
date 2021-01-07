package lab7;

public interface Task {
    public int getPriority();
    public void increasePriority();
    public void decreasePriority();
    public int getMaxTaskSize();

    public int[] execute() throws IllegalAccessException, InterruptedException;

    public boolean areConditionsFulfilled();
}
