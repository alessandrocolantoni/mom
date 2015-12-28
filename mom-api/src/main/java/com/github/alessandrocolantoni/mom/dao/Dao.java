package com.github.alessandrocolantoni.mom.dao;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Query;

import com.github.alessandrocolantoni.mom.query.LogicCondition;

public interface Dao  extends Serializable{

	public  <E> E findByPrimaryKey(Class<E> realClass, Object pkValue) throws DataAccessException;

	public <E> E findObjectByTemplate(E entity) throws DataAccessException;

	public <E> Object findObjectByLogicCondition(Class<E> realClass, LogicCondition logicCondition) throws DataAccessException;
	
	public <E> E findObjectByLogicCondition(String[]selectFields, Class<E> realClass, LogicCondition logicCondition, String orderBy) throws DataAccessException;
	
	public <E> E findObjectByQueryString(String queryString) throws DataAccessException;
	
	public <E> E findObjectByQueryString(String queryString, String parameterName, Object parameterValue) throws DataAccessException;
	
	public <E> E findObjectByQueryString(String queryString, Map<String,Object> parameters) throws DataAccessException;
	
	
	public <E> E findObjectByNativeQueryString(String queryString) throws DataAccessException;
	public <E> E findObjectByNativeQueryString(String queryString, String parameterName, Object parameterValue) throws DataAccessException;
	
	
	public <E> E findObjectByNativeQueryString(String queryString, Map<String,Object> parameters) throws DataAccessException;
	
	
	
	/**
	 * 
	 * @param entity
	 * @param firstResult
	 * @param maxResults
	 * @param orderBy  string to concat to ORDER BY. For example  field1 asc, field2 desc
	 * @return
	 * @throws DataAccessException
	 */
	public <E> Collection<E> findCollectionByTemplate(E entity, 
														Integer firstResult, Integer maxResults, 
														String orderBy) throws DataAccessException;


	public <E> List<E> findCollectionByTemplate(E entity) throws DataAccessException;
	public <E> List<E>  findCollectionByTemplate(E entity, String orderBy) throws DataAccessException;
	public <E> List<E>  findCollectionByTemplate(E entity, Integer firstResult, Integer maxResults) throws DataAccessException;
	
	public  <E> List<E> findCollectionByNullFields(Class<E> realClass, String[] nullFields) throws DataAccessException;
	
	public <E> List<E> findCollectionByLogicCondition(Class<E> realClass, LogicCondition logicCondition) throws DataAccessException;
	
	
	

	

	public <E> List<E> findCollectionByLogicCondition(String[]selectFields, Class<E> realClass, LogicCondition logicCondition) throws DataAccessException;
	
	public <E> List<E> findCollectionByLogicCondition(String[]selectFields, Class<E> realClass, LogicCondition logicCondition, String orderBy) throws DataAccessException;
	
	public <E> List<E> findCollectionByLogicCondition(Class<E> realClass, LogicCondition logicCondition, String orderBy) throws DataAccessException;
	
	public <E> List<E> findCollectionByLogicCondition(Boolean distinct, String[]selectFields, Class<E> realClass, LogicCondition logicCondition, String orderBy) throws DataAccessException;
	
	public <E> List<E> findCollectionByLogicCondition(Boolean distinct,String[] selectFields, Class<E> realClass, LogicCondition logicCondition, String orderBy,Integer firstResult, Integer maxResults) throws DataAccessException;
   
	public <E> List<E> findCollectionByLogicCondition(String[] selectFields, Class<E> realClass, LogicCondition logicCondition, String orderBy,Integer firstResult, Integer maxResults) throws DataAccessException;
	
	public <E> List<E> findCollectionByLogicCondition(Class<E> realClass, LogicCondition logicCondition, String orderBy,Integer firstResult, Integer maxResults) throws DataAccessException;

	public <E> List<E> findCollectionByLogicCondition(Boolean distinct,Class<E> realClass, LogicCondition logicCondition, String orderBy) throws DataAccessException;
	
	public <E> List<E> findCollectionByLogicCondition(Boolean distinct, Class<E> realClass, LogicCondition logicCondition) throws DataAccessException;
	
	public <E> List<E> findCollectionByLogicCondition(Boolean distinct, String[]selectFields, Class<E> realClass, LogicCondition logicCondition) throws DataAccessException;
	
	/**
	 * 
	 * @param distinct
	 * @param selectFields
	 * @param realClass
	 * @param logicCondition
	 * @param orderBy orderBy string to concat to ORDER BY. For example  field1 asc, field2 desc
	 * @param groupBy
	 * @param firstResult
	 * @param maxResults
	 * @return
	 * @throws DataAccessException
	 */
	public <E> List<E> findCollectionByLogicCondition(Boolean distinct,String[] selectFields, Class<E> realClass, LogicCondition logicCondition, String orderBy,String[] groupBy, Integer firstResult, Integer maxResults) throws DataAccessException;

	public <E> List<E> findCollectionByQueryString(String queryString) throws DataAccessException;
	
	public <E> List<E> findCollectionByQueryString(String queryString, Integer firstResult, Integer maxResults) throws DataAccessException;
	public <E> List<E> findCollectionByQueryString(String queryString, String parameterName, Object parameterValue) throws DataAccessException;
	public <E> List<E> findCollectionByQueryString(String queryString, Map<String,Object> parameters) throws DataAccessException;
	
	public <E> List<E> findCollectionByQueryString(String queryString, String parameterName, Object parameterValue, Integer firstResult, Integer maxResults) throws DataAccessException;
	
	public <E> List<E> findCollectionByQueryString(String queryString, Map<String,Object> parameters, Integer firstResult, Integer maxResults) throws DataAccessException;
	
	
	
	public <E> List<E> findCollectionByNativeQueryString(String queryString) throws DataAccessException;
	
	public <E> List<E> findCollectionByNativeQueryString(String queryString, Integer firstResult, Integer maxResults) throws DataAccessException;
	
	public <E> List<E> findCollectionByNativeQueryString(String queryString, String parameterName, Object parameterValue) throws DataAccessException;
	
	public <E> List<E> findCollectionByNativeQueryString(String queryString, Map<String,Object> parameters) throws DataAccessException;
	
	public <E> List<E> findCollectionByNativeQueryString(String queryString, String parameterName, Object parameterValue, Integer firstResult, Integer maxResults) throws DataAccessException;
	
	public <E> List<E> findCollectionByNativeQueryString(String queryString, Map<String,Object> parameters,Integer firstResult, Integer maxResults) throws DataAccessException;
	
	public <E,T> List<E> findCollectionByOrValues(Class<E> realClass,String pAttributeName,List<T> valuesCollection) throws DataAccessException;
	
	public <E,T> List<E> findCollectionByFieldInCollection(Class<E> realClass,String pAttributeName, List<T> valuesCollection) throws DataAccessException;
	
	public <E> List<E> searchValueInFields(Class<E> realClass, String[] pAttributeNames, Object value) throws DataAccessException;
	
	public <E> List<E> getCollectionOfStoredItemsNotInBean(Object pInstance, String pAttributeName) throws DataAccessException;
	
	public <E> List<E>  getCollectionOfStoredItemsInBean(Object pInstance, String pAttributeName) throws DataAccessException;

	public  <E> List<E>  getStoredCollection(Object pInstance, String pAttributeName) throws DataAccessException;
	
	
	
	
	
	
	
	
}
