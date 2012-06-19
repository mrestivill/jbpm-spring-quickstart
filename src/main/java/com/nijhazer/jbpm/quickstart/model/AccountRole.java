package com.nijhazer.jbpm.quickstart.model;

public class AccountRole {
	private String description;
	
	public AccountRole(String description) {
		this.setDescription(description);
	}
	
	public String getDescription() {
		return this.description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}

}
