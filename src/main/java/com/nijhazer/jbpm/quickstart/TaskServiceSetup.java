package com.nijhazer.jbpm.quickstart;

import java.util.List;

import org.jbpm.task.service.TaskService;
import org.jbpm.task.service.TaskServiceSession;
import org.jbpm.task.identity.UserGroupCallbackManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nijhazer.jbpm.quickstart.model.AccountRole;
import com.nijhazer.jbpm.quickstart.model.AccountUser;

/**
 * Provides necessary post-instantiation configuration for the TaskService in use.
 * Intended for use by Spring.
 * @author mwilson
 */
public class TaskServiceSetup {
	private static final Logger logger = LoggerFactory.getLogger(TaskServiceSetup.class);
	
	private TaskService taskService;
	private TaskResourceFactory taskResources;
	private List<AccountRole> roles;
	private List<AccountUser> users;
	
	public TaskServiceSetup() {
		
	}
	
	/**
	 * Executes additional TaskService configuration.
	 * Intended for use by Spring.
	 */
	public void initialize() {
		/**
		 * When processing User Tasks, jBPM must verify the existence of any users
		 * or groups to which a task should be assigned. This also occurs when checking
		 * if any tasks are assigned to a given user or group. Here, we provide jBPM
		 * with a callback-- an object that will be used to lookup user and group information.
		 */
		logger.debug("Initializing session factory");
		//TaskResourceFactory.getTaskClient(taskService, transactionManager);
		
		logger.debug("Retrieving a callback instance");
		LocalUserGroupCallbackImpl callback = LocalUserGroupCallbackImpl.getInstance();

		callback.setRoles(roles);
		callback.setUsers(users);
		
		logger.debug("Setting callback instance: {}", callback);
		UserGroupCallbackManager.getInstance().setCallback(callback);
		
		logger.debug("Creating a task session from service: {}", taskService);
		TaskServiceSession taskSession = taskService.createSession();
		
		logger.debug("Disposing of task session: {}", taskSession);
		taskSession.dispose();
	}
	
	public void setTaskService(TaskService taskService) {
		this.taskService = taskService;
	}

	public TaskResourceFactory getTaskResources() {
		return taskResources;
	}

	public void setTaskResources(TaskResourceFactory taskResources) {
		this.taskResources = taskResources;
	}

	public List<AccountRole> getRoles() {
		return roles;
	}

	public void setRoles(List<AccountRole> roles) {
		this.roles = roles;
	}

	public List<AccountUser> getUsers() {
		return users;
	}

	public void setUsers(List<AccountUser> users) {
		this.users = users;
	}
}
