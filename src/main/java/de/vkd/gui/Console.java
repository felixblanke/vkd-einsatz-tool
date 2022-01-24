package de.vkd.gui;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

@SuppressWarnings("serial")
public class Console extends JFrame{
    private JScrollPane scrollPane;
    private JTextPane textPane;

    public Console() {
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setPreferredSize(new Dimension( 800, 300));
        textPane = new JTextPane();
        textPane.setEditable(false);
        textPane.setBackground(Color.BLACK);
        textPane.setForeground(Color.WHITE);

        scrollPane = new JScrollPane(textPane);
        add(scrollPane);
        pack();

        //make console visible, if an exception occurs
        textPane.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void removeUpdate(DocumentEvent e) {
                updateConsole();
            }
            @Override
            public void insertUpdate(DocumentEvent e) {
                updateConsole();
            }
            @Override
            public void changedUpdate(DocumentEvent arg0) {
                updateConsole();
            }
        });
        setVisible(true);

    }

    private void updateConsole() {
        scrollToBottom();
        if(!isVisible()) this.setVisible(true);
    }

    public void scrollToBottom(){
        JScrollBar vertical = scrollPane.getVerticalScrollBar();
        vertical.setValue(vertical.getMaximum());
    }
    public JTextPane getTextPane() {
        return textPane;
    }
    public void setIcon(String iconPath){
        setIconImage(new ImageIcon(iconPath).getImage());
    }
}
