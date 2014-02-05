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

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;

import org.martus.client.swingui.UiMainWindow;

public class FxAddContactsController extends AbstractFxSetupWizardController implements Initializable
{
	public FxAddContactsController(UiMainWindow mainWindowToUse)
	{
		super(mainWindowToUse);
	}
	
	@Override
	public void initialize(URL location, ResourceBundle resources)
	{
		contactNameColumn.setCellValueFactory(new PropertyValueFactory<ContactsTableData, String>("contactName"));
		publicCodeColumn.setCellValueFactory(new PropertyValueFactory<ContactsTableData, String>("publicCode"));
		sentToColumn.setCellValueFactory(new PropertyValueFactory<ContactsTableData, Boolean>("sentTo"));
		receivedFromColumn.setCellValueFactory(new PropertyValueFactory<ContactsTableData, Boolean>("receivedFrom"));
		
		contactNameColumn.setCellFactory(TextFieldTableCell.<ContactsTableData>forTableColumn());
		publicCodeColumn.setCellFactory(TextFieldTableCell.<ContactsTableData>forTableColumn());
		sentToColumn.setCellFactory(CheckBoxTableCell.<ContactsTableData>forTableColumn(sentToColumn));
		receivedFromColumn.setCellFactory(CheckBoxTableCell.<ContactsTableData>forTableColumn(receivedFromColumn));
		
		contactsTableId.setItems(data);
	}
	
	@FXML
	public void addRow()
	{
		data.add(new ContactsTableData("", "", false, false));
	}

	@Override
	public String getFxmlLocation()
	{
		return "setupwizard/FxSetupAddContacts.fxml";
	}
	
	@FXML
	private TableView<ContactsTableData> contactsTableId;
	
	@FXML
	private TableColumn<ContactsTableData, String> contactNameColumn;
	
	@FXML
	private TableColumn<ContactsTableData, String> publicCodeColumn;
	
	@FXML
	private TableColumn<ContactsTableData, Boolean> sentToColumn;
	
	@FXML
	private TableColumn<ContactsTableData, Boolean> receivedFromColumn;
	
	@FXML
	private TableColumn<ContactsTableData, Button> removeColumn;
	
	@FXML
	private Button addRowButtonId;
	
	private ObservableList<ContactsTableData> data = FXCollections.observableArrayList();
}