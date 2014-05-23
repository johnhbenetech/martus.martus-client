/*

The Martus(tm) free, social justice documentation and
monitoring software. Copyright (C) 2001-2007, Beneficent
Technology, Inc. (The Benetech Initiative).

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

package org.martus.client.swingui.actions;

import javax.swing.AbstractAction;

import org.martus.client.bulletinstore.ClientBulletinStore;
import org.martus.client.core.MartusApp;
import org.martus.client.swingui.MartusLocalization;
import org.martus.client.swingui.UiMainPane;
import org.martus.client.swingui.UiMainWindow;
import org.martus.client.swingui.bulletintable.UiBulletinTablePane;
import org.martus.client.swingui.foldertree.UiFolderTreePane;

abstract public class UiMartusAction extends AbstractAction
{
	public UiMartusAction(UiMainWindow mainWindowToUse, String label)
	{
		super(label);
		mainWindow = mainWindowToUse;
	}
	
	protected MartusApp getApp()
	{
		return mainWindow.getApp();
	}
	
	protected ClientBulletinStore getStore()
	{
		return getApp().getStore();
	}
	
	protected MartusLocalization getLocalization()
	{
		return mainWindow.getLocalization();
	}
	
	protected UiMainWindow getMainWindow()
	{
		return mainWindow;
	}
	
	protected UiMainPane getMainPane()
	{
		return getMainWindow().getMainPane();
	}

	protected UiFolderTreePane getFolderTreePane()
	{
		return getMainPane().getFolderTreePane();
	}

	protected UiBulletinTablePane getBulletinsTable()
	{
		return getMainPane().getBulletinsTable();
	}

	public void doModifyBulletin()
	{
		getBulletinsTable().doModifyBulletin();
	}

	public void doSelectAllBulletins()
	{
		getBulletinsTable().doSelectAllBulletins();	
	}

	UiMainWindow mainWindow;
}