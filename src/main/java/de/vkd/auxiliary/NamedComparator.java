package de.vkd.auxiliary;

import java.util.Comparator;

public class NamedComparator<E> implements Comparator<E>{
	private String name;
	private Comparator<E> comparator;
	
	public NamedComparator(String name, Comparator<E> comparator) {
		this.name = name;
		this.comparator = comparator;
	}
	@Override
	public int compare(E arg0, E arg1){
		return comparator.compare(arg0, arg1);
	}
	public String getName(){
		return name;
	}
	@Override
	public String toString() {
		return getName();
	}
}
