package de.vkd.einsatz_tool.gui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import de.vkd.einsatz_tool.vkd.VK;
import de.vkd.framework.Framework;



public class RemarkDialog extends AbstractDialog {
  public RemarkDialog(Framework<VK> framework, Frame parent, String title, VK vk) {
    super(framework, parent, title, vk);

    JPanel pnlCenter = new JPanel();
    pnlCenter.setLayout(new GridBagLayout());

    GridBagConstraints gbc = new GridBagConstraints();
    gbc.insets = new Insets(3, 3, 3, 3);
    gbc.weighty = 0;

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

    add(pnlCenter);

    remark.addCaretListener(e -> pack());
    this.getAcceptButton().addActionListener(e -> {
        vk.setRemark(remark.getText());
        dispose();
    });
    this.getCancelButton().addActionListener(e -> dispose());

    pack();
    setLocationRelativeTo(parent);
  }
}
