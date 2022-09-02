package de.vkd.gui;


@SuppressWarnings("serial")
public class Dialog extends javax.swing.JDialog {
  public Dialog(java.awt.Window parent, String title) {
    super(parent);
    setTitle(title);
    setModalityType(ModalityType.APPLICATION_MODAL);
  }
}
