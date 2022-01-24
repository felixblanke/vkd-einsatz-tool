package de.vkd.xml;

import java.util.ArrayList;
import java.util.List;

public class XmlEntry {
    private final List<String> elementNames;
    private final String value;

    public XmlEntry(List<String> elementNames, String lastElementName, String value) {
        this.elementNames = new ArrayList<String>(elementNames);
        this.elementNames.add(lastElementName);
        this.value = value;
    }
    public XmlEntry(List<String> elementNames, String value) {
        this.elementNames = new ArrayList<String>(elementNames);
        this.value = value;
    }

    public List<String> getElementNames() {
        return elementNames;
    }
    public String getValue() {
        return value;
    }
    @Override
    public String toString() {
        return "XmlEntry " + getStringFromStringList(getElementNames()) + ": " + getValue();
    }
    private String getStringFromStringList(List<String> stringList){
        String returnString = "";
        for(String s: stringList)returnString+=s;
        return returnString;
    }
}
