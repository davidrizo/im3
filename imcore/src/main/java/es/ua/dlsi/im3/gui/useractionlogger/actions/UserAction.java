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

package es.ua.dlsi.im3.gui.useractionlogger.actions;


import es.ua.dlsi.im3.gui.useractionlogger.IAction;
import es.ua.dlsi.im3.gui.useractionlogger.UserActionCategory;

/**
 *
 * @author drizo
 */
public abstract class UserAction implements IAction {
    UserActionCategory category;
    private Object[] fields;
    
    public UserAction(UserActionCategory category, Object [] fields) {
	this.category = category;
	this.fields = fields;
    }

    public UserAction(UserActionCategory category) {
	this.category = category;
    }
    
    @Override
    public UserActionCategory getCategory() {
	return category;
    }

    @Override
    public Object[] getFields() {
	return fields;
    }    
    
    protected void setFields(Object [] fields) {
	this.fields = fields;
    }
}
