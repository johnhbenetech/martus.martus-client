/*

The Martus(tm) free, social justice documentation and
monitoring software. Copyright (C) 2014, Beneficent
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

import java.util.Iterator;
import java.util.Vector;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;

import org.martus.client.bulletinstore.BulletinFolder;
import org.martus.client.core.ConfigInfo;
import org.martus.client.core.MartusApp.SaveConfigInfoException;
import org.martus.client.swingui.MartusLocalization;
import org.martus.client.swingui.UiMainWindow;
import org.martus.client.swingui.actions.ActionDoer;
import org.martus.client.swingui.actions.ActionMenuBackupMyKeyPair;
import org.martus.client.swingui.actions.ActionMenuChangeUserNamePassword;
import org.martus.client.swingui.actions.ActionMenuContactInfo;
import org.martus.client.swingui.actions.ActionMenuCreateNewBulletin;
import org.martus.client.swingui.actions.ActionMenuManageContacts;
import org.martus.client.swingui.actions.ActionMenuPreferences;
import org.martus.client.swingui.actions.ActionMenuQuickSearch;
import org.martus.client.swingui.actions.ActionMenuSelectServer;
import org.martus.client.swingui.jfx.FxContentController;
import org.martus.client.swingui.jfx.FxInSwingFrameController;
import org.martus.client.swingui.jfx.landing.FxFolderSettingsController.FolderNotFoundException;
import org.martus.common.MartusLogger;
import org.martus.common.fieldspec.ChoiceItem;
import org.martus.common.network.OrchidTransportWrapper;

public class FxLandingShellController extends FxInSwingFrameController
{
	public FxLandingShellController(UiMainWindow mainWindowToUse)
	{
		super(mainWindowToUse);
		caseListProvider = new CaseListProvider();
		folderNameUserDefined = "";
	}

	@Override
	public String getFxmlLocation()
	{
		return "landing/LandingShell.fxml";
	}
	
	@Override
	public void initializeMainContentPane()
	{
		updateOnlineStatus();
		updateTorStatus();
		updateCases();
	}

	private void updateCases()
	{
		ConfigInfo config = getApp().getConfigInfo();
		folderNameCode = config.getFolderLabelCode();
		folderNameUserDefined = config.getFolderLabelCustomName();
		updateFolderLabel();
		
		caseListProvider.clear();
		Vector visibleFolders = getApp().getStore().getAllVisibleFolders();
		MartusLocalization localization = getLocalization();
		for(Iterator f = visibleFolders.iterator(); f.hasNext();)
		{
			BulletinFolder folder = (BulletinFolder) f.next();
			CaseListItem caseList = new CaseListItem(folder, localization);
			caseListProvider.add(caseList);
		}
		casesListView.setItems(caseListProvider);
	}

	@Override
	public void setContentPane(FxContentController contentController) throws Exception
	{
		Parent createContents = contentController.createContents();
		mainContentPane.getChildren().addAll(createContents);
	}

	private void updateTorStatus()
	{
		OrchidTransportWrapper transport = getApp().getTransport();
		boolean isTorRequested = transport.isTorEnabled();

		String text = isTorRequested ? "On" : "Off";
		toolbarButtonTor.setText(text);
	}
	
	private void updateOnlineStatus()
	{
		boolean isOnline = getApp().getTransport().isOnline();
		String text = isOnline ? "On" : "Off";
		toolbarButtonOnline.setText(text);
	}

	private void doAction(ActionDoer doer)
	{
		getStage().doAction(doer);
	}
	
	@FXML
	private void onPreferences(ActionEvent event)
	{
		doAction(new ActionMenuPreferences(getMainWindow()));
	}

	@FXML
	private void onManageContacts(ActionEvent event)
	{
		doAction(new ActionMenuManageContacts(getMainWindow()));
	}

	@FXML
	private void onConfigureServer(ActionEvent event)
	{
		doAction(new ActionMenuSelectServer(getMainWindow()));
	}
	
	@FXML
	private void onChangeUsernameAndPassword(ActionEvent event)
	{
		doAction(new ActionMenuChangeUserNamePassword(getMainWindow()));
	}
	
	@FXML
	private void onCreateNewAccount(ActionEvent event)
	{
		//TODO: add doAction
	}
	
	@FXML
	private void onQuickSearch(ActionEvent event)
	{
		doAction(new  ActionMenuQuickSearch(getMainWindow(), searchText.getText()));
	}

	@FXML
	private void onCreateNewBulletin(ActionEvent event)
	{
		doAction(new ActionMenuCreateNewBulletin(getMainWindow()));
	}
	
	@FXML
	private void onOnline(ActionEvent event)
	{
		boolean oldState = getApp().getTransport().isOnline();
		try
		{
			ConfigInfo configInfo = getApp().getConfigInfo();
			boolean newState = !oldState;
			
			configInfo.setIsNetworkOnline(newState);
			getApp().saveConfigInfo();

			updateOnlineStatus();
		} 
		catch (SaveConfigInfoException e)
		{
			MartusLogger.logException(e);
			getMainWindow().notifyDlg("ErrorSavingConfig");
		}
		catch (Exception e)
		{
			getStage().logAndNotifyUnexpectedError(e);
		}
	}

	@FXML
	private void onTor(ActionEvent event)
	{
		boolean oldState = getApp().getTransport().isTorEnabled();
		try
		{
			ConfigInfo configInfo = getApp().getConfigInfo();
			boolean newState = !oldState;
			
			configInfo.setUseInternalTor(newState);
			getApp().saveConfigInfo();

			updateTorStatus();
		} 
		catch (SaveConfigInfoException e)
		{
			MartusLogger.logException(e);
			getMainWindow().notifyDlg("ErrorSavingConfig");
		}
		catch (Exception e)
		{
			getStage().logAndNotifyUnexpectedError(e);
		}
	}

	@FXML
	private void onContactInformation(ActionEvent event)
	{
		doAction(new ActionMenuContactInfo(getMainWindow()));
	}
	
	@FXML
	private void onBackupKeypair(ActionEvent event)
	{
		doAction(new ActionMenuBackupMyKeyPair(getMainWindow()));
	}
	
	@FXML
	private void OnCasesMouseClicked(MouseEvent mouseEvent)
	{
		try
		{
			int selectedIndex = casesListView.getSelectionModel().getSelectedIndex();
			if(selectedIndex == INVALID_INDEX)
				return;
			CaseListItem selectedCase = caseListProvider.get(selectedIndex);
			BulletinFolder folder = getApp().findFolder(selectedCase.getName());
			BulletinsListController bulletinListController = (BulletinsListController)getStage().getCurrentController();
			bulletinListController.loadBulletinData(folder.getAllUniversalIdsUnsorted());
		} 
		catch (Exception e)
		{
			getStage().logAndNotifyUnexpectedError(e);
		}
	}

	@FXML
	public void onLogoClicked(MouseEvent mouseEvent) 
	{
		try
		{
			BulletinsListController bulletinListController = (BulletinsListController)getStage().getCurrentController();
			bulletinListController.loadAllBulletinsAndSortByMostRecent();
		} 
		catch (Exception e)
		{
			getStage().logAndNotifyUnexpectedError(e);
		}
	}

	private final class FolderNameChoiceBoxListener implements ChangeListener<ChoiceItem>
	{
		public FolderNameChoiceBoxListener()
		{
		}

		@Override public void changed(ObservableValue<? extends ChoiceItem> observableValue, ChoiceItem originalItem, ChoiceItem newItem) 
		{
			folderNameCode = newItem.getCode();
			updateFolderLabel();
		}
	}

	private final class FolderCustomNameListener implements ChangeListener<String>
	{
		public FolderCustomNameListener()
		{
		}

		@Override public void changed(ObservableValue<? extends String> observableValue, String original, String newLabel) 
		{
			folderNameUserDefined = newLabel;
			updateFolderLabel();
		}
	}

	protected void updateFolderLabel()
	{
		try
		{
			folderNameLabel.setText(FxFolderSettingsController.getFolderLabel(getLocalization(),folderNameCode, folderNameUserDefined));
		} 
		catch (FolderNotFoundException e)
		{
			getStage().logAndNotifyUnexpectedError(e);
		}
	}

	
	@FXML
	public void onFolderSettingsClicked(MouseEvent mouseEvent) 
	{
		folderManagement = new FxFolderSettingsController(getMainWindow(), new FolderNameChoiceBoxListener(), new FolderCustomNameListener());
		folderManagement.setInitialFolderName(folderNameCode, folderNameUserDefined);
		doAction(folderManagement);
	}
	
	private final int INVALID_INDEX = -1;
	@FXML
	protected TextField searchText;
	
	@FXML
	protected Button toolbarButtonOnline;
	
	@FXML
	protected Button toolbarButtonTor;
	
	@FXML
	protected AnchorPane mainContentPane;
	
	@FXML
	protected ListView<CaseListItem> casesListView;
	
	@FXML
	protected Label folderNameLabel;

	private CaseListProvider caseListProvider;
	protected String folderNameCode;
	protected String folderNameUserDefined;
	protected FxFolderSettingsController folderManagement;
}