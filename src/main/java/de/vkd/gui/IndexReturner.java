package de.vkd.gui;

/**
 * Used to dynamically create a list of indices. This is needed to filter a {@link CustomTable} e.g.
 * if the table has no fixed number of columns
 */
public abstract class IndexReturner {
  public abstract int[] getIndex();
}
