package es.ua.dlsi.im3.gui.interaction;

import es.ua.dlsi.im3.core.IM3Exception;

/**
 * @autor drizo
 */
public interface ISelectable {
    ISelectableTraversable getSelectionParent();

    /**
     * The selection manager will indicate it has been selected
     */
    void onSelect();
    void onUnselect();

    void onStartHover();
    void onEndHover();
    /**
     * Avoid using equals or hashCode
     * @return
     */
    String getUniqueID();
}
