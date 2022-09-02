package de.vkd.gui;

import de.vkd.auxiliary.NamedComparator;
import de.vkd.framework.Framework;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Point;
import java.awt.Window;
import java.util.List;
import java.util.regex.Pattern;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.RowFilter;
import javax.swing.SwingUtilities;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableRowSorter;

/**
 * @param <E> The type of the table entries, e.g. VK or Kuerzung;
 * @author Felix Blanke Custom JTable used as the basic table class for every table used in the
 *     project.
 */

@SuppressWarnings("serial")
public class CustomTable<E> extends JTable {
  private static final int TABLE_MIN_COL_WIDTH = 34;
  private static final int TABLE_MAX_COL_WIDTH = 400;
  private static final Color ALTERNATE_COLOR = new Color(240, 240, 240);
  private final JPopupMenu menu;
  private final JMenuItem sortMenuItem;
  private CustomTableModel<E> dataModel;
  private TableRowSorter<CustomTableModel<E>> rowSorter;
  private boolean alternateColorEnabled;

  public CustomTable(@SuppressWarnings("rawtypes") Framework framework, Container directParent,
                     List<NamedComparator<E>> comparatorList) {
    while (true) {
      if (directParent instanceof Window) {
        break;
      } else {
        directParent = directParent.getParent();
      }
    }
    final Window parent = (Window) directParent;

    getTableHeader().setReorderingAllowed(false);

    alternateColorEnabled = true;

    menu = new JPopupMenu();
    sortMenuItem = new JMenuItem(framework.getString("MNU_SORT"));
    menu.add(sortMenuItem);

    sortMenuItem.addActionListener(e -> {
      SortDialog<E> sortDialog = new SortDialog<>(framework, CustomTable.this, parent,
          framework.getString("DIALOG_SORT_TITLE"), dataModel.getComparatorChain(),
          comparatorList);
      sortDialog.setVisible(true);
    });
    menu.addPopupMenuListener(new PopupMenuListener() {
      @Override
      public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
        //select the row on which the click was made
        SwingUtilities.invokeLater(() -> {
          int rowAtPoint =
              rowAtPoint(SwingUtilities.convertPoint(menu, new Point(0, 0), CustomTable.this));
          if (rowAtPoint > -1) {
            setRowSelectionInterval(rowAtPoint, rowAtPoint);
          }
        });
        sortMenuItem.setEnabled(comparatorList != null);
      }

      @Override
      public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
        //repaint PopupMenu to fix the bug of the menu not closing correctly.
        repaint();
      }

      @Override
      public void popupMenuCanceled(PopupMenuEvent e) {
        //repaint PopupMenu to fix the bug of the menu not closing correctly.
        repaint();
      }
    });

    setComponentPopupMenu(menu);
  }

  /**
   * Add an {@link JMenuItem} to the {@link JPopupMenu} of the {@link CustomTable}.
   */
  public void addMenuItem(JMenuItem menuItem) {
    menu.add(menuItem);
  }

  //resizes the columns. Code snippet from the internet.
  public void resizeColumnWidth() {
    final TableColumnModel columnModel = getColumnModel();
    for (int column = 0; column < getColumnCount(); column++) {
      int width = TABLE_MIN_COL_WIDTH; // Min width
      for (int row = 0; row < getRowCount(); row++) {
        TableCellRenderer renderer = getCellRenderer(row, column);
        Component comp = prepareRenderer(renderer, row, column);
        width = Math.max(comp.getPreferredSize().width + 1, width);
      }
      if (width > TABLE_MAX_COL_WIDTH) {
        width = TABLE_MAX_COL_WIDTH;
      }
      columnModel.getColumn(column).setPreferredWidth(width);
    }
  }

  public CustomTableModel<E> getCustomTableModel() {
    return dataModel;
  }

  public void setModel(CustomTableModel<E> dataModel) {
    super.setModel(dataModel);
    this.dataModel = dataModel;
    //rowSorter is only used to search the table, not to sort it
    this.rowSorter = new CustomTableRowSorter<>(dataModel);
    setRowSorter(rowSorter);
  }

  /**
   * Searches the Table.
   *
   * @param searchString The String which is searched
   * @param index        The indices of the columns which should be searched. If empty, all columns
   *                     are searched.
   */
  public void search(String searchString, int... index) {
    if (rowSorter != null) {
      if (searchString.trim().isEmpty()) {
        rowSorter.setRowFilter(null);
        dataModel.refreshTable();
      } else {
        rowSorter.setRowFilter(
            RowFilter.regexFilter("(?i)" + Pattern.quote(searchString.trim()), index));
        dataModel.refreshTable();
      }
    }
  }

  public void refreshTable() {
    dataModel.refreshTable();
  }

  //Use ALTERNATE_COLOR as the background color of every second row:
  @Override
  public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
    Component returnComp = super.prepareRenderer(renderer, row, column);
    if (isAlternateColorEnabled() && !returnComp.getBackground().equals(getSelectionBackground())) {
      Color bg = (row % 2 == 0 ? ALTERNATE_COLOR : getBackground());
      returnComp.setBackground(bg);
      bg = null;
    }
    return returnComp;
  }

  public boolean isAlternateColorEnabled() {
    return alternateColorEnabled;
  }

  public void setAlternateColorEnabled(boolean alternateColorEnabled) {
    this.alternateColorEnabled = alternateColorEnabled;
  }
}
