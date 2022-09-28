package de.vkd.einsatz_tool.gui;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import de.vkd.auxiliary.ComparatorChain;
import de.vkd.einsatz_tool.vkd.Kuerzung;
import de.vkd.einsatz_tool.vkd.Main;
import de.vkd.einsatz_tool.vkd.VK;
import de.vkd.framework.Framework;
import de.vkd.gui.CustomButton;
import de.vkd.gui.CustomTable;
import de.vkd.gui.CustomTableModel;



public class KuerzungDialog extends AbstractDialog {
    private List<Kuerzung> workingList = new ArrayList<>();
  public KuerzungDialog(Framework<VK> framework, Frame parent, String title, VK vk) {
    super(framework, parent, title, vk);

    for (Kuerzung k : vk.getKuerzungsListe()) {
        workingList.add(k.clone());
    }

    JPanel pnlCenter = new JPanel();
    pnlCenter.setLayout(new GridBagLayout());

    GridBagConstraints gbc = new GridBagConstraints();
    gbc.insets = new Insets(3, 3, 3, 3);
    gbc.weighty = 0;

    gbc.anchor = GridBagConstraints.LINE_START;
    gbc.gridx = 0;
    gbc.gridy = 2;
    gbc.weightx = 0;
    gbc.gridwidth = 1;
    gbc.fill = GridBagConstraints.NONE;

    JLabel lblKuerzung = new JLabel(framework.getString("LABEL_KUERZ"));
    pnlCenter.add(lblKuerzung, gbc);

    gbc.gridx = GridBagConstraints.RELATIVE;
    gbc.weightx = 0;
    gbc.fill = GridBagConstraints.NONE;
    gbc.gridwidth = 2;
    String[] strArr = new String[19];
    for (int i = 0; i < strArr.length; i++) {
      strArr[i] = String.valueOf(5 * (i + 1)).concat("%");
    }
    PercentageComboBox cmbPercentage = new PercentageComboBox(strArr);
    cmbPercentage.setEditable(true);
    cmbPercentage.setPreferredSize(new Dimension(cmbPercentage.getMinimumSize().width - 80,
        cmbPercentage.getMinimumSize().height));
    pnlCenter.add(cmbPercentage, gbc);

    gbc.gridx = GridBagConstraints.RELATIVE;
    gbc.weightx = 0;
    gbc.fill = GridBagConstraints.NONE;
    gbc.gridwidth = 1;
    pnlCenter.add(new JLabel(framework.getString("LABEL_REASON")), gbc);

    gbc.gridwidth = 1;
    gbc.gridx = GridBagConstraints.RELATIVE;
    gbc.weightx = 1;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    JTextField reason = new JTextField("");
    pnlCenter.add(reason, gbc);

    gbc.anchor = GridBagConstraints.LINE_END;
    gbc.gridx = GridBagConstraints.RELATIVE;
    gbc.weightx = 0;
    gbc.gridwidth = 1;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    CustomButton btnKuerzungen = new CustomButton(framework.getString("BUTTON_ADD_REASON"));
    pnlCenter.add(btnKuerzungen, gbc);

    gbc.anchor = GridBagConstraints.LINE_END;
    gbc.gridx = GridBagConstraints.RELATIVE;
    gbc.weightx = 0;
    gbc.gridwidth = GridBagConstraints.REMAINDER;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    CustomButton btnRemoveKuerzungen =
        new CustomButton(framework.getString("BUTTON_REMOVE_REASON"));
    pnlCenter.add(btnRemoveKuerzungen, gbc);

    add(pnlCenter);
    add(Box.createVerticalStrut(3));

    CustomTable<Kuerzung> table =
        new CustomTable<>(framework, this, Main.getKuerzungComparatorList());
    table.setModel(
        new TableModelRemarkDialog(
            new String[] {
                framework.getString("TABLE_ID"),
                framework.getString("TABLE_PERCENTAGE"),
                framework.getString("TABLE_REASON")
            },
            new ComparatorChain<>(Main.KUERZUNG_PERCENTAGE_COMPARATOR, Main.KUERZUNG_ID_COMPARATOR),
            table));
    JScrollPane scrollPane = new JScrollPane(table);

    add(scrollPane);

    reason.addCaretListener(e -> pack());
    this.getAcceptButton().addActionListener(e -> {
        vk.getKuerzungsListe().clear();
        vk.getKuerzungsListe().addAll(workingList);
        dispose();
    });
    this.getCancelButton().addActionListener(e -> dispose());

    btnKuerzungen.addActionListener(e -> {
      String r = reason.getText();
      String p = (String) cmbPercentage.getSelectedItem();
      assert p != null;
      if (checkSelection(p)) {
        if (!r.isEmpty()) {
          workingList.add(new Kuerzung(cmbPercentage.getPercentage(p), r));
          ((TableModelRemarkDialog) table.getModel()).refreshTable();
        }
      }
    });
    btnRemoveKuerzungen.addActionListener(e -> {
      int[] i = table.getSelectedRows();
      for (int j : i) {
        TableModelRemarkDialog model = (TableModelRemarkDialog) table.getModel();
        model.removeKuerzung((int) table.getModel().getValueAt(j, 0));
        //                    model.removeRow(i[k]);
      }
      ((TableModelRemarkDialog) table.getModel()).refreshTable();


    });
    ((TableModelRemarkDialog) table.getModel()).refreshTable();

    pack();
    setLocationRelativeTo(parent);
  }

  class TableModelRemarkDialog extends CustomTableModel<Kuerzung> {
    public TableModelRemarkDialog(String[] columnNames, ComparatorChain<Kuerzung> defaultChain,
                                  CustomTable<Kuerzung> table) {
      super(columnNames, defaultChain, table);
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
      if (columnIndex == 0) {
        return Integer.class;
      }
      return String.class;
    }

    @Override
    public boolean isCellEditable(int row, int column) {
      return column == 1 || column == 2;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Override
    public void setValueAt(Object value, int row, int column) {
      if (value instanceof String && column == 1) {
        String s = getCorrectedSelection((String) value);
        if (checkSelection(s)) {
          Vector rowData = (Vector) getDataVector().get(row);
          rowData.set(column, s);
          Kuerzung k = getKuerzung((int) getValueAt(row, 0));
          assert k != null;
          k.setPercentage(Integer.parseInt(s.substring(0, s.length() - 1)));
          refreshTable();
        }
      } else if (value instanceof String && column == 2) {
        String s = (String) value;
        Vector rowData = (Vector) getDataVector().get(row);
        rowData.set(column, s);
        Kuerzung k = getKuerzung((int) getValueAt(row, 0));
        assert k != null;
        k.setReason(s);
        refreshTable();
      }
    }

    @Override
    public void refreshTable() {
      workingList = sort(workingList);
      this.setRowCount(0);
      Object[][] rowData = new Object[workingList.size()][this.getColumnCount()];
      for (int i = 0; i < workingList.size(); i++) {
        rowData[i][0] = workingList.get(i).getId();
        rowData[i][1] = String.valueOf(workingList.get(i).getPercentage()).concat("%");
        rowData[i][2] = workingList.get(i).getReason();
      }
      for (Object[] o : rowData) {
        this.addRow(o);
      }
      super.refreshTable();
    }

    private Kuerzung getKuerzung(int id) {
      for (Kuerzung k : workingList) {
        if (k.getId() == id) {
          return k;
        }
      }
      return null;
    }

    private void removeKuerzung(int id) {
      for (Kuerzung kuerz : workingList) {
        if (kuerz.getId() == id) {
          workingList.remove(kuerz);
          return;
        }
      }
    }
  }
}
