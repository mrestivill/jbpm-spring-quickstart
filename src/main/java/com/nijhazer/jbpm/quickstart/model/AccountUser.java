package com.nijhazer.jbpm.quickstart.model;

public class AccountUser {
	private String username;
	private AccountRole role;
	
	public AccountUser(String username, AccountRole role) {
		this.setUsername(username);
		this.setRole(role);
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public AccountRole getRole() {
		return role;
	}

	public void setRole(AccountRole role) {
		this.role = role;
	}

}
