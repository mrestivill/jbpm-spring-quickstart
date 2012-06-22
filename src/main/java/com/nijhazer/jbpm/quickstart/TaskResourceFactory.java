package com.nijhazer.jbpm.quickstart;

import org.drools.container.spring.beans.persistence.DroolsSpringTransactionManager;
import org.drools.persistence.TransactionManager;
import org.jbpm.task.service.TaskService;
import org.jbpm.task.service.local.LocalTaskService;
import org.jbpm.task.service.persistence.TaskSessionSpringFactoryImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.transaction.support.AbstractPlatformTransactionManager;

/**
 * Provider of LocalTaskService clients.
 * @author mwilson
 */
public class TaskResourceFactory implements ApplicationContextAware {
	private static final Logger logger = LoggerFactory.getLogger(TaskResourceFactory.class);
	private static ApplicationContext context;

	@Override
	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		this.context = applicationContext;
	}
	
	/**
	 * When using a LocalTaskService, it's best to obtain a new
	 * bean for each request. This method will allow for obtaining
	 * new instances of prototype- or request-scoped LocalTaskService
	 * clients from within Spring MVC controllers.
	 * @return LocalTaskService (client)
	 */
	public static LocalTaskService getTaskClient(TaskService taskService, AbstractPlatformTransactionManager transactionManager) {
		initializeSessionFactory(transactionManager);
		
		logger.debug("Creating local task service client, based on {}", taskService);
		LocalTaskService returnBean = new LocalTaskService(taskService);
		
		logger.debug("Returning task client: {}", returnBean.toString());
		return returnBean;
	}
	
	public static void initializeSessionFactory(AbstractPlatformTransactionManager transactionManager) {
		logger.debug("Initializing the session factory");
		TaskSessionSpringFactoryImpl springFactory = (TaskSessionSpringFactoryImpl) context.getBean("springTaskSessionFactory");
		
		logger.debug("Setting the transaction manager, based on {}", transactionManager);
		TransactionManager taskTx = new DroolsSpringTransactionManager(transactionManager);
		springFactory.setTransactionManager(taskTx);
		
		logger.debug("Initializing");
		springFactory.initialize();
	}

}
