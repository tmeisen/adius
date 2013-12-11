package org.adiusframework.util.db;

public class AutoIncreamentIdMapper extends IdMapper {
	private Integer autoIncreamentId;

	private Integer initializeId;

	public AutoIncreamentIdMapper() {
		this(0);
	}

	public AutoIncreamentIdMapper(Integer initializeId) {
		this.initializeId = initializeId;
		this.autoIncreamentId = initializeId;
	}

	public void addId(Integer id) throws NonUniqueIndexException {
		this.addId(id, this.autoIncreamentId);
		this.autoIncreamentId++;
	}

	@Override
	public void clear() {
		super.clear();
		this.autoIncreamentId = this.initializeId;
	}
}
