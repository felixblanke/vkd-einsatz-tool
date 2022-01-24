package de.vkd.gui;

import java.awt.*;
import java.io.*;
import java.util.*;
import java.util.List;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

/**
 * @author User "Lawrence Dol" from stackoverflow.com
 * @see https://stackoverflow.com/questions/342990/create-java-console-inside-a-gui-panel/343007#343007
 * Code is mainly written by "Lawrence Dol" and editted by Felix Blanke to fit the needs.
 */
public class TextAreaOutputStream extends OutputStream {
	
	
	
	private byte[] oneByte;
	private Appender appender; 

	public TextAreaOutputStream(JTextPane txtpane) {
		this(txtpane,1000);
    	}

	public TextAreaOutputStream(JTextPane txtpane, int maxlin) {
		if(maxlin<1)throw new IllegalArgumentException("TextAreaOutputStream maximum lines must be positive (value="+maxlin+")");
		oneByte=new byte[1];
		appender=new Appender(txtpane,maxlin);
	}

	/** Clear the current console text area. */
	public synchronized void clear() {
		if(appender!=null)appender.clear();
	}

	public synchronized void close() {
		appender=null;
	}

	public synchronized void flush() {}

	public synchronized void write(int val) {
		oneByte[0]=(byte)val;
		write(oneByte,0,1);
	}

	public synchronized void write(byte[] ba) {
		write(ba,0,ba.length);
		}

	public synchronized void write(byte[] ba,int str,int len) {
		if(appender!=null)appender.append(bytesToString(ba,str,len));
	}

	static private String bytesToString(byte[] ba, int str, int len) {
		return new String(ba,str,len);
	}

	static class Appender implements Runnable{
		private final JTextPane textPane;
		private final int maxLines;							//maximum lines allowed in text area
		private final LinkedList<Integer>lengths; 			//length of lines within text area
		private final List<String> values;					//values waiting to be appended
		
		private int curLength;								// length of current line
		private boolean clear;
		private boolean queue;

		Appender(JTextPane txtara, int maxlin) {
			textPane = txtara;
			maxLines = maxlin;
			lengths = new LinkedList<Integer>();
			values = new ArrayList<String>();
			
			curLength=0;
			clear = false;
			queue = true;
		}
	
			synchronized void append(String val) {
				values.add(val);
				if(queue){
					queue=false; 
					EventQueue.invokeLater(this);
				}
			}
	
		synchronized void clear() {
			clear=true;
			curLength=0;
			lengths.clear();
			values.clear();
			if(queue){
				queue=false; 
				EventQueue.invokeLater(this);
			}
		}
	
		// MUST BE THE ONLY METHOD THAT TOUCHES textArea!
		public synchronized void run() {
			if(clear) { textPane.setText(""); }
			for(String val: values) {
				curLength+=val.length();
				if(val.endsWith(EOL1) || val.endsWith(EOL2)) {
					if(lengths.size()>=maxLines) { 
						textPane.setText(textPane.getText().substring(lengths.removeFirst()));
					}
					lengths.addLast(curLength);
					curLength=0;
				}
				if(val.startsWith("[WARNING]")||val.startsWith("[SEVERE]")){
					StyledDocument doc = textPane.getStyledDocument();
					Style style = textPane.addStyle("style", null);
					StyleConstants.setForeground(style, Color.RED);
					
					try{
						doc.insertString(doc.getLength(), val ,style);
					}catch (BadLocationException e){}
				}else{
					StyledDocument doc = textPane.getStyledDocument();
					Style style = textPane.addStyle("style", null);
					StyleConstants.setForeground(style, Color.WHITE);
					
					try{
						doc.insertString(doc.getLength(), val ,style);
					}catch (BadLocationException e){}
				}
			}
			values.clear();
			clear =false;
			queue =true;
		}
	
		static private final String EOL1 = "\n";
		static private final String EOL2 = System.getProperty("line.separator", EOL1);
	}
} /* END PUBLIC CLASS */