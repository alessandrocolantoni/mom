package com.github.alessandrocolantoni.mom.dao;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.github.alessandrocolantoni.mom.query.LogicCondition;

public interface Dao  extends Serializable{

	public  <E> E findByPrimaryKey(Class<E> realClass, Object pkValue) throws DataAccessException;

	public <E> E findObjectByTemplate(E entity) throws DataAccessException;

	public <E> Object findObjectByLogicCondition(Class<E> realClass, LogicCondition logicCondition) throws DataAccessException;
	
	public <E> E findObjectByLogicCondition(String[]selectFields, Class<E> realClass, LogicCondition logicCondition, String orderBy) throws DataAccessException;
	
	public <E> E findObjectByQueryString(String queryString) throws DataAccessException;
	
	public <E> E findObjectByQueryString(String queryString, String parameterName, Object parameterValue) throws DataAccessException;
	
	public <E> E findObjectByQueryString(String queryString, Map<String,Object> parameters) throws DataAccessException;
	
	public <E> Collection<E> findCollectionByTemplate(E entity, 
														Integer firstResult, Integer maxResults, 
														String orderingField, Boolean asc) throws DataAccessException;


	public <E> List<E> findCollectionByTemplate(E entity) throws DataAccessException;
	public <E> List<E>  findCollectionByTemplate(E entity, String orderingField, Boolean asc) throws DataAccessException;
	
	
	public <E> List<E> findCollectionByLogicCondition(Class<E> realClass, LogicCondition logicCondition) throws DataAccessException;
	
	
	public <E> List<E> findCollectionByLogicCondition(Class<E> realClass,LogicCondition logicCondition,String orderingField, Boolean asc,Integer firstResult, Integer maxResults) throws DataAccessException;
	

	public <E> List<E> findCollectionByQueryString(String queryString) throws DataAccessException;

	
	
			

}
