package de.vkd.gui;

import java.awt.event.FocusEvent;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

@SuppressWarnings("serial")
public class SearchHintTextField extends HintTextField implements DocumentListener {
  private final int[] index;
  private CustomTable<?> table;
  private IndexReturner indexReturner;

  public SearchHintTextField(String hint, int... index) {
    super(hint);
    this.index = index;
    this.indexReturner = null;
    getDocument().addDocumentListener(this);
  }

  public SearchHintTextField(String hint, IndexReturner ret) {
    super(hint);
    this.index = null;
    this.indexReturner = ret;
    getDocument().addDocumentListener(this);
  }


  @Override
  public void changedUpdate(DocumentEvent e) {
    update();
  }

  @Override
  public void insertUpdate(DocumentEvent e) {
    update();
  }

  @Override
  public void removeUpdate(DocumentEvent e) {
    update();
  }

  @Override
  public void focusGained(FocusEvent e) {
    super.focusGained(e);
    update();
  }

  @Override
  public void focusLost(FocusEvent e) {
    super.focusLost(e);
    if (getText().trim().isEmpty()) {
      update();
    }
  }

  public void update() {
    if (table != null) {
      table.search(getText(), getIndex());
    }
  }

  public void setTable(CustomTable<?> table) {
    this.table = table;
    this.indexReturner = null;
  }

  public void setIndexReturner(IndexReturner indexReturner) {
    this.indexReturner = indexReturner;
  }

  public int[] getIndex() {
    if (indexReturner != null) {
      return indexReturner.getIndex();
    } else {
      return index;
    }
  }
}
