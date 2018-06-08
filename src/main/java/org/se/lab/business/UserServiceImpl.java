package org.se.lab.business;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.se.lab.data.DAOException;
import org.se.lab.data.UserDAO;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


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
			isValid = getUserDAO().isValidUser(username, encryptData(password));
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


    /**
     * Encrypt data as SHA-265 and Base64
     * @param data Clear data string
     * @return encrypted string
     */
    private String encryptData(String data) {
        try
        {
            // Generate a random salt
            //SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
            //byte[] salt = new byte[16];
            //random.nextBytes(salt);

            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(data.getBytes("UTF-8"));
            byte[] hash = md.digest();

            // result = hash+salt
            //byte[] result = new byte[hash.length+salt.length];
            //System.arraycopy(salt, 0, result, 0, salt.length);
            //System.arraycopy(hash, 0, result, salt.length, hash.length);

            String result = Base64.encodeBase64String(hash);

            System.out.println("data  : " + data + " " + hash.length + " bytes ");
            System.out.println("hash  : " + Hex.encodeHexString(hash) + " " + hash.length + " bytes ");
            System.out.println("base64  : " + result);
            //System.out.println("salt  : " + Hex.encodeHexString(salt) + " " + salt.length + " bytes ");
            //System.out.println("result: " + Hex.encodeHexString(result) + " " + result.length + " bytes ");

            return result;
        }
        catch (NoSuchAlgorithmException | UnsupportedEncodingException e)
        {
            throw new IllegalStateException(e);
        }
    }
}
