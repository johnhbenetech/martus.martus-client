/*

The Martus(tm) free, social justice documentation and
monitoring software. Copyright (C) 2015, Beneficent
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
package org.martus.client.swingui.jfx.generic;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;

import javax.swing.JPanel;

import org.martus.client.swingui.MartusLocalization;
import org.martus.client.swingui.UiMainWindow;

public class SwingDialogContentPane extends JPanel
{
	public SwingDialogContentPane(UiMainWindow mainWindowToUse)
	{
		setMainWindow(mainWindowToUse);
		isActive = new SimpleBooleanProperty(true);
	}

	public UiMainWindow getMainWindow()
	{
		return mainWindow;
	}

	private void setMainWindow(UiMainWindow mainWindow)
	{
		this.mainWindow = mainWindow;
	}
	
	public MartusLocalization getLocalization()
	{
		return getMainWindow().getLocalization();
	}

	public void addIsActiveListener(ChangeListener<Boolean> listener)
	{
		isActive.addListener(listener);
	}
	
	protected void dispose()
	{
		isActive.setValue(false);
	}

	protected void setTitle(String newTitle)
	{
		title = newTitle;
	}

	public String getTitle()
	{
		return title;
	}

	private UiMainWindow mainWindow;
	private SimpleBooleanProperty isActive;
	private String title;
}