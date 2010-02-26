/*

The Martus(tm) free, social justice documentation and
monitoring software. Copyright (C) 2001-2007, Beneficent
Technology, Inc. (The Benetech Initiative).

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

package org.martus.client.search;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import org.martus.client.core.SafeReadableBulletin;
import org.martus.common.MiniLocalization;
import org.martus.common.field.MartusField;
import org.martus.common.fieldspec.FieldSpec;

public class BulletinSearcher
{
	public BulletinSearcher(SearchTreeNode nodeToMatch)
	{
		this(nodeToMatch, false);
	}
	
	public BulletinSearcher(SearchTreeNode nodeToMatch, boolean requireSameRowMatching)
	{
		node = nodeToMatch;
		sameRowsMode = requireSameRowMatching;
	}

	public boolean doesMatch(SafeReadableBulletin b, MiniLocalization localization)
	{
		return (getMatchResults(b, localization).doesMatch());
	}

	private MatchResults getMatchResults(SafeReadableBulletin b, MiniLocalization localization)
	{
		int op = node.getOperation();
		if(op == SearchTreeNode.VALUE)
			return getMatchResultsForValueNode(b, localization);

		BulletinSearcher left = new BulletinSearcher(node.getLeft(), sameRowsMode);
		BulletinSearcher right = new BulletinSearcher(node.getRight(), sameRowsMode);
		MatchResults leftResults = left.getMatchResults(b, localization);
		MatchResults rightResults = right.getMatchResults(b, localization);

		if(op == SearchTreeNode.AND)
			return MatchResults.and(leftResults, rightResults, sameRowsMode);

		if(op == SearchTreeNode.OR)
			return MatchResults.or(leftResults, rightResults, sameRowsMode);
		
		System.out.println("BulletinSearcher.doesMatch uknown operation: " + op);
		return MatchResults.noMatches;
	}
	
	private MatchResults getMatchResultsForValueNode(SafeReadableBulletin b, MiniLocalization localization)
	{
		String searchForValue = node.getValue();

		FieldSpec fieldToSearch = node.getField();
		String tagToSearch = fieldToSearch.getTag();
		if(tagToSearch.length() == 0)
			return new MatchResults(b.contains(searchForValue));

		MartusField field = b.getPossiblyNestedField(fieldToSearch);
		if(field == null)
			return MatchResults.noMatches;
		
		int compareOp = node.getComparisonOperator();
		String[] nestedTags = SafeReadableBulletin.parseNestedTags(fieldToSearch.getTag());
		MartusField topLevelField = b.getPossiblyNestedField(nestedTags[0]);
		if(topLevelField.getType().isGrid())
		{
			Integer[] matchingRows = field.getMatchingRows(compareOp, searchForValue, localization);
			return new MatchResults(topLevelField.getTag(), matchingRows);
		}
		
		return new MatchResults(field.doesMatch(compareOp, searchForValue, localization));
	}
	
	static class MatchResults
	{
		public MatchResults(boolean simpleDoesMatch)
		{
			doesMatch = simpleDoesMatch;
			gridTagToMatchingRows = new HashMap();
		}
		
		private MatchResults()
		{
			this(false);
		}
		
		public MatchResults(String gridTag, Integer[] matchingRows)
		{
			this();
			doesMatch = (matchingRows.length > 0);
			gridTagToMatchingRows.put(gridTag, matchingRows);
		}
		
		public MatchResults(boolean simpleDoesMatch, Map matchingGridTagsAndRows)
		{
			this();
			doesMatch = simpleDoesMatch;
			gridTagToMatchingRows.putAll(matchingGridTagsAndRows);
		}
		
		public boolean doesMatch()
		{
			return doesMatch;
		}
		
		public static MatchResults and(MatchResults left, MatchResults right, boolean sameRowsMode)
		{
			if(!left.doesMatch || !right.doesMatch)
				return noMatches;

			HashMap intersection = new HashMap();
			Iterator it = left.gridTagToMatchingRows.keySet().iterator();
			while(it.hasNext())
			{
				String tag = (String)it.next();
				Integer[] safeLeftRows = (Integer[])left.gridTagToMatchingRows.get(tag);
				if(safeLeftRows == null)
					safeLeftRows = new Integer[0];
				Vector leftRows = new Vector(Arrays.asList(safeLeftRows));
				Integer[] safeRightRows = (Integer[])right.gridTagToMatchingRows.get(tag);
				if(safeRightRows == null)
					safeRightRows = new Integer[0];
				Vector rightRows = new Vector(Arrays.asList(safeRightRows));
				
				Vector mergedRows = new Vector();
				for(int i = 0; i < leftRows.size(); ++i)
				{
					if(rightRows.contains(leftRows.get(i)))
						mergedRows.add(leftRows.get(i));
				}
				
				intersection.put(tag, mergedRows.toArray(new Integer[0]));
			}
			
			boolean newDoesMatch = false;
			if(sameRowsMode)
				newDoesMatch = allGridsHaveMatchingRows(intersection);
			else
				newDoesMatch = (left.doesMatch && right.doesMatch);
			return new MatchResults(newDoesMatch, intersection);
		}
		
		public static MatchResults or(MatchResults left, MatchResults right, boolean sameRowsMode)
		{
			if(!left.doesMatch && !right.doesMatch)
				return noMatches;

			HashMap union = new HashMap();
			Iterator it = left.gridTagToMatchingRows.keySet().iterator();
			while(it.hasNext())
			{
				String tag = (String)it.next();
				Integer[] leftRows = (Integer[])left.gridTagToMatchingRows.get(tag);
				Integer[] rightRows = (Integer[])right.gridTagToMatchingRows.get(tag);
				
				Vector mergedRows = new Vector(Arrays.asList(leftRows));
				if(rightRows != null)
				{
					for(int i = 0; i < rightRows.length; ++i)
						if(!mergedRows.contains(rightRows[i]))
							mergedRows.add(rightRows[i]);
				}
				
				if(mergedRows.size() > 0)
					union.put(tag, mergedRows.toArray(new Integer[0]));
			}
			
			boolean newDoesMatch = false;
			if(sameRowsMode)
				newDoesMatch = allGridsHaveMatchingRows(union);
			else
				newDoesMatch = (left.doesMatch || right.doesMatch);
			return new MatchResults(newDoesMatch, union);
		}
		
		private static boolean allGridsHaveMatchingRows(Map map)
		{
			Iterator it = map.keySet().iterator();
			while(it.hasNext())
			{
				String tag = (String)it.next();
				Integer[] rows = (Integer[])map.get(tag);
				if(rows.length == 0)
					return false;
			}
			
			return true;
		}
		
		public static MatchResults noMatches = new MatchResults();
		
		private boolean doesMatch;
		private Map gridTagToMatchingRows;
	}
	
	private SearchTreeNode node;
	private boolean sameRowsMode;

}
