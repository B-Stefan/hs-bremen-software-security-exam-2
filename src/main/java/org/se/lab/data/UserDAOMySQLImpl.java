package org.se.lab.data;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAOMySQLImpl 
    extends AbstractDAOImpl
    implements UserDAO
{
	/*
	 * DAO operations
	 */
	public boolean isValidUser(String username, String password)
	{
		final String SQL = "SELECT id FROM user WHERE username=? AND password=?";

		PreparedStatement pstmt = null;
		ResultSet rs = null;
		boolean result = false;
		Connection c = getConnection();

		try
		{
			pstmt = c.prepareStatement(SQL);
			pstmt.setString(1, username);
			pstmt.setString(2, password);
			rs = pstmt.executeQuery();
			result = rs.next();
		}
		catch (SQLException e)
		{
			return false;
		}
		finally
		{
			if (rs != null)
				closeResultSet(rs);
			if (pstmt != null)
				closePreparedStatement(pstmt);

		}
		return result;
	}
}
