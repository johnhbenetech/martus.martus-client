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
package org.martus.client.swingui.jfx.landing.general;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

import org.martus.common.MiniLocalization;
import org.martus.common.bulletin.Bulletin;
import org.martus.common.packet.UniversalId;

public class ServerSyncTableRowData
{
	public ServerSyncTableRowData(Bulletin bulletin, Integer sizeOfBulletin, int locationOfBulletin, MiniLocalization localization)
	{
		uid = bulletin.getUniversalId();
		location = new SimpleStringProperty(getLocationString(locationOfBulletin, localization));
		title = new SimpleStringProperty(bulletin.get(Bulletin.TAGTITLE));
		author = new SimpleStringProperty(bulletin.get(Bulletin.TAGAUTHOR));
		long dateLastSaved = bulletin.getBulletinHeaderPacket().getLastSavedTime();
		dateSaved = new SimpleStringProperty(localization.formatDateTime(dateLastSaved));
		size = new SimpleIntegerProperty(sizeOfBulletin);
	}
	
	private String getLocationString(int locationOfBulletin, MiniLocalization localization)
	{
		if(locationOfBulletin == LOCATION_LOCAL)
			return localization.getFieldLabel("RecordLocationLocal");
		if(locationOfBulletin == LOCATION_SERVER)
			return localization.getFieldLabel("RecordLocationServer");
		if(locationOfBulletin == LOCATION_BOTH)
			return localization.getFieldLabel("RecordLocationBothLocalAndServer");
		return localization.getFieldLabel("RecordLocationUnknown");
	}

	public UniversalId getUniversalId()
	{
		return uid;
	}
	
	public String getTitle()
	{
		return title.get();
	}

    public SimpleStringProperty titleProperty() 
    { 
        return title; 
    }
	
	public String getAuthor()
	{
		return author.get();
	}

    public SimpleStringProperty authorProperty() 
    { 
        return author; 
    }

    public String getDateSaved()
	{
		return dateSaved.get();
	}
	
    public SimpleStringProperty dateSavedProperty() 
    { 
        return dateSaved; 
    }

	public String getLocation()
	{
		return location.getValue();
	}

	public SimpleStringProperty locationProperty()
	{
		return location;
	}

    public Integer getSize() 
    {
    		return size.getValue();
    }
 
    public SimpleIntegerProperty sizeProperty() 
    {
    		return size;
    }

    static public final int LOCATION_LOCAL = 0;
    static public final int LOCATION_SERVER = 1;
    static public final int LOCATION_BOTH = 2;
    
    static public final String LOCATION_PROPERTY_NAME = "location";
    static public final String TITLE_PROPERTY_NAME = "title";
    static public final String AUTHOR_PROPERTY_NAME = "author";
    static public final String DATE_SAVDED_PROPERTY_NAME = "dateSaved";
    static public final String SIZE_PROPERTY_NAME = "size";
    
	private final SimpleStringProperty location;
    private final SimpleStringProperty title;
	private final SimpleStringProperty author;
	private final SimpleStringProperty dateSaved;
	private final SimpleIntegerProperty size;
	
	private final UniversalId uid;
}