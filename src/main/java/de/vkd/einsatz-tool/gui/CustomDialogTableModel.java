package de.vkd.einsatz_tool.gui;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.swing.JLabel;

import de.vkd.auxiliary.ComparatorChain;
import de.vkd.framework.Framework;
import de.vkd.gui.CustomTable;
import de.vkd.gui.CustomTableModel;
import de.vkd.einsatz_tool.vkd.Main;
import de.vkd.einsatz_tool.vkd.VK;

@SuppressWarnings("serial")
final class CustomDialogTableModel extends CustomTableModel<VK>{
	//Funktion, "Rang", "Position", "Vorname", "Nachname", "ID"
	private List<VK> workingList = new ArrayList<VK>();
	private JLabel label;
	private Main m;
	private boolean isDriverTable;
	
	public CustomDialogTableModel(Main m, String[] columnNames, List<VK> workingList, JLabel outputLabel, ComparatorChain<VK> defaultChain, CustomTable<VK> table, boolean isDriverTable) {
		super(columnNames, defaultChain, table);
		this.m = m;
		this.workingList.addAll(workingList);
		this.label = outputLabel;
		this.isDriverTable = isDriverTable;
	}
	
	@Override
	public Class<?> getColumnClass(int columnIndex) {
		if(columnIndex == 0)return Boolean.class;
		if(columnIndex == 5) return Integer.class;
		return String.class;
	}
	
	@Override
	public boolean isCellEditable(int row, int column) {
		return column == 0;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void setValueAt(Object aValue, int row, int column) {
		if(aValue instanceof Boolean && column == 0){
			Vector rowData = (Vector) getDataVector().get(row);
			rowData.set(column, (boolean) aValue);
			VK vk = m.getVK((int) getValueAt(row, 5));
			
			if(!(boolean) aValue){
				while(workingList.contains(vk)){
					workingList.remove(vk);
				}
			}else{
				if(!workingList.contains(vk))workingList.add(vk);
			}
			workingList = Framework.sort(workingList, getComparatorChain());
			refreshTable();
		}
	}
	
	@Override
	public void refreshTable(){
		List<VK> l = new ArrayList<VK>();
		if(!isDriverTable){
			for(VK vk: m.getDatabase()){
				if(vk.isSelected())l.add(vk);
			}
			l = sort(l);
		}else{
			for(VK vk: m.getDatabase()){
				if(vk.isSelected() && vk.isDriver())l.add(vk);
			}
			l = sort(l);
		}
		this.setRowCount(0);
		Object[][] rowData = new Object[l.size()][this.getColumnCount()];
		for(int i = 0; i < l.size(); i++){
			VK vk = l.get(i);
			rowData[i][0] = workingList.contains(vk);
			rowData[i][1] = Main.getRankString(vk.getRank());
			rowData[i][2] = Main.outputPosition(vk.getPosition());
			rowData[i][3] = vk.getName();
			rowData[i][4] = vk.getSurname();
			rowData[i][5] = vk.getID();
		}
		for(Object[] o:rowData){
			this.addRow(o);
		}
		
		String s = m.getStringFromVKList(workingList);
		if(s == null || s.isEmpty())label.setText(m.getFramework().getString("EMPTY_LIST"));
		else label.setText(s);
		super.refreshTable();
	}

	List<VK> getWorkingList() {
		return workingList;
	}

	
}