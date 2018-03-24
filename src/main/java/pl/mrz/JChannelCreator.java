package pl.mrz;

import org.jgroups.JChannel;
import org.jgroups.protocols.*;
import org.jgroups.protocols.pbcast.*;
import org.jgroups.stack.ProtocolStack;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class JChannelCreator {

    public JChannel getChannel() {
        JChannel channel = null;
        try {
            channel = new JChannel(false);
            ProtocolStack stack = new ProtocolStack();
            channel.setProtocolStack(stack);
            addProtocols(stack);
            stack.init();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return channel;
    }

    private void addProtocols(ProtocolStack stack) throws UnknownHostException {
        stack.addProtocol(new UDP().setValue("mcast_group_addr", InetAddress.getByName("224.0.0.133")))
                .addProtocol(new PING())
                .addProtocol(new MERGE3())
                .addProtocol(new FD_SOCK())
                .addProtocol(new FD_ALL().setValue("timeout", 12000).setValue("interval", 3000))
                .addProtocol(new VERIFY_SUSPECT())
                .addProtocol(new BARRIER())
                .addProtocol(new NAKACK2())
                .addProtocol(new UNICAST3())
                .addProtocol(new STABLE())
                .addProtocol(new GMS())
                .addProtocol(new UFC())
                .addProtocol(new MFC())
                .addProtocol(new FRAG2())
                .addProtocol(new STATE_TRANSFER())
                .addProtocol(new SEQUENCER()) // porzadkowanie komunikatow
                .addProtocol(new FLUSH()); // porzadkowanie komunikatow
    }
}
