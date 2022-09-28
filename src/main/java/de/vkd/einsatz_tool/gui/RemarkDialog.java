package de.vkd.einsatz_tool.gui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.github.lgooddatepicker.components.DatePickerSettings;
import com.github.lgooddatepicker.components.DateTimePicker;
import com.github.lgooddatepicker.components.TimePickerSettings;

import de.vkd.einsatz_tool.vkd.VK;
import de.vkd.framework.Framework;


public class RemarkDialog extends AbstractDialog {
  private DateTimePicker beginPicker;
  private DateTimePicker endPicker;

  public RemarkDialog(Framework<VK> framework, Frame parent, String title, VK vk) {
    super(framework, parent, title, vk);

    JPanel pnlCenter = new JPanel();
    pnlCenter.setLayout(new GridBagLayout());

    GridBagConstraints gbc = new GridBagConstraints();
    gbc.insets = new Insets(3, 3, 3, 3);
    gbc.weighty = 0;

    JCheckBox chkCustomTimes = new JCheckBox("Individuelle Einsatzzeiten:");
    chkCustomTimes.setSelected(false);
    chkCustomTimes.setFocusPainted(false);

    DatePickerSettings dateSettings1 = new DatePickerSettings();
    TimePickerSettings timeSettings1 = new TimePickerSettings();

    dateSettings1.setAllowEmptyDates(false);
    timeSettings1.setAllowEmptyTimes(false);

    DatePickerSettings dateSettings2 = new DatePickerSettings();
    TimePickerSettings timeSettings2 = new TimePickerSettings();

    dateSettings2.setAllowEmptyDates(false);
    timeSettings2.setAllowEmptyTimes(false);

    beginPicker = new DateTimePicker(dateSettings1, timeSettings1);
    endPicker = new DateTimePicker(dateSettings2, timeSettings2);

    if (vk.hasIndividualTimes()) {
        chkCustomTimes.setSelected(true);
        beginPicker.setEnabled(true);
        endPicker.setEnabled(true);
        beginPicker.setDateTimeStrict(vk.getBeginDateTime());
        endPicker.setDateTimeStrict(vk.getEndDateTime());
        System.out.println("Individual Times");
    } else {
        chkCustomTimes.setSelected(false);
        beginPicker.setEnabled(false);
        endPicker.setEnabled(false);
        beginPicker.setDateTimeStrict(parent.getBeginPicker().getDateTimeStrict());
        endPicker.setDateTimeStrict(parent.getEndPicker().getDateTimeStrict());
    }

    chkCustomTimes.addActionListener(e -> {
        beginPicker.setEnabled(chkCustomTimes.isSelected());
        endPicker.setEnabled(chkCustomTimes.isSelected());
    });

    gbc.gridx = GridBagConstraints.RELATIVE;

    gbc.fill = GridBagConstraints.NONE;
    gbc.anchor = GridBagConstraints.CENTER;
    gbc.weightx = 0;
    pnlCenter.add(chkCustomTimes);

    gbc.weightx = 1;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    pnlCenter.add(beginPicker);

    gbc.fill = GridBagConstraints.NONE;
    gbc.anchor = GridBagConstraints.CENTER;
    gbc.weightx = 0;
    pnlCenter.add(new JLabel(framework.getString("LABEL_BETWEEN")), gbc);

    gbc.weightx = 1;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    pnlCenter.add(endPicker);

    // TODO:
    beginPicker.addDateTimeChangeListener(e -> {checkTimePicker(true);});
    endPicker.addDateTimeChangeListener(e -> {checkTimePicker(false);});

    gbc.gridx = 0;
    gbc.gridy = 1;
    gbc.weightx = 0;
    gbc.fill = GridBagConstraints.NONE;
    gbc.gridwidth = 1;
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
        if (chkCustomTimes.isSelected()) {
            vk.updateIndividualTimes(beginPicker.getDateTimeStrict(), endPicker.getDateTimeStrict());
        } else {
            vk.updateIndividualTimes(null, null);
        }
        dispose();
    });
    this.getCancelButton().addActionListener(e -> dispose());

    pack();
    setLocationRelativeTo(parent);
  }

  private void checkTimePicker(boolean beginWasChanged) {
    if (beginPicker.getDatePicker().getDate().compareTo(endPicker.getDatePicker().getDate()) > 0) {
      if (beginWasChanged) {
        endPicker.getDatePicker().setDate(beginPicker.getDatePicker().getDate());
      } else {
        beginPicker.getDatePicker().setDate(endPicker.getDatePicker().getDate());
      }
    }

    if (beginPicker.getDatePicker().getDate().compareTo(endPicker.getDatePicker().getDate()) == 0 && beginPicker.getTimePicker().getTime().compareTo(endPicker.getTimePicker().getTime()) > 0) {
      if (beginWasChanged) {
        endPicker.getTimePicker().setTime(beginPicker.getTimePicker().getTime());
      } else {
        beginPicker.getTimePicker().setTime(endPicker.getTimePicker().getTime());
      }
    }
  }
}
