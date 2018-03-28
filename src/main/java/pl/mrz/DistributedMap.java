package pl.mrz;

import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import org.jgroups.JChannel;
import org.jgroups.Message;
import org.jgroups.ReceiverAdapter;
import org.jgroups.View;
import org.jgroups.util.Util;
import pl.mrz.operation.Operation;
import pl.mrz.operation.PutOperation;
import pl.mrz.operation.RemoveOperation;

import java.io.*;
import java.util.AbstractMap;
import java.util.HashMap;
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

    private void eventLoop() {
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        while (true) {
            try {
                System.out.print("> ");
                System.out.flush();
                String line = in.readLine().toLowerCase();
                if (line.startsWith("quit") || line.startsWith("exit"))
                    break;
                if (line.startsWith("view"))
                    System.out.println(map);
                Message msg = new Message(null, null, new AbstractMap.SimpleImmutableEntry<>(line.split(" ")[0], line.split(" ")[1]));
                channel.send(msg);
            } catch (Exception ignored) {
            }
        }
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
            //Map.Entry<String, String> newEntry = new AbstractMap.SimpleImmutableEntry<>(key, value);
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

    public Set<String> getKeySet() {
        return map.keySet();
    }

    public ObservableMap<String, String> getHashMap() {
        return map;
    }
}
