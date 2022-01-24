package de.vkd.database;


public abstract class ObjectCreator<E> {
	private String[] columnNames;
	public ObjectCreator(String[] columnNames) {
		this.columnNames = columnNames;
	}
	public abstract E create(String... columnEntries);
	
	public int getColumnIndex(String colName){
		for(int i = 0; i < columnNames.length; i++){
			if(columnNames[i].equals(colName))return i;
		}
		return -1;
	}
}