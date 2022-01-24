package de.vkd.gui;

import java.util.List;

import javax.swing.table.DefaultTableModel;

import de.vkd.auxiliary.ComparatorChain;
import de.vkd.framework.Framework;

@SuppressWarnings("serial")
public abstract class CustomTableModel<E> extends DefaultTableModel{
    private CustomTable<E> table;

    private ComparatorChain<E> comparatorChain;
    /**
     * Creates an {@link CustomTableModel} without any columns
     */
    public CustomTableModel(ComparatorChain<E> comparatorChain, CustomTable<E> table) {
        this(null, comparatorChain, table);
    }
    public CustomTableModel(String[] columnNames, ComparatorChain<E> comparatorChain, CustomTable<E> table) {
        super(columnNames, 0);
        this.table = table;
        this.comparatorChain = comparatorChain;
    }
    public void refreshTable(){
        table.resizeColumnWidth();
    }
    /**
     * Sorts a List using the field {@code comparatorChain}.
     * @param l The List that should be sorted
     * @param chain The Chain that dictates the order used to sort the List
     * @return The sorted List
     */
    public List<E> sort(List<E> unsortedList){
        return Framework.sort(unsortedList, comparatorChain);
    }
    public ComparatorChain<E> getComparatorChain() {
        return comparatorChain;
    }
    public void setComparatorChain(ComparatorChain<E> comparatorChain) {
        this.comparatorChain = comparatorChain;
    }
}
