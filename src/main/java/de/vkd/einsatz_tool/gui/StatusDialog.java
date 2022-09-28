package de.vkd.einsatz_tool.gui;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import de.vkd.einsatz_tool.vkd.Status;
import de.vkd.einsatz_tool.vkd.VK;
import de.vkd.framework.Framework;


public class StatusDialog extends AbstractDialog {
  public StatusDialog(Framework<VK> framework, Frame parent, String title, VK vk) {
    super(framework, parent, title, vk);

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

    JComboBox<String> cmb2 = new JComboBox<>(this.getSEArray(framework));
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

    add(pnlCenter);

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
    this.getAcceptButton().addActionListener(e -> {
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
      dispose();
    });
    this.getCancelButton().addActionListener(e -> dispose());

    pack();
    setLocationRelativeTo(parent);
  }
}
