package de.vkd.xml_database;

import org.jdom2.Element;

public abstract class ElementUpdater<E> {
    public abstract void update(Element e, E value);
}
