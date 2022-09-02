package de.vkd.gui;

@SuppressWarnings("serial")
public class CustomButton extends javax.swing.JButton{
    public CustomButton() {
        setFocusPainted(false);
    }
    public CustomButton(String s) {
        this();
        setText(s);
    }
}
