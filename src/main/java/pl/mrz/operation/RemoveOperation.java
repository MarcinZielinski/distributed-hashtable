package pl.mrz.operation;

import java.util.Map;

public class RemoveOperation extends Operation {
    public RemoveOperation(String key, String value) {
        super(key, value);
    }

    public RemoveOperation(String key) {
        super(key, null);
    }

    @Override
    public void processMap(Map<String, String> map) {
        map.remove(getKey());
    }
}
