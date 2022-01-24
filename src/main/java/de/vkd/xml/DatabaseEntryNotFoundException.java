package de.vkd.xml;


@SuppressWarnings("serial")
public class DatabaseEntryNotFoundException extends XmlDatabaseException{
	public DatabaseEntryNotFoundException(XmlEntry searchedEntry) {
		super("Could not find the entry " + getStringFromStringList(searchedEntry.getElementNames()) + ".");
	}
	public DatabaseEntryNotFoundException(XmlEntry searchedEntry, XmlEntry[] criteria) {
		super("Could not find the entry " + getStringFromStringList(searchedEntry.getElementNames()) + " using the criteria " + getCriteriaString(criteria));
	}
	private static String getCriteriaString(XmlEntry[] criteria){
		String s = "";
		for(XmlEntry entry: criteria){
			s+= "\tcriterium " + getStringFromStringList(entry.getElementNames()) + " with the value " + entry.getValue() + "\n";
		}
		return s;
	}
}
