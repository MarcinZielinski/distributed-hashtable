package pl.mrz.operation;

import java.io.Serializable;
import java.util.Map;

public abstract class Operation implements Serializable{
    private String key;
    private String value;

    public Operation(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public abstract void processMap(Map<String, String> map);

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }
}
