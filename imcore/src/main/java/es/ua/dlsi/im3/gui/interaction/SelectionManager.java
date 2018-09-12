package es.ua.dlsi.im3.gui.interaction;

import es.ua.dlsi.im3.core.IM3Exception;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * It manages all selection business logic of visual elements
 * @autor drizo
 */
public class SelectionManager {
    private HashMap<String, ISelectable> selectedElements;
    private List<ISelectionChangeListener> selectionChangeListeners;

    public SelectionManager() {
        selectedElements = new HashMap<>();
        selectionChangeListeners = new LinkedList<>();
    }

    private void addElementToSelection(ISelectable element) {
        String ID = element.getUniqueID();
        if (!selectedElements.containsKey(ID)) {
            selectedElements.put(ID, element);
            element.onSelect();
        }
    }

    private void removeElementFromSelection(ISelectable element) {
        String ID = element.getUniqueID();
        if (selectedElements.containsKey(ID)) {
            selectedElements.remove(element.getUniqueID());
            element.onUnselect();
        }
    }

    public void clearSelection() {
        for (ISelectable selectable: selectedElements.values()) {
            selectable.onUnselect();
        }
        selectedElements.clear();
        notifySelectionChange();
    }

    public void select(ISelectable ... elements) {
        clearSelection();
        for (ISelectable selectable: elements) {
            addElementToSelection(selectable);
        }
        notifySelectionChange();
    }

    public void select(Collection<ISelectable> elements) {
        selectedElements.clear();
        for (ISelectable selectable: elements) {
            addElementToSelection(selectable);
        }
        notifySelectionChange();
    }

    public void unSelect(ISelectable ... elements) {
        for (ISelectable selectable: elements) {
            removeElementFromSelection(selectable);
        }
        notifySelectionChange();
    }

    private void notifySelectionChange() {
        for (ISelectionChangeListener selectionChangeListener: selectionChangeListeners) {
            selectionChangeListener.onSelectionChange(getSelection());
        }
    }

    public boolean isSelected(ISelectable element) {
        return selectedElements.containsKey(element.getUniqueID());
    }

    //TODO Promocionar esto a IM3.core
    //TODO Test unitario
    /**
     * It returns the most possible specific class all selected elements belong to
     * @return null if no element selected. It will return Object.class if this is the only common class
     */
    public Class<?> getCommonBaseClass() {
        Class<?> lastCommonClass = null;
        for (ISelectable selectable: selectedElements.values()) {
            if (lastCommonClass == null) {
                lastCommonClass = selectable.getClass();
            } else {
                while (!lastCommonClass.isAssignableFrom(selectable.getClass())) {
                    lastCommonClass = lastCommonClass.getSuperclass();
                }
            }
        }
        return lastCommonClass;
    }

    /**
     *
     * @param clazz
     * @return True if all selected elements belong (directly or through inheritance) to the given clazz class
     */
    public boolean isCommonBaseClass(Class<?> clazz) {
        Class<?> commonBaseClass = getCommonBaseClass();
        return commonBaseClass != null && clazz.isAssignableFrom(commonBaseClass);
    }

    public void subscribe(ISelectionChangeListener selectionChangeListener) {
        selectionChangeListeners.add(selectionChangeListener);
    }

    public void ubsubscribe(ISelectionChangeListener selectionChangeListener) {
        selectionChangeListeners.remove(selectionChangeListener);
    }

    public Collection<ISelectable> getSelection() {
        return selectedElements.values();
    }

    /**
     *
     * @return Selected element
     * @throws IM3Exception If no element is selected or more than one element are selected
     */
    public ISelectable getSingleElementSelected() throws IM3Exception {
        if (selectedElements.isEmpty()) {
            throw new IM3Exception("No element is selected");
        }
        if (selectedElements.size() > 1) {
            throw new IM3Exception("There are " + selectedElements.size() + " selected elements");
        }
        return selectedElements.values().iterator().next();
    }

}
