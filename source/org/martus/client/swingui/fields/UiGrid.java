/*

The Martus(tm) free, social justice documentation and
monitoring software. Copyright (C) 2001-2004, Beneficent
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

import java.awt.Color;
import java.awt.Component;

import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;

import org.martus.common.GridFieldSpec;
import org.martus.swing.UiTable;


public class UiGrid extends UiField
{

	public UiGrid(GridFieldSpec fieldSpec)
	{
		super();
		model = new GridTableModel(fieldSpec);
		table = new GridTable(model);
		table.setColumnSelectionAllowed(false);
		table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		table.setShowGrid(true);
		table.changeSelection(0, 1, false, false);		
		widget = new JScrollPane(table);
	}	
	
	class GridTable extends UiTable
	{
		public GridTable(TableModel model)
		{
			super(model);
			if(model.getColumnCount() > 5)
				setAutoResizeMode(AUTO_RESIZE_OFF);
		}
		
		public TableCellRenderer getCellRenderer(int row, int column)
		{
			//TODO Optimize: don't create a new renderer each time
			return new myCellRenderer();
		}

		public void changeSelection(int rowIndex, int columnIndex,
				boolean toggle, boolean extend)
		{
			if(columnIndex == 0)
				columnIndex = 1;
			super.changeSelection(rowIndex, columnIndex, toggle, extend);
		}
	}
	
	class myCellRenderer implements TableCellRenderer
	{
		public Component getTableCellRendererComponent(JTable tableToUse, Object value, boolean isSelected, boolean hasFocus, int row, int column)
		{
			JTextField cell = new JTextField((String)value);
			cell.setBorder(new EmptyBorder(0,0,0,0));
			if(column == 0)
			{
				cell.setBackground(Color.LIGHT_GRAY);
				cell.setForeground(Color.BLACK);
				return cell;
			}
			
			if(hasFocus)
			{
				cell.setBorder(new LineBorder(Color.BLACK,1));
			}
			else
			{
				if(isSelected)
				{
//					cell.setBackground(new Color(0,0,0));
				}
				else
				{
	//				cell.setBackground(new Color(255,255,255));
				}
			}
				
			return cell;
		}
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

	JScrollPane widget;
	UiTable table;
	GridTableModel model;
}
