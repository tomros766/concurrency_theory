package jcsp;


import org.jcsp.lang.*;

public class Producer implements CSProcess {
    SharedChannelInput inputHub;
    ChannelOutput outputHub;

    public Producer(SharedChannelInput inputHub, ChannelOutput outputHub) {
        this.inputHub = inputHub;
        this.outputHub = outputHub;
    }

    @Override
    public void run() {
        while (true) {

            One2OneChannelInt channel = (One2OneChannelInt) inputHub.read();
            int product = (int) Thread.currentThread().getId();
            channel.out().write(product);
            System.out.println(product + " PRODUCED");
        }
    }
}
