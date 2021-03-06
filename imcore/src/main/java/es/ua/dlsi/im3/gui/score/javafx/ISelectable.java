/*
 * Copyright (C) 2014 David Rizo Valero
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package es.ua.dlsi.im3.gui.score.javafx;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.gui.command.ICommand;
import javafx.beans.property.DoubleProperty;
import javafx.scene.Node;
import javafx.scene.input.KeyEvent;

/**
 *
 * @author drizo
 */
public interface ISelectable {
    ISelectableTraversable getParent();    
    void select();
    void unselect();
    
    void startEdit() throws IM3Exception;
    void endEdit()  throws IM3Exception;

    /**
     * Return the command to be executed
     * @param t
     * @return 
     */
    public ICommand onKeyEvent(KeyEvent t);

    public void startHover();
    public void endHover();

    /**
     * Any value valid for ordering
     * @return 
     */
    DoubleProperty getOrder();
    

    public void showContextMenu(double x, double y);

    Node getRoot();
    /**
     * For traversing with tab skipping different types (e.g. from barline to barline)
     * @return 
     */
    public String getSelectionType();
    
    //void buildInteraction();

    //public ICommand onDeleteAction();

}
