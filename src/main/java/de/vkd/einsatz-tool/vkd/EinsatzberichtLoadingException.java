package de.vkd.einsatz_tool.vkd;

@SuppressWarnings("serial")
public class EinsatzberichtLoadingException extends Exception{
	public EinsatzberichtLoadingException(String fileName, String msg, int row, int col) {
		super("The Einsatzbericht \"" + fileName + "\" could not be loaded. (" + msg + "(row " + row + ", col " + col + "))");
	}
}
