package lab8;

import java.util.*;
import java.util.concurrent.locks.Condition;

public class AsyncTaskQueue {
    Comparator<Task> comparator = new Comparator<>() {
        @Override
        public int compare(Task o1, Task o2) {
            return Integer.compare(o2.getPriority(), o1.getPriority());
        }
    };
    Queue<AsyncProducer> producers = new PriorityQueue<AsyncProducer>(10, comparator);
    Queue<AsyncConsumer> consumers = new PriorityQueue<AsyncConsumer>(10, comparator);
    Condition getCondition;
    Task previousTask = null;

    public AsyncTaskQueue(Condition condition) {
        this.getCondition = condition;
    }

    public Task poll() throws InterruptedException {

        Task t = this.peek();
        if (t instanceof AsyncProducer) {
            if (t != producers.peek())
                throw new IllegalArgumentException("The peek method returned value differs from producers peek!");
            return producers.poll();
        } else if (t instanceof AsyncConsumer) {
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
                Task result = candidate instanceof AsyncProducer ? consumers.peek() : producers.peek();
                previousTask = result;
                return result;
            } else {
                previousTask = candidate;
                return candidate;
            }
        }


    }


    public void addTask(Task task) {

        if (task instanceof AsyncProducer) {
            producers.add((AsyncProducer) task);
            getCondition.signal();
        } else if (task instanceof AsyncConsumer) {
            consumers.add((AsyncConsumer) task);
            getCondition.signal();
        } else
            throw new IllegalArgumentException("task is of unknown type!");
    }
}
