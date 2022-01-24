package de.vkd.gui;

import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import de.vkd.auxiliary.ComparatorChain;
import de.vkd.auxiliary.NamedComparator;
import de.vkd.framework.Framework;

@SuppressWarnings("serial")
public class SortDialog<E> extends Dialog{
	public final static int DEFAULT_INSET = 5;
	public final static int BTN_INSET = 2;	
	
	private CustomTable<E> parentTable;
	
	private List<NamedComparator<E>> activeComparator;
	private List<NamedComparator<E>> inactiveComparator;
	
	private CustomButton btnMoveSelectedUp;
	private CustomButton btnMoveSelectedDown;
	private CustomButton btnMoveSelectedLeft;
	private CustomButton btnMoveSelectedRight;
	private CustomButton btnOK;
	private CustomButton btnCancel;
	
	private JPanel pnlControl;
	private JPanel pnlMoveSelected;
	
	private JScrollPane scrollPaneActiveComparators;
	private JScrollPane scrollPaneInactiveComparators;
	private CustomList<E> lstActiveComparators;
	private CustomList<E> lstInactiveComparators;
	
	@SuppressWarnings("rawtypes")
	private Framework framework;
	
	public SortDialog(@SuppressWarnings("rawtypes") Framework framework, CustomTable<E> parentTable, Window parentWindow, String title, ComparatorChain<E> currentChain, List<NamedComparator<E>> comparatorList) {
		super(parentWindow, title);
		this.framework = framework;
		this.activeComparator = currentChain.getComparatorChain();
		List<NamedComparator<E>> comparatorListClone = new ArrayList<NamedComparator<E>>();
		comparatorListClone.addAll(comparatorList);
		for(NamedComparator<E> c: activeComparator){
			comparatorListClone.remove(c);
		}
		this.inactiveComparator = comparatorListClone;
		
		this.parentTable = parentTable;
		
		initComponents();
		pack();
		setMinimumSize(getPreferredSize());
		setLocationRelativeTo(parentWindow);
	}
	private void initComponents(){
		btnMoveSelectedUp = new CustomButton(framework.getString("BUTTON_MOVE_UP"));
		btnMoveSelectedUp.setToolTipText(framework.getString("BUTTON_MOVE_UP_TOOLTIP"));
		
		btnMoveSelectedDown = new CustomButton(framework.getString("BUTTON_MOVE_DOWN"));
		btnMoveSelectedDown.setToolTipText(framework.getString("BUTTON_MOVE_DOWN_TOOLTIP"));
		
		btnMoveSelectedLeft = new CustomButton(framework.getString("BUTTON_MOVE_LEFT"));
		btnMoveSelectedLeft.setToolTipText(framework.getString("BUTTON_MOVE_LEFT_TOOLTIP"));
		
		btnMoveSelectedRight = new CustomButton(framework.getString("BUTTON_MOVE_RIGHT"));
		btnMoveSelectedRight.setToolTipText(framework.getString("BUTTON_MOVE_RIGHT_TOOLTIP"));
		
		btnOK = new CustomButton(framework.getString("BUTTON_ACCEPT"));
		
		btnCancel = new CustomButton(framework.getString("BUTTON_CANCEL"));
				
		pnlControl = new JPanel();
		pnlControl.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		pnlControl.setLayout(new BoxLayout(pnlControl, BoxLayout.LINE_AXIS));
				
		pnlControl.add(btnOK);
		pnlControl.add(Box.createHorizontalStrut(2*DEFAULT_INSET));
		pnlControl.add(btnCancel);
		
		pnlMoveSelected = new JPanel();
		pnlMoveSelected.setLayout(new GridBagLayout());
		GridBagConstraints gbcControl = new GridBagConstraints();
		gbcControl.fill = GridBagConstraints.HORIZONTAL;
		gbcControl.weightx = 0;
		gbcControl.weighty = 1;
		
		gbcControl.anchor = GridBagConstraints.PAGE_START;
		gbcControl.insets = new Insets(0, 0, BTN_INSET, 0);
		pnlMoveSelected.add(btnMoveSelectedUp, gbcControl);
		gbcControl.gridy = 1;
		gbcControl.insets = new Insets(BTN_INSET, 0, BTN_INSET, 0);
		gbcControl.anchor = GridBagConstraints.CENTER;
		pnlMoveSelected.add(btnMoveSelectedRight, gbcControl);
		gbcControl.gridy = 2;
		pnlMoveSelected.add(btnMoveSelectedLeft, gbcControl);
		gbcControl.gridy = 3;
		gbcControl.insets = new Insets(BTN_INSET, 0, 0, 0);
		gbcControl.anchor = GridBagConstraints.PAGE_END;
		pnlMoveSelected.add(btnMoveSelectedDown, gbcControl);
		
		lstActiveComparators = new CustomList<E>(activeComparator);
		lstActiveComparators.setToolTipText(framework.getString("LIST_ACTIVE_TOOLTIP"));
		
		lstInactiveComparators = new CustomList<E>(inactiveComparator);
		lstInactiveComparators.setToolTipText(framework.getString("LIST_INACTIVE_TOOLTIP"));
		
		scrollPaneActiveComparators = new JScrollPane();
		scrollPaneActiveComparators.setViewportView(lstActiveComparators);
		
		scrollPaneInactiveComparators = new JScrollPane();
		scrollPaneInactiveComparators.setViewportView(lstInactiveComparators);
		
		
		setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(DEFAULT_INSET, DEFAULT_INSET, DEFAULT_INSET, DEFAULT_INSET);
		
		gbc.fill = GridBagConstraints.BOTH;
		gbc.weightx = 1;
		gbc.weighty = 1;
		gbc.anchor = GridBagConstraints.FIRST_LINE_START;
		
		add(lstActiveComparators, gbc);
		
		gbc.gridx = 2;
		
		add(lstInactiveComparators, gbc);
		
		gbc.weightx = 0;
		gbc.gridx = 1;
		
		add(pnlMoveSelected, gbc);
		
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		gbc.weightx = 0;
		gbc.weighty = 0;
		add(pnlControl, gbc);
		
		
		int maxLstWidth = Math.max(lstActiveComparators.getPreferredSize().width, 
				lstInactiveComparators.getPreferredSize().width);
		int maxLstHeight = Math.max(lstActiveComparators.getPreferredSize().height, 
				lstInactiveComparators.getPreferredSize().height);
		
		
		
	
		lstActiveComparators.setPreferredSize(new Dimension(maxLstWidth,maxLstHeight));
		lstInactiveComparators.setPreferredSize(new Dimension(maxLstWidth,maxLstHeight));
		
		setMinimumSize(getPreferredSize());
		
		lstActiveComparators.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				lstInactiveComparators.clearSelection();
			}
		});
		lstInactiveComparators.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				lstActiveComparators.clearSelection();
			}
		});
		
		btnMoveSelectedDown.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				boolean bActive = false;
				boolean bInactive = false;
				if(lstActiveComparators.getSelectedIndex()>=0)bActive = true;
				else if(lstInactiveComparators.getSelectedIndex()>=0)bInactive = true;
				if(bActive && bInactive)JOptionPane.showMessageDialog(SortDialog.this, framework.getString("EXCEPTION_SELECTION_IN_BOTH_LISTS"));
				else if(!bActive && !bInactive)JOptionPane.showMessageDialog(SortDialog.this, framework.getString("EXCEPTION_SELECTION_IN_NO_LIST"));
				else if(bActive){
					lstActiveComparators.moveElementDown(lstActiveComparators.getSelectedIndex());
					lstActiveComparators.requestFocusInWindow();
				}else{
					lstInactiveComparators.moveElementDown(lstInactiveComparators.getSelectedIndex());
					lstInactiveComparators.requestFocusInWindow();
				}
			}
		});
		btnMoveSelectedUp.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				boolean bActive = false;
				boolean bInactive = false;
				if(lstActiveComparators.getSelectedIndex()>=0)bActive = true;
				else if(lstInactiveComparators.getSelectedIndex()>=0)bInactive = true;
				if(bActive && bInactive)JOptionPane.showMessageDialog(SortDialog.this, framework.getString("EXCEPTION_SELECTION_IN_BOTH_LISTS"));
				else if(!bActive && !bInactive)JOptionPane.showMessageDialog(SortDialog.this, framework.getString("EXCEPTION_SELECTION_IN_NO_LIST"));
				else if(bActive){
					lstActiveComparators.moveElementUp(lstActiveComparators.getSelectedIndex());
					lstActiveComparators.requestFocusInWindow();
				}else{
					lstInactiveComparators.moveElementUp(lstInactiveComparators.getSelectedIndex());
					lstInactiveComparators.requestFocusInWindow();
				}
			}
		});
		btnMoveSelectedLeft.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				boolean bActive = false;
				boolean bInactive = false;
				if(lstActiveComparators.getSelectedIndex()>=0)bActive = true;
				else if(lstInactiveComparators.getSelectedIndex()>=0)bInactive = true;
				if(bActive && bInactive)JOptionPane.showMessageDialog(SortDialog.this, framework.getString("EXCEPTION_SELECTION_IN_BOTH_LISTS"));
				else if(!bActive && !bInactive)JOptionPane.showMessageDialog(SortDialog.this, framework.getString("EXCEPTION_SELECTION_IN_NO_LIST"));
				else if(bInactive){
					int index = lstInactiveComparators.getSelectedIndex();
					lstActiveComparators.addElement(lstInactiveComparators.getNamedComparatorAt(index));
					lstInactiveComparators.removeElementAt(index);
				}
			}
		});
		btnMoveSelectedRight.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				boolean bActive = false;
				boolean bInactive = false;
				if(lstActiveComparators.getSelectedIndex()>=0)bActive = true;
				else if(lstInactiveComparators.getSelectedIndex()>=0)bInactive = true;
				if(bActive && bInactive)JOptionPane.showMessageDialog(SortDialog.this, framework.getString("EXCEPTION_SELECTION_IN_BOTH_LISTS"));
				else if(!bActive && !bInactive)JOptionPane.showMessageDialog(SortDialog.this, framework.getString("EXCEPTION_SELECTION_IN_NO_LIST"));
				else if(bActive){
					int index = lstActiveComparators.getSelectedIndex();
					lstInactiveComparators.addElement(lstActiveComparators.getNamedComparatorAt(index));
					lstActiveComparators.removeElementAt(index);
				}
			}
		});
		btnCancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		btnOK.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				ComparatorChain<E> chain = new ComparatorChain<E>();
				List<NamedComparator<E>> list = lstActiveComparators.getNamedComparatorList();
				if(list.isEmpty()) {
					JOptionPane.showMessageDialog(SortDialog.this, "Es muss mindestens ein Sortierkriterium ausgew�hlt sein!");
					return;
				}
				for(NamedComparator<E> c: list)chain.addComparator(c);
				parentTable.getCustomTableModel().setComparatorChain(chain);
				parentTable.getCustomTableModel().refreshTable();
				dispose();
			}
		});
	}
	
	

}
