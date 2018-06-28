package es.ua.dlsi.im3.gui.interaction;

import java.util.Collection;

/**
 * A class that can receive selection changes notifications
 * @autor drizo
 */
public interface ISelectionChangeListener {
    void onSelectionChange(Collection<ISelectable> selection);
}
