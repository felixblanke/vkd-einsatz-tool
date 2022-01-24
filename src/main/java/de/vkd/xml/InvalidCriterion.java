package de.vkd.xml;


@SuppressWarnings("serial")
public class InvalidCriterion extends XmlDatabaseException{
    public InvalidCriterion(XmlEntry criterion) {
        super("The criterion " + getStringFromStringList(criterion.getElementNames()) + " with the value " + criterion.getValue() + " is not valid.");
    }
    public InvalidCriterion(XmlEntry criterion, String msg) {
        super("The criterion " + getStringFromStringList(criterion.getElementNames()) + " with the value " + criterion.getValue() + " is not valid. (" + msg + ")");
    }
}
