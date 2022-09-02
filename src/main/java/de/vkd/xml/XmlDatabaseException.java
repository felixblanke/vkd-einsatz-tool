package de.vkd.xml;

import java.util.List;

@SuppressWarnings("serial")
public abstract class XmlDatabaseException extends Exception{
    public XmlDatabaseException(String s) {
        super(s);
    }
    public static String getStringFromStringList(List<String> stringList){
        StringBuilder returnString = new StringBuilder();
        for(String s: stringList) returnString.append(s).append("|");
        return returnString.toString();
    }
}
