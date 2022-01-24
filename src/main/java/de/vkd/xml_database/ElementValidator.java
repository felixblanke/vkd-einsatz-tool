package de.vkd.xml_database;

import org.jdom2.Element;

public abstract class ElementValidator<E> {
    public abstract boolean isElementValid(Element e, E value);
}
