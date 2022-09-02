package de.vkd.xml;

import org.jdom2.Element;

public abstract class ElementCreatorXML<E> {
    public abstract Element generateXMLElement(E obj);
}
