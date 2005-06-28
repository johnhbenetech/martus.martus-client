/*

The Martus(tm) free, social justice documentation and
monitoring software. Copyright (C) 2001-2005, Beneficent
Technology, Inc. (Benetech).

Martus is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either
version 2 of the License, or (at your option) any later
version with the additions and exceptions described in the
accompanying Martus license file entitled "license.txt".

It is distributed WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, including warranties of fitness of purpose or
merchantability.  See the accompanying Martus License and
GPL license for more details on the required license terms
for this software.

You should have received a copy of the GNU General Public
License along with this program; if not, write to the Free
Software Foundation, Inc., 59 Temple Place - Suite 330,
Boston, MA 02111-1307, USA.

*/
package org.martus.client.swingui.fields;

import javax.swing.JComponent;
import javax.swing.ListSelectionModel;

import org.martus.client.swingui.grids.GridTable;
import org.martus.client.swingui.grids.GridTableModel;
import org.martus.common.GridData;
import org.martus.common.fieldspec.GridFieldSpec;
import org.martus.swing.UiScrollPane;
import org.martus.swing.UiTable;


public class UiGrid extends UiField
{
	public UiGrid(GridFieldSpec fieldSpec)
	{
		this(new GridTableModel(fieldSpec));
	}
	
	public UiGrid(GridTableModel modelToUse)
	{
		model = modelToUse;
		table = new GridTable(model);
		table.setColumnSelectionAllowed(false);
		table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		table.setShowGrid(true);
		table.changeSelection(0, 1, false, false);
		table.setRowHeight(table.getRowHeight() + ROW_HEIGHT_PADDING);
		widget = new UiScrollPane(table);
	}	
	
	public JComponent getComponent()
	{
		return widget;
	}
	
	public String getText()
	{
		return model.getXmlRepresentation();
	}

	public void setText(String newText)
	{
		try
		{
			model.setFromXml(newText);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public GridData getGridData()
	{
		return model.getGridData();
	}
	
	private static final int ROW_HEIGHT_PADDING = 3;

	UiScrollPane widget;
	UiTable table;
	GridTableModel model;
}

