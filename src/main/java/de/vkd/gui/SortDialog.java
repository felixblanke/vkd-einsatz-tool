package de.vkd.gui;

import de.vkd.auxiliary.ComparatorChain;
import de.vkd.auxiliary.NamedComparator;
import de.vkd.framework.Framework;
import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;
import java.util.ArrayList;
import java.util.List;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

@SuppressWarnings("serial")
public class SortDialog<E> extends Dialog {
  public final static int DEFAULT_INSET = 5;
  public final static int BTN_INSET = 2;

  private final CustomTable<E> parentTable;

  private final List<NamedComparator<E>> activeComparator;
  private final List<NamedComparator<E>> inactiveComparator;
  @SuppressWarnings("rawtypes")
  private final Framework framework;
  private CustomList<E> lstActiveComparators;
  private CustomList<E> lstInactiveComparators;

  public SortDialog(@SuppressWarnings("rawtypes") Framework framework, CustomTable<E> parentTable,
                    Window parentWindow, String title, ComparatorChain<E> currentChain,
                    List<NamedComparator<E>> comparatorList) {
    super(parentWindow, title);
    this.framework = framework;
    this.activeComparator = currentChain.getComparatorChain();
    List<NamedComparator<E>> comparatorListClone = new ArrayList<>(comparatorList);
    for (NamedComparator<E> c : activeComparator) {
      comparatorListClone.remove(c);
    }
    this.inactiveComparator = comparatorListClone;

    this.parentTable = parentTable;

    initComponents();
    pack();
    setMinimumSize(getPreferredSize());
    setLocationRelativeTo(parentWindow);
  }

