package org.adiusframework.util.db;

import java.util.List;

import org.adiusframework.util.db.hibernate.HibernateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.hibernate.CacheMode;
import org.hibernate.Query;
import org.hibernate.QueryException;
import org.hibernate.SessionFactory;

public class PaginationQuery<T> {

	/**
	 * Logger for this class
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(PaginationQuery.class.getName());

	private static final Integer DEFAULT_PAGE_SIZE = 100000;

	private Query hqlQuery;

	private Integer size;

	private Integer pageNumber;

	private Integer readCount;

	private Integer resultSize;

	private SessionFactory sessionFactory;

	public PaginationQuery(Query hqlQuery, Integer size, SessionFactory factory) {
		this(hqlQuery, size, factory, PaginationQuery.DEFAULT_PAGE_SIZE);
	}

	public PaginationQuery(Query hqlQuery, Integer size, SessionFactory factory, Integer resultSize) {

		// setting up the query
		this.hqlQuery = hqlQuery;
		this.resultSize = resultSize;
		this.hqlQuery.setCacheMode(CacheMode.IGNORE);
		this.sessionFactory = factory;

		// setting up other members
		this.size = size;
		this.readCount = 0;
		this.pageNumber = 0;
	}

	public Integer getPageNumber() {
		return this.pageNumber;
	}

	public boolean hasNextPage() {
		return (this.readCount < this.size);
	}

	@SuppressWarnings("unchecked")
	public List<T> getNextPageResults() {
		LOGGER.info("Current status: " + this.readCount + "/" + this.size + " Page:" + this.pageNumber);

		// clear previously fetched data in session and flush all changes
		LOGGER.debug("Flushing session... ");
		HibernateUtil.getSession(this.sessionFactory).flush();
		LOGGER.debug("Clearing session... ");
		HibernateUtil.getSession(this.sessionFactory).clear();

		// query data
		LOGGER.info("Getting next page results... ");
		if (!this.hasNextPage())
			return null;
		this.hqlQuery.setFirstResult(this.readCount);
		Integer maxResults = 0;
		if (this.size - this.readCount < this.resultSize)
			maxResults = this.size - this.readCount;
		else
			maxResults = this.resultSize;
		this.hqlQuery.setMaxResults(maxResults);
		List<T> results = this.hqlQuery.list();
		this.readCount += results.size();
		this.pageNumber++;
		if (results.size() < maxResults)
			throw new QueryException("The query returned a wrong number of results (" + results.size() + "/"
					+ maxResults + ")");
		LOGGER.info("Returning " + results.size() + " results...");
		return results;
	}
}
