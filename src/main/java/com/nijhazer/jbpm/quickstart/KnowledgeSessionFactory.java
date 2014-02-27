package com.nijhazer.jbpm.quickstart;

import javax.persistence.EntityManagerFactory;

import org.drools.KnowledgeBase;
import org.drools.container.spring.beans.persistence.DroolsSpringJpaManager;
import org.drools.container.spring.beans.persistence.DroolsSpringTransactionManager;
import org.drools.impl.EnvironmentFactory;
import org.drools.logger.KnowledgeRuntimeLoggerFactory;
import org.drools.persistence.PersistenceContextManager;
import org.drools.persistence.TransactionManager;
import org.drools.persistence.jpa.JPAKnowledgeService;
import org.drools.runtime.Environment;
import org.drools.runtime.EnvironmentName;
import org.drools.runtime.StatefulKnowledgeSession;
import org.jbpm.process.audit.JPAProcessInstanceDbLog;
import org.jbpm.process.audit.JPAWorkingMemoryDbLogger;
import org.jbpm.process.workitem.wsht.SyncWSHumanTaskHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.support.AbstractPlatformTransactionManager;

import com.nijhazer.jbpm.quickstart.TaskResourceFactory;

/**
 * Domain object for retrieving pre-configured KnowledgeSessions.
 * Designed for use with Spring.
 * @author mwilson
 */
public class KnowledgeSessionFactory {
	private static final Logger logger = LoggerFactory.getLogger(KnowledgeSessionFactory.class);
	
	private Environment env;
	private KnowledgeBase kbase;
	private EntityManagerFactory entityManagerFactory;
	private AbstractPlatformTransactionManager transactionManager;
	private TaskResourceFactory taskResources;

	/**
	 * Retrieves a StatefulKnowledgeSession, pre-configured for use with
	 * domain persistence model. The recipient will be responsible
	 * for disposing of the session.
	 * @return StatefulKnowledgeSession (new)
	 */
	public StatefulKnowledgeSession getStatefulKnowledgeSession() {
		logger.debug("Creating a new StatefulKnowledgeSession");
		env = getPersistenceEnvironment();
	
	    StatefulKnowledgeSession ksession = JPAKnowledgeService.newStatefulKnowledgeSession(kbase, null, env);

		JPAProcessInstanceDbLog.setEnvironment(env);
		registerWorkItemHandlers(ksession);
		new JPAWorkingMemoryDbLogger(ksession);
		KnowledgeRuntimeLoggerFactory.newConsoleLogger(ksession);
		
		logger.debug("Returning session {}", ksession);
		return ksession;
	}
	
	/**
	 * Retrieves an existing StatefulKnowledgeSession, pre-configured
	 * for use with the domain persistence model. The recipient
	 * will be responsible for disposing of the session.
	 * @param sessionId The ID of the existing KnowledgeSession
	 * @return StatefulKnowledgeSession (existing)
	 */
	public StatefulKnowledgeSession restoreStatefulKnowledgeSession(int sessionId) {
		logger.debug("Restoring StatefulKnowledgeSession with id {}", sessionId);
		env = getPersistenceEnvironment();
		
		JPAProcessInstanceDbLog.setEnvironment(env);

		StatefulKnowledgeSession ksession = JPAKnowledgeService.loadStatefulKnowledgeSession(sessionId, kbase, null, env);
		registerWorkItemHandlers(ksession);
		new JPAWorkingMemoryDbLogger(ksession);
		KnowledgeRuntimeLoggerFactory.newConsoleLogger(ksession);
		
		logger.debug("Returning session {}", ksession);
		return ksession;
	}
	
	/**
	 * The jBPM persistence layer will obtain persistence resources from an Environment
	 * object. This method configures the object and returns it for use by the jBPM
	 * persistence objects.
	 * @return configured Environment object
	 */
	private Environment getPersistenceEnvironment() {
		Environment env = EnvironmentFactory.newEnvironment();
		logger.debug("Setting environmental EntityManagerFactory to {}", entityManagerFactory);
		env.set(EnvironmentName.ENTITY_MANAGER_FACTORY, entityManagerFactory);
		
		PersistenceContextManager persistenceContextManager = new DroolsSpringJpaManager(env);
		logger.debug("Setting persistence context manager to {}", persistenceContextManager);
		env.set(EnvironmentName.PERSISTENCE_CONTEXT_MANAGER, persistenceContextManager);
		
		TransactionManager brmsTx = new DroolsSpringTransactionManager(transactionManager);
		logger.debug("Setting transaction manager to {}", brmsTx);
		env.set(EnvironmentName.TRANSACTION_MANAGER, brmsTx);
		
		return env;
	}
	
	/**
	 * Registers default WorkItemHandlers with the KnowledgeSession.
	 * @param ksession An existing StatefulKnowledgeSession
	 */
	private void registerWorkItemHandlers(StatefulKnowledgeSession ksession) {
		logger.debug("Registering work item handlers");
		ksession.getWorkItemManager().registerWorkItemHandler("Human Task", getHumanTaskWorkItemHandler());
	}
	
	/**
	 * Creates a WorkItemHandler for processing User Tasks.
	 * @return WSHumanTaskHandler
	 */
	private SyncWSHumanTaskHandler getHumanTaskWorkItemHandler() {
		logger.debug("Creating a Human Task Handler");
		SyncWSHumanTaskHandler handler = new SyncWSHumanTaskHandler();
		handler.setLocal(true);
		//handler.setClient(taskResources.getTaskClient());
		return handler;
	}
	
	public KnowledgeBase getKbase() {
		return kbase;
	}

	public void setKbase(KnowledgeBase kbase) {
		this.kbase = kbase;
	}

	public EntityManagerFactory getEntityManagerFactory() {
		return entityManagerFactory;
	}

	public void setEntityManagerFactory(EntityManagerFactory entityManagerFactory) {
		this.entityManagerFactory = entityManagerFactory;
	}

	public AbstractPlatformTransactionManager getTransactionManager() {
		return transactionManager;
	}

	public void setTransactionManager(AbstractPlatformTransactionManager transactionManager) {
		logger.debug("Setting transaction manager: {}", transactionManager);
		this.transactionManager = transactionManager;
	}

	public TaskResourceFactory getTaskResources() {
		return taskResources;
	}

	public void setTaskResources(TaskResourceFactory taskResources) {
		this.taskResources = taskResources;
	}
}
