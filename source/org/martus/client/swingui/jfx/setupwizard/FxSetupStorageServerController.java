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
package org.martus.client.swingui.jfx.setupwizard;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;

import org.martus.client.core.MartusApp.SaveConfigInfoException;
import org.martus.client.swingui.UiMainWindow;
import org.martus.client.swingui.jfx.FxController;
import org.martus.common.MartusLogger;

public class FxSetupStorageServerController extends AbstractFxSetupWizardContentController
{
	public FxSetupStorageServerController(UiMainWindow mainWindowToUse)
	{
		super(mainWindowToUse);
		
		destination = null;
	}
	
	@Override
	public void initialize(URL rootLocation, ResourceBundle bundle)
	{
		super.initialize(rootLocation, bundle);
		
		getWizardNavigationHandler().getNextButton().setVisible(false);
		defaultServerButton.setDefaultButton(true);
	}

	@Override
	public String getFxmlLocation()
	{
		return "setupwizard/SetupStorageServer.fxml";
	}

	@Override
	public FxController getNextControllerClassName()
	{
		return destination;
	}
	
	@FXML
	public void setupServerLater()
	{
		destination = new FxSetupImportTemplatesController(getMainWindow());
		getWizardStage().next();
	}
	
	@FXML
	public void useDefaultServer()
	{
		try
		{
			getApp().getConfigInfo().setServerName(getDefaultServerIp());
			getApp().getConfigInfo().setServerPublicKey(getDefaultServerPublicKey());
			getApp().saveConfigInfo();
		} 
		catch (SaveConfigInfoException e)
		{
			MartusLogger.logException(e);
			System.exit(1);
		}
		
		destination = new FxAddContactsController(getMainWindow());
		getWizardStage().next();
	}
	
	private String getDefaultServerIp()
	{
		return IP_FOR_SL1_US;
	}

	private String getDefaultServerPublicKey()
	{
		return PUBLIC_KEY_FOR_SL1_US;
	}

	@FXML
	public void advancedServerSettings()
	{
		destination = new FxAdvancedServerStorageSetupController(getMainWindow());
		getWizardStage().next();
	}
	
	private static final String IP_FOR_SL1_US = "54.213.152.140";
	private static final String PUBLIC_KEY_FOR_SL1_US = 
			"MIIBojANBgkqhkiG9w0BAQEFAAOCAY8AMIIBigKCAYEAjIX0yCfct1/WQptimL"
			+ "jK35F3wsW/SEQ8DGdxfMBTZX1GVoOD6zg0d71Ns1ij4FdnOUsD4QCN4Kiay"
			+ "Q+l28eIU8LL8L5oJClFwsVqgNDvPn8jR/CAbPy9NL0gKHevvX/dciVVCSrg"
			+ "Oyyc9p9MP05qyekXqVIfLoZNkcXL5tQKrEiqVdJaDEPepPIkQpBgFwF0QZl"
			+ "J7NdgF4T5wSyEt+fxL7qnZOCqchF8aVbSzAaGLRQEJEtFYTa9mOUCdCLtcn"
			+ "sdgnj+lLftaV5+8o8ZeUTbyH5H/NlLddboxlI8rNalY7E5f3DltOOmTyjMh"
			+ "KSaxl9lfIxpfKoeLdYb5bA74BV1AjbwnxahlN4KRZm/7i0RkapKIXZ0Hqus"
			+ "4JKUG5CJcIybS64ppt8ufCvAEERrZUzrrIDNwv+qob9PYFdiMq1xg+VNrxm"
			+ "/0RXfjwgXxNjDS07MTQc2w/z1egtsDLSi4dALw69nefS0hbZwbv8dIrN23i"
			+ "Hn0FNdbz81l1FrELGyh1hRAgMBAAE=";

	@FXML
	private Button laterButton;

	@FXML
	private Button defaultServerButton;

	@FXML
	private Hyperlink advancedHyperlink;
	
	private FxController destination;
}
