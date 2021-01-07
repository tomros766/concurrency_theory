package jcsp;

import org.jcsp.lang.*;

import java.util.*;


public class BufferHub implements CSProcess {
    SharedChannelOutput toProducers;
    SharedChannelOutput toConsumers;
    AltingChannelInput fromProducers;
    AltingChannelInput fromConsumers;
    BufferPiece[] bufferPieces;
    List<BufferPiece> producersBuffers;
    List<BufferPiece> consumersBuffers;
    List<BufferPiece> busyBuffers;
    Guard[] guards;
    Alternative alt;

    public BufferHub(SharedChannelOutput toProducers, SharedChannelOutput toConsumers, AltingChannelInput fromProducers, AltingChannelInput fromConsumers, BufferPiece[] bufferPieces) {
        this.toProducers = toProducers;
        this.toConsumers = toConsumers;
        this.fromProducers = fromProducers;
        this.fromConsumers = fromConsumers;
        this.bufferPieces = bufferPieces;

        producersBuffers = new ArrayList<>(Arrays.asList(bufferPieces));
        consumersBuffers = new ArrayList<>();
        busyBuffers = new ArrayList<>();

        guards = new Guard[] {fromProducers, fromConsumers};
        alt = new Alternative(guards);
    }


    @Override
    public void run() {
        while (true) {
            correctConsumers();
            correctProducers();

            int index = new Random().nextInt(2);
            if (index == 0) {
                if (!producersBuffers.isEmpty()) {
                    BufferPiece piece = producersBuffers.remove(0);
                    if (piece.state == BufferPiece.State.EMPTY) {
                        busyBuffers.add(piece);
                        One2OneChannelInt channel = piece.client;
                        Request request = Request.PRODUCE;
                        piece.hub.out().write(request);
                        toProducers.write(channel);
                    } else if (piece.state == BufferPiece.State.FULL){
                        consumersBuffers.add(piece);
                    } else if (piece.state == BufferPiece.State.BUSY) {
                        busyBuffers.add(piece);
                    }
                }
            } else {
                    if (!consumersBuffers.isEmpty()) {
                        BufferPiece piece = consumersBuffers.remove(0);
                        if (piece.state == BufferPiece.State.FULL) {
                            busyBuffers.add(piece);
                            One2OneChannelInt channel = piece.client;
                            Request request = Request.CONSUME;
                            piece.hub.out().write(request);
                            toConsumers.write(channel);
                        } else if (piece.state == BufferPiece.State.EMPTY) {
                            producersBuffers.add(piece);
                        } else if (piece.state == BufferPiece.State.BUSY) {
                            busyBuffers.add(piece);
                        }
                    }
                }
            correctBusy();
        }
    }

    private void correctBusy() {
        List<BufferPiece> toAdd = new ArrayList<>();
        for (BufferPiece piece : busyBuffers) {
            if (piece.state != BufferPiece.State.BUSY)
                toAdd.add(piece);
        }
        toAdd.forEach(b -> {
            if (b.state == BufferPiece.State.EMPTY){
                producersBuffers.add(b);
                busyBuffers.remove(b);
            }
            else if (b.state == BufferPiece.State.FULL) {
                consumersBuffers.add(b);
                busyBuffers.remove(b);
            }
        });
    }

    private void correctProducers() {
        List<BufferPiece> toAdd = new ArrayList<>();
        for (BufferPiece piece : producersBuffers) {
            if (piece.state != BufferPiece.State.EMPTY)
                toAdd.add(piece);
        }
        toAdd.forEach(b -> {
            if (b.state == BufferPiece.State.FULL){
                consumersBuffers.add(b);
                producersBuffers.remove(b);
            }
            else if (b.state == BufferPiece.State.BUSY) {
                busyBuffers.add(b);
                producersBuffers.remove(b);
            }
        });
    }

    private void correctConsumers() {
        List<BufferPiece> toAdd = new ArrayList<>();
        for (BufferPiece piece : consumersBuffers) {
            if (piece.state != BufferPiece.State.FULL)
                toAdd.add(piece);
        }
        toAdd.forEach(b -> {
            if (b.state == BufferPiece.State.EMPTY){
                producersBuffers.add(b);
                consumersBuffers.remove(b);
            }
            else if (b.state == BufferPiece.State.BUSY) {
                busyBuffers.add(b);
                consumersBuffers.remove(b);
            }
        });
    }
}
