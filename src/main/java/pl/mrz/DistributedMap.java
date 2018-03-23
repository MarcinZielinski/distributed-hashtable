package pl.mrz;

import org.jgroups.JChannel;

import java.util.HashMap;
import java.util.Map;

public class DistributedMap implements SimpleStringMap {

    private JChannel channel;
    private String channelName;
    private Map<String, String> map;


    public DistributedMap(String channelName) {
        this.channelName = channelName;
        this.channel = new JChannelCreator(channelName).getChannel();
        this.map = new HashMap<>();
        synchronize();
    }

    private void synchronize() {

    }

    @Override
    public boolean containsKey(String key) {
        return map.containsKey(key);
    }

    @Override
    public String get(String key) {
        return map.get(key);
    }

    @Override
    public String put(String key, String value) {
        return map.put(key, value);
    }

    @Override
    public String remove(String key) {
        return map.remove(key);
    }
}
