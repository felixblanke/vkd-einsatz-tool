package de.vkd.framework;

import java.util.HashMap;

public class VarSet {
    private HashMap<String, String> hm = new HashMap<String, String>();

    public void put(String varName, String varVal) {
        hm.put(varName, varVal);
    }

    public String get(String varName) {
        return hm.get(varName);
    }
}
