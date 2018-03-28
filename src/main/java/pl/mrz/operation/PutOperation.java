package pl.mrz.operation;

import java.util.Map;

public class PutOperation extends Operation {
    public PutOperation(String key, String value) {
        super(key, value);
    }

    @Override
    public void processMap(Map<String, String> map) {
        map.put(getKey(), getValue());
    }
}
