
package com.ldapauth.authn.realm.ldap;

import com.ldapauth.authn.realm.IAuthenticationServer;
import com.ldapauth.util.LdapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

/**
 * 标准LDAP认证服务器
 *
 * @author Crystal.Sea
 *
 */
public final class StandardLdapServer implements IAuthenticationServer {
	private final static Logger _logger = LoggerFactory.getLogger(StandardLdapServer.class);

	LdapUtils ldapUtils;

	String filterAttribute;

	boolean mapping;

	/* (non-Javadoc)
	 * @see com.connsec.web.authentication.realm.IAuthenticationServer#authenticate(java.lang.String, java.lang.String)
	 */
	@Override
	public boolean authenticate(String username, String password) {
		String queryFilter = String.format(filterAttribute, username);
		_logger.info(" filter : " + queryFilter);
		String dn="";
		SearchControls constraints = new SearchControls();
		constraints.setSearchScope(ldapUtils.getSearchScope());
		try {
			NamingEnumeration<SearchResult> results = ldapUtils.getConnection()
					.search(ldapUtils.getBaseDN(), queryFilter, constraints);

			if (results == null || !results.hasMore()) {
				_logger.error("Ldap user "+username +" not found . ");
				return false;
			}else{
				while (results != null && results.hasMore()) {
					SearchResult sr = (SearchResult) results.next();
					//String rdn = sr.getName();
					dn = sr.getNameInNamespace();
					_logger.debug("Directory user dn is "+dn+" .");
				}
			}
		} catch (NamingException e) {
			_logger.error("query throw NamingException:" + e.getMessage());
		} finally {
			//ldapUtils.close();
		}

		LdapUtils ldapPassWordValid=new LdapUtils(ldapUtils.getProviderUrl(),dn,password);
		ldapPassWordValid.openConnection();
		if(ldapPassWordValid.getCtx()!=null){
			_logger.debug("Directory user " + username + "  is validate .");
			ldapPassWordValid.close();
			return true;
		}
		return false;
	}

	public LdapUtils getLdapUtils() {
		return ldapUtils;
	}
	public void setLdapUtils(LdapUtils ldapUtils) {
		this.ldapUtils = ldapUtils;
	}
	public String getFilterAttribute() {
		return filterAttribute;
	}
	public void setFilterAttribute(String filterAttribute) {
		this.filterAttribute = filterAttribute;
	}

	public boolean isMapping() {
		return mapping;
	}

	public void setMapping(boolean mapping) {
		this.mapping = mapping;
	}

}
