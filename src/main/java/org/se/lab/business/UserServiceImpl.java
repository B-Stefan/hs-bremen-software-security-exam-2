package org.se.lab.business;

import org.se.lab.data.DAOException;
import org.se.lab.data.UserDAO;


public class UserServiceImpl 
	extends AbstractService 
	implements UserService
{
	/*
	 * Dependency: ---[1]-> UserDAO 
	 */
	private UserDAO userDAO;
	protected final UserDAO getUserDAO()
	{
		return userDAO;
	}
	public final void setUserDAO(final UserDAO userDAO)
	{
		this.userDAO = userDAO;
	}

	
	/*
	 * Business methods
	 */


    public boolean login(String username, String password)
    {
		boolean isValid = false;
		try
		{
			begin();
			isValid = getUserDAO().isValidUser(username, password);
			commit();
		}
        catch(DAOException e)
        {
            rollback();
			throw new ServiceException("Can't validate user!");
        }
        finally
        {
            closeConnection();
        }
		return isValid;    	
    }
}
