/*

The Martus(tm) free, social justice documentation and
monitoring software. Copyright (C) 2001-2004, Beneficent
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

import java.awt.Color;
import java.awt.ComponentOrientation;
import java.awt.Font;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;
import javax.swing.border.LineBorder;

import org.martus.client.swingui.UiLocalization;
import org.martus.client.swingui.UiMainWindow;
import org.martus.client.swingui.UiWarningLabel;
import org.martus.client.swingui.fields.UiField;
import org.martus.common.FieldSpec;
import org.martus.swing.ParagraphLayout;

abstract public class UiBulletinComponentSection extends JPanel
{
	UiBulletinComponentSection(UiMainWindow mainWindowToUse)
	{
		mainWindow = mainWindowToUse;

		setBorder(new EtchedBorder());

		ParagraphLayout layout = new ParagraphLayout();
		layout.outdentFirstField();
		setLayout(layout);

		sectionHeading = new JLabel("", null, JLabel.LEFT);
		sectionHeading.setVerticalTextPosition(JLabel.TOP);
		sectionHeading.setFont(sectionHeading.getFont().deriveFont(Font.BOLD));
		add(sectionHeading);
		
		warningIndicator = new UiWarningLabel();
		add(warningIndicator);
		clearWarningIndicator();
	}
	
	public UiMainWindow getMainWindow()
	{
		return mainWindow;
	}
	
	public UiLocalization getLocalization()
	{
		return getMainWindow().getLocalization();
	}

	void updateSectionBorder(boolean isEncrypted)
	{
		Color color = Color.lightGray;
		if(isEncrypted)
			color = Color.red;
		setBorder(new LineBorder(color, 5));
	}

	public void clearWarningIndicator()
	{
		warningIndicator.setVisible(false);
	}

	public void updateWarningIndicator(String text)
	{
		warningIndicator.setText(text);
		warningIndicator.setVisible(true);
	}

	ParagraphLayout getParagraphLayout()
	{
		return (ParagraphLayout)getLayout();
	}

	int getFirstColumnWidth()
	{
		return getParagraphLayout().getFirstColumnMaxWidth(this);
	}

	void matchFirstColumnWidth(UiBulletinComponentSection otherSection)
	{
		int thisWidth = getFirstColumnWidth();
		int otherWidth = otherSection.getFirstColumnWidth();
		if(otherWidth > thisWidth)
			getParagraphLayout().setFirstColumnWidth(otherWidth);
	}

	protected void setSectionIconAndTitle(String iconFileName, String title)
	{
		Icon icon = new ImageIcon(UiBulletinComponentSection.class.getResource(iconFileName));
		sectionHeading.setIcon(icon);
		sectionHeading.setText(title);
	}

	public void addComponents(JComponent item1, JComponent item2)
	{
		if(mainWindow.getLocalization().getComponentOrientation().equals(ComponentOrientation.LEFT_TO_RIGHT))
		{
			add(item1, ParagraphLayout.NEW_PARAGRAPH);
			add(item2);
		}
		else
		{
			if(!item2.isVisible())
				add(new JLabel(""),ParagraphLayout.NEW_PARAGRAPH);
			else
				add(item2, ParagraphLayout.NEW_PARAGRAPH);
			add(item1);
		}
	}


	protected UiMainWindow mainWindow;
	JLabel sectionHeading;
	JLabel warningIndicator;
	UiField[] fields;
	FieldSpec[] fieldSpecs;
}
