/*
 * Copyright (C) 2016 David Rizo Valero
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

package es.ua.dlsi.im3.omr.muret.useractionslog;


/**
 * Used as a pool of objects to avoid creating too many objects for user action logging
 * Public access to optimize
 * @author drizo
 */
public class UserActionsPool {
    static public CategoryAddStroke addStroke = new CategoryAddStroke();
    static public CategorySymbolAccept symbolAccept = new CategorySymbolAccept();
    static public CategorySymbolCancel symbolCancel = new CategorySymbolCancel();
    static public CategorySymbolChange symbolChange = new CategorySymbolChange();
    static public CategorySymbolCompleteTimer symbolCompleteTimer = new CategorySymbolCompleteTimer();
    static public CategorySymbolSelect symbolSelect = new CategorySymbolSelect();
    static public CategorySymbolSetPitch symbolSetPitch = new CategorySymbolSetPitch();
    static public CategorySymbolDelete symbolDelete = new CategorySymbolDelete();

    static public UserActionUndo undo = new UserActionUndo();
    static public UserActionRedo redo = new UserActionRedo();
    static public UserActionZoomIn zoomIn = new UserActionZoomIn();
    static public UserActionZoomOut zoomOut = new UserActionZoomOut();

    static public UserActionProjectOpen projectOpen = new UserActionProjectOpen();
    static public UserActionProjectClose projectClose = new UserActionProjectClose();
}
