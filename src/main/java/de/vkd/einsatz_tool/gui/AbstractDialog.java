package de.vkd.einsatz_tool.gui;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ItemEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import de.vkd.einsatz_tool.vkd.Main;
import de.vkd.einsatz_tool.vkd.Status;
import de.vkd.einsatz_tool.vkd.VK;
import de.vkd.framework.Framework;
import de.vkd.gui.CustomButton;
import de.vkd.gui.Dialog;

public abstract class AbstractDialog extends Dialog  {
    private CustomButton btnAccept;
    private CustomButton btnCancel;

    public AbstractDialog(Framework<VK> framework, Frame parent, String title, VK vk) {
        super(parent, title);
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

        btnAccept = new CustomButton(framework.getString("BUTTON_ACCEPT"));
        btnAccept.setFocusPainted(false);
        pnlControl.add(btnAccept);
        pnlControl.add(Box.createHorizontalStrut(3));

        btnCancel = new CustomButton(framework.getString("BUTTON_CANCEL"));
        btnCancel.setFocusPainted(false);
        pnlControl.add(btnCancel);

        pnlTop.add(pnlControl);
        pnlTop.add(Box.createHorizontalStrut(3));
        add(pnlTop);
        add(Box.createVerticalStrut(3));
    }
    CustomButton getAcceptButton() {
        return btnAccept;
    }
    CustomButton getCancelButton() {
        return btnCancel;
    }

      String getCorrectedSelection(String s) {
        s = s.trim();
        if (!s.endsWith("%")) {
          s += "%";
        }
        while (s.charAt(0) == '0') {
          s = s.substring(1);
        }
        return s;
      }

      boolean checkSelection(String selection) {
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

      String[] getSEArray(Framework<VK> framework) {
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

        int getPercentage(String selection) {
            return Integer.parseInt(selection.substring(0, selection.length() - 1));
          }
      }
}
