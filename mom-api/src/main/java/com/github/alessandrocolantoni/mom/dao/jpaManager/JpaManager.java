package com.github.alessandrocolantoni.mom.dao.jpaManager;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.persistence.Query;

import com.github.alessandrocolantoni.mom.dao.DataAccessException;
import com.github.alessandrocolantoni.mom.dao.RelationTypeMaps;

public interface JpaManager extends Serializable {

	
	public final static int ONE_TO_ONE = 0;
	public final static int ONE_TO_N = 1;
	public final static int M_TO_N = 2;
	public final static int M_TO_ONE = 3;  
	public final static int M_TO_N_INVERSE = 4;  
	public final static int EMBEDDED_ID = 5; 
	
	
	public <E> List<E> getResultList(Query query) throws DataAccessException;
	public <E> E getSingleResult(Query query) throws DataAccessException;
	public void setQueryParameters(Query query, Map<String, Object> parameters) throws DataAccessException;
	public <E> boolean isReferenceCollection(Class<E> pInstanceClass, String pAttributeName) throws DataAccessException;
	public Class<?> getEntityClass(Object entity) throws DataAccessException;
	public void setFirstAndMaxResults(Query query,  Integer firstResult, Integer maxResults) throws DataAccessException;
	public <E> Class<?> getClassFromPath(Class<E> realClass, String path) throws Exception;
	public boolean isInitialized(Object pInstance, String pAttributeName) throws Exception;
	public <E> int getRelationType(Class<E> realClass, String pAttributeName) throws Exception;
	public String getInverseManyToManyField(Object pInstance, String pAttributeName) throws Exception;
	public RelationTypeMaps buildRelationTypeMaps(Object parent, Collection<String> paths, boolean pathsHasToBeSorted) throws Exception;

}
