package com.kunlv.ddd.j.enode.core.configurations;

import java.util.Map;

/**
 * @author lvk618@gmail.com
 */
public class OptionSetting {
    private Map<String, String> options;

    public OptionSetting(Map<String, String> options) {
        this.options = options;
    }

    public void setOptionValue(String key, String value) {
        options.put(key, value);
    }

    public String getOptionValue(String key) {
        return options.get(key);
    }
}
