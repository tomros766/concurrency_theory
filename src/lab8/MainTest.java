package lab8;

import java.util.*;
import java.util.stream.Stream;

public class MainTest {
    public static void main(String[] args) throws InterruptedException {

        int producersCnt;
        int consumersCnt;
        String mode;
        int limit;
        int bufferSize;
        int additionalWorkLength;
        int bufferOperationsDelay;

        if (args.length < 7) {
            Scanner scanner = new Scanner(System.in);
            System.out.print("Enter number of producers: ");
            producersCnt = scanner.nextInt();
            System.out.print("Enter number of consumers: ");
            consumersCnt = scanner.nextInt();
            System.out.print("Enter limited Value: (t - time, v - values produced and consumed): ");
            mode = scanner.next();
            System.out.print("Enter limit (items or seconds): ");
            limit = scanner.nextInt();
            System.out.print("Enter buffer size: ");
            bufferSize = scanner.nextInt();
            System.out.print("Enter length of additional work in async threads (milliseconds): ");
            additionalWorkLength = scanner.nextInt();
            System.out.print("Enter delay of buffer operations (milliseconds): ");
            bufferOperationsDelay = scanner.nextInt();
        } else {
            producersCnt = Integer.parseInt(args[0]);
            consumersCnt = Integer.parseInt(args[1]);
            mode = args[2];
            limit = Integer.parseInt(args[3]);
            bufferSize = Integer.parseInt(args[4]);
            additionalWorkLength = Integer.parseInt(args[5]);
            bufferOperationsDelay = Integer.parseInt(args[6]);
        }

        SyncProducer[] syncProducersThreads = new SyncProducer[producersCnt];
        SyncConsumer[] syncConsumersThreads = new SyncConsumer[consumersCnt];
        AsyncProducerThread[] asyncProducersThreads = new AsyncProducerThread[producersCnt];
        AsyncProducer[] asyncProducers = new AsyncProducer[producersCnt];
        AsyncConsumerThread[] asyncConsumersThreads = new AsyncConsumerThread[consumersCnt];
        AsyncConsumer[] asyncConsumers = new AsyncConsumer[consumersCnt];

        SyncMonitor syncMonitor = new SyncMonitor(bufferSize, bufferOperationsDelay);

        AsyncScheduler asyncScheduler = new AsyncScheduler();
        AsyncBuffer asyncBuffer = new AsyncBuffer(bufferSize, bufferOperationsDelay);

        for (int i = 0; i < producersCnt; i++) {
            syncProducersThreads[i] = new SyncProducer(syncMonitor, bufferSize);
            asyncProducers[i] = new AsyncProducer(asyncBuffer, null);
            asyncProducersThreads[i] = new AsyncProducerThread(asyncProducers[i], asyncScheduler, additionalWorkLength);
        }
        for (int i = 0; i < consumersCnt; i++) {
            syncConsumersThreads[i] = new SyncConsumer(syncMonitor, bufferSize);
            asyncConsumers[i] = new AsyncConsumer(asyncBuffer, 0);
            asyncConsumersThreads[i] = new AsyncConsumerThread(asyncConsumers[i], asyncScheduler, additionalWorkLength);
        }

        asyncScheduler.start();

        if (mode.equals("t")) {
            performTimeLimitedTest(syncProducersThreads, syncConsumersThreads, asyncProducersThreads, asyncConsumersThreads, syncMonitor, asyncBuffer, limit, asyncScheduler);
        } else if (mode.equals("v")) {
            performValuesLimitedTest(syncProducersThreads, syncConsumersThreads, asyncProducersThreads, asyncConsumersThreads, syncMonitor, asyncBuffer, limit, asyncScheduler);
        }


    }

    private static void performValuesLimitedTest(SyncProducer[] syncProducersThreads, SyncConsumer[] syncConsumersThreads, AsyncProducerThread[] asyncProducersThreads, AsyncConsumerThread[] asyncConsumersThreads, SyncMonitor syncBuffer, AsyncBuffer asyncBuffer, int limit, AsyncScheduler scheduler) throws InterruptedException {
        Stream.of(syncProducersThreads, syncConsumersThreads).flatMap(Arrays::stream).forEach(Thread::start);
        long syncStart = System.currentTimeMillis();
        long syncEnd = System.currentTimeMillis();
        Stream.of(asyncProducersThreads, asyncConsumersThreads).flatMap(Arrays::stream).forEach(Thread::start);
        long asyncStart = System.currentTimeMillis();
        long asyncEnd = System.currentTimeMillis();
        boolean syncFinished = false;
        boolean asyncFinished = false;
        do {
            if (syncBuffer.getHowManyConsumed() >= limit && !syncFinished) {
                Arrays.stream(syncProducersThreads).forEach(SyncProducer::finish);
                System.out.println("SYNC FINISHED");
                syncEnd = System.currentTimeMillis();
                syncFinished=true;
            }
            if (asyncBuffer.getHowManyConsumed() >= limit && !asyncFinished) {
                Arrays.stream(asyncProducersThreads).forEach(AsyncProducerThread::finish);
                scheduler.finish();
                System.out.println("ASYNC FINISHED");
                asyncEnd = System.currentTimeMillis();
                asyncFinished=true;
            }
            Thread.sleep(100);
        } while (!syncFinished || !asyncFinished);
        System.out.println("STOPPED");

        long syncTime = syncEnd - syncStart;
        long asyncTime = asyncEnd - asyncStart;
        System.out.println("RESULTS : syncTime: " + syncTime + ", asyncTime: " + asyncTime + ". The difference is: " + (syncTime - asyncTime));


    }

    private static void performTimeLimitedTest(SyncProducer[] syncProducersThreads, SyncConsumer[] syncConsumersThreads, AsyncProducerThread[] asyncProducersThreads, AsyncConsumerThread[] asyncConsumersThreads, SyncMonitor syncBuffer, AsyncBuffer asyncBuffer, int limit, AsyncScheduler scheduler) throws InterruptedException {
        Stream.of(syncProducersThreads, syncConsumersThreads).flatMap(Arrays::stream).forEach(Thread::start);
        Stream.of(asyncProducersThreads, asyncConsumersThreads).flatMap(Arrays::stream).forEach(Thread::start);


        Thread.sleep(limit*1000);
        
        Arrays.stream(asyncProducersThreads).forEach(AsyncProducerThread::finish);

        Arrays.stream(syncProducersThreads).forEach(SyncProducer::finish);
        scheduler.finish();

        System.out.println("RESULTS : sync: " + syncBuffer.getHowManyConsumed() + ", async: " + asyncBuffer.getHowManyConsumed() + ". The difference is: " + (syncBuffer.getHowManyConsumed() - asyncBuffer.getHowManyConsumed()));

        Arrays.stream(asyncConsumersThreads).forEach(AsyncConsumerThread::finish);
        Arrays.stream(asyncProducersThreads).forEach(AsyncProducerThread::finish);
        scheduler.finish();
        Arrays.stream(syncConsumersThreads).forEach(SyncConsumer::finish);
        Arrays.stream(syncProducersThreads).forEach(SyncProducer::finish);


    }
}
