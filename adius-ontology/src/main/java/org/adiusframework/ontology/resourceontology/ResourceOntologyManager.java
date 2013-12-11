package org.adiusframework.ontology.resourceontology;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.adiusframework.ontology.OntologyManager;
import org.adiusframework.ontology.exception.OWLEntityNotFoundException;
import org.adiusframework.ontology.exception.OWLInvalidContentException;
import org.adiusframework.ontology.resourceontology.model.Protocol;
import org.adiusframework.ontology.resourceontology.model.ResourceOntologyConstants;
import org.adiusframework.ontology.resourceontology.model.ResourceOntologyEntity;
import org.adiusframework.ontology.resourceontology.model.ResourceType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;

/**
 * Class representing an extended version of the classic ontology manager. This
 * class contains specialized methods regarding the resource ontology model.
 * 
 * @author tm807416
 * 
 */
/**
 * @author tm807416
 * 
 */
public class ResourceOntologyManager extends OntologyManager {

	private static final Logger LOGGER = LoggerFactory.getLogger(ResourceOntologyManager.class);

	/**
	 * Fields containing references to the relevant classes of the resource
	 * ontology
	 */
	private OWLClass protocolClass, resourceTypeClass;

	/**
	 * Pointer to the main resource ontology
	 */
	private OWLOntology resourceOntology;

	/**
	 * Field referring protocols (like MySql or Ftp) defined in the ontology
	 */
	private Map<String, Protocol> protocols;

	/**
	 * Field referring the mapping of type identifiers and concret resource
	 * types
	 */
	private Map<String, ResourceType> types;

	/**
	 * Constructor to initialze a resource ontology manager. The constructor
	 * requires to provide the path to the ontology. This can either be a local
	 * resource or a web resource.
	 * 
	 * @param ontologyFileName
	 *            path to the ontology
	 * @throws OWLOntologyCreationException
	 *             if the ontology cannot be loaded (possibly the wrong format)
	 * @throws OWLEntityNotFoundException
	 *             if the ontology does not satisfy the requirements of a
	 *             resource ontology
	 */
	public ResourceOntologyManager(String ontologyFileName) throws OWLOntologyCreationException,
			OWLEntityNotFoundException {
		super(ontologyFileName);

		// lets identify the resource ontology
		LOGGER.debug("Identification of resource ontology");
		resourceOntology = null;
		if (getOntologies().size() > 1) {
			for (OWLOntology ontology : getOntologies()) {
				if (ontology.getOntologyID().getOntologyIRI().toString().contains("resource")) {
					resourceOntology = ontology;
					break;
				}
			}
		} else
			resourceOntology = getOntologies().iterator().next();
		if (resourceOntology == null)
			throw new OWLOntologyCreationException("Identification of resource ontology failed...");
		LOGGER.debug("Resource ontology is set to " + resourceOntology.getOntologyID());

		// initialize the manager by loading the relevant classes and
		// individuals of the ontology
		initProtocols();
		initTypes();
	}

	/**
	 * Method to initialize the list of protocols defined within the resource
	 * ontology.
	 * 
	 * @throws OWLEntityNotFoundException
	 */
	protected void initProtocols() throws OWLEntityNotFoundException {

		// same procedure as init locations: first we have to load the class,
		// second we can load the objects
		protocolClass = loadClass(ResourceOntologyConstants.CLASS_PROTOCOL);
		protocols = new HashMap<String, Protocol>();
		Set<OWLNamedIndividual> individuals = getReasoner().getInstances(protocolClass, false).getFlattened();
		for (OWLNamedIndividual individual : individuals) {
			String id = getEntityName(individual);
			putEntityInMap(protocols, id, new Protocol(id, individual), true);
		}
	}

	/**
	 * Method to initialize the types defined within the ontology.
	 * 
	 * @throws OWLEntityNotFoundException
	 */
	protected void initTypes() throws OWLEntityNotFoundException {

		// same procedure as before: first we have to load the class,
		// second we can load the objects
		resourceTypeClass = loadClass(ResourceOntologyConstants.CLASS_RESOURCETYPE);
		types = new HashMap<String, ResourceType>();
		Set<OWLNamedIndividual> individuals = getReasoner().getInstances(resourceTypeClass, false).getFlattened();
		for (OWLNamedIndividual individual : individuals) {
			String id = getEntityName(individual);
			putEntityInMap(types, id, new ResourceType(id, individual), true);
		}
	}

	/**
	 * Method simplifying the put of resource ontology entities into a
	 * map-index. The method puts the given entity into the map and if 'case
	 * insensitivity' is set to true, it also adds the lower and upper
	 * representation of the string as key.
	 * 
	 * @param map
	 *            index structure the entity has to be added to
	 * @param id
	 *            the id used as key of the index
	 * @param entity
	 *            the entity that have to be added
	 * @param caseInsensitiv
	 *            if set to true the entity is also added using the lower and
	 *            upper version of the key
	 */
	private <T extends ResourceOntologyEntity> void putEntityInMap(Map<String, T> map, String id, T entity,
			boolean caseInsensitiv) {
		map.put(id, entity);
		if (caseInsensitiv) {
			map.put(id.toLowerCase(), entity);
			map.put(id.toUpperCase(), entity);
		}
	}

	/**
	 * Method to get all protocols stored in the ontology
	 * 
	 * @return list of protocols
	 */
	public Collection<Protocol> getProtocols() {
		return protocols.values();
	}

	/**
	 * Method to get all types of resources defined in the ontology
	 * 
	 * @return list of defined types
	 */
	public Collection<ResourceType> getTypes() {
		return types.values();
	}

	/**
	 * Searches for a protocol that matches the given identifier
	 * 
	 * @param identifier
	 *            used to represent the protocol (e.g. FTP)
	 * @return the identified protocol
	 */
	public Protocol findProtocol(String identifier) {
		return protocols.get(identifier);
	}

	/**
	 * Searches for a resource type that matches the given identifier
	 * 
	 * @param identifier
	 *            used to represent the resource type (e.g. file)
	 * @return the identified resource type
	 */
	public ResourceType findType(String identifier) {
		return types.get(identifier);
	}

	/**
	 * Method that can be used to save the current "in-memory-version" of the
	 * ontology.
	 * 
	 * @param file
	 *            the file including path and name the ontology has to be saved
	 *            to
	 */
	public void saveOntologyBackup(File file) {
		try {
			getManager().saveOntology(resourceOntology, IRI.create(file));
		} catch (OWLOntologyStorageException e) {
			LOGGER.error("Save of ontology to " + file + " failed");
		}
	}

	public static void main(String[] args) throws OWLEntityNotFoundException, OWLOntologyCreationException,
			OWLInvalidContentException {
		ResourceOntologyManager rcm = new ResourceOntologyManager(
				"D:/Repositories/adius/_workspace/adiusframework-ontology/resourceontology.owl");
		LOGGER.debug(rcm.getProtocols().toString());
		LOGGER.debug(rcm.getTypes().toString());
		LOGGER.debug("Loading successful");
		rcm.saveOntologyBackup(new File("D:/Temp/test.owl"));
	}

}
