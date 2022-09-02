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
        StringBuilder s = new StringBuilder();
        for(XmlEntry entry: criteria){
            s.append("\tcriterium ").append(getStringFromStringList(entry.getElementNames()))
                .append(" with the value ").append(entry.getValue()).append("\n");
        }
        return s.toString();
    }
}
