package de.vkd.xml;

import java.util.List;

@SuppressWarnings("serial")
public abstract class XmlDatabaseException extends Exception{
	public XmlDatabaseException(String s) {
		super(s);
	}
	public static String getStringFromStringList(List<String> stringList){
		String returnString = "";
		for(String s: stringList)returnString+=s + "|";
		return returnString;
	}
}
