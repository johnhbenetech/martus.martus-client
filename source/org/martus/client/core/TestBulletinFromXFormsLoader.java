/*

Martus(TM) is a trademark of Beneficent Technology, Inc. 
This software is (c) Copyright 2001-2015, Beneficent Technology, Inc.

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
package org.martus.client.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javafx.beans.property.ReadOnlyObjectWrapper;

import org.martus.client.swingui.jfx.generic.data.ObservableChoiceItemList;
import org.martus.common.GridData;
import org.martus.common.GridRow;
import org.martus.common.HeadquartersKey;
import org.martus.common.HeadquartersKeys;
import org.martus.common.MiniLocalization;
import org.martus.common.bulletin.Bulletin;
import org.martus.common.crypto.MockMartusSecurity;
import org.martus.common.fieldspec.ChoiceItem;
import org.martus.common.fieldspec.DropDownFieldSpec;
import org.martus.common.fieldspec.FieldSpec;
import org.martus.common.fieldspec.FieldTypeDropdown;
import org.martus.common.fieldspec.FieldTypeNormal;
import org.martus.common.fieldspec.GridFieldSpec;
import org.martus.common.fieldspec.StandardFieldSpecs;
import org.martus.common.packet.UniversalId;
import org.martus.common.test.MockBulletinStore;
import org.martus.util.TestCaseEnhanced;

public class TestBulletinFromXFormsLoader extends TestCaseEnhanced
{
	public TestBulletinFromXFormsLoader(String name)
	{
		super(name);
	}
	
	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		
		security = MockMartusSecurity.createClient();
		localization = new MiniLocalization();
		store = new MockBulletinStore(this);
		store.setSignatureGenerator(security);
	}
	
	private MiniLocalization getLocalization()
	{
		return localization;
	}
	
	public void testFxBulletinWithXFormsWithOneInputField() throws Exception
	{
		FxBulletin fxBulletin = new FxBulletin(getLocalization());
		assertEquals("FxBulletin field specs should be filled?", 0, fxBulletin.getFieldSpecs().size());
		
		Bulletin bulletin = new Bulletin(security);
		bulletin.getFieldDataPacket().setXFormsModelAsString(getXFormsModelWithOnStringInputFieldXmlAsString());
		bulletin.getFieldDataPacket().setXFormsInstanceAsString(getXFormsInstanceXmlAsString());
		fxBulletin.copyDataFromBulletin(bulletin, store);
		assertEquals("FxBulletin filled from bulletin with data should have data?", getExpectedFieldCount(1), fxBulletin.getFieldSpecs().size());
		
		String TAG = "name";
		FieldSpec fieldSpec = fxBulletin.findFieldSpecByTag(TAG);
		assertTrue("Only field should be string?", fieldSpec.getType().isString());
		assertEquals("Incorrect field label?", FIELD_LABEL, fieldSpec.getLabel());
		assertEquals("Incorrect field tag?", TAG, fieldSpec.getTag());
		FxBulletinField field = fxBulletin.getField(fieldSpec);
		assertEquals("Incorrect field value?", FIELD_VALUE, field.getValue());
	}
	
	public void testFxBulletinWithXFormsWithChoiceField() throws Exception
	{
		FxBulletin fxBulletin = new FxBulletin(getLocalization());
		verifyFieldSpecCount(fxBulletin, 0);
		
		Bulletin bulletin = new Bulletin(security);
		bulletin.getFieldDataPacket().setXFormsModelAsString(getXFormsModelWithOnChoiceInputFieldXmlAsString());
		bulletin.getFieldDataPacket().setXFormsInstanceAsString(getXFormsInstanceWithChoiceAnswers());
		fxBulletin.copyDataFromBulletin(bulletin, store);
		verifyFieldSpecCount(fxBulletin, getExpectedFieldCount(1));

		FieldSpec fieldSpec = fxBulletin.findFieldSpecByTag(DROPDOWN_FIELD_TAG);
		verifyDropDownFieldSpecCreatedFromXFormsData(fieldSpec);
		verifyFieldCreatedFromXFormsData(fxBulletin.getField(fieldSpec));
	}

	private void verifyFieldSpecCount(FxBulletin fxBulletin, int expectedFieldSpecCount)
	{
		assertEquals("Incorrect field spec count?", expectedFieldSpecCount, fxBulletin.getFieldSpecs().size());
	}

	private void verifyDropDownFieldSpecCreatedFromXFormsData(FieldSpec fieldSpec)
	{
		assertTrue("Only field should be dropdown?", fieldSpec.getType().isDropdown());
		
		DropDownFieldSpec dropDownFieldSpec = (DropDownFieldSpec) fieldSpec;
		assertEquals("Incorrect drop down field label?", DROPDOWN_FIELD_LABEL, dropDownFieldSpec.getLabel());
		assertEquals("Incorrect drop down field tag?", DROPDOWN_FIELD_TAG, dropDownFieldSpec.getTag());
		List<ChoiceItem> expectedChoiceItems = getExpectedChoiceItems();
		List<ChoiceItem> actualChoiceItems = dropDownFieldSpec.getChoiceItemList();
		assertEquals("Incorrect choiceItem count", expectedChoiceItems.size(), actualChoiceItems.size());
		assertTrue("Incorrect choice items found in list?", expectedChoiceItems.containsAll(actualChoiceItems));
	}

	private void verifyFieldCreatedFromXFormsData(FxBulletinField field)
	{
		Vector<ObservableChoiceItemList> choiceItems = field.getChoiceItemLists();
		assertEquals("Incorrect number of choiceItems?", 1, choiceItems.size());
		assertEquals("Incorrect choice?", DROPDOWN_FIELD_CHOICE_PERSONAL_INTERVIEW_CODE, field.getValue());
	}
	
	private List<ChoiceItem> getExpectedChoiceItems()
	{
		List<ChoiceItem> expectedChoiceItems = new ArrayList<ChoiceItem>();
		expectedChoiceItems.add(new ChoiceItem(DROPDOWN_FIELD_CHOICE_LEGAL_REPORT_CODE, DROPDOWN_FIELD_CHOICE_LEGAL_REPORT_LABEL));
		expectedChoiceItems.add(new ChoiceItem(DROPDOWN_FIELD_CHOICE_MEDIA_PRESS_CODE, DROPDOWN_FIELD_CHOICE_MEDIA_PRESS_LABEL));
		expectedChoiceItems.add(new ChoiceItem(DROPDOWN_FIELD_CHOICE_PERSONAL_INTERVIEW_CODE, DROPDOWN_FIELD_CHOICE_PERSONAL_INTERVIEW_LABEL));
		
		return expectedChoiceItems;
	}
	
	public void testFxBulletinWithXFormsWithDateField() throws Exception
	{
		FxBulletin fxBulletin = new FxBulletin(getLocalization());

		assertEquals("FxBulletin field specs should be filled?", 0, fxBulletin.getFieldSpecs().size());
		Bulletin bulletin = new Bulletin(security);
		bulletin.getFieldDataPacket().setXFormsModelAsString(getXFormsModelWithDateInputField());
		bulletin.getFieldDataPacket().setXFormsInstanceAsString(getXFormsInstanceWithDateField());
		fxBulletin.copyDataFromBulletin(bulletin, store);
		assertEquals("FxBulletin filled from bulletin with data should have date field?", getExpectedFieldCount(1), fxBulletin.getFieldSpecs().size());
		
		FieldSpec fieldSpec = fxBulletin.findFieldSpecByTag("date");
		assertTrue("Incorrect field type?", fieldSpec.getType().isDate());
		
		FxBulletinField dateField = fxBulletin.getField(fieldSpec);
		assertEquals("Incorrect date?", DATE_VALUE, dateField.getValue());
	}
	
	public void testFxBulletinWithXFormsBooleanField() throws Exception
	{		
		verifyBooleanFieldConversion(getXFormsInstanceWithSingleItemChoiceListAsTrueBoolean(), FieldSpec.TRUESTRING);
		verifyBooleanFieldConversion(getXFormsInstanceWithSingleItemChoiceListAsFalseBoolean(), FieldSpec.FALSESTRING);
		verifyBooleanFieldConversion(getXFormsInstanceWithSingleItemChoiceListAsNoValueBoolean(), FieldSpec.FALSESTRING);
	}

	private void verifyBooleanFieldConversion(String xFormsInstance, String expectedBooleanValue) throws Exception
	{
		FxBulletin fxBulletin = new FxBulletin(getLocalization());
		assertEquals("FxBulletin field specs should be filled?", 0, fxBulletin.getFieldSpecs().size());
		Bulletin bulletin = new Bulletin(security);
		bulletin.getFieldDataPacket().setXFormsModelAsString(getXFormsModelWithSingleItemChoiceListAsBoolean());
		bulletin.getFieldDataPacket().setXFormsInstanceAsString(xFormsInstance);
		fxBulletin.copyDataFromBulletin(bulletin, store);
		assertEquals("FxBulletin filled from bulletin with data should have date field?", getExpectedFieldCount(1), fxBulletin.getFieldSpecs().size());
		
		FieldSpec fieldSpec = fxBulletin.findFieldSpecByTag("anonymous");
		assertTrue("Incorrect field type?", fieldSpec.getType().isBoolean());
		
		FxBulletinField dateField = fxBulletin.getField(fieldSpec);
		assertEquals("Incorrect date?", expectedBooleanValue, dateField.getValue());
	}

	private int getExpectedFieldCount(int expectedFieldsConverted)
	{
		final int TOP_SECTION_DEFAULT_FIELD_COUNT = StandardFieldSpecs.getDefaultTopSectionFieldSpecs().size();
		final int TOTAL_SECTION_FIELDS = 1;
		
		return TOP_SECTION_DEFAULT_FIELD_COUNT + TOTAL_SECTION_FIELDS + expectedFieldsConverted;
	}
	
	public void testFxBulletinWithXFormsRepeatField() throws Exception
	{
		FxBulletin fxBulletin = new FxBulletin(getLocalization());
		assertEquals("FxBulletin field specs should be filled?", 0, fxBulletin.getFieldSpecs().size());
		Bulletin bulletin = new Bulletin(security);
		bulletin.getFieldDataPacket().setXFormsModelAsString(getXFormsModelWithRepeats());
		bulletin.getFieldDataPacket().setXFormsInstanceAsString(getXFormsInstanceWithRepeats());
		fxBulletin.copyDataFromBulletin(bulletin, store);
		Vector<FieldSpec> fieldSpecs = fxBulletin.getFieldSpecs();
		assertEquals("FxBulletin filled from bulletin with data should have grid field?", getExpectedFieldCount(1), fieldSpecs.size());
		
		FieldSpec fieldSpec = fxBulletin.findFieldSpecByTag("/nm/victim_information");
		verifyGridFieldSpec(fieldSpec);
		verifyGridFieldData(fxBulletin, fieldSpec);
	}
	
	public void testOwnershipOfXFormsRecordWhenCreatingACopy() throws Exception
	{
		MockMartusSecurity secureAppAccount = MockMartusSecurity.createOtherClient();
		MockMartusSecurity martusDesktopAccount = MockMartusSecurity.createHQ();
		store.setSignatureGenerator(martusDesktopAccount);	

		String secureAppPublicKey = secureAppAccount.getPublicKeyString();
		String martusDesktopPublicKey = martusDesktopAccount.getPublicKeyString();
		assertNotEquals(secureAppPublicKey, martusDesktopPublicKey);
		
		Bulletin secureAppBulletin = new Bulletin(secureAppAccount);
		secureAppBulletin.getFieldDataPacket().setXFormsModelAsString(getXFormsModelWithRepeats());
		secureAppBulletin.getFieldDataPacket().setXFormsInstanceAsString(getXFormsInstanceWithRepeats());

		HeadquartersKey desktopKey = new HeadquartersKey(martusDesktopAccount.getPublicKeyString());
		HeadquartersKeys keys = new HeadquartersKeys(desktopKey);
		secureAppBulletin.setAuthorizedToReadKeys(keys);
		store.saveBulletinForTesting(secureAppBulletin);
		UniversalId 	secureAppId = secureAppBulletin.getUniversalId();
		
		FxBulletin desktopFxBulletin = new FxBulletin(localization);
		
		assertEquals("Not secureApp Public Key?", secureAppPublicKey, secureAppBulletin.getAccount());
		desktopFxBulletin.copyDataFromBulletin(secureAppBulletin, store);
		ReadOnlyObjectWrapper<UniversalId> universalIdProperty = desktopFxBulletin.universalIdProperty();
		assertEquals("UniversalId not change after we copied data from the bulletin", secureAppId, universalIdProperty.get());
		assertEquals("Now should be Public Key after copying data.", secureAppPublicKey, secureAppBulletin.getAccount());

		Bulletin desktopEditedBulletin = new Bulletin(martusDesktopAccount);
		desktopFxBulletin.copyDataToBulletin(desktopEditedBulletin);
		assertEquals("After editing desktop should own this bulletin", martusDesktopPublicKey, desktopEditedBulletin.getAccount());
		
		store.setSignatureGenerator(security);
	}

	private void verifyGridFieldData(FxBulletin fxBulletin, FieldSpec fieldSpec) throws Exception
	{
		FxBulletinGridField fxBulletinGridField = (FxBulletinGridField) fxBulletin.getField(fieldSpec);
		GridData gridData = new GridData(fxBulletinGridField.getGridFieldSpec(), fxBulletin.getAllReusableChoicesLists());
		gridData.setFromXml(fxBulletinGridField.getValue());
		assertEquals("Incorrect grid row count?", 2, gridData.getRowCount());
	
		GridRow firstRow = gridData.getRow(0);
		assertEquals("incorrect grid column value", "John", firstRow.getCellText(0));
		assertEquals("incorrect grid column value", "Smith", firstRow.getCellText(1));
		assertEquals("incorrect grid column value", "male", firstRow.getCellText(2));
		
		GridRow secondRow = gridData.getRow(1);
		assertEquals("incorrect grid column value", "Sunny", secondRow.getCellText(0));
		assertEquals("incorrect grid column value", "Dale", secondRow.getCellText(1));
		assertEquals("incorrect grid column value", "other", secondRow.getCellText(2));
	}

	private void verifyGridFieldSpec(FieldSpec fieldSpec)
	{
		assertTrue("Incorrect field type?", fieldSpec.getType().isGrid());
		GridFieldSpec gridFieldSpec = (GridFieldSpec) fieldSpec;
		assertEquals("incorrect grid column count?", 3, gridFieldSpec.getColumnCount());
		assertEquals("incorrect fieldType?", new FieldTypeNormal(), gridFieldSpec.getColumnType(0));
		assertEquals("incorrect fieldType?", new FieldTypeNormal(), gridFieldSpec.getColumnType(1));
		assertEquals("incorrect fieldType?", new FieldTypeDropdown(), gridFieldSpec.getColumnType(2));
	}

	
	private static String getXFormsModelWithOnStringInputFieldXmlAsString()
	{
		return 	"		<xforms_model>" +
				"			<h:html xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns=\"http://www.w3.org/2002/xforms\" xmlns:jr=\"http://openrosa.org/javarosa\" xmlns:h=\"http://www.w3.org/1999/xhtml\" xmlns:ev=\"http://www.w3.org/2001/xml-events\" >" +
				"				<h:head>" +
				"				<h:title>XForms Sample</h:title>" +
				"					<model>" +
				"					<instance>" +
				"						<nm id=\"SampleForUnitTesting\" >" +
				"							<name/>" +
				"						</nm>" +
				"		            </instance>" +
				"		            <bind nodeset=\"/nm/name\" type=\"string\" />" +
				"		        </model>" +
				"		    </h:head>" +
				"		    <h:body>" +
				"		            <input ref=\"name\" >" +
				"		                <label>" + FIELD_LABEL +  "</label>" +
				"		                <hint>(required)</hint>" +
				"		            </input>" +
				"		    </h:body>" +
				"		</h:html>" +
				"	</xforms_model>";
	}
	
	private static String getXFormsInstanceXmlAsString()
	{
		return "<xforms_instance>" +
				   "<nm id=\"SampleForUnitTesting\">" +
				      "<name>" + FIELD_VALUE + "</name>" +
				   "</nm>" +
				"</xforms_instance>";
	}
	
	private static String getXFormsModelWithOnChoiceInputFieldXmlAsString()
	{
		return 	"		<xforms_model>" +
				"			<h:html xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns=\"http://www.w3.org/2002/xforms\" xmlns:jr=\"http://openrosa.org/javarosa\" xmlns:h=\"http://www.w3.org/1999/xhtml\" xmlns:ev=\"http://www.w3.org/2001/xml-events\" >" +
				"				<h:head>" +
				"				<h:title>XForms Sample</h:title>" +
				"					<model>" +
				"					<instance>" +
				"						<nm id=\"SampleForUnitTesting\" >" +
				" 							<" + DROPDOWN_FIELD_TAG +"/>"+			
				"						</nm>" +
				"		            </instance>" +
				" 					<bind nodeset=\"/nm/"+ DROPDOWN_FIELD_TAG + "\" type=\"select1\" ></bind>" +
				"		        </model>" +
				"		    </h:head>" +
				"		    <h:body>" +				
				" 				<select1 ref=\""+ DROPDOWN_FIELD_TAG + "\" appearance=\"minimal\" >" +
				"				<label>" + DROPDOWN_FIELD_LABEL + "</label>" +
				"					 <item>" +
				"						 <label>" + DROPDOWN_FIELD_CHOICE_MEDIA_PRESS_LABEL + "</label>" +
				"						 <value>" + DROPDOWN_FIELD_CHOICE_MEDIA_PRESS_CODE + "</value>" +
				" 					</item>" +
				" 					<item>" +
				" 						<label>" + DROPDOWN_FIELD_CHOICE_LEGAL_REPORT_LABEL + "</label>" +
				" 						<value>" + DROPDOWN_FIELD_CHOICE_LEGAL_REPORT_CODE + "</value>" +
				" 					</item>" +
				" 					<item>" +
				" 						<label>" + DROPDOWN_FIELD_CHOICE_PERSONAL_INTERVIEW_LABEL + "</label>" +
				" 						<value>" + DROPDOWN_FIELD_CHOICE_PERSONAL_INTERVIEW_CODE + "</value>" +
				" 					</item>" +
				" 				</select1>" +
				"		    </h:body>" +
				"		</h:html>" +
				"	</xforms_model>";
	}
	
	private static String getXFormsInstanceWithChoiceAnswers()
	{
		return "<xforms_instance>" +
				   "<nm id=\"SampleForUnitTesting\">" +
				      "<sourceOfRecordInformation>" + DROPDOWN_FIELD_CHOICE_PERSONAL_INTERVIEW_CODE + "</sourceOfRecordInformation>" +
				   "</nm>" +
				"</xforms_instance>";
	}
	
	private static String getXFormsModelWithDateInputField()
	{
		return 
		"<h:html xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns=\"http://www.w3.org/2002/xforms\" xmlns:jr=\"http://openrosa.org/javarosa\" xmlns:h=\"http://www.w3.org/1999/xhtml\" xmlns:ev=\"http://www.w3.org/2001/xml-events\" >" +
			"<h:head>" +
				"<h:title>secureApp Prototype</h:title>" +
				"<model>" +
					"<instance>" +
						"<nm id=\"VitalVoices\" >" +
							"<date></date>" +
							"</nm>" +
					"</instance>" +
				"<bind jr:constraintMsg=\"No dates before 2000-01-01 allowed\" nodeset=\"/nm/date\" constraint=\". >= date('2000-01-01')\" type=\"date\" ></bind>" +
				"</model>" +
			"</h:head>" +
			"<h:body>" +
					"<input ref=\"date\" >" +
						"<label>Date of incident</label>" +
						"<hint>(No dates before 2000-01-01 allowed)</hint>" +
					"</input>" +
			"</h:body>" +
		"</h:html>" ;
	}
	
	private static String getXFormsInstanceWithDateField()
	{
		return 
				"<nm id=\"VitalVoices\" >" +
				"<date>" + DATE_VALUE + "</date>" +
				"</nm>";
	}
	
	private static String getXFormsModelWithSingleItemChoiceListAsBoolean()
	{
		return "<h:html xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns=\"http://www.w3.org/2002/xforms\" xmlns:jr=\"http://openrosa.org/javarosa\" xmlns:h=\"http://www.w3.org/1999/xhtml\" xmlns:ev=\"http://www.w3.org/2001/xml-events\" >" +
			    "<h:head>" +
			        "<h:title>secureApp Prototype</h:title>" +
			        "<model>" +
			            "<instance>" +
			                "<nm id=\"VitalVoices2\" >" +
			                    "<anonymous></anonymous>" +
			                "</nm>" +
			            "</instance>" +
			            "<bind nodeset=\"/nm/anonymous\" type=\"select\" ></bind>" +
			        "</model>" +
			    "</h:head>" +
			    "<h:body>" +
			        "<group appearance=\"field-list\" >" +
			            "<label>Section 4 (Check boxes)</label>" +
			            "<select ref=\"anonymous\" >" +
			                "<label>Does interviewee wish to remain anonymous?</label>" +
			                "<item>" +
			                    "<label></label>" +
			                    "<value>1</value>" +
			                "</item>" +
			            "</select>" +
			        "</group>" +
			    "</h:body>" +
			"</h:html>";
	}
	
	private static String getXFormsInstanceWithSingleItemChoiceListAsTrueBoolean()
	{
		return "<nm id=\"VitalVoices2\" ><anonymous>1</anonymous></nm>";
	}
	
	private static String getXFormsInstanceWithSingleItemChoiceListAsFalseBoolean()
	{
		return "<nm id=\"VitalVoices2\" ><anonymous>0</anonymous></nm>";
	}
	
	private static String getXFormsInstanceWithSingleItemChoiceListAsNoValueBoolean()
	{
		return "<nm id=\"VitalVoices2\" ><anonymous/></nm>";
	}
	
	private static String getXFormsInstanceWithRepeats()
	{
		return 	"<nm id=\"VitalVoices\" >" +
				"<victim_information>" +
				"<victimFirstName>John</victimFirstName>" +
				"<victimLastName>Smith</victimLastName>" +
				"<sex>male</sex>" +
				"</victim_information>" +
				"<victim_information>" +
				"<victimFirstName>Sunny</victimFirstName>" +
				"<victimLastName>Dale</victimLastName>" +
				"<sex>other</sex>" +
				"</victim_information>" +
				"</nm>";
	}
	
	private static String getXFormsModelWithRepeats()
	{
		return	"<h:html xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns=\"http://www.w3.org/2002/xforms\" xmlns:jr=\"http://openrosa.org/javarosa\" xmlns:h=\"http://www.w3.org/1999/xhtml\" xmlns:ev=\"http://www.w3.org/2001/xml-events\" >" +
			    "<h:head>" +
			        "<h:title>secureApp Prototype</h:title>" +
			        "<model>" +
			            "<instance>" +
			                "<nm id=\"VitalVoices\" >" +
			                    "<victim_information>" +
			                        "<victimFirstName></victimFirstName>" +
			                        "<victimLastName></victimLastName>" +
			                        "<sex></sex>" +
			                    "</victim_information>" +
			                "</nm>" +
			            "</instance>" +
			           	"<bind nodeset=\"/nm/victim_information/victimFirstName\" type=\"string\" ></bind>" +
			            "<bind nodeset=\"/nm/victim_information/victimLastName\" type=\"string\" ></bind>" +
			            "<bind nodeset=\"/nm/victim_information/sex\" type=\"select1\" ></bind>" +
			        "</model>" +
			    "</h:head>" +
			    "<h:body>" +
			            "<repeat nodeset=\"/nm/victim_information\" >" +
			                "<input ref=\"victimFirstName\" >" +
			                    "<label>Victim first name</label>" +
			                "</input>" +
			                "<input ref=\"victimLastName\" >" +
			                    "<label>Victim last name</label>" +
			                "</input>" +
			                "<select1 ref=\"sex\" appearance=\"minimal\" >" +
			                    "<label>Victim Sex</label>" +
			                    "<item>" +
			                        "<label>Female</label>" +
			                        "<value>female</value>" +
			                    "</item>" +
			                    "<item>" +
			                        "<label>Male</label>" +
			                        "<value>male</value>" +
			                    "</item>" +
			                    "<item>" +
			                        "<label>Other</label>" +
			                        "<value>other</value>" +
			                    "</item>" +
			                "</select1>" +
			            "</repeat>" +
			       
			    "</h:body>" +
			"</h:html>";
	}
	
	private static final String DROPDOWN_FIELD_TAG = "sourceOfRecordInformation";
	private static final String DROPDOWN_FIELD_LABEL = "Source of record information";
	private static final String DROPDOWN_FIELD_CHOICE_MEDIA_PRESS_CODE = "mediaPressCode";
	private static final String DROPDOWN_FIELD_CHOICE_LEGAL_REPORT_CODE = "legalReportCode";
	private static final String DROPDOWN_FIELD_CHOICE_PERSONAL_INTERVIEW_CODE = "personalInterviewCode";
	
	private static final String DROPDOWN_FIELD_CHOICE_MEDIA_PRESS_LABEL = "Media Press";
	private static final String DROPDOWN_FIELD_CHOICE_LEGAL_REPORT_LABEL = "Legal Report";
	private static final String DROPDOWN_FIELD_CHOICE_PERSONAL_INTERVIEW_LABEL = "Personal Interview";
	
	private static final String DATE_VALUE = "2015-03-24";
	
	private static final String FIELD_LABEL = "What is your name?";
	private static final String FIELD_VALUE = "John Johnson";
	
	private MockMartusSecurity security;
	private MiniLocalization localization;
	private MockBulletinStore store;
}
