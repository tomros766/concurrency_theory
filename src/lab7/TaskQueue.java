package lab7;

import lab8.Task;

import java.util.*;

import java.util.concurrent.locks.Condition;

public class TaskQueue {

    Comparator<Task> comparator = new Comparator<>() {
        @Override
        public int compare(Task o1, Task o2) {
            return Integer.compare(o2.getPriority(), o1.getPriority());
        }
    };
    Queue<Producer> producers = new PriorityQueue<Producer>(10, comparator);
    Queue<Consumer> consumers = new PriorityQueue<Consumer>(10, comparator);
    Condition getCondition;
    Task previousTask = null;

    public TaskQueue(Condition condition) {
        this.getCondition = condition;
    }

    public Task poll() throws InterruptedException {

        Task t = this.peek();
        if (t instanceof Producer) {
            if (t != producers.peek())
                throw new IllegalArgumentException("The peek method returned value differs from producers peek!");
            return producers.poll();
        } else if (t instanceof Consumer) {
            if (t != consumers.peek())
                throw new IllegalArgumentException("The peek method returned value differs from consumers peek!");
            return consumers.poll();
        } else {
            throw new IllegalArgumentException("The peek method returned neither Producer nor Consumer instance!");
        }

    }

    public Task peek() throws InterruptedException {

        while (producers.isEmpty() && consumers.isEmpty())
            getCondition.await();

        if (!producers.isEmpty() && consumers.isEmpty()) {
            previousTask = producers.peek();
            return producers.peek();
        } else if (producers.isEmpty()) {
            previousTask = consumers.peek();
            return consumers.peek();
        }

        if (producers.peek().getPriority() == consumers.peek().getPriority()) {
            List<Queue> tasks = new ArrayList<>();
            tasks.add(producers);
            tasks.add(consumers);
            int i = new Random().nextInt(2);
            Task candidate = (Task) tasks.get(i).peek();
            Task result = candidate == previousTask ? (Task) tasks.get(1 - i).peek() : candidate;
            previousTask = result;
            return result;
        } else {
            Task candidate = producers.peek().getPriority() > consumers.peek().getPriority() ? producers.peek() : consumers.peek();
            if (candidate == previousTask) {
                Task result = candidate instanceof Producer ? consumers.peek() : producers.peek();
                previousTask = result;
                return result;
            } else {
                previousTask = candidate;
                return candidate;
            }
        }


    }


    public void addTask(Task task) {

        if (task instanceof Producer) {
            producers.add((Producer) task);
            getCondition.signal();
        } else if (task instanceof Consumer) {
            consumers.add((Consumer) task);
            getCondition.signal();
        } else
            throw new IllegalArgumentException("task is of unknown type!");
    }

}
