package jcsp;

import org.jcsp.lang.CSProcess;
import org.jcsp.lang.Channel;
import org.jcsp.lang.One2OneChannel;
import org.jcsp.lang.One2OneChannelInt;

public class BufferPiece implements CSProcess {
    public enum State {
        EMPTY,
        FULL,
        BUSY
    }
    One2OneChannel hub;
    One2OneChannelInt client;
    State state = State.EMPTY;
    Request request = null;

    int slot = -1;

    public BufferPiece(One2OneChannel hub) {
        this.hub = hub;
        client = Channel.one2oneInt();
    }


    @Override
    public void run() {
        while (true) {
            request = (Request) hub.in().read();
            state = State.BUSY;
            if (request == Request.CONSUME) {
                client.out().write(slot);
                slot = -1;
                state = State.EMPTY;
            } else if (request == Request.PRODUCE) {
                slot = client.in().read();
                state = State.FULL;
            }
        }
    }


}
