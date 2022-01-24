package de.vkd.gui;

import javax.swing.table.TableRowSorter;

public class CustomTableRowSorter <E> extends TableRowSorter<CustomTableModel<E>>{
    public CustomTableRowSorter(CustomTableModel<E> dataModel) {
        super(dataModel);
    }
    @Override
    public boolean isSortable(int column) {
        return false;
    }
}
