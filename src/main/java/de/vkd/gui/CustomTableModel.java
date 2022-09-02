package de.vkd.gui;

import de.vkd.auxiliary.ComparatorChain;
import de.vkd.framework.Framework;
import java.util.List;
import javax.swing.table.DefaultTableModel;

@SuppressWarnings("serial")
public abstract class CustomTableModel<E> extends DefaultTableModel {
  private final CustomTable<E> table;

  private ComparatorChain<E> comparatorChain;

  /**
   * Creates an {@link CustomTableModel} without any columns
   */
  public CustomTableModel(ComparatorChain<E> comparatorChain, CustomTable<E> table) {
    this(null, comparatorChain, table);
  }

  public CustomTableModel(String[] columnNames, ComparatorChain<E> comparatorChain,
                          CustomTable<E> table) {
    super(columnNames, 0);
    this.table = table;
    this.comparatorChain = comparatorChain;
  }

  public void refreshTable() {
    table.resizeColumnWidth();
  }

  /**
   * Sorts a List using the field {@code comparatorChain}.
   *
   * @param unsortedList The List that should be sorted
   * @return The sorted List
   */
  public List<E> sort(List<E> unsortedList) {
    return Framework.sort(unsortedList, comparatorChain);
  }

  public ComparatorChain<E> getComparatorChain() {
    return comparatorChain;
  }

  public void setComparatorChain(ComparatorChain<E> comparatorChain) {
    this.comparatorChain = comparatorChain;
  }
}
