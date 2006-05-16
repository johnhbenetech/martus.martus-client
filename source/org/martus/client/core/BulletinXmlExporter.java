/*

The Martus(tm) free, social justice documentation and
monitoring software. Copyright (C) 2001-2006, Beneficent
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

package org.martus.client.core;

import java.io.IOException;
import java.io.Writer;
import java.util.Vector;

import org.martus.common.MartusXml;
import org.martus.common.MiniLocalization;
import org.martus.common.bulletin.AttachmentProxy;
import org.martus.common.bulletin.Bulletin;
import org.martus.common.bulletin.BulletinXmlExportImportConstants;
import org.martus.common.field.MartusField;
import org.martus.common.fieldspec.FieldSpec;
import org.martus.common.packet.BulletinHistory;
import org.martus.util.xml.XmlUtilities;

public class BulletinXmlExporter
{
	public BulletinXmlExporter(MiniLocalization localizationToUse)
	{
		localization = localizationToUse;
	}
	
	public void exportBulletins(Writer dest, Vector bulletins, boolean includePrivateData, boolean includeAttachments)
		throws IOException
	{
		dest.write(MartusXml.getTagStartWithNewline(BulletinXmlExportImportConstants.MARTUS_BULLETINS));
		writeXMLVersion(dest);
		writeExportMetaData(dest, includePrivateData, includeAttachments);

		for (int i = 0; i < bulletins.size(); i++)
		{
			Bulletin b = (Bulletin)bulletins.get(i);
			exportOneBulletin(dest, b, includePrivateData, includeAttachments);
		}
		dest.write(MartusXml.getTagEnd(BulletinXmlExportImportConstants.MARTUS_BULLETINS));
	}
	
	private void writeXMLVersion(Writer dest) throws IOException
	{
		dest.write(getXmlEncodedTagWithData(BulletinXmlExportImportConstants.XML_EXPORT_VERSION,BulletinXmlExportImportConstants.XML_EXPORT_VERSION_NUMBER));
	}
	
	private void writeExportMetaData(Writer dest, boolean includePrivateData, boolean includeAttachments) throws IOException
	{
		dest.write(MartusXml.getTagStartWithNewline(BulletinXmlExportImportConstants.EXPORT_META_DATA));
		if(includePrivateData)
		{
			dest.write(getXmlEncodedTagWithData(BulletinXmlExportImportConstants.PUBLIC_AND_PRIVATE,""));
		}
		else
		{
			dest.write("<!--  No Private FieldSpecs or Data was exported  -->\n");
			dest.write(getXmlEncodedTagWithData(BulletinXmlExportImportConstants.PUBLIC_ONLY,""));
		}
		
		if(!includeAttachments)
			dest.write(getXmlEncodedTagWithData(BulletinXmlExportImportConstants.NO_ATTACHMENTS_EXPORTED,""));
		
		dest.write(MartusXml.getTagEnd(BulletinXmlExportImportConstants.EXPORT_META_DATA));
		dest.write(BulletinXmlExportImportConstants.NEW_LINE);
	}

	private void writeBulletinMetaData(Writer dest, Bulletin b) throws IOException
	{
		dest.write(MartusXml.getTagStartWithNewline(BulletinXmlExportImportConstants.BULLETIN_META_DATA));
		dest.write(getXmlEncodedTagWithData(BulletinXmlExportImportConstants.ACCOUNT_ID, b.getAccount()));
		dest.write(getXmlEncodedTagWithData(BulletinXmlExportImportConstants.LOCAL_ID, b.getLocalId()));
		dest.write(getXmlEncodedTagWithData(BulletinXmlExportImportConstants.BULLETIN_LAST_SAVED_DATE_TIME, localization.formatDateTime(b.getLastSavedTime())));
		if(b.isAllPrivate())
			dest.write(getXmlEncodedTagWithData(BulletinXmlExportImportConstants.ALL_PRIVATE, ""));
		writeBulletinStatus(dest, b);			
		writeBulletinHistory(dest, b);
		dest.write(MartusXml.getTagEnd(BulletinXmlExportImportConstants.BULLETIN_META_DATA));
	}

	private void writeBulletinHistory(Writer dest, Bulletin b) throws IOException
	{
		dest.write(getXmlEncodedTagWithData(BulletinXmlExportImportConstants.BULLETIN_VERSION,Integer.toString(b.getVersion())));
		BulletinHistory history = b.getHistory();
		if(history.size() > 0)
		{
			dest.write(MartusXml.getTagStartWithNewline(BulletinXmlExportImportConstants.HISTORY));
			for(int i=0; i < history.size(); ++i)
			{
				dest.write(getXmlEncodedTagWithData(BulletinXmlExportImportConstants.ANCESTOR, history.get(i)));
			}
			dest.write(MartusXml.getTagEnd(BulletinXmlExportImportConstants.HISTORY));
		}
	}

	private void writeBulletinStatus(Writer dest, Bulletin b) throws IOException
	{
		String status = localization.getStatusLabel("draft");
		if(b.isSealed())
			status = localization.getStatusLabel("sealed");
		dest.write(getXmlEncodedTagWithData(BulletinXmlExportImportConstants.BULLETIN_STATUS, status));
	}
	
	private void writeBulletinFieldSpecs(Writer dest, Bulletin b, boolean includePrivateData) throws IOException
	{
		if(shouldIncludeTopSection(b, includePrivateData))
			writeFieldSpecs(dest, b.getTopSectionFieldSpecs(), BulletinXmlExportImportConstants.MAIN_FIELD_SPECS);
		if(includePrivateData)
			writeFieldSpecs(dest, b.getBottomSectionFieldSpecs(), BulletinXmlExportImportConstants.PRIVATE_FIELD_SPECS);
	}

	private boolean shouldIncludeTopSection(Bulletin b, boolean includePrivateData)
	{
		return includePrivateData || !b.isAllPrivate();
	}

	public void writeFieldSpecs(Writer dest, FieldSpec[] specs, String xmlTag) throws IOException
	{
		dest.write(MartusXml.getTagStartWithNewline(xmlTag));
		for(int i = 0; i < specs.length; i++)
		{
			dest.write(specs[i].toXml(BulletinXmlExportImportConstants.FIELD));
		}
		dest.write(MartusXml.getTagEnd(xmlTag));
		dest.write(BulletinXmlExportImportConstants.NEW_LINE);
	}

	private void exportOneBulletin(Writer dest, Bulletin b, boolean includePrivateData, boolean includeAttachments) throws IOException
	{
		dest.write(MartusXml.getTagStartWithNewline(BulletinXmlExportImportConstants.BULLETIN));

		writeBulletinMetaData(dest, b);
		writeBulletinFieldSpecs(dest, b, includePrivateData);

		dest.write(MartusXml.getTagStartWithNewline(BulletinXmlExportImportConstants.FIELD_VALUES));
		if(shouldIncludeTopSection(b, includePrivateData))
		{
			writeFields(dest, b, b.getTopSectionFieldSpecs());
			if(includeAttachments)
				writeAttachments(dest, b.getPublicAttachments(), BulletinXmlExportImportConstants.TOP_SECTION_ATTACHMENT_LIST);
		}

		if(includePrivateData)
		{
			writeFields(dest, b, b.getBottomSectionFieldSpecs());
			if(includeAttachments)
				writeAttachments(dest, b.getPrivateAttachments(), BulletinXmlExportImportConstants.BOTTOM_SECTION_ATTACHMENT_LIST);
		}
		dest.write(MartusXml.getTagEnd(BulletinXmlExportImportConstants.FIELD_VALUES));

		dest.write(MartusXml.getTagEnd(BulletinXmlExportImportConstants.BULLETIN));
		dest.write(BulletinXmlExportImportConstants.NEW_LINE);
	}

	private void writeAttachments(Writer dest, AttachmentProxy[] attachments, String attachmentSectionTag)
		throws IOException
	{
		if(attachments.length == 0)
			return;

		dest.write(MartusXml.getTagStartWithNewline(attachmentSectionTag));
		for (int i = 0; i < attachments.length; i++)
		{
			AttachmentProxy proxy = attachments[i];
			writeElement(dest, "", BulletinXmlExportImportConstants.ATTACHMENT, "", proxy.getLabel());
		}
		dest.write(MartusXml.getTagEnd(attachmentSectionTag));
	}

	private void writeFields(Writer dest, Bulletin b, FieldSpec[] specs)
		throws IOException
	{
		for (int i = 0; i < specs.length; i++)
		{
			FieldSpec spec = specs[i];
			if(spec.hasUnknownStuff())
				continue;		
			final String tag = spec.getTag();
			MartusField field = b.getField(tag);
			String value = field.getExportableData(localization);
			if(spec.getType().isGrid())
			{
				String valueTagAndData = MartusXml.getTagWithData(BulletinXmlExportImportConstants.VALUE, value);
				writeElementDirect(dest, tag, valueTagAndData);
			}
			else
			{
				writeElement(dest, tag, value);
			}
		}
		
	}
	
	private static String getXmlEncodedTagWithData(String tagName, String data)
	{
		return MartusXml.getTagWithData(tagName, XmlUtilities.getXmlEncoded(data));
	}
	
	private static void writeElement(Writer dest, String tag, String fieldData) throws IOException
	{
		String xmlFieldTagWithData = getXmlEncodedTagWithData(BulletinXmlExportImportConstants.VALUE, fieldData);
		writeElementDirect(dest, tag, xmlFieldTagWithData);
	}

	private static void writeElementDirect(Writer dest, String tag, String xmlFieldData) throws IOException
	{
		dest.write(MartusXml.getTagStartWithNewline("Field "+BulletinXmlExportImportConstants.TAG_ATTRIBUTE+"='"+tag+"'"));
		dest.write(xmlFieldData);
		dest.write(MartusXml.getTagEnd(MartusXml.tagField));		
	}

	private static void writeElement(Writer dest, String fieldType, String tag, String rawLabel, String rawFieldData) throws IOException
	{	
		String xmlFieldTypeAndValue = "";
		String xmlTagAndValue = "";
		String xmlLabelAndValue = "";
		String xmlValueAndFieldData = "";
		
		if(fieldType.length() > 0)
			xmlFieldTypeAndValue = getXmlEncodedTagWithData(BulletinXmlExportImportConstants.TYPE, fieldType);

		xmlTagAndValue = getXmlEncodedTagWithData(BulletinXmlExportImportConstants.TAG, tag);

		if (rawLabel.length() > 0)
			xmlLabelAndValue = getXmlEncodedTagWithData(BulletinXmlExportImportConstants.LABEL, rawLabel);
		
		if (rawFieldData.length() > 0)
			xmlValueAndFieldData = getXmlEncodedTagWithData(BulletinXmlExportImportConstants.VALUE, rawFieldData);
		writeElementDirect(dest, xmlFieldTypeAndValue, xmlTagAndValue, xmlLabelAndValue, xmlValueAndFieldData);		
	}	
	
	private static void writeElementDirect(Writer dest, String xmlEncodeType, String xmlEncodedTag, String xmlEncodedLabel, String xmlEncodedFieldData) throws IOException
	{						
		dest.write(MartusXml.getTagStartWithNewline("Field"));
		dest.write(xmlEncodeType);
		dest.write(xmlEncodedTag);
		dest.write(xmlEncodedLabel);
		dest.write(xmlEncodedFieldData);
		dest.write(MartusXml.getTagEnd(MartusXml.tagField));		
	}
	
	MiniLocalization localization;
}
