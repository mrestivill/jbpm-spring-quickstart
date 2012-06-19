package com.nijhazer.jbpm.quickstart;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.jbpm.task.identity.UserGroupCallback;

import com.nijhazer.jbpm.quickstart.model.AccountRole;
import com.nijhazer.jbpm.quickstart.model.AccountUser;

/**
 * Callback for use in authenticating users / groups
 * against a local database table, accessed using
 * domain repository objects.
 * @author mwilson
 */
public class LocalUserGroupCallbackImpl implements UserGroupCallback {
	private static final Logger logger = LoggerFactory.getLogger(LocalUserGroupCallbackImpl.class);
	
	private static LocalUserGroupCallbackImpl instance;

	private List<AccountRole> roles;
	private List<AccountUser> users;
	
	private Set<String> roleSet;
	private Set<String> usernameSet;
	private Map<String, AccountUser> userMap;
	
	private LocalUserGroupCallbackImpl() {
	}
	
	public static LocalUserGroupCallbackImpl getInstance() {
	   if (instance == null) {
          synchronized (LocalUserGroupCallbackImpl.class) {
             if (instance == null) {
                instance = new LocalUserGroupCallbackImpl();
             }
          }
       }
       return instance;
    }

	@Override
	public boolean existsGroup(String group) {
		logger.debug("Checking for the existence of group '{}'", group);
		if (roleSet.contains(group)) {
			logger.debug("Group '{}' exists; operation OK", group);
			return true;
		}
		logger.debug("Group '{}' not found", group);
		return false;
	}

	@Override
	public boolean existsUser(String user) {
		logger.debug("Checking for the existence of user '{}'", user);
		
		if (usernameSet.contains(user)) {
			logger.debug("User '{}' exists; operation OK", user);
			return true;
		}
		logger.debug("User '{}' not found", user);
		return false;
	}

	@Override
	public List<String> getGroupsForUser(String user, List<String> groups,
			List<String> allGroups) {
		List<String> returnList = new ArrayList<String>();
		
		AccountUser accountUser = null;
		if (usernameSet.contains(user)) {
			accountUser = userMap.get(user);
		}
		if (accountUser != null) {
			returnList.add(accountUser.getRole().getDescription());
		}
		
		return returnList;
	}

	public void setRoles(List<AccountRole> roles) {
		this.roles = roles;
		roleSet = new TreeSet<String>();
		for (AccountRole role : roles) {
			if (!roleSet.contains(role.getDescription())) {
				roleSet.add(role.getDescription());
			}
		}
	}

	public void setUsers(List<AccountUser> users) {
		this.users = users;
		usernameSet = new TreeSet<String>();
		userMap = new HashMap<String, AccountUser>();
		for (AccountUser user : users) {
			if (!usernameSet.contains(user.getUsername())) {
				usernameSet.add(user.getUsername());
				userMap.put(user.getUsername(), user);
			}
		}
	}
}
