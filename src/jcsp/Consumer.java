package jcsp;

import org.jcsp.lang.*;

public class Consumer implements CSProcess {
    SharedChannelInput inputHub;
    ChannelOutput outputHub;

    public Consumer(SharedChannelInput inputHub, ChannelOutput outputHub) {
        this.inputHub = inputHub;
        this.outputHub = outputHub;
    }

    @Override
    public void run() {
        while (true) {
            One2OneChannelInt channel = (One2OneChannelInt) inputHub.read();
            int product = channel.in().read();
            System.out.println("\t" + product + " CONSUMED");
        }
    }
}
