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

package org.martus.client.test;

import org.martus.client.core.MartusUserNameAndPassword;
import org.martus.client.core.Exceptions.BlankUserNameException;
import org.martus.client.core.Exceptions.MartusClientApplicationException;
import org.martus.client.core.Exceptions.PasswordMatchedUserNameException;
import org.martus.client.core.Exceptions.PasswordTooShortException;
import org.martus.common.test.TestCaseEnhanced;

public class TestMartusUserNameAndPassword extends TestCaseEnhanced
{
	public TestMartusUserNameAndPassword(String name)
	{
		super(name);
	}

	public void testValidateUserNameAndPassword() throws MartusClientApplicationException
	{
		try
		{
			MartusUserNameAndPassword.validateUserNameAndPassword("", "validPassword");
			fail("Why wasn't a BlankUserNameException thrown?");
		}
		catch(BlankUserNameException ignoreExpectedException)
		{}

		try
		{
			MartusUserNameAndPassword.validateUserNameAndPassword("validUserName","validUserName");
			fail("Why was another exception (not PasswordMatchedUserNameException) thrown?");
		}
		catch(PasswordMatchedUserNameException ignoreExpectedException)
		{}

		try
		{
			MartusUserNameAndPassword.validateUserNameAndPassword("validUserName","short");
			fail("Why was another exception (not PasswordTooShortException) thrown?");
		}
		catch(PasswordTooShortException ignoreExpectedException)
		{}
	}

	public void testIsWeakPassword()
	{
		assertTrue("Why was 'test' not a weak password?", MartusUserNameAndPassword.isWeakPassword("test"));
		assertFalse("Why was '123456789012345%$' not a strong password?", MartusUserNameAndPassword.isWeakPassword("123456789012345%$"));
	}



}
