package com.github.alessandrocolantoni.mom.dao;

import java.io.Serializable;
import java.util.Collection;

public interface Dao  extends Serializable{

	public  <T> T findByPrimaryKey(Class<T> realClass, Object pkValue) throws DataAccessException;


	public <E> Collection<E> findCollectionByTemplate(E entity, 
														Integer firstResult, Integer maxResults, 
														String orderingField) throws DataAccessException;


	public <E> E findObjectByTemplate(E entity) throws DataAccessException;


	public <E> Collection<E> findCollectionByQueryString(String queryString) throws DataAccessException;
	
	
			

}
