package org.adiusframework.util.db.hibernate;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import org.adiusframework.util.db.BulkLoader;
import org.adiusframework.util.db.DataManipulationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.hibernate.jdbc.Work;

public class BulkLoaderWork implements Work {

	/**
	 * Logger for this class
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(BulkLoaderWork.class.getName());

	private BulkLoader loader;

	private Integer result;

	public BulkLoaderWork(BulkLoader loader) {
		this.loader = loader;
		this.result = null;
	}

	public Integer getResult() {
		return this.result;
	}

	@Override
	public void execute(Connection connection) throws SQLException {

		// build sql query
		StringBuffer buffer = new StringBuffer();
		buffer.append("LOAD DATA LOCAL INFILE '");
		buffer.append(this.loader.getAbsolutePath(true));
		buffer.append("' INTO TABLE ");
		buffer.append(this.loader.getTable());
		buffer.append(" FIELDS TERMINATED BY '");
		buffer.append(this.loader.getFieldTerminator());
		buffer.append("' LINES TERMINATED BY '");
		buffer.append(this.loader.getLineTerminator());
		buffer.append("' (");
		buffer.append(this.loader.getColumns());
		buffer.append(")");

		// execute
		String sql = buffer.toString();
		LOGGER.debug("Executing bulk load: " + sql);
		Statement stmt = connection.createStatement();
		this.result = stmt.executeUpdate(sql);

		// check result of operation
		if (this.loader.isRowCountControlOn()) {
			LOGGER.debug("Data written " + this.result + "/" + this.loader.getExpectedRowCount());
			if (this.result != this.loader.getExpectedRowCount()) {
				LOGGER.error("The number of written rows (" + this.result
						+ "), does not match the number of expected rows (" + this.loader.getExpectedRowCount() + ")");
				throw new DataManipulationException("The number of written rows (" + this.result
						+ "), does not match the number of expected rows (" + this.loader.getExpectedRowCount() + ")");
			}
		} else
			LOGGER.debug("Data written " + this.result);
	}

}
