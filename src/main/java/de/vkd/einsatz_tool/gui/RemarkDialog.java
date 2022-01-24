package de.vkd.einsatz_tool.gui;

import de.vkd.auxiliary.ComparatorChain;
import de.vkd.einsatz_tool.vkd.Kuerzung;
import de.vkd.einsatz_tool.vkd.Main;
import de.vkd.einsatz_tool.vkd.Status;
import de.vkd.einsatz_tool.vkd.VK;
import de.vkd.framework.Framework;
import de.vkd.gui.CustomButton;
import de.vkd.gui.CustomTable;
import de.vkd.gui.CustomTableModel;
import de.vkd.gui.Dialog;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ItemEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;


@SuppressWarnings("serial")
public class RemarkDialog extends Dialog {
  private List<Kuerzung> workingList = new ArrayList<>();

  public RemarkDialog(Framework<VK> framework, Frame parent, String title, VK vk) {
    super(parent, title);

    for (Kuerzung k : vk.getKuerzungsListe()) {
      workingList.add(k.clone());
    }

    setResizable(false);

    setLayout(new BoxLayout(getContentPane(), BoxLayout.PAGE_AXIS));

    add(Box.createVerticalStrut(3));


    JPanel pnlTop = new JPanel();
    pnlTop.setLayout(new BoxLayout(pnlTop, BoxLayout.LINE_AXIS));

    pnlTop.add(Box.createHorizontalStrut(3));
    JLabel lblName =
        new JLabel(framework.getString("LABEL_DIALOG_NAME") + vk.getStringRepresentation());
    pnlTop.add(lblName);

    pnlTop.add(Box.createHorizontalGlue());
    pnlTop.add(Box.createHorizontalStrut(130));

    JPanel pnlControl = new JPanel();
    pnlControl.setLayout(new BoxLayout(pnlControl, BoxLayout.LINE_AXIS));
    CustomButton btnAccept = new CustomButton(framework.getString("BUTTON_ACCEPT"));
    btnAccept.setFocusPainted(false);
    pnlControl.add(btnAccept);
    pnlControl.add(Box.createHorizontalStrut(3));
    CustomButton btnCancel = new CustomButton(framework.getString("BUTTON_CANCEL"));
    btnCancel.setFocusPainted(false);
    pnlControl.add(btnCancel);

    pnlTop.add(pnlControl);
    pnlTop.add(Box.createHorizontalStrut(3));

    add(pnlTop);

    add(Box.createVerticalStrut(3));


    JPanel pnlCenter = new JPanel();
    pnlCenter.setLayout(new GridBagLayout());

    GridBagConstraints gbc = new GridBagConstraints();
    gbc.insets = new Insets(3, 3, 3, 3);
    gbc.weighty = 0;

    gbc.anchor = GridBagConstraints.LINE_START;
    gbc.gridy = 0;
    gbc.gridx = 0;
    gbc.gridwidth = 2;
    gbc.weightx = 0;
    JLabel lblStatus = new JLabel(framework.getString("LABEL_STATUS"));
    pnlCenter.add(lblStatus, gbc);

    JPanel pnlEinsatzstatus = new JPanel();
    pnlEinsatzstatus.setLayout(new BoxLayout(pnlEinsatzstatus, BoxLayout.X_AXIS));
    String[] cmbVals = new String[Status.values().length];
    for (int i = 0; i < cmbVals.length; i++) {
      cmbVals[i] = Status.values()[i].getListName();
    }
    JComboBox<String> cmb = new JComboBox<>(cmbVals);
    cmb.setEditable(false);
    cmb.setSelectedItem(vk.getStatus().getListName());
    pnlEinsatzstatus.add(cmb);


    Component spacer1 = Box.createHorizontalStrut(3);
    Component spacer2 = Box.createHorizontalStrut(3);

    spacer1.setVisible(false);
    spacer2.setVisible(false);

    pnlEinsatzstatus.add(spacer1);

    JLabel temp = new JLabel(framework.getString("LABEL_ERS_FOR"));
    temp.setVisible(false);
    pnlEinsatzstatus.add(temp);
    pnlEinsatzstatus.add(spacer2);

    JComboBox<String> cmb2 = new JComboBox<>(getSEArray(framework));
    cmb2.setEditable(false);
    if (vk.getErsatz() != null) {
      cmb2.setSelectedItem(vk.getErsatz().getStringRepresentation());
    }
    cmb2.setVisible(false);

    pnlEinsatzstatus.add(cmb2);

    gbc.gridx = GridBagConstraints.RELATIVE;
    gbc.gridwidth = GridBagConstraints.REMAINDER;
    gbc.weightx = 1;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    pnlCenter.add(pnlEinsatzstatus, gbc);

    gbc.gridx = 0;
    gbc.gridy = 1;
    gbc.weightx = 0;
    gbc.fill = GridBagConstraints.NONE;
    gbc.gridwidth = 2;
    gbc.anchor = GridBagConstraints.LINE_START;
    pnlCenter.add(new JLabel(framework.getString("LABEL_REMARKS")), gbc);

    gbc.gridx = GridBagConstraints.RELATIVE;
    gbc.weightx = 1;
    gbc.gridwidth = GridBagConstraints.REMAINDER;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    JTextField remark = new JTextField();
    remark.setText(vk.getRemark());
    pnlCenter.add(remark, gbc);

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
    if (cmb.getSelectedIndex() == 4) {
      temp.setVisible(true);
      cmb2.setVisible(true);
      spacer1.setVisible(true);
      spacer2.setVisible(true);
      pack();
    } else {
      temp.setVisible(false);
      cmb2.setVisible(false);
      spacer1.setVisible(false);
      spacer2.setVisible(false);
      pack();
    }

    cmb.addActionListener(e -> {
      if (cmb.getSelectedIndex() == 4) {
        temp.setVisible(true);
        cmb2.setVisible(true);
        spacer1.setVisible(true);
        spacer2.setVisible(true);
        pack();
      } else {
        temp.setVisible(false);
        cmb2.setVisible(false);
        spacer1.setVisible(false);
        spacer2.setVisible(false);
        pack();
      }
    });
    cmb2.addActionListener(e -> {
    });
    remark.addCaretListener(e -> pack());
    reason.addCaretListener(e -> pack());
    btnAccept.addActionListener(e -> {
      vk.setRemark(remark.getText());
      vk.setStatus(Status.getStatusByListName((String) cmb.getSelectedItem()));
      if (vk.getStatus().equals(Status.ERSATZ)) {
        String[] strArrSE = getSEArray(framework);
        String reference = (String) cmb2.getSelectedItem();
        for (String s : strArrSE) {
          if (s.equals(reference)) {
            for (VK vkL : framework.getDatabase()) {
              if (vkL.getStringRepresentation().equals(s)) {
                vk.setErsatz(vkL);
                break;
              }
            }
            break;
          }
        }
      }
      vk.getKuerzungsListe().clear();
      vk.getKuerzungsListe().addAll(workingList);

      dispose();
    });
    btnCancel.addActionListener(e -> dispose());
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

  private String getCorrectedSelection(String s) {
    s = s.trim();
    if (!s.endsWith("%")) {
      s += "%";
    }
    while (s.charAt(0) == '0') {
      s = s.substring(1);
    }
    return s;
  }

  private boolean checkSelection(String selection) {
    if (selection.endsWith("%")) {
      selection = selection.substring(0, selection.length() - 1);
    }
    if (selection.isEmpty()) {
      return false;
    }
    for (int i = 0; i < selection.length(); i++) {
      if (!Character.isDigit(selection.charAt(i))) {
        return false;
      }
    }
    return Integer.parseInt(selection) <= 100;
  }

  private String[] getSEArray(Framework<VK> framework) {
    List<VK> l = new ArrayList<>();
    for (VK vk : framework.getDatabase()) {
      if (vk.isSelected()
          && (vk.getStatus().equals(Status.SE) || vk.getStatus().equals(Status.UE))) {
        l.add(vk);
      }
    }
    l = Framework.sort(l, Main.VK_DEFAULT_COMPARATOR_CHAIN_IGNORING_GROUPS);
    String[] s = new String[l.size() + 1];
    s[0] = "--";
    for (int i = 0; i < l.size(); i++) {
      s[i + 1] = l.get(i).getStringRepresentation();
    }
    return s;
  }

  class PercentageComboBox extends JComboBox<String> {
    private String previousValue;

    public PercentageComboBox(String[] options) {
      super(options);
      addItemListener(event -> {
        if (event.getStateChange() == ItemEvent.SELECTED) {
          if (!checkSelection(getCorrectedSelection((String) getSelectedItem()))) {
            if (previousValue != null) {
              setSelectedItem(previousValue);
            } else {
              setSelectedIndex(-1);
            }
          } else {
            setSelectedItem(getCorrectedSelection((String) getSelectedItem()));
            previousValue = (String) getSelectedItem();
          }
        }
      });
    }

    private int getPercentage(String selection) {
      return Integer.parseInt(selection.substring(0, selection.length() - 1));
    }
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
