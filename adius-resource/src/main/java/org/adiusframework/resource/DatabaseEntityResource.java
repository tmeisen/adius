package org.adiusframework.resource;

public class DatabaseEntityResource extends AbstractResource {

	/**
	 * UID for serialization
	 */
	private static final long serialVersionUID = -438426093771104821L;

	private String entityTable;

	private int entityId;

	public DatabaseEntityResource() {
		setType(Resource.TYPE_ENTITY);
	}

	public void setEntityId(int entityId) {
		this.entityId = entityId;
	}

	public int getEntityId() {
		return entityId;
	}

	public void setEntityTable(String entityTable) {
		this.entityTable = entityTable;
	}

	public String getEntityTable() {
		return entityTable;
	}
}
