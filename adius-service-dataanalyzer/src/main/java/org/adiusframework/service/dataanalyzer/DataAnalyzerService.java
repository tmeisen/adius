package org.adiusframework.service.dataanalyzer;

import org.adiusframework.ontology.exception.OWLException;
import org.adiusframework.ontology.frameworkontology.FrameworkOntologyManager;
import org.adiusframework.resource.ObjectResource;
import org.adiusframework.resource.StringResourceCapability;
import org.adiusframework.resource.state.State;
import org.adiusframework.service.ErrorServiceResult;
import org.adiusframework.service.ServiceException;
import org.adiusframework.service.ServiceInstance;
import org.adiusframework.service.ServiceResult;
import org.adiusframework.service.StandardServiceResult;
import org.adiusframework.service.dataanalyzer.exception.DatabaseException;
import org.adiusframework.util.datastructures.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.hibernate.SessionFactory;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

public class DataAnalyzerService implements ServiceInstance {

	private static final Logger LOGGER = LoggerFactory.getLogger(DataAnalyzerService.class);

	private int entityId;

	private String ontology;

	private String analyzeScope;

	private String application;

	private SessionFactory sessionFactory;

	private String className;

	public String getOntology() {
		return this.ontology;
	}

	/**
	 * Sets the path where the OWL ontology is located
	 * 
	 * @param ontology
	 *            path of the OWL ontology
	 */
	public void setOntology(String ontology) {
		this.ontology = ontology;
	}

	public int getEntityId() {
		return entityId;
	}

	public void setEntityId(int entityId) {
		this.entityId = entityId;
	}

	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	public String getAnalyzeScope() {
		return analyzeScope;
	}

	public void setAnalyzeScope(String analyzeScope) {
		this.analyzeScope = analyzeScope;
	}

	public String getApplication() {
		return application;
	}

	public void setApplication(String application) {
		this.application = application;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	@Override
	public ServiceResult execute() throws ServiceException {
		StandardServiceResult result = new StandardServiceResult();
		LOGGER.info("Data Analyzer Service started:");
		LOGGER.info("Ontology: " + getOntology());
		LOGGER.info("Entity Id: " + getEntityId());
		LOGGER.info("Application: " + getApplication());
		LOGGER.info("Class name: " + getClassName());
		LOGGER.info("Analyze scope: " + getAnalyzeScope());

		// first lets create a content manager
		FrameworkOntologyManager contentManager = null;
		try {
			contentManager = new FrameworkOntologyManager(getOntology());
			Properties properties = new Properties();
			properties.setProperty("hasScope", getAnalyzeScope());
			contentManager.setContext(properties);
			LOGGER.debug("FrameworkOntologyManager created");
		} catch (OWLException e) {
			return new ErrorServiceResult(e.getMessage());
		} catch (OWLOntologyCreationException e) {
			return new ErrorServiceResult(e.getMessage());
		}
		DataAnalyzer analyzer = new DataAnalyzer(contentManager, getSessionFactory());
		LOGGER.debug("DataAnalyzer created");

		// now lets start with the analysis
		try {

			// first the axiom state
			State axiomState = analyzer.getAxiomState();
			DataAnalyzerService.addStateToResult(result, axiomState, "axiom_state");
			LOGGER.debug("Axiom state analyzed");
			LOGGER.debug(axiomState.toString());

			// second the actual state
			State actualState = analyzer.getActualState(getClassName(), String.valueOf(getEntityId()));
			DataAnalyzerService.addStateToResult(result, actualState, "actual_state");
			LOGGER.debug("ActualState analyzed");
			LOGGER.debug(actualState.toString());

			// and last but not least the target state
			State targetState = analyzer.getTargetState(getApplication());
			DataAnalyzerService.addStateToResult(result, targetState, "target_state");
			LOGGER.debug("TargetState analyzed");
			LOGGER.debug(targetState.toString());

		} catch (OWLException e) {
			LOGGER.error(e.getMessage());
			return new ErrorServiceResult(e.getMessage());
		} catch (DatabaseException e) {
			LOGGER.error(e.getMessage());
			return new ErrorServiceResult(e.getMessage());
		}
		return result;
	}

	/**
	 * Helper method to add a analyzed state to a result.
	 * 
	 * @param result
	 *            the result the state has to be added to.
	 * @param state
	 *            state object that represents a result of the analysis.
	 * @param name
	 *            name of the state.
	 */
	protected static void addStateToResult(StandardServiceResult result, State state, String name) {
		ObjectResource<State> stateResource = new ObjectResource<State>();
		stateResource.setCapability(new StringResourceCapability(name));
		stateResource.setObject(state);
		result.addResource(stateResource);
	}

}
