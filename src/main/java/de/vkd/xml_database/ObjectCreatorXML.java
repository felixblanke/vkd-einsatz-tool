package de.vkd.xml_database;

import org.jdom2.Element;

public abstract class ObjectCreatorXML<E> {
	public abstract E create(Element e);
}
