package pl.mrz;

import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import org.jgroups.*;
import org.jgroups.util.Util;
import pl.mrz.operation.Operation;
import pl.mrz.operation.PutOperation;
import pl.mrz.operation.RemoveOperation;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DistributedMap implements SimpleStringMap {

    private JChannel channel;
    private String channelName;
    private final ObservableMap<String, String> map;

    public DistributedMap(String channelName) {
        this.channelName = channelName;
        this.channel = new JChannelCreator().getChannel();
        this.map = FXCollections.observableHashMap();
        registerListener();
        connect();
        synchronize();
    }

    private void connect() {
        try {
            channel.connect(channelName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void registerListener() {
        channel.setReceiver(new ReceiverAdapter() {
            public void receive(Message msg) {
                Operation operation = (Operation) msg.getObject();
                synchronized (map) {
                    operation.processMap(map);
                }
                System.out.println("received entry " + msg);
            }

            public void viewAccepted(View view) {
                System.out.println("received view " + view);
                if (view instanceof MergeView) {
                    MergeView tmp = (MergeView) view;
                    ViewHandler handler = new ViewHandler(channel, (MergeView) view);
                    // requires separate thread as we don't want to block JGroups
                    handler.start();
                }
            }

            // called in the state provider
            public void getState(OutputStream output) throws Exception {
                synchronized (map) {
                    Util.objectToStream(new HashMap<>(map), new DataOutputStream(output));
                }
            }

            // called on the state requester
            public void setState(InputStream input) throws Exception {
                Map<String, String> state;
                state = (Map<String, String>) Util.objectFromStream(new DataInputStream(input));
                synchronized (map) {
                    map.clear();
                    map.putAll(state);
                }
                System.out.println(map.size() + " entries in distributed hash map):");
                for (Map.Entry<String, String> str : map.entrySet()) {
                    System.out.println(str);
                }
            }
        });
    }

    private void synchronize() {
        try {
            channel.getState(null, 10_000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public synchronized boolean containsKey(String key) {
        return map.containsKey(key);
    }

    @Override
    public synchronized String get(String key) {
        return map.get(key);
    }

    @Override
    public String put(String key, String value) {
        String res = null;
        try {
            Operation operation = new PutOperation(key, value);
            Message msg = new Message(null, null, operation);
            channel.send(msg);
            synchronized (this) {
                res = map.put(key, value);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }

    @Override
    public synchronized String remove(String key) {
        String res = null;
        try {
            Operation operation = new RemoveOperation(key);
            Message msg = new Message(null, null, operation);
            channel.send(msg);
            synchronized (this) {
                res = map.remove(key);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }

    public ObservableMap<String, String> getHashMap() {
        return map;
    }

    private static class ViewHandler extends Thread {
        JChannel ch;
        MergeView view;

        private ViewHandler(JChannel ch, MergeView view) {
            this.ch = ch;
            this.view = view;
        }

        public void run() {
            List<View> subgroups = view.getSubgroups();
            View tmp_view = subgroups.get(0); // picks the first
            Address local_addr = ch.getAddress();
            if (!tmp_view.getMembers().contains(local_addr)) {
                System.out.println("Not member of the new primary partition ("
                        + tmp_view + "), will re-acquire the state");
                try {
                    ch.getState(null, 30000);
                } catch (Exception ignored) {
                }
            } else {
                System.out.println("Not member of the new primary partition ("
                        + tmp_view + "), will do nothing");
            }
        }
    }
}
