/*

The Martus(tm) free, social justice documentation and
monitoring software. Copyright (C) 2001-2003, Beneficent
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

package org.martus.client.swingui.bulletincomponent;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;

import org.martus.client.swingui.UiMainWindow;
import org.martus.client.swingui.fields.UiBoolEditor;
import org.martus.client.swingui.fields.UiDateEditor;
import org.martus.client.swingui.fields.UiField;
import org.martus.client.swingui.fields.UiFlexiDateEditor;
import org.martus.common.bulletin.AttachmentProxy;
import org.martus.common.bulletin.Bulletin;
import org.martus.common.crypto.MartusCrypto;
import org.martus.common.utilities.MartusFlexidate;

public class UiBulletinEditor extends UiBulletinComponent
{
	public UiBulletinEditor(UiMainWindow mainWindowToUse)
	{
		super(mainWindowToUse);
		owner = mainWindowToUse;
		isEditable = true;
		// ensure that attachmentEditor gets initialized
	}

	public UiBulletinComponentSection createBulletinComponentSection(boolean encrypted)
	{
		return new UiBulletinComponentEditorSection(this, owner, encrypted);
	}

	public class AttachmentMissing extends UiField.DataInvalidException
	{
		public AttachmentMissing(String localizedTag)
		{
			super(localizedTag);
		}
	}
	
	public void validateData() throws UiField.DataInvalidException 
	{
		for(int fieldNum = 0; fieldNum < fields.length; ++fieldNum)
		{
			try 
			{
				fields[fieldNum].validate();
			} 
			catch (UiDateEditor.DateFutureException e) 
			{
				throw new UiDateEditor.DateFutureException(owner.getLocalization().getFieldLabel(fieldTags[fieldNum]));
			}
		}
		
		validateAttachments((UiBulletinComponentEditorSection)publicStuff);
		validateAttachments((UiBulletinComponentEditorSection)privateStuff);
	}
	
	private void validateAttachments(UiBulletinComponentEditorSection section) throws AttachmentMissing
	{
		AttachmentProxy[] publicAttachments = section.attachmentEditor.getAttachments();
		for(int aIndex = 0; aIndex < publicAttachments.length; ++aIndex)
		{
			File file = publicAttachments[aIndex].getFile();
			if (file != null)
			{
				if(!file.exists())
					throw new AttachmentMissing(file.getAbsolutePath());
			}
		}
	}

	public boolean isBulletinModified() throws
			IOException,
			MartusCrypto.EncryptionException
	{		
		
		Bulletin tempBulletin = new Bulletin(owner.getApp().getSecurity());					
		copyDataToBulletin(tempBulletin);
			
		String currentFieldText = null;
			
		for(int fieldNum = 0; fieldNum < fields.length; ++fieldNum)
		{			
			String fieldTag = fieldTags[fieldNum];			
			String oldFieldText = currentBulletin.get(fieldTag);
			
			if (fields[fieldNum] instanceof UiFlexiDateEditor)							
				currentFieldText = getBulletinFlexidateFormat(oldFieldText);					
			else
				currentFieldText = oldFieldText;		
																								
			if (!currentFieldText.equals(tempBulletin.get(fieldTag)))
			{									
				return true;
			}																
		}		
				
		if (isPublicAttachmentModified())	
			return true;						
		
		if (isPrivateAttachmentModified())
			return true;
			
		return false;			
	}	
	
	
	private String getBulletinFlexidateFormat(String fieldValue)
	{
		MartusFlexidate martusFlexidate = MartusFlexidate.createFromMartusDateString(fieldValue);
		DateFormat df = Bulletin.getStoredDateFormat();	
		String storedDateFormat = df.format(martusFlexidate.getBeginDate());
		return storedDateFormat+MartusFlexidate.DATE_RANGE_SEPARATER+martusFlexidate.getMatusFlexidate();		
	}		
	
	private boolean isPublicAttachmentModified()
	{
		UiBulletinComponentEditorSection section = (UiBulletinComponentEditorSection)publicStuff;
		AttachmentProxy[] publicAttachments = section.attachmentEditor.getAttachments();
		AttachmentProxy[] currentAttachments = currentBulletin.getPublicAttachments();
		
		if (isAnyAttachmentModified(currentAttachments, publicAttachments))
			return true;
		return false;
	}

	private boolean isPrivateAttachmentModified()
	{
		UiBulletinComponentEditorSection section = (UiBulletinComponentEditorSection)privateStuff;
		AttachmentProxy[] currentAttachments = currentBulletin.getPrivateAttachments();
		AttachmentProxy[] privateAttachments = section.attachmentEditor.getAttachments();	
			
		if (isAnyAttachmentModified(currentAttachments, privateAttachments))
			return true;
		
		return false;
	}

	private boolean isAnyAttachmentModified(AttachmentProxy[] oldProxies, AttachmentProxy[] newProxies)
	{					
		if (oldProxies.length != newProxies.length)						
			return true;
		
		for(int aIndex = 0; aIndex < oldProxies.length; ++aIndex)
		{									
			String newLocalId = newProxies[aIndex].getUniversalId().getLocalId();
			String oldLocalId = oldProxies[aIndex].getUniversalId().getLocalId();			
						
			if (!newLocalId.equals(oldLocalId))
				return true;														
		}		
		return false;	
	}		

	public void copyDataToBulletin(Bulletin bulletin) throws
		IOException,
		MartusCrypto.EncryptionException
	{				
		bulletin.clear();
			
		boolean isAllPrivate = false;
		if(allPrivateField.getText().equals(UiField.TRUESTRING))
			isAllPrivate = true;
		bulletin.setAllPrivate(isAllPrivate);
		for(int fieldNum = 0; fieldNum < fields.length; ++fieldNum)
		{						
			bulletin.set(fieldTags[fieldNum], fields[fieldNum].getText());													
		}

		UiBulletinComponentEditorSection publicSection = (UiBulletinComponentEditorSection)publicStuff;
		AttachmentProxy[] publicAttachments = publicSection.attachmentEditor.getAttachments();
		for(int aIndex = 0; aIndex < publicAttachments.length; ++aIndex)
		{
			AttachmentProxy a = publicAttachments[aIndex];
			bulletin.addPublicAttachment(a);
		}

		UiBulletinComponentEditorSection privateSection = (UiBulletinComponentEditorSection)privateStuff;
		AttachmentProxy[] privateAttachments = privateSection.attachmentEditor.getAttachments();
		for(int aIndex = 0; aIndex < privateAttachments.length; ++aIndex)
		{
			AttachmentProxy a = privateAttachments[aIndex];
			bulletin.addPrivateAttachment(a);
		}

	}	

	public UiField createBoolField()
	{
		return new UiBoolEditor(this);
	}

	UiMainWindow owner;
}
