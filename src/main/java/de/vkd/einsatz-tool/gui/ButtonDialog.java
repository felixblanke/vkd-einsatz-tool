package de.vkd.einsatz_tool.gui;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;


import de.vkd.gui.CustomButton;
import de.vkd.gui.CustomTable;
import de.vkd.gui.Dialog;
import de.vkd.gui.SearchHintTextField;
import de.vkd.auxiliary.ComparatorChain;
import de.vkd.framework.Framework;
import de.vkd.einsatz_tool.vkd.Main;
import de.vkd.einsatz_tool.vkd.VK;

@SuppressWarnings("serial")
public class ButtonDialog extends Dialog{

	public ButtonDialog(
			Main m, 
			Window parent, String title, 
			String[] columnNames, 
			String labelStartStringVal, 
			List<VK> workingList, JLabel outputLabel, 
			Insets tableInsets,
			ComparatorChain<VK> defaultChain,
			boolean isDriverDialog) {
		super(parent, title);
		
		setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
		JPanel pnlControl = new JPanel();
		pnlControl.setLayout(new GridBagLayout());
		
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0;
		gbc.weighty = 0;
		gbc.anchor = GridBagConstraints.FIRST_LINE_START;
		gbc.fill = GridBagConstraints.NONE;
		gbc.insets = tableInsets;
		
		pnlControl.add(new JLabel(m.getFramework().getString("LABEL_ALREADY_ASSIGNED")), gbc);
		
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 1;
		gbc.gridy = 0;
		gbc.gridx = 1;
		JLabel lblAlreadyAssigned = new JLabel();
		lblAlreadyAssigned.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.BLACK));
		pnlControl.add(lblAlreadyAssigned, gbc);
		
		gbc.fill = GridBagConstraints.BOTH;
		gbc.gridy = 1;
		gbc.gridx = 0;
		gbc.gridwidth = 2;
		SearchHintTextField htfSearch = new SearchHintTextField(m.getFramework().getString("TF_SEARCH"), 1, 2, 3, 4);
		pnlControl.add(htfSearch, gbc);
		
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridx = 2;
		gbc.gridy = 0;
		gbc.gridwidth = 1;
		gbc.weightx = 0;
		CustomButton accept = new CustomButton(m.getFramework().getString("BUTTON_ACCEPT"));
		accept.setFocusPainted(false);
		pnlControl.add(accept, gbc);
		gbc.gridy = 1;
		CustomButton cancel = new CustomButton(m.getFramework().getString("BUTTON_CANCEL"));
		cancel.setFocusPainted(false);
		pnlControl.add(cancel, gbc);
		
		
		
		add(pnlControl);
		
		CustomTable<VK> table = new CustomTable<VK>(m.getFramework(), parent, Main.getVKComparatorList());
		
		
		CustomDialogTableModel tb = new CustomDialogTableModel(m, columnNames, workingList, lblAlreadyAssigned, defaultChain, table, isDriverDialog);
		//Hi-Elo Code, but not necessary anymore :(
//			Constructor<CustomDialogTableModel> constr = clazz.getConstructor(Frame.class, columnNames.getClass(), workingList.getClass(), alreadyAssigned.getClass(), chain.getClass(), table.getClass());
//			CustomDialogTableModel tb = constr.newInstance(parent, columnNames, workingList, alreadyAssigned, chain, table);
		table.setModel(tb);
		table.getCustomTableModel().refreshTable();
	    JScrollPane scrollPane = new JScrollPane(table);
	    
	    table.resizeColumnWidth();
	    table.setFillsViewportHeight(true);
		htfSearch.setTable(table);
	    add(scrollPane);
	    pack();
	    
	    accept.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				List<VK> list = ((CustomDialogTableModel)table.getModel()).getWorkingList();
				ComparatorChain<VK> chain = defaultChain;
				list = Framework.sort(list, chain);
				//write to workingList
				workingList.clear();
				workingList.addAll(list);
				outputLabel.setText(m.getFramework().getString(labelStartStringVal)
						.concat(m.getStringFromVKList(workingList)));
				
				dispose();
			}
		});
	    cancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
	    
	    setLocationRelativeTo(parent);
	}

}