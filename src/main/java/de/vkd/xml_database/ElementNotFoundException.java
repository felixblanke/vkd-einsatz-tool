package de.vkd.xml_database;

@SuppressWarnings("serial")
public class ElementNotFoundException extends Exception {
    public ElementNotFoundException(String searchedEntry) {
        super("Could not find the entry " + searchedEntry + ".");
    }
}
