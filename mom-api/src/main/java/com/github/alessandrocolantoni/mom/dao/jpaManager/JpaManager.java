package com.github.alessandrocolantoni.mom.dao.jpaManager;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import javax.persistence.Query;

import com.github.alessandrocolantoni.mom.dao.DataAccessException;

public interface JpaManager extends Serializable {

	public <E> List<E> getResultList(Query query) throws DataAccessException;
	public <E> E getSingleResult(Query query) throws DataAccessException;
	public void setQueryParameters(Query query, Map<String, Object> parameters) throws DataAccessException;
	public <E> boolean isReferenceCollection(Class<E> pInstanceClass, String pAttributeName) throws DataAccessException;
	public Class<?> getEntityClass(Object entity) throws DataAccessException;
	public void setFirstAndMaxResults(Query query,  Integer firstResult, Integer maxResults) throws DataAccessException;
	public <E> Class<?> getClassFromPath(Class<E> realClass, String path) throws Exception;

}
