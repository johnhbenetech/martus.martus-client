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
package org.martus.client.swingui.jfx.landing;

import javafx.application.Platform;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import org.martus.client.swingui.UiMainWindow;
import org.martus.client.swingui.jfx.generic.PureFxStage;
import org.martus.client.swingui.jfx.landing.bulletins.BulletinsListController;
import org.martus.client.swingui.jfx.landing.cases.FxCaseManagementController;

public class PureFxMainStage implements FxMainStage
{
	public PureFxMainStage(UiMainWindow mainWindowToUse, Stage realStage) throws Exception
	{
		stage = new PureFxStage(mainWindowToUse, "", realStage);
		stage.setOnCloseRequest((event) -> mainWindowToUse.exitNormally());
		shellController = new FxLandingShellController(stage.getMainWindow());
		Scene scene = new Scene(new Pane());
		stage.setScene(scene);
		Platform.runLater(() -> initializeContents());
	}
	
	private void initializeContents()
	{
		try
		{
			stage.showCurrentPage();
			Parent shellContents = shellController.createContents();
			Scene scene = stage.getActualStage().getScene();
			scene.setRoot(shellContents);
		}
		catch(Exception e)
		{
			stage.getMainWindow().unexpectedErrorDlg(e);
		}
	}

	@Override
	public BulletinsListController getBulletinsListController()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public FxCaseManagementController getCaseManager()
	{
		// TODO Auto-generated method stub
		return null;
	}
	
	private PureFxStage stage;
	private FxLandingShellController shellController;
}