  private void initComponents() {
    CustomButton btnMoveSelectedUp = new CustomButton(framework.getString("BUTTON_MOVE_UP"));
    btnMoveSelectedUp.setToolTipText(framework.getString("BUTTON_MOVE_UP_TOOLTIP"));

    CustomButton btnMoveSelectedDown = new CustomButton(framework.getString("BUTTON_MOVE_DOWN"));
    btnMoveSelectedDown.setToolTipText(framework.getString("BUTTON_MOVE_DOWN_TOOLTIP"));

    CustomButton btnMoveSelectedLeft = new CustomButton(framework.getString("BUTTON_MOVE_LEFT"));
    btnMoveSelectedLeft.setToolTipText(framework.getString("BUTTON_MOVE_LEFT_TOOLTIP"));

    CustomButton btnMoveSelectedRight = new CustomButton(framework.getString("BUTTON_MOVE_RIGHT"));
    btnMoveSelectedRight.setToolTipText(framework.getString("BUTTON_MOVE_RIGHT_TOOLTIP"));

    CustomButton btnOK = new CustomButton(framework.getString("BUTTON_ACCEPT"));

    CustomButton btnCancel = new CustomButton(framework.getString("BUTTON_CANCEL"));

    JPanel pnlControl = new JPanel();
    pnlControl.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
    pnlControl.setLayout(new BoxLayout(pnlControl, BoxLayout.LINE_AXIS));

    pnlControl.add(btnOK);
    pnlControl.add(Box.createHorizontalStrut(2 * DEFAULT_INSET));
    pnlControl.add(btnCancel);

    JPanel pnlMoveSelected = new JPanel();
    pnlMoveSelected.setLayout(new GridBagLayout());
    GridBagConstraints gbcControl = new GridBagConstraints();
    gbcControl.fill = GridBagConstraints.HORIZONTAL;
    gbcControl.weightx = 0;
    gbcControl.weighty = 1;

    gbcControl.anchor = GridBagConstraints.PAGE_START;
    gbcControl.insets = new Insets(0, 0, BTN_INSET, 0);
    pnlMoveSelected.add(btnMoveSelectedUp, gbcControl);
    gbcControl.gridy = 1;
    gbcControl.insets = new Insets(BTN_INSET, 0, BTN_INSET, 0);
    gbcControl.anchor = GridBagConstraints.CENTER;
    pnlMoveSelected.add(btnMoveSelectedRight, gbcControl);
    gbcControl.gridy = 2;
    pnlMoveSelected.add(btnMoveSelectedLeft, gbcControl);
    gbcControl.gridy = 3;
    gbcControl.insets = new Insets(BTN_INSET, 0, 0, 0);
    gbcControl.anchor = GridBagConstraints.PAGE_END;
    pnlMoveSelected.add(btnMoveSelectedDown, gbcControl);

    lstActiveComparators = new CustomList<>(activeComparator);
    lstActiveComparators.setToolTipText(framework.getString("LIST_ACTIVE_TOOLTIP"));

    lstInactiveComparators = new CustomList<>(inactiveComparator);
    lstInactiveComparators.setToolTipText(framework.getString("LIST_INACTIVE_TOOLTIP"));

    JScrollPane scrollPaneActiveComparators = new JScrollPane();
    scrollPaneActiveComparators.setViewportView(lstActiveComparators);

    JScrollPane scrollPaneInactiveComparators = new JScrollPane();
    scrollPaneInactiveComparators.setViewportView(lstInactiveComparators);


    setLayout(new GridBagLayout());
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.insets = new Insets(DEFAULT_INSET, DEFAULT_INSET, DEFAULT_INSET, DEFAULT_INSET);

    gbc.fill = GridBagConstraints.BOTH;
    gbc.weightx = 1;
    gbc.weighty = 1;
    gbc.anchor = GridBagConstraints.FIRST_LINE_START;

    add(lstActiveComparators, gbc);

    gbc.gridx = 2;

    add(lstInactiveComparators, gbc);

    gbc.weightx = 0;
    gbc.gridx = 1;

    add(pnlMoveSelected, gbc);

    gbc.gridx = 0;
    gbc.gridy = 1;
    gbc.gridwidth = GridBagConstraints.REMAINDER;
    gbc.weightx = 0;
    gbc.weighty = 0;
    add(pnlControl, gbc);


    int maxLstWidth = Math.max(lstActiveComparators.getPreferredSize().width,
        lstInactiveComparators.getPreferredSize().width);
    int maxLstHeight = Math.max(lstActiveComparators.getPreferredSize().height,
        lstInactiveComparators.getPreferredSize().height);


    lstActiveComparators.setPreferredSize(new Dimension(maxLstWidth, maxLstHeight));
    lstInactiveComparators.setPreferredSize(new Dimension(maxLstWidth, maxLstHeight));

    setMinimumSize(getPreferredSize());

    lstActiveComparators.addListSelectionListener(e -> lstInactiveComparators.clearSelection());
    lstInactiveComparators.addListSelectionListener(e -> lstActiveComparators.clearSelection());

    btnMoveSelectedDown.addActionListener(e -> {
      boolean bActive = false;
      boolean bInactive = false;
      if (lstActiveComparators.getSelectedIndex() >= 0) {
        bActive = true;
      } else if (lstInactiveComparators.getSelectedIndex() >= 0) {
        bInactive = true;
      }
      if (bActive && bInactive) {
        JOptionPane.showMessageDialog(SortDialog.this,
            framework.getString("EXCEPTION_SELECTION_IN_BOTH_LISTS"));
      } else if (!bActive && !bInactive) {
        JOptionPane.showMessageDialog(SortDialog.this,
            framework.getString("EXCEPTION_SELECTION_IN_NO_LIST"));
      } else if (bActive) {
        lstActiveComparators.moveElementDown(lstActiveComparators.getSelectedIndex());
        lstActiveComparators.requestFocusInWindow();
      } else {
        lstInactiveComparators.moveElementDown(lstInactiveComparators.getSelectedIndex());
        lstInactiveComparators.requestFocusInWindow();
      }
    });
    btnMoveSelectedUp.addActionListener(e -> {
      boolean bActive = false;
      boolean bInactive = false;
      if (lstActiveComparators.getSelectedIndex() >= 0) {
        bActive = true;
      } else if (lstInactiveComparators.getSelectedIndex() >= 0) {
        bInactive = true;
      }
      if (bActive && bInactive) {
        JOptionPane.showMessageDialog(SortDialog.this,
            framework.getString("EXCEPTION_SELECTION_IN_BOTH_LISTS"));
      } else if (!bActive && !bInactive) {
        JOptionPane.showMessageDialog(SortDialog.this,
            framework.getString("EXCEPTION_SELECTION_IN_NO_LIST"));
      } else if (bActive) {
        lstActiveComparators.moveElementUp(lstActiveComparators.getSelectedIndex());
        lstActiveComparators.requestFocusInWindow();
      } else {
        lstInactiveComparators.moveElementUp(lstInactiveComparators.getSelectedIndex());
        lstInactiveComparators.requestFocusInWindow();
      }
    });
    btnMoveSelectedLeft.addActionListener(e -> {
      boolean bActive = false;
      boolean bInactive = false;
      if (lstActiveComparators.getSelectedIndex() >= 0) {
        bActive = true;
      } else if (lstInactiveComparators.getSelectedIndex() >= 0) {
        bInactive = true;
      }
      if (bActive && bInactive) {
        JOptionPane.showMessageDialog(SortDialog.this,
            framework.getString("EXCEPTION_SELECTION_IN_BOTH_LISTS"));
      } else if (!bActive && !bInactive) {
        JOptionPane.showMessageDialog(SortDialog.this,
            framework.getString("EXCEPTION_SELECTION_IN_NO_LIST"));
      } else if (bInactive) {
        int index = lstInactiveComparators.getSelectedIndex();
        lstActiveComparators.addElement(lstInactiveComparators.getNamedComparatorAt(index));
        lstInactiveComparators.removeElementAt(index);
      }
    });
    btnMoveSelectedRight.addActionListener(e -> {
      boolean bActive = false;
      boolean bInactive = false;
      if (lstActiveComparators.getSelectedIndex() >= 0) {
        bActive = true;
      } else if (lstInactiveComparators.getSelectedIndex() >= 0) {
        bInactive = true;
      }
      if (bActive && bInactive) {
        JOptionPane.showMessageDialog(SortDialog.this,
            framework.getString("EXCEPTION_SELECTION_IN_BOTH_LISTS"));
      } else if (!bActive && !bInactive) {
        JOptionPane.showMessageDialog(SortDialog.this,
            framework.getString("EXCEPTION_SELECTION_IN_NO_LIST"));
      } else if (bActive) {
        int index = lstActiveComparators.getSelectedIndex();
        lstInactiveComparators.addElement(lstActiveComparators.getNamedComparatorAt(index));
        lstActiveComparators.removeElementAt(index);
      }
    });
    btnCancel.addActionListener(e -> dispose());
    btnOK.addActionListener(e -> {
      ComparatorChain<E> chain = new ComparatorChain<>();
      List<NamedComparator<E>> list = lstActiveComparators.getNamedComparatorList();
      if (list.isEmpty()) {
        JOptionPane.showMessageDialog(SortDialog.this,
            "Es muss mindestens ein Sortierkriterium ausgew�hlt sein!");
        return;
      }
      for (NamedComparator<E> c : list) {
        chain.addComparator(c);
      }
      parentTable.getCustomTableModel().setComparatorChain(chain);
      parentTable.getCustomTableModel().refreshTable();
      dispose();
    });
  }


}
