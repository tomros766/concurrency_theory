package jcsp;

import org.jcsp.lang.*;

public class Main {
    private static final int PRODUCERS_COUNT = 1000;
    private static final int CONSUMERS_COUNT = 1000;
    private static final int BUFFER_SIZE = 1000;

    public static void main(String[] args) {
        Any2AnyChannel producersToHub = Channel.any2any(PRODUCERS_COUNT);
        One2OneChannel hubToProducers = Channel.one2one(PRODUCERS_COUNT);
        Any2AnyChannel consumersToHub = Channel.any2any(CONSUMERS_COUNT);
        One2OneChannel hubToConsumers = Channel.one2one(CONSUMERS_COUNT);
        Producer[] producers = new Producer[PRODUCERS_COUNT];
        Consumer[] consumers = new Consumer[CONSUMERS_COUNT];
        BufferPiece[] bufferPieces = new BufferPiece[BUFFER_SIZE];

        for (int i = 0; i < PRODUCERS_COUNT; i++) {
            producers[i] = new Producer(producersToHub.in(), hubToProducers.out());
        }
        for (int i = 0; i < CONSUMERS_COUNT; i++) {
            consumers[i] = new Consumer(consumersToHub.in(), hubToConsumers.out());
        }
        for (int i = 0; i < BUFFER_SIZE; i++) {
            bufferPieces[i] = new BufferPiece(Channel.one2one());
        }

        BufferHub hub = new BufferHub(producersToHub.out(), consumersToHub.out(), hubToProducers.in(), hubToConsumers.in(), bufferPieces);

        CSProcess[] procList = new CSProcess[producers.length + consumers.length + bufferPieces.length + 1];
        System.arraycopy(producers, 0, procList, 0, producers.length);
        System.arraycopy(consumers, 0, procList, producers.length, consumers.length);
        System.arraycopy(bufferPieces, 0, procList, producers.length + consumers.length, bufferPieces.length);
        procList[procList.length - 1] = hub;
        Parallel par = new Parallel(procList);
        par.run();
    }
}
