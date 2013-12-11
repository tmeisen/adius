package org.adiusframework.resource;

import org.adiusframework.util.datastructures.MySqlConnectionContainer;

public class MySqlDatabaseResource extends AbstractResource {

	/**
	 * UID for serialization
	 */
	private static final long serialVersionUID = 828621293240783013L;

	private MySqlConnectionContainer connectionContainer;

	public MySqlDatabaseResource() {
		setType(Resource.TYPE_DATABASE);
		setProtocol(Resource.PROTOCOL_MYSQL);
		setConnectionContainer(null);
	}

	public void setConnectionContainer(MySqlConnectionContainer mySqlConnectionContainer) {
		this.connectionContainer = mySqlConnectionContainer;
	}

	public MySqlConnectionContainer getConnectionContainer() {
		return connectionContainer;
	}

}
