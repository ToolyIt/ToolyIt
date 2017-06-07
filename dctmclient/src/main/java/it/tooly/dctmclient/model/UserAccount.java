/**
 *
 */
package it.tooly.dctmclient.model;

/**
 * A simple representation of a user account
 *
 * @author M.E. de Boer
 *
 */
public class UserAccount extends DctmObject implements IUserAccount {
	private String loginName;
	private String password;

	public UserAccount(String userLoginName) {
		super(userLoginName);
		this.loginName = userLoginName;
		this.password = "";
	}

	public UserAccount(String userLoginName, String password) {
		super(userLoginName);
		this.loginName = userLoginName;
		this.password = password;
	}

	/**
	 * Get the user account name. This is NOT the login name.
	 * @see opensource.dctm.model.ModelObject#getName()
	 */
	@Override
	public String getName() {
		return super.getName();
	}

	/**
	 * Get the user login name.
	 */
	public String getLoginName() {
		return this.loginName;
	}

	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}

	/**
	 * @return the password
	 */
	public String getPassword() {
		return this.password;
	}

	/**
	 * @param password
	 *            the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}
}
