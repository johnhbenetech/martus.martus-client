/*

The Martus(tm) free, social justice documentation and
monitoring software. Copyright (C) 2001-2005, Beneficent
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

package org.martus.client.swingui.fields;

import java.util.Vector;
import javax.swing.JComponent;
import javax.swing.JLabel;

import org.martus.common.clientside.ChoiceItem;
import org.martus.swing.UiLabel;

public class UiChoiceViewer extends UiField
{
	public UiChoiceViewer(ChoiceItem[] choicesToUse)
	{
		initalize(choicesToUse);
	}

	public UiChoiceViewer(Vector choicesToUse)
	{
		ChoiceItem[] choicesArray = new ChoiceItem[choicesToUse.size()];
		for(int i = 0; i < choicesArray.length; i++)
		{
			String item = (String)choicesToUse.get(i);
			choicesArray[i] = new ChoiceItem(item,item);
		}
		initalize(choicesArray);
	}

	private void initalize(ChoiceItem[] choicesToUse)
	{
		choices = choicesToUse;
		widget = new UiLabel();
	}

	public JComponent getComponent()
	{
		return widget;
	}

	public String getText()
	{
		return "";
	}

	public void setText(String newText)
	{
		ChoiceItem item = choices[0];
		for(int i = 0; i < choices.length; ++i)
		{
			if(newText.equals(choices[i].getCode()))
			{
				item = choices[i];
				break;
			}
		}
		widget.setText(" " + item.toString() + " ");
	}

	JLabel widget;
	ChoiceItem[] choices;
}

