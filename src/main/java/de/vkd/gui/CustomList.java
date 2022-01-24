package de.vkd.gui;

import java.util.List;

import javax.swing.AbstractListModel;
import javax.swing.JList;
import javax.swing.ListSelectionModel;

import de.vkd.auxiliary.NamedComparator;

/**
 * @author Felix Blanke
 * Only used in {@link SortDialog}
 * @param <E> The Elements of the List, e.g. {@link VK} or {@link Kuerzung}
 */

@SuppressWarnings("serial")
public class CustomList<E> extends JList<String>{
    private ComparatorListModel model;

    public CustomList(List<NamedComparator<E>> comparatorList) {
        setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        model = new ComparatorListModel(comparatorList);
        setModel(model);
    }

    public void moveElementUp(int index){
        model.moveElementUp(index);
    }
    public void moveElementDown(int index){
        model.moveElementDown(index);
    }
    public void removeElementAt(int index){
        model.removeElementAt(index);
    }
    public void addElement(NamedComparator<E> newElement){
        model.addElement(newElement);
    }
    public NamedComparator<E> getNamedComparatorAt(int index){
        return model.getNamedComparatorAt(index);
    }
    public List<NamedComparator<E>> getNamedComparatorList(){
        return model.getNamedComparatorList();
    }

    class ComparatorListModel extends AbstractListModel<String>{
        private List<NamedComparator<E>> currentList;
        public ComparatorListModel(List<NamedComparator<E>> currentList) {
            this.currentList = currentList;
            if(currentList != null && !currentList.isEmpty())super.fireIntervalAdded(this, 0, currentList.size()-1);
        }

        @Override
        public String getElementAt(int index) {
            return (index+1) + ". " + currentList.get(index).getName();
        }

        @Override
        public int getSize() {
            return currentList != null ? currentList.size() : 0;
        }

        public void removeElementAt(int index){
            if(currentList == null) return;
            currentList.remove(index);
            super.fireIntervalRemoved(this, index, index);
        }
        public void addElement(NamedComparator<E> newElement){
            if(currentList == null) return;
            int index = currentList.size();
            currentList.add(newElement);
            super.fireIntervalAdded(this, index, index);
        }
        public void moveElementUp(int index){
            if(currentList == null) return;
            if(index > 0){
                NamedComparator<E> temp = currentList.get(index);
                currentList.set(index, currentList.get(index-1));
                currentList.set(index-1, temp);
                setSelectedIndex(index-1);
                super.fireContentsChanged(this, index, index-1);
            }
        }
        public void moveElementDown(int index){
            if(currentList == null) return;
            if(index < currentList.size()-1){
                NamedComparator<E> temp = currentList.get(index);
                currentList.set(index, currentList.get(index+1));
                currentList.set(index+1, temp);
                setSelectedIndex(index+1);
                super.fireContentsChanged(this, index, index+1);
            }
        }
        public NamedComparator<E> getNamedComparatorAt(int index){
            return currentList == null ? null : currentList.get(index);
        }
        public List<NamedComparator<E>> getNamedComparatorList(){
            return currentList;
        }
    }
}
